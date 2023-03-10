package com.example.wspersonalshopper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

public class BasketActivity extends BaseActivity  {

    private TextView tvNazev;
    private  TextView tvCelkem;

    private ArrayList<C_Item> basketItems;
    private C_Item aktItem;
    private ListView lvItems;
    private ItemsAdapter adapter;

    private boolean queryInProgress;
    private DataBridge db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        //
        SharedPreferences preferencesBase = getApplicationContext().getSharedPreferences(PreferConst.SHARED_PREFS, MODE_PRIVATE);
        String guid = preferencesBase.getString(PreferConst.GUID, "");
        String androidID = preferencesBase.getString(PreferConst.ANDROID_ID, "");
        String server = preferencesBase.getString(PreferConst.SERVER, "");
        int terminalId = preferencesBase.getInt(PreferConst.TERMINAL_ID, 0);
        boolean presApi = preferencesBase.getBoolean(PreferConst.PRES_API, true);
        //
        db=new DataBridge(androidID,guid,server,presApi, this);
        //
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basketItems.size()>0) {
                    Messages.ShowQuestion(BasketActivity.this, "Upozornění", "Košík není prázdný\nOpravdu ukončit?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, null);
                }
                else finish();
            }
        });

        ImageButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basketItems.size()>0) {
                    Messages.ShowQuestion(BasketActivity.this, "Upozornění", "Opravdu vyprázdnit košík ?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            basketItems.clear();
                            adapter.notifyDataSetChanged();
                            tvCelkem.setText("0.00");
                        }
                    }, null);
                }
            }
        });

        tvNazev = findViewById(R.id.tvNazev);
        tvCelkem = findViewById(R.id.tvCelkem);
        lvItems = findViewById(R.id.lvItems);

        /*
        mDecorView = getWindow().getDecorView();
        hideSystemUI();
         */

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        basketItems = new ArrayList<>();

        adapter = new ItemsAdapter(this, basketItems);
        lvItems.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action_alternative));
        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(myBroadcastReceiver);
        } catch (Exception ex) {

        }
        super.onPause();
    }

    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, BasketActivity.class);
        context.startActivity(intent);
    }

    private void PrepoctiSoucet()
    {
        Double soucet = 0.0;
        for (C_Item i : basketItems) {
            soucet += i.Cena * i.Mnozstvi;
        }
        tvCelkem.setText(Utils.dfCena.format(soucet));
    }

    public void QuantityFragmentDialog_QuantityOK(Integer quantity) {
        aktItem.Mnozstvi = quantity;
        adapter.notifyDataSetChanged();
        PrepoctiSoucet();
    }

    // ***********************************************************************************
    public class ItemsAdapter extends ArrayAdapter<C_Item> {

        public ItemsAdapter(Context context, ArrayList<C_Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            C_Item item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.basket_item_layout, parent, false);
            }
            TextView tvName = convertView.findViewById(R.id.tvItemNazev);
            TextView tvCena = convertView.findViewById(R.id.tvItemCena);
            ImageButton btnItemShow=convertView.findViewById(R.id.btnItemShow);
            Button btnItemPlus=convertView.findViewById(R.id.btnItemPlus);
            Button btnItemMinus=convertView.findViewById(R.id.btnItemMinus);
            ImageButton btnItemDel=convertView.findViewById(R.id.btnItemDel);
            TextView tvItemMnoz = convertView.findViewById(R.id.tvItemMnoz);
            //
            tvName.setText(Utils.df.format(item.Mnozstvi)+" "+item.Nazev);
            tvCena.setText(Utils.dfCena.format(item.Cena));
            tvItemMnoz.setText(Utils.df.format(item.Mnozstvi));
            //
            if (item.ShowEditMnoz)
            {
                btnItemDel.setVisibility(View.VISIBLE);
                btnItemPlus.setVisibility(View.VISIBLE);
                btnItemMinus.setVisibility(View.VISIBLE);
                tvItemMnoz.setVisibility(View.VISIBLE);
            }
            else {
                btnItemDel.setVisibility(View.GONE);
                btnItemPlus.setVisibility(View.GONE);
                btnItemMinus.setVisibility(View.GONE);
                tvItemMnoz.setVisibility(View.GONE);
            }

            btnItemShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.ShowEditMnoz=!item.ShowEditMnoz;
                    ((BaseAdapter) lvItems.getAdapter()).notifyDataSetChanged();
                }
            });

            btnItemPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.Mnozstvi += 1;
                    tvName.setText(Utils.df.format(item.Mnozstvi) + " " + item.Nazev);
                    tvItemMnoz.setText(Utils.df.format(item.Mnozstvi));
                    PrepoctiSoucet();
                }
            });

            btnItemPlus.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    aktItem=item;
                    FragmentManager fm = getSupportFragmentManager();
                    QuantityFragmentDialog  quantityFragmentDialog = QuantityFragmentDialog.newInstance(10);
                    quantityFragmentDialog.setCancelable(false);
                    quantityFragmentDialog.show(fm, "QuantityFragmentDialog");
                    return false;
                }
            });

            btnItemMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.Mnozstvi>0) {
                        item.Mnozstvi -= 1;
                        tvName.setText(Utils.df.format(item.Mnozstvi) + " " + item.Nazev);
                        tvItemMnoz.setText(Utils.df.format(item.Mnozstvi));
                        PrepoctiSoucet();
                    }
                }
            });

            btnItemMinus.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    aktItem=item;
                    FragmentManager fm = getSupportFragmentManager();
                    QuantityFragmentDialog  quantityFragmentDialog = QuantityFragmentDialog.newInstance(10);
                    quantityFragmentDialog.setCancelable(false);
                    quantityFragmentDialog.show(fm, "QuantityFragmentDialog");
                    return false;
                }
            });

            btnItemDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    basketItems.remove(item);
                    adapter.notifyDataSetChanged();
                    PrepoctiSoucet();
                }
            });

            return convertView;
        }
    }

    private void Zapis(C_Item item)
    {


    }

    // ********************************************************************************************
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(getResources().getString(R.string.activity_intent_filter_action)) || action.equals(getResources().getString(R.string.activity_intent_filter_action_alternative))) {
                String barcode = "";
                //  Received a barcode scan
                String decodedData = intent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
                final String scanResult_1 = intent.getStringExtra("SCAN_BARCODE1");
                final String scanResult_2 = intent.getStringExtra("SCAN_BARCODE2");
                final int barcodeType = intent.getIntExtra("SCAN_BARCODE_TYPE", -1);
                final String scanStatus = intent.getStringExtra("SCAN_STATE");

                if ("ok".equals(scanStatus)) {
                    barcode = scanResult_1;
                } else {
                    barcode = decodedData;
                }
                if (barcode.isEmpty()) {
                    barcode = "";
                }
                if (!barcode.matches("")) {
                    for (C_Item i : basketItems) {
                        i.ShowEditMnoz = false;
                    }
                    HledejEan(barcode);
                }
            }
        }
    };

    private void HledejEan(String barcode) {
        if (!queryInProgress ) {
            new HledejEanAsync().execute(barcode);
        } else {
            MediaPlayer mp = MediaPlayer.create(BasketActivity.this, R.raw.beep_01a);
            mp.start();
        }
    }

    private class HledejEanAsync extends AsyncTask<String, Void, C_Info> {

        final C_Info info = new C_Info();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            queryInProgress = true;
        }

        @Override
        protected C_Info doInBackground(String... params) {
            String ean = params[0];
            try {
                if (!db.isConnected()) {
                    info.ErrorMsg = "chyba v API propojeni";
                    info.ConnectErr = true;
                } else {
                    //KOD_ZBOZI,NAZEV_ZBOZI,ID_VELIKOSTI,NAZEV_VELIKOSTI,ID_BARVY,NAZEV_BARVY,ID_DELKY,NAZEV_DELKY, ID_ROZMERU, NAZEV_ROZMERU, PC, STAV, STAV_KS, POZICE_TEXT,ZBOZI_ID
                    // VYPRODEJ, ZAKAZ_OBJEDNAVANI, NEAKTIVNI_POLOZKA, ID_DODAVATEL,NC_POSLEDNI, TEMA_ID, SKUPINA_ID
                    double mnozVaha=0;
                    if (ean.length()==13 && (ean.substring(0,2).equals("28") || ean.substring(0,2).equals("29")) ) {
                        mnozVaha = Double.parseDouble(ean.substring(6, 12))/1000;
                        ean = ean.substring(0, 6) + "0000000";
                    }
                    db.SetQuery_MOBILNI_TERMINAL(0, "EAN", ean, 0, 0, "", 0, 0, 0);
                    if (db.ExecQuery()) {
                        try {
                            if (db.hasRow) {
                                info.item.Ean = ean;
                                info.item.Kod = db.getString("KOD_ZBOZI");
                                info.item.Nazev = db.getString("NAZEV_ZBOZI");
                                info.item.VelikostId = db.getInt("ID_VELIKOSTI");
                                info.item.VelikostNazev = db.getString("NAZEV_VELIKOSTI");
                                info.item.BarvaId = db.getInt("ID_BARVY");
                                info.item.BarvaNazev = db.getString("NAZEV_BARVY");
                                info.item.DelkaId = db.getInt("ID_DELKY");
                                info.item.DelkaNazev = db.getString("NAZEV_DELKY");
                                info.item.RozmerId = db.getInt("ID_ROZMERU");
                                info.item.RozmerNazev = db.getString("NAZEV_ROZMERU");
                                //info.item.Cena = rs.getDouble(11);
                                info.item.StavStr = db.getString("STAV");
                                info.item.Stav = db.getDouble("STAV_KS");
                                info.item.LokacePozn = db.getString("POZICE_TEXT");
                                info.item.ZboziId = db.getInt("ZBOZI_ID");
                                info.item.Cena = db.getDouble("PC");
                                info.item.Vyprodej = db.getBoolean("VYPRODEJ");
                                info.item.ZakazObjednani = db.getBoolean("ZAKAZ_OBJEDNAVANI");
                                info.item.Naktivni = db.getBoolean("NEAKTIVNI_POLOZKA");
                                info.item.DodavatelId = db.getInt("ID_DODAVATEL");
                                info.item.NcPosledni = db.getDouble("NC_POSLEDNI");
                                info.item.TemaId = db.getInt("TEMA_ID");
                                info.item.SkupinaId = db.getInt("SKUPINA_ID");
                                info.item.Mnozstvi = 1;
                                info.item.MnozVaha = mnozVaha;
                                info.Nasel = true;
                            }
                        } catch (Exception e) {
                            info.ErrorMsg = e.getMessage();
                        }
                    } else
                        info.ErrorMsg = db.ErrorMsg;
                    db.CloseQuery();
                }
            } catch (Exception ex) {
                info.ErrorMsg = ex.getMessage();
            }
            return info;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(C_Info result) {
            super.onPostExecute(result);
            String msg = "";
            //
            if (info.Nasel) {
                if (info.item.MnozVaha > 0) info.item.Mnozstvi = info.item.MnozVaha;
                else info.item.Mnozstvi = 1;
                //
                boolean nasel = false;
                for (C_Item i : basketItems) {
                    if (i.Ean.equals(info.item.Ean)) {
                        nasel = true;
                        i.Mnozstvi = i.Mnozstvi + info.item.Mnozstvi;
                        i.ShowEditMnoz = true;
                        break;
                    }
                }
                if (!nasel) {
                    C_Item nItem = new C_Item(info.item);
                    nItem.ShowEditMnoz = true;
                    basketItems.add(nItem);
                }
                PrepoctiSoucet();
                ((BaseAdapter) lvItems.getAdapter()).notifyDataSetChanged();
                queryInProgress=false;
            } else {
                MediaPlayer mp = MediaPlayer.create(BasketActivity.this, R.raw.beep_01a);
                mp.start();
                if (info.ConnectErr) {
                    Reconnect();
                } else {
                    if (info.ErrorMsg.matches("")) msg = "Neznámý čárový kód";
                    else msg = "Chyba pří hledání\n" + info.ErrorMsg;
                    Messages.ShowRedAlert(BasketActivity.this, "Chyba", msg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            queryInProgress = false;
                        }
                    });
                }
            }
        }

        private void Reconnect() {
            AlertDialog.Builder alert = new AlertDialog.Builder(BasketActivity.this);
            alert.setTitle("Chyba");
            alert.setMessage("Bylo přerušeno spojení\nZkontrolujte WiFi");
            alert.setIcon(R.drawable.msg_varovani);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (db.Reconnect()) queryInProgress = false;
                    else Reconnect();
                }
            });
            alert.setNegativeButton("Ukončit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    CloseActivity();
                }
            });
            alert.show();
        }

        private void CloseActivity() {
            finishAffinity();
            finish();
        }


    }


}
