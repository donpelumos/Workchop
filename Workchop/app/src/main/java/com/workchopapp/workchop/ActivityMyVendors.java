package com.workchopapp.workchop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;

/**
 * Created by BALE on 18/07/2016.
 */

public class ActivityMyVendors extends AppCompatActivity {
    ActionBar appBar;
    ListView vendorTypeList;
    ListVendorType [] vendorTypeRows;
    String userId;

    ArrayList<String> vendorsList, gasSupplierList, hairStylistList, makeUpList, mechanicList, tailorList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBar  = getSupportActionBar();
        appBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        setContentView(R.layout.activity_myvendors);
        vendorTypeList = (ListView)findViewById(R.id.vendorTypeList);
        final View rootView = this.getLayoutInflater().inflate(R.layout.row_vendortypelist, null);
        View view = rootView.getRootView();
        userId = getIntent().getStringExtra("userId");
        /*vendorTypeRows = new ListVendorType[]{
                new ListVendorType("Master Gas Suppliers",R.drawable.icongas,"0"),
                new ListVendorType("Master Hair Stylists",R.drawable.iconstylist,"4"),
                new ListVendorType("Master Make-Up Artists",R.drawable.iconmakeup,"4"),
                new ListVendorType("Master Mechanics",R.drawable.iconmechanic ,"0"),
                new ListVendorType("Master Tailors",R.drawable.icontailor,"4")};

        final AdapterVendorTypeList adp = new AdapterVendorTypeList(view.getContext(),R.layout.row_vendortypelist, vendorTypeRows );
        vendorTypeList.setAdapter(adp);*/
        vendorsList = getIntent().getStringArrayListExtra("foundVendorList");
        //Toast.makeText(this,vendorsList.size() + " found",Toast.LENGTH_SHORT).show();
        tailorList = new ArrayList<>();  mechanicList = new ArrayList<>();  makeUpList = new ArrayList<>();
        hairStylistList = new ArrayList<>();  gasSupplierList = new ArrayList<>();
        //populateList();
        //Toast.makeText(this, mechanicList.size() + " mechanics found",Toast.LENGTH_SHORT).show();
        vendorTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityMyVendors.this, ActivityMySelectedVendor.class);
                intent.putExtra("index",position);
                if(position ==0){
                    intent.putStringArrayListExtra("selectedVendorList", gasSupplierList);
                    intent.putExtra("index", position);
                    intent.putExtra("userId", userId);
                }
                else if(position == 1){
                    intent.putStringArrayListExtra("selectedVendorList", hairStylistList);
                    intent.putExtra("index", position);
                    intent.putExtra("userId", userId);
                }
                else if(position == 2){
                    intent.putStringArrayListExtra("selectedVendorList", makeUpList);
                    intent.putExtra("index", position);
                    intent.putExtra("userId", userId);
                }
                else if(position == 3){
                    intent.putStringArrayListExtra("selectedVendorList", mechanicList);
                    intent.putExtra("index", position);
                    intent.putExtra("userId", userId);
                }
                else if(position == 4){
                    intent.putStringArrayListExtra("selectedVendorList", tailorList);
                    intent.putExtra("index", position);
                    intent.putExtra("userId", userId);
                }
                startActivity(intent);
            }
        });
        new getTradesmenCount(ActivityMyVendors.this).execute(userId);

    }

    private class getTradesmenCount extends AsyncTask<String,Void,String> {
        Context context;

        public getTradesmenCount(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_vendor_counts.php";
            String dataUrlParameters="";
            try{
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {

            }
            try {
                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl + "?" + dataUrlParameters));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                Log.v("TAGV",dataUrl + "?" + dataUrlParameters);
                final StringBuffer sb2 = new StringBuffer("");
                String line2 = "";
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        String [] counts = sb2.toString().split("--");
                        vendorTypeRows = new ListVendorType[]{
                                new ListVendorType("Master Gas Suppliers",R.drawable.icongas,counts[0]),
                                new ListVendorType("Master Hair Stylists",R.drawable.iconstylist,counts[1]),
                                new ListVendorType("Master Make-Up Artists",R.drawable.iconmakeup,counts[2]),
                                new ListVendorType("Master Mechanics",R.drawable.iconmechanic ,counts[3]),
                                new ListVendorType("Master Tailors",R.drawable.icontailor,counts[4])};

                        final AdapterVendorTypeList adp = new AdapterVendorTypeList(context,R.layout.row_vendortypelist, vendorTypeRows );
                        vendorTypeList.setAdapter(adp);
                    }
                });
            }
            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        //Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        //Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        //Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
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

    public void populateList(){
        for(int i=0; i<vendorsList.size(); i++){
            String [] value = vendorsList.get(i).split(" +");
            for(String val : value) {
                if (val.toLowerCase().equals("mechanic")) {
                    mechanicList.add(vendorsList.get(i));
                } else if (val.toLowerCase().equals("makeup")) {
                    makeUpList.add(vendorsList.get(i));
                } else if (val.toLowerCase().equals("fashion") || val.toLowerCase().equals("tailor")) {
                    tailorList.add(vendorsList.get(i));
                } else if (val.toLowerCase().equals("hair") || val.toLowerCase().equals("stylist")) {
                    hairStylistList.add(vendorsList.get(i));
                } else if (val.toLowerCase().equals("gas")) {
                    gasSupplierList.add(vendorsList.get(i));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_terms, menu);
        return true;
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
}
