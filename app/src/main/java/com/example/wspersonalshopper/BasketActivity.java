package com.example.wspersonalshopper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class BasketActivity extends BaseActivity  {

    private TextView tvText;

    private class Item {
        public String nazev;
        public double cena;
        public Double mnoz;
        public int kod;
        public  boolean showEditMnoz;

    }

    private ArrayList<Item> items;
    private ListView lvItems;
    private ItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Delete", Toast.LENGTH_SHORT).show();
            }
        });

        tvText = findViewById(R.id.tvNazev);
        lvItems = findViewById(R.id.lvItems);

        mDecorView = getWindow().getDecorView();
        hideSystemUI();
        //setUpAdmin();

        items = new ArrayList<>();

        adapter = new ItemsAdapter(this, items);
        lvItems.setAdapter(adapter);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item=items.get(position);
                item.showEditMnoz=!item.showEditMnoz;
                ((BaseAdapter) lvItems.getAdapter()).notifyDataSetChanged();
            }
        });

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
                    for (Item i: items) {
                        i.showEditMnoz=false;
                    }
                    //
                    Item nItem=new Item();
                    nItem.nazev="Nejake zbozi dlouhy nazev";
                    nItem.cena=155.50;
                    nItem.mnoz= 2.0;
                    nItem.showEditMnoz=true;
                    items.add(nItem);
                    ((BaseAdapter) lvItems.getAdapter()).notifyDataSetChanged();
                    /*
                    switch (scanBarcodeAkce) {
                        case 1:
                            if (aktEditText == edEan) {
                                aktEditText.requestFocus();
                                HledejEan(barcode);
                            } else
                                aktEditText.setText(barcode);
                            break;
                    }
                    */
                }
            }
        }
    };

    // ***********************************************************************************
    // ***********************************************************************************
    public class ItemsAdapter extends ArrayAdapter<Item> {

        public ItemsAdapter(Context context, ArrayList<Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.basket_item_layout, parent, false);
            }
            TextView tvName = convertView.findViewById(R.id.tvItemNazev);
            TextView tvMnoz = convertView.findViewById(R.id.tvItemMnoz);
            TextView tvCena = convertView.findViewById(R.id.tvItemCena);
            Button btnX=convertView.findViewById(R.id.button);
            Button btnPlus=convertView.findViewById(R.id.btnPlus);
            Button btnMinus=convertView.findViewById(R.id.btnMinus);
            tvName.setText(String.valueOf(item.mnoz)+" "+item.nazev);
            tvCena.setText(String.valueOf(item.cena));
            tvMnoz.setText(String.valueOf(item.mnoz));
            //
            if (item.showEditMnoz)
            {
                btnPlus.setVisibility(View.VISIBLE);
                btnMinus.setVisibility(View.VISIBLE);
                tvMnoz.setVisibility(View.VISIBLE);
            }
            else {
                btnPlus.setVisibility(View.GONE);
                btnMinus.setVisibility(View.GONE);
                tvMnoz.setVisibility(View.GONE);
            }

            btnX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.showEditMnoz=!item.showEditMnoz;
                    ((BaseAdapter) lvItems.getAdapter()).notifyDataSetChanged();
                }
            });

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.mnoz+=1;
                    tvName.setText(String.valueOf(item.mnoz)+" "+item.nazev);
                    tvMnoz.setText(String.valueOf(item.mnoz));
                }
            });

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.mnoz-=1;
                    tvName.setText(String.valueOf(item.mnoz)+" "+item.nazev);
                    tvMnoz.setText(String.valueOf(item.mnoz));
                }
            });

            return convertView;
        }
    }


}
