package com.example.wspersonalshopper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.System.err;
import static java.lang.System.exit;

import java.util.Base64;
import java.util.Map;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private Button btnKiosk, btnBasket, btnCfg;
    private TextView tvStatus;
    private ImageView imgWS;
    private Spinner dropdown;

    public static Context appContext;
    Object[][] resultSet;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String server, database, guid, terminal_nazev, logoString, androidID, skladNazev, error_code;
    private boolean presApi;
    private int sklad, terminal_id;
    private DataBridge db;
    private boolean connectionOK;
    private String[] sklady;
    private Integer[] skladyID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext = getApplicationContext();
        dropdown = new Spinner(MainActivity.this);

        btnKiosk = findViewById(R.id.btnKiosk);
        btnBasket = findViewById(R.id.btnBasket);
        btnCfg = findViewById(R.id.btnCfg);
        tvStatus = findViewById(R.id.tvStatus);
        imgWS=findViewById(R.id.imgWinShop);

        setUpAdmin();
        updateButtonState();

        preferences = getApplicationContext().getSharedPreferences(PreferConst.SHARED_PREFS, MODE_PRIVATE);
        editor = preferences.edit();
/*
        editor.clear();
        editor.commit();
        exit(0);
*/
        androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        guid = "";
        if (!preferences.contains(PreferConst.ANDROID_ID)) {
            editor.putString(PreferConst.ANDROID_ID, androidID);
            editor.apply();
        }

        btnCfg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NastavPripojeni();
                return false;
            }
        });

        btnBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent basketIntent = new Intent(MainActivity.this, BasketActivity.class);
                basketIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(basketIntent);
                /*
                overridePendingTransition(0, 0);
                BasketActivity.startThisActivity(mContext);
                 */
            }
        });

        btnKiosk.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enableKioskMode(!WSPersonalShopperApp.isInLockMode());
                updateButtonState();
                return false;
            }
        });

        imgWS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        ProcesInicializace();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {

        } catch (Exception ex) {
            Log.e(TAG, "onPause.unregisterReceiver", ex);
        }
        super.onPause();
    }

    private void updateButtonState() {
        if (WSPersonalShopperApp.isInLockMode()) {
            btnKiosk.setText("D");
        } else {
            btnKiosk.setText("E");
        }
    }

    private void ProcesInicializace() {
        presApi = preferences.getBoolean(PreferConst.PRES_API, false);
        server = preferences.getString(PreferConst.SERVER, "");
        database = preferences.getString(PreferConst.DATABASE, "");
        terminal_id = preferences.getInt(PreferConst.TERMINAL_ID, 0);
        terminal_nazev = preferences.getString(PreferConst.TERMINAL_NAZEV, "");
        if (server.equals("")) NastavPripojeni();
        else Pripojeni();
    }

    private void NastavPripojeni() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nastavení připojení");

        final CheckBox inputApi = new CheckBox(this);
        final EditText inputServer = new EditText(this);
        final EditText inputDB = new EditText(this);
        final TextView inApi = new TextView(this);
        final TextView inServer = new TextView(this);
        final TextView inDB = new TextView(this);
        inApi.setText("Api");
        inServer.setText("Server");
        inDB.setText("Databáze");
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(inApi);
        ll.addView(inputApi);
        ll.addView(inServer);
        ll.addView(inputServer);
        ll.addView(inDB);
        ll.addView(inputDB);

        alert.setView(ll);

        alert.setCancelable(false);

        inputApi.setChecked(presApi);
        inputServer.setText(server);
        inputDB.setText(database);

        alert.setPositiveButton("Nastavit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                boolean edApi = inputApi.isChecked();
                String edServer = inputServer.getText().toString();
                String edDB = inputDB.getText().toString();

                if (edServer == "") {
                    Toast.makeText(MainActivity.this, "Server", Toast.LENGTH_SHORT);
                } else if (edDB == "") {
                    Toast.makeText(MainActivity.this, "Databáze", Toast.LENGTH_SHORT);
                } else {
                    presApi = edApi;
                    editor.putBoolean(PreferConst.PRES_API, presApi);
                    server = edServer;
                    editor.putString(PreferConst.SERVER, server);
                    database = edDB;
                    editor.putString(PreferConst.DATABASE, database);
                    editor.apply();
                    //
                    Pripojeni();
                }
            }
        });

        alert.setNegativeButton("Zavřít", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!preferences.contains(PreferConst.SERVER)) exit(0);
            }
        });

        alert.show();
    }

    private void Pripojeni() {
        btnBasket.setEnabled(false);
        int status = -1;
        String errMsg = "";
        db = new DataBridge(androidID, guid, server, presApi, this);
        try {
            if (db.isConnected()) connectionOK = true;
            else {
                errMsg = db.ErrorMsg;
                connectionOK = false;
            }
            if (connectionOK) {
                String logoStringShort;
                try {
                    db.SetQuery_MOBILNI_LOGIN(1, 2, androidID, "", "", "", "", 0, "");
                    if (db.ExecQuery()) {
                        status = db.getInt("VSTUP");

                        byte[] encodedBytes = new byte[0];
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            encodedBytes = Base64.getDecoder().decode(db.getString("LOGO"));
                        }
                        logoString=new String(encodedBytes);
                        logoString = "";
                    } else {
                        errMsg = db.ErrorMsg;
                    }
                    db.CloseQuery();
                } catch (Exception ex) {
                    errMsg = ex.getMessage();
                }
            }
        } catch (Exception ex) {
            errMsg = ex.getMessage();
        } finally {
            if (!errMsg.isEmpty()) {
                db.Close();
                Messages.ShowError(MainActivity.this, "Chyba", errMsg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvStatus.setText("Chyba při připojování");
                    }
                });
            }
        }
        //
        switch (status) {
            case 0: // neni registrace
                Registrace();
                break;
            case 1:
                // povoleni prihlaseni
                tvStatus.setText("");
                Prihlaseni();
                btnBasket.setEnabled(true);
                break;
            case 2: // cekani na dokonceni registrace
                tvStatus.setText("Toto zařízení nemá povolený přístup");
                break;
        }

        // logo
                /*
                if (logoString == null || logoString.equals("")) {
                    logoImg.setVisibility(View.INVISIBLE);
                } else {
                    if (logoString.substring(0, 2).toLowerCase() == "0x") {
                        logoStringShort = logoString.substring(2);
                    } else {
                        logoStringShort = logoString;
                    }
                    byte[] logoData = hexStringToByteArray(logoString);
                    Bitmap logo = BitmapFactory.decodeByteArray(logoData, 0, logoData.length);
                    if (logo == null) {
                        logoImg.setVisibility(View.INVISIBLE);
                    } else {
                        logoImg.setVisibility(View.VISIBLE);
                        logoImg.setImageBitmap(logo);
                    }
                }
                */
        //
    }

    private void Registrace() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Registrace zařízení");
        LinearLayout ll = new LinearLayout(MainActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText inputNazev = new EditText(this);
        final TextView inNazev = new TextView(this);
        inNazev.setText("Název terminálu");
        ll.addView(inNazev);
        ll.addView(inputNazev);
        alert.setView(ll);
        alert.setCancelable(false);

        alert.setPositiveButton("Nastavit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String edNazev = inputNazev.getText().toString();
                if (edNazev == "") {
                    Toast.makeText(MainActivity.this, "Server", Toast.LENGTH_SHORT);
                } else
                    terminal_nazev = edNazev;
                editor.putString(PreferConst.TERMINAL_NAZEV, terminal_nazev);
                editor.apply();

                if (connectionOK)
                {
                    try {
                        int status = 0;
                        db.SetQuery_MOBILNI_LOGIN(1, 3, "1234", terminal_nazev, "", "", "", 0, "CS");
                        if (db.ExecQuery()) {
                            status = db.getInt("REGISTRACE");
                        }
                        switch (status) {
                            case 0:
                                Toast.makeText(MainActivity.this, "Zařízení již bylo zaregistrováno", Toast.LENGTH_SHORT);
                                Prihlaseni();
                            case 1:
                                Messages.ShowInfo(MainActivity.this, "Registrace", "Registrace byla dokončena.\nVyčkejte na potvrzení", null);
                        }
                        db.CloseQuery();
                    } catch (Exception ex) {
                        Messages.ShowError(MainActivity.this, "Registrace", ex.getMessage(), null);
                    }
                }
            }
        });

        alert.setNegativeButton("Zavřít", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    private void Prihlaseni() {
        if (connectionOK) {
            int pass = 0;
            int terminal_id = 0;
            String terminal_nazev = "";
            int skladId = 0;
            try {
                db.SetQuery_MOBILNI_LOGIN(1, 1, "1234", "", "", "", "", 0, "");
                if (db.ExecQuery()) {
                    pass = db.getInt("PASS");
                    guid = db.getString("guid");
                    skladId = db.getInt("SKLAD_ID");
                    terminal_id = db.getInt("id");
                    terminal_nazev = db.getString("NAZEV");
                } else Messages.ShowError(MainActivity.this, "Přihlášení", db.ErrorMsg, null);
                db.CloseQuery();
            } catch (Exception ex) {
                Messages.ShowError(MainActivity.this, "Přihlášení", ex.getMessage(), null);
            }
            switch (pass) {
                case 0:
                    //Spatne heslo
                    break;
                case 1:
                    //zarizeni nalezeno. povoleno prihlaseni
                    tvStatus.setText("");

                    editor.putString(PreferConst.GUID, guid);
                    editor.putInt(PreferConst.TERMINAL_ID, terminal_id);
                    editor.putString(PreferConst.TERMINAL_NAZEV, terminal_nazev);
                    editor.apply();

                    db.ReInit(androidID, guid, server, presApi, this);

                    if (skladId == 0) NastaveniSkladu();

                    break;
                default:
                    //nemelo by se dit, just-in-case reseni
                    tvStatus.setText("Chyba v ověření");
                    break;
            }
        }
    }

    private void NastaveniSkladu() {
        String errMsg = "";
        if (connectionOK) {
            try {
                db.SetQuery_MOBILNI_LOGIN(10, 4, "", "", "", "", "", 0, "");
                if (db.ExecQueryArr()) {
                    if (db.hasRow) {
                        int rowCount = 1;
                        while (db.nextRow()) {
                            rowCount++;
                        }
                        //
                        sklady = new String[rowCount];
                        skladyID = new Integer[rowCount];
                        db.SetQuery_MOBILNI_LOGIN(10, 4, "", "", "", "", "", 0, "");
                        if (db.ExecQueryArr()) {
                            if (db.hasRow) {
                                int counter = 0;
                                do {
                                    skladyID[counter] = db.getInt("ID_SKLADU");
                                    sklady[counter] = db.getString("NAZEV_SKLADU");
                                    counter++;
                                } while (db.nextRow());
                            }
                        }
                        db.CloseQuery();
                    }
                } else {
                    Messages.ShowError(MainActivity.this, "Výběr skladu", db.ErrorMsg, null);
                    sklady = new String[0];
                    skladyID = new Integer[0];
                }
                db.CloseQuery();
            } catch (Exception ex) {
                Messages.ShowError(MainActivity.this, "Výběr skladu", ex.getMessage(), null);
            }
            if (sklady.length > 0) {
                ArrayAdapter<String> adapter_b = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line, sklady);

                adapter_b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter_b);
                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parentView,
                                               View selectedItemView, int position, long id) {
                        sklad = skladyID[dropdown.getSelectedItemPosition()];
                        skladNazev = sklady[dropdown.getSelectedItemPosition()];
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {// do nothing
                        sklad = skladyID[dropdown.getSelectedItemPosition()];
                        skladNazev = sklady[dropdown.getSelectedItemPosition()];
                    }

                });
                AlertDialog.Builder alert_sklad = new AlertDialog.Builder(MainActivity.this);

                alert_sklad.setTitle("Výběr skladu");

                LinearLayout ll_b = new LinearLayout(MainActivity.this);
                ll_b.setOrientation(LinearLayout.VERTICAL);
                ll_b.addView(dropdown);
                alert_sklad.setView(ll_b);
                alert_sklad.setCancelable(false);

                alert_sklad.setPositiveButton("Nastavit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putInt(PreferConst.SKLAD, sklad);
                        editor.putString(PreferConst.SKLAD_NAZEV, skladNazev);
                        editor.apply();
                        try {
                            int status = 0;
                            db.SetQuery_MOBILNI_LOGIN(0, 5, "", "", "", "", "", sklad, "");
                            if (db.ExecQuery()) {
                                if (db.hasRow) {
                                    status = db.getInt("VLOZENO");
                                } else status = 1;
                            } else
                                Messages.ShowError(MainActivity.this, "Přihlášení", db.ErrorMsg, null);
                            db.CloseQuery();
                            if (status == 1) {

                            }
                        } catch (Exception ex) {
                            String msg = ex.getMessage();
                        }
                    }
                });

                alert_sklad.setNegativeButton("Zavřít", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent failsafeIntent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(failsafeIntent);
                    }
                });
                alert_sklad.show();
            }
        }
    }

    public static Context getContextOfApplication() {
        return appContext;
    }


}

