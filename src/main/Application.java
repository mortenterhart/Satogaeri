package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    public void startApplication(String... arguments) {
        launch(arguments);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Logic Puzzle Satogaeri by Nikoli");
        primaryStage.setResizable(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(GUISettings.fxmlPackagePath));
        Pane rootElement = loader.load();
        Scene windowScene = new Scene(rootElement);
        primaryStage.setScene(windowScene);

        primaryStage.show();
    }

}
