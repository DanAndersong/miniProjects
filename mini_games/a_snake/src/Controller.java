import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Controller extends Thread {
    private Queue<KeyEvent> keyEvents = new ArrayBlockingQueue<KeyEvent>(2);
    static JFrame frame;

    @Override
    public void run() {
        frame = new JFrame("KeyPress Tester");
        frame.setTitle("Transparent JFrame Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((Room.game.getWidth() * 10) + 17, (Room.game.getHeight() * 10) + 40);
        frame.setLayout(new GridBagLayout());
        frame.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                System.exit(0);
            }
            @Override
            public void focusGained(FocusEvent e) { }

        });

        frame.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                keyEvents.add(e);
            }
            public void keyTyped(KeyEvent e) { }
            public void keyReleased(KeyEvent e) { }

        });
    }

    public boolean hasKeyEvents() {
        return !keyEvents.isEmpty();
    }

    public KeyEvent getEventFromTop() {
        return keyEvents.poll();
    }

}

