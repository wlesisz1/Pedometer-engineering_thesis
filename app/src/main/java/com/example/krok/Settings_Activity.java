package com.example.krok;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LogPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Settings_Activity extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView textView12;
    EditText edittext2;
    String cel;
    SampleSQLiteDBHelper db2helper;
    String xmlString = null;
    Button button5;
    private String mParam1;
    private String mParam2;
    private Settings_Activity.OnFragmentInteractionListener mListener;



    private static boolean copyAsset(Context context) {

        OutputStream out = null;
        try {
            File file = new File(context.getFilesDir(), "data.json");
            if (file.exists()) {

            } else {
                AssetManager am = context.getAssets();
                InputStream is = am.open("data.json");
                int length = is.available();
                byte[] data = new byte[length];
                LogPrinter k = new LogPrinter(1, "hejooo");
                k.println("errror");
                Toast.makeText(context, "file", Toast.LENGTH_LONG);
                int read;
                new File(context.getFilesDir() + "data.json").createNewFile();
                out = new FileOutputStream(context.getFilesDir() + "data.json");
                while ((read = is.read(data)) != -1) {
                    out.write(data, 0, read);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db2helper = new SampleSQLiteDBHelper(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Toast.makeText(getActivity(), "steps", Toast.LENGTH_LONG);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_settings, container, false);
        final EditText edittext2 = view.findViewById(R.id.editText2);
        Button button5 = view.findViewById(R.id.button2);
        Button button_resetTrip = view.findViewById(R.id.button_delete_trip);
        button_resetTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = getLayoutInflater().inflate(R.layout.trip_delete_spinner, null);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Wpisz nazwe wycieczki:");

                alertDialog.setCancelable(false);
                final Spinner delete_spinner = (Spinner) view.findViewById(R.id.spinner_delete);
                Cursor cursor = db2helper.GetTripNames(getActivity());
                cursor.moveToFirst();



                ArrayList<String> Items = new ArrayList<>();
                Log.println(Log.ASSERT, "ass", String.valueOf(cursor.getCount()));
                while (!cursor.isAfterLast()) {

                    Log.println(Log.ASSERT, "ass", cursor.getString(0));
                    Items.add(cursor.getString(0));
                    cursor.moveToNext();
                }
                Object[] temp = Items.toArray();
                String[] items = Arrays.copyOf(temp, temp.length, String[].class);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                delete_spinner.setAdapter(adapter);
                cursor.close();
                delete_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString( "selected_trip_to_delete", items[position] );
                        editor.commit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Usuń", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                        String db_delete = sharedPref.getString("selected_trip_to_delete", "empty");
                        if(db_delete!="empty") {
                            db2helper.ClearTrip(db_delete);
                        }

                    }
                });


                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });


                alertDialog.setView(view);
                alertDialog.show();
            }
        });

        Button button_resetTrips = view.findViewById(R.id.button_delete_trips);
        button_resetTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAllTrips();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db2helper.Clear();
            }
        });
        edittext2.setText(cel);
        edittext2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                cel = edittext2.getText().toString();
                save();
            }
        });
        File F = new File(getActivity().getFilesDir() + "data.json");
        if (!F.exists())
            copyAsset(getActivity());

        try {
            InputStream is = new FileInputStream(getActivity().getFilesDir() + "data.json");

            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            String d = new String(data);
            JSONObject jso = new JSONObject(d);
            JSONObject main = jso.getJSONObject("sys");
            Toast.makeText(getActivity(), main.getString("steps"), Toast.LENGTH_LONG);
            cel = main.getString("steps");
            edittext2.setText(main.getString("steps"));

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Settings_Activity.OnFragmentInteractionListener) {
            mListener = (Settings_Activity.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public void onPause() {

        super.onPause();
    }

    public void save() {
        try {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString( "cel_krok", cel );

            editor.commit();

            InputStream is = new FileInputStream(getActivity().getFilesDir() + "data.json");
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            is.close();
            String d = new String(data);
            JSONObject jso = new JSONObject(d);
            JSONObject sys = jso.getJSONObject("sys");
            sys.put("steps", cel);
            jso.put("sys", sys);
            OutputStream os = new FileOutputStream(getActivity().getFilesDir() + "data.json");
            os.write(jso.toString().getBytes());


        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }



    public void ClearAllTrips() {
        Cursor cursor = db2helper.GetTripNames(getActivity());
        cursor.moveToFirst();


        //     Log.println(Log.ASSERT, "ass", String.valueOf(cursor.getCount()));
        while (!cursor.isAfterLast()) {

            //Log.println(Log.ASSERT, "ass", cursor.getString(0));
            db2helper.ClearTrip(cursor.getString(0));
            cursor.moveToNext();
        }
    }

}
