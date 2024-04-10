/**
 * @author  Michael Cuison, Marlon Sayson
 * 
 * @since August 21, 2018
 */
package org.guanzon.appdriver.agent;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class ShowMessageFX {
    public static String InputText(Stage foOwner, String fsMessage, String fsTitle, String fsHeader){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.initOwner(foOwner);
        dialog.setTitle(fsTitle);
        dialog.setHeaderText(fsHeader);
        dialog.setContentText(fsMessage);
        
        Optional<String> result = dialog.showAndWait();    
        if (result.isPresent())
            return result.get();
        else return "";
    }
    
    public static String InputText(String fsMessage, String fsTitle, String fsHeader){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle(fsTitle);
        dialog.setHeaderText(fsHeader);
        dialog.setContentText(fsMessage);
        
        Optional<String> result = dialog.showAndWait();    
        if (result.isPresent())
            return result.get();
        else return "";
    }
    
    public static void Information(Stage foOwner, String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(foOwner);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        alert.showAndWait();
    }
    
    public static void Warning(Stage foOwner, String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(foOwner);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        alert.showAndWait();
    }
    
    public static void Error(Stage foOwner, String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(foOwner);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        alert.showAndWait();
    }
    
    public static boolean OkayCancel(Stage foOwner, String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(foOwner);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    
    public static boolean YesNo(Stage foOwner, String fsMessage, String fsTitle, String fsHeader){
        ButtonType Yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType No = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);  
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, fsMessage, Yes, No);
        alert.initOwner(foOwner);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(No) == Yes;
    }
    
    public static void Information(String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        alert.showAndWait();
    }
    
    public static void Warning(String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        alert.showAndWait();
    }
    
    public static void Error(String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        alert.showAndWait();
    }
    
    public static boolean OkayCancel(String fsMessage, String fsTitle, String fsHeader){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        alert.setContentText(fsMessage);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    
    public static boolean YesNo(String fsMessage, String fsTitle, String fsHeader){
        ButtonType Yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType No = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);  
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, fsMessage, Yes, No);
        alert.setTitle(fsTitle);
        alert.setHeaderText(fsHeader);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(No) == Yes;
    }
}
