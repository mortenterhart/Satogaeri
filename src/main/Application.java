package main;

import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    public void startApplication(String... arguments) {
        launch(arguments);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();
    }

}
