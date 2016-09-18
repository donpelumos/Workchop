package com.workchopapp.workchop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by BALE on 20/08/2016.
 */

public class DialogAbout extends DialogFragment {
    AlertDialog dialog;
    TextView appName, copyrightStatement, versionName, termsAndConditions;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_about, null);
        appName = (TextView)view.findViewById(R.id.appName);
        copyrightStatement = (TextView)view.findViewById(R.id.copoyrightStatement);
        versionName = (TextView)view.findViewById(R.id.versionName);
        termsAndConditions = (TextView)view.findViewById(R.id.termsAndConditions);
        String udata= termsAndConditions.getText().toString();
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        termsAndConditions.setText(content);
        termsAndConditions.setClickable(true);
        termsAndConditions.setOnTouchListener(new TextHighlighterOnTouchListener(termsAndConditions));
        builder.setView(view);
        dialog = builder.create();
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        appName.setTypeface(type);
        copyrightStatement.setTypeface(type);
        versionName.setTypeface(type);
        termsAndConditions.setTypeface(type);
        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.workchopapp.com/terms.php";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
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
