package main

//"8A6z1C5k"
import (
	"bufio"
	"errors"
	"fmt"
	"io"
	"io/ioutil"
	"os"
	"strings"

	"golang.org/x/crypto/ssh"
)

// host := "93.185.166.239:22"
// user := "root"
// pwd, _ := reader.ReadString('\n')

var host string = ""
var user string = ""
var pwd string = ""

func main() {
	reader := bufio.NewReader(os.Stdin)
	file, err := ioutil.ReadFile("config.conf")
	if err != nil {
		file, err = createConfigFile()
		if err != nil {
			fmt.Printf("Error: %v", err)
			return
		}
	}
	userData := strings.Split(string(file), "\n")

	host = userData[0]
	user = userData[1]
	if len(userData[2]) > 1 {
		pwd = userData[2]
	} else {
		fmt.Printf("Connect to %s\nPassword: ", host)
		pwd, err = reader.ReadString('\n')
		pwd = strings.TrimSuffix(pwd, "\n")
		if err != nil {
			fmt.Printf("Error: %v", err)
			return
		}
	}

	config := &ssh.ClientConfig{
		User: user,
		Auth: []ssh.AuthMethod{
			ssh.Password(pwd),
		},
		HostKeyCallback: ssh.InsecureIgnoreHostKey(),
	}

	connection, err := ssh.Dial("tcp", host, config)
	if err != nil {
		fmt.Printf("failed to deal: %v", err)
	} else {
		fmt.Println("Connection complete")
	}

	session, err := connection.NewSession()
	if err != nil {
		fmt.Printf("failed to create session: %v", err)
		connection.Close()
		return
	} else {
		fmt.Println("Session created")
	}

	// modes := ssh.TerminalModes{
	// 	ssh.ECHO:          0,     // disable echoing
	// 	ssh.TTY_OP_ISPEED: 14400, // input speed = 14.4kbaud
	// 	ssh.TTY_OP_OSPEED: 14400, // output speed = 14.4kbaud
	// }

	// if err := session.RequestPty("xterm", 80, 40, modes); err != nil {
	// 	session.Close()
	// }

	stdin, err := session.StdinPipe()
	if err != nil {
		fmt.Printf("Unable to setup stdin for session: %v", err)
	}
	go io.Copy(stdin, os.Stdin)

	stdout, err := session.StdoutPipe()
	if err != nil {
		fmt.Printf("Unable to setup stdout for session: %v", err)
	}
	go io.Copy(os.Stdout, stdout)

	// fmt.Print("Enter wg key name for create: ")
	// keyName, err := reader.ReadString('\n')
	// keyName = strings.TrimSuffix(keyName, "\n")
	err = session.Shell()
	stdin.Write([]byte("ls -la\n"))

	session.Run("ls -la")
	
	if err != nil {
		fmt.Printf("Can't create wg key %v", err)
	}
	// session.Run("wg genkey | tee " + keyName + "-private.key | wg pubkey > " + keyName + "-public.key")
	session.Close()
	connection.Close()
}

func createConfigFile() ([]byte, error) {
	var configData [3]string
	reader := bufio.NewReader(os.Stdin)
	tmp_value := ""
	var err error
	var answer string

	for {
		fmt.Print("Can't find a config file. Create it? (y/n): ")
		answer, err := reader.ReadString('\n')
		answer = strings.TrimSuffix(answer, "\n")
		if answer != "y" && answer != "n" {
			fmt.Println("Incorrect answer, enter 'y' or 'n'")
			continue
		}
		if err != nil {
			return nil, err
		} else if answer == "n" {
			return nil, errors.New("answer for create config file is no")
		} else {
			break
		}
	}

	// IP and PORT
	fmt.Print("Enter IP with port of your VPS server: ")
	tmp_value, err = reader.ReadString('\n')
	tmp_value = strings.TrimSuffix(tmp_value, "\n")

	if err != nil {
		fmt.Println("error step insert IP and PORT")
		return nil, err
	}
	configData[0] = tmp_value

	// USER
	fmt.Print("User name: ")
	tmp_value, err = reader.ReadString('\n')
	tmp_value = strings.TrimSuffix(tmp_value, "\n")

	if err != nil {
		fmt.Println("error step insert USER")
		return nil, err
	}
	configData[1] = tmp_value

	// PASSWORD
	for {
		fmt.Print("Will we save password?(y/n): ")
		answer, err = reader.ReadString('\n')
		if err != nil {
			fmt.Println("error step insert PASSWORD")
			return nil, err
		}
		answer = strings.TrimSuffix(answer, "\n")
		if answer != "y" && answer != "n" {
			fmt.Println("Incorrect answer, enter 'y' or 'n'")
			continue
		}
		if answer == "y" {
			fmt.Print("Password: ")
			tmp_value, err = reader.ReadString('\n')
			tmp_value = strings.TrimSuffix(tmp_value, "\n")

			if err != nil {
				fmt.Println("error step insert PASSWORD")
				return nil, err
			}
			tmp_value = strings.TrimSuffix(tmp_value, "\n")
			configData[2] = tmp_value
		}
		break
	}

	fmt.Printf("Config is created:\n"+
		"IP: %s \n"+
		"USER: %s \n", configData[0], configData[1])

	file, err := os.Create("config.conf")
	if err != nil {
		fmt.Println("Can't create the config file.")
		return nil, err
	}
	for i := 0; i < len(configData); i++ {
		file.WriteString(configData[i] + "\n")
	}
	return ioutil.ReadFile("config.conf")
}
