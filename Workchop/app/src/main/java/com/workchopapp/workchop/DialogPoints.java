package com.workchopapp.workchop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by BALE on 22/08/2016.
 */

public class DialogPoints extends DialogFragment {
    AlertDialog dialog;
    Bundle bundle;
    String userId;
    String pointsCount="";
    TextView pointsText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_points, null);
        builder.setView(view);
        bundle = getArguments();
        userId = bundle.getString("userId");
        //confirmationCode = bundle.getString("val");
        pointsText = (TextView)view.findViewById(R.id.pointsText);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        pointsText.setTypeface(type);
        new getPoints(getActivity().getApplicationContext()).execute(userId);
        dialog = builder.create();

        return dialog;
    }

    private class getPoints extends AsyncTask<String,Void,String> {
        Context context;

        public getPoints(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_points.php";


            Date date = new Date();
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(getActivity(),new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("LINK ADDRESS",dataUrl+"?"+dataUrlParameters);
            try{

                url = new URL(dataUrl+"?"+dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(dataUrl+"?"+dataUrlParameters));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line="";
                Log.v("ADDRESS",dataUrl+"?"+dataUrlParameters);
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        pointsText.setText("You have "+sb.toString()+" work points");
                    }
                });

                in.close();
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

}
