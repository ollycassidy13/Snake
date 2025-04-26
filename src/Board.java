public class Board {
    final int ROW_COUNT, COL_COUNT;
    private Cell[][] cells;

    public Board(int rowCount, int columnCount) {
        ROW_COUNT = rowCount;
        COL_COUNT = columnCount;

        cells = new Cell[ROW_COUNT][COL_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int column = 0; column < COL_COUNT; column++) {
                cells[row][column] = new Cell(row, column);
            }
        }
    }

    public Cell[][] getCells() { return cells; }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public void generateFood(Snake snake) {
        java.util.List<Cell> snakeCells = snake.getSnakePartList();
        
        boolean validPositionFound = false;
        int row, col;
        
        while (!validPositionFound) {
            row = (int)(Math.random() * ROW_COUNT);
            col = (int)(Math.random() * COL_COUNT);
            
            boolean conflict = false;
            for (Cell snakePart : snakeCells) {
                if (snakePart.getRow() == row && snakePart.getCol() == col) {
                    conflict = true;
                    break;
                }
            }
            
            if (!conflict) {
                cells[row][col].setCellType(CellType.FOOD);
                validPositionFound = true;
            }
        }
    }
}
