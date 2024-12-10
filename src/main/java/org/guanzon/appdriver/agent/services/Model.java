package org.guanzon.appdriver.agent.services;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

public class Model implements GEntity{
    protected LogWrapper logwrapr;
    
    protected String XML;
    protected String ID;
    protected String TABLE;
    
    protected GRider poGRider;
    protected CachedRowSet poEntity;
    protected JSONObject poJSON;
    protected int pnEditMode;

    public void setApplicationDriver(GRider applicationDriver){
        poGRider = applicationDriver;
    }
    
    public void setXML(String xmlFileName){
        xmlFileName = xmlFileName.replace(".xml", "");
        
        XML = xmlFileName + ".xml";
        logwrapr = new LogWrapper(xmlFileName, "cas-error.log");
    }
    
    public void setTableName(String tableName){
        TABLE = tableName;
    }
    
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    
    @Override
    public String getColumn(int columnIndex) {
        try {
            return poEntity.getMetaData().getColumnLabel(columnIndex);
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
        }
        return "";
    }

    @Override
    public int getColumn(String columnName) {
        try {
            return MiscUtil.getColumnIndex(poEntity, columnName);
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
        }
        return -1;
    }

    @Override
    public int getColumnCount() {
        try {
            return poEntity.getMetaData().getColumnCount();
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
        }

        return -1;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public String getTable() {
        return TABLE;
    }

    @Override
    public Object getValue(int columnIndex) {
        try {
            return poEntity.getObject(columnIndex);
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public Object getValue(String columnName) {
        try {
            return poEntity.getObject(MiscUtil.getColumnIndex(poEntity, columnName));
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public JSONObject setValue(int columnIndex, Object value) {
        try {
            poJSON = MiscUtil.validateColumnValue(System.getProperty("sys.default.path.metadata") + XML, MiscUtil.getColumnLabel(poEntity, columnIndex), value);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poEntity.updateObject(columnIndex, value);
            poEntity.updateRow();

            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put("value", getValue(columnIndex));
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    @Override
    public JSONObject setValue(String colunmName, Object value) {
        poJSON = new JSONObject();

        try {
            return setValue(MiscUtil.getColumnIndex(poEntity, colunmName), value);
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }

    @Override
    public JSONObject newRecord() {
        poJSON = new JSONObject();
        
        setValue(ID, getNextCode());

        pnEditMode = EditMode.ADDNEW;
        poJSON.put("result", "success");
        
        return poJSON;
    }

    @Override
    public JSONObject openRecord(String id) {
        poJSON = new JSONObject();

        String lsSQL = MiscUtil.makeSelect(this);

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, ID + " = " + SQLUtil.toSQL(id));

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            if (loRS.next()) {
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
                    setValue(lnCtr, loRS.getObject(lnCtr));
                }
                
                MiscUtil.close(loRS);

                pnEditMode = EditMode.READY;

                poJSON = new JSONObject();
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                poJSON = new JSONObject();
                poJSON.put("result", "error");
                poJSON.put("message", "No record to load.");
            }
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }
    
    @Override
    public JSONObject updateRecord() {
        poJSON = new JSONObject();
        
        if (pnEditMode != EditMode.READY){
            poJSON.put("result", "error");
            poJSON.put("message", "No record was loaded.");
        } else {
            pnEditMode = EditMode.UPDATE;
            poJSON.put("result", "success");
        }
        
        return poJSON;
    }

    @Override
    public JSONObject saveRecord() {
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL;
            
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setValue(ID, getNextCode());

                lsSQL = MiscUtil.makeSQL(this);

                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                        poJSON = new JSONObject();
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON = new JSONObject();
                        poJSON.put("result", "error");
                        poJSON.put("message", poGRider.getErrMsg());
                    }
                } else {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "No record to save.");
                }
            } else {                
                Model loOldEntity = new Model();
                loOldEntity.setApplicationDriver(poGRider);
                loOldEntity.setXML(XML);
                loOldEntity.setTableName(TABLE);
                loOldEntity.initialize();
                
                //replace with the primary key column info
                poJSON = loOldEntity.openRecord((String) this.getValue(ID));

                if ("success".equals((String) poJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, ID + " = " + SQLUtil.toSQL(this.getValue(ID)));

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                            poJSON = new JSONObject();
                            poJSON.put("result", "success");
                            poJSON.put("message", "Record saved successfully.");
                        } else {
                            poJSON = new JSONObject();
                            poJSON.put("result", "error");
                            poJSON.put("message", poGRider.getErrMsg());
                        }
                    } else {
                        poJSON = new JSONObject();
                        poJSON.put("result", "success");
                        poJSON.put("message", "No updates has been made.");
                    }
                } else {
                    poJSON = new JSONObject();
                    poJSON.put("result", "error");
                    poJSON.put("message", "Record discrepancy. Unable to save record.");
                }
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid update mode. Unable to save record.");
            return poJSON;
        }
        
        if (((String) poJSON.get("result")).equals("success")) openRecord((String) getValue(ID));

        return poJSON;
    }

    @Override
    public void list() {
        Method[] methods = this.getClass().getMethods();

        System.out.println("--------------------------------------------------------------------");
        System.out.println("LIST OF PUBLIC METHODS FOR " + this.getClass().getName() + ":");
        System.out.println("--------------------------------------------------------------------");
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }
    
    public String getNextCode(){
        return MiscUtil.getNextCode(getTable(), ID, false, poGRider.getConnection(), ""); 
    }
}
