package com.example.wspersonalshopper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.WindowManager;

public class Messages {

    private static void ShowMsgProc(Context context, String title, String message, DialogInterface.OnClickListener onClickListener, int icon) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(icon);
        if (onClickListener==null)
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
        else
            dialog.setPositiveButton("Ok",onClickListener);
        dialog.show();
    }

    public static void ShowInfo(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        ShowMsgProc(context,title,message,onClickListener,R.drawable.msg_info);
    }

    public static void ShowWarning(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        ShowMsgProc(context,title,message,onClickListener,R.drawable.msg_varovani);
    }

    public static void ShowError(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        ShowMsgProc(context,title,message,onClickListener,R.drawable.msg_err);
    }

    public static void ShowQuestion(Context context, String title, String message, DialogInterface.OnClickListener onClickListenerOK, DialogInterface.OnClickListener onClickListenerESC) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(R.drawable.msg_dotaz);
        dialog.setPositiveButton("Ok", onClickListenerOK);
        dialog.setNegativeButton("Zruš", onClickListenerESC);
        dialog.show();
    }

    private static void ShowAlertProc(Context context, String title, String message, DialogInterface.OnClickListener onClickListener, boolean beep ) {
        if (beep) {
            MediaPlayer mp = MediaPlayer.create(context, R.raw.beep_01a);
            mp.start();
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        dialog.setTitle(title);
        dialog.setMessage(message);
        if (onClickListener==null)
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
        else
            dialog.setPositiveButton("Ok",onClickListener);
        dialog.setCancelable(false);
        dialog.show();
        /*
        AlertDialog dlg=dialog.show();
        dlg.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dlg.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        */



    }

    public static void ShowRedAlert(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        ShowAlertProc(context, title, message, onClickListener, false );
    }

    public static void ShowRedAlertBeep(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        ShowAlertProc(context, title, message, onClickListener, true );
    }

}
