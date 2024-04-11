package org.guanzon.appdriver.agent;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONObject;

public class SampleScreenController implements Initializable {
    GRider instance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Java_Systems";
        }
        else{
            path = "/srv/GGC_Java_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        instance = new GRider("gRider");
        
        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getErrMsg());
            System.exit(1);
        }
    }    
    
    @FXML
    private void button_click(ActionEvent event) {
        //ShowMessageFX.Information("This is a test.", "This is a test.", "This is a test.");
        
        String lsSQL = "SELECT * FROM TownCity" +
                        " LIMIT 10";
       
        ResultSet loRS = instance.executeQuery(lsSQL);
        //JSONObject loJSON = ShowDialogFX.Browse(instance, loRS, "TownID»Town Name", "sTownIDxx»sTownName");

        //JSONObject loJSON = ShowDialogFX.Search(instance, lsSQL, "", "Town ID»Town Name", "sTownIDxx»sTownName", "sTownIDxx»sTownName", 1);

        JSONObject loJSON = ShowDialogFX.getUserApproval(instance);
        ShowMessageFX.Information(loJSON.toString(), "This is a test.", "This is a test.");
    }
    
}
