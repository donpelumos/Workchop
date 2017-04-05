package com.workchopapp.workchop;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by BALE on 25/03/2017.
 */

public class ActivityConfirmationCodeVendor extends AppCompatActivity {
    TextView confirmationButton;
    EditText confirmationCodeText;
    String confirmationCode, surname, firstname, phoneNo, enterPassword, email, vendorId, type;
    String rawPassword;
    SQLiteDatabase mydatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        setContentView(R.layout.activity_confirmation_code);
        mydatabase  = openOrCreateDatabase("workchop.db",MODE_PRIVATE,null);
        Cursor resultSet = mydatabase.rawQuery("Select * from TestLoginPassword",null);
        resultSet.moveToFirst();
        rawPassword = resultSet.getString(0);
        //Toast.makeText(getActivity().getApplicationContext(), "DATABASE SEEN - RAW PASSWORD = "+ rawPassword, Toast.LENGTH_SHORT).show();
        Log.v("DATABASE SEEN","DATABASE SEEN");
        mydatabase.execSQL("DROP TABLE TestLoginPassword;");

        confirmationCode = getIntent().getStringExtra("extra");
        surname = getIntent().getStringExtra("surname");
        firstname = getIntent().getStringExtra("firstname");
        phoneNo = getIntent().getStringExtra("phoneNo");
        vendorId = getIntent().getStringExtra("vendorId");
        email = getIntent().getStringExtra("email");
        type = getIntent().getStringExtra("type");
        enterPassword = getIntent().getStringExtra("password");
        confirmationButton = (TextView)findViewById(R.id.confirmationButton);
        confirmationCodeText = (EditText)findViewById(R.id.confirmationCode);
        confirmationButton.setOnTouchListener(new TextHighlighterOnTouchListener(confirmationButton));
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getRegistrationCode((ActivityConfirmationCodeVendor.this)).execute("");
            }
        });
    }

    private class permVendorSignUp extends AsyncTask<String,Void,String> {
        Context context;

        public permVendorSignUp(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/perm_vendor_signup.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/clear_temp_vendor_table.php";
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateFormat dateYear = new SimpleDateFormat("yyyy");
            DateFormat dateMonth = new SimpleDateFormat("MM");
            DateFormat dateDay = new SimpleDateFormat("dd");
            DateFormat dateHour = new SimpleDateFormat("HH");
            DateFormat dateMinute = new SimpleDateFormat("mm");
            DateFormat dateSecond = new SimpleDateFormat("ss");

            Date date = new Date();
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "name="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&type="+URLEncoder.encode(params[1],"UTF-8")
                        +"&email_address="+URLEncoder.encode(params[2],"UTF-8")
                        +"&mobile_number="+URLEncoder.encode(params[3],"UTF-8")
                        +"&location_index=0&vendor_id="+URLEncoder.encode(params[4],"UTF-8")
                        +"&password="+URLEncoder.encode(params[5],"UTF-8")
                        +"&suspended_index=0&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");
                dataUrlParameters2 = "id="+ URLEncoder.encode(vendorId,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityConfirmationCodeVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters2));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        if(sb.toString().split("--")[0].equals("done")) {
                            //Toast.makeText(context, sb2.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "VENDOR ACCOUNT CREATED", Toast.LENGTH_SHORT).show();

                            SQLiteDatabase mydatabase2 = openOrCreateDatabase("workchop_user_account.db", MODE_PRIVATE, null);
                            mydatabase2.execSQL("CREATE TABLE IF NOT EXISTS LoginDetails(detail VARCHAR);");
                            mydatabase2.execSQL(String.format("INSERT INTO LoginDetails VALUES('%s');",
                                    params[3]+"=="+params[5]+"==2"));

                            Intent intent = new Intent(context,ActivityVendorMain.class);
                            intent.putExtra("val4", vendorId);
                            intent.putExtra("val7","signupScreen");
                            startActivity(intent);
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

    private class getRegistrationCode extends AsyncTask<String,Void,String> {
        Context context;

        public getRegistrationCode(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl2 = "http://workchopapp.com/mobile_app/get_vendor_registration_code.php";
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            Date date = new Date();
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters2 = "id="+URLEncoder.encode(vendorId,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;

            try{

                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters2));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                Log.v("ADDRESS",dataUrl2+"?"+dataUrlParameters2);
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }
                confirmationCode = sb2.toString();

                String dataUrl3 = "http://workchopapp.com/mobile_app/get_temp_vendor_details.php";
                String dataUrlParameters3 = null;
                try {
                    dataUrlParameters3 = "code="+URLEncoder.encode(confirmationCode,"UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
                }
                HttpClient client3 = new DefaultHttpClient();
                HttpGet request3 = new HttpGet();
                request3.setURI(new URI(dataUrl3+"?"+dataUrlParameters3));
                HttpResponse response3 = client3.execute(request3);
                BufferedReader in3 = new BufferedReader(new InputStreamReader(response3.getEntity().getContent()));

                final StringBuffer sb3 = new StringBuffer("");
                String line3="";
                Log.v("ADDRESS2",dataUrl2+"?"+dataUrlParameters2);
                Log.v("ADDRESS3",dataUrl3+"?"+dataUrlParameters3);
                while ((line3 = in3.readLine()) != null) {
                    sb3.append(line3);
                    break;
                }
                surname = sb3.toString().split("--")[1];
                phoneNo = sb3.toString().split("--")[2];
                email = sb3.toString().split("--")[3];
                vendorId = sb3.toString().split("--")[5];
                Log.v("ENTER PWD - RAW PWD", enterPassword + " - " + rawPassword);
                enterPassword = rawPassword;
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        //Toast.makeText(context, "Registration Code Sent To Email - "+confirmationCode, Toast.LENGTH_SHORT).show();
                        //Log.v("Confrer",confirmationCode);
                        //Toast.makeText(getActivity().getApplicationContext(),"Comparing "+confirmationCode+" with "+
                        //confirmationCodeText.getText().toString(),Toast.LENGTH_SHORT).show();

                        if(confirmationCodeText.getText().toString().equals(confirmationCode)) {
                            //Toast.makeText(getActivity().getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                            new permVendorSignUp(ActivityConfirmationCodeVendor.this).execute(surname,type,
                                    email,phoneNo,vendorId,enterPassword);
                        }
                        else{
                            Toast.makeText(ActivityConfirmationCodeVendor.this, "Wrong Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                in2.close();
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class TextHighlighterOnTouchListener implements View.OnTouchListener {
        //This
        final TextView imageButton;

        public TextHighlighterOnTouchListener(final TextView imageButton) {
            super();
            this.imageButton = imageButton;
        }

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //grey color filter, you can change the color as you like
                imageButton.setAlpha((float)0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float) 1.0);
            }
            return false;
        }

    }
}
