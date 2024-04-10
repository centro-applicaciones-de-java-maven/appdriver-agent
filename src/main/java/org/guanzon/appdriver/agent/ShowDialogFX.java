package org.guanzon.appdriver.agent;

import java.sql.ResultSet;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ShowDialogFX {
    public static JSONObject Browse(GRider foGRider,
                                        ResultSet loRS,
                                        String fsHeader,
                                        String fsColName){
       
        long lnRow = MiscUtil.RecordCount(loRS);
        
        if (lnRow == 1)
            return CommonUtils.loadJSON(loRS);
        else{
            if (MiscUtil.RecordCount(loRS) >= 1){
                try {
                    loRS.first();
                    
                    QuickSearch loSearch = new QuickSearch();
                    loSearch.setGRider(foGRider);
                    loSearch.setResultSet(loRS);
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
        
        return null;
    }
    
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
        return null;
    }
}
