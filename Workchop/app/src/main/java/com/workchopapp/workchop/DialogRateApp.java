package com.workchopapp.workchop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BALE on 25/07/2016.
 */

public class DialogRateApp extends DialogFragment {
    ImageView star1, star2, star3, star4, star5;
    int currentIndex, rated, starsRated;
    Button rateButton;
    AlertDialog dialog;
    TextView rateLabel;
    String userId;
    public DialogRateApp(){

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_rateapp, null);
        builder.setView(view);
        dialog = builder.create();
        currentIndex = 0;
        userId = getArguments().getString("userId");
        rated = 0;
        rateButton = (Button)view.findViewById(R.id.rateButton);
        rateButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(rateButton));
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new sendRating(getActivity().getApplicationContext()).execute(userId,String.valueOf(starsRated));
                dismiss();
            }
        });
        starsRated = 0;
        star1 = (ImageView)view.findViewById(R.id.star1);
        star2 = (ImageView)view.findViewById(R.id.star2);
        star3 = (ImageView)view.findViewById(R.id.star3);
        star4 = (ImageView)view.findViewById(R.id.star4);
        star5 = (ImageView)view.findViewById(R.id.star5);
        star1.setOnTouchListener(new ImageHighlighterOnTouchListener(star1));
        star2.setOnTouchListener(new ImageHighlighterOnTouchListener(star2));
        star3.setOnTouchListener(new ImageHighlighterOnTouchListener(star3));
        star4.setOnTouchListener(new ImageHighlighterOnTouchListener(star4));
        star5.setOnTouchListener(new ImageHighlighterOnTouchListener(star5));
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rated == 0 && starsRated == 0){
                    rated = 1; starsRated = 1;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated > 1){
                    rated = 1; starsRated = 1;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setLayoutParams(params); star2.setLayoutParams(params2); star3.setLayoutParams(params2);
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star4.setLayoutParams(params2); star5.setLayoutParams(params2);
                }
                else if(rated == 1 && starsRated == 1){
                    rated = 0; starsRated = 0;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params);
                }
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rated == 0 && starsRated == 0){
                    rated = 1; starsRated = 2;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated < 2){
                    rated = 1; starsRated = 2;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated == 2){
                    rated = 1; starsRated = 1;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params); star2.setLayoutParams(params2);
                }
                else if(rated == 1 && starsRated > 2){
                    rated = 1; starsRated = 2;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params2);
                    star4.setLayoutParams(params2); star5.setLayoutParams(params2);
                }
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rated == 0 && starsRated == 0){
                    rated = 1; starsRated = 3;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated < 3){
                    rated = 1; starsRated = 3;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated == 3){
                    rated = 1; starsRated = 2;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params2);
                }
                else if(rated == 1 && starsRated > 3){
                    rated = 1; starsRated = 3;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params2); star5.setLayoutParams(params2);
                }
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rated == 0 && starsRated == 0){
                    rated = 1; starsRated = 4;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated < 4){
                    rated = 1; starsRated = 4;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated == 4){
                    rated = 1; starsRated = 3;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params2);
                }
                else if(rated == 1 && starsRated > 4){
                    rated = 1; starsRated = 4;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params); star5.setLayoutParams(params2);
                }
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rated == 0 && starsRated == 0){
                    rated = 1; starsRated = 5;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params); star5.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated < 5){
                    rated = 1; starsRated = 5;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params); star5.setLayoutParams(params);
                }
                else if(rated == 1 && starsRated == 5){
                    rated = 1; starsRated = 4;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.ratingblack));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params); star5.setLayoutParams(params2);
                }
                else if(rated == 1 && starsRated > 5){
                    rated = 1; starsRated = 5;
                    float density = view.getContext().getResources().getDisplayMetrics().density;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int)(28*density),(int)(28*density));
                    star1.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star2.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star3.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star4.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star5.setImageDrawable(getResources().getDrawable(R.drawable.rating));
                    star1.setLayoutParams(params); star2.setLayoutParams(params); star3.setLayoutParams(params);
                    star4.setLayoutParams(params); star5.setLayoutParams(params);
                }
            }
        });
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        rateButton.setTypeface(type);
        rateLabel = (TextView)view.findViewById(R.id.rateLabel);
        rateLabel.setTypeface(type);
        return dialog;
    }

    private class sendRating extends AsyncTask<String,Void,String> {
        Context context;

        public sendRating(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_rating.php";
            DateFormat dateYear = new SimpleDateFormat("yyyy");
            DateFormat dateMonth = new SimpleDateFormat("MM");
            DateFormat dateDay = new SimpleDateFormat("dd");
            DateFormat dateHour = new SimpleDateFormat("HH");
            DateFormat dateMinute = new SimpleDateFormat("mm");
            DateFormat dateSecond = new SimpleDateFormat("ss");
            Date date = new Date();


            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&rating="+URLEncoder.encode(params[1],"UTF-8")
                        +"&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");;
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            try{
                url = new URL(dataUrl+"?"+dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(dataUrl+"?"+dataUrlParameters));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line="";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if(sb.toString().equals("done")) {
                            Toast.makeText(context,"Ratings Received",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                        else {
                        }

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

    private class ImageHighlighterOnTouchListener implements View.OnTouchListener {
        //This
        final ImageView imageButton;

        public ImageHighlighterOnTouchListener(final ImageView imageButton) {
            super();
            this.imageButton = imageButton;
        }

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //grey color filter, you can change the color as you like
                imageButton.setAlpha((float)0.5);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float)1.0);
            }
            return false;
        }

    }
    private class ButtonHighlighterOnTouchListener implements View.OnTouchListener {
        //This
        final Button imageButton;

        public ButtonHighlighterOnTouchListener(final Button imageButton) {
            super();
            this.imageButton = imageButton;
        }

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //grey color filter, you can change the color as you like
                imageButton.setAlpha((float)0.5);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float)1.0);
            }
            return false;
        }

    }
}
