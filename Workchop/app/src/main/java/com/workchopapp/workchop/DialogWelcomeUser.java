package com.workchopapp.workchop;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by BALE on 05/01/2017.
 */

public class DialogWelcomeUser extends DialogFragment {
    AlertDialog dialog;
    Button gotItButton;
    TextView welcomeText, inviteText, inviteLink;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_welcome_user, null);
        welcomeText = (TextView)view.findViewById(R.id.welcomeText);
        inviteText = (TextView)view.findViewById(R.id.inviteText);
        inviteLink = (TextView)view.findViewById(R.id.inviteLink);
        inviteLink.setOnTouchListener(new TextHighlighterOnTouchListener(inviteLink));
        gotItButton = (Button)view.findViewById(R.id.gotItButton);
        String udata2 = inviteLink.getText().toString();
        SpannableString content2 = new SpannableString(udata2);
        content2.setSpan(new UnderlineSpan(), 0, udata2.length(), 0);
        inviteLink.setText(content2);
        inviteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                getActivity().startActivityForResult(intent, 1);
            }
        });
        gotItButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(gotItButton));
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.create();

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        welcomeText.setTypeface(type);
        welcomeText.setText("Welcome to Workchop!\n" +
                "The No. 1 tradesman reference platform.\n" +
                "\n" +
                "At Workchop we believe you should not have any trouble finding the best vendors and tradesmen for your needs.\n" +
                "\n" +
                "That's why we created Workchop to connect you with the best tradesmen based on your friends' recommendations and references.\n" +
                "\n" +
                "We are already searching your contact list for your tradesmen. Please do well to rate and review them here.\n" +
                "Please also ensure you have added your location details accurately.\n" +
                "\n");
        inviteText.setTypeface(type);
        inviteLink.setTypeface(type);
        return dialog;
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
                imageButton.setAlpha((float)0.6);
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
                imageButton.setAlpha((float) 0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float) 1.0);
            }
            return false;
        }

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
                imageButton.setAlpha((float)0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float)1.0);
            }
            return false;
        }

    }
}
