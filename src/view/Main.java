package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Single Flow Graph Solver");
        primaryStage.setScene(new Scene(root, 415, 550));
        primaryStage.setX(300);
        primaryStage.setY(200);
        primaryStage.show();
        primaryStage.resizableProperty().setValue(false);
    }


    public static void main(String[] args) {
        launch(args);

    }
}
