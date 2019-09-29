package autoParser;

import javax.swing.*;

public class GUI extends JFrame {
    JFrame jFrame;

    public GUI () {
        super("Window");
        jFrame = new JFrame();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setVisible(true);

    }
}
