/**
* Object for searching from tables using JSON Array as source.
* 
* @author  Michael Torres Cuison
* @version 1.0
* @since   2020-11-10 © Guanzon MIS-SEG 2018 and beyond 
*/

package org.guanzon.appdriver.agent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class JSONBrowse extends Application {
    public static String pxeQuickSearchScreen = "/JSONBrowse.fxml";
    
    public void setData(JSONArray foData){this.poData = foData;}
    public void setColumnHeader(String fsColHeader){this.psDescription = fsColHeader;}
    public void setColunmName(String fsColName){this.psField = fsColName;}
    
    private double xOffset = 0; 
    private double yOffset = 0;
   
    private static JSONArray poData;
    private static String psDescription;
    private static String psField;
    private static String psReturnVal;
    
    private static JSONObject poJSON;
    
    public String getResult(){return psReturnVal;}
    public JSONObject getJSON(){return poJSON;}
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(pxeQuickSearchScreen));

        /*SET PARAMETERS TO CLASS*/
        JSONBrowseController oSearch = new JSONBrowseController();
        oSearch.setData(poData);
        oSearch.setFieldHeader(psDescription);
        oSearch.setFieldCriteria(psField);
        
        fxmlLoader.setController(oSearch);
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
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.getIcons().add(new Image("64.png"));
        primaryStage.setTitle("Quick Browse v2.0");
        primaryStage.showAndWait();
        
        if (!oSearch.isCancelled()){
            poJSON = oSearch.getJSON();
        } else{
            psReturnVal = "";
            poJSON = null;
        } 
    }

    public static void main(String[] args) {
        launch(args);
    }
}