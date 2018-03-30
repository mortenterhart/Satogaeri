package main;

import gui.controller.GuiController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logging.LogEngine;

import java.io.IOException;

public class Application extends javafx.application.Application {

    public void startApplication(String... arguments) {
        LogEngine.instance.init();
        launch(arguments);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Logic Puzzle " + Configuration.instance.projectName + " by Nikoli");
        primaryStage.setResizable(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(GUISettings.fxmlPackagePath));
        Pane rootElement = loader.load();
        GuiController controller = loader.getController();
        controller.setMainStage(primaryStage);
        controller.setHostServices(getHostServices());
        controller.enableTaskCancelling();

        Scene windowScene = new Scene(rootElement);
        primaryStage.setScene(windowScene);

        primaryStage.show();
    }

}
