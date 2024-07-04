import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class Game implements ActionListener {
    public static final int DIRECTION_NONE = 0,
                            DIRECTION_RIGHT = 1,
                            DIRECTION_LEFT = -1,
                            DIRECTION_UP = 2,
                            DIRECTION_DOWN = -2;
    private Snake snake;
    private Board board;
    private int direction;
    private boolean gameOver;
    private int fruitsEaten; 
    private GamePanel gamePanel;
    private Timer timer;
    private JFrame frame;
    private JLabel scoreCounter;
    private JLabel endMessage;
    private boolean paused;
    private JLabel pauseMessage;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton restartButton;
    private JPanel buttonPanel;

    public Game(Snake snake, Board board) {
        //initialise variables
        this.snake = snake;
        this.board = board;
        this.fruitsEaten = 0;
        this.direction = DIRECTION_NONE;
        this.gameOver = false;
        this.paused = false;

        //set up the game window
        frame = new JFrame("Snake Game");
        gamePanel = new GamePanel(board, snake);
        
        //add game panel to the center
        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);

        //add restart and pause buttons at bottom
        buttonPanel = new JPanel(new FlowLayout());
        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> pauseGame());
        buttonPanel.add(pauseButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        //add score counter at top
        scoreCounter = new JLabel("Score: " + fruitsEaten);
        frame.add(scoreCounter, BorderLayout.NORTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //set up key bindings
        setupKeyBindings();

        //set up the game timer
        timer = new Timer(500, this); //game updates every 500ms
        timer.start();
    }

    private void setGameOver(boolean gameOver) { 
        this.gameOver = gameOver;
        if (gameOver) {
            System.out.println("crash");
            gameEnd();
        } 
    }

    private void gameEnd() {
        endMessage = new JLabel("Game Over!");
        frame.remove(gamePanel);; //remove gamepanel
        frame.add(endMessage, BorderLayout.CENTER); //add Game Over message
        frame.revalidate();
        frame.repaint();
    }

    //updates game
    private void update() {
        if (!gameOver) {
            if (direction != DIRECTION_NONE) {
                Cell nextCell = getNextCell(snake.getHead());

                //if snake crashes/out of bounds
                if (nextCell == null || snake.checkCrash(nextCell)) {
                    setGameOver(true);
                    timer.stop();
                } else {
                    //if food
                    if (nextCell.getCellType() == CellType.FOOD) {
                        snake.grow();
                        fruitsEaten++;
                        updateScore();
                        board.generateFood();
                    }
                
                    snake.move(nextCell);
                }
            }
        }
        gamePanel.repaint(); //refresh the game panel
    }

    private void updateScore() {
        scoreCounter.setText("Score: " + fruitsEaten);
    }

    private Cell getNextCell(Cell currentPosition) {
        int row = currentPosition.getRow();
        int col = currentPosition.getCol();

        if (direction == DIRECTION_RIGHT) {
            col++;
        } 
        else if (direction == DIRECTION_LEFT) {
            col--;
        } 
        else if (direction == DIRECTION_UP) {
            row--;
        } 
        else if (direction == DIRECTION_DOWN) {
            row++;
        }

        if (row < 0 || row >= board.ROW_COUNT || col < 0 || col >= board.COL_COUNT) {
            return null;
        }

        return board.getCells()[row][col];
    }

    private void changeDirection(int newDirection) {
        //prevent snake from reversing
        if (direction != -newDirection) {
            direction = newDirection;
        }
    }

    private void pauseGame() {
        timer.stop();
        paused = true;
        pauseMessage = new JLabel("Game Paused!");
        buttonPanel.remove(pauseButton); //remove pause button
        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> resumeGame());
        buttonPanel.add(resumeButton); //add resume button
        frame.remove(gamePanel);
        frame.add(pauseMessage, BorderLayout.CENTER); //add game paused message
        frame.revalidate();
        frame.repaint();
    }

    private void resumeGame() {
        buttonPanel.remove(resumeButton); //remove resume button
        buttonPanel.add(pauseButton); //add pause button
        frame.remove(pauseMessage);
        frame.add(gamePanel, BorderLayout.CENTER);
        setupKeyBindings();
        frame.revalidate();
        frame.repaint();
        paused = false;
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    private void restartGame() {
        timer.stop();
        if (paused) {resumeGame();}
        board = new Board(10, 10);
        Cell initPos = new Cell(5, 5);
        snake = new Snake(initPos);
        direction = DIRECTION_NONE;
        gameOver = false;
        fruitsEaten = 0;
        updateScore();
        board.generateFood();

        gamePanel = new GamePanel(board, snake);

        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(scoreCounter, BorderLayout.NORTH); // Re-add the score counter
        frame.revalidate();
        frame.repaint();
        endMessage = null; // Reset the end message
        
        setupKeyBindings();
        timer.start();
    }

    private void setupKeyBindings() {
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        changeDirection(DIRECTION_UP);
                        break;
                    case KeyEvent.VK_S:
                        changeDirection(DIRECTION_DOWN);
                        break;
                    case KeyEvent.VK_A:
                        changeDirection(DIRECTION_LEFT);
                        break;
                    case KeyEvent.VK_D:
                        changeDirection(DIRECTION_RIGHT);
                        break;
                }
            }
        });
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Cell initPos = new Cell(5, 5);
                Snake initSnake = new Snake(initPos);
                Board board = new Board(10, 10);
                new Game(initSnake, board);
                board.generateFood();
            }
        });
    }
}
