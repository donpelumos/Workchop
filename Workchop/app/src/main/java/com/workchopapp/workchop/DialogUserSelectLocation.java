package com.workchopapp.workchop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by BALE on 03/08/2016.
 */

public class DialogUserSelectLocation extends DialogFragment {
    TextView newQuadrant1,newQuadrant2,newQuadrant3,newQuadrant4,newQuadrant5,newQuadrant6,newQuadrant7;
    TextView cantFind;
    AlertDialog dialog;
    int quadrantIndex, selectedQuadrant;
    int locationChosen;
    ColorSelected mColorSelected;
    Button selectLocationButton;
    ImageView leftArrow, rightArrow;
    public int selectedHalf;

    public DialogUserSelectLocation(){

    }
    public void setSelectedInt(int a){
        selectedHalf = a;
    }

    public interface ColorSelected{
        public void onSelected2(int color, int selectedQuadrant);
        public void done2();
        public void onDestroy2();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mColorSelected = (ColorSelected) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_user_select_location, null);
        builder.setView(view);
        cantFind = (TextView)view.findViewById(R.id.cantFind);
        String udata= cantFind.getText().toString();
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        cantFind.setText(content);
        locationChosen = 0;

        newQuadrant1 = (TextView)view.findViewById(R.id.newQuadrant1);
        newQuadrant2 = (TextView)view.findViewById(R.id.newQuadrant2);
        newQuadrant3 = (TextView)view.findViewById(R.id.newQuadrant3);
        newQuadrant4 = (TextView)view.findViewById(R.id.newQuadrant4);
        newQuadrant5 = (TextView)view.findViewById(R.id.newQuadrant5);
        newQuadrant6 = (TextView)view.findViewById(R.id.newQuadrant6);
        newQuadrant7 = (TextView)view.findViewById(R.id.newQuadrant7);

        selectLocationButton = (Button)view.findViewById(R.id.selectLocationButton);

        newQuadrant1.setOnTouchListener(new  TextHighlighterOnTouchListener(newQuadrant1));
        newQuadrant2.setOnTouchListener(new  TextHighlighterOnTouchListener(newQuadrant2));
        newQuadrant3.setOnTouchListener(new  TextHighlighterOnTouchListener(newQuadrant3));
        newQuadrant4.setOnTouchListener(new  TextHighlighterOnTouchListener(newQuadrant4));
        newQuadrant5.setOnTouchListener(new  TextHighlighterOnTouchListener(newQuadrant5));
        newQuadrant6.setOnTouchListener(new  TextHighlighterOnTouchListener(newQuadrant6));
        newQuadrant7.setOnTouchListener(new  TextHighlighterOnTouchListener(newQuadrant7));
        cantFind.setOnTouchListener(new  TextHighlighterOnTouchListener(cantFind));
        cantFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"Locations are divided into sectors. Select the one closest to you.",Toast.LENGTH_LONG).show();
            }
        });
        newQuadrant1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)1.0); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); selectLocationButton.setVisibility(View.VISIBLE);
                locationChosen = 1;
            }
        });
        newQuadrant2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)1.0); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); selectLocationButton.setVisibility(View.VISIBLE);
                locationChosen = 2;
            }
        });
        newQuadrant3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)1.0);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); selectLocationButton.setVisibility(View.VISIBLE);
                locationChosen = 3;
            }
        });
        newQuadrant4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)1.0); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); selectLocationButton.setVisibility(View.VISIBLE);
                locationChosen = 4;
            }
        });
        newQuadrant5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)1.0); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); selectLocationButton.setVisibility(View.VISIBLE);
                locationChosen = 5;
            }
        });
        newQuadrant6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)1.0);
                newQuadrant7.setAlpha((float)0.7); selectLocationButton.setVisibility(View.VISIBLE);
                locationChosen = 6;
            }
        });
        newQuadrant7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)1.0); selectLocationButton.setVisibility(View.VISIBLE);
                locationChosen = 7;
            }
        });

        selectLocationButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(selectLocationButton));
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorSelected.onSelected2(R.color.workchopBlue, locationChosen);
                mColorSelected.done2();
                dialog.cancel();
            }
        });

        selectedQuadrant = 0;
        selectLocationButton.setVisibility(View.INVISIBLE);

        quadrantIndex = 1;

        if(selectedQuadrant == 1){
            newQuadrant1.setAlpha((float)1.0); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 2){
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)1.0); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 3){
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)1.0);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 4){
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)1.0); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 5){
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)1.0); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 6){
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)1.0);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 7){
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)1.0);
        }

        leftArrow = (ImageView)view.findViewById(R.id.leftArrow);
        rightArrow = (ImageView)view.findViewById(R.id.rightArrow);
        leftArrow.setOnTouchListener(new  ImageHighlighterOnTouchListener(leftArrow));
        rightArrow.setOnTouchListener(new  ImageHighlighterOnTouchListener(rightArrow));
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"Available only in Lagos. Rolling out to other states soon.",Toast.LENGTH_LONG).show();
            }
        });
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"Available only in Lagos. Rolling out to other states soon.",Toast.LENGTH_LONG).show();
            }
        });

        dialog = builder.create();
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface interfacer, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                }
                return true;
            }
        });

        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mColorSelected.onDestroy2();
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
                imageButton.setAlpha((float)0.5);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float)1.0);
            }
            return false;
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
                imageButton.setAlpha((float) 1.0);
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
                imageButton.setAlpha((float)0.6);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                imageButton.setAlpha((float) 1.0);
            }
            return false;
        }

    }
}