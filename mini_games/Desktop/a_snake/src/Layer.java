import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Layer extends JPanel {

    @Override
    public void paintComponent(Graphics paint) {
        super.paintComponent(paint);

        paint.setColor(Color.RED);
        //Right border
        paint.fillRect(Room.game.getWidth() * 10 + 10, 0, 2, Room.game.getWidth() * 10 + 10);
        //Bottom border
        paint.fillRect(0, Room.game.getHeight() * 10 + 10, Room.game.getHeight() * 10 + 10, 2);
        paint.setColor(Color.gray);
        //Mouse
        paint.fillRect(Room.game.getMouse().getX() * 10, Room.game.getMouse().getY()* 10, 10, 10);

        ArrayList<SnakeSection> getSection = Room.game.getSnake().getSections();
        //Snake
        for (int i = 0; i < getSection.size(); i++) {
            paint.setColor(Color.blue);
            paint.fillRect(getSection.get(i).getX() * 10, getSection.get(i).getY() * 10, 10, 10);
        }
    }

}
