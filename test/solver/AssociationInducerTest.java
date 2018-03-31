package solver;

import main.AlgorithmParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import puzzle.*;

public class AssociationInducerTest {
    private static AssociationInducer inducer;
    private static Board board;
    private static FieldCircle keyCircle;
    private static Region expectedRegion;
    private static Cell expectedCell;

    @BeforeAll
    public static void initialize() {
        board = new Board(AlgorithmParameters.instance.boardDimension);
        inducer = new AssociationInducer(board);

        fillBoardWithCellsAndRegions();
    }

    private static void fillBoardWithCellsAndRegions() {
        expectedRegion = new Region();
        keyCircle = new FieldCircle(Distance.ANY);
        expectedCell = new Cell(3, 6, keyCircle);
        board.set(expectedCell, 3, 6);
        Cell cell2 = new Cell(5, 2, new FieldCircle(1));
        board.set(cell2, 5, 2);
        Cell cell3 = new Cell(0, 9);
        board.set(cell3, 0, 9);
        Cell cell4 = new Cell(4, 7, new FieldCircle(3));
        board.set(cell4, 4, 7);
        expectedRegion.addCell(expectedCell);
        expectedRegion.addCell(cell2);
        expectedRegion.addCell(cell3);
        expectedRegion.addCell(cell4);
        board.addRegion(expectedRegion);
    }

    @Test
    public void testGetRegionByCircleCorrectRegion() {
        Assertions.assertEquals(expectedRegion, inducer.getRegionBy(keyCircle));
    }

    @Test
    public void testGetCellByCircleCorrectCell() {
        Assertions.assertEquals(expectedCell, inducer.getCellBy(keyCircle));
    }
}
