package source;


public class Main {

    public static void main(String[] args) {
        valuesFactory valuesFactory = new valuesFactory(100,"   ", 3);
        valuesFactory.generateRandomUsers(5,15,true,true, true);
        valuesFactory.saveToFile("/Users/user/github/miniProjects/programms/vuGenerator/src/source/result");
    }

}
