package com.workchopapp.workchop;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.IntegerRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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
import java.util.List;
import java.util.Map;

/**
 * Created by BALE on 14/08/2016.
 */

public class ActivityVendorMain extends AppCompatActivity implements DialogUserSelectLocation.ColorSelected,
        DialogChat.ReturnListener{
    int index;
    Map<String, String> sortedContacts;
    ArrayList<String> contactsName, contactsNumber, contactsDetails, newContactsName, newContactsNumber, foundVendors,
            finalContactsList;
    ArrayList<Integer> contactsIndex, newContactsIndex;
    LinearLayout contactList;

    String vendorName = "";
    String vendorPhoneNo = "";
    String vendorLocation = "";
    String vendorType = "";
    String vendorEmail = "";

    TabHost tabHost;
    TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3;
    ListView vendorProfileList, vendorResultList;
    ListUserProfile [] vendorProfileRows;
    TextView messageRoundTabFrame, exitRoundTabFrame;
    TextView messageRoundTab, exitRoundTab;
    ImageView messageRoundImage, exitRoundImage;
    ProgressDialog progress;
    TextView selectVendorType;
    ImageView selectVendorTypeIcon;
    LinearLayout selectVendorTypeList;
    int selectedIndex, optionsVisible, selectedHalf, selectedQuadrant;
    TextView option1, option2, option3, option4, option5, option6;
    ProgressDialog progressdialog;
    LinearLayout selector;
    View colorIndicator;
    int comboIndex = 0;
    int ll = 0;
    TextView lookingFor;
    boolean gpsEnabled;
    int workchopUserLocationIndex = 0;
    ProgressDialog vendorSearcherDialog;
    ArrayList<String> workchopVendorList, workchopVendorList1, workchopVendorList2, workchopVendorList3, workchopVendorList4,
            workchopVendorList5;
    SQLiteDatabase mydatabase;
    DialogUserSelectLocation dialogUserSelectLocation;
    String vendorId ="";
    ArrayList<String> searchedRows;
    ArrayList<Integer> searchedVendorReviews, searchedContactsOfContactCount;
    Bundle args; String [] vals;
    ArrayList<String> chatNames, chatTimes, chatIds, chatCounts, zeroChats;
    ListView messageList;
    FragmentManager fm;
    int checkNotification = 1;
    ArrayList<ListChats> chats;
    int chatOpen=0;
    String fromScreen="chats", notificationName="";
    int currentChatPosition=-1;
    Handler handler, handler2;
    AdapterChats adp;
    int pollCounter = 1;
    ArrayList<Integer> phoneNotificationIds;
    String currentChatId;
    int totalChatCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vendor_main);
        index = 0;
        fm = getFragmentManager();

        Intent intent = getIntent();

        //intent.putExtra("val7","loginScreen");

        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("",getResources().getDrawable(R.drawable.chat));
        tabSpec1.setContent(R.id.chatTab);
        tabHost.addTab(tabSpec1);
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("",getResources().getDrawable(R.drawable.user_icon_blue));
        tabSpec2.setContent(R.id.userProfileTab);
        tabHost.addTab(tabSpec2);
        vendorProfileList = (ListView)findViewById(R.id.vendorProfileList);
        vendorId = getIntent().getStringExtra("vendorId");

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");


        messageList = (ListView)findViewById(R.id.chatList);
        chatNames = new ArrayList<>();
        chatTimes = new ArrayList<>();
        chatIds = new ArrayList<>();
        chatCounts = new ArrayList<>();
        zeroChats = new ArrayList<>();



        dialogUserSelectLocation = new DialogUserSelectLocation();
        File database=getApplicationContext().getDatabasePath("rubbish.db");

        if (!database.exists() && getIntent().getStringExtra("val7").equals("signupScreen")) {
            Toast.makeText(ActivityVendorMain.this, "Sample Database not Found", Toast.LENGTH_LONG).show();
            mydatabase  = openOrCreateDatabase("rubbish.db",MODE_PRIVATE,null);
            dialogUserSelectLocation.show(getFragmentManager(),"dialog14");
            vendorId = intent.getStringExtra("val4");
            Log.v("I HAVE ENTERED","CASE 1");
        }
        else if (!database.exists() && getIntent().getStringExtra("val7").equals("loginScreen")){
            vendorName = intent.getStringExtra("val1");
            vendorPhoneNo = intent.getStringExtra("val2");
            vendorLocation = intent.getStringExtra("val3");
            vendorId = intent.getStringExtra("val4");
            vendorType = intent.getStringExtra("val5");
            vendorEmail = intent.getStringExtra("val6");
            Log.v("I HAVE ENTERED","CASE 2");
            progress = new ProgressDialog(ActivityVendorMain.this);
            progress.setTitle("Loading");
            progress.show();
            //phoneNoToFile();
            new getChats(ActivityVendorMain.this).execute(vendorId);
        }
        else if (database.exists() && getIntent().getStringExtra("val7").equals("signupScreen")){
            Toast.makeText(ActivityVendorMain.this, "Sample Database Found", Toast.LENGTH_LONG).show();
            mydatabase  = openOrCreateDatabase("rubbish.db",MODE_PRIVATE,null);
            dialogUserSelectLocation.show(getFragmentManager(),"dialog14");
            vendorId = intent.getStringExtra("val4");
            Log.v("I HAVE ENTERED","CASE 3");
        }
        else if(getIntent().getStringExtra("val7").equals("notification")){
            checkNotification = getIntent().getIntExtra("check",0);
            Log.v("I HAVE ENTERED","CASE 4");
            DialogChatVendor newFragment = new DialogChatVendor();
            Bundle bundle = new Bundle();
            vendorId = getIntent().getStringExtra("vendorId");
            bundle.putString("vendorId", getIntent().getStringExtra("vendorId"));
            bundle.putString("userId", getIntent().getStringExtra("userId"));
            bundle.putString("userName", getIntent().getStringExtra("name"));
            newFragment.setArguments(bundle);
            newFragment.show(fm, "dialog11");
            progress = new ProgressDialog(ActivityVendorMain.this);
            progress.setTitle("Loading");
            progress.show();
        }
        else{
            vendorName = intent.getStringExtra("val1");
            vendorPhoneNo = intent.getStringExtra("val2");
            vendorLocation = intent.getStringExtra("val3");
            vendorId = intent.getStringExtra("val4");
            vendorType = intent.getStringExtra("val5");
            vendorEmail = intent.getStringExtra("val6");
            Log.v("I HAVE ENTERED","CASE 5");
            progress = new ProgressDialog(ActivityVendorMain.this);
            progress.setTitle("Loading");
            progress.show();
            //phoneNoToFile();
            new getChats(ActivityVendorMain.this).execute(vendorId);
        }

        new setLoggedIn(ActivityVendorMain.this).execute(vendorId);



        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                messageList.getChildAt(position).findViewById(R.id.chatCount).setVisibility(View.INVISIBLE);
                TextView tv = (TextView)messageList.getChildAt(position).findViewById(R.id.chatCount);
                tv.setText("0");
                //adp.notifyDataSetChanged();
                tv.setVisibility(View.INVISIBLE);
                chatOpen = 1;
                currentChatPosition = position;
                chats.set(position,new ListChats(chatNames.get(position), R.drawable.chat, chatTimes.get(position), 0));
                DialogChatVendor newFragment = new DialogChatVendor();
                Bundle bundle = new Bundle();
                bundle.putString("vendorId", vendorId);
                bundle.putString("userId", chatIds.get(position));
                currentChatId = chatIds.get(position);
                bundle.putString("userName", chatNames.get(position));
                newFragment.setArguments(bundle);
                newFragment.show(fm, "dialog11");
            }
        });

        final View rootView = this.getLayoutInflater().inflate(R.layout.row_userprofile, null);
        View view = rootView.getRootView();


        float density = getResources().getDisplayMetrics().density;

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabHost.getCurrentTab()) {
                    case 0:
                        tabHost.setCurrentTab(0);
                        break;
                    case 1:
                        tabHost.setCurrentTab(1);
                        break;
                    default:
                        break;
                }
            }
        });

        vendorProfileRows = new ListUserProfile[]{new ListUserProfile("About",R.drawable.aboutvendor),
                new ListUserProfile("Account",R.drawable.account), new ListUserProfile("Change Password",R.drawable.password ),
                new ListUserProfile("Feedback",R.drawable.feedback), new ListUserProfile("Rate App",R.drawable.rate),
                new ListUserProfile("Report An Issue",R.drawable.report ), new ListUserProfile("Share",R.drawable.shareicon),
                new ListUserProfile("Terms and Privacy Notice",R.drawable.privacy)};

        final AdapterUserProfile adp = new AdapterUserProfile(view.getContext(),R.layout.row_userprofile, vendorProfileRows );
        vendorProfileList.setAdapter(adp);
        vendorProfileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Intent intent = new Intent(ActivityVendorMain.this, ActivityAccountVendor.class);
                    intent.putExtra("vendorId",vendorId);
                    startActivity(intent);
                }
                else if(position == 2){
                    Intent intent = new Intent(ActivityVendorMain.this, ActivityChangePasswordVendor.class);
                    intent.putExtra("vendorId",vendorId);
                    startActivity(intent);
                }
                else if(position == 3){
                    Intent intent = new Intent(ActivityVendorMain.this, ActivityFeedbackVendor.class);
                    intent.putExtra("vendorId",vendorId);
                    startActivity(intent);
                }
                else if(position == 4){
                    DialogRateAppVendor rateFragment = new DialogRateAppVendor();
                    Bundle bundle = new Bundle();
                    bundle.putString("vendorId",vendorId);
                    rateFragment.setArguments(bundle);
                    rateFragment.show(getFragmentManager(), "dialog9");
                }
                else if(position == 5){
                    Intent intent = new Intent(ActivityVendorMain.this, ActivityReportIssueVendor.class);
                    intent.putExtra("vendorId",vendorId);
                    startActivity(intent);
                }
                else if(position == 6){
                    DialogShare dialog = new DialogShare();
                    dialog.show(getFragmentManager(), "dialog33");
                }
                else if(position == 7){
                    String url = "http://www.workchopapp.com/terms.php";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                else if(position == 0){
                    DialogAbout aboutFragment = new DialogAbout();
                    aboutFragment.show(getFragmentManager(), "dialog29");
                }
            }
        });


        WindowManager wm = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        LinearLayout selectorFrame = (LinearLayout)findViewById(R.id.selectorFrame);
        FrameLayout selectVendorTypeFrame = (FrameLayout)findViewById(R.id.selectVendorTypeFrame);

        Resources r = getResources();
        float widthDp = (float)width/density;
        if(widthDp < 360){

        }

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pollCaller();
                handler.postDelayed(this,10000);
            }
        },2000);

        handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                pollCaller2();
                handler2.postDelayed(this,30000);
            }
        },5000);

        new phoneNoToFile2(ActivityVendorMain.this).execute(vendorId);


    }

    public void phoneNoToFile(){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Workchop", "files");
            //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists())
            {
                root.mkdirs();
            }
            File gpxfile = new File(root, "phone_number_vendor.txt");

            FileWriter writer = new FileWriter(gpxfile,false);
            writer.append(vendorPhoneNo);
            writer.flush();
            writer.close();
            //Toast.makeText(this, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    private class phoneNoToFile2 extends AsyncTask<String, Void, String> {
        Context context;
        public phoneNoToFile2(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_vendor_details.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;

            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                        vendorPhoneNo = sb.toString().split("--")[1];
                        phoneNoToFile();
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

    public void pollCaller(){
        try {
            //Log.v("CHATS SIZE", String.valueOf(chats.size()));
            new pollingClass(ActivityVendorMain.this).execute(vendorId);
            Log.v("CALLING POLL 2", "POLL " + pollCounter);
        }
        catch(NullPointerException e){
            Log.v("ERROR CAUSE", e.toString());
            Toast.makeText(ActivityVendorMain.this,"No Chats",Toast.LENGTH_SHORT).show();
        }
    }

    public void pollCaller2(){
        try {
            //Log.v("CHATS SIZE", String.valueOf(chats.size()));
            new pollingUserIsVendorClass(ActivityVendorMain.this).execute(vendorPhoneNo,"2");
            Log.v("CALLING POLL 2", "POLL ");
        }
        catch(NullPointerException e){
            Log.v("ERROR CAUSE", e.toString());
            Toast.makeText(ActivityVendorMain.this," ",Toast.LENGTH_SHORT).show();
        }
    }

    public void createNotification(String userid, String name, String message, int id) {
        Intent intent = new Intent(this, ActivityVendorMain.class);
        intent.putExtra("val7","notification");
        intent.putExtra("name",name);
        intent.putExtra("userId",userid);
        intent.putExtra("vendorId",vendorId);
        intent.putExtra("check", 0);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Workchop")
                .setContentText(message).setSmallIcon(R.drawable.workchopphoneicon_notification)
                .setContentIntent(pIntent).build();
                //.addAction(R.drawable.icon, "Call", pIntent)
                //.addAction(R.drawable.icon, "More", pIntent)
                //.addAction(R.drawable.icon, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, noti);
    }

    @Override
    public void onReturned(int changed) {
        chatOpen = 0;
        if(changed == 1){
            //Intent intent = getIntent();
            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //finish();
            //startActivity(intent);
        }
        currentChatPosition = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler2.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
    }

    private class pollingClass extends AsyncTask<String, Void, String> {
        Context context;
        public pollingClass(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            final String dataUrl2 = "http://workchopapp.com/mobile_app/get_vendor_chat_list2.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            try {
                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters));
                Log.v("URI", dataUrl2+"?"+dataUrlParameters);
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }

                in2.close();

                Handler h = new Handler(Looper.getMainLooper());
                final String finalDataUrlParameters = dataUrlParameters;
                h.post(new Runnable() {
                    public void run() {
                        //Log.v("full text",sb.toString());
                        if(sb2.toString().contains("------")) {
                            String[] allChats = sb2.toString().split("------");
                            chatCounts = new ArrayList<String>();
                            chatTimes = new ArrayList<String>();
                            chatNames = new ArrayList<String>();
                            chatIds = new ArrayList<String>();

                            int tempTotalChatCount = 0;
                            int beep = 0;

                            for (int i = 0; i < allChats.length; i++) {
                                String[] chatRows = allChats[i].split("--");
                                chatNames.add(chatRows[1]);
                                chatTimes.add(chatRows[2]);
                                chatIds.add(chatRows[0]);
                                chatCounts.add(chatRows[3]);
                                tempTotalChatCount = tempTotalChatCount + Integer.parseInt(chatRows[3]);
                                //Log.v("URI", dataUrl2 + "?" + finalDataUrlParameters);
                                Log.v("CHAT COUNT", chatCounts.get(i) + "");
                            }
                            //chats = new ArrayList<ListChats>();
                            for(int i=0; i<=chats.size(); i++){
                                chats.remove(0);
                            }
                            chats.clear();
                            adp.notifyDataSetChanged();

                            for (int i = 0; i < chatNames.size(); i++) {
                                chats.add(new ListChats(chatNames.get(i), R.drawable.chat, getDateFormat(chatTimes.get(i)),
                                        Integer.parseInt(chatCounts.get(i))));
                            }
                            adp.notifyDataSetChanged();
                            for(int i=0; i<chatCounts.size(); i++){
                                Log.v("TOTAL CHAT COUNT",String.valueOf(totalChatCount));
                                if(chatIds.get(i).equals(currentChatId)){

                                }
                                else if(totalChatCount > 0 && totalChatCount < tempTotalChatCount ){
                                    if(Integer.parseInt(chatCounts.get(i)) > 0){
                                        beep = 1;
                                    }
                                }
                            }
                            if(tempTotalChatCount == 0){
                                totalChatCount = tempTotalChatCount;
                                beep = 0;
                            }
                            else if(totalChatCount != tempTotalChatCount){
                                totalChatCount = tempTotalChatCount;
                                beep = 1;
                            }
                            else{
                                beep = 0;
                            }
                            if(beep == 1 && totalChatCount > 0){
                                MediaPlayer mp = MediaPlayer.create(context, R.raw.beep1);
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(400);
                                if (mp != null) {
                                    mp.release();
                                }
                                mp = MediaPlayer.create(context, R.raw.beep1);
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.release();
                                    }
                                });
                            }

                            /*
                            if (fromScreen.equals("notification")){
                                fromScreen = "chats";
                                for (int j = 0; j < messageList.getCount(); j++) {
                                    TextView child = (TextView) messageList.getChildAt(j).findViewById(R.id.vendorName);
                                    TextView child2 = (TextView) messageList.getChildAt(j).findViewById(R.id.chatCount);
                                    currentChatPosition = j;
                                    if(notificationName.equals(child.getText().toString())){
                                        child2.setText("0");
                                        child2.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                            else if(fromScreen.equals("chats")) {
                                for (int i = 0; i < chatCount.size(); i++) {
                                    for (int j = 0; j < messageList.getCount(); j++) {
                                        try {
                                            TextView child = (TextView) messageList.getChildAt(j).findViewById(R.id.vendorName);
                                            TextView child2 = (TextView) messageList.getChildAt(j).findViewById(R.id.chatCount);
                                            //child2.setVisibility(View.VISIBLE);
                                            if (child.getText().toString().equals(chatNames.get(i)) && j!= currentChatPosition) {
                                                if (child2.getText().toString().equals(chatCount.get(i))) {
                                                    Log.v("COMPARING - EQUAL", child2.getText().toString() + "--" + chatCount.get(i));
                                                }
                                                else {
                                                    Log.v("COMPARING - NOT EQUAL", child2.getText().toString() + "--" + chatCount.get(i));
                                                    MediaPlayer mp = MediaPlayer.create(context, R.raw.beep1);
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    v.vibrate(400);
                                                    if (mp != null) {
                                                        mp.release();
                                                    }
                                                    mp = MediaPlayer.create(context, R.raw.beep1);
                                                    mp.start();
                                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                        public void onCompletion(MediaPlayer mp) {
                                                            mp.release();
                                                        }
                                                    });
                                                    if (Integer.parseInt(chatCount.get(i)) > 0) {
                                                        child2.setVisibility(View.VISIBLE);
                                                        child2.setText(chatCount.get(i));
                                                    }
                                                    else if (Integer.parseInt(chatCount.get(i)) == 0) {
                                                        child2.setVisibility(View.INVISIBLE);
                                                    }
                                                    //adp.notifyDataSetChanged();
                                                }
                                            }
                                        } catch (NullPointerException e) {

                                        }
                                    }
                                }
                            }
                            */

                            Log.v("NEW ADAPTER","SET FOR LIST");
                        }
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

    private class getChats extends AsyncTask<String, Void, String> {
        Context context;
        public getChats(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_vendor_chat_list.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/get_vendor_chat_list2.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;

            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
            try{
                /*
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
                }*/

                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters));
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
                        if(sb2.toString().equals("false")){
                            progress.dismiss();
                        }
                        else {
                            if(sb2.toString().contains("------")) {
                                String[] allChats = sb2.toString().split("------");
                                ArrayList<String> seenNotification = new ArrayList<String>();

                                for (int i = 0; i < allChats.length; i++) {
                                    String[] chatRows = allChats[i].split("--");
                                    chatNames.add(chatRows[1]);
                                    chatTimes.add(chatRows[2]);
                                    chatIds.add(chatRows[0]);
                                    chatCounts.add(chatRows[3]);
                                    //seenNotification.add(chatRows[4]);
                                }
                                chats = new ArrayList<ListChats>();
                                for (int i = 0; i < chatNames.size(); i++) {
                                    Log.v("COUNT", chatCounts.get(i));
                                    chats.add(new ListChats(chatNames.get(i), R.drawable.chat, getDateFormat(chatTimes.get(i)),
                                            Integer.parseInt(chatCounts.get(i))));
                                /*
                                if(checkNotification == 1) {
                                    createNotification(chatIds.get(i), chatNames.get(i),
                                            chatCounts.get(i) + " new messages from " + chatNames.get(i), i);
                                }*/
                                }
                                adp = new AdapterChats(getApplicationContext(), R.layout.row_chats, chats);
                                messageList.setAdapter(adp);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                for (int i = 0; i < chatNames.size(); i++) {
                                    //notificationManager.cancel(i);

                                }
                            }
                            progress.dismiss();
                        }
                    }
                });
                in2.close();
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

    private class pollingUserIsVendorClass extends AsyncTask<String, Void, String> {
        Context context;
        public pollingUserIsVendorClass(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/user_is_vendor.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;

            try {
                dataUrlParameters = "phone_no="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&mode="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
            try{

                url = new URL(dataUrl+"?"+dataUrlParameters);
                Log.v("URL",dataUrl+"?"+dataUrlParameters);
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
                        if(sb.toString().equals("false")){

                        }
                        else {
                            Toast.makeText(context, sb.toString()+" New Message(s) in your User Account", Toast.LENGTH_SHORT).show();
                            MediaPlayer mp = MediaPlayer.create(context, R.raw.beep1);
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(400);
                            if (mp != null) {
                                mp.release();
                            }
                            mp = MediaPlayer.create(context, R.raw.beep1);
                            mp.start();
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }
                            });
                        }
                    }
                });
                in.close();
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(ActivityVendorMain.this)
                .setIcon(R.drawable.exit)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        handler.removeCallbacksAndMessages(null);
                        handler2.removeCallbacksAndMessages(null);
                        new setLoggedOut(ActivityVendorMain.this).execute(vendorId);
                        ActivityVendorMain.this.finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onSelected2(int color, int selectedQuadrant) {
        workchopUserLocationIndex = selectedQuadrant;
        new vendorLocationUploader(ActivityVendorMain.this).execute(vendorId,String.valueOf(workchopUserLocationIndex));
    }

    @Override
    public void done2() {

    }

    @Override
    public void onDestroy2() {
        if(workchopUserLocationIndex == 0){
            dialogUserSelectLocation.show(getFragmentManager(),"dialog14");
        }
    }

    private class setLoggedIn extends AsyncTask<String,Void,String> {
        Context context;

        public setLoggedIn(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/set_logged_in_vendor.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityVendorMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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

    private class setLoggedOut extends AsyncTask<String,Void,String> {
        Context context;

        public setLoggedOut(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/set_logged_out_vendor.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityVendorMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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

    private class vendorLocationUploader extends AsyncTask<String,Void,String> {
        Context context;

        public vendorLocationUploader(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl2 = "http://workchopapp.com/mobile_app/update_vendor_location.php";
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters2 = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&location_index="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(ActivityVendorMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
            try{

                url = new URL(dataUrl2+"?"+dataUrlParameters2);
                Log.v("NEW URL =========", dataUrl2+"?"+dataUrlParameters2);
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
                        Toast.makeText(ActivityVendorMain.this,"Location Updated",Toast.LENGTH_SHORT).show();
                    }
                });
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

    public String getDateFormat(String date){
        String newFormat = "";
        String datePart = date.split(" ")[0];
        String timePart = date.split(" ")[1];
        String year = datePart.split("-")[0];
        String month = datePart.split("-")[1];
        String day = datePart.split("-")[2];
        String dayString = day;
        String [] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String monthString = months[Integer.parseInt(month)-1];

        String hour = timePart.split(":")[0];
        String timeSector = "am";
        if(Integer.parseInt(hour)>12){
            hour = String.valueOf(Integer.parseInt(hour)-12);
            timeSector = "pm";
        }
        String minute = timePart.split(":")[1];
        String second = timePart.split(":")[2];

        newFormat = monthString+"-"+dayString+" | "+hour+":"+minute+""+timeSector;
        return newFormat;
    }

    private class RoundTabHighlighterOnTouchListener implements View.OnTouchListener {
        //This
        final TextView imageButton;

        public RoundTabHighlighterOnTouchListener(final TextView imageButton) {
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
                imageButton.setAlpha((float)0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float) 1.0);
            }
            return false;
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
