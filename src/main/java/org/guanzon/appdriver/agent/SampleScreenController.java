package org.guanzon.appdriver.agent;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
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
        
        String lsSQL = "SELECT * FROM TownCity" +
                        " LIMIT 10";
       
        ResultSet loRS = instance.executeQuery(lsSQL);
        JSONObject loJSON = ShowDialogFX.Browse(instance, 
                            loRS, 
                    "Town Name", 
                    "sTownName");
        
        System.out.println(loJSON.toString());
    }    
    
}
