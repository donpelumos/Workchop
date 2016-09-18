package com.workchopapp.workchop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.app.Activity.RESULT_OK;
import static com.workchopapp.workchop.R.id.imageView;

/**
 * Created by BALE on 18/07/2016.
 */

public class DialogVendorProfile extends DialogFragment  implements  DialogSetNewVendorLocation.ColorSelected,
DialogChat.ReturnListener{
    String title;
    ImageView smsButton, chatButton, phonecallButton, smsButton2, chatButton2, phonecallButton2;
    TextView addNew, viewMore;
    ImageView addNewButton;
    LinearLayout vendorImageFrame;
    ImageView vendorImage;
    String userId, vendorType;
    EditText vendorPhoneNo1, vendorPhoneNo2;
    TextView review, reviewerName, reviewerText, showing;
    EditText vendorLocation;
    RelativeLayout vendorContact1Case, vendorContact2Case;
    InputStream is3 = null;
    String vendorId;
    ImageView editVendorProfile;
    String vendorNameText, vendorNumber, isVendorSmart;
    EditText surnameFirstname;
    String indexer="";
    int vendorTypeIndex = 0;
    int editOn = 0;
    int selectedVendorLocation;
    String ba1 = "";
    LinearLayout reviewContainer;
    int SELECT_PHOTO = 1;
    Button addToMyVendors;
    Close mClose;
    interface Close{
        void onDialogClosed();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mClose = (DialogVendorProfile.Close) activity;
    }
    public DialogVendorProfile(){

    }

    @Override
    public void onSelected2(int color, int selectedQuadrant) {
        selectedVendorLocation = selectedQuadrant;
    }

    @Override
    public void done2() {
        new receiveLocation(getActivity().getApplicationContext()).execute("");
        String locationString="";
        switch (selectedVendorLocation)
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
        }
        vendorLocation.setText(locationString);
    }

    @Override
    public void onDestroy2() {

    }

    @Override
    public void onReturned(int made) {

    }

    public interface NewReviewListener {
        public void onSelected(android.app.DialogFragment dialog, String value);
    }
    NewReviewListener mNewReviewListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_vendorprofile, null);
        smsButton = (ImageView)view.findViewById(R.id.smsButton);
        chatButton = (ImageView)view.findViewById(R.id.chatButton);
        phonecallButton = (ImageView)view.findViewById(R.id.phonecallButton);
        smsButton2 = (ImageView)view.findViewById(R.id.smsButton2);
        chatButton2 = (ImageView)view.findViewById(R.id.chatButton2);
        phonecallButton2 = (ImageView)view.findViewById(R.id.phonecallButton2);
        editVendorProfile = (ImageView)view.findViewById(R.id.editVendorProfile);
        reviewContainer = (LinearLayout)view.findViewById(R.id.reviewContainer);
        addToMyVendors = (Button)view.findViewById(R.id.addToMyVendors);
        vendorId = "";

        userId = getArguments().getString("userId");
        vendorType = getArguments().getString("vendorType");
        indexer = getArguments().getString("index");
        vendorContact1Case = (RelativeLayout)view.findViewById(R.id.vendorContact1Case);
        vendorContact2Case = (RelativeLayout)view.findViewById(R.id.vendorContact2Case);
        vendorImage = (ImageView)view.findViewById(R.id.vendorImage);
        selectedVendorLocation = 0;

        editVendorProfile.setOnTouchListener(new ImageHighlighterOnTouchListener(editVendorProfile));
        editVendorProfile.setVisibility(View.GONE);
        smsButton.setOnTouchListener(new ImageHighlighterOnTouchListener(smsButton));
        smsButton2.setOnTouchListener(new ImageHighlighterOnTouchListener(smsButton2));
        chatButton.setOnTouchListener(new ImageHighlighterOnTouchListener(chatButton));
        chatButton2.setOnTouchListener(new ImageHighlighterOnTouchListener(chatButton2));
        phonecallButton.setOnTouchListener(new ImageHighlighterOnTouchListener(phonecallButton));
        phonecallButton2.setOnTouchListener(new ImageHighlighterOnTouchListener(phonecallButton2));
        addNew = (TextView)view.findViewById(R.id.addNew);
        addNewButton = (ImageView)view.findViewById(R.id.addNewButton);
        addNew.setOnTouchListener(new TextHighlighterOnTouchListener(addNew));
        addNewButton.setOnTouchListener(new ImageHighlighterOnTouchListener(addNewButton));
        builder.setView(view);
        WindowManager wm = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float density = view.getContext().getResources().getDisplayMetrics().density;
        Resources r = getResources();
        float widthDp = (float)width/density;
        Toast.makeText(view.getContext(),"width - "+width+"PX. height - "+height+"px\n in dp - "+widthDp + " den = "+density,Toast.LENGTH_LONG).show();
        vendorImageFrame = (LinearLayout)view.findViewById(R.id.vendorImageFrame);
        if(widthDp < 480){
            LinearLayout.LayoutParams params = new
                    LinearLayout.LayoutParams((int)(70*density),(int)(70*density));
            vendorImageFrame.setLayoutParams(params);
        }
        new getUserSelectedVendor(getActivity().getApplicationContext()).execute(userId, vendorType);
        viewMore = (TextView)view.findViewById(R.id.viewMore);
        String udata= viewMore.getText().toString();
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        viewMore.setText(content);

        viewMore.setOnTouchListener(new TextHighlighterOnTouchListener(viewMore));
        phonecallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                TextView text1 = (TextView)view.findViewById(R.id.vendorPhoneNo1);
                String callText = "tel:"+text1.getText();
                intent.setData(Uri.parse(callText));
                startActivity(intent);
                new probablyUsed(getActivity().getApplicationContext()).execute("");
            }
        });
        phonecallButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                TextView text1 = (TextView)view.findViewById(R.id.vendorPhoneNo2);
                String callText = "tel:"+text1.getText();
                intent.setData(Uri.parse(callText));
                startActivity(intent);
                new probablyUsed(getActivity().getApplicationContext()).execute("");
            }
        });
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                TextView text1 = (TextView)view.findViewById(R.id.vendorPhoneNo1);
                sendIntent.setData(Uri.parse("smsto:"));
                sendIntent.putExtra("sms_body", "");
                sendIntent.putExtra("address"  , new String(text1.getText().toString()));
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
                new probablyUsed(getActivity().getApplicationContext()).execute("");
            }
        });
        smsButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                TextView text1 = (TextView)view.findViewById(R.id.vendorPhoneNo2);
                sendIntent.setData(Uri.parse("smsto:"));
                sendIntent.putExtra("sms_body", "");
                sendIntent.putExtra("address"  , new String(text1.getText().toString()));
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
                new probablyUsed(getActivity().getApplicationContext()).execute("");
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogChat newFragment = new DialogChat();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("vendorId", vendorId);
                bundle.putString("vendorName", surnameFirstname.getText().toString());
                newFragment.setArguments(bundle);
                newFragment.setTargetFragment(DialogVendorProfile.this,0);
                new probablyUsed(getActivity().getApplicationContext()).execute("");
                newFragment.show(getFragmentManager(), "dialog3");

            }
        });
        chatButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogChat newFragment = new DialogChat();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("vendorId", vendorId);
                bundle.putString("vendorName", surnameFirstname.getText().toString());
                newFragment.setArguments(bundle);
                newFragment.setTargetFragment(DialogVendorProfile.this,0);
                new probablyUsed(getActivity().getApplicationContext()).execute("");
                newFragment.show(getFragmentManager(), "dialog3");

            }
        });
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewReview newFragment = new DialogNewReview();
                Bundle bundle = new Bundle();
                bundle.putString("userId",userId);
                bundle.putString("vendorId", vendorId);
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "dialog2");
            }
        });
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewReview newFragment = new DialogNewReview();
                Bundle bundle = new Bundle();
                bundle.putString("userId",userId);
                bundle.putString("vendorId", vendorId);
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "dialog2");
            }
        });

        chatButton.setVisibility(View.INVISIBLE); chatButton2.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams ppp = new RelativeLayout.LayoutParams(1,1);
        ppp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        chatButton.setClickable(true);  chatButton2.setClickable(true);
        chatButton.setLayoutParams(ppp);  chatButton2.setLayoutParams(ppp);


        vendorImage.setOnTouchListener(new ImageHighlighterOnTouchListener(vendorImage));
        vendorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogVendorPictureView dialogVendorPictureView = new DialogVendorPictureView();
                Bundle bundle = new Bundle();
                bundle.putString("url","http://workchopapp.com/mobile_app/vendor_pictures/"+vendorId+".jpg");
                dialogVendorPictureView.setArguments(bundle);
                dialogVendorPictureView.show(getFragmentManager(),"dialog 16");
            }
        });
        vendorImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                return false;
            }
        });
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        surnameFirstname = (EditText)view.findViewById(R.id.surnameFirstname);
        surnameFirstname.setTypeface(type);
        surnameFirstname.setEnabled(false);
        vendorLocation = (EditText)view.findViewById(R.id.vendorLocation);
        vendorLocation.setTypeface(type);
        vendorPhoneNo1 = (EditText)view.findViewById(R.id.vendorPhoneNo1);
        vendorPhoneNo2 = (EditText)view.findViewById(R.id.vendorPhoneNo2);
        vendorPhoneNo1.setEnabled(false);
        vendorPhoneNo2.setEnabled(false);
        review = (TextView)view.findViewById(R.id.review);
        vendorPhoneNo1.setTypeface(type);
        vendorPhoneNo2.setTypeface(type);
        review.setTypeface(type);
        addNew.setTypeface(type);
        viewMore.setTypeface(type);
        reviewerName = (TextView)view.findViewById(R.id.reviewerName);
        reviewerText = (TextView)view.findViewById(R.id.reviewerText);
        reviewerName.setTypeface(type);
        reviewerText.setTypeface(type);
        showing = (TextView)view.findViewById(R.id.showing);
        showing.setTypeface(type);
        vendorLocation.setTypeface(type);
        vendorLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    DialogSetNewVendorLocation dialogUserSelectLocation = new DialogSetNewVendorLocation();
                    dialogUserSelectLocation.setTargetFragment(DialogVendorProfile.this,0);
                    dialogUserSelectLocation.show(getFragmentManager(), "dialog18");
                }
                return false;
            }
        });
        editVendorProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editOn ==0){
                    editOn = 1;
                    editVendorProfile.setImageResource(R.drawable.verified);
                    surnameFirstname.setEnabled(true);
                    vendorPhoneNo1.setEnabled(true);
                    vendorPhoneNo2.setEnabled(true);
                    vendorLocation.setEnabled(true);
                }
                else{
                    editOn = 0;
                    editVendorProfile.setImageResource(R.drawable.edit);
                    surnameFirstname.setEnabled(false);
                    vendorPhoneNo1.setEnabled(false);
                    vendorPhoneNo2.setEnabled(false);
                    vendorLocation.setEnabled(false);
                    if(selectedVendorLocation == 0 || vendorLocation.getText().length() == 0 || surnameFirstname.getText().length() == 0 ||
                            vendorPhoneNo1.getText().length() == 0 )
                    {
                        Toast.makeText(getActivity().getApplicationContext(),"Invalid input",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        vendorNameText = surnameFirstname.getText().toString();
                        if(vendorPhoneNo2.getText().toString().trim().length() > 0){
                            vendorNumber = vendorPhoneNo1.getText().toString().trim() +"&&" + vendorPhoneNo2.getText().toString().trim();
                        }
                        else{
                            vendorNumber = vendorPhoneNo1.getText().toString().trim();
                        }
                        new updateVendor(getActivity().getApplicationContext()).execute(vendorId,vendorNameText,vendorNumber,String.valueOf(selectedVendorLocation));
                        dismiss();
                    }
                }
            }
        });

        return builder.create();
    }

    private void restartFirstActivity() {
        Intent i = getActivity().getApplicationContext().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getApplicationContext().getPackageName() );

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(ProfileSignUp.this, "valid Input", Toast.LENGTH_SHORT).show();
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    Drawable d = new BitmapDrawable(selectedImage);
                    vendorImage.setImageDrawable(d);
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
                    //retrieve data on return
                    cropIntent.putExtra("return-data", true);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    try {
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bao);

                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    byte [] ba = bao.toByteArray();
                    ba1 = Base64.encodeToString(ba,Base64.DEFAULT).toString();
                    new uploadVendorPicture(getActivity().getApplicationContext()).execute();
                    //start the activity - we handle returning in onActivityResult
                    startActivityForResult(cropIntent, 3);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Invalid File Input", Toast.LENGTH_SHORT).show();
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
                    vendorImage.setImageDrawable(d);

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    try {
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bao);

                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    byte [] ba = bao.toByteArray();
                    ba1 = Base64.encodeToString(ba,Base64.DEFAULT).toString();

                    new uploadVendorPicture(getActivity().getApplicationContext()).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Invalid File Input", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class uploadVendorPicture extends AsyncTask<String,Void,String> {
        Context context;

        public uploadVendorPicture(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {

            String imageUrl= "http://workchopapp.com/mobile_app/upload_vendor_pictures.php";


            URL url = null;
            try{
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("base64", ba1));
                nameValuePairs.add(new BasicNameValuePair("ImageName", vendorId + ".jpg"));
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

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class getUserSelectedVendor extends AsyncTask<String,Void,String> {
        Context context;

        public getUserSelectedVendor(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_selected_vendor.php";

            String imageUrl;
            if(vendorId.length() > 0) {
                imageUrl = "http://workchopapp.com/mobile_app/vendor_pictures/" + vendorId + ".jpeg";
            }
            else{
                imageUrl = "http://workchopapp.com/mobile_app/vendor_pictures/person.jpg";
            }
            String dataUrlParameters = null;
            String imageUrlParameters = null;
            try {
                dataUrlParameters = "id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&type="+URLEncoder.encode(params[1],"UTF-8");

                //imageUrlParameters = URLEncoder.encode(imageUrl);
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

                            String [] values = sb.toString().split("----")[Integer.parseInt(indexer)].split("--");
                            int i = 0;
                            //Toast.makeText(context,String.valueOf(values.length),Toast.LENGTH_SHORT).show();
                            if(values == null){

                            }
                            else{
                                String vendorName = values[0];
                                String vendorPhoneNo = values[3];
                                String vendorLocationIndex = values[1];
                                selectedVendorLocation = Integer.parseInt(vendorLocationIndex);
                                String vendorsId = values[2];
                                vendorId = vendorsId;
                                //Log.v("VENDOR ID", vendorId +" USER ID - " +userId);
                                surnameFirstname.setText(vendorName);
                                String locationString="";
                                switch (Integer.parseInt(vendorLocationIndex))
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
                                }
                                vendorLocation.setText(locationString);
                                if(vendorPhoneNo.split("&&").length == 1){
                                    vendorPhoneNo1.setText(vendorPhoneNo);
                                    vendorPhoneNo2.setHint("XXXXXXXXXXX");
                                    //vendorContact2Case.setVisibility(View.GONE);
                                }
                                else{
                                    vendorPhoneNo1.setText(vendorPhoneNo.split("&&")[0]);
                                    vendorPhoneNo2.setText(vendorPhoneNo.split("&&")[1]);
                                    vendorPhoneNo2.setHint("XXXXXXXXXXX");
                                }
                                new getSmartVendor(context).execute("");
                                new getReviews(context).execute(vendorId);
                                new getVendorImage(context).execute("");
                            }
                        }
                    });
                }
                in.close();
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class getVendorImage extends AsyncTask<String,Void,String> {
        Context context;

        public getVendorImage(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String imageUrl = "http://workchopapp.com/mobile_app/vendor_pictures/" + vendorId + ".jpg";
            String isSmartUrl = "http://workchopapp.com/mobile_app/check_smart_vendor.php?vendor_id="+vendorId;

            String dataUrlParameters = null;

            URL url = null;
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(isSmartUrl));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }

                URL url2 = new URL(imageUrl);
                HttpURLConnection connection  = (HttpURLConnection) url2.openConnection();
                isSmartUrl = URLEncoder.encode(isSmartUrl,"UTF-8");

                Log.v("VENDORS GOTTEN","VENDORS GOTTEN");


                connection.setDoOutput(true);
                connection.connect();

                is3 = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap img = BitmapFactory.decodeStream(is3);

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Log.v("IS VENDOR SMART",sb2.toString());
                        if(sb2.toString().equals("1")){

                            editVendorProfile.setVisibility(View.GONE);
                        }
                        vendorImage.setImageBitmap(img );

                    }
                });
                connection.disconnect();
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class getSmartVendor extends AsyncTask<String,Void,String> {
        Context context;

        public getSmartVendor(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String isSmartUrl = "http://workchopapp.com/mobile_app/check_smart_vendor.php?vendor_id="+vendorId+"&user_id="+userId;
            String isMyVendor = "http://workchopapp.com/mobile_app/is_my_vendor.php?vendor_id="+vendorId+"&user_id="+userId;
            URL url = null;
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(isSmartUrl));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line="";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                //Log.v("IS SMART URL",isSmartUrl);

                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(isMyVendor));
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
                        Log.v("IS VENDOR SMART",sb.toString());
                        Log.v("IS MY VENDOR",sb2.toString());
                        if(sb.toString().equals("0.5")) {
                            editVendorProfile.setVisibility(View.VISIBLE);
                            chatButton.setVisibility(View.INVISIBLE); chatButton2.setVisibility(View.INVISIBLE);
                        }
                        else if(sb.toString().equals("1")){
                            editVendorProfile.setVisibility(View.GONE);
                            chatButton.setEnabled(true);  chatButton2.setEnabled(true);

                            float density = getResources().getDisplayMetrics().density;
                            chatButton.setVisibility(View.VISIBLE); chatButton2.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams pp = new RelativeLayout.LayoutParams((int)(54*density),(int)(41*density));
                            pp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            pp.setMargins(0,(int)(5*density),0,0);
                            chatButton.setClickable(true);  chatButton2.setClickable(true);
                            chatButton.setLayoutParams(pp);  chatButton2.setLayoutParams(pp);
                        }
                        else{
                            editVendorProfile.setVisibility(View.GONE);
                            chatButton.setVisibility(View.INVISIBLE); chatButton2.setVisibility(View.INVISIBLE);
                        }

                        if(sb2.toString().equals("false")){
                            addToMyVendors.setVisibility(View.VISIBLE);
                        }
                        else{
                            addToMyVendors.setVisibility(View.GONE);
                        }
                    }
                });
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class probablyUsed extends AsyncTask<String,Void,String> {
        Context context;

        public probablyUsed(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/probably_used.php?vendor_id="+vendorId+"&user_id="+userId;

            URL url = null;
            try{
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
                        if(sb.toString().split("--")[0].equals("done")) {
                            Log.v("SUCCESSFULLY ADDED","TO PROBABLY USED");
                        }
                    }
                });
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class getReviews extends AsyncTask<String,Void,String> {
        Context context;

        public getReviews(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String isSmartUrl = "http://workchopapp.com/mobile_app/get_review.php?vendor_id="+vendorId;
            Log.v("eed edde",isSmartUrl);
            URL url = null;
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(isSmartUrl));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        if(sb2.toString().contains("------")) {
                            String[] reviews = sb2.toString().split("------");
                            for (int i = 0; i < reviews.length; i++) {
                                String[] reviewRow = reviews[i].split("--");
                                reviewContainer.addView(setReview(reviewRow[0] + " " + reviewRow[1], reviewRow[2], Integer.parseInt(reviewRow[3]),
                                        context, reviewRow[4]));
                            }
                        }
                    }
                });
            }

            catch(Exception e){
                Log.v("ERROR",e.getMessage());
            }
            return null;
        }
    }

    private class receiveLocation extends AsyncTask<String, Void, String> {
        Context context;
        public receiveLocation(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            Handler h = new Handler(Looper.getMainLooper());


            h.post(new Runnable() {
                public void run() {
                    Toast.makeText(context,String.valueOf(selectedVendorLocation),Toast.LENGTH_SHORT).show();
                    String locationString="";
                    switch (selectedVendorLocation)
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
                    }
                    vendorLocation.setText("Location "+selectedVendorLocation+" : "+locationString);
                }
            });

            return null;
        }
    }

    private class updateVendor extends AsyncTask<String, Void, String> {
        Context context;
        public updateVendor(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/update_user_vendor.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "vendor_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&vendor_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&vendor_number="+URLEncoder.encode(params[2],"UTF-8")
                        +"&vendor_location_category="+URLEncoder.encode(params[3],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context,"Vendor Updated",Toast.LENGTH_SHORT).show();
                        mClose.onDialogClosed();
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

    public LinearLayout setReview(String name, String message, int rating, Context context, String points){
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        float density = getResources().getDisplayMetrics().density;
        LinearLayout reviewFrame = new LinearLayout(context);
        LinearLayout.LayoutParams reviewFrameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        reviewFrame.setOrientation(LinearLayout.VERTICAL);
        reviewFrame.setLayoutParams(reviewFrameParams);

        LinearLayout nameTextCase = new LinearLayout(context);
        LinearLayout.LayoutParams nameTextCaseParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameTextCase.setGravity(Gravity.CENTER_VERTICAL);
        nameTextCase.setOrientation(LinearLayout.HORIZONTAL);
        nameTextCase.setLayoutParams(nameTextCaseParams);

        TextView nameText = new TextView(context);
        LinearLayout.LayoutParams nameTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameTextParams.gravity = Gravity.LEFT;
        nameText.setPadding((int)(8*density),(int)(4*density),0,0);
        nameText.setLayoutParams(nameTextParams);
        nameText.setTypeface(type, Typeface.BOLD);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        nameText.setTextColor(Color.parseColor("#000000"));
        StringBuilder textBuilder = new StringBuilder(name.split(" ")[1]);
        textBuilder.replace(0,1,name.split(" ")[1].substring(0,1).toUpperCase());
        name = name.split(" ")[0] + " " + textBuilder.toString();
        nameText.setText(name.split(" ")[1]+" . "+name.split(" ")[0].substring(0,1).toUpperCase()+" - ");

        ImageView pointsImage = new ImageView(context);
        LinearLayout.LayoutParams pointsImageParams = new LinearLayout.LayoutParams((int)(18*density),(int)(18*density));
        pointsImageParams.setMargins((int)(5*density),0,(int)(0*density),0);
        pointsImage.setLayoutParams(pointsImageParams);
        pointsImage.setImageResource(R.drawable.reward);

        TextView namePoints = new TextView(context);
        LinearLayout.LayoutParams namePointsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        namePointsParams.gravity = Gravity.LEFT;
        namePoints.setPadding((int)(1*density),(int)(4*density),0,0);
        namePoints.setLayoutParams(namePointsParams);
        namePoints.setTypeface(type, Typeface.BOLD);
        namePoints.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        namePoints.setTextColor(Color.parseColor("#222222"));
        namePoints.setText(" "+points);

        nameTextCase.addView(nameText);
        nameTextCase.addView(pointsImage);
        nameTextCase.addView(namePoints);

        TextView reviewText = new TextView(context);
        LinearLayout.LayoutParams reviewTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        reviewTextParams.gravity = Gravity.LEFT;
        reviewText.setPadding((int)(8*density),0,0,0);
        reviewText.setLayoutParams(reviewTextParams);
        reviewText.setTypeface(type, Typeface.NORMAL);
        reviewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        reviewText.setTextColor(Color.parseColor("#222222"));
        reviewText.setText(message);

        RelativeLayout ratingFrame = new RelativeLayout(context);
        ratingFrame.setLayoutParams(reviewFrameParams);

        LinearLayout ratingRow = new LinearLayout(context);
        RelativeLayout.LayoutParams ratingRowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ratingRowParams.setMargins(0,0,(int)(5*density),0);
        ratingRowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ratingRow.setLayoutParams(ratingRowParams);
        ratingRow.setGravity(Gravity.RIGHT);
        ratingRow.setPadding((int)(4*density),(int)(4*density),(int)(4*density),(int)(4*density));



        for(int i=0; i<rating; i++){
            ImageView star = new ImageView(context);
            LinearLayout.LayoutParams starParams = new LinearLayout.LayoutParams((int)(12*density),(int)(12*density));
            starParams.setMargins((int)(2*density),(int)(0*density),(int)(2*density),(int)(0*density));
            star.setLayoutParams(starParams);
            star.setImageResource(R.drawable.rating);
            ratingRow.addView(star);
        }
        ratingFrame.addView(ratingRow);

        View lineView = new View(context);
        lineView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)(2*density)));
        lineView.setBackgroundColor(Color.parseColor("#999999"));

        reviewFrame.addView(nameTextCase);
        reviewFrame.addView(reviewText);
        reviewFrame.addView(ratingFrame);
        reviewFrame.addView(lineView);

        return reviewFrame;
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
}
