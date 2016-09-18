package com.workchopapp.workchop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.graphics.Color;
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

import org.w3c.dom.Text;

/**
 * Created by BALE on 22/07/2016.
 */

public class DialogLocationSelector extends DialogFragment {
    TextView quadrant1, quadrant2, quadrant3, quadrant4, quadrant5, quadrant6, quadrant7, quadrant8;
    TextView newQuadrant1,newQuadrant2,newQuadrant3,newQuadrant4,newQuadrant5,newQuadrant6,newQuadrant7;
    TextView cantFind;
    AlertDialog dialog;
    int quadrantIndex, selectedQuadrant;
    TextView quadrantSelected;
    LinearLayout firstSet, secondSet;
    TextView ball1, ball2, locationsCovered;
    ColorSelected mColorSelected;
    ImageView leftArrow, rightArrow;
    public int selectedHalf;

    public DialogLocationSelector(){

    }
    public void setSelectedInt(int a){
        selectedHalf = a;
    }

    public interface ColorSelected{
        public void onSelected(int color, int selectedQuadrant);
        public void done();
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
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_locationselector, null);
        builder.setView(view);
        cantFind = (TextView)view.findViewById(R.id.cantFind);
        String udata= cantFind.getText().toString();
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        cantFind.setText(content);
        newQuadrant1 = (TextView)view.findViewById(R.id.newQuadrant1);
        newQuadrant2 = (TextView)view.findViewById(R.id.newQuadrant2);
        newQuadrant3 = (TextView)view.findViewById(R.id.newQuadrant3);
        newQuadrant4 = (TextView)view.findViewById(R.id.newQuadrant4);
        newQuadrant5 = (TextView)view.findViewById(R.id.newQuadrant5);
        newQuadrant6 = (TextView)view.findViewById(R.id.newQuadrant6);
        newQuadrant7 = (TextView)view.findViewById(R.id.newQuadrant7);
        newQuadrant1.setOnTouchListener(new TextHighlighterOnTouchListener(newQuadrant1));
        newQuadrant2.setOnTouchListener(new TextHighlighterOnTouchListener(newQuadrant2));
        newQuadrant3.setOnTouchListener(new TextHighlighterOnTouchListener(newQuadrant3));
        newQuadrant4.setOnTouchListener(new TextHighlighterOnTouchListener(newQuadrant4));
        newQuadrant5.setOnTouchListener(new TextHighlighterOnTouchListener(newQuadrant5));
        newQuadrant6.setOnTouchListener(new TextHighlighterOnTouchListener(newQuadrant6));
        newQuadrant7.setOnTouchListener(new TextHighlighterOnTouchListener(newQuadrant7));
        cantFind.setOnTouchListener(new TextHighlighterOnTouchListener(cantFind));
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
                newQuadrant7.setAlpha((float)0.7); mColorSelected.onSelected(R.color.workchopGrey, 1);
                mColorSelected.done();
                dialog.cancel();
            }
        });
        newQuadrant2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)1.0); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); mColorSelected.onSelected(R.color.workchopBlue, 2);
                mColorSelected.done();
                dialog.cancel();
            }
        });
        newQuadrant3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)1.0);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); mColorSelected.onSelected(R.color.workchopBlue, 3);
                mColorSelected.done();
                dialog.cancel();
            }
        });
        newQuadrant4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)1.0); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); mColorSelected.onSelected(R.color.workchopGrey, 4);
                mColorSelected.done();
                dialog.cancel();
            }
        });
        newQuadrant5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)1.0); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)0.7); mColorSelected.onSelected(R.color.workchopBlue, 5);
                mColorSelected.done();
                dialog.cancel();
            }
        });
        newQuadrant6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)1.0);
                newQuadrant7.setAlpha((float)0.7); mColorSelected.onSelected(R.color.workchopBlue, 6);
                mColorSelected.done();
                dialog.cancel();
            }
        });
        newQuadrant7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
                newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
                newQuadrant7.setAlpha((float)1.0); mColorSelected.onSelected(R.color.workchopBlue, 7);
                mColorSelected.done();
                dialog.cancel();
            }
        });
        //quadrantSelected = (TextView)view.findViewById(R.id.quadrantSelected);
        //quadrant1 = (TextView)view.findViewById(R.id.quadrant1);
        //quadrant2 = (TextView)view.findViewById(R.id.quadrant2);
        //quadrant3 = (TextView)view.findViewById(R.id.quadrant3);
        //quadrant4 = (TextView)view.findViewById(R.id.quadrant4);
        //quadrant5 = (TextView)view.findViewById(R.id.quadrant5);
        //quadrant6 = (TextView)view.findViewById(R.id.quadrant6);
        //quadrant7 = (TextView)view.findViewById(R.id.quadrant7);
        //quadrant8 = (TextView)view.findViewById(R.id.quadrant8);
        //quadrant1.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant1));
        //quadrant2.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant2));
        //quadrant3.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant3));
        //quadrant4.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant4));
        //quadrant5.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant5));
        //quadrant6.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant6));
        //quadrant7.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant7));
        //quadrant8.setOnTouchListener(new TextHighlighterOnTouchListener(quadrant8));
        ////firstSet = (LinearLayout)view.findViewById(R.id.firstSet);
        //secondSet = (LinearLayout)view.findViewById(R.id.secondSet);
        //selectedHalf = getArguments().getInt("val");
        selectedQuadrant = getArguments().getInt("val2");

        quadrantIndex = 1;
        //ball1 = (TextView)view.findViewById(R.id.ball1);
        //ball2 = (TextView)view.findViewById(R.id.ball2);
        //ball1.setOnTouchListener(new TextHighlighterOnTouchListener(ball1));
        //ball2.setOnTouchListener(new TextHighlighterOnTouchListener(ball2));
        //locationsCovered = (TextView)view.findViewById(R.id.locationsCovered);

        if(selectedQuadrant == 1){
            //quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1_frame));
            //locationsCovered.setText("All Locations");
            newQuadrant1.setAlpha((float)1.0); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 2){
            //quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2_frame));
            //locationsCovered.setText("Ebute Metta - Yaba - Akoka - Lagos Island - Oke Arin - Sura - Obalende");
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)1.0); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 3){
            //quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3_frame));
            //locationsCovered.setText("Shomolu - Bariga - Anthony - Maryland - Ilupeju");
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)1.0);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 4){
            //quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4_frame));
            //locationsCovered.setText("Ikeja - Ogba - Opebi - Oregun - Alausa - Berger");
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)1.0); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 5){
            //quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5_frame));
            //locationsCovered.setText("Ajah - Lekki - Victoria Island - Ikoyi - Epe");
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)1.0); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 6){
            //quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6_frame));
            //locationsCovered.setText("Oshodi - Alimosho - Abule Egba - Ikotun - Egbeda");
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)1.0);
            newQuadrant7.setAlpha((float)0.7);
        }
        else if(selectedQuadrant == 7){
            //quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7_frame));
            //locationsCovered.setText("Surulere - Iponri - Festac - Aguda - Bode Thomas - Apapa - Mile 2 - Badagry - Ojuelegba");
            newQuadrant1.setAlpha((float)0.7); newQuadrant2.setAlpha((float)0.7); newQuadrant3.setAlpha((float)0.7);
            newQuadrant4.setAlpha((float)0.7); newQuadrant5.setAlpha((float)0.7); newQuadrant6.setAlpha((float)0.7);
            newQuadrant7.setAlpha((float)1.0);
        }
        /*else if(selectedQuadrant == 8){
            quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8_frame));
            locationsCovered.setText("Ikorodu - Ojota - Mile 12 - Ketu");
        }*/
        //quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1_frame));



        /*ball1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstSet.setVisibility(View.VISIBLE);
                secondSet.setVisibility(View.GONE);
                ball1.setBackground(getResources().getDrawable(R.drawable.background_locationselector));
                ball2.setBackground(getResources().getDrawable(R.drawable.background_locationselector_transparent));
            }
        });
        ball2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondSet.setVisibility(View.VISIBLE);
                firstSet.setVisibility(View.GONE);
                ball2.setBackground(getResources().getDrawable(R.drawable.background_locationselector));
                ball1.setBackground(getResources().getDrawable(R.drawable.background_locationselector_transparent));
            }
        });
        if(selectedHalf == 1) {
            secondSet.setVisibility(View.GONE);
            firstSet.setVisibility(View.VISIBLE);
            ball1.setBackground(getResources().getDrawable(R.drawable.background_locationselector));
            ball2.setBackground(getResources().getDrawable(R.drawable.background_locationselector_transparent));
        }
        else if(selectedHalf == 2)
        {
            firstSet.setVisibility(View.GONE);
            secondSet.setVisibility(View.VISIBLE);
            ball2.setBackground(getResources().getDrawable(R.drawable.background_locationselector));
            ball1.setBackground(getResources().getDrawable(R.drawable.background_locationselector_transparent));
        }
        else{
            secondSet.setVisibility(View.GONE);
            firstSet.setVisibility(View.VISIBLE);
            ball1.setBackground(getResources().getDrawable(R.drawable.background_locationselector));
            ball2.setBackground(getResources().getDrawable(R.drawable.background_locationselector_transparent));
        }
        quadrantSelected.setOnTouchListener(new TextHighlighterOnTouchListener(quadrantSelected));
        quadrantSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorSelected.done();
                dialog.cancel();
            }
        });

        quadrant1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 1;
                locationsCovered.setText("All Locations");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1_frame));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8));
                mColorSelected.onSelected(R.color.quadrantOne, 1, 1);
            }
        });
        quadrant2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 2;
                locationsCovered.setText("Ebute Metta - Yaba - Akoka - Lagos Island - Oke Arin - Sura - Obalende");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2_frame));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8));
                mColorSelected.onSelected(R.color.quadrantTwo, 1, 2);
            }
        });
        quadrant3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 3;
                locationsCovered.setText("Shomolu - Bariga - Anthony - Maryland - Ilupeju");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3_frame));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8));
                mColorSelected.onSelected(R.color.quadrantThree, 1, 3);
            }
        });
        quadrant4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 4;
                locationsCovered.setText("Ikeja - Ogba - Opebi - Oregun - Alausa - Berger");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4_frame));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8));
                mColorSelected.onSelected(R.color.quadrantFour, 1, 4);
            }
        });
        quadrant5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 5;
                locationsCovered.setText("Ajah - Lekki - Victoria Island - Ikoyi - Epe");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5_frame));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8));
                mColorSelected.onSelected(R.color.quadrantFive, 2, 5);
            }
        });
        quadrant6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 6;
                locationsCovered.setText("Oshodi - Alimosho - Abule Egba - Ikotun - Egbeda");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6_frame));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8));
                mColorSelected.onSelected(R.color.quadrantSix, 2, 6);
            }
        });
        quadrant7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 7;
                locationsCovered.setText("Surulere - Iponri - Festac - Aguda - Bode Thomas - Apapa - Mile 2 - Badagry - Ojuelegba");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7_frame));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8));
                mColorSelected.onSelected(R.color.quadrantSeven, 2, 7);
            }
        });
        quadrant8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quadrantIndex = 8;
                locationsCovered.setText("Ikorodu - Ojota - Mile 12 - Ketu");
                quadrant1.setBackground(getResources().getDrawable(R.drawable.background_quadrant1));
                quadrant2.setBackground(getResources().getDrawable(R.drawable.background_quadrant2));
                quadrant3.setBackground(getResources().getDrawable(R.drawable.background_quadrant3));
                quadrant4.setBackground(getResources().getDrawable(R.drawable.background_quadrant4));
                quadrant5.setBackground(getResources().getDrawable(R.drawable.background_quadrant5));
                quadrant6.setBackground(getResources().getDrawable(R.drawable.background_quadrant6));
                quadrant7.setBackground(getResources().getDrawable(R.drawable.background_quadrant7));
                quadrant8.setBackground(getResources().getDrawable(R.drawable.background_quadrant8_frame));
                mColorSelected.onSelected(R.color.quadrantEight, 2, 8);
            }
        });*/
        leftArrow = (ImageView)view.findViewById(R.id.leftArrow);
        rightArrow = (ImageView)view.findViewById(R.id.rightArrow);
        leftArrow.setOnTouchListener(new ImageHighlighterOnTouchListener(leftArrow));
        rightArrow.setOnTouchListener(new ImageHighlighterOnTouchListener(rightArrow));
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
}
