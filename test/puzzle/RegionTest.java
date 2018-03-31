package puzzle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegionTest {

    @Test
    public void testCellIsAdded() {
        Region region = new Region();
        region.addCell(new Cell(0, 0));

        Assertions.assertFalse(region.getCells().isEmpty());
    }

    @Test
    public void testRegionContainsCell() {
        Region region = new Region();
        Cell cell = new Cell(0, 1);
        region.addCell(cell);

        Assertions.assertTrue(region.contains(cell));
    }

    @Test
    public void testRegionContainsCellWithCircle() {
        Region region = new Region();
        FieldCircle circle = new FieldCircle(3);
        Cell cell = new Cell(0, 1, circle);
        region.addCell(cell);

        Assertions.assertTrue(region.contains(circle));
    }

    @Test
    public void testRegionContainsNumberOfCircles() {
        Region region = new Region();
        Cell cell1 = new Cell(0, 0, new FieldCircle(3));
        Cell cell2 = new Cell(0, 1, new FieldCircle(2));
        Cell cell3 = new Cell(1, 0, new FieldCircle(Distance.ANY));
        region.addCell(cell1);
        region.addCell(cell2);
        region.addCell(cell3);

        Assertions.assertEquals(3, region.getNumberOfCircles());
    }

    @Test
    public void testSetFinalRegionSetsAllCellsInvariant() {
        Region region = new Region();
        Cell cell1 = new Cell(0, 0, new FieldCircle(3));
        Cell cell2 = new Cell(0, 1, new FieldCircle(2));
        Cell cell3 = new Cell(1, 0, new FieldCircle(Distance.ANY));
        region.addCell(cell1);
        region.addCell(cell2);
        region.addCell(cell3);

        region.setFinal(true);

        for (Cell cell : region.getCells()) {
            if (cell.hasAnyCircleRegistered()) {
                Assertions.assertTrue(cell.isInvariant());
            }
        }
    }

    @Test
    public void testSetFinalTurnsToFinal() {
        Region region = new Region();
        region.setFinal(true);

        Assertions.assertTrue(region.isFinal());
    }
}
