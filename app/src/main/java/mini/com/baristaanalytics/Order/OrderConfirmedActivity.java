package mini.com.baristaanalytics.Order;

import android.animation.ArgbEvaluator;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapter.OkoaOrdersRecyclerviewAdapter;
import Adapter.SectionsPagerAdapter;
import Model.CoffeeOrder;
import mini.com.baristaanalytics.R;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;
import utilities.MyApplication;

import static utilities.MyApplication.CHANNEL_2_ID;

/**
 *This is the admin-side of viewing and managing customer orders
 */
public class OrderConfirmedActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    private String TAG = "ORDER CONFIRMED";
    private Context ctx;
    // Speech to text
    // Array of input speech from user
    private List<MessageItem> message_items = new ArrayList<>();
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ViewPager viewPager;

    // AWS Polly vars
    CognitoCachingCredentialsProvider credentialsProvider;
    private List<Voice> voices;
    private CoffeeOrder order;
    private Integer[] colors = null;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private CoffeeOrder coffeeOrder;
    private DatabaseReference coffeeList,coffee_Order;    // Speech to text
    private ArrayList<CoffeeOrder> coffeeOrderArrayList;
    // Amazon Polly permissions.
    private static final String COGNITO_POOL_ID = "CHANGEME";

    private NotificationManagerCompat notificationManager;

    // Region of Amazon Polly.
    private static final Regions MY_REGION = Regions.US_EAST_1;
    private AmazonPollyPresigningClient client;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ImageButton btn;
    // AWS Media Player
    private MediaPlayer mediaPlayer;
    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;

    private Integer notificationCount;
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupVoicesList();

    }
    /**
     * Setup Polly Voices List
     */

    void setupVoicesList() {
        // Asynchronously get available Polly voices.
        new GetPollyVoices().execute();

    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"onActivityResult(): Result from speech to text");
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    MessageItem message_item = new MessageItem(result.get(0));
                    message_items.add(message_item);
                    decodeUserInput(result.get(0));
                }
                break;
            }

        }
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showToast(isConnected);
    }

    @Override
    protected void onPause(){
        super.onPause();
        animationDrawable.stop();
        notificationCount =0;
        //unregisterReceiver(connectivityReceiver);
    }
    @Override
    protected void onStop(){
        super.onStop();
        notificationCount=0;
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    protected void onResume() {
        super.onResume();
        setupNewMediaPlayer();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
        if(animationDrawable != null){
            animationDrawable.start();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);
        Log.d(TAG,"onCreate:Activity Started");
        relativeLayout = findViewById(R.id.relLayoutConvo);
        notificationCount = 1;
        animationDrawable = (AnimationDrawable)  relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

        notificationManager = NotificationManagerCompat.from(this);
        ctx = OrderConfirmedActivity.this;
        coffeeOrderArrayList = new ArrayList<>();
        //initPollyClient();
        database = FirebaseDatabase.getInstance();
        coffee_Order = database.getReference("OkoaCoffeeOrders");

        coffee_Order.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :
                        dataSnapshot.getChildren()) {
                    CoffeeOrder coffeeOrder = snap.getValue(CoffeeOrder.class);

                    if(!exists(coffeeOrder)){
                        // Update to order status
                        coffeeOrderArrayList.add(coffeeOrder);
                        notifyCustomers(coffeeOrder);
                    }
                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //setupViewPager();
    }
    private void notifyCustomers(CoffeeOrder coffeeOrder){
            if(coffeeOrder.getOrder_State().equals("Ordered")){
                sendNotification();
                notificationCount+=1;
        }
    }
    private boolean exists(CoffeeOrder coffeeOrder) {
        Boolean flag = false;
        if(coffeeOrderArrayList.size() >0){

            for (int i = 0; i < coffeeOrderArrayList.size(); i++) {
                CoffeeOrder temp = coffeeOrderArrayList.get(i);
                if(
                        coffeeOrder.getUUID().equals(temp.getUUID())
                                && coffeeOrder.getOrder_date().equals(temp.getOrder_date())
                        ){
                    // This is a new coffee order(not in arraylist)
                    // Need to check if the status is 'Ordered'
                    return true;
                }

            }
        }
        return false;
    }

    private void sendNotification() {
        String message = "New order received. Click to review and process this order";

        String title = "Barista Analytics Order Update";
        String notficationGroup = "Customer Orders";
        Intent intent = new Intent(this,CustomerOrders.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,intent,0);

        Notification notification =  new NotificationCompat.Builder(
                this,CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_current_orders)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText(notificationCount + " New Orders")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroup(notficationGroup)
                .setGroupSummary(true)
                .build();

        notificationManager.notify(1,notification);

    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewConfirmed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OkoaOrdersRecyclerviewAdapter(coffeeOrderArrayList,this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Responsible for adding 3 tabs to the viewpager
     * -Current Orders
     * -Previous Orders
     * -Messages Fragment
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter  = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(new CurrentOrdersFragment());
        //adapter.addFragments(new MessagesFragment());
        adapter.addFragments(new OrderHistoryFragment());
        ViewPager viewPager = (ViewPager)findViewById(R.id.container);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_current_orders);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_converation);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_pervious_orders);
    }
    public void promptSpeechInput(View view) {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if(isConnected){
            animationDrawable.start();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt));
            try {
                //initRecyclerView();
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.speech_not_supported),
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            showToast(isConnected);
        }

    }

    /**
     * AWS Polly Media Player
     */
    void setupNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                setupNewMediaPlayer();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                //playButton.setEnabled(true);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //playButton.setEnabled(true);
                return false;
            }
        });
    }

    private void decodeUserInput(String s) {
        // Check what the user gave as input
        // It can be an americano
        if(s.contains("history") || s.contains("previous")){
            // Take to the previous orders activity
            // Send a variable to notify the fragment loader on which fragment to display
        }else if(s.contains("view my order") || s.contains("view order")) {
            // Take to the current orders activity
            // Send a variable to notify fragment holder to load the current-orders fragment
        }
    }

    /**
     * Initialize amazon polly
     */
    private void initPollyClient() {
        // Initialize the Amazon Cognito credentials provider.
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:7e3cee5d-a9dc-4951-8d7c-63275541e8e3", // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        // Create a client that supports generation of presigned URLs.
        client = new AmazonPollyPresigningClient(credentialsProvider);
    }
    private void showToast(boolean isConnected) {
        String message = "Checking";
        if (isConnected) {
            message = "Good! Connected to Internet";
            if(animationDrawable != null){
                animationDrawable.start();
            }
            //this.btn.setClickable(true);

        } else {
            if(animationDrawable != null){
                animationDrawable.stop();
            }
            message = "Sorry! Please connect to the internet to proceed";
            //this.btn.setClickable(false);
        }
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
    /**
     * Setup responding to the user
     */
    private void setupPlayButton(String words){
        MessageItem item = new MessageItem(words);
        message_items.add(item);
        if(voices != null){
            // Create speech synthesis request.
            SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                    new SynthesizeSpeechPresignRequest()
                            // Set text to synthesize.
                            .withText(words)
                            // Set voice selected by the user.
                            .withVoiceId(voices.get(33).getId())
                            // Set format to MP3.
                            .withOutputFormat(OutputFormat.Mp3);

            // Get the presigned URL for synthesized speech audio stream.
            URL presignedSynthesizeSpeechUrl =
                    client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

            Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

            // Create a media player to play the synthesized audio stream.
            if (mediaPlayer.isPlaying()) {
                setupNewMediaPlayer();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                // Set media player's data source to previously obtained URL.
                mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
            } catch (IOException e) {
                Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
            }

            // Start the playback asynchronously (since the data source is a network stream).
            mediaPlayer.prepareAsync();
        }else {
            Toast.makeText(ctx, "Unable to get voice response.", Toast.LENGTH_LONG).show();
        }

    }

    private class GetPollyVoices extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (voices != null) {
                return null;
            }

            // Create describe voices request.
            DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

            DescribeVoicesResult describeVoicesResult;
            try {
                // Synchronously ask the Polly Service to describe available TTS voices.
                describeVoicesResult = client.describeVoices(describeVoicesRequest);
            } catch (RuntimeException e) {
                Log.e(TAG, "Unable to get available voices. " + e.getMessage());
                return null;
            }

            // Get list of voices from the result.
            voices = describeVoicesResult.getVoices();

            // Log a message with a list of available TTS voices.
            Log.i(TAG, "Available Polly voices: " + voices);

            return null;
        }


    }
}
