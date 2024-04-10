package org.guanzon.appdriver.agent;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.json.simple.JSONObject;

class QuickSearchController implements Initializable {
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox cmbField;
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
            paFldName = psFldName.split("»");
            
            //set field to be used on dynamic querying of database...
            if(!psSQLSrce.isEmpty())
                paColName = psColName.split("»");

            if (paColHead.length != paFldName.length){
                ShowMessageFX.Error(getStage(), "Column size discrepancy. Application will close.", pxeModuleName, "Please inform MIS Department.");
                System.exit(1);
            }
            
            ObservableList<String> laHeader = FXCollections.observableArrayList(paColHead);
            
            cmbField.getItems().clear();
            cmbField.setItems(laHeader);
            cmbField.getSelectionModel().select(pnSort);
            txtSearch.setText(psCondition);
            
            initGrid();
            executeQuery();
        }
        pbActivated = !pbActivated;
    }    
    
    private Stage getStage(){
        Stage stage = (Stage) txtSearch.getScene().getWindow();
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
    private void cmdField_Click(ActionEvent event) {
        pnSort = cmbField.getSelectionModel().getSelectedIndex();
        txtSearch.setPromptText((String) cmbField.getValue());
        executeQuery();
    }
    
    @FXML
    private void table_click(MouseEvent event) {
        model = table.getSelectionModel().getSelectedItem();
        pnSelectd = table.getSelectionModel().getSelectedIndex();
        txtSearch.requestFocus();
        
        if (event.getClickCount() >= 2){
            loadData();
        
            pbCancelled = false;
            unloadScene(event);
        }
    }
    
    @FXML
    private void txtSearch_Released(KeyEvent event) throws SQLException {
        executeQuery();
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
    
    private void executeQuery(){       
        String lsSQL = psSQLSrce;
        
        if (!lsSQL.isEmpty()){
            String lsCondition;
            
            lsCondition = paColName[pnSort] + " LIKE " + SQLUtil.toSQL(txtSearch.getText() + "%");

            lsSQL = MiscUtil.addCondition(lsSQL, lsCondition) + " ORDER BY " + paColName[pnSort];

            try {
                poSource = poGRider.executeQuery(lsSQL);
                loadDetail();
            } catch (SQLException ex) {
                Logger.getLogger(QuickSearchController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                loadDetail();
            } catch (SQLException ex) {
                Logger.getLogger(QuickSearchController.class.getName()).log(Level.SEVERE, null, ex);
                ShowMessageFX.Error(getStage(), ex.getMessage(), pxeModuleName, "Please inform MIS Department.");
                System.exit(1);
            }
            
            txtSearch.setDisable(true);
            cmbField.setDisable(true);
        }
        
    }    
    private void loadDetail() throws SQLException{
        if (poSource == null){
            if (psSQLSrce != null && !psSQLSrce.isEmpty()){
                poSource = poGRider.executeQuery(psSQLSrce);
            } else {
                ShowMessageFX.Error(getStage(), "Both query and resulset is null.", pxeModuleName, "Please inform MIS Department.");
                System.exit(1);
            }
        }
        
        data.clear();
        
        if (MiscUtil.RecordCount(poSource) > 0 ) poSource.beforeFirst();
        
        while(poSource.next()){
            data.add(new TableModel((paFldName.length <= 0 ? "" : poSource.getString(paFldName[0])), 
                                    (paFldName.length <= 1 ? "" : poSource.getString(paFldName[1])), 
                                    (paFldName.length <= 2 ? "" : poSource.getString(paFldName[2])), 
                                    (paFldName.length <= 3 ? "" : poSource.getString(paFldName[3])), 
                                    (paFldName.length <= 4 ? "" : poSource.getString(paFldName[4])),
                                    (paFldName.length <= 5 ? "" : poSource.getString(paFldName[5])), 
                                    (paFldName.length <= 6 ? "" : poSource.getString(paFldName[6])), 
                                    (paFldName.length <= 7 ? "" : poSource.getString(paFldName[7])), 
                                    (paFldName.length <= 8 ? "" : poSource.getString(paFldName[8])),
                                    (paFldName.length <= 9 ? "" : poSource.getString(paFldName[9]))));
        }
        
        table.getSelectionModel().selectFirst();
        pnSelectd = table.getSelectionModel().getSelectedIndex();
    }
    
    private void loadData(){
        JSONObject loJSON = new JSONObject();
        try {
            if (MiscUtil.RecordCount(poSource) <= 0) return;
            
            poSource.absolute((int) pnSelectd + 1);
            for (int lnCtr = 1; lnCtr <= poSource.getMetaData().getColumnCount(); lnCtr++){
                //getColumnName = returns the column name
                //loJSON.put(poSource.getMetaData().getColumnName(lnCtr), poSource.getString(lnCtr));
                
                //getColumnLabel = returns the column alias
                loJSON.put(poSource.getMetaData().getColumnLabel(lnCtr), poSource.getString(lnCtr));
            }
        } catch (SQLException ex) {
            Logger.getLogger(QuickSearchController.class.getName()).log(Level.SEVERE, null, ex);
            ShowMessageFX.Error(getStage(), ex.getMessage(), pxeModuleName, "Please inform MIS Department.");
            System.exit(1);
        }

        poJSON = loJSON;
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
    
    public void setGRider(GRider foGRider){this.poGRider = foGRider;}
    public void setSQLSource(String fsSource){this.psSQLSrce = fsSource;}
    public void setFieldValue(String fsCondition){this.psCondition = fsCondition;}
    public void setFieldName(String fsField){this.psFldName = fsField;}
    public void setFieldHeader(String fsDescript){this.psColHead = fsDescript;}
    public void setFieldCriteria(String fsCriteria){this.psColName = fsCriteria;}
    public void setSort(int fnSort){this.pnSort = fnSort;}
    public void setDataSource(ResultSet foSource){this.poSource = foSource;}
    
    public boolean isCancelled(){return pbCancelled;}
    public JSONObject getJSON(){return poJSON;}
    
    private GRider poGRider;
    private ResultSet poSource;
    private String psSQLSrce = "";
    private String psCondition = "";
    private String psColName;
    private String psFldName;
    private String psColHead;
    
    private String[] paColHead;
    private String[] paColName;
    private String[] paFldName;
    
    private int pnSort = 0;
    private long pnSelectd = -1;
    
    private boolean pbActivated = false;
    private boolean pbCancelled = true;
    private JSONObject poJSON = null;
    
    private ObservableList<TableModel> data = FXCollections.observableArrayList();
    private TableModel model;
    
    private final String pxeModuleName = "QuickSearchFX";
}
