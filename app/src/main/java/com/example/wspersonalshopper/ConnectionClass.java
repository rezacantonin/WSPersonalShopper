package com.example.wspersonalshopper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConnectionClass extends MainActivity {
    String server;
    String database;

    String username = "Mobil_app";
    String password = "m0b-rePorty&stat";

    String userpassword;

    SharedPreferences prefs;

    Context applicationContext = MainActivity.getContextOfApplication();

    String z="";

    @SuppressLint("NewApi")
    public Connection CONN() {
        prefs = applicationContext.getSharedPreferences(PreferConst.SHARED_PREFS, MODE_PRIVATE);
        server = prefs.getString(PreferConst.SERVER, "");
        database = prefs.getString(PreferConst.DATABASE, "");
        server = server.replace("\\","&");
        String[] serverArray = server.split("&&", 2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL;
        Integer columnCount = 0;
        Integer rowNumber = 0;
         z = "start" ;
        DriverManager.setLoginTimeout(60);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            z = "getting connection";

                if ( serverArray.length == 1 ){
                    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + server + "/" + database+ ";user=" + username + ";password=" + password + ";");

                }else{
                    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + serverArray[0] + "/" + database+ ";instance=" + serverArray[1]  + ";user=" + username + ";password=" + password + ";");

                }
            z = "got connection";

        } catch (Exception e) {
            e.printStackTrace();
            z = e.getMessage();
            if (z.contains("Network error IOException: failed to connect to")||(z.contains("connect failed: ECONNREFUSED (Connection refused)"))){

            }else{

            }
            System.out.println(resultSet);
            z = "the end";
        }
        return conn;
    }

}
