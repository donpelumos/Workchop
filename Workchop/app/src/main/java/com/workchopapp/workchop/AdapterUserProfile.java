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
 * Created by BALE on 17/07/2016.
 */

public class AdapterUserProfile extends ArrayAdapter<ListUserProfile> {
    Context context;
    ListUserProfile[] data;
    int layoutResourceId;
    int selectedItem;


    public AdapterUserProfile(final Context context, final int layoutResourceId, final ListUserProfile [] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public View getView(final int n, final View view, final ViewGroup viewGroup) {
        View inflate = view;
        ImageView menuImage = null;
        TextView menuText = null;
        if (inflate == null) {
            inflate = LayoutInflater.from(getContext()).inflate(this.layoutResourceId, null);
            //Log.v("DONE","Done");
        }
        menuImage = (ImageView)inflate.findViewById(R.id.userprofile_icon);
        menuText = (TextView)inflate.findViewById(R.id.userprofile_text);
        //inflate.setTag((Object)tag);

        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"fonts/GOTHIC.TTF");
        final ListUserProfile list = this.data[n];
        menuText.setText((CharSequence)list.menuName);
        menuImage.setImageResource(list.menuImage);
        menuText.setTypeface(type);

        return inflate;
    }


}
