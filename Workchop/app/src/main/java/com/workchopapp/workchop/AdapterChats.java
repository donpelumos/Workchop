package com.workchopapp.workchop;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BALE on 25/07/2016.
 */

public class AdapterChats extends ArrayAdapter<ListChats> {
    Context context;
    ArrayList<ListChats> data;
    int layoutResourceId;
    int selectedItem;


    public AdapterChats(final Context context, final int layoutResourceId, final ArrayList<ListChats> data) {
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
        TextView chatCountText = null;
        if (inflate == null) {
            inflate = LayoutInflater.from(getContext()).inflate(this.layoutResourceId, null);
            //Log.v("DONE","Done");
        }
        menuImage = (ImageView)inflate.findViewById(R.id.chatIcon);
        menuText = (TextView)inflate.findViewById(R.id.vendorName);
        menuText2 = (TextView)inflate.findViewById(R.id.dateTime);
        chatCountText = (TextView)inflate.findViewById(R.id.chatCount);

        final ListChats list = this.data.get(n);
        menuText.setText((CharSequence)list.personName);
        //menuImage.setImageResource(list.personImage);
        menuText2.setText((CharSequence)list.dateTime);
        chatCountText.setText((CharSequence)String.valueOf(list.chatCount));
        inflate.findViewById(R.id.chatCount).setVisibility(View.VISIBLE);
        if(list.chatCount == 0){
            inflate.findViewById(R.id.chatCount).setVisibility(View.INVISIBLE);
            //chatCountText.setVisibility(View.INVISIBLE);
        }
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/GOTHIC.TTF");
        menuText.setTypeface(type);
        menuText2.setTypeface(type);
        chatCountText.setTypeface(type);

        return inflate;
    }


}
