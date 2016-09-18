package com.workchopapp.workchop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by BALE on 28/07/2016.
 */

public class ActivityChangePasswordVendor extends AppCompatActivity {
    ActionBar appBar;
    EditText oldPassword, newPassword1, newPassword2, confirmationCode;
    Button changePasswordButton, confirmationCodeButton, verifyConfirmationCodeButton;
    int oldPasswordCorrect, confirmationCodeReceived;
    String checkPasswordResult = "";
    String vendorId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        appBar = getSupportActionBar();
        appBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        setContentView(R.layout.activity_change_password_vendor);
        vendorId = getIntent().getStringExtra("vendorId");

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");
        oldPassword = (EditText)findViewById(R.id.oldPassword);
        newPassword1 = (EditText)findViewById(R.id.newPassword1);
        newPassword2 = (EditText)findViewById(R.id.newPassword2);
        confirmationCode = (EditText)findViewById(R.id.confirmationCode);
        changePasswordButton = (Button)findViewById(R.id.changePasswordButton);
        confirmationCodeButton = (Button)findViewById(R.id.confirmationCodeButton);
        verifyConfirmationCodeButton = (Button)findViewById(R.id.verifyConfirmationCodeButton);
        changePasswordButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(changePasswordButton));
        confirmationCodeButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(confirmationCodeButton));
        verifyConfirmationCodeButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(verifyConfirmationCodeButton));
        oldPassword.setTypeface(type); newPassword1.setTypeface(type); newPassword2.setTypeface(type);
        confirmationCodeButton.setTypeface(type); verifyConfirmationCodeButton.setTypeface(type);
        changePasswordButton.setTypeface(type);
        oldPasswordCorrect = 0; confirmationCodeReceived = 0;
        confirmationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPassword.getText().length() >= 1) {
                    new checkPassword(ActivityChangePasswordVendor.this).execute(vendorId,oldPassword.getText().toString());
                }
                else{
                    Toast.makeText(ActivityChangePasswordVendor.this,"Enter a valid password",Toast.LENGTH_LONG).show();
                }
            }
        });
        verifyConfirmationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //newPassword1.setEnabled(true);
                //newPassword2.setEnabled(true);
                //changePasswordButton.setEnabled(true);

            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPassword1.getText().toString().equals(newPassword2.getText().toString())){
                    Toast.makeText(ActivityChangePasswordVendor.this,"Password successfully changed",Toast.LENGTH_LONG).show();
                    new updatePassword(ActivityChangePasswordVendor.this).execute(vendorId,newPassword1.getText().toString());
                    //onBackPressed();
                }
                else{
                    Toast.makeText(ActivityChangePasswordVendor.this,"Passwords do not match",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class checkPassword extends AsyncTask<String,Void,String> {
        Context context;

        public checkPassword(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/check_vendor_password.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            try{
                url = new URL(dataUrl+"?"+dataUrlParameters);
                Log.v("INSIDE GET USER VENDORS",dataUrl+"?"+dataUrlParameters);
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
                        if(sb.toString().equals("true")) {
                            checkPasswordResult = sb.toString();
                            newPassword1.setEnabled(true);
                            newPassword2.setEnabled(true);
                            changePasswordButton.setEnabled(true);
                        }
                        else {
                            checkPasswordResult = "false";
                            Toast.makeText(context,"Wrong Password",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                in.close();
            }

            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally{
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            return null;
        }
    }

    private class updatePassword extends AsyncTask<String,Void,String> {
        Context context;

        public updatePassword(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/update_vendor_password.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            try{
                url = new URL(dataUrl+"?"+dataUrlParameters);
                Log.v("INSIDE GET USER VENDORS",dataUrl+"?"+dataUrlParameters);
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
                            Toast.makeText(ActivityChangePasswordVendor.this,"Password Changed",Toast.LENGTH_SHORT).show();
                            oldPassword.setText(""); newPassword1.setText(""); newPassword1.setEnabled(false);
                            newPassword2.setText("");newPassword2.setEnabled(false); confirmationCodeButton.setEnabled(false);
                            finish();
                        }
                        else {
                        }

                    }
                });
                in.close();
            }

            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally{
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;

        //return super.onOptionsItemSelected(item);
    }

    private class TextViewHighlighterOnTouchListener implements View.OnTouchListener {
        //This
        final TextView imageButton;

        public TextViewHighlighterOnTouchListener(final TextView imageButton) {
            super();
            this.imageButton = imageButton;
        }

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //grey color filter, you can change the color as you like
                imageButton.setAlpha((float)0.7);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float) 1.0);
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
                imageButton.setAlpha((float) 0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float) 1.0);
            }
            return false;
        }

    }
}
