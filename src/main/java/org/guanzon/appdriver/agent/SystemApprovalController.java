package org.guanzon.appdriver.agent;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * System User Approval form controller
 *
 * @author Michael Torres Cuison
 * @since 2019.06.10
 */
class SystemApprovalController implements Initializable {

    @FXML
    private Button btnOkay;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtField01.requestFocus();
    } 
    
    @FXML
    private void btnOkay_Click(ActionEvent event) {
        psUsername = txtField01.getText().toString();
        psPassword = txtField02.getText().toString();
        pbCancelled = false;
        unloadScene(event);
    }

    @FXML
    private void btnCancel_Click(ActionEvent event) {
        pbCancelled = true;
        unloadScene(event);
    }
    
    private void unloadScene(ActionEvent event){
        Node source = (Node)  event.getSource(); 
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    public String getUsername(){return psUsername;}
    public String getPassword(){return psPassword;}
    public boolean isCancelled(){return pbCancelled;}
    
    private String psUsername = "";
    private String psPassword = "";
    private boolean pbCancelled = true;
    
    private final String pxeModuleName = this.getClass().getSimpleName();
}
