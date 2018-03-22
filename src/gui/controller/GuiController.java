package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class GuiController {

    @FXML
    private GridPane gridPane;

    @FXML
    private void initialize() {
        PuzzleInitializer initializer = new PuzzleInitializer(gridPane);
    }
}
