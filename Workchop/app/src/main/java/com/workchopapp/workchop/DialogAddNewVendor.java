package com.workchopapp.workchop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;

import static com.workchopapp.workchop.R.id.vendorPhoneNo1;

/**
 * Created by BALE on 21/07/2016.
 */

public class DialogAddNewVendor extends DialogFragment implements DialogSetNewVendorLocation.ColorSelected{

    AlertDialog dialog;
    EditText vendorName, vendorLocation, vendorPhoneNo1, vendorPhoneNo2;
    Button addButton;
    Spinner vendorType;
    int selectedVendorLocation;
    ArrayList vendorTypesList;
    ArrayAdapter<String> a1;
    Typeface type;
    String userId, vendorNameText, vendorNumber, isVendorSmart;
    int vendorTypeIndex = 0;
    NewVendorAdded mNewVendorAdded;
    String [] vendorList = {"Vendor Type","Gas Supplier","Hair Stylist","Make-up Artist","Mechanic","Tailor"};
    DialogSetNewVendorLocation mDialogSetNewVendorLocation;
    public DialogAddNewVendor(){

    }

    interface NewVendorAdded{
        void onAdd();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNewVendorAdded = (NewVendorAdded) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_addnewvendor, null);
        builder.setView(view);
        type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        vendorName = (EditText)view.findViewById(R.id.vendorName);
        vendorLocation = (EditText)view.findViewById(R.id.vendorLocation);
        vendorPhoneNo1 = (EditText)view.findViewById(R.id.vendorPhoneNo1);
        vendorPhoneNo2 = (EditText)view.findViewById(R.id.vendorPhoneNo2);
        selectedVendorLocation = 0;
        addButton = (Button)view.findViewById(R.id.addButton);
        vendorType = (Spinner)view.findViewById(R.id.vendorType);
        userId = getArguments().getString("userId");

        vendorTypesList = new ArrayList();

        //a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendorType.setAdapter(new CustomArrayAdapter<String>(getActivity().getApplicationContext(),vendorList));
        vendorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vendorTypeIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(addButton));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new receiveLocation(getActivity().getApplicationContext()).execute("");
                if(selectedVendorLocation == 0 || vendorLocation.getText().length() == 0 || vendorName.getText().length() == 0 ||
                        vendorPhoneNo1.getText().length() == 0 || vendorTypeIndex == 0 || vendorTypeIndex == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Invalid input",Toast.LENGTH_SHORT).show();
                }
                else{
                    vendorNameText = vendorName.getText().toString();
                    if(vendorPhoneNo2.getText().length() > 0){
                        vendorNumber = vendorPhoneNo1.getText().toString() +"&&" + vendorPhoneNo2.getText().toString();
                    }
                    else{
                        vendorNumber = vendorPhoneNo1.getText().toString();
                    }
                    new uploadVendors(getActivity().getApplicationContext()).execute(userId,vendorNameText,vendorNumber,
                            String.valueOf(vendorTypeIndex),"0",String.valueOf(selectedVendorLocation));
                    dismiss();
                }
                mNewVendorAdded.onAdd();
            }
        });
        vendorName.setTypeface(type);  vendorLocation.setTypeface(type);  vendorPhoneNo1.setTypeface(type);
        vendorPhoneNo2.setTypeface(type);  addButton.setTypeface(type);
        vendorLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    DialogSetNewVendorLocation dialogUserSelectLocation = new DialogSetNewVendorLocation();
                    dialogUserSelectLocation.setTargetFragment(DialogAddNewVendor.this,0);
                    dialogUserSelectLocation.show(getFragmentManager(), "dialog17");
                }
                return false;
            }
        });
        dialog = builder.create();


        return dialog;
    }

    private class CustomArrayAdapter<T> extends ArrayAdapter<T> {
        public CustomArrayAdapter(Context ctx, T [] objects)
        {
            super(ctx, android.R.layout.simple_spinner_item, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            float density = getResources().getDisplayMetrics().density;
            ((TextView) v).setPadding((int)(5*density),(int)(5*density),(int)(5*density),(int)(5*density));
            ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            ((TextView) v).setTypeface(type);
            ((TextView) v).setTextColor(Color.parseColor("#666666")) ;

            return v;
        }

        //other constructors

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getView(position, convertView, parent);

            TextView text = (TextView)view.findViewById(android.R.id.text1);
            text.setTypeface(type);
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            float density = getResources().getDisplayMetrics().density;
            text.setPadding((int)(5*density),(int)(5*density),(int)(5*density),(int)(5*density));
            text.setTextColor(Color.parseColor("#888888"));//choose your color :)

            return view;
        }
    }

    @Override
    public void onSelected2(int color, int selectedQuadrant) {
        selectedVendorLocation = selectedQuadrant;
    }

    @Override
    public void done2() {
        new receiveLocation(getActivity().getApplicationContext()).execute("");
        vendorLocation.setText("Location "+selectedVendorLocation);
    }

    @Override
    public void onDestroy2() {

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
                imageButton.setAlpha((float) 1.0);
            }
            return false;
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
                    vendorLocation.setText("Location "+selectedVendorLocation);
                }
            });

            return null;
        }
    }
    private class uploadVendors extends AsyncTask<String, Void, String> {
        Context context;
        public uploadVendors(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/upload_user_vendors.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8")
                        +"&vendor_name="+URLEncoder.encode(params[1],"UTF-8")
                        +"&vendor_number="+URLEncoder.encode(params[2],"UTF-8")
                        +"&vendor_type="+URLEncoder.encode(params[3],"UTF-8")
                        +"&is_vendor_smart="+URLEncoder.encode(params[4],"UTF-8")
                        +"&vendor_location_category="+URLEncoder.encode(params[5],"UTF-8");
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
                        Toast.makeText(context,"Vendor Added",Toast.LENGTH_SHORT).show();
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
}
