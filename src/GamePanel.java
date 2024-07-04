import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private Board board;
    private Snake snake;

    public GamePanel(Board board, Snake snake) {
        this.board = board;
        this.snake = snake;
        setPreferredSize(new Dimension(board.ROW_COUNT * 20, board.COL_COUNT * 20)); //size based on the grid
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawSnake(g);
        drawFood(g);
    }

    private void drawGrid(Graphics g) {
        for (int row = 0; row < board.ROW_COUNT; row++) {
            for (int col = 0; col < board.COL_COUNT; col++) {
                g.drawRect(col * 20, row * 20, 20, 20); //draws the grid cells
            }
        }
    }

    private void drawSnake(Graphics g) {
        g.setColor(Color.GREEN);
        for (Cell cell : snake.getSnakePartList()) {
            g.fillRect(cell.getCol() * 20, cell.getRow() * 20, 20, 20); //draws the snake parts
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(Color.RED);
        for (int row = 0; row < board.ROW_COUNT; row++) {
            for (int col = 0; col < board.COL_COUNT; col++) {
                if (board.getCells()[row][col].getCellType() == CellType.FOOD) {
                    g.fillRect(col * 20, row * 20, 20, 20); //draws the food
                }
            }
        }
    }
}
