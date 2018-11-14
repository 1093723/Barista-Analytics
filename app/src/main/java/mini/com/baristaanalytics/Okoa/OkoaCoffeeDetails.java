package mini.com.baristaanalytics.Okoa;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapter.FoodViewHolder;
import Model.Beverage;
import Services.OrderService;
import mini.com.baristaanalytics.Order.OrderConfirmed;
import mini.com.baristaanalytics.R;
import mini.com.baristaanalytics.Account.RegisterCustomerActivity;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;

public class OkoaCoffeeDetails extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String beverageName,beverageDescription,beveragePriceSmall,beveragePriceTall,beverageImage;
    private String TAG = "OKOA COFFEE DETAILS";
    private Context ctx;
    // Speech to text
    // Array of input speech from user
    private List<MessageItem> message_items = new ArrayList<>();
    private final int REQ_CODE_SPEECH_INPUT = 100;
    // AWS Polly vars
    CognitoCachingCredentialsProvider credentialsProvider;
    private List<Voice> voices;
    // Amazon Polly permissions.
    private static final String COGNITO_POOL_ID = "CHANGEME";

    // Region of Amazon Polly.
    private static final Regions MY_REGION = Regions.US_EAST_1;
    private AmazonPollyPresigningClient client;

    private ImageButton btn;
    // AWS Media Player
    private MediaPlayer mediaPlayer;

    FirebaseRecyclerAdapter<Beverage, FoodViewHolder> adapter;
    ElegantNumberButton btnLarge;
    ElegantNumberButton btnSmall;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okoa_coffee_details);
        Log.d(TAG,"onCreate(): Activity started");
    }
    public void promptSpeechInput(View view) {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if(isConnected){
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
     * This method takes all the input from the user in order to process their order
     * This is done by first confirming that they are still signed in
     *
     * @param view
     */
    public void processOrder(View view){
        if(btnSmall.getNumber() != null && btnLarge.getNumber() != null){
           // Ordered small and large
        }else if(btnSmall != null){
            // Only small
        }
        else if(btnLarge.getNumber() != null){
            // Only Large
        }else {
            // Nothing ordered
        }

        if(mAuth != null){
            if(mAuth.getCurrentUser() != null) {
                // Bruce stuff, that is, needs to confirm the order
                OrderService orderService = new OrderService();
                Long beveragePrice = Long.parseLong(beveragePriceTall);
                //orderService.processOrder("Okoa",mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getUid().toString(),"1",beverageName,beveragePrice,databaseRef);

                Intent orderConfirmed = new Intent(OkoaCoffeeDetails.this,OrderConfirmed.class);
                startActivity(orderConfirmed);
            }else {
                Intent registrationPage = new Intent(OkoaCoffeeDetails.this, RegisterCustomerActivity.class);
                registrationPage.putExtra("beverage_name", beverageName);
                registrationPage.putExtra("beverage_description", beverageDescription);
                registrationPage.putExtra("beverage_image", beverageImage);
                registrationPage.putExtra("price_small", beveragePriceSmall);
                registrationPage.putExtra("price_tall", beveragePriceTall);
                registrationPage.putExtra("orderQuantity", 1);
                startActivity(registrationPage);
            }
        }
        else {
            Intent registrationPage = new Intent(OkoaCoffeeDetails.this, RegisterCustomerActivity.class);
            startActivity(registrationPage);
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
        if(s.contains("yes") || s.contains("sure") || s.contains("right")
                || s.contains("definitely")){
            Intent confirmed = new Intent(this,OrderConfirmed.class);
            startActivity(confirmed);
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
            //this.btn.setClickable(true);

        } else {
            message = "Sorry! Please connect to the internet to proceed";
            //this.btn.setClickable(false);
        }
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
    private void setValues(Intent intent) {
        beverageName = intent.getStringExtra("beverage_name");
        beverageDescription = intent.getStringExtra("beverage_description");
        beverageImage = intent.getStringExtra("beverage_image");
        // String intent_beverage_category = intent.getStringExtra("beverage_category");
        beveragePriceSmall = intent.getStringExtra("price_small");
        // String intent_price_medium = intent.getStringExtra("price_medium");
        beveragePriceTall = intent.getStringExtra("price_tall");
    }
}
