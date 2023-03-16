package com.example.wspersonalshopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

public class SettingFragmentDialog extends DialogFragment {

    private static  final  String TAG="SettingFragmentDialog";

    private static final String ARG_API = "API";
    private static final String ARG_API_SSL = "API_SSL";
    private static final String ARG_API_SERVER = "API_SERVER";
    private static final String ARG_SQL_SERVER = "SQL_SERVER";
    private static final String ARG_DATABASE = "DATABASE";

    private boolean api;
    private boolean apiSsl;
    private String apiServer;
    private String sqlServer;
    private String database;

    public SettingFragmentDialog() {
        // Required empty public constructor
    }

    public static SettingFragmentDialog newInstance(boolean _api, boolean _apiSsl, String _apiServer, String _sqlServer, String _database) {
        SettingFragmentDialog fragment = new SettingFragmentDialog();
        Bundle args = new Bundle();
        args.putBoolean(ARG_API, _api);
        args.putBoolean(ARG_API_SSL, _apiSsl);
        args.putString(ARG_API_SERVER, _apiServer);
        args.putString(ARG_SQL_SERVER, _sqlServer);
        args.putString(ARG_DATABASE, _database);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            api = getArguments().getBoolean(ARG_API);
            apiSsl = getArguments().getBoolean(ARG_API_SSL);
            apiServer = getArguments().getString(ARG_API_SERVER);
            sqlServer = getArguments().getString(ARG_SQL_SERVER);
            database = getArguments().getString(ARG_DATABASE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment_dialog, container, false);

        Button btnOK = view.findViewById(R.id.btnSettingFragOK);
        Button btnZpet = view.findViewById(R.id.btnSettingFragZpet);
        CheckBox chbApi= view.findViewById(R.id.chbSettingFragApi);
        CheckBox chbApiSsl= view.findViewById(R.id.chbSettingFragApiSsl);
        EditText edApiServer= view.findViewById(R.id.edSettingFragApiServer);
        EditText edSqlServer= view.findViewById(R.id.edSettingFragSqlServer);
        EditText edDatabase= view.findViewById(R.id.edSettingFragDatabase);

        chbApi.setChecked(api);
        chbApiSsl.setChecked(apiSsl);
        edApiServer.setText(apiServer);
        edSqlServer.setText(sqlServer);
        edDatabase.setText(database);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api = chbApi.isChecked();
                apiSsl = chbApiSsl.isChecked();
                apiServer = edApiServer.getText().toString();
                sqlServer = edSqlServer.getText().toString();
                database = edDatabase.getText().toString();
                ((MainActivity)getActivity()).SettingFragmentDialog_OK(api, apiSsl, apiServer, sqlServer, database);
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
}