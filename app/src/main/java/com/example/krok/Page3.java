package com.example.krok;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.LogPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Page3 extends Fragment {
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
    private OnFragmentInteractionListener mListener;

    public Page3() {
    }

    public static Page3 newInstance(String param1, String param2) {
        Page3 fragment = new Page3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.page3_layout, container, false);
        final EditText edittext2 = view.findViewById(R.id.editText2);
        Button button5 = view.findViewById(R.id.button2);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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


}

