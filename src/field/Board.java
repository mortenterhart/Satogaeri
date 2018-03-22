package field;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Region> containedRegions;
    private Cell[][] cellBoard;

    public Board() {
        containedRegions = new ArrayList<>();
        cellBoard = new Cell[10][10];
    }
}
