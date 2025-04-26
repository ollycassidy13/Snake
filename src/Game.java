import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

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
    private Timer animationTimer;
    private final int ANIMATION_DELAY = 16; // 60 FPS

    public Game(Snake snake, Board board) {
        this.snake = snake;
        this.board = board;
        this.fruitsEaten = 0;
        this.direction = DIRECTION_NONE;
        this.gameOver = false;
        this.paused = false;

        frame = new JFrame("Snake Game");
        gamePanel = new GamePanel(board, snake);
        
        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);

        setupUI();

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        setupKeyBindings();

        timer = new Timer(500, this); 
        timer.start();

        setupAnimations();
    }

    private void setGameOver(boolean gameOver) { 
        this.gameOver = gameOver;
        if (gameOver) {
            System.out.println("crash");
            gameEnd();
        } 
    }

    private void gameEnd() {
        timer.stop();
        animationTimer.stop();
        
        JPanel gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new BoxLayout(gameOverPanel, BoxLayout.Y_AXIS));
        gameOverPanel.setBackground(new Color(0, 0, 0, 180));
        
        endMessage = new JLabel("Game Over!");
        endMessage.setForeground(Color.RED);
        endMessage.setFont(new Font("Arial", Font.BOLD, 36));
        endMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel finalScore = new JLabel("Final Score: " + fruitsEaten);
        finalScore.setForeground(Color.WHITE);
        finalScore.setFont(new Font("Arial", Font.BOLD, 24));
        finalScore.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton playAgain = createStylishButton("Play Again", new Color(200, 50, 50));
        playAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgain.addActionListener(e -> restartGame());
        
        gameOverPanel.add(Box.createVerticalGlue());
        gameOverPanel.add(endMessage);
        gameOverPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        gameOverPanel.add(finalScore);
        gameOverPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        gameOverPanel.add(playAgain);
        gameOverPanel.add(Box.createVerticalGlue());
        
        frame.remove(gamePanel);
        frame.add(gameOverPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void update() {
        if (!gameOver) {
            if (direction != DIRECTION_NONE) {
                Cell nextCell = getNextCell(snake.getHead());

                if (nextCell == null || snake.checkCrash(nextCell)) {
                    setGameOver(true);
                    timer.stop();
                } else {
                    if (nextCell.getCellType() == CellType.FOOD) {
                        snake.grow();
                        fruitsEaten++;
                        updateScore();
                        board.generateFood(snake);
                    }
                
                    snake.move(nextCell);
                }
            }
        }
        gamePanel.repaint(); 
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
        if (direction != -newDirection) {
            direction = newDirection;
        }
    }

    private void pauseGame() {
        timer.stop();
        animationTimer.stop();
        paused = true;
        pauseMessage = new JLabel("Game Paused!");
        buttonPanel.remove(pauseButton); 
        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> resumeGame());
        buttonPanel.add(resumeButton); 
        frame.remove(gamePanel);
        frame.add(pauseMessage, BorderLayout.CENTER); 
        frame.revalidate();
        frame.repaint();
    }

    private void resumeGame() {
        buttonPanel.remove(resumeButton); 
        buttonPanel.add(pauseButton);
        frame.remove(pauseMessage);
        frame.add(gamePanel, BorderLayout.CENTER);
        setupKeyBindings();
        frame.revalidate();
        frame.repaint();
        paused = false;
        timer.start();
        animationTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
    }

    private void restartGame() {
        timer.stop();
        animationTimer.stop();
        if (paused) {resumeGame();}
        board = new Board(10, 10);
        Cell initPos = new Cell(5, 5);
        snake = new Snake(initPos);
        direction = DIRECTION_NONE;
        gameOver = false;
        fruitsEaten = 0;
        updateScore();
        board.generateFood(snake);

        gamePanel = new GamePanel(board, snake);

        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        scorePanel.setBackground(new Color(40, 40, 40));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        scorePanel.add(scoreCounter);
        
        frame.add(scorePanel, BorderLayout.NORTH);
        
        frame.revalidate();
        frame.repaint();
        endMessage = null; 
        
        setupKeyBindings();
        setupAnimations();
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

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        scorePanel.setBackground(new Color(40, 40, 40));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        scoreCounter = new JLabel("Score: " + fruitsEaten);
        scoreCounter.setFont(new Font("Arial", Font.BOLD, 18));
        scoreCounter.setForeground(Color.WHITE);
        scorePanel.add(scoreCounter);
        
        frame.add(scorePanel, BorderLayout.NORTH);
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(40, 40, 40));
        
        restartButton = createStylishButton("Restart", new Color(100, 150, 200));
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);
        
        pauseButton = createStylishButton("Pause", new Color(100, 200, 100));
        pauseButton.addActionListener(e -> pauseGame());
        buttonPanel.add(pauseButton);
        
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStylishButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, color, 0, getHeight(), color.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(color.darker().darker());
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void setupAnimations() {
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.updateAnimations();
                gamePanel.repaint();
            }
        });
        animationTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Cell initPos = new Cell(5, 5);
                Snake initSnake = new Snake(initPos);
                Board board = new Board(10, 10);
                new Game(initSnake, board);
                board.generateFood(initSnake);
            }
        });
    }
}
