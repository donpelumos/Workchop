package com.workchopapp.workchop;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by BALE on 09/07/2016.
 */
public class ActivityLogin extends AppCompatActivity {
    ActionBar appBar;
    Button tab1, tab2, loginButton, signupButton;
    TextView forgotPassword, signUpLink;
    Spinner vendorType;
    LinearLayout tab1Bg, tab2Bg, navBar;
    ImageView backgroundImage, backgroundImage2, backgroundImage3, backgroundImage4;
    int imageIndex, roundTabIndex;
    Animation fadeIn, fadeOut;
    Handler hand;
    LinearLayout  frame1, frame2, frame3, frame4, frame5;
    TextView loginRoundTabFrame, signupRoundTabFrame, aboutRoundTabFrame, exitRoundTabFrame;
    TextView loginRoundTab, signupRoundTab, aboutRoundTab, exitRoundTab;
    TextView aboutText1, aboutText2;
    EditText emailOrPhone, password, firstname, surname, email, phoneNo, enterPasword;
    LinearLayout loginLayout, aboutLayout, vendorTypeFrame;
    ScrollView signupLayout;
    String [] vendorList = {"--Vendor Type--","Gas Supplier","Hair Stylist","Make-up Artist","Mechanic","Tailor"};
    ArrayAdapter<String> a1;
    ImageView curve1, curve2, curve3, curve4, curve5, curve6, curve7, curve8;
    AlertDialog confirmDialog;
    int userReadyToSignUp;
    String userId;
    int confirmationMode;
    int selectedVendorType = 0;
    String confirmationCode;
    String joinedSurname = null;  String joinedFirstname = null;  String joinedEmail = null;  String joinedPhoneNo = null;
    String joinedPassword = null;
    int joinedType = 0;
    String loggedEmail = null; String loggedPassword = null; String loggedFirstname = null; String loggedSurname = null;
    String loggedPhoneNo = null; String loggedLocation = null; String loggedId = null;
    boolean gpsEnabled;
    ArrayList<String> contactsName, contactsNumber, contactsDetails, newContactsName, newContactsNumber, foundVendors,
            finalContactsList;
    ArrayList<Integer> contactsIndex, newContactsIndex;
    int index;
    Map<String, String> sortedContacts;
    ProgressDialog signingInProgress;
    SQLiteDatabase mydatabase, mydatabase2;
    String vendorId = "";
    ImageView logo;

    int normalTabIndex;
    int [] backgroundImages = {R.drawable.tailors, R.drawable.makeups, R.drawable.mechanics};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File databaseAccount = getApplicationContext().getDatabasePath("workchop_user_account.db");

        if (!databaseAccount.exists()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            appBar = getSupportActionBar();
            appBar.hide();
            setContentView(R.layout.activity_login);
            mydatabase2  = openOrCreateDatabase("workchop_user_account.db",MODE_PRIVATE,null);
            Log.v("FIRST TIME", "FIRST TIME LOGIN");
            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/GOTHIC.TTF");
            ////Toast.makeText(ActivityLogin.this,String.valueOf(isLocationEnabled(ActivityLogin.this)),Toast.LENGTH_LONG).show();
            hand = new Handler();
            vendorTypeFrame = (LinearLayout) findViewById(R.id.vendorTypeFrame);
            vendorTypeFrame.setVisibility(View.GONE);
            emailOrPhone = (EditText) findViewById(R.id.emailOrPhone);
            password = (EditText) findViewById(R.id.password);
            firstname = (EditText) findViewById(R.id.firstname);
            surname = (EditText) findViewById(R.id.surname);
            email = (EditText) findViewById(R.id.email);
            phoneNo = (EditText) findViewById(R.id.phoneNo);
            enterPasword = (EditText) findViewById(R.id.enterPassword);
            vendorType = (Spinner) findViewById(R.id.vendorType);
            vendorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedVendorType = position;
                    joinedType = position;
                    ////Toast.makeText(ActivityLogin.this, String.valueOf(position),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            gpsEnabled = isLocationServiceEnabled();
            logo = (ImageView) findViewById(R.id.logo);

            frame1 = (LinearLayout) findViewById(R.id.frame1);
            frame2 = (LinearLayout) findViewById(R.id.frame2);
            frame3 = (LinearLayout) findViewById(R.id.frame3);
            frame4 = (LinearLayout) findViewById(R.id.frame4);
            frame5 = (LinearLayout) findViewById(R.id.frame5);

            signupButton = (Button) findViewById(R.id.signupButton);
            signupButton.setTransformationMethod(null);
            tab1 = (Button) findViewById(R.id.tab1);
            tab2 = (Button) findViewById(R.id.tab2);
            navBar = (LinearLayout) findViewById(R.id.navBar);
            tab1.setTransformationMethod(null);
            tab2.setTransformationMethod(null);
            tab1Bg = (LinearLayout) findViewById(R.id.tab1Bg);
            tab2Bg = (LinearLayout) findViewById(R.id.tab2Bg);
            normalTabIndex = 1;
            userReadyToSignUp = 1;
            confirmationMode = 0;
            confirmationCode = null;
            index = 0;
            contactsIndex = new ArrayList<Integer>();
            newContactsIndex = new ArrayList<Integer>();
            contactsName = new ArrayList<String>();
            contactsNumber = new ArrayList<String>();
            newContactsName = new ArrayList<String>();
            newContactsNumber = new ArrayList<String>();
            finalContactsList = new ArrayList<String>();
            userId = null;
            tab1.setOnTouchListener(new TabHighlighterOnTouchListener(tab1));
            tab2.setOnTouchListener(new TabHighlighterOnTouchListener(tab2));


            imageIndex = 0;

            backgroundImage = (ImageView) findViewById(R.id.backgroundImage);
            backgroundImage2 = (ImageView) findViewById(R.id.backgroundImage2);
            backgroundImage3 = (ImageView) findViewById(R.id.backgroundImage3);
            backgroundImage4 = (ImageView) findViewById(R.id.backgroundImage4);

            fadeIn = AnimationUtils.loadAnimation(ActivityLogin.this, R.anim.fade_in);
            fadeOut = AnimationUtils.loadAnimation(ActivityLogin.this, R.anim.fade_out);
            changeBackground();

            loginButton = (Button) findViewById(R.id.loginButton);
            forgotPassword = (TextView) findViewById(R.id.forgotPassword);
            signUpLink = (TextView) findViewById(R.id.signUpLink);
            loginButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(loginButton));
            signupButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(signupButton));
            loginButton.setTransformationMethod(null);
            forgotPassword.setOnTouchListener(new TextViewHighlighterOnTouchListener(forgotPassword));
            signUpLink.setOnTouchListener(new TextViewHighlighterOnTouchListener(signUpLink));
            String udata = forgotPassword.getText().toString();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
            forgotPassword.setText(content);
            String udata2 = signUpLink.getText().toString();
            SpannableString content2 = new SpannableString(udata2);
            content2.setSpan(new UnderlineSpan(), 0, udata2.length(), 0);
            signUpLink.setText(content2);
            vendorType = (Spinner) findViewById(R.id.vendorType);

            roundTabIndex = 1;
            loginRoundTab = (TextView) findViewById(R.id.loginRoundTab);
            signupRoundTab = (TextView) findViewById(R.id.signupRoundTab);
            aboutRoundTab = (TextView) findViewById(R.id.aboutRoundTab);
            exitRoundTab = (TextView) findViewById(R.id.exitRoundTab);
            loginRoundTabFrame = (TextView) findViewById(R.id.loginRoundTabFrame);
            signupRoundTabFrame = (TextView) findViewById(R.id.signupRoundTabFrame);
            aboutRoundTabFrame = (TextView) findViewById(R.id.aboutRoundTabFrame);
            exitRoundTabFrame = (TextView) findViewById(R.id.exitRoundTabFrame);
            loginRoundTab.setOnTouchListener(new RoundTabHighlighterOnTouchListener(loginRoundTab));
            signupRoundTab.setOnTouchListener(new RoundTabHighlighterOnTouchListener(signupRoundTab));
            aboutRoundTab.setOnTouchListener(new RoundTabHighlighterOnTouchListener(aboutRoundTab));
            exitRoundTab.setOnTouchListener(new RoundTabHighlighterOnTouchListener(exitRoundTab));


            loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
            signupLayout = (ScrollView) findViewById(R.id.signupLayout);
            signupLayout.setVisibility(View.GONE);
            vendorType = (Spinner) findViewById(R.id.vendorType);

            aboutLayout = (LinearLayout) findViewById(R.id.aboutLayout);
            aboutLayout.setVisibility(View.GONE);

            a1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vendorList);
            a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vendorType.setAdapter(a1);
            gpsEnabled = isLocationEnabled(ActivityLogin.this);

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tab1Bg.setBackgroundColor(Color.rgb(0, 117, 216));
                    tab1.setTextColor(Color.rgb(0, 117, 216));
                    tab2.setTextColor(Color.rgb(255, 255, 255));
                    tab2Bg.setBackgroundColor(Color.rgb(241, 241, 241));
                    vendorTypeFrame.setVisibility(View.GONE);
                    normalTabIndex = 1;
                    float density = getResources().getDisplayMetrics().density;
                    if (normalTabIndex == 2) {
                        vendorTypeFrame.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                                ActionBar.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, (int) (27 * density), 0, 0);
                        frame1.setLayoutParams(params);
                        frame2.setLayoutParams(params);
                        frame3.setLayoutParams(params);
                        frame4.setLayoutParams(params);
                        frame5.setLayoutParams(params);
                        vendorTypeFrame.setLayoutParams(params);
                        signupLayout.invalidate();
                        signupLayout.requestLayout();
                    } else {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                                ActionBar.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, (int) (35 * density), 0, 0);
                        frame1.setLayoutParams(params);
                        frame2.setLayoutParams(params);
                        frame3.setLayoutParams(params);
                        frame4.setLayoutParams(params);
                        frame5.setLayoutParams(params);
                        vendorTypeFrame.setLayoutParams(params);
                        signupLayout.invalidate();
                        signupLayout.requestLayout();
                    }
                }
            });
            tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tab2Bg.setBackgroundColor(Color.rgb(0, 117, 216));
                    tab2.setTextColor(Color.rgb(0, 117, 216));
                    tab1.setTextColor(Color.rgb(255, 255, 255));
                    tab1Bg.setBackgroundColor(Color.rgb(241, 241, 241));
                    vendorTypeFrame.setVisibility(View.VISIBLE);
                    normalTabIndex = 2;
                    float density = getResources().getDisplayMetrics().density;
                    if (normalTabIndex == 2) {
                        vendorTypeFrame.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                                ActionBar.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, (int) (27 * density), 0, 0);
                        frame1.setLayoutParams(params);
                        frame2.setLayoutParams(params);
                        frame3.setLayoutParams(params);
                        frame4.setLayoutParams(params);
                        frame5.setLayoutParams(params);
                        vendorTypeFrame.setLayoutParams(params);
                        signupLayout.invalidate();
                        signupLayout.requestLayout();
                    } else {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                                ActionBar.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, (int) (35 * density), 0, 0);
                        frame1.setLayoutParams(params);
                        frame2.setLayoutParams(params);
                        frame3.setLayoutParams(params);
                        frame4.setLayoutParams(params);
                        frame5.setLayoutParams(params);
                        vendorTypeFrame.setLayoutParams(params);
                        signupLayout.invalidate();
                        signupLayout.requestLayout();
                    }
                }
            });

            loginRoundTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //logo.setVisibility(View.VISIBLE);
                    roundTabIndex = 1;
                    loginRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                    signupRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    aboutRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    loginLayout.setVisibility(View.VISIBLE);
                    signupLayout.setVisibility(View.GONE);
                    aboutLayout.setVisibility(View.GONE);
                    navBar.setVisibility(View.VISIBLE);
                    tab1.setText("User Login");
                    tab1.setTransformationMethod(null);
                    tab2.setText("Vendor Login");
                    tab2.setTransformationMethod(null);

                }
            });
            signupRoundTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roundTabIndex = 2;
                    //logo.setVisibility(View.GONE);
                    loginRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    signupRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                    aboutRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    loginLayout.setVisibility(View.GONE);
                    signupLayout.setVisibility(View.VISIBLE);
                    aboutLayout.setVisibility(View.GONE);
                    navBar.setVisibility(View.VISIBLE);
                    tab1.setText("User Signup");
                    tab1.setTransformationMethod(null);
                    tab2.setText("Vendor Signup");
                    tab2.setTransformationMethod(null);
                    float density = getResources().getDisplayMetrics().density;

                }
            });
            signUpLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roundTabIndex = 2;
                    loginRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    signupRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                    aboutRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    loginLayout.setVisibility(View.GONE);
                    signupLayout.setVisibility(View.VISIBLE);
                    aboutLayout.setVisibility(View.GONE);
                    navBar.setVisibility(View.VISIBLE);
                    tab1.setText("User Signup");
                    tab1.setTransformationMethod(null);
                    tab2.setText("Vendor Signup");
                    tab2.setTransformationMethod(null);
                }
            });
            aboutRoundTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roundTabIndex = 3;
                    loginLayout.setVisibility(View.GONE);
                    signupLayout.setVisibility(View.GONE);
                    aboutLayout.setVisibility(View.VISIBLE);
                    navBar.setVisibility(View.GONE);
                    loginRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    signupRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    aboutRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                    exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                }
            });
            exitRoundTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    roundTabIndex = 4;
                    loginRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    signupRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    aboutRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_transparent_circle));
                    exitRoundTabFrame.setBackground(getResources().getDrawable(R.drawable.background_blue_circle));
                    new AlertDialog.Builder(ActivityLogin.this)
                            .setIcon(R.drawable.exit)
                            .setTitle("Exit Application")
                            .setMessage("Are you sure you want to exit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hand.removeCallbacksAndMessages(null);
                                    deleteDatabase("workchop_user_account.db");
                                    ActivityLogin.this.finishAffinity();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            });
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hand.removeCallbacksAndMessages(null);
                    if (normalTabIndex == 1) {
                        if (emailOrPhone.getText().length() < 1 || password.getText().length() < 1) {
                            //Toast.makeText(ActivityLogin.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                        } else {
                            loggedEmail = emailOrPhone.getText().toString();
                            loggedPassword = password.getText().toString();
                            signingInProgress = new ProgressDialog(ActivityLogin.this);
                            signingInProgress.setTitle("Signing In");
                            signingInProgress.setMessage("just a moment...");
                            signingInProgress.show();
                            new userSignIn(ActivityLogin.this).execute(loggedEmail, loggedPassword);
                        }
                    } else if (normalTabIndex == 2) {
                        if (emailOrPhone.getText().length() < 1 || password.getText().length() < 1) {
                            //Toast.makeText(ActivityLogin.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                        } else {
                            loggedEmail = emailOrPhone.getText().toString();
                            loggedPassword = password.getText().toString();
                            signingInProgress = new ProgressDialog(ActivityLogin.this);
                            signingInProgress.setTitle("Signing In");
                            signingInProgress.setMessage("just a moment...");
                            signingInProgress.show();
                            new vendorSignIn(ActivityLogin.this).execute(loggedEmail, loggedPassword);
                            //Intent intent = new Intent(ActivityLogin.this, ActivityVendorMain.class);
                            //startActivity(intent);
                        }
                    }

                }
            });
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(ActivityLogin.this, String.valueOf(email.getText().toString().length()) + "|" +
                            //surname.getText().toString().length() + "|" + firstname.getText().toString().length() + "|"
                            //+ phoneNo.getText().toString().length() + "|" + userReadyToSignUp, Toast.LENGTH_SHORT).show();
                    if (normalTabIndex == 1) {
                        //new checkEmail(ActivityLogin.this).execute(email.getText().toString());
                        if (userReadyToSignUp == 0 || surname.getText().toString().length() < 3 || firstname.getText().toString().length() < 3
                                || enterPasword.getText().toString().length() < 3 || phoneNo.getText().toString().length() < 3) {
                            //Toast.makeText(ActivityLogin.this, "Check Filled Data", Toast.LENGTH_SHORT).show();
                        } else if (userReadyToSignUp == 1 && surname.getText().toString().length() >= 3 && firstname.getText().toString().length() >= 3
                                && enterPasword.getText().toString().length() >= 3 && phoneNo.getText().toString().length() >= 3) {
                            //Toast.makeText(ActivityLogin.this, "READY TO SIGN UP", Toast.LENGTH_SHORT).show();
                            joinedSurname = surname.getText().toString();
                            joinedFirstname = firstname.getText().toString();
                            joinedEmail = email.getText().toString();
                            joinedPassword = enterPasword.getText().toString();
                            joinedPhoneNo = phoneNo.getText().toString();

                            mydatabase = openOrCreateDatabase("workchop.db", MODE_PRIVATE, null);
                            //mydatabase.execSQL("DROP TABLE TestLoginPassword;");
                            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS TestLoginPassword(password VARCHAR);");
                            mydatabase.execSQL(String.format("INSERT INTO TestLoginPassword VALUES('%s');", joinedPassword));
                            //Toast.makeText(ActivityLogin.this, "TABLE CREATED WITH PASSWORD - " + joinedPassword, Toast.LENGTH_LONG).show();
                            new userSignUp(ActivityLogin.this).execute(surname.getText().toString(), firstname.getText().toString(),
                                    email.getText().toString(), phoneNo.getText().toString(), "345677", enterPasword.getText().toString(),
                                    String.valueOf(joinedType));

                        } else {
                            //Toast.makeText(ActivityLogin.this, "Check Filled Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (normalTabIndex == 2) {
                        if (userReadyToSignUp == 0 || surname.getText().toString().length() < 3 || firstname.getText().toString().length() < 3
                                || enterPasword.getText().toString().length() < 3 || phoneNo.getText().toString().length() < 3
                                || selectedVendorType == 0) {
                            //Toast.makeText(ActivityLogin.this, "Checked Filled Data", Toast.LENGTH_SHORT).show();
                        } else if (userReadyToSignUp == 1 && surname.getText().toString().length() >= 3 && firstname.getText().toString().length() >= 3
                                && enterPasword.getText().toString().length() >= 3 && phoneNo.getText().toString().length() >= 3
                                && selectedVendorType > 0) {
                            //Toast.makeText(ActivityLogin.this, "READY TO SIGN UP", Toast.LENGTH_SHORT).show();
                            joinedSurname = surname.getText().toString();
                            joinedFirstname = firstname.getText().toString();
                            joinedEmail = email.getText().toString();
                            joinedPassword = enterPasword.getText().toString();
                            joinedPhoneNo = phoneNo.getText().toString();
                            joinedType = vendorType.getSelectedItemPosition();
                            mydatabase = openOrCreateDatabase("workchop.db", MODE_PRIVATE, null);
                            //mydatabase.execSQL("DROP TABLE TestLoginPassword;");
                            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS TestLoginPassword(password VARCHAR);");
                            mydatabase.execSQL(String.format("INSERT INTO TestLoginPassword VALUES('%s');", joinedPassword));
                            //Toast.makeText(ActivityLogin.this, "TABLE CREATED WITH PASSWORD - " + joinedPassword, Toast.LENGTH_LONG).show();
                            new vendorSignUp(ActivityLogin.this).execute(surname.getText().toString() + " " + firstname.getText().toString(),
                                    email.getText().toString(), phoneNo.getText().toString(), "345677", enterPasword.getText().toString(),
                                    String.valueOf(joinedType));

                        } else {
                            //Toast.makeText(ActivityLogin.this, "Check Filled Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (email.getText().toString().length() == 0 && firstname.getText().toString().length() >= 3 && surname.getText().toString().length() >= 3 &&
                            phoneNo.getText().toString().length() >= 3 && enterPasword.getText().toString().length() >= 3) {
                        userReadyToSignUp = 1;
                    } else if (email.getText().toString().length() > 0) {
                        if (email.getText().toString().contains("@") && email.getText().toString().contains(".")) {
                            new checkEmail(ActivityLogin.this).execute(email.getText().toString(), String.valueOf(normalTabIndex));
                        } else {
                            userReadyToSignUp = 0;
                            ////Toast.makeText(ActivityLogin.this,"Invalid Email Format",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            phoneNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (phoneNo.getText().toString().length() > 10) {
                        new checkPhoneNo(ActivityLogin.this).execute(phoneNo.getText().toString(), String.valueOf(normalTabIndex));
                    } else if (firstname.getText().toString().length() >= 3 && surname.getText().toString().length() >= 3 &&
                            phoneNo.getText().toString().length() >= 3 && enterPasword.getText().toString().length() >= 3) {
                        userReadyToSignUp = 1;
                    } else if (phoneNo.getText().toString().length() == 0) {
                        userReadyToSignUp = 0;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // code to execute when EditText loses focus
                        if (email.getText().toString().length() == 0) {
                            userReadyToSignUp = 1;
                        }
                    }
                }
            });
            phoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // code to execute when EditText loses focus
                        if (phoneNo.getText().toString().length() < 10) {
                            userReadyToSignUp = 0;
                        }
                    }
                }
            });
            aboutText1 = (TextView) findViewById(R.id.aboutText1);
            aboutText2 = (TextView) findViewById(R.id.aboutText2);
            aboutText2.setOnTouchListener(new TextViewHighlighterOnTouchListener(aboutText2));
            aboutText2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://www.workchopapp.com/terms.php";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            String text1 = "\n\nThe Workchop app is a reference platform that connects you with verified and reviewed vendors close to you. " +
                    "The vendors are typically reviewed and verified by friends in your contact list and friends in your friends' network.\n" +
                    "\n" +
                    "With Workchop you are able to search for vendors which are ranked based on relevance (sector location, reviews and the degree of use by your friends). " +
                    "Vendors can also create their own accounts through which they manage their online presence and communicate with potential customers " +
                    "like you.\n\n" +
                    "You can also add and review your own vendors on the Workchop platform to grow your workpoints (WP). " +
                    "'Workpoints (WP)' helps us rank users and vendors who enjoy using the Workchop app.\n" +
                    "\n" +
                    "We hope you enjoy using the app as much we enjoyed creating it.\n\nPlease contact us at support@workchoppapp.com, if you have any queries.\n" +
                    "\n" +
                    "Workchop, a product of Deftbase.\n";
            aboutText1.setText(text1);
            String udatax = aboutText2.getText().toString();
            SpannableString contentx = new SpannableString(udatax);
            contentx.setSpan(new UnderlineSpan(), 0, udatax.length(), 0);
            tab1.setTypeface(type);
            tab2.setTypeface(type);
            emailOrPhone.setTypeface(type);
            password.setTypeface(type);
            surname.setTypeface(type);
            firstname.setTypeface(type);
            email.setTypeface(type);
            enterPasword.setTypeface(type);
            aboutText2.setTypeface(type);
            aboutText1.setTypeface(type);
            phoneNo.setTypeface(type);
            forgotPassword.setTypeface(type);
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActivityLogin.this, ActivityForgotPassword.class);
                    startActivity(intent);
                }
            });
            signUpLink.setTypeface(type);
            signupButton.setTypeface(type);
            loginButton.setTypeface(type);
            //aboutText2.setText(contentx);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            float density = getResources().getDisplayMetrics().density;
            Resources r = getResources();
            float widthDp = (float) width / density;
            //Toast.makeText(this, String.valueOf(widthDp), Toast.LENGTH_LONG).show();

            //readContacts();

            if (widthDp <= 480) {
                email.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
                password.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
                surname.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
                firstname.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
                emailOrPhone.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
                vendorType.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
                enterPasword.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
                phoneNo.setLayoutParams(new LinearLayout.LayoutParams((int) (230 * density), (int) (37 * density)));
            }
            if (widthDp < 360) {
                email.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));
                password.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));
                surname.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));
                firstname.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));
                emailOrPhone.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));
                vendorType.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));
                enterPasword.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));
                phoneNo.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * density), (int) (33 * density)));

                curve1 = (ImageView) findViewById(R.id.curve1);
                curve2 = (ImageView) findViewById(R.id.curve2);
                curve3 = (ImageView) findViewById(R.id.curve3);
                curve4 = (ImageView) findViewById(R.id.curve4);
                curve5 = (ImageView) findViewById(R.id.curve5);
                curve6 = (ImageView) findViewById(R.id.curve6);
                curve7 = (ImageView) findViewById(R.id.curve7);
                curve8 = (ImageView) findViewById(R.id.curve8);
                curve1.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));
                curve2.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));
                curve3.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));
                curve4.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));
                curve5.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));

                curve6.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));
                curve7.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));
                curve8.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), (int) (33 * density)));
            }


        }
        else{
            //deleteDatabase("workchop_user_account.db");
            try {
                Log.v("NOT FIRST TIME", " NOT FIRST TIME LOGIN");
                mydatabase2 = openOrCreateDatabase("workchop_user_account.db", MODE_PRIVATE, null);
                Cursor resultSet = mydatabase2.rawQuery("Select * from LoginDetails", null);
                resultSet.moveToFirst();
                Log.v("TO STRING", resultSet.getString(0));
                String userEmail = resultSet.getString(0).split("==")[0];
                String userPassword = resultSet.getString(0).split("==")[1];
                Log.v("Comparing ", resultSet.getString(0).split("==")[2]);
                if (resultSet.getString(0).split("==")[2].equals("1")) {
                    new userSignInAuto(ActivityLogin.this).execute(userEmail, userPassword);
                }
                else {
                    new vendorSignInAuto(ActivityLogin.this).execute(userEmail, userPassword);
                }
            }
            catch(Exception e){
                deleteDatabase("workchop_user_account.db");
                ActivityLogin.this.finishAffinity();
                Intent intent = new Intent(ActivityLogin.this,ActivityLogin.class);
                startActivity(intent);
            }
        }
    }

    public void changeBackground() {
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v("Handler", "Called");
                if(imageIndex == 0){
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            backgroundImage2.setAlpha((float)1);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            backgroundImage.setAlpha((float)0);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    backgroundImage.startAnimation(fadeOut);
                    backgroundImage2.startAnimation(fadeIn);
                    imageIndex++;
                }
                else if(imageIndex == 1){
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            backgroundImage3.setAlpha((float)1);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            backgroundImage2.setAlpha((float)0);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    backgroundImage2.startAnimation(fadeOut);
                    backgroundImage3.startAnimation(fadeIn);
                    imageIndex++;
                }
                else if(imageIndex == 2){
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            backgroundImage4.setAlpha((float)1);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            backgroundImage3.setAlpha((float)0);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    backgroundImage3.startAnimation(fadeOut);
                    backgroundImage4.startAnimation(fadeIn);
                    imageIndex++;
                }
                else if(imageIndex == 3){
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            backgroundImage.setAlpha((float)1);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            backgroundImage4.setAlpha((float)0);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    backgroundImage4.startAnimation(fadeOut);
                    backgroundImage.startAnimation(fadeIn);
                    imageIndex=0;
                }
                changeBackground();
            }
        }, 5000);
    }

    private class checkEmail extends AsyncTask<String,Void,String> {
        Context context;

        public checkEmail(Context c) {
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/check_email.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "email_address="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&mode="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("LINK ADDRESS",dataUrl+"?"+dataUrlParameters);
            try {
                url = new URL(dataUrl + "?" + dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpParams param = client.getParams();
                HttpConnectionParams.setConnectionTimeout(param, 3000);
                HttpConnectionParams.setSoTimeout(param, 3000);

                HttpGet request = new HttpGet();
                request.setParams(param);
                request.setURI(new URI(dataUrl + "?" + dataUrlParameters));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line = "";
                Log.v("ADDRESS", dataUrl + "?" + dataUrlParameters);
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        if(sb.toString().equals("1")) {
                            Toast.makeText(context, "Email Address exists", Toast.LENGTH_LONG).show();
                            userReadyToSignUp = 0;
                        }
                        else{
                            userReadyToSignUp = 1;
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
            return null;
        }
    }

    private class checkPhoneNo extends AsyncTask<String,Void,String> {
        Context context;

        public checkPhoneNo(Context c) {
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/check_phoneno.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "phone_number="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&mode="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("LINK ADDRESS",dataUrl+"?"+dataUrlParameters);
            try {
                url = new URL(dataUrl + "?" + dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpParams param = client.getParams();
                HttpConnectionParams.setConnectionTimeout(param, 3000);
                HttpConnectionParams.setSoTimeout(param, 3000);

                HttpGet request = new HttpGet();
                request.setParams(param);
                request.setURI(new URI(dataUrl + "?" + dataUrlParameters));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line = "";
                Log.v("ADDRESS", dataUrl + "?" + dataUrlParameters);
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        if(sb.toString().equals("1")) {
                            Toast.makeText(context, "Phone Number exists", Toast.LENGTH_LONG).show();
                            userReadyToSignUp = 0;
                        }
                        else{
                            userReadyToSignUp = 1;
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

    private class userSignUp extends AsyncTask<String,Void,String> {
        Context context;

        public userSignUp(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/user_signup.php";
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateFormat dateYear = new SimpleDateFormat("yyyy");
            DateFormat dateMonth = new SimpleDateFormat("MM");
            DateFormat dateDay = new SimpleDateFormat("dd");
            DateFormat dateHour = new SimpleDateFormat("HH");
            DateFormat dateMinute = new SimpleDateFormat("mm");
            DateFormat dateSecond = new SimpleDateFormat("ss");

            Date date = new Date();
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "surname="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&firstname="+URLEncoder.encode(params[1],"UTF-8")
                        +"&email_address="+URLEncoder.encode(params[2],"UTF-8")
                        +"&mobile_number="+URLEncoder.encode(params[3],"UTF-8")
                        +"&location_index=0&user_id="+URLEncoder.encode(params[4],"UTF-8")
                        +"&password="+URLEncoder.encode(params[5],"UTF-8")
                        +"&suspended_index=0&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityLogin.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                            surname.setText(""); firstname.setText(""); email.setText(""); phoneNo.setText("");
                            enterPasword.setText("");
                            userId = sb.toString().split("--")[1];
                            CharSequence colors[] = new CharSequence[] {"Email Address", "Mobile Phone"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                            LayoutInflater li = LayoutInflater.from(ActivityLogin.this.getApplicationContext());
                            View promptsView = li.inflate(R.layout.prompt, null);
                            builder.setTitle("Choose where to send confirmation code");

                            builder.setItems(colors, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // the user clicked on colors[which]
                                    confirmationMode = which + 1;
                                    //Toast.makeText(ActivityLogin.this,userId+"--"+which,Toast.LENGTH_LONG).show();

                                    if(which == 0) {
                                        new sendRegistrationCode(ActivityLogin.this).execute(userId, userId.substring(0, 6),
                                                String.valueOf(confirmationMode));
                                    }
                                    else if(which == 1){
                                        new sendToPhone(ActivityLogin.this).execute(userId, userId.substring(0, 6),
                                                String.valueOf(confirmationMode));
                                        ////Toast.makeText(ActivityLogin.this,"Option 2",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            builder.show();
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

    private class vendorSignUp extends AsyncTask<String,Void,String> {
        Context context;

        public vendorSignUp(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/vendor_sign_up.php";
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateFormat dateYear = new SimpleDateFormat("yyyy");
            DateFormat dateMonth = new SimpleDateFormat("MM");
            DateFormat dateDay = new SimpleDateFormat("dd");
            DateFormat dateHour = new SimpleDateFormat("HH");
            DateFormat dateMinute = new SimpleDateFormat("mm");
            DateFormat dateSecond = new SimpleDateFormat("ss");

            Date date = new Date();
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "name="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&email_address="+URLEncoder.encode(params[1],"UTF-8")
                        +"&mobile_number="+URLEncoder.encode(params[2],"UTF-8")
                        +"&location_index=0&user_id="+URLEncoder.encode(params[3],"UTF-8")
                        +"&password="+URLEncoder.encode(params[4],"UTF-8")
                        +"&type="+URLEncoder.encode(params[5],"UTF-8")
                        +"&suspended_index=0&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityLogin.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                            surname.setText(""); firstname.setText(""); email.setText(""); phoneNo.setText("");
                            enterPasword.setText("");
                            //selectedVendorType = 0;
                            //joinedType = 0;
                            vendorId = sb.toString().split("--")[1];
                            CharSequence colors[] = new CharSequence[] {"Email Address", "Mobile Phone"};

                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                            LayoutInflater li = LayoutInflater.from(ActivityLogin.this.getApplicationContext());
                            View promptsView = li.inflate(R.layout.prompt, null);
                            builder.setTitle("Choose where to send confirmation code");

                            builder.setItems(colors, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // the user clicked on colors[which]
                                    confirmationMode = which + 1;
                                    //Toast.makeText(ActivityLogin.this,vendorId+"--"+which,Toast.LENGTH_LONG).show();

                                    if(which == 0) {
                                        new sendRegistrationCodeVendor(ActivityLogin.this).execute(vendorId, vendorId.substring(0, 6),
                                                String.valueOf(confirmationMode));
                                    }
                                    else if(which == 1){
                                        new sendToPhoneVendor(ActivityLogin.this).execute(vendorId, vendorId.substring(0, 6),
                                                String.valueOf(confirmationMode));
                                        ////Toast.makeText(ActivityLogin.this,"Option 2",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            builder.show();
                        }
                        else{
                            Log.v("ERROR CODE",sb.toString());
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

    private class userSignIn extends AsyncTask<String,Void,String> {
        Context context;

        public userSignIn(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/user_login.php";

            Date date = new Date();
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "email="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityLogin.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("LINK ADDRESS",dataUrl+"?"+dataUrlParameters);
            try{

                url = new URL(dataUrl+"?"+dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpParams paramsx = client.getParams();
                HttpConnectionParams.setConnectionTimeout(paramsx, 3000);
                HttpConnectionParams.setSoTimeout(paramsx, 3000);
                HttpGet request = new HttpGet();
                request.setParams(paramsx);

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
                        Log.v("KEY",sb.toString());
                        if(sb.toString().equals("false")) {
                            signingInProgress.dismiss();
                            Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                        else if(sb.toString().split("--")[0].equals("logged")){
                            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Workchop", "files");
                            File gpxfile = new File(root, "phone_number.txt");
                            mydatabase2.execSQL("CREATE TABLE IF NOT EXISTS LoginDetails(detail VARCHAR);");
                            mydatabase2.execSQL(String.format("INSERT INTO LoginDetails VALUES('%s');",
                                    params[0]+"=="+params[1]+"==1"));
                            if(gpxfile.exists()){
                                StringBuilder text = new StringBuilder();
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(gpxfile));
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        text.append(line);
                                    }
                                    br.close();
                                    //Toast.makeText(context,text.toString(),Toast.LENGTH_SHORT).show();
                                    if((params[0].equals(text.toString())) && (params[0].equals(sb.toString().split("--")[4]) ||
                                            params[0].equals(sb.toString().split("--")[3]))){
                                        new userSignInAuto(context).execute(params[0],params[1]);
                                    }
                                    else {
                                        signingInProgress.dismiss();
                                        Toast.makeText(context, "Account already logged in", Toast.LENGTH_SHORT).show();
                                        Date date = new Date();
                                        DateFormat dateDay = new SimpleDateFormat("dd");
                                        DateFormat dateHour = new SimpleDateFormat("HH");
                                        DateFormat dateMinute = new SimpleDateFormat("mm");
                                        DateFormat dateSecond = new SimpleDateFormat("ss");
                                        final String sentConfirmationCode = dateDay.format(date)+dateSecond.format(date)+dateMinute.format(date)+
                                                dateHour.format(date);
                                        new sendLoginToPhone(context).execute(sentConfirmationCode,sb.toString().split("--")[3]);
                                        final EditText input = new EditText(context);
                                        new AlertDialog.Builder(ActivityLogin.this)
                                                .setView(input)
                                                .setIcon(R.drawable.workchopphoneicon)
                                                .setTitle("Confirm Account")
                                                .setMessage("Enter Confirmation Code To Login")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(input.getText().toString().equals(sentConfirmationCode)){
                                                            new userSignInAuto(context).execute(params[0],params[1]);
                                                        }
                                                        else{
                                                            Toast.makeText(context,"Wrong Code",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .show();
                                    }
                                }
                                catch (IOException e) {
                                    //You'll need to add proper error handling here
                                }
                            }
                            else {
                                signingInProgress.dismiss();
                                Toast.makeText(context, "Account already logged in", Toast.LENGTH_SHORT).show();
                                Date date = new Date();
                                DateFormat dateDay = new SimpleDateFormat("dd");
                                DateFormat dateHour = new SimpleDateFormat("HH");
                                DateFormat dateMinute = new SimpleDateFormat("mm");
                                DateFormat dateSecond = new SimpleDateFormat("ss");
                                final String sentConfirmationCode = dateDay.format(date)+dateSecond.format(date)+dateMinute.format(date)+
                                        dateHour.format(date);
                                new sendLoginToPhone(context).execute(sentConfirmationCode,sb.toString().split("--")[3]);
                                final EditText input = new EditText(context);
                                new AlertDialog.Builder(ActivityLogin.this)
                                        .setView(input)
                                        .setIcon(R.drawable.workchopphoneicon)
                                        .setTitle("Confirm Account")
                                        .setMessage("Enter Confirmation Code To Login")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            if(input.getText().toString().equals(sentConfirmationCode)){
                                                new userSignInAuto(context).execute(params[0],params[1]);
                                            }
                                            else{
                                                Toast.makeText(context,"Wrong Code",Toast.LENGTH_SHORT).show();
                                            }
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        }
                        else{
                            //Toast.makeText(context, "Correct Email and Password", Toast.LENGTH_SHORT).show();
                            String [] values = sb.toString().split("--");
                            loggedFirstname = values[0];
                            loggedSurname = values[1];
                            loggedSurname = values[1];
                            loggedPhoneNo = values[2];
                            loggedEmail = values[3];
                            loggedLocation = values[4];
                            loggedId = values[5];
                            //Toast.makeText(ActivityLogin.this, loggedId,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("val1",loggedFirstname);
                            intent.putExtra("val2",loggedSurname);
                            intent.putExtra("val3",loggedPhoneNo);
                            intent.putExtra("val4",loggedEmail);
                            intent.putExtra("val5",loggedLocation);
                            intent.putExtra("val6",loggedId);
                            intent.putExtra("val7","loginScreen");

                            mydatabase2.execSQL("CREATE TABLE IF NOT EXISTS LoginDetails(detail VARCHAR);");
                            mydatabase2.execSQL(String.format("INSERT INTO LoginDetails VALUES('%s');",
                                    params[0]+"=="+params[1]+"==1"));
                            Log.v("DATABASE TABLE","CREATED");

                            signingInProgress.dismiss();
                            startActivity(intent);
                            finish();
                            //new getUserDetails(ActivityLogin.this).execute(loggedEmail, loggedPassword);
                            //Intent int1 = new Intent(ActivityLogin.this, ActivityMain.class);
                            //startActivity(int1);
                            //finish();
                        }
                    }
                });

                in.close();
            }

            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        signingInProgress.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        signingInProgress.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        signingInProgress.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            finally{
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        signingInProgress.dismiss();
                    }
                });
            }
            return null;
        }
    }

    private class userSignInAuto extends AsyncTask<String,Void,String> {
        Context context;

        public userSignInAuto(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/user_login.php";

            Date date = new Date();
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "email="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityLogin.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("LINK ADDRESS",dataUrl+"?"+dataUrlParameters);
            try{

                url = new URL(dataUrl+"?"+dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpParams paramsx = client.getParams();
                HttpConnectionParams.setConnectionTimeout(paramsx, 3000);
                HttpConnectionParams.setSoTimeout(paramsx, 3000);
                HttpGet request = new HttpGet();
                request.setParams(paramsx);

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
                        Log.v("KEY",sb.toString());
                        if(sb.toString().equals("false")) {
                            signingInProgress.dismiss();
                            //Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                        else if(sb.toString().split("--")[0].equals("logged")){
                            String [] values = sb.toString().split("--");
                            loggedFirstname = values[1];
                            loggedSurname = values[2];
                            loggedPhoneNo = values[3];
                            loggedEmail = values[4];
                            loggedLocation = values[5];
                            loggedId = values[6];
                            //Toast.makeText(ActivityLogin.this, loggedId,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("val1",loggedFirstname);
                            intent.putExtra("val2",loggedSurname);
                            intent.putExtra("val3",loggedPhoneNo);
                            intent.putExtra("val4",loggedEmail);
                            intent.putExtra("val5",loggedLocation);
                            intent.putExtra("val6",loggedId);
                            intent.putExtra("val7","loginScreen");
                            //signingInProgress.dismiss();
                            startActivity(intent);
                            finish();
                        }
                        else{
                            //Toast.makeText(context, "Correct Email and Password", Toast.LENGTH_SHORT).show();
                            String [] values = sb.toString().split("--");
                            loggedFirstname = values[0];
                            loggedSurname = values[1];
                            loggedSurname = values[1];
                            loggedPhoneNo = values[2];
                            loggedEmail = values[3];
                            loggedLocation = values[4];
                            loggedId = values[5];
                            //Toast.makeText(ActivityLogin.this, loggedId,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("val1",loggedFirstname);
                            intent.putExtra("val2",loggedSurname);
                            intent.putExtra("val3",loggedPhoneNo);
                            intent.putExtra("val4",loggedEmail);
                            intent.putExtra("val5",loggedLocation);
                            intent.putExtra("val6",loggedId);
                            intent.putExtra("val7","loginScreen");
                            //signingInProgress.dismiss();
                            startActivity(intent);
                            finish();
                            //new getUserDetails(ActivityLogin.this).execute(loggedEmail, loggedPassword);
                            //Intent int1 = new Intent(ActivityLogin.this, ActivityMain.class);
                            //startActivity(int1);
                            //finish();
                        }
                    }
                });

                in.close();
            }

            catch(SocketTimeoutException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        ActivityLogin.this.finishAffinity();
                        Intent intent = new Intent(context,ActivityTerms.class);
                        startActivity(intent);
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        ActivityLogin.this.finishAffinity();
                        Intent intent = new Intent(context,ActivityTerms.class);
                        startActivity(intent);
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        ActivityLogin.this.finishAffinity();
                        Intent intent = new Intent(context,ActivityTerms.class);
                        startActivity(intent);
                    }
                });
            }
            return null;
        }
    }

    private class vendorSignIn extends AsyncTask<String,Void,String> {
        Context context;

        public vendorSignIn(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/vendor_login.php";

            Date date = new Date();
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "email="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityLogin.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("LINK ADDRESS",dataUrl+"?"+dataUrlParameters);
            try{

                url = new URL(dataUrl+"?"+dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpParams paramsx = client.getParams();
                HttpConnectionParams.setConnectionTimeout(paramsx, 3000);
                HttpConnectionParams.setSoTimeout(paramsx, 3000);
                HttpGet request = new HttpGet();
                request.setParams(paramsx);

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
                        if(sb.toString().equals("false")) {
                            signingInProgress.dismiss();
                            Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                        else if(sb.toString().split("--")[0].equals("logged")){
                            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"Workchop", "files");
                            File gpxfile = new File(root, "phone_number_vendor.txt");
                            mydatabase2.execSQL("CREATE TABLE IF NOT EXISTS LoginDetails(detail VARCHAR);");
                            mydatabase2.execSQL(String.format("INSERT INTO LoginDetails VALUES('%s');",
                                    params[0]+"=="+params[1]+"==2"));
                            if(gpxfile.exists()){
                                StringBuilder text = new StringBuilder();
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(gpxfile));
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        text.append(line);
                                    }
                                    br.close();
                                    if(sb.toString().split("--").length == 6) {
                                        if ((params[0].equals(text.toString())) && (params[0].equals(sb.toString().split("--")[2]) ||
                                                params[0].equals(sb.toString().split("--")[6]))) {
                                            //signingInProgress.dismiss();
                                            new vendorSignInAuto(context).execute(params[0], params[1]);
                                        }
                                        else {
                                            signingInProgress.dismiss();
                                            Toast.makeText(context, "Account already logged in", Toast.LENGTH_SHORT).show();
                                            Date date = new Date();
                                            DateFormat dateDay = new SimpleDateFormat("dd");
                                            DateFormat dateHour = new SimpleDateFormat("HH");
                                            DateFormat dateMinute = new SimpleDateFormat("mm");
                                            DateFormat dateSecond = new SimpleDateFormat("ss");
                                            final String sentConfirmationCode = dateDay.format(date)+dateSecond.format(date)+dateMinute.format(date)+
                                                    dateHour.format(date);
                                            new sendLoginToPhone(context).execute(sentConfirmationCode,sb.toString().split("--")[2]);
                                            final EditText input = new EditText(context);
                                            new AlertDialog.Builder(ActivityLogin.this)
                                                    .setView(input)
                                                    .setIcon(R.drawable.workchopphoneicon)
                                                    .setTitle("Confirm Account")
                                                    .setMessage("Enter Confirmation Code To Login")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if(input.getText().toString().equals(sentConfirmationCode)){
                                                                new vendorSignInAuto(context).execute(params[0],params[1]);
                                                            }
                                                            else{
                                                                Toast.makeText(context,"Wrong Code",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                    else{
                                        if (params[0].equals(text.toString()) && params[0].equals(sb.toString().split("--")[2])) {
                                            //signingInProgress.dismiss();
                                            new vendorSignInAuto(context).execute(params[0], params[1]);
                                        }
                                        else {
                                            signingInProgress.dismiss();
                                            Toast.makeText(context, "Account already logged in", Toast.LENGTH_SHORT).show();
                                            Date date = new Date();
                                            DateFormat dateDay = new SimpleDateFormat("dd");
                                            DateFormat dateHour = new SimpleDateFormat("HH");
                                            DateFormat dateMinute = new SimpleDateFormat("mm");
                                            DateFormat dateSecond = new SimpleDateFormat("ss");
                                            final String sentConfirmationCode = dateDay.format(date)+dateSecond.format(date)+dateMinute.format(date)+
                                                    dateHour.format(date);
                                            new sendLoginToPhone(context).execute(sentConfirmationCode,sb.toString().split("--")[2]);
                                            final EditText input = new EditText(context);
                                            new AlertDialog.Builder(ActivityLogin.this)
                                                    .setView(input)
                                                    .setIcon(R.drawable.workchopphoneicon)
                                                    .setTitle("Confirm Account")
                                                    .setMessage("Enter Confirmation Code To Login")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if(input.getText().toString().equals(sentConfirmationCode)){
                                                                new vendorSignInAuto(context).execute(params[0],params[1]);
                                                            }
                                                            else{
                                                                Toast.makeText(context,"Wrong Code",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                }
                                catch (IOException e) {
                                    //You'll need to add proper error handling here
                                }
                            }
                            else {
                                signingInProgress.dismiss();
                                Toast.makeText(context, "Account already logged in", Toast.LENGTH_SHORT).show();
                                Date date = new Date();
                                DateFormat dateDay = new SimpleDateFormat("dd");
                                DateFormat dateHour = new SimpleDateFormat("HH");
                                DateFormat dateMinute = new SimpleDateFormat("mm");
                                DateFormat dateSecond = new SimpleDateFormat("ss");
                                final String sentConfirmationCode = dateDay.format(date)+dateSecond.format(date)+dateMinute.format(date)+
                                        dateHour.format(date);
                                new sendLoginToPhone(context).execute(sentConfirmationCode,sb.toString().split("--")[2]);
                                final EditText input = new EditText(context);
                                new AlertDialog.Builder(ActivityLogin.this)
                                        .setView(input)
                                        .setIcon(R.drawable.workchopphoneicon)
                                        .setTitle("Confirm Account")
                                        .setMessage("Enter Confirmation Code To Login")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(input.getText().toString().equals(sentConfirmationCode)){
                                                    new vendorSignInAuto(context).execute(params[0],params[1]);
                                                }
                                                else{
                                                    Toast.makeText(context,"Wrong Code",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                            }
                        }
                        else{
                            //Toast.makeText(context, "Correct Email and Password", Toast.LENGTH_LONG).show();
                            String [] values = sb.toString().split("--");
                            String vendorName = values[0];
                            String vendorPhoneNo = values[1];
                            String vendorLocation = values[2];
                            String vendorId = values[3];
                            String vendorType = values[4];
                            String vendorEmail = " ";
                            if(values.length == 6){
                                vendorEmail = values[5];
                            }


                            Intent intent = new Intent(ActivityLogin.this, ActivityVendorMain.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("val1",vendorName);
                            intent.putExtra("val2",vendorPhoneNo);
                            intent.putExtra("val3",vendorLocation);
                            intent.putExtra("val4",vendorId);
                            intent.putExtra("val5",vendorType);
                            intent.putExtra("val6",vendorEmail);
                            intent.putExtra("val7","loginScreen");

                            mydatabase2.execSQL("CREATE TABLE IF NOT EXISTS LoginDetails(detail VARCHAR);");
                            mydatabase2.execSQL(String.format("INSERT INTO LoginDetails VALUES('%s');",
                                    params[0]+"=="+params[1]+"==2"));

                            signingInProgress.dismiss();
                            startActivity(intent);
                        }
                    }
                });

                in.close();
            }

            catch(SocketTimeoutException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                        signingInProgress.dismiss();
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                        signingInProgress.dismiss();
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                        signingInProgress.dismiss();
                    }
                });
            }
            finally{
                signingInProgress.dismiss();
            }
            return null;
        }
    }

    private class vendorSignInAuto extends AsyncTask<String,Void,String> {
        Context context;

        public vendorSignInAuto(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/vendor_login.php";

            Date date = new Date();
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "email="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(ActivityLogin.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            Log.v("LINK ADDRESS",dataUrl+"?"+dataUrlParameters);
            try{

                url = new URL(dataUrl+"?"+dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpParams paramsx = client.getParams();
                HttpConnectionParams.setConnectionTimeout(paramsx, 3000);
                HttpConnectionParams.setSoTimeout(paramsx, 3000);
                HttpGet request = new HttpGet();
                request.setParams(paramsx);

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
                        if(sb.toString().equals("false")) {
                            signingInProgress.dismiss();
                            //Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                        else if(sb.toString().split("--")[0].equals("logged")){
                            //Toast.makeText(context, "Account already logged in", Toast.LENGTH_SHORT).show();
                            String [] values = sb.toString().split("--");
                            String vendorName = values[1];
                            String vendorPhoneNo = values[2];
                            String vendorLocation = values[3];
                            String vendorId = values[4];
                            String vendorType = values[5];
                            String vendorEmail = " ";
                            if(values.length == 7){
                                vendorEmail = values[6];
                            }
                            Intent intent = new Intent(ActivityLogin.this, ActivityVendorMain.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("val1",vendorName);
                            intent.putExtra("val2",vendorPhoneNo);
                            intent.putExtra("val3",vendorLocation);
                            intent.putExtra("val4",vendorId);
                            intent.putExtra("val5",vendorType);
                            intent.putExtra("val6",vendorEmail);
                            intent.putExtra("val7","loginScreen");
                            //signingInProgress.dismiss();
                            startActivity(intent);
                        }
                        else{
                            //Toast.makeText(context, "Correct Email and Password", Toast.LENGTH_LONG).show();
                            String [] values = sb.toString().split("--");
                            String vendorName = values[0];
                            String vendorPhoneNo = values[1];
                            String vendorLocation = values[2];
                            String vendorId = values[3];
                            String vendorType = values[4];
                            String vendorEmail = " ";
                            if(values.length == 6){
                                vendorEmail = values[5];
                            }
                            Intent intent = new Intent(ActivityLogin.this, ActivityVendorMain.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("val1",vendorName);
                            intent.putExtra("val2",vendorPhoneNo);
                            intent.putExtra("val3",vendorLocation);
                            intent.putExtra("val4",vendorId);
                            intent.putExtra("val5",vendorType);
                            intent.putExtra("val6",vendorEmail);
                            intent.putExtra("val7","loginScreen");
                            //signingInProgress.dismiss();
                            startActivity(intent);
                        }
                    }
                });

                in.close();
            }

            catch(SocketTimeoutException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        ActivityLogin.this.finishAffinity();
                        Intent intent = new Intent(context,ActivityTerms.class);
                        startActivity(intent);
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        ActivityLogin.this.finishAffinity();
                        Intent intent = new Intent(context,ActivityTerms.class);
                        startActivity(intent);
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        try {
                            //Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                            signingInProgress.dismiss();
                        }
                        catch (NullPointerException e){
                            ActivityLogin.this.finishAffinity();
                            Intent intent = new Intent(context,ActivityTerms.class);
                            startActivity(intent);
                        }
                    }
                });
            }
            return null;
        }
    }

    private class getUserDetails extends AsyncTask<String,Void,String> {
        Context context;

        public getUserDetails(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/user_login.php";

            Date date = new Date();
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "email="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&password="+URLEncoder.encode(params[1],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;
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

                in.close();
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class sendRegistrationCode extends AsyncTask<String,Void,String> {
        Context context;

        public sendRegistrationCode(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/send_user_registration_code.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/get_user_registration_code.php";
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
                dataUrlParameters = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&code="+URLEncoder.encode(params[1],"UTF-8")
                        +"&mode="+URLEncoder.encode(params[2],"UTF-8")
                        +"&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");
                dataUrlParameters2 = "id="+URLEncoder.encode(userId,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;
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
                Log.v("ADDRESS",dataUrl2+"?"+dataUrlParameters2);
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }
                confirmationCode = sb2.toString();
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Registration Code Sent To Email", Toast.LENGTH_LONG).show();
                        Log.v("Confrer",confirmationCode);
                        readContacts();
                    }
                });

                in.close();
            }

            catch(Exception e){
				Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class sendRegistrationCodeVendor extends AsyncTask<String,Void,String> {
        Context context;

        public sendRegistrationCodeVendor(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/send_vendor_registration_code.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/get_vendor_registration_code.php";
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
                dataUrlParameters = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&code="+URLEncoder.encode(params[1],"UTF-8")
                        +"&mode="+URLEncoder.encode(params[2],"UTF-8")
                        +"&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");
                dataUrlParameters2 = "id="+URLEncoder.encode(vendorId,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;
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
                Log.v("ADDRESS",dataUrl2+"?"+dataUrlParameters2);
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }
                confirmationCode = sb2.toString();
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Registration Code Sent To Email", Toast.LENGTH_LONG).show();
                        Log.v("Confrer",confirmationCode);
                        DialogPromptVendor dpp = new DialogPromptVendor(confirmationCode, joinedSurname, joinedFirstname, joinedPhoneNo,
                                joinedPassword, joinedEmail, vendorId, String.valueOf(joinedType));
                        Bundle b = new Bundle();
                        Log.v("Confrer",confirmationCode);
                        b.putString("val",confirmationCode);
                        dpp.setArguments(b);
                        dpp.show(getFragmentManager(), "dialog15");
                        //readContacts();
                    }
                });

                in.close();
            }

            catch(Exception e){
				Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                Log.v("ERROR",e.getMessage());
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
            String dataUrla = "http://workchopapp.com/mobile_app/send_user_registration_code.php";
            String dataUrlb = "http://workchopapp.com/mobile_app/get_user_registration_code.php";

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

            try {
                dataUrlParametersa = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&code="+URLEncoder.encode(params[1],"UTF-8")
                        +"&mode="+URLEncoder.encode(params[2],"UTF-8")
                        +"&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");
                dataUrlParametersb= "id="+URLEncoder.encode(userId,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;
            try{
                HttpClient clienta = new DefaultHttpClient();
                HttpGet requesta = new HttpGet();
                requesta.setURI(new URI(dataUrla+"?"+dataUrlParametersa));
                HttpResponse responsea = clienta.execute(requesta);
                BufferedReader ina = new BufferedReader(new InputStreamReader(responsea.getEntity().getContent()));

                final StringBuffer sba = new StringBuffer("");
                String linea="";
                Log.v("ADDRESS-A",dataUrla+"?"+dataUrlParametersa);
                while ((linea = ina.readLine()) != null) {
                    sba.append(linea);
                    break;
                }
                confirmationCode = params[1];
                Handler ha = new Handler(Looper.getMainLooper());
                ha.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Registration Code Sent To Phone ", Toast.LENGTH_LONG).show();
                        //readContacts();
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
                        //Toast.makeText(context, "Response =>"+ sessionID, Toast.LENGTH_LONG).show();
                    }
                });
                String urlData = "http://www.smslive247.com/http/index.aspx?cmd=sendmsg&sessionid="
                        +sessionID+"&message=";
                String urlData3 = "&sender=Workchop&sendto="+joinedPhoneNo+"&msgtype=0";
                String urlData2="";
                try {
                    urlData2 = URLEncoder.encode("Your Confirmation Code is "+confirmationCode,"UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                        //Toast.makeText(context, "Response Code=>"+ newResponse, Toast.LENGTH_LONG).show();
                        readContacts();
                    }
                });
                in.close();
                in2.close();
            }
            catch(Exception e){
				Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class sendToPhoneVendor extends AsyncTask<String,Void,String> {
        Context context;

        public sendToPhoneVendor(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://www.smslive247.com/http/index.aspx?cmd=login&owneremail=donpelumos@gmail.com&subacct=EMCONF&subacctpwd=workchop12345";
            String dataUrla = "http://workchopapp.com/mobile_app/send_vendor_registration_code.php";
            String dataUrlb = "http://workchopapp.com/mobile_app/get_vendor_registration_code.php";

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

            try {
                dataUrlParametersa = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&code="+URLEncoder.encode(params[1],"UTF-8")
                        +"&mode="+URLEncoder.encode(params[2],"UTF-8")
                        +"&date_year="+URLEncoder.encode(dateYear.format(date),"UTF-8")
                        +"&date_month="+URLEncoder.encode(dateMonth.format(date),"UTF-8")
                        +"&date_day="+URLEncoder.encode(dateDay.format(date),"UTF-8")
                        +"&date_hour="+URLEncoder.encode(dateHour.format(date),"UTF-8")
                        +"&date_minute="+URLEncoder.encode(dateMinute.format(date),"UTF-8")
                        +"&date_second="+URLEncoder.encode(dateSecond.format(date),"UTF-8");
                dataUrlParametersb= "id="+URLEncoder.encode(vendorId,"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;
            try{
                HttpClient clienta = new DefaultHttpClient();
                HttpGet requesta = new HttpGet();
                requesta.setURI(new URI(dataUrla+"?"+dataUrlParametersa));
                HttpResponse responsea = clienta.execute(requesta);
                BufferedReader ina = new BufferedReader(new InputStreamReader(responsea.getEntity().getContent()));

                final StringBuffer sba = new StringBuffer("");
                String linea="";
                Log.v("ADDRESS-A",dataUrla+"?"+dataUrlParametersa);
                while ((linea = ina.readLine()) != null) {
                    sba.append(linea);
                    break;
                }
                confirmationCode = params[1];
                Handler ha = new Handler(Looper.getMainLooper());
                ha.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Registration Code Sent To Phone", Toast.LENGTH_SHORT).show();
                        //readContacts();
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
                        //Toast.makeText(context, "Response =>"+ sessionID, Toast.LENGTH_LONG).show();
                    }
                });
                String urlData = "http://www.smslive247.com/http/index.aspx?cmd=sendmsg&sessionid="
                        +sessionID+"&message=";
                String urlData3 = "&sender=Workchop&sendto="+joinedPhoneNo+"&msgtype=0";
                String urlData2="";
                try {
                    urlData2 = URLEncoder.encode("Your Confirmation Code is "+confirmationCode,"UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    //Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                        //Toast.makeText(context, "Response Code=>"+ newResponse, Toast.LENGTH_LONG).show();
                        //readContacts();
                        DialogPromptVendor dpp = new DialogPromptVendor(confirmationCode, joinedSurname, joinedFirstname, joinedPhoneNo,
                                joinedPassword, joinedEmail, vendorId, String.valueOf(joinedType));
                        Bundle b = new Bundle();
                        Log.v("Confrer",confirmationCode);
                        b.putString("val",confirmationCode);
                        dpp.setArguments(b);
                        dpp.show(getFragmentManager(), "dialog15");
                    }
                });
                in.close();
                in2.close();
            }
            catch(Exception e){
				Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
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
                        .replace("-","")
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
                Log.v("SORTED NO REPETITION "+inn,val.getValue() + "---" + val.getKey());
                inn++;
            }
            ArrayList<Integer> found = findVendors(treeMap);
            for(int i=0; i<foundVendors.size(); i++) {
                TextView text = new TextView(ActivityLogin.this);
                text.setTextColor(Color.rgb(235, 235, 235));
                text.setBackgroundColor(Color.GRAY);
                text.setText("Contact Details -- " + foundVendors.get(i));
                Log.v("FOUND VENDOR "+i, foundVendors.get(i));
                //contactList.addView(text);
            }
            /*//Toast.makeText(ActivityMain.this,String.valueOf(cur.getCount())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
                    Toast.LENGTH_SHORT).show();*/
            //Toast.makeText(ActivityLogin.this,String.valueOf(treeMap.size())+" Contacts Exist. Found "+foundVendors.size()+" Vendors",
            //        Toast.LENGTH_SHORT).show();
            DialogPrompt dpp = new DialogPrompt(confirmationCode, joinedSurname, joinedFirstname, joinedPhoneNo,
                    joinedPassword, joinedEmail, userId);
            Bundle b = new Bundle();
            Log.v("Confrer",confirmationCode);
            b.putString("val",confirmationCode);
            b.putStringArrayList("val2",finalContactsList);
            b.putStringArrayList("val3", foundVendors);
            dpp.setArguments(b);
            dpp.show(getFragmentManager(), "dialog15");
        }
        else{
            Toast.makeText(ActivityLogin.this,"Contacts don't exist on this device",Toast.LENGTH_SHORT).show();
        }

    }

    public ArrayList<Integer> findVendors(Map<String, String> vendorList){
        ArrayList<Integer> vendorIndexList = new ArrayList<Integer>();
        int index=0;
        for(Map.Entry<String, String> val : vendorList.entrySet()){
            finalContactsList.add(val.getKey());
            String [] valArray = val.getKey().split(" +");
            for(String value : valArray){
                if(value.toLowerCase().equals("mechanic") || value.toLowerCase().equals("gas") || value.toLowerCase().equals("makeup") || value.toLowerCase().equals("make up") ||
                        value.toLowerCase().equals("tailor") || value.toLowerCase().equals("fashion designer")|| value.toLowerCase().equals("fashion") ||
                        value.toLowerCase().equals("hair") || value.toLowerCase().equals("stylist")
                        || value.toLowerCase().equals("mech") || value.toLowerCase().equals("barber")){
                    vendorIndexList.add(index);
                    foundVendors.add(val.getKey());
                }
            }
            index++;
        }
        return vendorIndexList;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_terms, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            signingInProgress.dismiss();
        }
        catch(NullPointerException e){

        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(ActivityLogin.this)
                .setIcon(R.drawable.exit)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        try {
                            hand.removeCallbacksAndMessages(null);
                            deleteDatabase("workchop_user_account.db");
                            ActivityLogin.this.finishAffinity();
                        }
                        catch(NullPointerException e){

                        }
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

    private class sendLoginToPhone extends AsyncTask<String,Void,String> {
        Context context;

        public sendLoginToPhone(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String dataUrl = "http://www.smslive247.com/http/index.aspx?cmd=login&owneremail=donpelumos@gmail.com&subacct=EMCONF&subacctpwd=workchop12345";

            URL url = null;
            HttpURLConnection connection = null;
            HttpURLConnection connection2 = null;
            try{

                Handler ha = new Handler(Looper.getMainLooper());
                ha.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Confirmation Code Sent To Phone", Toast.LENGTH_LONG).show();
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
                        //Toast.makeText(context, "Response =>"+ sessionID, Toast.LENGTH_LONG).show();
                    }
                });
                String urlData = "http://www.smslive247.com/http/index.aspx?cmd=sendmsg&sessionid="
                        +sessionID+"&message=";
                String urlData3 = "&sender=Workchop&sendto="+params[1]+"&msgtype=0";
                String urlData2="";
                try {
                    urlData2 = URLEncoder.encode("Your Confirmation Code is "+params[0],"UTF-8");
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

    private class TabHighlighterOnTouchListener implements View.OnTouchListener {
        //This
        final Button imageButton;

        public TabHighlighterOnTouchListener(final Button imageButton) {
            super();
            this.imageButton = imageButton;
        }

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //grey color filter, you can change the color as you like
                imageButton.setAlpha((float)0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float)1.0);
            }
            return false;
        }

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
