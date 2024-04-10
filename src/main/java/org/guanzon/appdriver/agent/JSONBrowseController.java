package org.guanzon.appdriver.agent;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;

import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONBrowseController implements Initializable {
    @FXML
    private Button cmdLoad;
    @FXML
    private Button cmdCancel;
    @FXML
    private TableView<TableModel> table;
    @FXML
    private Button btnExit;
    @FXML
    private FontAwesomeIconView glyphExit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (!pbActivated){
            //set the column Header
            paColHead = psColHead.split("»");
            paColName = psColName.split("»");
            
            if (paColHead.length != paColName.length){
                ShowMessageFX.Error(getStage(), "Column size discrepancy. Application will close.", pxeModuleName, "Please inform MIS Department.");
                System.exit(1);
            }
            
            ObservableList<String> laHeader = FXCollections.observableArrayList(paColHead);
            
            initGrid();
            loadDetail();
        }
        pbActivated = !pbActivated;
    }    
    
    private Stage getStage(){
        Stage stage = (Stage) cmdLoad.getScene().getWindow();
        return stage;
    }
    
    @FXML
    private void cmdLoad_Click(ActionEvent event) {
        loadData();
        
        pbCancelled = false;
        unloadScene(event);
    }

    @FXML
    private void closeForm(ActionEvent event) {
        pbCancelled = true;
        unloadScene(event);
    }
    
    @FXML
    private void cmdCancel_Click(ActionEvent event) {
        pbCancelled = true;
        unloadScene(event);
    }
    
    @FXML
    private void table_click(MouseEvent event) {
        model = table.getSelectionModel().getSelectedItem();
        
        if (event.getClickCount() >= 2){
            pnSelectd = table.getSelectionModel().getSelectedIndex();
            
            loadData();
        
            pbCancelled = false;
            unloadScene(event);
        }
    }
    
    private void initGrid(){
        TableColumn index01 = new TableColumn("");
        TableColumn index02 = new TableColumn("");
        TableColumn index03 = new TableColumn("");
        TableColumn index04 = new TableColumn("");
        TableColumn index05 = new TableColumn("");
        TableColumn index06 = new TableColumn("");
        TableColumn index07 = new TableColumn("");
        TableColumn index08 = new TableColumn("");
        TableColumn index09 = new TableColumn("");
        TableColumn index10 = new TableColumn("");
        
        index01.setSortable(false); index01.setResizable(false);
        index02.setSortable(false); index02.setResizable(false);
        index03.setSortable(false); index03.setResizable(false);
        index04.setSortable(false); index04.setResizable(true);
        index05.setSortable(false); index05.setResizable(true);
        index06.setSortable(false); index06.setResizable(true);
        index07.setSortable(false); index07.setResizable(true);
        index08.setSortable(false); index08.setResizable(true);
        index09.setSortable(false); index09.setResizable(true);
        index10.setSortable(false); index10.setResizable(true);
        
        table.getColumns().clear();
        
        /*Check the number of columns for viewing*/
        int lnCtr;
        for(lnCtr = 1; lnCtr <= paColHead.length; lnCtr++){
            switch (lnCtr){
                case 1: 
                    index01.setText(paColHead[lnCtr -1]); table.getColumns().add(index01);
                    index01.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index01"));
                    
                    switch (paColHead.length){
                        case 1:
                            index01.prefWidthProperty().bind(table.widthProperty().multiply(1)); break;
                        case 2:
                        case 3:
                        case 4:
                            index01.prefWidthProperty().bind(table.widthProperty().multiply(0.25)); break;
                        default:
                            index01.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
                    }
                    break;
                case 2: 
                    index02.setText(paColHead[lnCtr -1]); table.getColumns().add(index02);
                    index02.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index02"));
                    
                    switch (paColHead.length){
                        case 2:
                            index02.prefWidthProperty().bind(table.widthProperty().multiply(0.75)); break;
                        case 3:
                            index02.prefWidthProperty().bind(table.widthProperty().multiply(0.50)); break;
                        case 4:
                            index02.prefWidthProperty().bind(table.widthProperty().multiply(0.40)); break;
                        default:
                            index02.prefWidthProperty().bind(table.widthProperty().multiply(0.30));
                    }                    
                    break;
                case 3: 
                    index03.setText(paColHead[lnCtr -1]); table.getColumns().add(index03);
                    index03.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index03"));
                    
                    switch (paColHead.length){
                        case 3:
                            index03.prefWidthProperty().bind(table.widthProperty().multiply(0.25)); break;
                        case 4:
                            index03.prefWidthProperty().bind(table.widthProperty().multiply(0.20)); break;
                        default:
                            index03.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                    }
                    break;
                case 4:
                    index04.setText(paColHead[lnCtr -1]); table.getColumns().add(index04);
                    index04.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
                    index04.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index04"));
                    
                    switch (paColHead.length){
                        case 4:
                            index04.prefWidthProperty().bind(table.widthProperty().multiply(0.15)); break;
                        default:
                            index04.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
                    }
                    break;
                case 5: 
                    index05.setText(paColHead[lnCtr -1]); table.getColumns().add(index05);
                    index05.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                    index05.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index05")); break;
                case 6: 
                    index06.setText(paColHead[lnCtr -1]); table.getColumns().add(index06);
                    index06.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                    index06.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index06")); break;
                case 7: 
                    index07.setText(paColHead[lnCtr -1]); table.getColumns().add(index07);
                    index07.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                    index07.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index07")); break;
                case 8: 
                    index08.setText(paColHead[lnCtr -1]); table.getColumns().add(index08);
                    index08.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                    index08.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index08")); break;
                case 9: 
                    index09.setText(paColHead[lnCtr -1]); table.getColumns().add(index09);
                    index09.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                    index09.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index09")); break;
                case 10: 
                    index10.setText(paColHead[lnCtr -1]); table.getColumns().add(index10);
                    index10.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
                    index10.setCellValueFactory(new PropertyValueFactory<TableModel,String>("index10")); break;
                default:
                    ShowMessageFX.Error(getStage(), "Column index not supported. [" + lnCtr + "]", pxeModuleName, "Please inform MIS Department.");
                    System.exit(1);
            }
        }

        /*Set data source to table*/
        table.setItems(data);
    }
      
    private void loadDetail(){
        JSONObject loJSON;
        
        if (poArray== null){
            ShowMessageFX.Error(getStage(), "JSON Array is null.", pxeModuleName, "Please inform MIS Department.");
            System.exit(1);
        }
        
        data.clear();
        
        for (Object obj : poArray){
            loJSON = (JSONObject) obj;
            
            data.add(new TableModel((paColName.length <= 0 ? "" : (String) loJSON.get(paColName[0])), 
                                    (paColName.length <= 1 ? "" : (String) loJSON.get(paColName[1])), 
                                    (paColName.length <= 2 ? "" : (String) loJSON.get(paColName[2])), 
                                    (paColName.length <= 3 ? "" : (String) loJSON.get(paColName[3])), 
                                    (paColName.length <= 4 ? "" : (String) loJSON.get(paColName[4])),
                                    (paColName.length <= 5 ? "" : (String) loJSON.get(paColName[5])), 
                                    (paColName.length <= 6 ? "" : (String) loJSON.get(paColName[6])), 
                                    (paColName.length <= 7 ? "" : (String) loJSON.get(paColName[7])), 
                                    (paColName.length <= 8 ? "" : (String) loJSON.get(paColName[8])),
                                    (paColName.length <= 9 ? "" : (String) loJSON.get(paColName[9]))));
        }
        
        table.getSelectionModel().selectFirst();
        pnSelectd = table.getSelectionModel().getSelectedIndex();
    }
    
    private void loadData(){
        poJSON = null;
        
        if (poArray.isEmpty()) return;
        
        int lnCtr = -1;
        for (Object obj : poArray){
            lnCtr += 1;
            
            if (lnCtr == pnSelectd){
                poJSON = (JSONObject) obj;
                return;
            }
        }
    }
    
    private void unloadScene(ActionEvent event){
        Node source = (Node)  event.getSource(); 
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    private void unloadScene(MouseEvent event){
        Node source = (Node)  event.getSource(); 
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    public void setData(JSONArray foData){this.poArray = foData;}
    public void setFieldHeader(String fsDescript){this.psColHead = fsDescript;}
    public void setFieldCriteria(String fsCriteria){this.psColName = fsCriteria;}
    
    public boolean isCancelled(){return pbCancelled;}
    public JSONObject getJSON(){return poJSON;}
    
    private boolean pbCancelled;
    private boolean pbActivated;
    
    private String psColHead;
    private String psColName;
    
    private String[] paColHead;
    private String[] paColName;
    
    private JSONObject poJSON = null;
    private JSONArray poArray;
    
    private int pnSelectd = -1;
    
    private TableModel model;
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    
    private final String pxeModuleName = "JSONBrowser";
}
