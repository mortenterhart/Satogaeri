package gui.controller;

import field.Board;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class GuiController {

    @FXML
    private GridPane gridPane;

    @FXML
    private void initialize() {
        gridPane.setGridLinesVisible(false);
        PuzzleInitializer initializer = new PuzzleInitializer(gridPane, new Board(10));
        initializer.setup();
    }
}
