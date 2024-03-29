package com.example.wspersonalshopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBridge {

    private boolean presApi, presApiSsl, winShopStd;
    private String apiServer, sqlServer, database;

    private String guid;
    private String androidID;

    private int jsonArrIdx;
    private JSONArray jsonArr;
    private JSONObject json;

    private Api api;
    private Connection connect;
    private     ConnectionClass connectClass;
    private Statement stmt = null;
    private ResultSet rs = null;

    private String query;
    private  int retType;

    public String ErrorMsg;
    public  boolean hasRow;
    private  Context context;

    public DataBridge(Context _context)
    {
        SharedPreferences preferences = _context.getSharedPreferences(PreferConst.SHARED_PREFS, _context.MODE_PRIVATE);
        androidID = preferences.getString(PreferConst.ANDROID_ID, "");
        guid = preferences.getString(PreferConst.GUID, "");
        apiServer = preferences.getString(PreferConst.API_SERVER, "");
        presApi = preferences.getBoolean(PreferConst.PRES_API, false);
        presApiSsl = preferences.getBoolean(PreferConst.PRES_API_SSL, false);
        sqlServer = preferences.getString(PreferConst.SQL_SERVER, "");
        database = preferences.getString(PreferConst.DATABASE, "");
        winShopStd = preferences.getBoolean(PreferConst.WS_STD, false);
        context=_context;
        //
        ErrorMsg="";
        if (presApi)
        {
            api=new Api();
            api.Init(androidID, guid, apiServer, presApiSsl, sqlServer, database, _context);
        }
        else
        {
            connectClass = new ConnectionClass();
            connect = connectClass.CONN(context);
        }
    }

    public void ReInit( Context _context) {
        SharedPreferences preferences = _context.getSharedPreferences(PreferConst.SHARED_PREFS, _context.MODE_PRIVATE);
        guid = preferences.getString(PreferConst.GUID, "");
        androidID = preferences.getString(PreferConst.ANDROID_ID, "");
        sqlServer = preferences.getString(PreferConst.SQL_SERVER, "");
        database = preferences.getString(PreferConst.DATABASE, "");
        apiServer = preferences.getString(PreferConst.API_SERVER, "");
        presApi = preferences.getBoolean(PreferConst.PRES_API, false);
        presApiSsl = preferences.getBoolean(PreferConst.PRES_API_SSL, false);
        winShopStd = preferences.getBoolean(PreferConst.WS_STD, false);
        if (presApi) {
            api.Init(androidID, guid, apiServer, presApiSsl, sqlServer, database, _context);
        }
    }

    public boolean isConnected() {
        boolean res = false;
        try {
            if (presApi) {
                if (api != null) {
                    if (api.isConnected) res = true;
                    else ErrorMsg = api.errorMsg;
                }
            }
            else
                res = connect != null && !connect.isClosed();
        } catch (Exception e) {
            ErrorMsg = e.getMessage();
        }
        return res;
    }

    public void SetQuery_MOBILNI_TERMINAL(int _retType, String typAkce, String pStr, int pInt, double pDec, String pStr2, int pInt2, int pInt3, double pDec2, String pStr3) {
        retType = _retType;
        if (presApi)
            if (winShopStd)
                query = api.SP_MOBILNI_TERMINAL_toJSON_Std(_retType, typAkce, pStr, pInt, pDec, pStr2, pInt2, pInt2, pDec2, pStr3);
            else
                query = api.SP_MOBILNI_TERMINAL_toJSON(_retType, typAkce, pStr, pInt, pDec, pStr2, pInt2, pInt2, pDec2);
        else {
            if (winShopStd)
                query = "exec usr_MOBILNI_TERMINAL '" + androidID + "','" + guid + "','" + typAkce + "','" + pStr + "'," + pInt + "," + pDec + ",'" + pStr2 + "'," + pInt2 + "," + pInt3 + "," + pDec2+",'"+pStr3+"'";
            else
                query = "exec usr_MOBILNI_TERMINAL '" + androidID + "','" + guid + "','" + typAkce + "','" + pStr + "'," + pInt + "," + pDec + ",'" + pStr2 + "'," + pInt2 + "," + pInt3 + "," + pDec2;
        }
    }

    public void SetQuery_MOBILNI_LOGIN(int _retType, int typ, String heslo, String nazev, String poznamka, String telefon, String email, int skladId, String jazyk) {
        retType = _retType;
        if (presApi)
            query = api.SP_MOBILNI_LOGIN_toJSON(retType, typ, heslo, nazev, poznamka, telefon, email, skladId, jazyk);
        else
            query = "exec usr_MOBILNI_LOGIN " + typ + ",'" + androidID + "','" + heslo + "','" + nazev + "','" + poznamka + "','" + telefon + "','" + email + "'," + skladId + ",'" + guid + "','" + jazyk + "'";
    }

    public boolean ExecQuery() {
        boolean res = false;
        hasRow=false;
        try {
            if (presApi) {
                JSONObject data = api.Query(query, true);
                if (data != null) {
                    try {
                        json = data;
                        hasRow=true;
                        res = true;
                    } catch (Exception e) {
                        ErrorMsg = e.getMessage();
                    }
                } else {
                    if (retType == 0) res = true;
                    else ErrorMsg="";
                }
            } else {
                for (int i=1; i<=2; i++) {
                    stmt = null;
                    rs = null;
                    try {
                        stmt = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            res = true;
                            hasRow = true;
                        } else {
                            if (retType == 0) res = true;
                            else ErrorMsg = "";
                        }
                        break;
                    } catch (SQLException ex) {
                        if (i==1 && (!isConnected() || ErrorMsg.indexOf("Software caused connection abort") > 0 )) {
                            Utils.CloseQuery(stmt, rs);
                            Reconnect();
                        }
                        else {
                            ErrorMsg = ex.getMessage();
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ErrorMsg = ex.getMessage();
        }
        return res;
    }

    public boolean ExecQueryArr() {   // pokud je res=true, je vzdy nacten prvni zaznam
        boolean res = false;
        hasRow=false;
        try {
            if (presApi) {
                JSONArray data = api.QueryArr(query, true);
                if (data != null) {
                    try {
                        jsonArr = data;
                        jsonArrIdx = 0;
                        if (jsonArr.length() > 0) {
                            json = jsonArr.getJSONObject(0);
                            res = true;
                            hasRow = true;
                        } else {
                            if (retType == 10) res = true;
                            else ErrorMsg = "Žádná data k dispozici";
                        }
                    } catch (Exception e) {
                        ErrorMsg = e.getMessage();
                    }
                }
            } else {
                for (int i=1; i<=2; i++) {
                    stmt = null;
                    rs = null;
                    try {
                        stmt = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            res = true;
                            hasRow = true;
                        } else {
                            if (retType == 10) res = true;
                            else ErrorMsg = "Žádná data k dispozici";
                        }
                        break;
                    } catch (SQLException ex) {
                        if (i==1 && (!isConnected() || ErrorMsg.indexOf("Software caused connection abort") > 0 )) {
                            Utils.CloseQuery(stmt, rs);
                            Reconnect();
                        }
                        else {
                            ErrorMsg = ex.getMessage();
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ErrorMsg = ex.getMessage();
        }
        return res;
    }

    public void CloseQuery()
    {
        if (!presApi) Utils.CloseQuery(stmt, rs);
    }

    public boolean nextRow() throws Exception {
        try {
            if (presApi) {
                if (jsonArrIdx < jsonArr.length() - 1) {
                    jsonArrIdx++;
                    json = jsonArr.getJSONObject(jsonArrIdx);
                    return true;
                } else return false;
            } else
                return rs.next();
        }
        catch ( Exception ex) {
            throw new Exception(ex);
        }
    }

    public int getInt(String name) throws Exception {
        try {
            if (presApi)
                return json.getInt(name);
            else
                return rs.getInt(name);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public String getString(String name) throws Exception {
        try {
            if (presApi)
                return json.getString(name);
            else
                return rs.getString(name);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public Double getDouble(String name) throws Exception {
        try {
            if (presApi)
                return json.getDouble(name);
            else
                return rs.getDouble(name);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public Boolean getBoolean(String name) throws Exception {
        try {
            if (presApi)
                return json.getBoolean(name);
            else
                return rs.getBoolean(name);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public boolean Reconnect() {
        if (!presApi) connect = connectClass.CONN(context);
        return (isConnected());
    }

    public void Close()
    {
        try {
            if (connect != null && !connect.isClosed()) connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
