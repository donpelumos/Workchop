package com.workchopapp.workchop;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by BALE on 21/07/2016.
 */

public class AdapterVendorResult extends ArrayAdapter<ListVendorResult> {
    Context context;
    ListVendorResult [] data;
    int layoutResourceId;
    int selectedItem;
    float widthDp;

    public AdapterVendorResult(final Context context, final int layoutResourceId, final ListVendorResult [] data, float widthDp) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.widthDp = widthDp;
    }

    public View getView(final int n, final View view, final ViewGroup viewGroup) {
        View inflate = view;
        ImageView menuImage = null;
        TextView menuText = null;
        TextView menuText2 = null;
        TextView menuContactUsage = null;
        if (inflate == null) {
            inflate = LayoutInflater.from(getContext()).inflate(this.layoutResourceId, null);
            //Log.v("DONE","Done");
        }
        menuImage = (ImageView)inflate.findViewById(R.id.vendorIcon);
        menuText = (TextView)inflate.findViewById(R.id.vendorName);
        menuText2 = (TextView)inflate.findViewById(R.id.vendorLocation);
        menuContactUsage = (TextView)inflate.findViewById(R.id.usedByText);


        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/GOTHIC.TTF");
        final ListVendorResult list = this.data[n];
        menuText.setText((CharSequence)list.menuName);
        menuImage.setImageResource(list.menuImage);
        menuText2.setText((CharSequence)list.menuLocation);
        menuContactUsage.setText((CharSequence)list.menuContactUsage);
        menuText.setTypeface(type);
        menuText2.setTypeface(type);
        menuContactUsage.setTypeface(type,Typeface.BOLD);
        if(widthDp < 480){
            menuText2.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
            menuContactUsage.setTextSize(TypedValue.COMPLEX_UNIT_SP,9);
        }


        return inflate;
    }


}
