/**
 * @author  Michael Cuison
 * 
 * @since    2019-06-10
 */

package org.guanzon.appdriver.agent;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class SysApprovalFX extends Application {
    public final static String pxeMainForm = "/SystemApproval.fxml";
    public final static String pxeStageIcon = "64.png";
    public final static String pxeMainFormTitle = "System Approval";
    
    private double xOffset = 0; 
    private double yOffset = 0;
    
    private String psUsername = "";
    private String psPassword = "";
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(pxeMainForm));
        
        //get the controller of the main interface
        SystemApprovalController loControl = new SystemApprovalController();
        
        //the controller class to the main interface
        fxmlLoader.setController(loControl);
        
        //load the main interface
        Parent parent = fxmlLoader.load();
        
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        //set the main interface as the scene
        Scene scene = new Scene(parent);
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(pxeStageIcon));
        stage.setTitle(pxeMainFormTitle);
        stage.showAndWait();
        
        if (!loControl.isCancelled()){
            psUsername = loControl.getUsername();
            psPassword = loControl.getPassword();
        }
    }

    public static void main(String[] args) {launch(args);}
    
    public String getUsername(){return psUsername;}
    public String getPassword(){return psPassword;}
}
