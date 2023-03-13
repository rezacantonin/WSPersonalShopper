package com.example.wspersonalshopper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

public class QuantityFragmentDialog extends DialogFragment {

    private static  final  String TAG="QuantityFragmentDialog";
    private static final String ARG_MAXQ = "ARG_MAXQ";
    private static final String ARG_CURRQ = "ARG_CURRQ";

    private int maxQ, currQ;

    private ArrayList<Integer> data;
    private QuantityAdapter adapter;

    public QuantityFragmentDialog() {
        // Required empty public constructor
    }

    public static QuantityFragmentDialog newInstance(Integer _maxQ, Integer _currQ) {
        QuantityFragmentDialog fragment = new QuantityFragmentDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_MAXQ, _maxQ);
        args.putInt(ARG_CURRQ, _currQ);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maxQ = getArguments().getInt(ARG_MAXQ);
            currQ = getArguments().getInt(ARG_CURRQ);
            data = new ArrayList<>();
            for (int i = 1; i <= maxQ; i++) data.add(i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quantity_fragment_dialog, container, false);

        Button btnZpet = view.findViewById(R.id.btnQuantityFragZpet);
        ListView listView = view.findViewById(R.id.lvQuantityFrag);
        adapter = new QuantityAdapter(getActivity(), data);
        listView.setAdapter(adapter);
        listView.setSelection(currQ-1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Integer quantity=data.get(position);
                ((BasketActivity)getActivity()).QuantityFragmentDialog_QuantityOK(quantity);
                getDialog().dismiss();
            }
        });

        btnZpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    // ***********************************************************************************
    public class QuantityAdapter extends ArrayAdapter<Integer> {

        public QuantityAdapter(Context context, ArrayList<Integer> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Integer item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.quantity_fragment_item_layout, parent, false);
            }
            TextView tvNumber = convertView.findViewById(R.id.tvQuantityFragNumber);
            //
            tvNumber.setText(String.valueOf(item));

            return convertView;
        }
    }
}