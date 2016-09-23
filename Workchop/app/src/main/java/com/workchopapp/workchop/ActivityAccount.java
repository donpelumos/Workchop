package com.workchopapp.workchop;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * Created by BALE on 25/07/2016.
 */

public class ActivityAccount extends AppCompatActivity implements DialogPictureOptions.NoticeDialogListener,
DialogUserSelectLocation.ColorSelected{

    AlertDialog.Builder builder;
    android.app.AlertDialog dialog;
    ActionBar appBar;
    Button editAccount;
    ImageView accountImage;
    EditText surname, firstname, email, mobileNumber;
    String userId;
    int editMode;
    int SELECT_PHOTO = 1;
    String ba1 = "";
    InputStream is3 = null;
    TextView mobileNumberLabel, emailLabel, locationLabel;
    EditText userLocation;
    String checkPasswordResult = "";
    int workchopUserLocationIndex=0;
    int confirmationMode = 0;
    ProgressDialog progress;
    Typeface type;
    int timer=1;
    Handler imageLoaderHandler=null;
    AsyncTask<String,Void,String> pictureAsyncTask=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBar  = getSupportActionBar();
        appBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        setContentView(R.layout.activity_account);
        type = Typeface.createFromAsset(getAssets(),"fonts/GOTHIC.TTF");
        progress = new ProgressDialog(ActivityAccount.this);
        imageLoaderHandler = new Handler();
        pictureAsyncTask = new getUserPicture(ActivityAccount.this);

        progress.setTitle("Loading");
        progress.setMessage("Loading . . .");
        progress.show();

        editMode = 0;
        editAccount = (Button)findViewById(R.id.editAccount);
        editAccount.setOnTouchListener(new ButtonHighlighterOnTouchListener(editAccount));
        surname = (EditText)findViewById(R.id.surname);
        firstname = (EditText)findViewById(R.id.firstname);
        email = (EditText)findViewById(R.id.email);
        locationLabel = (TextView)findViewById(R.id.locationLabel);
        userLocation = (EditText)findViewById(R.id.userLocation);
        mobileNumber = (EditText)findViewById(R.id.mobileNumber);
        userId = getIntent().getStringExtra("userId");
        editAccount.setEnabled(true);
        new getUserDetails(ActivityAccount.this).execute(userId);

        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode == 1){
                    editMode = 0;
                    editAccount.setBackground(getResources().getDrawable(R.drawable.background_button_gold));
                    surname.setEnabled(false);
                    firstname.setEnabled(false);
                    mobileNumber.setEnabled(false);
                    email.setEnabled(false);
                    userLocation.setEnabled(false);
                    editAccount.setText("Edit");
                    new updateUserDetails(ActivityAccount.this).execute(userId,surname.getText().toString(), firstname.getText().toString(),
                            mobileNumber.getText().toString(), email.getText().toString(), String.valueOf(workchopUserLocationIndex));
                }
                else{
                    builder = new AlertDialog.Builder(ActivityAccount.this);


                    final EditText input = new EditText(ActivityAccount.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setTypeface(type);
                    builder.setView(input);
                    builder.setTitle("Enter Password");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String pwd = input.getText().toString();
                            new checkPassword(ActivityAccount.this).execute(userId,pwd);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialog = builder.create();

                    dialog.show();

                }
            }
        });
        accountImage = (ImageView)findViewById(R.id.accountImage);
        accountImage.setOnTouchListener(new ImageHighlighterOnTouchListener(accountImage));
        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPictureOptions fragment = new DialogPictureOptions();
                Bundle bundle = new Bundle();
                bundle.putString("userId",userId);
                fragment.show(getFragmentManager(),"dialog7");
            }
        });

        surname.setTypeface(type);
        firstname.setTypeface(type);
        mobileNumber.setTypeface(type);
        email.setTypeface(type);
        mobileNumberLabel = (TextView)findViewById(R.id.mobileNumberLabel);
        emailLabel = (TextView)findViewById(R.id.emailLabel);
        mobileNumberLabel.setTypeface(type);
        emailLabel.setTypeface(type);
        locationLabel.setTypeface(type);
        editAccount.setTypeface(type);
        userLocation.setTypeface(type);

        userLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    DialogUserSelectLocation dialogUserSelectLocation = new DialogUserSelectLocation();
                    dialogUserSelectLocation.show(getFragmentManager(), "dialog20");
                }
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pictureAsyncTask.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_account, menu);
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
            case R.id.signOut:
                new android.support.v7.app.AlertDialog.Builder(ActivityAccount.this)
                        .setIcon(R.drawable.signout)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                deleteDatabase("workchop_user_account.db");
                                ActivityAccount.this.finishAffinity();
                                new setLoggedOut(ActivityAccount.this).execute(userId);
                                //Intent intent = new Intent(ActivityMain.this, ActivityLogin.class);
                                //startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;
        }
        return true;

        //return super.onOptionsItemSelected(item);
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
                Toast.makeText(ActivityAccount.this,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onSelected(DialogFragment dialogFragment, int index) {
        if(index == 0){
            dialogFragment.dismiss();
            DialogPictureView fragment = new DialogPictureView();
            Bundle bundle = new Bundle();
            bundle.putString("userId",userId);
            fragment.setArguments(bundle);
            fragment.show(getFragmentManager(),"dialog7");
        }
        else if(index == 1){
            dialogFragment.dismiss();
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bitmap img = null;
                //Toast.makeText(ProfileSignUp.this, "valid Input", Toast.LENGTH_SHORT).show();
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    img = selectedImage;
                    //profilePic.setImageBitmap(selectedImage);
                    Drawable d = new BitmapDrawable(selectedImage);
                    accountImage.setImageDrawable(d);
                    //profilePictureInput.setText(data.getDataString());
                    //profilePictureInput.setTextSize(11);
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(imageUri, "image/*");
                    //set crop properties
                    cropIntent.putExtra("crop", "true");
                    //indicate aspect of desired crop
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    //indicate output X and Y
                    cropIntent.putExtra("outputX", 256);
                    cropIntent.putExtra("outputY", 256);
                    cropIntent.putExtra("scale",true);
                    //retrieve data on return
                    cropIntent.putExtra("return-data", true);
                    File f = new File(Environment.getExternalStorageDirectory(),
                            "/temporary_holder.jpg");
                    try {
                        f.createNewFile();
                    } catch (IOException ex) {
                        Log.e("io", ex.getMessage());
                    }

                    Uri uri = Uri.fromFile(f);
                    cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    try {
                        img.compress(Bitmap.CompressFormat.JPEG, 100, bao);

                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    byte [] ba = bao.toByteArray();
                    ba1 = Base64.encodeToString(ba,Base64.DEFAULT).toString();
                    new uploadUserPicture(ActivityAccount.this).execute();
                    //start the activity - we handle returning in onActivityResult
                    startActivityForResult(cropIntent, 3);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityAccount.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Your device doesn't support the crop action!";
                }
            }
            else {
                Toast.makeText(ActivityAccount.this, "Invalid File Inputed", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(ProfileSignUp.this, "valid Input", Toast.LENGTH_SHORT).show();
                try {
                    Bundle extras = data.getExtras();
                    //get the cropped bitmap
                    Bitmap selectedImage = extras.getParcelable("data");
                    Drawable d = new BitmapDrawable(selectedImage);
                    accountImage.setImageDrawable(d);

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    try {
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bao);

                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    byte [] ba = bao.toByteArray();
                    ba1 = Base64.encodeToString(ba,Base64.DEFAULT).toString();

                    new uploadUserPicture(ActivityAccount.this).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityAccount.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(ActivityAccount.this, "Invalid File Input", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSelected2(int color, int selectedQuadrant) {
        workchopUserLocationIndex = selectedQuadrant;
    }

    @Override
    public void done2() {
        String a="";
        int index = workchopUserLocationIndex;
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
        userLocation.setText(a);
    }

    @Override
    public void onDestroy2() {

    }

    private class getUserDetails extends AsyncTask<String,Void,String> {
        Context context;

        public getUserDetails(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_details.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
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
                if(sb.toString().equals("none")){

                }
                else{

                    h.post(new Runnable() {
                        public void run() {
                            String [] values = sb.toString().split("--");
                            int i = 0;
                            //Toast.makeText(context,String.valueOf(values.length),Toast.LENGTH_SHORT).show();
                            if(values == null){

                            }
                            else{
                                surname.setText(values[0]);
                                firstname.setText(values[1]);
                                mobileNumber.setText(values[2]);
                                email.setText(values[3]);
                                String locationString="";
                                switch (Integer.parseInt(values[4]))
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
                                        locationString = "Oshodi--Egbeda";
                                        break;
                                }
                                userLocation.setText(locationString);

                                pictureAsyncTask.execute();
                            }
                        }
                    });
                }
                in.close();

            }

            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        progress.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        progress.dismiss();
                        Toast.makeText(context, "Unable to Connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        progress.dismiss();
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

    private class checkPassword extends AsyncTask<String,Void,String> {
        Context context;

        public checkPassword(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/check_user_password.php";

            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
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
                                editMode = 1;
                                editAccount.setBackground(getResources().getDrawable(R.drawable.background_button_green));
                                surname.setEnabled(true);
                                firstname.setEnabled(true);
                                mobileNumber.setEnabled(true);
                                email.setEnabled(true);
                                userLocation.setEnabled(true);
                                editAccount.setText("Done");
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

    private class updateUserDetails extends AsyncTask<String,Void,String> {
        Context context;

        public updateUserDetails(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/update_user_details.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&surname="+URLEncoder.encode(params[1],"UTF-8")
                        +"&firstname="+URLEncoder.encode(params[2],"UTF-8")
                        +"&mobile_number="+URLEncoder.encode(params[3],"UTF-8")
                        +"&email_address="+URLEncoder.encode(params[4],"UTF-8")
                        +"&location_index="+URLEncoder.encode(params[5],"UTF-8");
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
                if(sb.toString().equals("done")){
                    h.post(new Runnable() {
                        public void run() {
                            //new getUserDetails(context).execute(userId);
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            startActivity(intent);
                            Toast.makeText(context,"Account Details Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    private class getUserPicture extends AsyncTask<String,Void,String> {
        Context context;
        Bitmap img;
        HttpURLConnection connection = null;

        public getUserPicture(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String imageUrl= "http://workchopapp.com/mobile_app/user_pictures/"+userId+".jpg";
            Log.v("INSIDE","GET PICTURE");
            String dataUrlParameters = null;

            URL url = null;
            try{

                URL url2 = new URL(imageUrl);
                connection  = (HttpURLConnection) url2.openConnection();


                Log.v("VENDORS GOTTEN","VENDORS GOTTEN");

                connection.setDoOutput(true);
                connection.connect();
                is3 = connection.getInputStream();
                Log.v("CONNECTION", "CONNECTED");
                BitmapFactory.Options options = new BitmapFactory.Options();
                Log.v("BITMAP FACTORY", "CONNECTED");
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Log.v("BITMAP FACTORY", "OPTIONS SET");
                img = BitmapFactory.decodeStream(is3);
                Log.v("STREAM", "DECODED");
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        accountImage.setImageBitmap(img );
                        progress.dismiss();
                        editAccount.setEnabled(true);
                        imageLoaderHandler.removeCallbacksAndMessages(null);
                        try {
                            is3.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        connection.disconnect();
                    }
                });
                try {
                    is3.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                connection.disconnect();
                Log.v("FINISHED","GETTING PICTURE");
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
                        progress.dismiss();
                    }
                });
            }

            return null;
        }
    }

    private class uploadUserPicture extends AsyncTask<String,Void,String> {
        Context context;

        public uploadUserPicture(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {

            String imageUrl= "http://workchopapp.com/mobile_app/upload_user_pictures.php";


            URL url = null;
            try{
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("base64", ba1));
                nameValuePairs.add(new BasicNameValuePair("ImageName", userId + ".jpg"));
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(imageUrl);
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    String st = EntityUtils.toString(response.getEntity());
                    Log.v("log_tag", "In the try Loop" + st);

                } catch (Exception e) {
                    Log.v("log_tag", "Error in http connection " + e.toString());
                }

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(context,"Picture Uploaded",Toast.LENGTH_LONG).show();

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
                imageButton.setAlpha((float)0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float)1.0);
            }
            return false;
        }

    }
}
