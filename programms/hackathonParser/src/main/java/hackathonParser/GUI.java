package hackathonParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

class GUI {
    private Map<Integer, Map> data;
    private JFrame jFrame;

    public GUI (Map<Integer, Map> data) {
        this.data = data;
        jFrame = new JFrame();
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(getEvents());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jFrame.getContentPane().add(scrollPane);
        jFrame.pack();
    }

    private Component getEvents() {
        JPanel centralPanel = new JPanel(new GridLayout(data.size(),1,0,0));

        for (Map.Entry<Integer, Map> eventPair : data.entrySet()){
            Map<String,String> value = eventPair.getValue();
            Box box = Box.createHorizontalBox();
            box.setBorder(new EmptyBorder(5,10,5, 0));

            //Logo
            BufferedImage logo= null;
            try {
                logo = ImageIO.read(new URL(value.get("logo")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert logo != null;
            box.add(new JLabel(new ImageIcon(logo.getScaledInstance(140, 100, Image.SCALE_SMOOTH))));

            //description
            JPanel descriptionPanel = new JPanel(new GridLayout(3,1,0,0));
            descriptionPanel.setBorder(new EmptyBorder(0,10,0,0));
            descriptionPanel.setBackground(Color.white);
            descriptionPanel.add(new JLabel(value.get("title")));
            descriptionPanel.add(new JLabel(value.get("cost")));
            descriptionPanel.add(new JLabel(value.get("other")));
            box.add(descriptionPanel);

            //Button
            JPanel jPanel = new JPanel(new BorderLayout());
            jPanel.setBackground(Color.white);
            JButton jButton = new JButton("Перейти");
            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    openWebPage(value.get("url"));
                }
            });
            jPanel.add(jButton ,BorderLayout.EAST);
            box.add(jPanel);
                    centralPanel.add(box);
        }
        return centralPanel;
    }

    private static void openWebPage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JFrame getjFrame() {
        return jFrame;
    }
}