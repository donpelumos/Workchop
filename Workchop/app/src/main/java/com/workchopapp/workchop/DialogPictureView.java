package com.workchopapp.workchop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BALE on 25/07/2016.
 */

public class DialogPictureView extends DialogFragment {
    AlertDialog dialog;
    String userId;
    ImageView accountImage;
    InputStream is3;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_picture_view, null);
        Bundle bundle = getArguments();
        accountImage = (ImageView)view.findViewById(R.id.accountImage);
        userId = bundle.getString("userId");
        accountImage.setVisibility(View.GONE);
        new getUserPicture(getActivity().getApplicationContext()).execute("userId");
        builder.setView(view);
        dialog = builder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    private class getUserPicture extends AsyncTask<String,Void,String> {
        Context context;

        public getUserPicture(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String imageUrl= "http://workchopapp.com/mobile_app/user_pictures/"+userId+".jpg";

            String dataUrlParameters = null;

            URL url = null;
            try{

                URL url2 = new URL(imageUrl);
                HttpURLConnection connection  = (HttpURLConnection) url2.openConnection();


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
                        accountImage.setImageBitmap(img );
                        accountImage.setVisibility(View.VISIBLE);

                    }
                });

            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }
}