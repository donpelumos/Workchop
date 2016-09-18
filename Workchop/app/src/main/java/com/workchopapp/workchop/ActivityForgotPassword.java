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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BALE on 28/07/2016.
 */

public class ActivityForgotPassword extends AppCompatActivity {
    ActionBar appBar;
    EditText phoneNumber, newPassword1, newPassword2, confirmationCode;
    Button changePasswordButton, confirmationCodeButton, receivedCOnfirmationCodeButton;
    int oldPasswordCorrect, confirmationCodeReceived;
    String checkPasswordResult = "";
    TextView selectTect;
    Spinner typeSpinner;
    String [] typeSpinnerList = {"-- Select Type--","User","Vendor"};
    ArrayAdapter<String> a1;
    int typeSelected = 0;
    String mobilePhoneNumber="";
    String sentConfirmationCode="";
    TelephonyManager tMgr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        appBar = getSupportActionBar();

        appBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        setContentView(R.layout.activity_forgot_password);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");
        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
        newPassword1 = (EditText)findViewById(R.id.newPassword1);
        newPassword2 = (EditText)findViewById(R.id.newPassword2);
        confirmationCode = (EditText)findViewById(R.id.confirmationCode);
        changePasswordButton = (Button)findViewById(R.id.changePasswordButton);
        confirmationCodeButton = (Button)findViewById(R.id.confirmationCodeButton);
        receivedCOnfirmationCodeButton = (Button)findViewById(R.id.receivedConfirmationCodeButton);
        changePasswordButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(changePasswordButton));
        confirmationCodeButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(confirmationCodeButton));
        receivedCOnfirmationCodeButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(receivedCOnfirmationCodeButton));

        selectTect = (TextView)findViewById(R.id.selectText);
        selectTect.setTypeface(type);
        typeSpinner = (Spinner)findViewById(R.id.type);
        a1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeSpinnerList);
        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(a1);

        tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mobilePhoneNumber = tMgr.getLine1Number();
        //Toast.makeText(ActivityForgotPassword.this,"Phone Number - "+mobilePhoneNumber,Toast.LENGTH_SHORT).show();

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        phoneNumber.setTypeface(type); newPassword1.setTypeface(type); newPassword2.setTypeface(type);
        confirmationCodeButton.setTypeface(type); receivedCOnfirmationCodeButton.setTypeface(type);
        changePasswordButton.setTypeface(type);
        oldPasswordCorrect = 0; confirmationCodeReceived = 0;
        confirmationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(typeSelected == 0){
                    Toast.makeText(ActivityForgotPassword.this,"Select a type",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (phoneNumber.getText().length() >= 1) {
                        new checkPhoneNumber(ActivityForgotPassword.this).execute(String.valueOf(typeSelected),
                                phoneNumber.getText().toString());
                    } else {
                        Toast.makeText(ActivityForgotPassword.this, "Enter a valid Phone Number", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        receivedCOnfirmationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmationCode.getText().toString().equals(sentConfirmationCode)){
                    //Toast.makeText(ActivityForgotPassword.this, "Equals", Toast.LENGTH_SHORT).show();
                    newPassword1.setEnabled(true);  newPassword2.setEnabled(true);
                    changePasswordButton.setEnabled(true);
                }
                else{
                    Toast.makeText(ActivityForgotPassword.this, "Wrong Confirmation Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPassword1.getText().toString().equals(newPassword2.getText().toString())){
                    Toast.makeText(ActivityForgotPassword.this,"Password successfully changed",Toast.LENGTH_LONG).show();
                    new updatePassword(ActivityForgotPassword.this).execute(String.valueOf(typeSelected),newPassword1.getText().toString(),
                            phoneNumber.getText().toString());
                    //onBackPressed();
                }
                else{
                    Toast.makeText(ActivityForgotPassword.this,"Passwords do not match",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class checkPhoneNumber extends AsyncTask<String,Void,String> {
        Context context;

        public checkPhoneNumber(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_details_from_phone.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "type="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&phone_number="+URLEncoder.encode(params[1],"UTF-8");
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
                            confirmationCode.setEnabled(true);
                            receivedCOnfirmationCodeButton.setEnabled(true);
                            new sendToPhone(context).execute(params[1]);
                            //newPassword1.setEnabled(true);
                            //newPassword2.setEnabled(true);
                            //changePasswordButton.setEnabled(true);
                        }
                        else if(sb.toString().equals("logged")){
                            checkPasswordResult = "false";
                            Toast.makeText(context,"Account already logged in",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context,"No such number exists",Toast.LENGTH_SHORT).show();
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

    private class sendToPhone extends AsyncTask<String,Void,String> {
        Context context;

        public sendToPhone(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://www.smslive247.com/http/index.aspx?cmd=login&owneremail=donpelumos@gmail.com&subacct=EMCONF&subacctpwd=workchop12345";

            Date date = new Date();
            String dataUrlParametersa = null;
            String dataUrlParametersb = null;
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateFormat dateYear = new SimpleDateFormat("yyyy");
            DateFormat dateMonth = new SimpleDateFormat("MM");
            DateFormat dateDay = new SimpleDateFormat("dd");
            DateFormat dateHour = new SimpleDateFormat("HH");
            DateFormat dateMinute = new SimpleDateFormat("mm");
            DateFormat dateSecond = new SimpleDateFormat("ss");

            sentConfirmationCode = dateDay.format(date)+dateSecond.format(date)+dateMinute.format(date)+dateHour.format(date);

            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;
            try{

                Handler ha = new Handler(Looper.getMainLooper());
                ha.post(new Runnable() {
                    public void run() {
                        //Toast.makeText(context, "Confirmation Code Sent To Phone - "+sentConfirmationCode, Toast.LENGTH_LONG).show();
                    }
                });

                url = new URL(dataUrl);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(dataUrl));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line="";
                Log.v("ADDRESS",dataUrl);
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                final String sessionID = sb.toString().split(":")[1].trim();
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Response =>"+ sessionID, Toast.LENGTH_LONG).show();
                    }
                });
                String urlData = "http://www.smslive247.com/http/index.aspx?cmd=sendmsg&sessionid="
                        +sessionID+"&message=";
                String urlData3 = "&sender=Workchop&sendto="+params[0]+"&msgtype=0";
                String urlData2="";
                try {
                    urlData2 = URLEncoder.encode("Your Confirmation Code is "+sentConfirmationCode,"UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
                }
                urlData = urlData+urlData2+urlData3;
                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(urlData));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                Log.v("ADDRESS",urlData);
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }
                final String newResponse = sb2.toString().split(":")[0].trim();
                Handler h2 = new Handler(Looper.getMainLooper());
                h2.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Response Code=>"+ newResponse, Toast.LENGTH_LONG).show();
                    }
                });
                in.close();
                in2.close();
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
            String dataUrl = "http://workchopapp.com/mobile_app/set_new_password.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "type="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8")
                        +"&number="+URLEncoder.encode(params[1],"UTF-8");
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
                        Toast.makeText(ActivityForgotPassword.this,"Password Changed",Toast.LENGTH_SHORT).show();
                        phoneNumber.setText(""); newPassword1.setText(""); newPassword1.setEnabled(false);
                        newPassword2.setText("");newPassword2.setEnabled(false); confirmationCodeButton.setEnabled(false);
                        receivedCOnfirmationCodeButton.setEnabled(false); confirmationCode.setEnabled(false);
                        finish();

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
