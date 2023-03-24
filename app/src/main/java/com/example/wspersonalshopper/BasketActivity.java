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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private boolean winShopStd;
    private DataBridge db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        //
        SharedPreferences preferencesBase = getApplicationContext().getSharedPreferences(PreferConst.SHARED_PREFS, MODE_PRIVATE);
        int terminalId = preferencesBase.getInt(PreferConst.TERMINAL_ID, 0);
        winShopStd = preferencesBase.getBoolean(PreferConst.WS_STD, false);
        //
        db=new DataBridge( this);
        //
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basketItems.size()>0) {
                    Messages.ShowQuestion(BasketActivity.this, "Upozornění", "Košík není prázdný\nOpravdu ukončit?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            VymazVse();
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
                            if (VymazVse()) {
                                basketItems.clear();
                                adapter.notifyDataSetChanged();
                                tvCelkem.setText("0.00");
                            }
                        }
                    }, null);
                }
            }
        });

        ImageButton btnPayBasket = findViewById(R.id.btnPayBasket);
        btnPayBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basketItems.size()>0) {
                    Messages.ShowQuestion(BasketActivity.this, "Upozornění", "Opravdu košík uzavřít?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            PlatbaKosiku();
                        }
                    }, null);
                    //
                    //Heslo();
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

        VymazVse();

        basketItems = new ArrayList<>();
        tvNazev.setText("Košík "+terminalId);
        tvCelkem.setText("0.00");

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

    @Override
    public void onBackPressed() {
        if (basketItems.size()>0) {
            Messages.ShowQuestion(BasketActivity.this, "Upozornění", "Košík není prázdný\nOpravdu ukončit?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    VymazVse();
                    finish();
                }
            }, null);
        }
        else finish();
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
        if (Zapis(aktItem, quantity)) {
            aktItem.Mnozstvi = quantity;
            adapter.notifyDataSetChanged();
            PrepoctiSoucet();
        }
    }

    private boolean Zapis(C_Item _item, double _mnozstvi ) {
        boolean pomRes=false;
        if (winShopStd)
            db.SetQuery_MOBILNI_TERMINAL(1, "PS_VlozRadek", _item.Kod, _item.BarvaId, _mnozstvi, _item.VelikostNazev, 0, 0, 0);
        else
            db.SetQuery_MOBILNI_TERMINAL(1, "PS_VlozRadek", "", _item.ZboziId, _mnozstvi, "", 0, 0, 0);
        try {
            if (db.ExecQuery()) {
                boolean res = db.getInt("VLOZENO") != 0;
                if (!res)
                    Messages.ShowError(BasketActivity.this, "Chyba", "Nelze zapsat položku košíku", null);
                else {
                    pomRes=true;
                }
            } else
                Messages.ShowError(BasketActivity.this, "Chyba", "Nelze zapsat položku košíku", null);
            db.CloseQuery();
        } catch (Exception ex) {
            Messages.ShowError(BasketActivity.this, "Chyba", "Nelze zapsat položku košíku\n" + ex.getMessage(), null);
        }
        return  pomRes;
    }

    private boolean VymazVse( ) {
        boolean pomRes=false;
        db.SetQuery_MOBILNI_TERMINAL(1, "PS_VymazVse", "", 0, 0, "", 0, 0, 0);
        try {
            if (db.ExecQuery()) {
                boolean res = db.getInt("VLOZENO") != 0;
                if (!res)
                    Messages.ShowError(BasketActivity.this, "Chyba", "Nelze vymazat položky košíku", null);
                else {
                    pomRes=true;
                }
            } else
                Messages.ShowError(BasketActivity.this, "Chyba", "Nelze vymazat položky košíku", null);
            db.CloseQuery();
        } catch (Exception ex) {
            Messages.ShowError(BasketActivity.this, "Chyba", "Nelze zapsat vymazat položky\n" + ex.getMessage(), null);
        }
        return  pomRes;
    }

    private void PlatbaKosiku()
    {
        db.SetQuery_MOBILNI_TERMINAL(1, "PS_Uzavrit", "", 0, 0, "", 0, 0, 0);
        try {
            if (db.ExecQuery()) {
                boolean res = db.getInt("VLOZENO") != 0;
                if (!res)
                    Messages.ShowError(BasketActivity.this, "Chyba", "Košík nelze uzavřít", null);
                else {
                    basketItems.clear();
                    adapter.notifyDataSetChanged();
                    tvCelkem.setText("0.00");
                    finish();
                }
            } else
                Messages.ShowError(BasketActivity.this, "Chyba", "Košík nelze uzavřít", null);
            db.CloseQuery();
        } catch (Exception ex) {
            Messages.ShowError(BasketActivity.this, "Chyba", "Košík nelze uzavřít\n" + ex.getMessage(), null);
        }

    }

    private void Heslo()
    {
        final AlertDialog.Builder alertPw = new AlertDialog.Builder(BasketActivity.this);

        alertPw.setTitle("Platba košíku");
        alertPw.setMessage("Zadejte heslo");

        final EditText adminPasswordEdittext = new EditText(BasketActivity.this);
        final TextView adminPwTextView = new TextView(BasketActivity.this);

        adminPwTextView.setText("");
        adminPasswordEdittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        LinearLayout ll = new LinearLayout(BasketActivity.this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(adminPwTextView);
        ll.addView(adminPasswordEdittext);
        alertPw.setView(ll);
        alertPw.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String heslo = adminPasswordEdittext.getText().toString();
                if (heslo.matches("1596") ) {
                    PlatbaKosiku();
                }
            }
        });

        alertPw.setNegativeButton("Zpět", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alertPw.show();
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
            TextView tvBarvaVel = convertView.findViewById(R.id.tvItemBarvaVel);
            TextView tvCena = convertView.findViewById(R.id.tvItemCena);
            ImageButton btnItemShow=convertView.findViewById(R.id.btnItemShow);
            Button btnItemMnoz=convertView.findViewById(R.id.btnItemMnoz);
            ImageButton btnItemDel=convertView.findViewById(R.id.btnItemDel);
            //
            tvName.setText(item.Nazev);
            tvCena.setText(Utils.df.format(item.Mnozstvi)+" x "+ Utils.dfCena.format(item.Cena)+" = "+Utils.dfCena.format(item.Mnozstvi*item.Cena));
            if (item.BarvaNazev.isEmpty() && item.VelikostNazev.isEmpty()) tvBarvaVel.setVisibility(View.GONE);
            else {
                tvBarvaVel.setText(item.BarvaNazev + " " + item.VelikostNazev);
                tvBarvaVel.setVisibility(View.VISIBLE);
            }
            //
            if (item.ShowEditMnoz)
            {
                btnItemDel.setVisibility(View.VISIBLE);
                btnItemMnoz.setVisibility(View.VISIBLE);
            }
            else {
                btnItemDel.setVisibility(View.GONE);
                btnItemMnoz.setVisibility(View.GONE);
            }

            btnItemShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.ShowEditMnoz=!item.ShowEditMnoz;
                    ((BaseAdapter) lvItems.getAdapter()).notifyDataSetChanged();
                }
            });

            btnItemMnoz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aktItem=item;
                    FragmentManager fm = getSupportFragmentManager();
                    QuantityFragmentDialog  quantityFragmentDialog = QuantityFragmentDialog.newInstance((int)item.Stav, (int)item.Mnozstvi);
                    quantityFragmentDialog.setCancelable(false);
                    quantityFragmentDialog.show(fm, "QuantityFragmentDialog");
                }
            });

            btnItemMnoz.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    aktItem=item;
                    FragmentManager fm = getSupportFragmentManager();
                    EditQuantFragmentDialog  editQuantFragmentDialog = EditQuantFragmentDialog.newInstance((int)item.Stav, (int)item.Mnozstvi);
                    editQuantFragmentDialog.setCancelable(false);
                    editQuantFragmentDialog.show(fm, "EditQuantFragmentDialog");
                    return true;
                }
            });

            btnItemDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Zapis(item,0)) {
                        basketItems.remove(item);
                        adapter.notifyDataSetChanged();
                        PrepoctiSoucet();
                    }
                }
            });

            return convertView;
        }
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
                    double mnozVaha=0;
                    if (ean.length()==13 && (ean.substring(0,2).equals("28") || ean.substring(0,2).equals("29")) ) {
                        mnozVaha = Double.parseDouble(ean.substring(6, 12))/1000;
                        ean = ean.substring(0, 6) + "0000000";
                    }
                    db.SetQuery_MOBILNI_TERMINAL(0, "PS_EAN", ean, 0, 0, "", 0, 0, 0);
                    if (db.ExecQuery()) {
                        try {
                            if (db.hasRow) {
                                info.item.Ean = ean;
                                info.item.Kod = db.getString("KOD_ZBOZI").trim();
                                info.item.Nazev = db.getString("NAZEV_ZBOZI").trim();
                                info.item.VelikostId = db.getInt("ID_VELIKOSTI");
                                info.item.VelikostNazev = db.getString("NAZEV_VELIKOSTI").trim();
                                info.item.BarvaId = db.getInt("ID_BARVY");
                                info.item.BarvaNazev = db.getString("NAZEV_BARVY").trim();
                                info.item.DelkaId = db.getInt("ID_DELKY");
                                info.item.DelkaNazev = db.getString("NAZEV_DELKY").trim();
                                info.item.RozmerId = db.getInt("ID_ROZMERU");
                                info.item.RozmerNazev = db.getString("NAZEV_ROZMERU").trim();
                                //info.item.Cena = rs.getDouble(11);
                                info.item.Stav = db.getDouble("STAV_KS") - db.getDouble("MNOZ_ZASOBNIK");;
                                info.item.ZboziId = db.getInt("ZBOZI_ID");
                                info.item.Cena = db.getDouble("PC");
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
                if (info.item.Stav>0) {
                    if (info.item.MnozVaha > 0) info.item.Mnozstvi = info.item.MnozVaha;
                    else info.item.Mnozstvi = 1;
                    //
                    boolean nasel = false;
                    for (C_Item i : basketItems) {
                        if (i.ZboziId==info.item.ZboziId && i.Kod.equals(info.item.Kod)
                           && i.VelikostId==info.item.VelikostId && i.VelikostNazev.equals(info.item.VelikostNazev)
                           && i.BarvaId==info.item.BarvaId && i.BarvaNazev.equals(info.item.BarvaNazev)) {
                            nasel = true;
                            i.ShowEditMnoz = true;
                            if (info.item.Stav>=i.Mnozstvi + info.item.Mnozstvi) {
                                if (Zapis(i, i.Mnozstvi + info.item.Mnozstvi)) {
                                    i.Mnozstvi = i.Mnozstvi + info.item.Mnozstvi;
                                }
                            }
                            else {
                                Messages.ShowWarning(BasketActivity.this, "Upozornění", "Dosaženo max. množství", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        queryInProgress = false;
                                    }
                                });
                            }
                            break;
                        }
                    }
                    if (!nasel) {
                        if (Zapis(info.item,info.item.Mnozstvi )) {
                            C_Item nItem = new C_Item(info.item);
                            nItem.ShowEditMnoz = true;
                            basketItems.add(nItem);
                        }
                    }
                    PrepoctiSoucet();
                    ((BaseAdapter) lvItems.getAdapter()).notifyDataSetChanged();
                    queryInProgress=false;
                }
                else {
                    Messages.ShowWarning(BasketActivity.this, "Upozornění", "Zboží je vyprodáno", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            queryInProgress = false;
                        }
                    });
                }
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
