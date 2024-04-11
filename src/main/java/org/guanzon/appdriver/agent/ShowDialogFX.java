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
     * @param foGRider - Application Driver
     * @param foRS - ResultSet
     * @param fsHeader - Column Headers separated by »
     * @param fsColName - Column Names separated by »
     * @return result as success/failed and other information
     */
    public static JSONObject Browse(GRider foGRider,
                                        ResultSet foRS,
                                        String fsHeader,
                                        String fsColName){
       
        long lnRow = MiscUtil.RecordCount(foRS);
        
        if (lnRow == 1)
            return CommonUtils.loadJSON(foRS);
        else{
            if (MiscUtil.RecordCount(foRS) >= 1){
                try {
                    foRS.first();
                    
                    QuickSearch loSearch = new QuickSearch();
                    loSearch.setGRider(foGRider);
                    loSearch.setResultSet(foRS);
                    loSearch.setSQLSource("");
                    loSearch.setConditionValue("");
                    loSearch.setColumnHeader(fsHeader);
                    loSearch.setColunmName(fsColName);
                    loSearch.setColumnCriteria("");
                    loSearch.setColumnIndex(-1);

                    CommonUtils.showModal(loSearch);
                    return loSearch.getJSON();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }         
        }
        
        JSONObject loJSON = new JSONObject();
        loJSON.put("result", "error");
        loJSON.put("message", "Search has been cancelled or no record selected.");
        
        return loJSON;
    }
    
    /**
     * Browse records or transactions.
     * 
     * @param foJSON - Array of values in JSONArray format.
     * @param fsHeader - Column Headers separated by »
     * @param fsColName - Column Names separated by »
     * @return result as success/failed and other information
     */
    public static JSONObject Browse(JSONArray foJSON,
                                        String fsHeader,
                                        String fsColName){    
                                        
        try {
            JSONBrowse instance = new JSONBrowse();
        
            instance.setData(foJSON);
            instance.setColumnHeader(fsHeader);
            instance.setColunmName(fsColName);
            
            CommonUtils.showModal(instance);
            return instance.getJSON();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        JSONObject loJSON = new JSONObject();
        loJSON.put("result", "error");
        loJSON.put("message", "Search has been cancelled or no record selected.");
        
        return loJSON;
    }
    
    /**
     * Search records or transactions.
     * 
     * @param foGRider - Application Driver
     * @param fsSQL - SQL Query used for searching
     * @param fsValue - SQL Condition
     * @param fsHeader - Column Headers separated by »
     * @param fsColName - Column Names separated by »
     * @param fsColCrit - Column Criteria separated by »
     * @param fnSort - Column Index used for sorting results
     * @return result as success/failed and other information
     */
    public static JSONObject Search(GRider foGRider,
                                        String fsSQL,
                                        String fsValue,
                                        String fsHeader,
                                        String fsColName,
                                        String fsColCrit,
                                        int fnSort){
        
        String [] laSplit = fsColCrit.split("»");
        String lsCondition;
                
        lsCondition = laSplit[fnSort] + " LIKE " + SQLUtil.toSQL(fsValue + "%");
        fsSQL = MiscUtil.addCondition(fsSQL, lsCondition);
        
        try {
            ResultSet loRS = foGRider.executeQuery(fsSQL);
                
            if (MiscUtil.RecordCount(loRS) == 1) return CommonUtils.loadJSON(loRS);

            if (MiscUtil.RecordCount(loRS) > 1){
                loRS.first();

                QuickSearch loSearch = new QuickSearch();
                loSearch.setGRider(foGRider);
                loSearch.setResultSet(loRS);
                loSearch.setSQLSource(fsSQL);
                loSearch.setConditionValue(fsValue);
                loSearch.setColumnHeader(fsHeader);
                loSearch.setColunmName(fsColName);
                loSearch.setColumnCriteria(fsColCrit);
                loSearch.setColumnIndex(fnSort);

                CommonUtils.showModal(loSearch);
                return loSearch.getJSON();   
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            
        JSONObject loJSON = new JSONObject();
        loJSON.put("result", "error");
        loJSON.put("message", "Search has been cancelled or no record selected.");
        
        return loJSON;
    }
    
    /**
     * System Approval
     * 
     * @param foGRider Application Driver
     * @return result as success/failed and other information
     */
    public static JSONObject getUserApproval(GRider foGRider){
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

            arr[0] = foGRider.Encrypt(arr[0]);
            arr[1] = foGRider.Encrypt(arr[1]);

            String lsSQL = "SELECT * FROM xxxSysUser" + 
                            " WHERE sLogNamex = " + SQLUtil.toSQL(arr[0]) +
                                " AND sPassword = " + SQLUtil.toSQL(arr[1]) +
                                " AND (cUserType = '1' OR sProdctID = " + 
                                        SQLUtil.toSQL(foGRider.getProductID()) + ")";

            ResultSet loRS = foGRider.executeQuery(lsSQL);

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
                if (!loRS.getString("sProdctID").equalsIgnoreCase(foGRider.getProductID())){
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
            ex.printStackTrace();
            loJSON.put("result", "error");
            loJSON.put("message", ex.getMessage());
            return loJSON;
        }
    }
    
    /**
     * System Approval
     * 
     * @param foGRider Application Driver
     * @param foStage FX Stage
     * @return result as success/failed and other information
     */
    public static JSONObject getUserApproval(GRider foGRider, Stage foStage){
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

            arr[0] = foGRider.Encrypt(arr[0]);
            arr[1] = foGRider.Encrypt(arr[1]);

            String lsSQL = "SELECT * FROM xxxSysUser" + 
                            " WHERE sLogNamex = " + SQLUtil.toSQL(arr[0]) +
                                " AND sPassword = " + SQLUtil.toSQL(arr[1]) +
                                " AND (cUserType = '1' OR sProdctID = " + 
                                        SQLUtil.toSQL(foGRider.getProductID()) + ")";

            ResultSet loRS = foGRider.executeQuery(lsSQL);

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
                if (!loRS.getString("sProdctID").equalsIgnoreCase(foGRider.getProductID())){
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
            ex.printStackTrace();
            loJSON.put("result", "error");
            loJSON.put("message", ex.getMessage());
            return loJSON;
        }
    }
}