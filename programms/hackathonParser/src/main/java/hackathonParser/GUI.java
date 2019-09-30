package hackathonParser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUI extends JFrame {
    private JFrame jFrame;

    public GUI (ArrayList<String> events) {
        super("Window");
        jFrame = new JFrame();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(events.size(),1,5,1));

        for (String event : events) {
            jPanel.add(new JButton(event));
        }
        JScrollPane scrollPane = new JScrollPane(jPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(scrollPane);
    }
}
