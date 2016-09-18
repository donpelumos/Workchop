package com.workchopapp.workchop;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by BALE on 18/07/2016.
 */

public class ActivityMySelectedVendor extends AppCompatActivity implements DialogAddNewVendor.NewVendorAdded,
DialogVendorProfile.Close{
    ActionBar appBar;
    Intent intent;
    String title;
    TextView searchRoundTabFrame, searchRoundTab, addVendorRoundTab,addVendorRoundTabFrame, refreshRoundTab, refreshRoundTabFrame;
    int icon;
    ListView selectedVendorList;
    ArrayList<String> vendorsList;
    ListSelectedVendor [] vendorRows;
    int roundIndex;
    EditText searchBar;
    String userId;
    int vendorType, vendorRowsLength;
    ProgressDialog progress;
    FragmentManager fm;
    String vendorId;
    ProgressDialog addingVendor;

    Map<String, String> sortedContacts;
    int index;
    ArrayList<String> contactsName, contactsNumber, contactsDetails, newContactsName, newContactsNumber, foundVendors,
            finalContactsList;
    ArrayList<Integer> contactsIndex, newContactsIndex;
    ArrayList<String> workchopVendorList, workchopVendorList1, workchopVendorList2, workchopVendorList3, workchopVendorList4,
            workchopVendorList5, vendorsIds;
    int workchopUserLocationIndex, vendorToBeDeletedPosition=0;

    ProgressDialog progressdialog;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBar = getSupportActionBar();
        appBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        intent = getIntent();
        userId = getIntent().getStringExtra("userId");
        vendorType = getIntent().getIntExtra("index",0)+1;
        addingVendor = new ProgressDialog(ActivityMySelectedVendor.this);
        addingVendor.setTitle("Adding New Vendor");
        index = 0;
        contactsIndex = new ArrayList<Integer>();newContactsIndex = new ArrayList<Integer>();
        contactsName = new ArrayList<String>();contactsNumber = new ArrayList<String>();
        newContactsName = new ArrayList<String>();newContactsNumber = new ArrayList<String>();
        finalContactsList = new ArrayList<String>();
        workchopVendorList = new ArrayList<>();
        workchopVendorList1 = new ArrayList<>();
        workchopVendorList2 = new ArrayList<>();
        workchopVendorList3 = new ArrayList<>();
        workchopVendorList4 = new ArrayList<>();
        workchopVendorList5 = new ArrayList<>();
        vendorsIds = new ArrayList<>();
        workchopUserLocationIndex = 0;
        new getUserLocation(ActivityMySelectedVendor.this).execute(userId);
        vendorId = "";

        contactsName = new ArrayList<String>();contactsNumber = new ArrayList<String>();
        newContactsName = new ArrayList<String>();newContactsNumber = new ArrayList<String>();
        contactsIndex = new ArrayList<Integer>();newContactsIndex = new ArrayList<Integer>();

        fm = getFragmentManager();
        if(intent.getIntExtra("index",0) == 3){
            title = "My Mechanics";
            icon = R.drawable.iconmechanic;
        }
        else if(intent.getIntExtra("index",0) == 0){
            title = "My Gas Suppliers";
            icon = R.drawable.icongas;
        }
        else if(intent.getIntExtra("index",0) == 1){
            title = "My Hair Stylists";
            icon = R.drawable.iconstylist;
        }
        else if(intent.getIntExtra("index",0) == 2){
            title = "My Make-Up Artists";
            icon = R.drawable.iconmakeup;
        }
        else if(intent.getIntExtra("index",0) == 4){
            title = "My Tailors";
            icon = R.drawable.icontailor;
        }
        appBar.setTitle(title);
        setContentView(R.layout.activity_myselectedvendor);
        vendorsList = getIntent().getStringArrayListExtra("selectedVendorList");
        //Toast.makeText(this,vendorsList.size()+" " + title + " found",Toast.LENGTH_SHORT).show();
        selectedVendorList = (ListView)findViewById(R.id.selectedVendorList);
        final View rootView = this.getLayoutInflater().inflate(R.layout.row_selectedvendor, null);
        View view = rootView.getRootView();



        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("loading...");
        progress.show();

        Log.v("XXXXXXXXXX",userId+"===="+String.valueOf(vendorType));



        selectedVendorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogVendorProfile newFragment = new DialogVendorProfile();
                Bundle bundle = new Bundle();
                bundle.putString("userId",userId);
                bundle.putString("vendorType",String.valueOf(vendorType));
                bundle.putString("index", String.valueOf(position));
                newFragment.setArguments(bundle);
                newFragment.show(fm, "dialog");
            }
        });

        selectedVendorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vendorToBeDeletedPosition = position;
                new AlertDialog.Builder(ActivityMySelectedVendor.this)
                        .setIcon(R.drawable.delete)
                        .setTitle("Remove Vendor")
                        .setMessage("Are you sure you want to remove this vendor?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(ActivityMySelectedVendor.this,"Position "+vendorToBeDeletedPosition+" to go with id "+
                                        //vendorsIds.get(vendorToBeDeletedPosition),
                                        //Toast.LENGTH_SHORT).show();
                                new deleteVendor(ActivityMySelectedVendor.this).execute(userId,vendorsIds.get(vendorToBeDeletedPosition));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
            }
        });

        searchRoundTab = (TextView)findViewById(R.id.searchRoundTab);
        addVendorRoundTab = (TextView)findViewById(R.id.addVendorRoundTab);
        refreshRoundTab = (TextView)findViewById(R.id.refreshRoundTab);
        searchRoundTabFrame = (TextView)findViewById(R.id.searchRoundTabFrame);
        addVendorRoundTabFrame = (TextView)findViewById(R.id.addVendorRoundTabFrame);
        refreshRoundTabFrame = (TextView)findViewById(R.id.refreshRoundTabFrame);
        searchRoundTab.setOnTouchListener(new TextHighlighterOnTouchListener(searchRoundTab));
        addVendorRoundTab.setOnTouchListener(new TextHighlighterOnTouchListener(addVendorRoundTab));
        refreshRoundTab.setOnTouchListener(new TextHighlighterOnTouchListener(refreshRoundTab));
        roundIndex = 1;
        searchBar = (EditText)findViewById(R.id.searchBar);
        progressdialog = new ProgressDialog(this);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(ActivityMySelectedVendor.this, searchBar.getText().toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchRoundTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundIndex = 3;
                searchRoundTab.setBackground(getResources().getDrawable(R.drawable.background_darkgrey_circle));
                addVendorRoundTab.setBackground(getResources().getDrawable(R.drawable.background_grey_circle));
                refreshRoundTab.setBackground(getResources().getDrawable(R.drawable.background_grey_circle));
                searchBar.setVisibility(View.VISIBLE);
            }
        });
        refreshRoundTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundIndex = 2;
                searchRoundTab.setBackground(getResources().getDrawable(R.drawable.background_grey_circle));
                addVendorRoundTab.setBackground(getResources().getDrawable(R.drawable.background_grey_circle));
                refreshRoundTab.setBackground(getResources().getDrawable(R.drawable.background_darkgrey_circle));
                searchBar.setVisibility(View.GONE);

                progressdialog.setTitle("Refreshing List");
                progressdialog.setMessage("Refreshing contact and vendor information. (This may take a few minutes ...)");
                progressdialog.show();
                readContacts();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Intent intent = getIntent();
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        //finish();
                        //startActivity(intent);
                        //progressdialog.dismiss();
                    }
                },5000);
            }
        });
        addVendorRoundTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundIndex = 1;
                searchRoundTab.setBackground(getResources().getDrawable(R.drawable.background_grey_circle));
                addVendorRoundTab.setBackground(getResources().getDrawable(R.drawable.background_darkgrey_circle));
                refreshRoundTab.setBackground(getResources().getDrawable(R.drawable.background_grey_circle));
                searchBar.setVisibility(View.GONE);
                DialogAddNewVendor dialog = new DialogAddNewVendor();
                Bundle bundle = new Bundle();
                bundle.putString("userId",userId);
                bundle.putString("vendorId", vendorId);
                dialog.setArguments(bundle);
                //dialog.show(getFragmentManager(),"dialog4");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");
        searchBar.setTypeface(type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                try {
                    ContentResolver cr = getContentResolver();
                    Uri contactData = data.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();
                    //Toast.makeText(ActivityMySelectedVendor.this,cursor.getCount(),Toast.LENGTH_SHORT).show();

                    int numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                    String number = cursor.getString(numberIndex);
                    String name = cursor.getString(nameIndex);
                    number = number.replace("+234", "0").replaceAll("\\s+", "");
                    Toast.makeText(ActivityMySelectedVendor.this, name + "--" + number, Toast.LENGTH_SHORT).show();
                    new vendorsUploader(ActivityMySelectedVendor.this).execute(userId, name, number, String.valueOf(vendorType), "0",
                            String.valueOf(workchopUserLocationIndex));
                    addingVendor.show();
                    new updatePoints(ActivityMySelectedVendor.this).execute(userId,"5");
                }
                catch (Exception e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(ActivityMySelectedVendor.this,"Error Adding Contact", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onAdd() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    @Override
    public void onDialogClosed() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
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
                imageButton.setAlpha((float)0.5);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float)1.0);
            }
            return false;
        }

    }

    private class getUserLocation extends AsyncTask<String,Void,String> {
        Context context;

        public getUserLocation(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_location.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
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

                Log.v("VENDORS GOTTEN","VENDORS GOTTEN");

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        workchopUserLocationIndex = Integer.parseInt(sb.toString());
                        new getUserVendors(ActivityMySelectedVendor.this).execute(userId,String.valueOf(vendorType));
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

    private class getUserVendors extends AsyncTask<String,Void,String> {
        Context context;

        public getUserVendors(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/select_user_vendors.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&vendor_type="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
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

                Log.v("VENDORS GOTTEN","VENDORS GOTTEN");

                Handler h = new Handler(Looper.getMainLooper());
                if(sb.toString().equals("none")){
                    progress.dismiss();
                }
                else{
                    h.post(new Runnable() {
                        public void run() {
                            String [] values = sb.toString().split("----");
                            int i = 0;
                            Toast.makeText(context,String.valueOf(values.length),Toast.LENGTH_SHORT).show();
                            if(values == null){

                            }
                            else{
                                vendorRows = new ListSelectedVendor[values.length];
                                for(String key : values){
                                    final String vendorName = key.split("--")[0];
                                    String vendorPhoneNo = key.split("--")[3];
                                    vendorId = key.split("--")[2];
                                    vendorsIds.add(key.split("--")[2]);
                                    final String vendorLocation = getLocation(Integer.parseInt(key.split("--")[1]));
                                    vendorRows[i]= new ListSelectedVendor(vendorName,icon,vendorLocation);
                                    i++;
                                    Log.v("DONE","DONE");
                                }
                                final AdapterSelectedVendor adp = new AdapterSelectedVendor(context,R.layout.row_selectedvendor, vendorRows );
                                selectedVendorList.setAdapter(adp);
                            }

                        }
                    });
                    progress.dismiss();
                }
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
                        progress.dismiss();
                    }
                });
            }
            return null;
        }
    }

    public ArrayList<Integer> findVendors(Map<String, String> vendorList){
        ArrayList<Integer> vendorIndexList = new ArrayList<Integer>();
        int index=0;
        for(Map.Entry<String, String> val : vendorList.entrySet()){
            finalContactsList.add(val.getKey());
            String [] valArray = val.getKey().split(" +");
            for(String value : valArray){
                if(value.toLowerCase().equals("mechanic") || value.toLowerCase().equals("gas") || value.toLowerCase().equals("makeup") ||
                        value.toLowerCase().equals("tailor") || value.toLowerCase().equals("fashion designer")|| value.toLowerCase().equals("fashion") ||
                        value.toLowerCase().equals("hair") || value.toLowerCase().equals("stylist") || value.toLowerCase().equals("barber")){
                    vendorIndexList.add(index);
                    foundVendors.add(val.getKey());
                }
            }
            index++;
        }
        return vendorIndexList;
    }

    public void readContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        contactsDetails = new ArrayList<String>();
        foundVendors = new ArrayList<String>();
        if (cur.getCount() > 0 ) {
            //Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts exist",Toast.LENGTH_SHORT).show();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).replace("-","")
                        .replace("(","")
                        .replace(")", "");
                String phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .replaceAll("\\s+", "")
                        .replace("-","")
                        .replace("(","")
                        .replace(")","");
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER))) > 0) {
                    //Log.v("Contact " + index, "Name : " + name + " -- Phone : " + phone + " -- ID : " + id);
                    String joined = name +" "+ phone.replace("+234","0");
                    contactsDetails.add(joined);
                    contactsName.add(name);
                    contactsNumber.add(phone);
                    contactsIndex.add(index);
                }
                index++;
            }
            sortedContacts = new HashMap<>();
            for(int i=0; i<contactsDetails.size(); i++){
                sortedContacts.put(contactsDetails.get(i),"");
            }
            Collections.sort(contactsDetails);
            Log.v("=============","=================");
            for(int i=0; i<contactsDetails.size(); i++){
                //Log.v("TESTING",contactsDetails.get(i));
            }

            int inn = 1;
            Map<String, String> treeMap = new TreeMap<>(sortedContacts);
            for(Map.Entry<String, String> val : treeMap.entrySet()){
                //Log.v("SORTED NO REPETITION "+inn,val.getValue() + "---" + val.getKey());
                inn++;
            }
            ArrayList<Integer> found = findVendors(treeMap);
            for(int i=0; i<foundVendors.size(); i++) {
                TextView text = new TextView(ActivityMySelectedVendor.this);
                text.setTextColor(Color.rgb(235, 235, 235));
                text.setBackgroundColor(Color.GRAY);
                text.setText("Contact Details -- " + foundVendors.get(i));
                //contactList.addView(text);
            }
            /*Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
                    Toast.LENGTH_SHORT).show();*/
            Toast.makeText(ActivityMySelectedVendor.this,String.valueOf(treeMap.size())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ActivityMySelectedVendor.this,"Contacts don't exist on this device",Toast.LENGTH_SHORT).show();
        }
        uploadUserCotacts2();

    }

    public void uploadUserCotacts (){
        //for(int i=0; i < finalContactsList.size(); i++){
        for(int i=33; i < 37; i++){
            String [] value = finalContactsList.get(i).split(" ");
            //Log.v("CONTACT ID "+i, finalContactsList.get(i));
            String contactName = "";
            String contactNumber = "";
            for(int j=0; j< value.length; j++){
                if(j == (value.length-1)){
                    contactNumber = value[j];
                }
                else{
                    contactName = contactName + " " + value[j];
                }
            }
            new contactsUploader(ActivityMySelectedVendor.this).execute(userId,contactName,contactNumber);
        }

        Toast.makeText(ActivityMySelectedVendor.this,"USER CONTACTS FULLY UPLOADED", Toast.LENGTH_LONG).show();
    }

    public void uploadUserCotacts2 (){
        //for(int i=0; i < finalContactsList.size(); i++){
        uploadUserVendors2();
        for(int i=0; i < finalContactsList.size(); i++){
            String [] value = finalContactsList.get(i).split(" ");
            //Log.v("CONTACT ID "+i, finalContactsList.get(i));
            String contactName = "";
            String contactNumber = "";
            for(int j=0; j< value.length; j++){
                if(j == (value.length-1)){
                    contactNumber = value[j];
                }
                else{
                    contactName = contactName + " " + value[j];
                }
            }
            new contactsUploader2(ActivityMySelectedVendor.this).execute(userId,contactName,contactNumber, String.valueOf(i));
        }

        Toast.makeText(ActivityMySelectedVendor.this,"USER CONTACTS FULLY UPLOADED", Toast.LENGTH_LONG).show();
    }

    private class contactsUploader extends AsyncTask<String,Void,String> {
        Context context;

        public contactsUploader(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_contacts.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&contact_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&contact_number="+URLEncoder.encode(params[2],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                        if(sb.toString().split("--")[0].equals("done")) {
                            //Toast.makeText(context, "TEMPORARY SIGN UP", Toast.LENGTH_LONG).show();
                            uploadUserVendors();
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

    private class contactsUploader2 extends AsyncTask<String,Void,String> {
        Context context;

        public contactsUploader2(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_contacts.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&contact_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&contact_number="+URLEncoder.encode(params[2],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                        if(sb.toString().split("--")[0].equals("done")) {
                            //Toast.makeText(context, "TEMPORARY SIGN UP", Toast.LENGTH_LONG).show();

                        }
                        if(Integer.parseInt(params[3]) == finalContactsList.size()-1){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {progressdialog.dismiss();
                                }
                            },3000);
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

    public void uploadUserVendors(){
        for(int i=0; i < foundVendors.size(); i++){
            String [] value = foundVendors.get(i).split(" ");
            Log.v("INSIDE SORTING VENDORS "+i, foundVendors.get(i));
            String contactName = "";
            int contactType = 0;
            String contactNumber = "";
            for(int j=0; j< value.length; j++){
                if(j == (value.length-1)){
                    contactNumber = value[j];
                }
                else{
                    contactName = contactName + " " + value[j];
                }
            }
            if(contactName.toLowerCase().contains("gas") || contactName.toLowerCase().contains("gas supplier")){
                contactType = 1;
                workchopVendorList1.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("hair") || contactName.toLowerCase().contains("stylist") ||
                    contactName.toLowerCase().contains("hair stylist") || contactName.toLowerCase().contains("barber")){
                contactType = 2;
                workchopVendorList2.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("makeup") || contactName.toLowerCase().contains("make up") ){
                contactType = 3;
                workchopVendorList3.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("mech.") || contactName.toLowerCase().contains("mechanic") ||
                    contactName.toLowerCase().contains("car repair") || contactName.toLowerCase().contains("rewire")){
                contactType = 4;
                workchopVendorList4.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("tailor") || contactName.toLowerCase().contains("fashion")){
                contactType = 5;
                workchopVendorList5.add(contactName + " " + String.valueOf(contactType));
            }
            new vendorsUploader(ActivityMySelectedVendor.this).execute(userId,contactName,contactNumber,String.valueOf(contactType),"0",
                    String.valueOf(workchopUserLocationIndex));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(ActivityMySelectedVendor.this,"User location - "+workchopUserLocationIndex,Toast.LENGTH_LONG).show();
            }
        },2000);
        //Toast.makeText(ActivityMySelectedVendor.this,"USER VENDORS SUCCESSFULLY UPLOADED",Toast.LENGTH_LONG).show();
    }

    public void uploadUserVendors2(){
        for(int i=0; i < foundVendors.size(); i++){
            String [] value = foundVendors.get(i).split(" ");
            Log.v("INSIDE SORTING VENDORS "+i, foundVendors.get(i));
            String contactName = "";
            int contactType = 0;
            String contactNumber = "";
            for(int j=0; j< value.length; j++){
                if(j == (value.length-1)){
                    contactNumber = value[j];
                }
                else{
                    contactName = contactName + " " + value[j];
                }
            }
            if(contactName.toLowerCase().contains("gas") || contactName.toLowerCase().contains("gas supplier")){
                contactType = 1;
                workchopVendorList1.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("hair") || contactName.toLowerCase().contains("stylist") ||
                    contactName.toLowerCase().contains("hair stylist") || contactName.toLowerCase().contains("barber")){
                contactType = 2;
                workchopVendorList2.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("makeup") || contactName.toLowerCase().contains("make up") ){
                contactType = 3;
                workchopVendorList3.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("mech.") || contactName.toLowerCase().contains("mechanic") ||
                    contactName.toLowerCase().contains("car repair") || contactName.toLowerCase().contains("rewire")){
                contactType = 4;
                workchopVendorList4.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("tailor") || contactName.toLowerCase().contains("fashion")){
                contactType = 5;
                workchopVendorList5.add(contactName + " " + String.valueOf(contactType));
            }
            new vendorsUploader2(ActivityMySelectedVendor.this).execute(userId,contactName,contactNumber,String.valueOf(contactType),"0",
                    String.valueOf(workchopUserLocationIndex));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(ActivityMySelectedVendor.this,"User location - "+workchopUserLocationIndex,Toast.LENGTH_LONG).show();
            }
        },2000);
        //Toast.makeText(ActivityMySelectedVendor.this,"USER VENDORS SUCCESSFULLY UPLOADED",Toast.LENGTH_LONG).show();
    }

    private class vendorsUploader extends AsyncTask<String,Void,String> {
        Context context;

        public vendorsUploader(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_vendors.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/update_user_location.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&vendor_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&vendor_number="+URLEncoder.encode(params[2],"UTF-8")
                        +"&vendor_type="+URLEncoder.encode(params[3],"UTF-8")
                        +"&is_vendor_smart="+URLEncoder.encode(params[4],"UTF-8")
                        +"&vendor_location_category="+URLEncoder.encode(params[5],"UTF-8");
                dataUrlParameters2 = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&location_index="+URLEncoder.encode(params[5],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
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
                String [] values = sb.toString().split("--");

                url = new URL(dataUrl2+"?"+dataUrlParameters2);
                Log.v("OLD URL =========", dataUrl+"?"+dataUrlParameters);
                Log.v("NEW URL =========", dataUrl2+"?"+dataUrlParameters2);
                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters2));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line);
                    break;
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        new vendorAsContact(ActivityMySelectedVendor.this).execute(userId,params[1],params[2]);
                        //Toast.makeText(context,"Vendor Successfully Added", Toast.LENGTH_SHORT).show();
                        //onAdd();
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

    private class vendorsUploader2 extends AsyncTask<String,Void,String> {
        Context context;

        public vendorsUploader2(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_vendors.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/update_user_location.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&vendor_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&vendor_number="+URLEncoder.encode(params[2],"UTF-8")
                        +"&vendor_type="+URLEncoder.encode(params[3],"UTF-8")
                        +"&is_vendor_smart="+URLEncoder.encode(params[4],"UTF-8")
                        +"&vendor_location_category="+URLEncoder.encode(params[5],"UTF-8");
                dataUrlParameters2 = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&location_index="+URLEncoder.encode(params[5],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
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
                String [] values = sb.toString().split("--");

                url = new URL(dataUrl2+"?"+dataUrlParameters2);
                Log.v("OLD URL =========", dataUrl+"?"+dataUrlParameters);
                Log.v("NEW URL =========", dataUrl2+"?"+dataUrlParameters2);
                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters2));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line);
                    break;
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        //progressdialog.dismiss();
                        new vendorAsContact(ActivityMySelectedVendor.this).execute(userId,params[1],params[2]);

                        //Toast.makeText(context,"Vendor Successfully Added", Toast.LENGTH_SHORT).show();
                        //onAdd();
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

    private class vendorAsContact extends AsyncTask<String,Void,String> {
        Context context;

        public vendorAsContact(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_contacts.php";
            String dataUrlParameters = null;

            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&contact_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&contact_number="+URLEncoder.encode(params[2],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
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
                String [] values = sb.toString().split("--");

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        if(sb.toString().split("--")[0].equals("done")) {
                            Toast.makeText(context,"Vendor Added As Contact", Toast.LENGTH_SHORT).show();
                            addingVendor.dismiss();

                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            startActivity(intent);
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

    private class deleteVendor extends AsyncTask<String,Void,String> {
        Context context;

        public deleteVendor(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/delete_user_vendor.php";
            String dataUrlParameters = null;

            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&vendor_id="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
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
                    public void run() {
                        if(sb.toString().equals("done")) {
                            Toast.makeText(context,"Vendor Removed", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            startActivity(intent);
                            //onAdd();
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

    public String getLocation(int index){
        String a="";
        if(index == 1){
            a = "<Surulere--Badagry>";
        }
        else if(index == 2){
            a = "<Ikeja--Berger>";
        }
        else if(index == 3){
            a = "<Shomolu--Ilupeju>";
        }
        else if(index == 4){
            a = "<Yaba--Obalende>";
        }
        else if(index == 5){
            a = "<Ojota--Ikorodu>";
        }
        else if(index == 6){
            a = "<V.I--Epe>";
        }
        else if(index == 7){
            a = "<Oshodi--Egbeda>";
        }
        return a;
    }

    private class updatePoints extends AsyncTask<String,Void,String> {
        Context context;

        public updatePoints(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/update_points.php";


            Date date = new Date();
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&adder="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityMySelectedVendor.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()),
                        Toast.LENGTH_LONG).show();
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
                        if(sb.toString().equals("done")){
                            Log.v("USER - ","WORKCHOP POINTS ADDED");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_terms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
