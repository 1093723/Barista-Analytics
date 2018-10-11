package mini.com.baristaanalytics.Okoa;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapter.FoodViewHolder;
import Interface.ItemClickListener;
import Model.Beverage;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;
import mini.com.baristaanalytics.R;

public class OkoaCategoryHot extends AppCompatActivity {
    private String TAG = "OKOA HOT";
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
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference coffeeList;

    FirebaseRecyclerAdapter<Beverage, FoodViewHolder> adapter;
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
        setContentView(R.layout.activity_okoa_category_hot);
        Log.d(TAG,"onCreate:Activity Started");
        this.ctx = this;
        // Firebase
        database = FirebaseDatabase.getInstance();
        coffeeList = database.getReference("CoffeeMenuOkoa");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewFoods);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadCoffeeList();

        initPollyClient();
        setupNewMediaPlayer();
        String Greeting = "What beverage would you like?";
        setupPlayButton(Greeting);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    private void loadCoffeeList() {
        FirebaseRecyclerOptions<Beverage> options =
                new FirebaseRecyclerOptions.Builder<Beverage>()
                        .setQuery(coffeeList.orderByChild("beverage_category").equalTo("hot"), Beverage.class)
                        .build();
                Log.d(TAG,"getting model model ");
        adapter = new FirebaseRecyclerAdapter<Beverage, FoodViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Beverage model) {
                holder.food_txt_viewName.setText(model.getBeverage_name());
                Picasso.with(getBaseContext()).load(model.getBeverage_image())
                        .into(holder.food_image);
                final Beverage beverage = model;
                Log.d(TAG,model.getPrice_small().toString());
                Log.d(TAG,model.getPrice_tall().toString());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(OkoaCategoryHot.this, beverage.getBeverage_name(), Toast.LENGTH_SHORT).show();
                        final Intent intent = new Intent(OkoaCategoryHot.this,OkoaCoffeeDetails.class);
                        intent.putExtra("beverageName", beverage.getBeverage_name());
                        intent.putExtra("beverageImage",beverage.getBeverage_image());
                        intent.putExtra("beverageDescription",beverage.getBeverage_description());
                        intent.putExtra("beveragePriceSmall",beverage.getPrice_small());
                        intent.putExtra("beveragePriceLarge",beverage.getPrice_tall());

                        startActivity(intent);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(view);
            }
        };
        // Set adapter
        Log.d(TAG,"setting adapter");
        recyclerView.setAdapter(adapter);
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
        Intent okoa = new Intent(this, OkoaSize.class);
        String coffeeName = "coffeeName";
        String almostThere;
        if(s.contains("Americano")){
            // Add the coffee name to the intent and retrieve it in the OkoaSize class
            almostThere = "Americano coming up. Let's confirm the size";
            setupPlayButton(almostThere);
            okoa.putExtra(coffeeName,"Americano");
            startActivity(okoa);
            // User would like americano
        }
        else if(s.contains("Chai Latte")){
            // User would like a Chai Latte
            // Add the coffee name to the intent and retrieve it in the OkoaSize class
            okoa.putExtra(coffeeName,"Chai Latte");
            startActivity(okoa);
        }else {
            String coffeeNotRecognized = "Please repeat that.";
            setupPlayButton(coffeeNotRecognized);
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
}
