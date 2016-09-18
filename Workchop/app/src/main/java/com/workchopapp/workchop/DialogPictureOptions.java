package com.workchopapp.workchop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by BALE on 25/07/2016.
 */

public class DialogPictureOptions extends DialogFragment {

    ListView options;
    String userId;


    public interface NoticeDialogListener {
        public void onSelected(DialogFragment fragment, int index);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (NoticeDialogListener)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_picture_options, null);

        builder.setView(view)
        // Add action buttons
        ;

        options = (ListView)view.findViewById(R.id.pictureOptions);
        ArrayList<String> list = new ArrayList<String>();
        list.add("View");list.add("Change");
        ArrayAdapter<String> adp2 = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1,list);
        options.setAdapter(adp2);

        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mListener.onSelected(DialogPictureOptions.this, 0);
                    builder.create().dismiss();
                } else if (position == 1) {
                    mListener.onSelected(DialogPictureOptions.this, 1);
                    builder.create().dismiss();
                }
            }
        });

        return builder.create();

    }




}
