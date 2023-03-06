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
    private  final String _http="http";
    private Context _context;


    public  int Id;
    public  int Pocet;
    public String errorMsg;

    public  JSONObject respData;
    public  boolean reqFinished;

    OkHttpClient client;
    MediaType MEDIA_TYPE_JSON;


    public boolean isClosed() {
        return false;
    }

    public void Init(String imei, String guid, String apiServer, Context context)
    {
        _imei=imei;
        _guid=guid;
        _apiServer=apiServer;
        _context=context;

        client = new OkHttpClient();
        MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    }

    public String SP_MOBILNI_TERMINAL_toJSON(int retType, String typAkce, String pStr, int pInt, double pDec, String pStr2, int pInt2, int pInt3, double pDec2) {
        return "{" +
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
                errorMsg = e.getMessage();
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

    public boolean TestConnection() throws Exception {
        boolean ret = false;
        errorMsg = "";
        try {
            reqFinished = false;
            respData = null;
            getRequest("info");
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

    /*
    void getRequest(String cmd) throws IOException {
        String[] param=new String[1];
        param[0]=_http+"://"+_apiServer+"/"+cmd;
        new HTTPGetReqTask().execute(param);
    }

    void postRequest(String postData) throws IOException {
        String[] param=new String[2];
        param[0]=_http+"://"+_apiServer+"/data";
        param[1]=postData;
        new HTTPPostReqTask().execute(param);
    }

    private class HTTPGetReqTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            JSONObject respObj=null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.length()>0) respObj = new JSONObject(sb.toString());
                respData = respObj;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reqFinished=true;
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return respObj;
        }

        @Override
        protected void onPostExecute(JSONObject result)
        {
            super.onPostExecute(result);
        }
    }

    private class HTTPPostReqTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject respObj=null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(params[1]);
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.length()>0) respObj = new JSONObject(sb.toString());
                respData = respObj;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reqFinished=true;
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return respObj;
        }

        @Override
        protected void onPostExecute(JSONObject result)
        {
            super.onPostExecute(result);
            respData = result;
        }
    }
    */


}

