package com.workchopapp.workchop;

/**
 * Created by BALE on 25/07/2016.
 */

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by BALE on 28/09/2015.
 */
public class ActivityChats extends AppCompatActivity implements DialogChat.ReturnListener {
    ListChats [] ListChatsData;
    ListView messageList;
    ImageButton backButton;
    FragmentManager fm;
    String userId;
    ArrayList<String> chatNames, chatTimes, chatIds, chatCount, zeroChats;
    ProgressDialog progress;
    ActionBar appBar;
    int pollCounter = 1;
    Timer timer;
    TimerTask timerTask;
    AdapterChats adp;
    ArrayList<Integer> phoneNotificationIds;
    int chatOpen=0;
    String fromScreen="chats", notificationName="";
    int currentChatPosition=-1;
    Handler handler, chatLoadHandler;
    int chatLoadTime = 0;
    String currentChatId;
    ArrayList<ListChats> chats;
    int totalChatCount = 0;
    int chatLoaded = 0;

    public ActivityChats(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBar = getSupportActionBar();
        appBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0075D8")));
        setContentView(R.layout.activity_chats);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//This hides the automatic keyboard when activity starts
        fm = getFragmentManager();
        userId = getIntent().getStringExtra("userId");
        messageList = (ListView)findViewById(R.id.chatList);
        chatNames = new ArrayList<>();
        chatTimes = new ArrayList<>();
        chatIds = new ArrayList<>();
        chatCount = new ArrayList<>();
        zeroChats = new ArrayList<>();
        phoneNotificationIds = getIntent().getIntegerArrayListExtra("notificationIds");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        for(int i=0; i<phoneNotificationIds.size(); i++){
            notificationManager.cancel(phoneNotificationIds.get(i));
        }
        progress = new ProgressDialog(ActivityChats.this);
        progress.setTitle("Loading");
        progress.show();
        chatLoadHandler = new Handler();
        chatLoadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(chatLoadTime == 13){
                    progress.dismiss();
                    Toast.makeText(ActivityChats.this,"Check Internet Connection,",Toast.LENGTH_SHORT).show();
                    chatLoadHandler.removeCallbacksAndMessages(null);
                }
                else if (chatLoaded == 1){
                    chatLoadHandler.removeCallbacksAndMessages(null);
                    Log.v("CHAT LOADED"," BEFORE TIME OUT");
                }
                else{
                    chatLoadTime++;
                    //chatLoadHandler.postDelayed(this,1000);
                }
            }
        }, 1000);
        new getChats(ActivityChats.this).execute(userId);
        try {
            if (getIntent().getStringExtra("val7").equals("notification")) {
                DialogChat newFragment = new DialogChat();
                Bundle bundle = new Bundle();
                fromScreen = getIntent().getStringExtra("val7");
                notificationName = getIntent().getStringExtra("name");
                bundle.putString("vendorId", getIntent().getStringExtra("vendorId"));
                bundle.putString("userId", getIntent().getStringExtra("userId"));
                bundle.putString("vendorName", getIntent().getStringExtra("name"));
                Log.v("VALUES GOTTEN", getIntent().getStringExtra("vendorId") + " " + getIntent().getStringExtra("userId") + " " +
                        getIntent().getStringExtra("name"));
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "dialog24");
                Log.v("I AM INSIDE", "4");
            }
        }
        catch (NullPointerException e){

        }

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                messageList.getChildAt(position).findViewById(R.id.chatCount).setVisibility(View.INVISIBLE);
                TextView tv = (TextView)messageList.getChildAt(position).findViewById(R.id.chatCount);
                tv.setText("0");
                //adp.notifyDataSetChanged();
                tv.setVisibility(View.INVISIBLE);
                chatOpen = 1;
                currentChatPosition = position;
                chats.set(position,new ListChats(chatNames.get(position), R.drawable.chat, chatTimes.get(position), 0));
                DialogChat newFragment = new DialogChat();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("vendorId", chatIds.get(position));
                currentChatId = chatIds.get(position);
                bundle.putString("vendorName", chatNames.get(position));
                newFragment.setArguments(bundle);
                newFragment.show(fm, "dialog11");
            }
        });

        timerTask = new TimerTask() {
            @Override
            public void run() {
                pollCaller();
            }
        };
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pollCaller();
                handler.postDelayed(this,10000);
            }
        },2000);
        //timer = new Timer(true);
        //timer.scheduleAtFixedRate(timerTask, 5000, 7*1000);

    }

    public void pollCaller(){
        try {
            //Log.v("CHATS SIZE", String.valueOf(chats.size()));
            new pollingClass(ActivityChats.this).execute(userId);
            Log.v("CALLING POLL 2", "POLL " + pollCounter);
        }
        catch(NullPointerException e){
            Toast.makeText(ActivityChats.this,"No Chats",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReturned(int changed) {
        chatOpen = 0;
        if(changed == 1){
            //Intent intent = getIntent();
            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //finish();
            //startActivity(intent);
        }
        currentChatPosition = -1;
    }

    private class pollingClass extends AsyncTask<String, Void, String> {
        Context context;
        public pollingClass(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            final String dataUrl2 = "http://workchopapp.com/mobile_app/get_user_chat_list2.php";
            String dataUrlParameters = null;
            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }
            try {
                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }

                in2.close();

                Handler h = new Handler(Looper.getMainLooper());
                final String finalDataUrlParameters = dataUrlParameters;
                h.post(new Runnable() {
                    public void run() {
                        //Log.v("full text",sb.toString());
                        if(sb2.toString().contains("------")) {
                            String[] allChats = sb2.toString().split("------");
                            chatCount = new ArrayList<String>();
                            chatTimes = new ArrayList<String>();
                            chatNames = new ArrayList<String>();
                            chatIds = new ArrayList<String>();

                            int tempTotalChatCount = 0;
                            int beep = 0;

                            for (int i = 0; i < allChats.length; i++) {
                                String[] chatRows = allChats[i].split("--");
                                chatNames.add(chatRows[1]);
                                chatTimes.add(chatRows[3]);
                                chatIds.add(chatRows[0]);
                                chatCount.add(chatRows[4]);
                                tempTotalChatCount = tempTotalChatCount + Integer.parseInt(chatRows[4]);
                                //Log.v("URI", dataUrl2 + "?" + finalDataUrlParameters);
                                Log.v("CHAT COUNT", chatCount.get(i) + "");
                            }

                            //chats = new ArrayList<ListChats>();
                            for(int i=0; i<=chats.size(); i++){
                                chats.remove(0);
                            }
                            chats.clear();
                            adp.notifyDataSetChanged();

                            for (int i = 0; i < chatNames.size(); i++) {
                                chats.add(new ListChats(chatNames.get(i), R.drawable.chat, getDateFormat(chatTimes.get(i)),
                                        Integer.parseInt(chatCount.get(i))));
                            }
                            adp.notifyDataSetChanged();
                            for(int i=0; i<chatCount.size(); i++){
                                Log.v("TOTAL CHAT COUNT",String.valueOf(totalChatCount));
                                if(chatIds.get(i).equals(currentChatId)){

                                }
                                else if(totalChatCount > 0 && totalChatCount < tempTotalChatCount ){
                                    if(Integer.parseInt(chatCount.get(i)) > 0){
                                        beep = 1;
                                    }
                                }
                            }
                            if(tempTotalChatCount == 0){
                                totalChatCount = tempTotalChatCount;
                                beep = 0;
                            }
                            else if(totalChatCount != tempTotalChatCount){
                                totalChatCount = tempTotalChatCount;
                                beep = 1;
                            }
                            else{
                                beep = 0;
                            }
                            if(beep == 1){
                                MediaPlayer mp = MediaPlayer.create(context, R.raw.beep1);
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(400);
                                if (mp != null) {
                                    mp.release();
                                }
                                mp = MediaPlayer.create(context, R.raw.beep1);
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.release();
                                    }
                                });
                            }

                            //totalChatCount = 0;
                            /*
                            if (fromScreen.equals("notification")){
                                fromScreen = "chats";
                                for (int j = 0; j < messageList.getCount(); j++) {
                                    TextView child = (TextView) messageList.getChildAt(j).findViewById(R.id.vendorName);
                                    TextView child2 = (TextView) messageList.getChildAt(j).findViewById(R.id.chatCount);
                                    currentChatPosition = j;
                                    if(notificationName.equals(child.getText().toString())){
                                        child2.setText("0");
                                        child2.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                            else if(fromScreen.equals("chats")) {
                                for (int i = 0; i < chatCount.size(); i++) {
                                    for (int j = 0; j < messageList.getCount(); j++) {
                                        try {
                                            TextView child = (TextView) messageList.getChildAt(j).findViewById(R.id.vendorName);
                                            TextView child2 = (TextView) messageList.getChildAt(j).findViewById(R.id.chatCount);
                                            //child2.setVisibility(View.VISIBLE);
                                            if (child.getText().toString().equals(chatNames.get(i)) && j!= currentChatPosition) {
                                                if (child2.getText().toString().equals(chatCount.get(i))) {
                                                    Log.v("COMPARING - EQUAL", child2.getText().toString() + "--" + chatCount.get(i));
                                                }
                                                else {
                                                    Log.v("COMPARING - NOT EQUAL", child2.getText().toString() + "--" + chatCount.get(i));
                                                    MediaPlayer mp = MediaPlayer.create(context, R.raw.beep1);
                                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                    v.vibrate(400);
                                                    if (mp != null) {
                                                        mp.release();
                                                    }
                                                    mp = MediaPlayer.create(context, R.raw.beep1);
                                                    mp.start();
                                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                        public void onCompletion(MediaPlayer mp) {
                                                            mp.release();
                                                        }
                                                    });
                                                    if (Integer.parseInt(chatCount.get(i)) > 0) {
                                                        child2.setVisibility(View.VISIBLE);
                                                        child2.setText(chatCount.get(i));
                                                    }
                                                    else if (Integer.parseInt(chatCount.get(i)) == 0) {
                                                        child2.setVisibility(View.INVISIBLE);
                                                    }
                                                    //adp.notifyDataSetChanged();
                                                }
                                            }
                                        } catch (NullPointerException e) {

                                        }
                                    }
                                }
                            }
                            */

                            Log.v("NEW ADAPTER","SET FOR LIST");
                        }
                    }
                });
            }
            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            finally{
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }

            return null;
        }
    }

    public void swapChat(int pos){
        String tempName = ""; String tempCount = ""; String tempDateTime = "";
        TextView tv = (TextView)messageList.getChildAt(pos).findViewById(R.id.vendorName);
        TextView tv2 = (TextView)messageList.getChildAt(pos).findViewById(R.id.dateTime);
        TextView tv3 = (TextView)messageList.getChildAt(pos).findViewById(R.id.chatCount);
        tempName = tv.getText().toString();
        tempCount = tv3.getText().toString();
        tempDateTime = tv2.getText().toString();
        for(int i = pos; i>0; i--){
            adp.getItem(i).chatCount = adp.getItem(i-1).chatCount;
            adp.getItem(i).dateTime = adp.getItem(i-1).dateTime;
            adp.getItem(i).personName = adp.getItem(i-1).personName;

            chatCount.set(i,chatCount.get(i-1));
            chatIds.set(i, chatIds.get(i-1));
            chatNames.set(i, chatNames.get(i-1));
            chatTimes.set(i, chatTimes.get(i-1));
        }
        adp.getItem(0).chatCount = Integer.parseInt(tempCount);
        adp.getItem(0).personName = tempName;
        adp.getItem(0).dateTime = tempDateTime;
        adp.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);

        //timer.cancel();
        //timer.purge();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private class getChats extends AsyncTask<String, Void, String> {
        Context context;
        public getChats(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            String dataUrl = "http://workchopapp.com/mobile_app/get_user_chat_list.php";
            String dataUrl2 = "http://workchopapp.com/mobile_app/get_user_chat_list2.php";
            String dataUrlParameters = null;
            String dataUrlParameters2 = null;

            try {
                dataUrlParameters = "user_id="+ URLEncoder.encode(params[0],"UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                Toast.makeText(context,new String("Exception: "+ e.getCause()+ "\n"+ e.getMessage()), Toast.LENGTH_LONG).show();
            }

            URL url = null;
            HttpURLConnection connection = null;
            try{

                url = new URL(dataUrl+"?"+dataUrlParameters);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(dataUrl+"?"+dataUrlParameters));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                final StringBuffer sb = new StringBuffer("");
                String line="";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                HttpClient client2 = new DefaultHttpClient();
                HttpGet request2 = new HttpGet();
                request2.setURI(new URI(dataUrl2+"?"+dataUrlParameters));
                HttpResponse response2 = client2.execute(request2);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));

                final StringBuffer sb2 = new StringBuffer("");
                String line2="";
                while ((line2 = in2.readLine()) != null) {
                    sb2.append(line2);
                    break;
                }

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        if(sb.toString().equals("false")){
                            progress.dismiss();
                            Toast.makeText(context,"No Chats",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(sb2.toString().contains("------")) {
                                chatLoaded = 1;
                                String[] allChats = sb2.toString().split("------");

                                for (int i = 0; i < allChats.length; i++) {
                                    String[] chatRows = allChats[i].split("--");
                                    chatNames.add(chatRows[1]);
                                    chatTimes.add(chatRows[3]);
                                    chatIds.add(chatRows[0]);
                                    chatCount.add(chatRows[4]);
                                    //Log.v("CHAT COUNT",chatCount.get(i)+"");
                                }
                                chats = new ArrayList<ListChats>();

                                for (int i = 0; i < chatNames.size(); i++) {
                                    chats.add(new ListChats(chatNames.get(i), R.drawable.chat, getDateFormat(chatTimes.get(i)),
                                            Integer.parseInt(chatCount.get(i))));
                                }
                                //messageList.setAdapter(null);
                                //adp = null;

                                adp = new AdapterChats(getApplicationContext(), R.layout.row_chats, chats);
                                adp.notifyDataSetChanged();
                                messageList.setAdapter(adp);

                                for(int i=0; i<chatIds.size(); i++){
                                    new getChatImage(context).execute(chatIds.get(i),String.valueOf(i));
                                }

                            }
                            progress.dismiss();
                        }
                    }
                });
                in.close();
            }

            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            catch(URISyntaxException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            finally{
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        progress.dismiss();
                    }
                });
            }
            return null;
        }
    }

    private class getChatImage extends AsyncTask<String,Void,String> {
        Context context;

        public getChatImage(Context c){
            context = c;
        }

        @Override
        protected String doInBackground(final String... params) {
            String imageUrl = "http://workchopapp.com/mobile_app/vendor_pictures/" + params[0] + ".jpg";

            String dataUrlParameters = null;

            URL url = null;
            try{
                URL url2 = new URL(imageUrl);
                HttpURLConnection connection  = (HttpURLConnection) url2.openConnection();
                connection.setDoOutput(true);
                connection.connect();
                InputStream is3 = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap img = BitmapFactory.decodeStream(is3);

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        try {
                            View view = messageList.getChildAt(Integer.parseInt(params[1]));
                            ImageView recentImage = (ImageView)view.findViewById(R.id.chatIcon);
                            recentImage.setImageBitmap(img);
                        }
                        catch(NullPointerException e){
                            Log.v("NULL IMAGE POINTER","");
                        }
                    }
                });
                connection.disconnect();
            }

            catch(MalformedURLException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            catch(IOException e){
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            finally{
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                    }
                });
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;

        //return super.onOptionsItemSelected(item);
    }

    public String getDateFormat(String date){
        String newFormat = "";
        String datePart = date.split(" ")[0];
        String timePart = date.split(" ")[1];
        String year = datePart.split("-")[0];
        String month = datePart.split("-")[1];
        String day = datePart.split("-")[2];
        String dayString = day;
        String [] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String monthString = months[Integer.parseInt(month)-1];

        String hour = timePart.split(":")[0];
        String timeSector = "am";
        if(Integer.parseInt(hour)>12){
            hour = String.valueOf(Integer.parseInt(hour)-12);
            timeSector = "pm";
        }
        String minute = timePart.split(":")[1];
        String second = timePart.split(":")[2];

        newFormat = monthString+"-"+dayString+" | "+hour+":"+minute+" "+timeSector;
        return newFormat;
    }

}
