package com.workchopapp.workchop;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by BALE on 08/07/2016.
 */
public class ActivityTermsLoginPage extends AppCompatActivity {
    ActionBar appBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBar  = getSupportActionBar();
        appBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        appBar.setDisplayHomeAsUpEnabled(true);
        appBar.setHomeButtonEnabled(true);
        setContentView(R.layout.activity_terms_loginpage);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(ActivityTermsLoginPage.this,"Going Back",Toast.LENGTH_LONG).show();
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
        else if(id ==  R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
