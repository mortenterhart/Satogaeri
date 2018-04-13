package solver;

import main.AlgorithmParameters;
import move.MoveDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import puzzle.Board;
import puzzle.Cell;
import puzzle.FieldCircle;

public class DirectionMapperTest {

    private static DirectionMapper mapper;
    private static MoveDirection direction = MoveDirection.DOWN;

    @BeforeAll
    public static void chooseDirectionAndBuildBoard() {
        Board board = new Board(AlgorithmParameters.instance.boardDimension);
        mapper = new DirectionMapper(direction, board);
    }

    @Test
    public void testMapNextMovePosition() {
        Cell cell = new Cell(0, 0, new FieldCircle(2));

        Assertions.assertEquals(2, mapper.mapNextMovePosition(cell));
    }

    @Test
    public void testMapRelatedCoordinate() {
        Cell cell = new Cell(0, 4, new FieldCircle(3));

        Assertions.assertEquals(4, mapper.mapRelatedCoordinate(cell));
    }

    @Test
    public void testMapMoveConditionExclusive() {
        Assertions.assertFalse(mapper.mapMoveConditionExclusive(3, 3));
    }

    @Test
    public void testMapMoveConditionInclusive() {
        Assertions.assertTrue(mapper.mapMoveConditionInclusive(3, 3));
    }

    @Test
    public void testMapLoopStep() {
        Assertions.assertEquals(1, mapper.mapLoopStep());
    }

    @Test
    public void testMapBoardBoundaries() {
        Assertions.assertTrue(mapper.mapBoardBoundaries(9));
    }
}
