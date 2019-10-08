import java.util.ArrayList;

public class Snake {
    private SnakeDirection direction;
    private boolean isAlive;
    private ArrayList<SnakeSection> sections;

    public Snake(int x, int y) {
        sections = new ArrayList<>();
        sections.add(new SnakeSection(x, y));
        isAlive = true;
        direction = SnakeDirection.DOWN;
    }

    void move() {
        if (!isAlive) return;

        switch (direction) {
            case DOWN:  move(0, 1);  { break; }
            case UP:    move(0, -1); { break; }
            case LEFT:  move(-1, 0); { break; }
            case RIGHT: move(1, 0);
        }
    }

    private void move(int dx, int dy) {
        int x = sections.get(0).getX() + dx;
        int y = sections.get(0).getY() + dy;
        SnakeSection newHead = new SnakeSection(x, y);
        checkBorders(newHead);
        checkBody(newHead);

        if (isAlive) {
            sections.add(0, newHead);

            if (Room.game.getMouse().getX() == x && Room.game.getMouse().getY() == y) {
                Room.game.eatMouse();
                return;
            }
            sections.remove(sections.size()-1);
        }
    }

    private void checkBorders (SnakeSection head) {
        if (head.getY() > Room.game.getHeight() || head.getX() > Room.game.getWidth()
                                           || head.getX() < 0 || head.getY() < 0) {
            isAlive = false;
        }
    }

    private void checkBody(SnakeSection head) {
        if (sections.contains(head)) {
            isAlive = false;
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setDirection(SnakeDirection direction) {
        this.direction = direction;
    }

    public ArrayList<SnakeSection> getSections() {
        return sections;
    }
}
