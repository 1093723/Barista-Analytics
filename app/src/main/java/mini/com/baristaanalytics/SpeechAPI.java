package mini.com.baristaanalytics;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapter.RecyclerViewAdapter;
import Services.SpeechProcessorService;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;
import utilities.MyApplication;

import static android.content.ContentValues.TAG;

public class SpeechAPI extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    Context ctx;
    private TextView message;
    private ImageButton btnSpeak;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<MessageItem> message_items = new ArrayList<>();

    private final int REQ_CODE_SPEECH_INPUT = 100;

    //AWS Polly Vars
    CognitoCachingCredentialsProvider credentialsProvider;
    private List<Voice> voices;

    // Amazon Polly permissions.
    private static final String COGNITO_POOL_ID = "CHANGEME";

    // Region of Amazon Polly.
    private static final Regions MY_REGION = Regions.US_EAST_1;
    private AmazonPollyPresigningClient client;

    // AWS Media Player
    MediaPlayer mediaPlayer;

    ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupVoicesList();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_api);
        btnSpeak = findViewById(R.id.btnSpeak);
        ctx = this;
        message = findViewById(R.id.message);
        checkConnection();
        initPollyClient();
        //Snackbar snack = (Snackbar) findViewById(R.id.fab);
        setupNewMediaPlayer();
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }
    /***
     * Internet-related permissions
     */
    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showToast(isConnected);
        if(!isConnected){

            btnSpeak.setClickable(false);
        }else {
            btnSpeak.setClickable(true);
            initPollyClient();
        }
    }

    private void showToast(boolean isConnected) {
        String message;
        boolean flag = true;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            btnSpeak.setClickable(false);
        }
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
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
        unregisterReceiver(connectivityReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        MyApplication.getInstance().setConnectivityListener(this);
    }
    /**
     * Setup responding to the user
     */
    private void setupPlayButton(String words){
        if(words.equals("welcome")){
            String welcome_message = "Hello. I'm Bruce. Your voice-assistant Barista throughout your " +
                    "use of the application. I can help you search for coffee places and view available coffee shops near you";
            MessageItem item = new MessageItem(welcome_message);
            message_items.add(item);
            // Create speech synthesis request.
            SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                    new SynthesizeSpeechPresignRequest()
                            // Set text to synthesize.
                            .withText(welcome_message)
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
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
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

    }
    /**
     * Setup Polly Voices List
     */
    void setupVoicesList() {
        // Asynchronously get available Polly voices.
        new GetPollyVoices().execute();

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
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // result.get(0) contains the result from the user
                    MessageItem message_item = new MessageItem(result.get(0));
                    message_items.add(message_item);
                    Log.d(TAG, "The array size is: " + message_items.size());
                    initRecyclerView();
                    SpeechProcessorService speechProcessorService = new SpeechProcessorService(result.get(0),ctx);

                    if(speechProcessorService.isOkoaRequested()){
                        setupPlayButton("You'd like Okoa wouldn't you?");
                    }else if(speechProcessorService.isSupportedCoffeePlace()){
                        setupPlayButton("Now showing supported coffee places");
                    }else {
                        setupPlayButton("I didn't quite get that.");
                    }
                    // Say anything related to 'like' or 'coffee' to trigger the maps
//                    if(result.get(0).contains("like") && result.get(0).contains("coffee")){
//                        Intent x = new Intent(this, MapsActivity.class);
//                        String toSpeak = "Proceeding to user registration";
//                        MessageItem message = new MessageItem(toSpeak);
//                        message_items.add(message);
//                        //Trigger AWS Polly
//                        setupPlayButton("We're Almost There");
//                        startActivity(x);
//                        finish();
//                    }
//                    else if((result.get(0).contains("registration") ||
//                            result.get(0).contains("register")) &&
//                            (result.get(0).contains("user") || result.get(0).contains("customer"))){
//                        Intent x = new Intent(this, RegisterCustomerActivity.class);
//                        String toSpeak = "Let's get you signed in so you can order coffee";
//                        setupPlayButton(toSpeak);
//                        startActivity(x);
//                    }else if((result.get(0).contains("registration") ||
//                            result.get(0).contains("register")) &&
//                            (result.get(0).contains("admin") || result.get(0).contains("administrator"))){
//                        Intent x = new Intent(this, RegisterAdminActivity.class);
//                        String toSpeak = "Proceeding to administrator registration";
//                        setupPlayButton(toSpeak);
//                        startActivity(x);
//                    }
                    //message.setText(message_items.size());
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(message_items,this);
        recyclerView.setAdapter(adapter);
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

            setupPlayButton("welcome");
            return null;
        }


    }
}
