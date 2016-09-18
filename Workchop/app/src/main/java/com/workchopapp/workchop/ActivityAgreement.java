package com.workchopapp.workchop;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class ActivityAgreement extends AppCompatActivity {

    RelativeLayout layout;
    Button agree;
    TextView terms;
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File database=getApplicationContext().getDatabasePath("workchop.db");

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            setContentView(R.layout.activity_agreement);
            //Toast.makeText(ActivityAgreement.this, "Database not Found", Toast.LENGTH_LONG).show();
            mydatabase  = openOrCreateDatabase("workchop.db",MODE_PRIVATE,null);
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
            agree = (Button)findViewById(R.id.agreeTerms);
            agree.setTransformationMethod(null);
            agree.setOnTouchListener(new ButtonHighlighterOnTouchListener(agree));
            agree.setBackgroundColor(Color.argb(255, 44, 44, 44));
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginScreen = new Intent(ActivityAgreement.this, ActivityLogin.class);
                    startActivity(loginScreen);
                    finish();
                }
            });
            terms = (TextView)findViewById(R.id.tc);
            String udata= terms.getText().toString();
            SpannableString content = new SpannableString(udata);
            content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
            terms.setText(content);
            terms.setOnTouchListener(new TextViewHighlighterOnTouchListener(terms));
            terms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://www.workchopapp.com/terms.php";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            layout = (RelativeLayout)findViewById(R.id.bg);
            int colors[] = { 0xffFFDF00 , 0xff0075D8};

            GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        }
        else {
            Intent intent = new Intent(ActivityAgreement.this, ActivityLogin.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            //Toast.makeText(ActivityAgreement.this, "Database Found - " + getDatabasePath("workchop.db").getPath().toString(),
                    //Toast.LENGTH_LONG).show();
        }
        //setContentView(R.layout.activity_agreement);


        //layout.setBackground(g);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agreement, menu);
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
                imageButton.setBackgroundColor(Color.argb(155, 44, 44, 44));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setBackgroundColor(Color.argb(255, 44, 44, 44));
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
                imageButton.setBackgroundColor(Color.argb(155, 248, 248, 248));
                imageButton.setTextColor(Color.argb(255, 235, 203, 0));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setBackgroundColor(Color.argb(255, 248, 248,248));
                imageButton.setTextColor(Color.argb(255, 255, 223, 0));
            }
            return false;
        }

    }
}





