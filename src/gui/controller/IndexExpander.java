package gui.controller;

public class IndexExpander {
    private int boardWidth;

    public IndexExpander(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int expand(int x, int y) {
        return y * boardWidth + x;
    }
}
