package com.workchopapp.workchop;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by BALE on 18/07/2016.
 */

public class AdapterSelectedVendor extends ArrayAdapter<ListSelectedVendor> {
    Context context;
    ListSelectedVendor [] data;
    int layoutResourceId;
    int selectedItem;


    public AdapterSelectedVendor(final Context context, final int layoutResourceId, final ListSelectedVendor [] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public View getView(final int n, final View view, final ViewGroup viewGroup) {
        View inflate = view;
        ImageView menuImage = null;
        TextView menuText = null;
        TextView menuText2 = null;
        if (inflate == null) {
            inflate = LayoutInflater.from(getContext()).inflate(this.layoutResourceId, null);
            //Log.v("DONE","Done");
        }
        menuImage = (ImageView)inflate.findViewById(R.id.vendorIcon);
        menuText = (TextView)inflate.findViewById(R.id.vendorName);
        menuText2 = (TextView)inflate.findViewById(R.id.vendorLocation);

        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/GOTHIC.TTF");
        final ListSelectedVendor list = this.data[n];
        menuText.setText((CharSequence)list.menuName);
        menuImage.setImageResource(list.menuImage);
        menuText2.setText((CharSequence)list.menuLocation);
        menuText.setTypeface(type);
        menuText2.setTypeface(type);


        return inflate;
    }


}
