package hackathonParser;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GUI extends JFrame {
    private JFrame jFrame;

    public GUI (ArrayList<String> events) {
        super("Hackathons");
        jFrame = new JFrame();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        int eventQuantity = getEventQuantity(events);

        JPanel centralPanel = new JPanel(new GridLayout(eventQuantity,1,0,0));

        for (int i = 0, eventCount = 0; i < eventQuantity;) {
            if (events.get(eventCount).equals("")){
                eventCount++;
                continue;
            }

            Box box = Box.createHorizontalBox();
            box.setBorder(new EmptyBorder(5,10,5,10));

            //Image
            BufferedImage image = null;
            try {
                image = ImageIO.read(new URL(events.get(eventCount++)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert image != null;
            box.add(new JLabel(new ImageIcon(image.getScaledInstance(140, 100, Image.SCALE_SMOOTH))));
//            box.add(Box.createHorizontalStrut());

            //Event
            JPanel descriptionPanel = new JPanel(new GridLayout(3,1,0,0));
            descriptionPanel.setBorder(new EmptyBorder(0,10,0,0));
            descriptionPanel.setBackground(Color.white);
            for (int j = 0; j < 3; j++) {
                descriptionPanel.add(new JLabel(events.get(eventCount++)));
            }
            box.add(descriptionPanel);
            box.add(Box.createHorizontalGlue());

            //Button
            JPanel jPanel = new JPanel(new BorderLayout());
            jPanel.setBackground(Color.white);
            jPanel.add(new JButton("Перейти"),BorderLayout.EAST);
            box.add(jPanel);

            centralPanel.add(box);
            i++;
        }

        JScrollPane scrollPane = new JScrollPane(centralPanel);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane);
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
}
