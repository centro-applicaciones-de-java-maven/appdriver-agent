package org.guanzon.appdriver.agent;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.UserLockState;
import org.guanzon.appdriver.constant.UserState;
import org.guanzon.appdriver.constant.UserType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ShowDialogFX {
    /**
     * Browse records or transactions.
     * 
     * @param applicationDriver - Application Driver
     * @param resultset - ResultSet
     * @param columnHeaders - Column Headers separated by »
     * @param columnLabels - Column Labels separated by »
     * @return result as success/failed and other information
     */
    public static JSONObject Browse(GRider applicationDriver,
                                        ResultSet resultset,
                                        String columnHeaders,
                                        String columnLabels){
       
        JSONObject loJSON = new JSONObject();
        long lnRow = MiscUtil.RecordCount(resultset);
        
        if (lnRow == 1)
            return CommonUtils.loadJSON(resultset);
        else{
            if (MiscUtil.RecordCount(resultset) >= 1){
                try {
                    resultset.first();
                    
                    QuickSearch loSearch = new QuickSearch();
                    loSearch.setGRider(applicationDriver);
                    loSearch.setResultSet(resultset);
                    loSearch.setSQLSource("");
                    loSearch.setConditionValue("");
                    loSearch.setColumnHeader(columnHeaders);
                    loSearch.setColunmName(columnLabels);
                    loSearch.setColumnCriteria("");
                    loSearch.setColumnIndex(-1);

                    CommonUtils.showModal(loSearch);
                    return loSearch.getJSON();
                } catch (Exception ex) {
                    loJSON.put("result", "error");
                    loJSON.put("message", ex.getMessage());
                    return loJSON;
                }
            }         
        }
        
        loJSON.put("result", "error");
        loJSON.put("message", "Search has been cancelled or no record selected.");
        return loJSON;
    }
    
    /**
     * Browse records or transactions.
     * 
     * @param jsonResult - Array of values in JSONArray format.
     * @param columnHeaders - Column Headers separated by »
     * @param columnLabels - Column Names separated by »
     * @return result as success/failed and other information
     */
    public static JSONObject Browse(JSONArray jsonResult,
                                    String columnHeaders,
                                    String columnLabels){    
                                        
        JSONObject loJSON = new JSONObject();
        
        try {
            JSONBrowse instance = new JSONBrowse();
        
            instance.setData(jsonResult);
            instance.setColumnHeader(columnHeaders);
            instance.setColunmName(columnLabels);
            
            CommonUtils.showModal(instance);
            return instance.getJSON();
        } catch (Exception ex) {
            loJSON.put("result", "error");
            loJSON.put("message", ex.getMessage());
            return loJSON;
        }
    }
    
    /**
     * Search records or transactions.
     * 
     * @param applicationDriver - Application Driver
     * @param sqlStatement - SQL Statement
     * @param conditionValue - Condition Value
     * @param columnHeaders - Column Headers separated by »
     * @param columnLabels - Column Labels separated by »
     * @param columnCriterias - Column Criteria separated by »
     * @param sortIndex - Column Index used for sorting results
     * @return result as success/failed and other information
     */
    public static JSONObject Search(GRider applicationDriver,
                                        String sqlStatement,
                                        String conditionValue,
                                        String columnHeaders,
                                        String columnLabels,
                                        String columnCriterias,
                                        int sortIndex){
        
        JSONObject loJSON = new JSONObject();
        
        String [] laSplit = columnCriterias.split("»");
        String lsCondition;
                
        lsCondition = laSplit[sortIndex] + " LIKE " + SQLUtil.toSQL(conditionValue + "%");
        sqlStatement = MiscUtil.addCondition(sqlStatement, lsCondition);
        
        try {
            ResultSet loRS = applicationDriver.executeQuery(sqlStatement);
                
            if (MiscUtil.RecordCount(loRS) == 1) return CommonUtils.loadJSON(loRS);

            if (MiscUtil.RecordCount(loRS) > 1){
                loRS.first();

                QuickSearch loSearch = new QuickSearch();
                loSearch.setGRider(applicationDriver);
                loSearch.setResultSet(loRS);
                loSearch.setSQLSource(sqlStatement);
                loSearch.setConditionValue(conditionValue);
                loSearch.setColumnHeader(columnHeaders);
                loSearch.setColunmName(columnLabels);
                loSearch.setColumnCriteria(columnCriterias);
                loSearch.setColumnIndex(sortIndex);

                CommonUtils.showModal(loSearch);
                return loSearch.getJSON();   
            }
        } catch (SQLException ex) {
            loJSON.put("result", "error");
            loJSON.put("message", ex.getMessage());
            return loJSON;
        } catch (Exception ex) {
            loJSON.put("result", "error");
            loJSON.put("message", ex.getMessage());
            return loJSON;
        }
        
        loJSON.put("result", "error");
        loJSON.put("message", "Search has been cancelled or no record selected.");
        return loJSON;
    }
    
    /**
     * System Approval
     * 
     * @param applicationDriver Application Driver
     * @return result as success/failed and other information
     */
    public static JSONObject getUserApproval(GRider applicationDriver){
        JSONObject loJSON = new JSONObject();
        String [] arr = new String [2];
        
        try {
            
            SysApprovalFX instance = new SysApprovalFX();
            CommonUtils.showModal(instance);

            arr[0] = instance.getUsername();
            arr[1] = instance.getPassword();

            if (arr[0].isEmpty() || arr[1].isEmpty()) {
                loJSON.put("result", "error");
                loJSON.put("message", "Username or password is not set.");
                return loJSON;
            }

            arr[0] = applicationDriver.Encrypt(arr[0]);
            arr[1] = applicationDriver.Encrypt(arr[1]);

            String lsSQL = "SELECT * FROM xxxSysUser" + 
                            " WHERE sLogNamex = " + SQLUtil.toSQL(arr[0]) +
                                " AND sPassword = " + SQLUtil.toSQL(arr[1]) +
                                " AND (cUserType = '1' OR sProdctID = " + 
                                        SQLUtil.toSQL(applicationDriver.getProductID()) + ")";

            ResultSet loRS = applicationDriver.executeQuery(lsSQL);

            String lsUserIDxx;
            String lsEmployID;
            int lnRights;
            
            if (!loRS.next()){
                loJSON.put("result", "error");
                loJSON.put("message", "Verify your log name and/or password");
                return loJSON;
            }
            
            lsUserIDxx = loRS.getString("sUserIDxx");
            lsEmployID = loRS.getString("sEmployNo");
            lnRights = loRS.getInt("nUserLevl");
            
            if (loRS.getString("cUserStat").equals(UserState.SUSPENDED)){
                loJSON.put("result", "error");
                loJSON.put("message", "User has no Rights for Procedure Approval!!!");
                return loJSON;
            }
            
            if (loRS.getString("cLockStat").equals(UserLockState.LOCKED)){
                loJSON.put("result", "error");
                loJSON.put("message", "User has no Rights for Procedure Approval!!!");
                return loJSON;
            }
            
            if (loRS.getString("cUserType").equals(UserType.LOCAL)){
                if (!loRS.getString("sProdctID").equalsIgnoreCase(applicationDriver.getProductID())){
                    loJSON.put("result", "error");
                    loJSON.put("message", "User has no Rights for Procedure Approval!!!");
                    return loJSON;
                }
            }
            
            loJSON.put("result", "success");          
            loJSON.put("sUserIDxx", (String) lsUserIDxx);
            loJSON.put("sEmployID", (String) lsEmployID);
            loJSON.put("nUserLevl", (int) lnRights);
            return loJSON;
        } catch (Exception ex) {
            loJSON.put("result", "error");
            loJSON.put("message", ex.getMessage());
            return loJSON;
        }
    }
    
    /**
     * System Approval
     * 
     * @param applicationDriver Application Driver
     * @param stage FX Stage
     * @return result as success/failed and other information
     */
    public static JSONObject getUserApproval(GRider applicationDriver, 
                                                Stage stage){
        JSONObject loJSON = new JSONObject();
        String [] arr = new String [2];
        
        try {
            
            SysApprovalFX instance = new SysApprovalFX();
            CommonUtils.showModal(instance);

            arr[0] = instance.getUsername();
            arr[1] = instance.getPassword();

            if (arr[0].isEmpty() || arr[1].isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Username or password is not set.");
                return loJSON;
            }

            arr[0] = applicationDriver.Encrypt(arr[0]);
            arr[1] = applicationDriver.Encrypt(arr[1]);

            String lsSQL = "SELECT * FROM xxxSysUser" + 
                            " WHERE sLogNamex = " + SQLUtil.toSQL(arr[0]) +
                                " AND sPassword = " + SQLUtil.toSQL(arr[1]) +
                                " AND (cUserType = '1' OR sProdctID = " + 
                                        SQLUtil.toSQL(applicationDriver.getProductID()) + ")";

            ResultSet loRS = applicationDriver.executeQuery(lsSQL);

            String lsUserIDxx;
            String lsEmployID;
            int lnRights;
            
            if (!loRS.next()){
                loJSON.put("result", "error");
                loJSON.put("message", "Verify your log name and/or password");
                return loJSON;
            }
            
            lsUserIDxx = loRS.getString("sUserIDxx");
            lsEmployID = loRS.getString("sEmployNo");
            lnRights = loRS.getInt("nUserLevl");
            
            if (loRS.getString("cUserStat").equals(UserState.SUSPENDED)){
                loJSON.put("result", "error");
                loJSON.put("message", "User has no Rights for Procedure Approval!!!");
                return loJSON;
            }
            
            if (loRS.getString("cLockStat").equals(UserLockState.LOCKED)){
                loJSON.put("result", "error");
                loJSON.put("message", "User has no Rights for Procedure Approval!!!");
                return loJSON;
            }
            
            if (loRS.getString("cUserType").equals(UserType.LOCAL)){
                if (!loRS.getString("sProdctID").equalsIgnoreCase(applicationDriver.getProductID())){
                    loJSON.put("result", "error");
                    loJSON.put("message", "User has no Rights for Procedure Approval!!!");
                    return loJSON;
                }
            }
            
            loJSON.put("result", "success");
            loJSON.put("sUserIDxx", (String) lsUserIDxx);
            loJSON.put("sEmployID", (String) lsEmployID);
            loJSON.put("nUserLevl", (int) lnRights);
            return loJSON;
        } catch (Exception ex) {
            loJSON.put("result", "error");
            loJSON.put("message", ex.getMessage());
            return loJSON;
        }
    }
}