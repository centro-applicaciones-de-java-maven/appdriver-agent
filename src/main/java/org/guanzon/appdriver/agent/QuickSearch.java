/**
* Object for searching from tables.
* 
* @author  Michael Torres Cuison
* @version 1.0
* @since   2018-04-26 © Guanzon MIS-SEG 2018 and beyond 
*/

package org.guanzon.appdriver.agent;

import java.sql.ResultSet;
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
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONObject;

class QuickSearch extends Application {
    public static String pxeQuickSearch = "QuickSearchFX";
    public static String pxeQuickSearchScreen = "QuickSearch.fxml";
    
    public void setGRider(GRider foGRider){this.poGRider = foGRider;};
    public void setResultSet(ResultSet foRecSource){this.poSource = foRecSource;}
    public void setSQLSource(String fsSQLSource){this.psSQLSource = fsSQLSource;}
    public void setConditionValue(String fsValue){this.psValue = fsValue;}
    public void setColumnHeader(String fsColHeader){this.psDescription = fsColHeader;}
    public void setColunmName(String fsColName){this.psField = fsColName;}
    public void setColumnCriteria(String fsColCriteria){this.psFieldCrit = fsColCriteria;}
    public void setColumnIndex(int fnSort){this.pnSort = fnSort;}
    
    private double xOffset = 0; 
    private double yOffset = 0;
    
    private static GRider poGRider;
    private static ResultSet poSource;
    private static String psField;
    private static String psFieldCrit;
    private static String psDescription;
    private static String psSQLSource;
    private static String psReturnVal;
    private static String psValue;
    private static int pnSort;
    
    private static JSONObject poJSON;
    
    public String getResult(){return psReturnVal;}
    public JSONObject getJSON(){return poJSON;}
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(pxeQuickSearchScreen));

        /*SET PARAMETERS TO CLASS*/
        QuickSearchController oSearch = new QuickSearchController();
        oSearch.setGRider(poGRider);
        oSearch.setDataSource(poSource);
        oSearch.setSQLSource(psSQLSource);
        oSearch.setFieldValue(psValue);
        oSearch.setFieldName(psField);
        oSearch.setFieldCriteria(psFieldCrit);
        oSearch.setFieldHeader(psDescription);
        oSearch.setSort(pnSort);
        
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
        primaryStage.setTitle("Quick Search v2.0");
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