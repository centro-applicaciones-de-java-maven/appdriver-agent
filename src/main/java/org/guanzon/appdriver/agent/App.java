package org.guanzon.appdriver.agent;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * JavaFX App
 */
public class App extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/SampleScreen.fxml"));

            //get the controller of the main interface
            SampleScreenController loControl = new SampleScreenController();

            //the controller class to the main interface
            fxmlLoader.setController(loControl);

            //load the main interface
            Parent parent = fxmlLoader.load();
            //set the main interface as the scene
            Scene scene = new Scene(parent);

            //get the screen size
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.getIcons().add(new Image("64.png"));
            stage.setTitle("Sample Form");

            // set stage as maximized but not full screen
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch();
    }

}