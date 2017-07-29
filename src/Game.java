import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ThreadLocalRandom;


public class Game {
    private JFrame mainFrame;
    private JPanel contentPanel;
    private BoardPanel gamePanel;

    public static void main (String [] args) {
        Game game = new Game();
        game.startGame();
    }

    public Game() {
        setUpGame();
    }

    private void setUpGame() {
        mainFrame = new JFrame("Snake");
        contentPanel = new JPanel();
        gamePanel = new BoardPanel();

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new CardLayout());
        contentPanel.add(gamePanel);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setContentPane(contentPanel);
        mainFrame.pack();
        mainFrame.setSize(1000,1000);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);
    }

    private void startGame () {
        gamePanel.startGame();
    }
}


class BoardPanel extends JPanel implements KeyListener {
    private JPanel[] grid;
    
    private int WIDTH = 20;
    private int HEIGHT = 20;
    
    private int S_LENGTH;
    private Direction S_DIRECTION;
    private boolean CHANGE_DIRECTION = false;

    private Point[] SNAKE;
    private Point APPLE;

    private int DELAY = 100;

    // constants for game status
    private int NORMAL = 0;
    private int GAME_WIN = 1;
    private int GAME_OVER = -1;
    private int EAT_APPLE = 2;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // if the player has already changed direction in the same delay interval,
        //   then ignore the command
        if (CHANGE_DIRECTION) {
            return;
        }

        // update the direction of snake
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_RIGHT:
                if (S_DIRECTION != Direction.LEFT) {
                    S_DIRECTION = Direction.RIGHT;
                    CHANGE_DIRECTION = true;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (S_DIRECTION != Direction.RIGHT) {
                    S_DIRECTION = Direction.LEFT;
                    CHANGE_DIRECTION = true;
                }
                break;
            case KeyEvent.VK_UP:
                if (S_DIRECTION != Direction.DOWN) {
                    S_DIRECTION = Direction.UP;
                    CHANGE_DIRECTION = true;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (S_DIRECTION != Direction.UP) {
                    S_DIRECTION = Direction.DOWN;
                    CHANGE_DIRECTION = true;
                }
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

    public BoardPanel() {
        setUpBoard();
        setFocusable(true);
        setOpaque(true);
    }

    private void setUpBoard() {
        grid = new JPanel[WIDTH * HEIGHT];
        SNAKE = new Point[WIDTH * HEIGHT];

        // add panels to grid
        setLayout(new GridLayout(HEIGHT, WIDTH, 3, 3));
        for (int i = 0; i < WIDTH * HEIGHT; ++i) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.white);
            add(panel);
            grid[i] = panel;
        }

        addKeyListener(this);

    }

    public void startGame() {
        S_LENGTH = 3;
        S_DIRECTION = Direction.RIGHT;
        SNAKE = new Point[WIDTH * HEIGHT];
        
        for (int i = 0; i < WIDTH * HEIGHT; ++i) {
            SNAKE[i] = new Point(-1, -1);
        }

        SNAKE[0].x = WIDTH / 2 + 1;
        SNAKE[0].y = HEIGHT / 2;
        SNAKE[1].x = WIDTH / 2;
        SNAKE[1].y = HEIGHT / 2;
        SNAKE[2].x = WIDTH / 2 - 1;
        SNAKE[2].y = HEIGHT / 2;

        putApple();
        drawSnake();

        Timer timer = new Timer(DELAY, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSnake();
                int status = checkStatus();
                if (status == EAT_APPLE) {
                    putApple();
                    ++S_LENGTH;
                } else if (status == GAME_OVER) {
                    timer.stop();
                    System.out.println("Game over!");
                } else if (status == GAME_WIN) {
                    timer.stop();
                    System.out.println("You win!");
                }
                CHANGE_DIRECTION = false;
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

        // update the snake on grid
        grid[xyToInd(SNAKE[0].x, SNAKE[0].y)].setBackground(Color.green);
        grid[xyToInd(SNAKE[1].x, SNAKE[1].y)].setBackground(Color.gray);
        if (prev_x != -1) {
            grid[xyToInd(prev_x, prev_y)].setBackground(Color.white);
        }
    }

    private void drawSnake() {
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

    private int checkStatus() {
        if (SNAKE[0].equals(APPLE)) {
            return EAT_APPLE;
        }

        if (S_LENGTH == WIDTH * HEIGHT) {
            return GAME_WIN;
        }

        for (int i = 1; i < S_LENGTH; ++i) {
            if (SNAKE[0].equals(SNAKE[i])) {
                return GAME_OVER;
            }
        }

        return NORMAL;
    }

    private void putApple() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        boolean spotAvailable = false;
        int ind = 0;
        Point pos = new Point();

        while (!spotAvailable) {
            ind = random.nextInt(0, WIDTH * HEIGHT);
            pos = indToxy(ind);
            for (int i = 0; i < S_LENGTH; ++i) {
                // if the random point is within the snake, then regenerate an apple
                if (SNAKE[i].equals(pos)) {
                    break;
                }
                if (i == S_LENGTH - 1) {
                    spotAvailable = true;
                }
            }
        }
        APPLE = pos;
        grid[ind].setBackground(Color.red);
    }

    private int xyToInd (int x, int y) {
        return (y - 1) * WIDTH + x - 1;
    }

    private Point indToxy (int ind) {
        Point p = new Point();

        p.x = (ind + 1) % WIDTH;
        if (p.x == 0) {
            p.x = WIDTH;
        }

        p.y = (ind + 1) / HEIGHT + 1;
        return p;
    }
}
