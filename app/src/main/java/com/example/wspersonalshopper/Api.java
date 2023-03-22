package com.example.wspersonalshopper;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {

    private String _imei;
    private String _guid;
    private String _apiServer;
    private String _sqlServer;
    private String _database;
    private  String _http="https";
    private Context _context;


    public  int Id;
    public  int Pocet;
    public String errorMsg;
    public boolean isConnected;

    public  JSONObject respData;
    public  boolean reqFinished;

    OkHttpClient client;
    MediaType MEDIA_TYPE_JSON;

    public void Init(String imei, String guid, String apiServer, boolean ssl, String sqlServer, String database, Context context) {
        _imei = imei;
        _guid = guid;
        _apiServer = apiServer;
        _sqlServer = sqlServer;
        _database = database;
        _context = context;

        if (ssl) {
            _http = "https";
            /*
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (Exception e) {
                e.printStackTrace();
            }

            OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
            newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            newBuilder.hostnameVerifier((hostname, session) -> true);

            client = newBuilder.build();

             */
            client = new OkHttpClient();
        } else {
            _http = "http";
            client = new OkHttpClient();
        }

        MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        try {
            isConnected = TestConnection();
        } catch (Exception ex) {
            errorMsg = ex.getMessage();
        }
    }

    public String SP_MOBILNI_TERMINAL_toJSON(int retType, String typAkce, String pStr, int pInt, double pDec, String pStr2, int pInt2, int pInt3, double pDec2) {
        return "{" +
                "  \"sqlServer\": \""+_sqlServer+"\"," +
                "  \"database\": \""+_database+"\"," +
                "  \"name\": \"usr_MOBILNI_TERMINAL\"," +
                "  \"params\": [" +
                "    { \"name\": \"@IMEI\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + _imei + "\"}," +
                "    { \"name\": \"@GUID\",\"dataType\": \"char\",\"size\":40,\"value\": \"" + _guid + "\"}," +
                "    { \"name\": \"@TYP_AKCE\",\"dataType\": \"char\",\"size\":255,\"value\": \"" + typAkce + "\"}," +
                "    { \"name\": \"@P_STR\",\"dataType\": \"char\",\"size\":255,\"value\": \"" + pStr + "\"}," +
                "    { \"name\": \"@P_INT\",\"dataType\": \"int\",\"value\": \"" + pInt + "\"}," +
                "    { \"name\": \"@P_DEC\",\"dataType\": \"dec\",\"value\": \"" + pDec + "\"}," +
                "    { \"name\": \"@P_STR_2\",\"dataType\": \"char\",\"size\":255,\"value\": \"" + pStr2 + "\"}," +
                "    { \"name\": \"@P_INT_2\",\"dataType\": \"int\",\"value\": \"" + pInt2 + "\"}," +
                "    { \"name\": \"@P_INT_3\",\"dataType\": \"int\",\"value\": \"" + pInt3 + "\"}," +
                "    { \"name\": \"@P_DEC_2\",\"dataType\": \"dec\",\"value\": \"" + pDec2 + "\"}" +
                "  ]," +
                "  \"retType\": " + retType + "" +
                "}";
    }

    public String SP_MOBILNI_TERMINAL_ETI_toJSON(int retType, String typAkce, String pStr, int pInt) {
        return "{" +
                "  \"sqlServer\": \""+_sqlServer+"\"," +
                "  \"database\": \""+_database+"\"," +
                "  \"name\": \"usr_MOBILNI_TERMINAL_ETI\"," +
                "  \"params\": [" +
                "    { \"name\": \"@IMEI\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + _imei + "\"}," +
                "    { \"name\": \"@GUID\",\"dataType\": \"char\",\"size\":40,\"value\": \"" + _guid + "\"}," +
                "    { \"name\": \"@TYP_AKCE\",\"dataType\": \"char\",\"size\":255,\"value\": \"" + typAkce + "\"}," +
                "    { \"name\": \"@P_STR\",\"dataType\": \"char\",\"size\":255,\"value\": \"" + pStr + "\"}," +
                "    { \"name\": \"@P_INT\",\"dataType\": \"int\",\"value\": \"" + pInt + "\"}" +
                "  ]," +
                "  \"retType\": " + retType + "" +
                "}";
    }

    public String SP_MOBILNI_LOGIN_toJSON(int retType, int typ, String heslo, String nazev, String poznamka, String telefon, String email, int skladId, String jazyk) {
        return "{" +
                "  \"sqlServer\": \""+_sqlServer+"\"," +
                "  \"database\": \""+_database+"\"," +
                "  \"name\": \"usr_MOBILNI_LOGIN\"," +
                "  \"params\": [" +
                "    { \"name\": \"@TYP\",\"dataType\": \"int\",\"value\": \"" + typ + "\"}," +
                "    { \"name\": \"@UUID\", \"dataType\": \"char\", \"size\":80, \"value\": \"" + _imei + "\"}," +
                "    { \"name\": \"@HESLO\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + heslo + "\"}," +
                "    { \"name\": \"@NAZEV\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + nazev + "\"}," +
                "    { \"name\": \"@POZNAMKA\", \"dataType\": \"char\", \"size\":4096, \"value\": \"" + poznamka + "\"}," +
                "    { \"name\": \"@TELEFON\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + telefon + "\"}," +
                "    { \"name\": \"@E_MAIL\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + email + "\"}," +
                "    { \"name\": \"@SKLAD_ID\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + skladId + "\"}," +
                "    { \"name\": \"@GUID\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + _guid + "\"}," +
                "    { \"name\": \"@JAZYK\", \"dataType\": \"char\", \"size\":255, \"value\": \"" + jazyk + "\"}" +
                "  ]," +
                "  \"retType\": " + retType + "" +
                "}";
    }

    void postRequest(String postBody) throws IOException {
        Request request = new Request.Builder()
                .url(_http+"://"+_apiServer+"/data")
                .post(RequestBody.create(postBody, MEDIA_TYPE_JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                reqFinished=true;
                errorMsg="Chyba spojení";
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.code() == 404) errorMsg = response.message();
                        else if (response.code() == 400) errorMsg = response.body().string();
                        else errorMsg = "Unexpected code " + response.code();
                    } else {
                        String resBody = response.body().string();
                        if (!resBody.equals("")) respData = new JSONObject(resBody);
                    }
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                }
                finally {
                    response.close();
                    client.connectionPool().evictAll();
                }
                reqFinished = true;
            }
        });
    }

    void getRequest(String cmd) throws IOException {
        Request request = new Request.Builder()
                .url(_http+"://"+_apiServer+"/"+cmd)
                .get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                reqFinished=true;
                errorMsg = "Chyba spojení";
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        if (response.code() == 404) errorMsg = response.message();
                        else if (response.code() == 400) errorMsg = response.body().string();
                        else errorMsg = "Unexpected code " + response.code();
                    } else {
                        String resBody = response.body().string();
                        if (!resBody.equals("")) respData = new JSONObject(resBody);
                    }
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                }
                finally {
                    response.close();
                    client.connectionPool().evictAll();
                }
                reqFinished = true;
            }
        });
    }



    private JSONObject QueryProc(String body) {
        errorMsg = "";
        JSONObject ret = null;
        try {
            reqFinished=false;
            respData=null;
            postRequest(body);
            long timeout = new Date().getTime();
            timeout+=4000;
            do {
                long now = new Date().getTime();
                if (now>timeout) {
                    errorMsg ="timeout";
                    break;
                }
            } while (!reqFinished);
            ret=respData;
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        return ret;
    }

    public JSONObject Query(String body, boolean throwExc) throws Exception {
        JSONObject ret = null;
        errorMsg = "";
        JSONObject obj = QueryProc(body);
        if (obj != null) {
            try {
                ret = obj.getJSONObject("data");
            } catch (JSONException e) {
                errorMsg = e.getMessage();
            }
        }
        if (!errorMsg.equals("") && throwExc) throw new Exception(errorMsg);
        return ret;
    }

    public JSONArray QueryArr(String body, boolean throwExc) throws Exception {
        JSONArray ret = null;
        errorMsg = "";
        JSONObject obj = QueryProc(body);
        if (obj != null) {
            try {
                ret = obj.getJSONArray("dataArr");
            } catch (JSONException e) {
                errorMsg = e.getMessage();
            }
        }
        if (!errorMsg.equals("") && throwExc) throw new Exception(errorMsg);
        return ret;
    }

    public boolean ExecIUD(String body, boolean throwExc) throws Exception {
        boolean ret = false;
        errorMsg = "";
        Id = 0;
        JSONObject obj = QueryProc(body);
        if (obj != null) {
            try {
                JSONObject data = obj.getJSONObject("data");
                Pocet = data.getInt("VLOZENO");
                if (data.has("HLAVICKA_ID")) Id = data.getInt("HLAVICKA_ID");
                if (data.has("RADEK_ID")) Id = data.getInt("RADEK_ID");
                if (Pocet > 0) ret = true;
            } catch (JSONException e) {
                errorMsg = e.getMessage();
            }
        }
        if (!errorMsg.equals("") && throwExc) throw new Exception(errorMsg);
        return ret;
    }

    private boolean TestConnection() throws Exception {
        boolean ret = false;
        errorMsg = "";
        try {
            reqFinished = false;
            respData = null;
            getRequest("info/"+_sqlServer+";"+_database);
            //
            long timeout = new Date().getTime();
            timeout += 4000;
            do {
                long now = new Date().getTime();
                if (now > timeout) {
                    errorMsg = "timeout";
                    break;
                }
            } while (!reqFinished);
        } catch (Exception e) {
            errorMsg = e.getMessage();
        }
        if (respData != null) {
            try {
                if (respData.has("CONN")) {
                    ret = respData.getString("CONN").equals("OK");
                }
            } catch (JSONException e) {
                errorMsg = e.getMessage();
            }
        }
        if (!errorMsg.equals("")) throw new Exception(errorMsg);
        return ret;
    }

}

