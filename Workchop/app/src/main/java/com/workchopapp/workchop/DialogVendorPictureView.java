package com.workchopapp.workchop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by BALE on 05/08/2016.
 */

public class DialogVendorPictureView extends DialogFragment {
    ImageView vendorPicture;
    String pictureUrl;
    InputStream is3;
    AlertDialog dialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_vendor_picture_view, null);
        vendorPicture = (ImageView)view.findViewById(R.id.vendorPicture);
        pictureUrl = getArguments().getString("url");
        new displayImage(getActivity().getApplicationContext()).execute(pictureUrl);
        builder.setView(view);
        dialog = builder.create();
        return dialog;
    }

    private class displayImage extends AsyncTask<String,Void,String> {
        Context context;

        public displayImage(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {

            String imageUrl= params[0];

            try{
                URL url2 = new URL(imageUrl);
                //is3 = (InputStream) new URL(imageUrl).getContent();
                HttpURLConnection connection  = (HttpURLConnection) url2.openConnection();

                //final InputStream is2 = connection.getInputStream();

                Log.v("VENDORS GOTTEN","VENDORS GOTTEN");

                connection.setDoOutput(true);
                connection.connect();
                is3 = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap img = BitmapFactory.decodeStream(is3);

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        vendorPicture.setImageBitmap(img );
                        vendorPicture.setVisibility(View.GONE);
                        vendorPicture.setVisibility(View.VISIBLE);
                    }
                });
                connection.disconnect();
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }
}
