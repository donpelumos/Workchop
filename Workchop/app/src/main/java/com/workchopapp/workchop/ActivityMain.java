package com.workchopapp.workchop;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by BALE on 13/07/2016.
 */
public class ActivityMain extends AppCompatActivity implements DialogLocationSelector.ColorSelected,
        DialogUserSelectLocation.ColorSelected {
    int index;
    Map<String, String> sortedContacts;
    ArrayList<String> contactsName, contactsNumber, contactsDetails, newContactsName, newContactsNumber, foundVendors, foundVendorsCount,
            finalContactsList, foundVendors2, finalContactsList2;
    ArrayList<Integer> contactsIndex, newContactsIndex;
    LinearLayout contactList;
    TabHost tabHost;
    TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3;
    ListView userProfileList, vendorResultList, recentList;
    ListUserProfile [] userProfileRows;
    TextView messageRoundTabFrame, exitRoundTabFrame;
    TextView messageRoundTab, exitRoundTab;
    ImageView messageRoundImage, exitRoundImage;
    ProgressDialog progress, backupProgress;
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
    String userId;
    int workchopUserLocationIndex;
    ProgressDialog vendorSearcherDialog;
    ArrayList<String> workchopVendorList, workchopVendorList1, workchopVendorList2, workchopVendorList3, workchopVendorList4,
            workchopVendorList5;
    SQLiteDatabase mydatabase;
    DialogUserSelectLocation dialogUserSelectLocation;
    ArrayList<String> searchedRows, recentIds;
    ArrayList<Integer> searchedVendorReviews, searchedContactsOfContactCount;
    Bundle args; String [] vals;
    ArrayList<String> chatNames, chatTimes, chatIds, chatCount;
    int checkNotification = 1;
    ArrayList<Integer> phoneNotificationIds;
    String loggedPhoneNo = "";
    Handler handler, handlerChats;
    ArrayAdapter adapterRecent;
    int totalChatCount = 0;
    int freshSignIn =0;
    TextView chatNotifierCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        index = 0;
        gpsEnabled = isLocationServiceEnabled();
        chatNotifierCount = (TextView)findViewById(R.id.chatNotifierCount);
        searchedContactsOfContactCount = new ArrayList<>();

        contactsName = new ArrayList<String>();contactsNumber = new ArrayList<String>();
        newContactsName = new ArrayList<String>();newContactsNumber = new ArrayList<String>();
        contactsIndex = new ArrayList<Integer>();newContactsIndex = new ArrayList<Integer>();
        selectedIndex = 0;  optionsVisible = 0;
        selectVendorType = (TextView)findViewById(R.id.selectVendorType);
        selectVendorTypeIcon = (ImageView)findViewById(R.id.selectVendorTypeIcon);
        selectVendorTypeList = (LinearLayout)findViewById(R.id.selectVendorTypeList);
        recentList = (ListView)findViewById(R.id.recentList);
        selector = (LinearLayout)findViewById(R.id.selector);
        vendorResultList = (ListView)findViewById(R.id.vendorResultList);
        tabHost = (TabHost)findViewById(R.id.tabHost);
        phoneNotificationIds = new ArrayList<>();
        recentIds = new ArrayList<>();
        tabHost.setup();
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("",getResources().getDrawable(R.drawable.search_icon_blue));
        tabSpec1.setContent(R.id.searchTab);
        tabHost.addTab(tabSpec1);
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("",getResources().getDrawable(R.drawable.recent_icon_blue));
        tabSpec2.setContent(R.id.recentTab);
        tabHost.addTab(tabSpec2);
        tabSpec3 = tabHost.newTabSpec("profile");
        tabSpec3.setIndicator("",getResources().getDrawable(R.drawable.user_icon_blue));
        tabSpec3.setContent(R.id.userProfileTab);
        tabHost.addTab(tabSpec3);
        option1 = (TextView)findViewById(R.id.option1);
        option2 = (TextView)findViewById(R.id.option2);
        option3 = (TextView)findViewById(R.id.option3);
        option4 = (TextView)findViewById(R.id.option4);
        option5 = (TextView)findViewById(R.id.option5);
        option6 = (TextView)findViewById(R.id.option6);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");
        option1.setTypeface(type);
        option2.setTypeface(type);
        option3.setTypeface(type);
        option4.setTypeface(type);
        option5.setTypeface(type);
        option6.setTypeface(type);
        selectVendorType.setTypeface(type);
        userProfileList = (ListView)findViewById(R.id.userProfileList);
        lookingFor = (TextView)findViewById(R.id.lookingFor);
        TextView textIn = (TextView)findViewById(R.id.textIn);
        textIn.setTypeface(type);
        gpsEnabled = isLocationEnabled(ActivityMain.this);

        dialogUserSelectLocation = new DialogUserSelectLocation();

        workchopUserLocationIndex = 0;

        finalContactsList = getIntent().getStringArrayListExtra("finalContactsList");
        foundVendors = getIntent().getStringArrayListExtra("foundVendors");
        userId = getIntent().getStringExtra("userId");
        workchopVendorList = new ArrayList<>();
        workchopVendorList1 = new ArrayList<>();
        workchopVendorList2 = new ArrayList<>();
        workchopVendorList3 = new ArrayList<>();
        workchopVendorList4 = new ArrayList<>();
        workchopVendorList5 = new ArrayList<>();

        chatNames = new ArrayList<>();
        chatTimes = new ArrayList<>();
        chatIds = new ArrayList<>();
        chatCount = new ArrayList<>();

        File database=getApplicationContext().getDatabasePath("rubbish.db");

        if (!database.exists() && getIntent().getStringExtra("val7").equals("signupScreen")) {
            ////Toast.makeText(ActivityMain.this, "Sample Database not Found", Toast.LENGTH_SHORT).show();
            mydatabase  = openOrCreateDatabase("rubbish.db",MODE_PRIVATE,null);
            dialogUserSelectLocation.show(getFragmentManager(),"dialog14");
            freshSignIn = 1;
            Log.v("I AM INSIDE","1");
        }
        else if (!database.exists() && getIntent().getStringExtra("val7").equals("loginScreen")){
            ////Toast.makeText(ActivityMain.this, "Sample Database Found", Toast.LENGTH_SHORT).show();
            workchopUserLocationIndex = Integer.parseInt(getIntent().getStringExtra("val5"));
            userId = getIntent().getStringExtra("val6");
            loggedPhoneNo = getIntent().getStringExtra("val3");
            ////Toast.makeText(ActivityMain.this,"USER ID - " +userId,Toast.LENGTH_SHORT).show();
            new getVendorTypes2(ActivityMain.this).execute("");
            Log.v("I AM INSIDE","2");
            //phoneNoToFile();
            new getChats(ActivityMain.this).execute(userId);
        }
        else if (database.exists() && getIntent().getStringExtra("val7").equals("signupScreen")){
            ////Toast.makeText(ActivityMain.this, "Sample Database Found", Toast.LENGTH_SHORT).show();
            mydatabase  = openOrCreateDatabase("rubbish.db",MODE_PRIVATE,null);
            dialogUserSelectLocation.show(getFragmentManager(),"dialog14");
            freshSignIn = 1;
            Log.v("I AM INSIDE","3");
        }
        else if(getIntent().getStringExtra("val7").equals("notification")){
            checkNotification = getIntent().getIntExtra("check",0);
            DialogChat newFragment = new DialogChat();
            Bundle bundle = new Bundle();
            userId = getIntent().getStringExtra("userId");
            bundle.putString("vendorId", getIntent().getStringExtra("vendorId"));
            bundle.putString("userId", getIntent().getStringExtra("userId"));
            bundle.putString("userName", getIntent().getStringExtra("name"));
            newFragment.setArguments(bundle);
            newFragment.show(getFragmentManager(), "dialog24");
            Log.v("I AM INSIDE","4");
        }
        else{
            ////Toast.makeText(ActivityMain.this, "Sample Database Found", Toast.LENGTH_SHORT).show();
            workchopUserLocationIndex = Integer.parseInt(getIntent().getStringExtra("val5"));
            userId = getIntent().getStringExtra("val6");
            loggedPhoneNo = getIntent().getStringExtra("val3");
            ////Toast.makeText(ActivityMain.this,"USER ID - " +userId,Toast.LENGTH_SHORT).show();
            new getVendorTypes2(ActivityMain.this).execute("");
            Log.v("I AM INSIDE","5");
            //phoneNoToFile();
            new getChats(ActivityMain.this).execute(userId);
        }

        new setLoggedIn(ActivityMain.this).execute(userId);

        lookingFor.setTypeface(type);
        //new getLocation(ActivityMain.this).execute("");


        final View rootView = this.getLayoutInflater().inflate(R.layout.row_userprofile, null);
        View view = rootView.getRootView();
        selectVendorTypeList.setVisibility(View.GONE);

        float density = getResources().getDisplayMetrics().density;



        selectVendorType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //grey color filter, you can change the color as you like
                    selectVendorTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.downblue));
                    selectVendorType.setAlpha((float)0.8);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    selectVendorTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.down));
                    selectVendorType.setAlpha((float)1.0);
                }
                return false;
            }
        });
        selectVendorTypeIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //grey color filter, you can change the color as you like
                    selectVendorTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.downblue));
                    selectVendorType.setAlpha((float)0.8);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    selectVendorTypeIcon.setImageDrawable(getResources().getDrawable(R.drawable.down));
                    selectVendorType.setAlpha((float)1.0);
                }
                return false;
            }
        });
        selectVendorType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optionsVisible == 0){
                    optionsVisible = 1;
                    selectVendorTypeList.setVisibility(View.VISIBLE);
                }
                else{
                    optionsVisible = 0;
                    selectVendorTypeList.setVisibility(View.GONE);
                }
            }
        });
        selectVendorTypeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optionsVisible == 0){
                    optionsVisible = 1;
                    selectVendorTypeList.setVisibility(View.VISIBLE);
                }
                else{
                    optionsVisible = 0;
                    selectVendorTypeList.setVisibility(View.GONE);
                }
            }
        });


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
                    case 2:
                        tabHost.setCurrentTab(2);
                        break;

                    default:

                        break;
                }
            }
        });
        //tabHost.setCurrentTab(0);

        //NO NEED TO READ AS CONTACTS ARE READ DURING SIGN UP
        //readContacts();

        userProfileRows = new ListUserProfile[]{
                new ListUserProfile("About",R.drawable.aboutvendor),
                new ListUserProfile("Account",R.drawable.account), new ListUserProfile("Change Password",R.drawable.password ),
                new ListUserProfile("Feedback",R.drawable.feedback),
                new ListUserProfile("My Vendors",R.drawable.myvendors), new ListUserProfile("Rate App",R.drawable.rate),
                new ListUserProfile("Report An Issue",R.drawable.report ), new ListUserProfile("Share",R.drawable.shareicon),
                new ListUserProfile("Work Points",R.drawable.reward),
                new ListUserProfile("Terms and Privacy Notice",R.drawable.privacy)};

        final AdapterUserProfile adp = new AdapterUserProfile(view.getContext(),R.layout.row_userprofile, userProfileRows );
        userProfileList.setAdapter(adp);
        userProfileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4){
                    Intent intent = new Intent(ActivityMain.this, ActivityMyVendors.class);
                    intent.putStringArrayListExtra("foundVendorList",foundVendors);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
                else if(position == 1){
                    Intent intent = new Intent(ActivityMain.this, ActivityAccount.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                }
                else if(position == 2){
                    Intent intent = new Intent(ActivityMain.this, ActivityChangePassword.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                }
                else if(position == 3){
                    Intent intent = new Intent(ActivityMain.this, ActivityFeedback.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                }
                else if(position == 5){
                    DialogRateApp rateFragment = new DialogRateApp();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId",userId);
                    rateFragment.setArguments(bundle);
                    rateFragment.show(getFragmentManager(), "dialog9");
                }
                else if(position == 0){
                    DialogAbout aboutFragment = new DialogAbout();
                    aboutFragment.show(getFragmentManager(), "dialog29");
                }
                else if(position == 6){
                    Intent intent = new Intent(ActivityMain.this, ActivityReportIssue.class);
                    intent.putExtra("userId",userId);
                    startActivity(intent);
                }
                else if(position == 7){
                    DialogShare dialog = new DialogShare();
                    dialog.show(getFragmentManager(), "dialog33");
                }
                else if(position == 8){
                    DialogPoints dialog = new DialogPoints();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId",userId);
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "dialog32");
                }
                else if(position == 9){
                    String url = "http://www.workchopapp.com/terms.php";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                else if(position == 10){
                    /*new AlertDialog.Builder(ActivityMain.this)
                            .setIcon(R.drawable.signout)
                            .setTitle("Sign Out")
                            .setMessage("Are you sure you want to sign out?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                    deleteDatabase("workchop_user_account.db");
                                    ActivityMain.this.finishAffinity();
                                    new setLoggedOut(ActivityMain.this).execute(userId);
                                    //Intent intent = new Intent(ActivityMain.this, ActivityLogin.class);
                                    //startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();*/
                }
            }
        });

        messageRoundTab = (TextView)findViewById(R.id.messageRoundTab);
        exitRoundTab = (TextView)findViewById(R.id.exitRoundTab2);
        messageRoundTabFrame = (TextView)findViewById(R.id.messageRoundTabFrame);
        exitRoundTabFrame = (TextView)findViewById(R.id.exitRoundTabFrame2);
        messageRoundTab.setOnTouchListener(new ActivityMain.RoundTabHighlighterOnTouchListener(messageRoundTab));
        exitRoundTab.setOnTouchListener(new ActivityMain.RoundTabHighlighterOnTouchListener(exitRoundTab));
        messageRoundImage = (ImageView)findViewById(R.id.messageRoundImage);
        exitRoundImage = (ImageView)findViewById(R.id.exitRoundImage);
        messageRoundImage.setOnTouchListener(new ImageHighlighterOnTouchListener(messageRoundImage));
        exitRoundImage.setOnTouchListener(new ImageHighlighterOnTouchListener(exitRoundImage));
        exitRoundTab.setVisibility(View.GONE);

        messageRoundTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //messageRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                //exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                Intent intent = new Intent(ActivityMain.this, ActivityChats.class);
                intent.putExtra("userId", userId);
                intent.putIntegerArrayListExtra("notificationIds",phoneNotificationIds);
                chatNotifierCount.setVisibility(View.GONE);
                startActivity(intent);
            }
        });
        messageRoundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //messageRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                //exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                Intent intent = new Intent(ActivityMain.this, ActivityChats.class);
                intent.putExtra("userId", userId);
                intent.putIntegerArrayListExtra("notificationIds",phoneNotificationIds);
                chatNotifierCount.setVisibility(View.GONE);
                startActivity(intent);
            }
        });
        exitRoundTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //messageRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                //exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                /*new AlertDialog.Builder(ActivityMain.this)
                        .setIcon(R.drawable.exit_new)
                        .setTitle("Exit Application")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                ActivityMain.this.finishAffinity();
                                //new setLoggedOut(ActivityMain.this).execute(userId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();*/
            }
        });
        exitRoundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                messageRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                new AlertDialog.Builder(ActivityMain.this)
                        .setIcon(R.drawable.exit)
                        .setTitle("Exit Application")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                ActivityMain.this.finishAffinity();
                                //new setLoggedOut(ActivityMain.this).execute(userId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();*/
            }
        });

        option1.setOnTouchListener(new TextHighlighterOnTouchListener(option1));
        option2.setOnTouchListener(new TextHighlighterOnTouchListener(option2));
        option3.setOnTouchListener(new TextHighlighterOnTouchListener(option3));
        option4.setOnTouchListener(new TextHighlighterOnTouchListener(option4));
        option5.setOnTouchListener(new TextHighlighterOnTouchListener(option5));
        option6.setOnTouchListener(new TextHighlighterOnTouchListener(option6));
        progressdialog = new ProgressDialog(this);
        progressdialog.setTitle("Searching For Vendors");
        progressdialog.setMessage("searching...");
        selector.setEnabled(true);
        selector.setVisibility(View.GONE);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectVendorType.setText(option1.getText());
                comboIndex = 1;
                optionsVisible = 0;
                selectVendorType.setText(option1.getText());
                selectVendorTypeList.setVisibility(View.GONE);
                vendorResultList.setVisibility(View.GONE);
                selector.setVisibility(View.VISIBLE);
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectVendorType.setText(option1.getText());
                comboIndex = 2;
                optionsVisible = 0;
                selectVendorType.setText(option2.getText());
                selectVendorTypeList.setVisibility(View.GONE);
                vendorResultList.setVisibility(View.GONE);
                selector.setVisibility(View.VISIBLE);
            }
        });
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectVendorType.setText(option1.getText());
                comboIndex = 3;
                optionsVisible = 0;
                selectVendorType.setText(option3.getText());
                selectVendorTypeList.setVisibility(View.GONE);
                vendorResultList.setVisibility(View.GONE);
                selector.setVisibility(View.VISIBLE);
            }
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectVendorType.setText(option1.getText());
                comboIndex = 4;
                optionsVisible = 0;
                selectVendorType.setText(option4.getText());
                selectVendorTypeList.setVisibility(View.GONE);
                vendorResultList.setVisibility(View.GONE);
                selector.setVisibility(View.VISIBLE);
            }
        });
        option5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectVendorType.setText(option1.getText());
                comboIndex = 5;
                optionsVisible = 0;
                selectVendorType.setText(option5.getText());
                selectVendorTypeList.setVisibility(View.GONE);
                vendorResultList.setVisibility(View.GONE);
                selector.setVisibility(View.VISIBLE);
            }
        });
        option6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVendorType.setText(option6.getText());
                selectVendorTypeList.setVisibility(View.GONE);
                optionsVisible = 0;
                vendorResultList.setVisibility(View.GONE);
                //selector.setEnabled(false);
                selector.setVisibility(View.GONE);
            }
        });

        //selector.setEnabled(false);
        //selector.setVisibility(View.GONE);
        selector.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    selector.setAlpha((float)0.6);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    selector.setAlpha((float)1.0);
                }
                return false;
            }
        });

        vendorResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogSearchedVendorProfile newFragment = new DialogSearchedVendorProfile();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("vendorType",String.valueOf(selectedQuadrant));
                bundle.putString("vendorId",searchedRows.get(position).split("----")[1]);
                bundle.putString("usedBy",searchedRows.get(position).split("----")[6]);
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "dialog6");
            }
        });
        selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVendorTypeList.setVisibility(View.GONE);
                optionsVisible = 0;

                DialogLocationSelector locationSelector= new DialogLocationSelector();
                Bundle args = new Bundle();
                //args.putInt("val",selectedHalf);
                args.putInt("val2",selectedQuadrant);
                locationSelector.setArguments(args);
                locationSelector.show(getFragmentManager(), "dialog5");
            }
        });
        colorIndicator = (View)findViewById(R.id.colorIndicator);
        colorIndicator.setBackgroundColor(getResources().getColor(R.color.workchop_blue));



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
            LinearLayout.LayoutParams widthParams = new
                    LinearLayout.LayoutParams((int)(77*density),LinearLayout.LayoutParams.WRAP_CONTENT);
            widthParams.setMargins((int)(-79*density),0,0,0);
            LinearLayout.LayoutParams widthParams2 = new
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            widthParams2.setMargins(0,0,(int)(79*density),0);
            selectVendorTypeFrame.setLayoutParams(widthParams2);
            colorIndicator.setVisibility(View.GONE);
            selectorFrame.setLayoutParams(widthParams);
            lookingFor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            selectVendorType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textIn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            option1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            option2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            option3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            option4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            option5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            option6.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogSearchedVendorProfile dialog = new DialogSearchedVendorProfile();
                Bundle bundle = new Bundle();
                bundle.putString("usedBy"," ");
                bundle.putString("userId",userId);
                bundle.putString("vendorId",recentIds.get(position));
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(),"dialog31");
            }
        });
        //startActivity(sendIntent);

        new confirmVendorUsed(ActivityMain.this).execute("");
        new getRecent(ActivityMain.this).execute(userId);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pollCaller();
                handler.postDelayed(this,45000);
            }
        },5000);

        handlerChats = new Handler();
        handlerChats.postDelayed(new Runnable() {
            @Override
            public void run() {
                pollCaller2();
                handlerChats.postDelayed(this,8000);
            }
        },5000);

        new phoneNoToFile2(ActivityMain.this).execute(userId);
        backupProgress = new ProgressDialog(ActivityMain.this);
        backupProgress.setTitle("Backing up contact and vendor information");
        backupProgress.setMessage("This may take a few minutes");
        getContactsCount();
    }

    public void  phoneNoToFile(){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Workchop", "files");
            //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists())
            {
                root.mkdirs();
            }
            File gpxfile = new File(root, "phone_number.txt");

            FileWriter writer = new FileWriter(gpxfile,false);
            writer.append(loggedPhoneNo);
            writer.flush();
            writer.close();
            ////Toast.makeText(this, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
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
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_details.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;

            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                        loggedPhoneNo = sb.toString().split("--")[2];
                        phoneNoToFile();
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

    public void pollCaller(){
        try {
            //Log.v("CHATS SIZE", String.valueOf(chats.size()));
            new pollingUserIsVendorClass(ActivityMain.this).execute(loggedPhoneNo,"1");
            Log.v("CALLING POLL 2", "POLL ");
        }
        catch(NullPointerException e){
            Log.v("ERROR CAUSE", e.toString());
            //Toast.makeText(ActivityMain.this," ",Toast.LENGTH_SHORT).show();
        }
    }

    public void pollCaller2(){
        try {
            //Log.v("CHATS SIZE", String.valueOf(chats.size()));
            new pollingChatCount(ActivityMain.this).execute(userId);
            Log.v("CALLING CHAT COUNTER", "POLL ");
        }
        catch(NullPointerException e){
            Log.v("ERROR CAUSE", e.toString());
            //Toast.makeText(ActivityMain.this," ",Toast.LENGTH_SHORT).show();
        }
    }

    private class pollingChatCount extends AsyncTask<String, Void, String> {
        Context context;
        public pollingChatCount(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            final String dataUrl2 = "http://workchopapp.com/mobile_app/get_user_chat_list2.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            Log.v("CHAT URL",dataUrl2+"?"+dataUrlParameters);
            try {
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

                in2.close();

                Handler h = new Handler(Looper.getMainLooper());
                final String finalDataUrlParameters = dataUrlParameters;
                h.post(new Runnable() {
                    public void run() {
                        //Log.v("full text",sb.toString());
                        if(sb2.toString().contains("------")) {
                            String[] allChats = sb2.toString().split("------");
                            ArrayList<String>newChatCount = new ArrayList<String>();
                            ArrayList<String> newChatTimes = new ArrayList<String>();
                            ArrayList<String> newChatNames = new ArrayList<String>();
                            ArrayList<String> newChatIds = new ArrayList<String>();

                            int tempTotalChatCount = 0;
                            int beep = 0;

                            for (int i = 0; i < allChats.length; i++) {
                                String[] chatRows = allChats[i].split("--");
                                newChatNames.add(chatRows[1]);
                                newChatTimes.add(chatRows[3]);
                                newChatIds.add(chatRows[0]);
                                newChatCount.add(chatRows[4]);
                                tempTotalChatCount = tempTotalChatCount + Integer.parseInt(chatRows[4]);
                                //Log.v("URI", dataUrl2 + "?" + finalDataUrlParameters);
                            }
                            Log.v("TEMP VS PERM",tempTotalChatCount+"--"+totalChatCount);
                            if(totalChatCount > 0 && totalChatCount < tempTotalChatCount ) {
                                beep = 1;
                            }
                            if(tempTotalChatCount == 0){
                                totalChatCount = tempTotalChatCount;
                                beep = 0;
                                chatNotifierCount.setVisibility(View.GONE);
                            }
                            else if(totalChatCount != tempTotalChatCount){
                                totalChatCount = tempTotalChatCount;
                                chatNotifierCount.setText(String.valueOf(totalChatCount));
                                chatNotifierCount.setVisibility(View.VISIBLE);
                                beep = 1;
                            }
                            else{
                                chatNotifierCount.setText(String.valueOf(totalChatCount));
                                chatNotifierCount.setVisibility(View.VISIBLE);
                                beep = 0;
                            }

                            if(beep == 1){
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
                    }
                });
            }
            catch(MalformedURLException e){

            }
            catch(URISyntaxException e){

            }
            catch(IOException e){

            }
            finally{
                Log.v("CHAT ERROR","ERROR");
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
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, sb.toString()+" New Message(s) in your Vendor Account", Toast.LENGTH_SHORT).show();
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

    public void createNotification(String vendorid, String name, String message, int id) {
        Intent intent = new Intent(this, ActivityChats.class);
        intent.putExtra("val7","notification");
        intent.putExtra("name",name);
        intent.putExtra("userId",userId);
        intent.putExtra("vendorId",vendorid);
        intent.putExtra("from","noti");
        intent.putIntegerArrayListExtra("notificationIds",phoneNotificationIds);
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
        phoneNotificationIds.add(id);
        notificationManager.notify(id, noti);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep1);
        if (mp != null) {
            mp.release();
        }
        // Create a new MediaPlayer to play this sound
        mp = MediaPlayer.create(this, R.raw.beep1);
        mp.start();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(400);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();

            };
        });
    }

    public void confirmer(String [] rows){
        //System.out.println("LENGTH OF ROWS - "+rows.length);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        for(int i=0; i<rows.length; i++){

            Date date = new Date();
            //System.out.println(rows[i]);
            String [] dateArray = dateFormat.format(date).split("-");
            double currentYear = (Integer.parseInt(dateArray[0])-2016) * 365 * 24 * 3600;
            double currentMonth = ((double)(Integer.parseInt(dateArray[1]))/12 * 365) * 24 * 3600;
            double currentDay = Integer.parseInt(dateArray[2]) * 24 * 3600;
            double currentHour = Integer.parseInt(dateArray[3]) * 3600;
            double currentMinute = Integer.parseInt(dateArray[4]) * 60;
            double currentSecond = Integer.parseInt(dateArray[5]);
            //System.out.printf("%f, %f, %f, %f%, f, %f \n",currentYear,currentMonth,currentDay,currentHour,currentMinute,
                    //currentSecond);
            double currentTotal = currentYear+currentMonth+currentDay+currentHour+currentMinute+currentSecond;
            String newDateArray = rows[i].split("--")[3];
            double newYear = (Integer.parseInt(newDateArray.split(" ")[0].split("-")[0])-2016) * 365 * 24 * 3600;
            double newMonth = ((double)Integer.parseInt(newDateArray.split(" ")[0].split("-")[1])/12) * 365 * 24 * 3600;
            double newDay = Integer.parseInt(newDateArray.split(" ")[0].split("-")[2]) * 24 * 3600;
            double newHour = Integer.parseInt(newDateArray.split(" ")[1].split(":")[0]) * 3600;
            double newMinute = Integer.parseInt(newDateArray.split(" ")[1].split(":")[1]) * 60;
            double newSecond = Integer.parseInt(newDateArray.split(" ")[1].split(":")[2]);
            double newTotal = newYear + newMonth + newDay + newHour + newMinute + newSecond;
            double timeDifference = currentTotal - newTotal;
            if(timeDifference  > 21600){
                DialogNewReviewUsed dialog = new DialogNewReviewUsed();
                Bundle bundle = new Bundle();
                bundle.putString("userId",userId);
                bundle.putString("vendorId",rows[i].split("--")[1]);
                bundle.putString("question","Did you recently use "+rows[i].split("--")[2]);
                bundle.putString("date_time",rows[i].split("--")[3]);
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(),"dialog30");
            }
            //Log.v("Time Converted",String.valueOf(currentTotal)+"--"+String.valueOf(newTotal));
        }
    }

    private class getRecentImage extends AsyncTask<String,Void,String> {
        Context context;

        public getRecentImage(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String imageUrl = "http://workchopapp.com/mobile_app/vendor_pictures/" + params[0] + ".jpg";
            Log.v("IMAGE URL",imageUrl);
            String dataUrlParameters = null;

            URL url = null;
            try{
                URL url2 = new URL(imageUrl);
                HttpURLConnection connection  = (HttpURLConnection) url2.openConnection();

                connection.setDoOutput(true);
                connection.connect();

                InputStream is3 = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap img = BitmapFactory.decodeStream(is3);

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        try {
                            View view = recentList.getChildAt(Integer.parseInt(params[1]));
                            ImageView recentImage = (ImageView)view.findViewById(R.id.chatIcon);
                            recentImage.setImageBitmap(img);
                            //adapterRecent.notifyDataSetChanged();

                        }
                        catch(NullPointerException e){
                            Log.v("NULL IMAGE POINTER","");
                        }
                    }
                });
                connection.disconnect();
            }

            catch(MalformedURLException e){
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
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_chat_list2.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;

            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                        if(sb.toString().equals("false")){
                            try {
                                //progress.dismiss();
                            }
                            catch(NullPointerException e){

                            }
                        }
                        else if(sb.toString().length() <10){

                        }
                        else {
                            String[] allChats = sb.toString().split("------");
                            ArrayList<String> seenNotification = new ArrayList<String>();

                            for (int i = 0; i < allChats.length; i++) {
                                String [] chatRows = allChats[i].split("--");
                                chatNames.add(chatRows[1]);
                                chatTimes.add(chatRows[3]);
                                chatIds.add(chatRows[0]);
                                chatCount.add(chatRows[4]);
                                //seenNotification.add(chatRows[4]);
                            }
                            ListChats [] chats = new ListChats[chatIds.size()];
                                    /*new ListChats("Vendor 1",R.drawable.chat, "date--time" ), new ListChats("Vendor 2",R.drawable.chat, "date--time"  ),
                                    new ListChats("Vendor 3",R.drawable.chat, "date--time"  ),
                                    new ListChats("Vendor 4",R.drawable.chat, "date--time" ), new ListChats("Vendor 5",R.drawable.chat, "date--time" ),
                                    new ListChats("Vendor 6",R.drawable.chat, "date--time"  ), new ListChats("Vendor 7",R.drawable.chat, "date--time" ),
                                    new ListChats("Vendor 8",R.drawable.chat, "date--time" ), new ListChats("Vendor 9",R.drawable.chat, "date--time" ),
                                    new ListChats("Vendor 10",R.drawable.chat, "date--time" ), new ListChats("Vendor 11",R.drawable.chat, "date--time" ),
                                    new ListChats("Vendor 12",R.drawable.chat, "date--time" )};*/
                            for(int i=0; i<chats.length; i++){
                                chats[i] = new ListChats(chatNames.get(i),R.drawable.chat,chatTimes.get(i),
                                        Integer.parseInt(chatCount.get(i)));
                                checkNotification = Integer.parseInt(chatCount.get(i));
                                if(checkNotification > 0) {
                                    createNotification(chatIds.get(i), chatNames.get(i),
                                            chatCount.get(i) + " new messages from " + chatNames.get(i), i);
                                }
                            }
                            //final AdapterChats adp = new AdapterChats(getApplicationContext(),R.layout.row_chats, chats );
                            //messageList.setAdapter(adp);
                            //progress.dismiss();
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

    private class getRecent extends AsyncTask<String, Void, String> {
        Context context;
        public getRecent(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_recently_used.php";
            String dataUrlParameters = null;

            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                        if(sb.toString().equals("false")){
                            //progress.dismiss();
                        }
                        else {
                            if(sb.toString().contains("------")) {
                                String [] values = sb.toString().split("------");
                                ArrayList<ListChats> recents = new ArrayList<ListChats>();

                                for (int i = 0; i < values.length; i++) {
                                    recentIds.add(values[i].split("--")[1]);
                                    recents.add(new ListChats(values[i].split("--")[2], R.drawable.recent_icon_blue,
                                            getDateFormat(values[i].split("--")[3]),0));
                                }

                                adapterRecent = new AdapterRecent(getApplicationContext(), R.layout.row_chats, recents);
                                adapterRecent.notifyDataSetChanged();
                                recentList.setAdapter(adapterRecent);

                                for(int i=0; i<recentIds.size(); i++){
                                    new getRecentImage(context).execute(recentIds.get(i),String.valueOf(i));
                                }
                            }
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

        newFormat = monthString+"-"+dayString+" | "+hour+":"+minute+" "+timeSector;
        return newFormat;
    }

    public void uploadUserCotacts (){
        //for(int i=0; i < finalContactsList.size(); i++){

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
            new contactsUploader(ActivityMain.this).execute(userId,contactName,contactNumber, String.valueOf(i));
        }
        new getVendorTypes(ActivityMain.this).execute("");

        //Toast.makeText(ActivityMain.this,"USER CONTACTS FULLY UPLOADED", Toast.LENGTH_SHORT).show();
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
                    contactName.toLowerCase().contains("car repair") || contactName.toLowerCase().contains("rewire")
                    || contactName.toLowerCase().contains("mech")){
                contactType = 4;
                workchopVendorList4.add(contactName + " " + String.valueOf(contactType));
            }
            else if(contactName.toLowerCase().contains("tailor") || contactName.toLowerCase().contains("fashion")){
                contactType = 5;
                workchopVendorList5.add(contactName + " " + String.valueOf(contactType));
            }
            new vendorsUploader(ActivityMain.this).execute(userId,contactName,contactNumber,String.valueOf(contactType),"0",
                    String.valueOf(workchopUserLocationIndex), String.valueOf(i));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //progress.dismiss();
                ////Toast.makeText(ActivityMain.this,"User location - "+workchopUserLocationIndex,Toast.LENGTH_SHORT).show();
            }
        },2000);
        Toast.makeText(ActivityMain.this,foundVendors.size()+" VENDORS SUCCESSFULLY ADDED TO YOUR VENDORS LIST",Toast.LENGTH_SHORT).show();
        uploadUserCotacts();
    }

    public void setVendorTypes(){
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");
        float density = getResources().getDisplayMetrics().density;
        for(int i=0; i<workchopVendorList.size(); i++) {
            final TextView workchopVendorTypeRow = new TextView(ActivityMain.this);
            workchopVendorTypeRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            workchopVendorTypeRow.setTypeface(type);
            workchopVendorTypeRow.setTextColor(Color.parseColor("#000000"));
            workchopVendorTypeRow.setClickable(true);
            workchopVendorTypeRow.setText(workchopVendorList.get(i));
            workchopVendorTypeRow.setOnTouchListener(new TextHighlighterOnTouchListener(workchopVendorTypeRow));
            workchopVendorTypeRow.setPadding((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));
            selectVendorTypeList.addView(workchopVendorTypeRow);
            View workchopVendorTypeLine = new View(ActivityMain.this);
            LinearLayout.LayoutParams paramsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (1 * density));
            paramsLine.setMargins((int) (2 * density), (int) (0 * density), (int) (2 * density), (int) (0 * density));
            workchopVendorTypeLine.setLayoutParams(paramsLine);
            workchopVendorTypeLine.setBackgroundColor(Color.parseColor("#888888"));
            selectVendorTypeList.addView(workchopVendorTypeLine);
            final int j = i+1;
            workchopVendorTypeRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comboIndex = j;
                    optionsVisible = 0;
                    selectVendorType.setText(workchopVendorTypeRow.getText());
                    selectVendorTypeList.setVisibility(View.GONE);
                    vendorResultList.setVisibility(View.GONE);
                    selector.setVisibility(View.VISIBLE);
                }
            });

        }
        //uploadUserVendors();
    }

    public void setVendorTypes2(){
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");
        float density = getResources().getDisplayMetrics().density;
        for(int i=0; i<workchopVendorList.size(); i++) {
            final TextView workchopVendorTypeRow = new TextView(ActivityMain.this);
            workchopVendorTypeRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            workchopVendorTypeRow.setTypeface(type);
            workchopVendorTypeRow.setTextColor(Color.parseColor("#000000"));
            workchopVendorTypeRow.setClickable(true);
            workchopVendorTypeRow.setText(workchopVendorList.get(i));
            workchopVendorTypeRow.setOnTouchListener(new TextHighlighterOnTouchListener(workchopVendorTypeRow));
            workchopVendorTypeRow.setPadding((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));
            selectVendorTypeList.addView(workchopVendorTypeRow);
            View workchopVendorTypeLine = new View(ActivityMain.this);
            LinearLayout.LayoutParams paramsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (1 * density));
            paramsLine.setMargins((int) (2 * density), (int) (0 * density), (int) (2 * density), (int) (0 * density));
            workchopVendorTypeLine.setLayoutParams(paramsLine);
            workchopVendorTypeLine.setBackgroundColor(Color.parseColor("#888888"));
            selectVendorTypeList.addView(workchopVendorTypeLine);
            final int j = i+1;
            workchopVendorTypeRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comboIndex = j;
                    optionsVisible = 0;
                    selectVendorType.setText(workchopVendorTypeRow.getText());
                    selectVendorTypeList.setVisibility(View.GONE);
                    vendorResultList.setVisibility(View.GONE);
                    selector.setVisibility(View.VISIBLE);
                }
            });

        }
    }

    public void searcher(int vendorTypeIndex, ArrayList<String> rows){
        final View rootView2 = this.getLayoutInflater().inflate(R.layout.row_vendorresult, null);
        View view2 = rootView2.getRootView();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        float density = getResources().getDisplayMetrics().density;
        Resources r = getResources();
        float widthDp = (float) width / density;

        ListVendorResult [] vendorRows = null;
        int icon = 1;
        if(vendorTypeIndex == 1){
            icon = R.drawable.icongas;
        }
        else if(vendorTypeIndex == 2){
            icon = R.drawable.iconstylist;
        }
        else if(vendorTypeIndex == 3){
            icon = R.drawable.iconmakeup;
        }
        else if(vendorTypeIndex == 4){
            icon = R.drawable.iconmechanic;
        }
        else if(vendorTypeIndex == 5){
            icon = R.drawable.icontailor;
        }
        String locationString="";
        switch (selectedQuadrant)
        {
            case 1:
                locationString = "<Surulere--Badagry>";
                break;
            case 2:
                locationString = "<Ikeja--Berger>";
                break;
            case 3:
                locationString = "<Shomolu--Ilupeju>";
                break;
            case 4:
                locationString = "<Yaba--Obalende>";
                break;
            case 5:
                locationString = "<Ojota--Ikorodu>";
                break;
            case 6:
                locationString = "<V.I--Epe>";
                break;
            case 7:
                locationString = "<Oshodi--Egbeda>";
                break;
            case 21:
                locationString = "Abaji";
                break;
            case 22:
                locationString = "Abuja Municipal";
                break;
            case 23:
                locationString = "Bwari";
                break;
            case 24:
                locationString = "Gwagwalada";
                break;
            case 25:
                locationString = "Kuje";
                break;
            case 26:
                locationString = "Kwari";
                break;
        }
        if(rows.size() <=5 ) {
            vendorRows = new ListVendorResult[rows.size()];
            for (int i = 0; i < rows.size(); i++) {
                Log.v("IIII", rows.get(i));

                vendorRows[i] = new ListVendorResult(rows.get(i).split("----")[0], icon, " "+locationString+" ",
                        rows.get(i).split("----")[5]);
            }
        }
        else{
            vendorRows = new ListVendorResult[5];
            for (int i = 0; i < 5; i++) {
                Log.v("IIII", rows.get(i));

                vendorRows[i] = new ListVendorResult(rows.get(i).split("----")[0], icon, " " + locationString+" ",
                        rows.get(i).split("----")[5]);
            }
        }
        final AdapterVendorResult adp = new AdapterVendorResult(view2.getContext(),R.layout.row_vendorresult, vendorRows, widthDp );
        vendorResultList.setAdapter(adp);
        vendorResultList.setVisibility(View.VISIBLE);
        vendorSearcherDialog.dismiss();
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }

    public boolean isLocationServiceEnabled(){
        LocationManager locationManager = null;
        boolean gps_enabled= false,network_enabled = false;

        if(locationManager ==null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }

    private class contactsUploader extends AsyncTask<String,Void,String> {
        Context context;

        public contactsUploader(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_contacts.php";


            if(Integer.parseInt(params[3])+1 == finalContactsList.size()){
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        progress.dismiss();
                    }
                });

            }
            else{
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        int curr = Integer.parseInt(params[3]) + 1;
                        int percent = (int)(double)((curr*100)/finalContactsList.size());
                        //progress.setTitle("Uploading User Contacts - "+ curr +" of "+finalContactsList.size());
                        progress.setTitle("Saving Contacts - "+ percent+"%");
                    }
                });
            }
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&contact_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&contact_number="+URLEncoder.encode(params[2],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                            ////Toast.makeText(context, "TEMPORARY SIGN UP", Toast.LENGTH_SHORT).show();
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

    private class getVendorTypes extends AsyncTask<String,Void,String> {
        Context context;

        public getVendorTypes(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/vendor_types.php";

           URL url = null;
            HttpURLConnection connection = null;
            try{

                url = new URL(dataUrl);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(dataUrl));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line="";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                String [] values = sb.toString().split("--");

                for(String value : values){
                    Log.v("URL FOR VENDOR TYPES", value);
                    workchopVendorList.add(value);
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        ////Toast.makeText(context,"DOWNLOADED VENDOR TYPES = "+workchopVendorList.toString(),Toast.LENGTH_SHORT).show();
                        setVendorTypes();
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

    private class getVendorTypes2 extends AsyncTask<String,Void,String> {
        Context context;

        public getVendorTypes2(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/vendor_types.php";

            URL url = null;
            HttpURLConnection connection = null;
            try{

                url = new URL(dataUrl);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(dataUrl));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line="";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                String [] values = sb.toString().split("--");

                for(String value : values){
                    Log.v("URL FOR VENDOR TYPES", value);
                    workchopVendorList.add(value);
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        ////Toast.makeText(context,"DOWNLOADED VENDOR TYPES = "+workchopVendorList.toString(),Toast.LENGTH_SHORT).show();
                        setVendorTypes2();
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

    private class confirmVendorUsed extends AsyncTask<String,Void,String> {
        Context context;

        public confirmVendorUsed(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_probably_used.php?user_id="+userId;

            URL url = null;
            HttpURLConnection connection = null;
            try{

                url = new URL(dataUrl);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(dataUrl));
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
                        if(sb.toString().contains("------")){
                            confirmer(sb.toString().split("------"));
                        }
                        else{

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

    private class vendorsUploader extends AsyncTask<String,Void,String> {
        Context context;

        public vendorsUploader(Context c){
            context = c;
        }

        @Override
        protected String doInBackground( final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_vendors.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/update_user_location.php";

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    int curr = Integer.parseInt(params[6])+1;
                    //progress.setTitle("Populating Vendors - " + curr +" of "+foundVendors.size());
                    int percent = (int)(double)((curr*100)/foundVendors.size());
                    progress.setTitle("Saving Vendors - "+ percent+"%");
                }
            });
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
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                        new vendorAsContact(context).execute(params[0],params[1],params[2]);
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
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                            ////Toast.makeText(context,"Vendor Added As Contact", Toast.LENGTH_SHORT).show();
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

    private class setLoggedIn extends AsyncTask<String,Void,String> {
        Context context;

        public setLoggedIn(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/set_logged_in.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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

    private class setLoggedOut extends AsyncTask<String,Void,String> {
        Context context;

        public setLoggedOut(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/set_logged_out.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    public void readContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        contactsDetails = new ArrayList<String>();
        foundVendors = new ArrayList<String>();
        if (cur.getCount() > 0 ) {
            ////Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts exist",Toast.LENGTH_SHORT).show();
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
                    String joined = name +" "+ phone;
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
                TextView text = new TextView(ActivityMain.this);
                text.setTextColor(Color.rgb(235, 235, 235));
                text.setBackgroundColor(Color.GRAY);
                text.setText("Contact Details -- " + foundVendors.get(i));
                //contactList.addView(text);
            }
            /*//Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
                    Toast.LENGTH_SHORT).show();*/
            ////Toast.makeText(ActivityMain.this,String.valueOf(treeMap.size())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
                    //Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(ActivityMain.this,"Contacts don't exist on this device",Toast.LENGTH_SHORT).show();
        }

    }

    public void getContactsCount() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        contactsDetails = new ArrayList<String>();
        foundVendorsCount = new ArrayList<String>();
        if (cur.getCount() > 0 ) {
            ////Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts exist",Toast.LENGTH_SHORT).show();
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
                    String joined = name +" "+ phone;
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

            ////Toast.makeText(ActivityMain.this,"User Contacts Size - "+treeMap.size(),Toast.LENGTH_SHORT).show();
            new checkContactsEqual(ActivityMain.this).execute(userId,String.valueOf(treeMap.size()));
            /*//Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
                    Toast.LENGTH_SHORT).show();*/
            ////Toast.makeText(ActivityMain.this,String.valueOf(treeMap.size())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
            //Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(ActivityMain.this,"Contacts don't exist on this device",Toast.LENGTH_SHORT).show();
        }

    }

    private class checkContactsEqual extends AsyncTask<String,Void,String> {
        Context context;

        public checkContactsEqual(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_contacts_count.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                        ////Toast.makeText(context,"Existing contacts-"+params[1]+" and database contacts-"+sb.toString(),
                                //Toast.LENGTH_SHORT).show();
                        if(Integer.parseInt(params[1]) < Integer.parseInt(sb.toString())){
                            ////Toast.makeText(context,"NO NEED FOR REFRESH",Toast.LENGTH_SHORT).show();
                        }
                        else if((Integer.parseInt(params[1])-Integer.parseInt(sb.toString())) > 25 && freshSignIn == 0){
                            ////Toast.makeText(context,"NEED FOR REFRESH",Toast.LENGTH_SHORT).show();
                            backupProgress.show();
                            readContacts2();
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

    public void readContacts2() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        contactsDetails = new ArrayList<String>();
        foundVendors2 = new ArrayList<String>();
        finalContactsList2 = new ArrayList<String>();
        if (cur.getCount() > 0 ) {
            ////Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts exist",Toast.LENGTH_SHORT).show();
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
            ArrayList<Integer> found = findVendors2(treeMap);

            Toast.makeText(ActivityMain.this,String.valueOf(treeMap.size())+" Contacts Exist. Found "+foundVendors2.size()+" Vendors",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(ActivityMain.this,"Contacts don't exist on this device",Toast.LENGTH_SHORT).show();
        }
        uploadUserCotacts2();

    }

    public void uploadUserCotacts2 (){
        //uploadUserVendors2();
        for(int i=0; i < finalContactsList2.size(); i++){
            String [] value = finalContactsList2.get(i).split(" ");
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
            new contactsUploader2(ActivityMain.this).execute(userId,contactName,contactNumber,
                    String.valueOf(finalContactsList2.size() - i),String.valueOf(i));
        }
        uploadUserVendors2();

        //Toast.makeText(ActivityMain.this,"USER CONTACTS FULLY UPLOADED", Toast.LENGTH_LONG).show();
    }

    public void uploadUserVendors2(){
        for(int i=0; i < foundVendors2.size(); i++){
            String [] value = foundVendors2.get(i).split(" ");
            Log.v("INSIDE SORTING VENDORS "+i, foundVendors2.get(i));
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
            }
            else if(contactName.toLowerCase().contains("hair") || contactName.toLowerCase().contains("stylist") ||
                    contactName.toLowerCase().contains("hair stylist") || contactName.toLowerCase().contains("barber")){
                contactType = 2;
            }
            else if(contactName.toLowerCase().contains("makeup") || contactName.toLowerCase().contains("make up") ){
                contactType = 3;
            }
            else if(contactName.toLowerCase().contains("mech.") || contactName.toLowerCase().contains("mechanic") ||
                    contactName.toLowerCase().contains("car repair") || contactName.toLowerCase().contains("rewire")){
                contactType = 4;
            }
            else if(contactName.toLowerCase().contains("tailor") || contactName.toLowerCase().contains("fashion")){
                contactType = 5;
            }
            new vendorsUploader2(ActivityMain.this).execute(userId,contactName,contactNumber,String.valueOf(contactType),"0",
                    String.valueOf(workchopUserLocationIndex), String.valueOf(i));
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
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    int curr = Integer.parseInt(params[4]) + 1;
                    //backupProgress.setTitle("Backing up data - "+ curr +" of "+finalContactsList2.size());
                    int percent = (int)(double)((curr*100)/finalContactsList2.size());
                    backupProgress.setTitle("Backing up contacts - "+ percent+"%");
                }
            });
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("REFRESH CONTACTS UPLOAD",dataUrl+"?"+dataUrlParameters);
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
                            ////Toast.makeText(context, "TEMPORARY SIGN UP", Toast.LENGTH_LONG).show();
                        }
                        if(Integer.parseInt(params[3]) < 2){
                            //backupProgress.dismiss();
                            //Toast.makeText(context, "BACK-UP COMPLETE", Toast.LENGTH_LONG).show();
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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if(Integer.parseInt(params[6])+1 == foundVendors2.size()) {
                        backupProgress.dismiss();
                    }
                    else{
                        int curr = Integer.parseInt(params[6]) + 1;
                        //backupProgress.setTitle("Backing up data - " + curr + " of " + foundVendors2.size());
                        int percent = (int)(double)((curr*100)/foundVendors2.size());
                        backupProgress.setTitle("Backing up vendors - "+ percent+"%");
                    }
                }
            });
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
                //Toast.makeText(ActivityMain.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                Log.v("REFRESH VENDORS UPLOAD", dataUrl+"?"+dataUrlParameters);
                //Log.v("NEW URL =========", dataUrl2+"?"+dataUrlParameters2);
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
                        new vendorAsContact(ActivityMain.this).execute(userId,params[1],params[2]);

                        ////Toast.makeText(context,"Vendor Successfully Added", Toast.LENGTH_SHORT).show();
                        //onAdd();
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
        super.onBackPressed();
        /*new AlertDialog.Builder(ActivityMain.this)
                .setIcon(R.drawable.exit)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityMain.this.finishAffinity();
                        handler.removeCallbacksAndMessages(null);
                        handlerChats.removeCallbacksAndMessages(null);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handlerChats.removeCallbacksAndMessages(null);
        /*
        finishAffinity();
        if(android.os.Build.VERSION.SDK_INT >= 21)
        {
            finishAndRemoveTask();
        }
        else
        {
            finish();
        }*/
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
                        value.toLowerCase().equals("hair") || value.toLowerCase().equals("stylist")){
                    vendorIndexList.add(index);
                    foundVendors.add(val.getKey());
                }
            }
            index++;
        }
        return vendorIndexList;
    }

    public ArrayList<Integer> findVendors2(Map<String, String> vendorList){
        ArrayList<Integer> vendorIndexList = new ArrayList<Integer>();
        int index=0;
        for(Map.Entry<String, String> val : vendorList.entrySet()){
            finalContactsList2.add(val.getKey());
            String [] valArray = val.getKey().split(" +");
            for(String value : valArray){
                if(value.toLowerCase().equals("mechanic") || value.toLowerCase().equals("gas") || value.toLowerCase().equals("makeup") ||
                        value.toLowerCase().equals("tailor") || value.toLowerCase().equals("fashion designer")|| value.toLowerCase().equals("fashion") ||
                        value.toLowerCase().equals("hair") || value.toLowerCase().equals("stylist")){
                    vendorIndexList.add(index);
                    foundVendors2.add(val.getKey());
                }
            }
            index++;
        }
        return vendorIndexList;
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelected(int color, final int selectedQuadrant) {
        colorIndicator.setBackgroundColor(getResources().getColor(color));
        //DialogLocationSelector df = (DialogLocationSelector) getFragmentManager().findFragmentByTag("dialog5");
        this.selectedHalf = selectedHalf;
        optionsVisible = 0;
        //progressdialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //searcher(comboIndex);
                new searchEngine(ActivityMain.this).execute(userId,String.valueOf(comboIndex),
                        String.valueOf(selectedQuadrant));
                //progressdialog.dismiss();
            }
        },1500);
        vendorSearcherDialog = new ProgressDialog(ActivityMain.this);
        vendorSearcherDialog.setTitle("Searching");
        vendorSearcherDialog.show();
        this.selectedQuadrant = selectedQuadrant;
    }

    private class searchEngine extends AsyncTask<String,Void,String> {
        Context context;

        public searchEngine(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/search_engine.php";
            String dataUrlParameters = "";
            searchedRows = new ArrayList<>();
            final int type = Integer.parseInt(params[1]);
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&vendor_type="+URLEncoder.encode(params[1],"UTF-8")
                        +"&location_index="+URLEncoder.encode(params[2],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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
                searchedRows = new ArrayList<>();
                if(sb.toString().length() > 1) {
                    final String[] values = sb.toString().split("==========");
                    for(int i=0; i< values.length; i++){
                        searchedRows.add(values[i]);
                    }
                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            //progressdialog.dismiss();
                            //Text(context, String.valueOf(searchedRows.size()), Toast.LENGTH_SHORT).show();
                            searcher(type, searchedRows);
                        }
                    });
                }
                else{
                    vendorSearcherDialog.dismiss();
                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "No Vendors Found", Toast.LENGTH_SHORT).show();
                            vendorResultList.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                in.close();
            }

            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        vendorSearcherDialog.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        vendorSearcherDialog.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        vendorSearcherDialog.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally{
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        vendorSearcherDialog.dismiss();
                    }
                });
            }
            return null;
        }
    }

    @Override
    public void onSelected2(int color, int selectedQuadrant) {
        colorIndicator.setBackgroundColor(getResources().getColor(color));
        workchopUserLocationIndex = selectedQuadrant;
    }

    @Override
    public void done(){
        final ProgressDialog progress2 = new ProgressDialog(this);
        progress2.setTitle("Searching device for vendors");
        progress2.setMessage("searching...");
        //progress2.show();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress2.dismiss();
            }
        },2000);*/
    }

    @Override
    public void done2(){

        progress = new ProgressDialog(this);
        progress.setTitle("Populating Vendor List");
        progress.setMessage("Searching device for vendors...");
        progress.show();
    }

    @Override
    public void onDestroy2() {
        if(workchopUserLocationIndex == 0){
            dialogUserSelectLocation.show(getFragmentManager(),"dialog14");
        }
        else{
            Log.v("tO UPLOAD"," USER VENDORS");
            for(int i=0; i<foundVendors.size(); i++){
                Log.v("VENDOR INDEX -",foundVendors.get(i));
            }
            uploadUserVendors();
            //uploadUserCotacts();
        }
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
