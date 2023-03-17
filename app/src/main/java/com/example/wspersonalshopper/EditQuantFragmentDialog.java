package com.example.wspersonalshopper;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class EditQuantFragmentDialog extends DialogFragment {

    private static  final  String TAG="EditQuantFragmentDialog";
    private static final String ARG_MAXQ = "ARG_MAXQ";
    private static final String ARG_CURRQ = "ARG_CURRQ";

    private int maxQ, currQ;

    public EditQuantFragmentDialog() {
        // Required empty public constructor
    }


    public static EditQuantFragmentDialog newInstance(Integer _maxQ, Integer _currQ) {
        EditQuantFragmentDialog fragment = new EditQuantFragmentDialog();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_quant_fragment_dialog, container, false);

        Button btnOK = view.findViewById(R.id.btnEditQuantFragOK);
        Button btnZpet = view.findViewById(R.id.btnEditQuantFragZpet);
        EditText edEditQuantFragMnozstvi = view.findViewById(R.id.edEditQuantFragMnozstvi);
        edEditQuantFragMnozstvi.setHint(String.valueOf( currQ));


        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        /*
        InputMethodManager im = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        boolean res= im.showSoftInput(edEditQuantFragMnozstvi, InputMethodManager.SHOW_IMPLICIT);
        */

        edEditQuantFragMnozstvi.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_ENTER)
                {
                    Integer quantity = Integer.parseInt(edEditQuantFragMnozstvi.getText().toString());
                    if (quantity <= maxQ) {
                        ((BasketActivity) getActivity()).QuantityFragmentDialog_QuantityOK(quantity);
                        getDialog().dismiss();
                    } else
                        Toast.makeText(getActivity(), "Max. množství je " + maxQ, Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer quantity = Integer.parseInt(edEditQuantFragMnozstvi.getText().toString());
                if (quantity <= maxQ) {
                    ((BasketActivity) getActivity()).QuantityFragmentDialog_QuantityOK(quantity);
                    getDialog().dismiss();
                } else
                    Toast.makeText(getContext(), "Max. množství je " + maxQ, Toast.LENGTH_LONG).show();
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
}