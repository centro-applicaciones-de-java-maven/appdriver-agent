package org.guanzon.appdriver.agent.services;

import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.json.simple.JSONObject;

public class Parameter implements GRecord{
    protected LogWrapper logwrapr;
    
    protected GRider poGRider;
    protected boolean pbWthParent;
    protected String psRecdStat;

    protected JSONObject poJSON;
    
    public void setApplicationDriver(GRider applicationDriver){
        poGRider = applicationDriver;
    }
    
    public void setWithParentClass(boolean withParentClass){
        pbWthParent = withParentClass;
    }
    
    public void setLogWrapper(LogWrapper logWrapper){
        logwrapr = logWrapper;
    }
    
    public void initialize() {
    }

    @Override
    public void setRecordStatus(String recordStatus) {
        psRecdStat = recordStatus;
    }

    @Override
    public int getEditMode() {
        return getModel().getEditMode();
    }

    @Override
    public JSONObject newRecord() {        
        return getModel().newRecord();
    }

    @Override
    public JSONObject openRecord(String Id) {
        return getModel().openRecord(Id);
    }

    @Override
    public JSONObject updateRecord() {
        return getModel().updateRecord();
    }

    @Override
    public JSONObject saveRecord() {
        if (!pbWthParent) {
            poGRider.beginTrans();
        }

        poJSON = isEntryOkay();
        
        if (!"success".equals((String) poJSON.get("result"))) return poJSON;
        
        poJSON = getModel().saveRecord();

        if ("success".equals((String) poJSON.get("result"))) {
            if (!pbWthParent) {
                poGRider.commitTrans();
            }
        } else {
            if (!pbWthParent) {
                poGRider.rollbackTrans();
            }
        }

        return poJSON;
    }

    @Override
    public JSONObject deleteRecord() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JSONObject deactivateRecord() {
        poJSON = new JSONObject();
        
        if (getModel().getEditMode() != EditMode.READY ||
            getModel().getEditMode() != EditMode.UPDATE){
        
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }
        
        
        if (getModel().getEditMode() == EditMode.READY) {
            poJSON = updateRecord();
            if ("error".equals((String) poJSON.get("result"))) return poJSON;
        } 

        poJSON = getModel().setValue("cRecdStat", "0");

        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        return getModel().saveRecord();
    }

    @Override
    public JSONObject activateRecord() {
        poJSON = new JSONObject();
        
        if (getModel().getEditMode() != EditMode.READY ||
            getModel().getEditMode() != EditMode.UPDATE){
        
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }
        
        
        if (getModel().getEditMode() == EditMode.READY) {
            poJSON = updateRecord();
            if ("error".equals((String) poJSON.get("result"))) return poJSON;
        } 

        poJSON = getModel().setValue("cRecdStat", "1");
        
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        return getModel().saveRecord();
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) {
        return null;
    }

    @Override
    public Model getModel() {
        return null;
    }
    
    @Override
    public String getSQ_Browse(){
        String lsCondition = "";

        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }

            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }
        
        return MiscUtil.addCondition(MiscUtil.makeSelect(getModel()), lsCondition);
    }

    @Override
    public JSONObject isEntryOkay() {
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        
        return poJSON;
    }
}
