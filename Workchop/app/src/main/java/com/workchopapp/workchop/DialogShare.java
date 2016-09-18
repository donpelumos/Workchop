package com.workchopapp.workchop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by BALE on 24/08/2016.
 */

public class DialogShare extends DialogFragment{
    AlertDialog dialog;
    ImageView facebookIcon, twitterIcon;
    TextView facebookText, twitterText;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_share, null);
        facebookIcon = (ImageView)view.findViewById(R.id.facebookIcon);
        twitterIcon = (ImageView)view.findViewById(R.id.twitterIcon);
        facebookText = (TextView)view.findViewById(R.id.facebookText);
        twitterText = (TextView)view.findViewById(R.id.twitterText);
        facebookIcon.setOnTouchListener(new ImageHighlighterOnTouchListener(facebookIcon));
        twitterIcon.setOnTouchListener(new ImageHighlighterOnTouchListener(twitterIcon));
        facebookText.setOnTouchListener(new TextHighlighterOnTouchListener(facebookText));
        twitterText.setOnTouchListener(new TextHighlighterOnTouchListener(twitterText));
        builder.setView(view);
        dialog = builder.create();

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(),"fonts/GOTHIC.TTF");
        facebookText.setTypeface(type);
        twitterText.setTypeface(type);
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Workchop")
                            .setContentDescription(
                                    "Get access to vendors easily by using Workchop")
                            .setContentUrl(Uri.parse("http://www.workchopapp.com"))
                            .build();

                    shareDialog.show(linkContent);
                }
                /*
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
                        .build();
                ShareDialog shareDialog = new ShareDialog(getActivity());
                shareDialog.show(DialogShare.this,content);*/

            }
        });
        twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon); // your bitmap
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, "Get access to vendors easily by using Workchop - http://www.workchopapp.com ");
                tweetIntent.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse("android.resource://com.workchopapp.workchop/" + R.drawable.icontwitter));
                tweetIntent.setType("text/plain");
                //startActivity(tweetIntent);
                PackageManager packManager = getActivity().getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for(ResolveInfo resolveInfo: resolvedInfoList){
                    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                        tweetIntent.setClassName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }
                if(resolved){
                    startActivity(tweetIntent);
                }
                else{
                    Intent i = new Intent();
                    i.putExtra(Intent.EXTRA_TEXT, "Get access to vendors easily by using Workchop - http://www.workchopapp.com ");
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text="
                            +urlEncode("Get access to vendors easily by using Workchop - http://www.workchopapp.com ")));
                    startActivity(i);
                    Toast.makeText(getActivity().getApplicationContext(), "Twitter app isn't found", Toast.LENGTH_LONG).show();
                }
            }
        });

        return dialog;
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            Log.wtf("VVV", "UTF-8 should always be supported", e);
            return "";
        }
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
