import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GamePanel extends JPanel {
    private Board board;
    private Snake snake;
    private Color backgroundColor = new Color(20, 20, 20);
    private Color gridColor = new Color(50, 50, 50, 100);
    
    private int cellWidth;
    private int cellHeight;
    
    private float foodPulseValue = 1.0f;
    private int foodPulseDirection = 1;
    
    public GamePanel(Board board, Snake snake) {
        this.board = board;
        this.snake = snake;
        
        setPreferredSize(new Dimension(board.COL_COUNT * 20, board.ROW_COUNT * 20));
        setBackground(backgroundColor);
        
        calculateCellSize();
    }
    
    private void calculateCellSize() {
        if (getWidth() > 0 && getHeight() > 0) {
            cellWidth = getWidth() / board.COL_COUNT;
            cellHeight = getHeight() / board.ROW_COUNT;
        } else {
            cellWidth = 20;
            cellHeight = 20;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        calculateCellSize();
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawBackground(g2d);
        drawGrid(g2d);
        drawFood(g2d);
        drawSnake(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 20, 40),
                                                 getWidth(), getHeight(), new Color(0, 40, 60));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(gridColor);
        for (int row = 0; row < board.ROW_COUNT; row++) {
            for (int col = 0; col < board.COL_COUNT; col++) {
                g2d.drawRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    private void drawSnake(Graphics2D g2d) {
        java.util.List<Cell> snakeParts = snake.getSnakePartList();
        
        for (int i = 1; i < snakeParts.size(); i++) {
            Cell cell = snakeParts.get(i);
            int x = cell.getCol() * cellWidth;
            int y = cell.getRow() * cellHeight;
            
            GradientPaint snakeGradient = new GradientPaint(
                x, y, new Color(0, 180, 0),
                x + cellWidth, y + cellHeight, new Color(0, 240, 0));
            g2d.setPaint(snakeGradient);
            
            int roundSize = Math.max(8, Math.min(cellWidth, cellHeight) / 3);
            RoundRectangle2D.Float segment = new RoundRectangle2D.Float(
                x + 1, y + 1, cellWidth - 2, cellHeight - 2, roundSize, roundSize);
            g2d.fill(segment);
            
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.fillRect(x + cellWidth/6, y + cellHeight/6, cellWidth/3, cellHeight/3);
        }
        
        if (!snakeParts.isEmpty()) {
            Cell head = snakeParts.get(0);
            int x = head.getCol() * cellWidth;
            int y = head.getRow() * cellHeight;
            
            g2d.setColor(new Color(0, 180, 0));
            int roundSize = Math.max(8, Math.min(cellWidth, cellHeight) / 3);
            g2d.fillRoundRect(x + 1, y + 1, cellWidth - 2, cellHeight - 2, roundSize, roundSize);
            
            g2d.setColor(Color.WHITE);
            int eyeSize = Math.max(2, cellWidth / 5);
            g2d.fillOval(x + cellWidth/5, y + cellHeight/4, eyeSize, eyeSize);
            g2d.fillOval(x + 3*cellWidth/5, y + cellHeight/4, eyeSize, eyeSize);
            
            g2d.setColor(Color.BLACK);
            int pupilSize = Math.max(1, eyeSize / 2);
            g2d.fillOval(x + cellWidth/5 + eyeSize/4, y + cellHeight/4 + eyeSize/4, pupilSize, pupilSize);
            g2d.fillOval(x + 3*cellWidth/5 + eyeSize/4, y + cellHeight/4 + eyeSize/4, pupilSize, pupilSize);
        }
    }

    private void drawFood(Graphics2D g2d) {
        for (int row = 0; row < board.ROW_COUNT; row++) {
            for (int col = 0; col < board.COL_COUNT; col++) {
                if (board.getCells()[row][col].getCellType() == CellType.FOOD) {
                    int x = col * cellWidth;
                    int y = row * cellHeight;
                    
                    RadialGradientPaint appleGradient = new RadialGradientPaint(
                        new Point(x + cellWidth/2, y + cellHeight/2), 
                        Math.min(cellWidth, cellHeight) / 2 * foodPulseValue,
                        new float[] {0.0f, 1.0f},
                        new Color[] {new Color(255, 50, 50), new Color(180, 0, 0)}
                    );
                    g2d.setPaint(appleGradient);
                    
                    int size = (int)(Math.min(cellWidth, cellHeight) * 0.8 * foodPulseValue);
                    int offsetX = (cellWidth - size) / 2;
                    int offsetY = (cellHeight - size) / 2;
                    g2d.fillOval(x + offsetX, y + offsetY, size, size);
                    
                    g2d.setColor(new Color(101, 67, 33));
                    g2d.fillRect(x + cellWidth/2 - cellWidth/10, y + cellHeight/5, cellWidth/5, cellHeight/7);
                    
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.fillOval(x + cellWidth/4, y + cellHeight/4, cellWidth/3, cellHeight/5);
                    
                    drawGlow(g2d, x + cellWidth/2, y + cellHeight/2, Color.RED);
                }
            }
        }
    }
    
    private void drawGlow(Graphics2D g2d, int centerX, int centerY, Color color) {
        Composite originalComposite = g2d.getComposite();
        
        int maxRadius = Math.max(cellWidth, cellHeight);
        
        for (int i = 0; i < 5; i++) {
            float alpha = 0.1f - (i * 0.02f);
            if (alpha > 0) {
                int size = maxRadius + (i * maxRadius/4);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(color);
                g2d.fillOval(centerX - size/2, centerY - size/2, size, size);
            }
        }
        
        g2d.setComposite(originalComposite);
    }
    
    public void updateAnimations() {
        foodPulseValue += foodPulseDirection * 0.03f;
        if (foodPulseValue > 1.2f) {
            foodPulseValue = 1.2f;
            foodPulseDirection = -1;
        } else if (foodPulseValue < 0.8f) {
            foodPulseValue = 0.8f;
            foodPulseDirection = 1;
        }
    }
}
