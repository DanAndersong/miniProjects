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
import java.util.ArrayList;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

class GUI {
    private static int dataUrlCount = 0;
    private ArrayList<String> dataUrl;
    private ArrayList<String> data;
    private JFrame jFrame;

    public GUI (ArrayList<String> data, ArrayList<String> dataUrl) {
        jFrame = new JFrame();
        this.dataUrl = dataUrl;
        this.data = data;
        jFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame.setSize(800, 600);

        JScrollPane scrollPane = new JScrollPane(getEvents());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jFrame.getContentPane().add(scrollPane);
    }

    private Component getEvents() {
        int eventQuantity = getEventQuantity(data);
        JPanel centralPanel = new JPanel(new GridLayout(eventQuantity,1,0,0));

        for (int i = 0, eventCount = 0; i < eventQuantity; i++) {
            Box box = Box.createHorizontalBox();
            box.setBorder(new EmptyBorder(5,10,5, 0));

            //Image
            BufferedImage image = null;
            try {
                image = ImageIO.read(new URL(data.get(eventCount++)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert image != null;
            box.add(new JLabel(new ImageIcon(image.getScaledInstance(140, 100, Image.SCALE_SMOOTH))));

            //Event
            JPanel descriptionPanel = new JPanel(new GridLayout(3,1,0,0));
            descriptionPanel.setBorder(new EmptyBorder(0,10,0,0));
            descriptionPanel.setBackground(Color.white);

            while (true) {
                descriptionPanel.add(new JLabel(data.get(eventCount++)));
                if (data.get(eventCount).equals("")){
                    box.add(descriptionPanel);
                    box.add(Box.createHorizontalGlue());

                    //Button
                    JPanel jPanel = new JPanel(new BorderLayout());
                    jPanel.setBackground(Color.white);
                    JButton jButton = new JButton("Перейти");
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            openWebPage(dataUrl.get(dataUrlCount++));
                        }
                    });
                    jPanel.add(jButton ,BorderLayout.EAST);
                    box.add(jPanel);
                    centralPanel.add(box);

                    eventCount++;
                    break;
                }
            }
        }
        return centralPanel;
    }

    private int getEventQuantity(ArrayList<String> events) {
        int quantity = 0;
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).equals("")) {
                quantity++;
            }
        }
        return quantity;
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