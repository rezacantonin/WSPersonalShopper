package com.example.wspersonalshopper;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

public class Utils {
    private static  final  String TAG="Utils";

    public static final DecimalFormat df= new DecimalFormat("###.###");
    public static final DecimalFormat dfCena= new DecimalFormat("##0.00");

    public static void  CloseQuery(Statement stmt, ResultSet rs)
    {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            rs = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ex) {
            }
            stmt = null;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }



}
