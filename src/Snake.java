// To represent a snake
import java.util.LinkedList;

public class Snake {
    private LinkedList<Cell> snakePartList = new LinkedList<>();
    private Cell head;

    //constructor
    public Snake(Cell initPos) {
        head = initPos;
        snakePartList.add(head);
        head.setCellType(CellType.SNAKE_NODE);
    }

    //grow snake
    public void grow() { snakePartList.add(head); }

    //move snake - remove tail, mark old head as snake and move new head
    public void move(Cell nextCell) {
        System.out.println("Snake is moving to " + nextCell.getRow() + " " + nextCell.getCol());
        Cell tail = snakePartList.removeLast();
        tail.setCellType(CellType.EMPTY);

        head = nextCell;
        head.setCellType(CellType.SNAKE_NODE);
        snakePartList.addFirst(head);
    }

    //check if snake crashes into itself
    public boolean checkCrash(Cell nextCell) {
        System.out.println("Checking for crash");
        for (Cell cell : snakePartList) {
            if (cell == nextCell) {
                return true;
            }
        }
        return false;
    }

    public LinkedList<Cell> getSnakePartList() { return snakePartList; }

    public void setSnakePartList(LinkedList<Cell> snakePartList) { this.snakePartList = snakePartList; }

    public Cell getHead() { return head; }

    public void setHead(Cell head) { this.head = head; }
}
