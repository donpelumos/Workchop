package com.workchopapp.workchop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
        vendorTypeRows = new ListVendorType[]{
                new ListVendorType("Gas Supplier",R.drawable.icongas),
                new ListVendorType("Hair Stylist",R.drawable.iconstylist),
                new ListVendorType("Make-Up Artist",R.drawable.iconmakeup),
                new ListVendorType("Mechanic",R.drawable.iconmechanic ), new ListVendorType("Tailor",R.drawable.icontailor)};

        final AdapterVendorTypeList adp = new AdapterVendorTypeList(view.getContext(),R.layout.row_vendortypelist, vendorTypeRows );
        vendorTypeList.setAdapter(adp);
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
