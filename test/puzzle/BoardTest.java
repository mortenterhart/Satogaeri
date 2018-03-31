package puzzle;

import main.AlgorithmParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BoardTest {
    private static Board board;

    @BeforeAll
    public static void addCirclesToBoard() {
        board = new Board(AlgorithmParameters.instance.boardDimension);
        board.set(new Cell(0, 0, new FieldCircle(3)), 0, 0);
        board.set(new Cell(5, 4, new FieldCircle(1)), 5, 4);
        board.set(new Cell(2, 7, new FieldCircle(Distance.ANY)), 2, 7);
        board.set(new Cell(1, 6), 1, 6);
    }

    @Test
    public void testGetCellFromLocation() {
        Assertions.assertEquals(1, board.get(5, 4).getCircle().getDistance().toIntValue());
    }

    @Test
    public void testGetAllRegisteredCirclesEquals3() {
        Assertions.assertEquals(3, board.getAllRegisteredCircles().size());
    }
}
