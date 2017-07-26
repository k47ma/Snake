import sun.plugin.services.WIExplorerBrowserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Game extends JComponent implements KeyListener {
    private JFrame mainFrame;
    private JPanel gridContainer;
    private JPanel[] grid;
    
    private int WIDTH = 20;
    private int HEIGHT = 20;
    
    private int S_LENGTH;
    private Direction S_DIRECTION;

    private Point[] SNAKE;

    private int DELAY = 100;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_RIGHT:
                S_DIRECTION = Direction.RIGHT;
                break;
            case KeyEvent.VK_LEFT:
                S_DIRECTION = Direction.LEFT;
                break;
            case KeyEvent.VK_UP:
                S_DIRECTION = Direction.UP;
                break;
            case KeyEvent.VK_DOWN:
                S_DIRECTION = Direction.DOWN;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private enum Direction {
        RIGHT, LEFT, UP, DOWN
    }

    private Game() {
        setUpGame();
    }

    public static void main(String [] args) {
        Game game = new Game();
        game.startGame();
    }

    private void setUpGame() {
        mainFrame = new JFrame("Snake");
        gridContainer = new JPanel();
        grid = new JPanel[WIDTH * HEIGHT];
        SNAKE = new Point[WIDTH * HEIGHT];

        // add panels to grid
        gridContainer.setLayout(new GridLayout(WIDTH, HEIGHT, 5, 5));
        for (int i = 0; i < WIDTH * HEIGHT; ++i) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.white);
            gridContainer.add(panel);
            grid[i] = panel;
        }

        mainFrame.add(gridContainer);
        mainFrame.setSize(1000, 1000);
        mainFrame.setFocusable(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.addKeyListener(this);
        mainFrame.setVisible(true);
    }

    private void startGame() {
        S_LENGTH = 3;
        S_DIRECTION = Direction.RIGHT;
        SNAKE = new Point[WIDTH * HEIGHT];
        
        for (int i = 0; i < WIDTH * HEIGHT; ++i) {
            SNAKE[i] = new Point(-1, -1);
        }

        SNAKE[0].x = 11;
        SNAKE[0].y = 10;
        SNAKE[1].x = 10;
        SNAKE[1].y = 10;
        SNAKE[2].x = 9;
        SNAKE[2].y = 10;

        drawSnake();

        Timer timer = new Timer(DELAY, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSnake();
                drawSnake();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void moveSnake() {
        int prev_x = SNAKE[0].x;
        int prev_y = SNAKE[0].y;
        for (int i = 0; i < S_LENGTH; ++i) {
            if (i == 0) {
                switch (S_DIRECTION) {
                    case RIGHT:
                        ++SNAKE[i].x;
                        if (SNAKE[i].x > WIDTH) {
                            SNAKE[i].x = 1;
                        }
                        break;
                    case LEFT:
                        --SNAKE[i].x;
                        if (SNAKE[i].x < 1) {
                            SNAKE[i].x = WIDTH;
                        }
                        break;
                    case UP:
                        --SNAKE[i].y;
                        if (SNAKE[i].y < 1) {
                            SNAKE[i].y = HEIGHT;
                        }
                        break;
                    case DOWN:
                        ++SNAKE[i].y;
                        if (SNAKE[i].y > HEIGHT) {
                            SNAKE[i].y = 1;
                        }
                        break;
                }
            } else {
                int temp_x = SNAKE[i].x;
                int temp_y = SNAKE[i].y;
                SNAKE[i].x = prev_x;
                SNAKE[i].y = prev_y;
                prev_x = temp_x;
                prev_y = temp_y;
            }
        }
    }

    private void drawSnake() {
        clearBoard();
        for (int i = 0; i < S_LENGTH; ++i) {
            int x = SNAKE[i].x;
            int y = SNAKE[i].y;

            if (i == 0) {
                grid[xyToInd(x, y)].setBackground(Color.green);
            } else {
                grid[xyToInd(x, y)].setBackground(Color.gray);
            }
        }
    }

    private int xyToInd (int x, int y) {
        return (y - 1) * WIDTH + x - 1;
    }

    private void clearBoard() {
        for (int i = 0; i < WIDTH * HEIGHT; ++i) {
            grid[i].setBackground(Color.white);
        }
    }
}
