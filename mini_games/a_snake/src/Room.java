import java.awt.event.KeyEvent;

public class Room {
    static Room game;

    private int width;
    private int height;
    private Snake snake;
    private Mouse mouse;

    public Room(int width, int height, Snake snake) {
        this.width = width;
        this.height = height;
        this.snake = snake;
        game = this;
    }

    public static void main(String[] args) {
        game = new Room(20, 20, new Snake(0, 0));
        game.createMouse();
        game.run();
    }

    public void run() {
        Controller gInterface = new Controller();
        gInterface.start();

        while (snake.isAlive()) {
            if (gInterface.hasKeyEvents()) {
                KeyEvent event = gInterface.getEventFromTop();
                if (event.getKeyChar() == 'q') return;

                if (event.getKeyCode() == KeyEvent.VK_LEFT)
                    snake.setDirection(SnakeDirection.LEFT);
                else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
                    snake.setDirection(SnakeDirection.RIGHT);
                else if (event.getKeyCode() == KeyEvent.VK_UP)
                    snake.setDirection(SnakeDirection.UP);
                else if (event.getKeyCode() == KeyEvent.VK_DOWN)
                    snake.setDirection(SnakeDirection.DOWN);
            }
            snake.move();
            print();
            sleep();
        }
    }

    private void print() {
        if (Controller.frame != null) {
            Controller.frame.setContentPane(new Layer());
            Controller.frame.setVisible(true);
        }
    }

    void eatMouse() {
        createMouse();
    }

    private void createMouse() {
        int x = (int) (Math.random() * width);
        int y = (int) (Math.random() * height);
        mouse = new Mouse(x, y);
    }

    public void sleep() {
        int size = snake.getSections().size();
        try {
            if (size > 14) {
                Thread.sleep(200);
            }else {
                Thread.sleep(520 - size * 20);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Snake getSnake() {
        return snake;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
