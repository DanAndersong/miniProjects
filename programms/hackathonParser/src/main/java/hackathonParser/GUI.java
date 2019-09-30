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

        JPanel centralPanel = new JPanel(new GridLayout(events.size()/3,1,0,0));

        for (int i = 0, eventCount = 0; i < events.size()/3; i++) {
            Box box = Box.createHorizontalBox();
            box.setBorder(new EmptyBorder(5,10,5,10));

            //Image
            BufferedImage image = null;
            try {
                image = ImageIO.read(new URL("https://it-events.com/system/events/logos/000/016/563/original/80_227_227_event_5ba4e9dda12e7.png?1568812413"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert image != null;
            box.add(new JLabel(new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH))));
            box.add(Box.createHorizontalStrut(10));

            //Event
            JPanel eventPanel = new JPanel(new GridLayout(3,1,0,1));
            for (int j = 0; j < 3; j++) {
                eventPanel.add(new JLabel(events.get(eventCount++)));
            }
            box.add(eventPanel);
            box.add(Box.createHorizontalGlue());

            //Button
            JPanel jPanel = new JPanel(new BorderLayout());
            jPanel.add(new JButton("Узнать больше"), BorderLayout.EAST);
            box.add(jPanel);

            centralPanel.add(box);
        }

        JScrollPane scrollPane = new JScrollPane(centralPanel);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane);
    }
}
