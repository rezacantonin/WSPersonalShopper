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


}
