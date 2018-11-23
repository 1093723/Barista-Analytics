package mini.com.baristaanalytics.Doubleshot;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
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

import Adapter.OkoaColdMenuAdapter;
import Database.Database;
import Model.Beverage;
import Model.CoffeeOrder;
import Services.OrderService;
import Services.SpeechProcessorService;
import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.Account_Management.LoginActivity;
import mini.com.baristaanalytics.Okoa.OkoaCategoryCold;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;
import mini.com.baristaanalytics.R;

public class DoubleshotCategoryCold extends AppCompatActivity {
    private String TAG = "DOUBLESHOT COLD";
    /**
     * Bruce-related variables
     */
    private ImageButton btnBruce;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private SpeechProcessorService speechProcessorService;
    // AWS Polly vars
    CognitoCachingCredentialsProvider credentialsProvider;
    private List<Voice> voices;
    private AmazonPollyPresigningClient client;

    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;
    /**
     * Order-related variables
     */
    private List<String> coffeeNames;
    private ProgressBar progressBar;
    private CoffeeOrder coffeeOrder;
    private Boolean validCoffeeSize;
    private Boolean validCoffeeName;
    /**
     * Layout-related variables
     */
    private ViewPager viewPager;
    private Context ctx;
    private Beverage beverage;
    private TextView txtViewPriceSmall,txtViewPriceLarge;
    private OkoaColdMenuAdapter adapter;
    private List<Beverage> beverageList;
    private Dialog helpDialog;
    private TableRow tableRow;

    /**
     * Firebase-related variables
     */
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference coffeeList,doubleshotCoffeeOrders;

    /**
     * Google speech to text
     */
    // Array of input speech from user
    private List<MessageItem> message_items = new ArrayList<>();
    // Media player
    private MediaPlayer mediaPlayer;

    private ImageView cupSmall,cupTall;
    private TextView txtView_beverage_price_small,
            txtView_beverage_price_tall,txtView_rands_small,txtView_beverage_rands_tall;
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

    public void help_tutorial_cold(View v){
        TextView textclose;
        helpDialog.setContentView(R.layout.help_tutorial_cold);
        textclose = (TextView) helpDialog.findViewById(R.id.Xclose);
        textclose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                helpDialog.dismiss();
            }
        });
        helpDialog.show();
    }
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ctx,"fadein-to-fadeout");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubleshot_category_cold);
        Log.d(TAG,"onCreate:Activity Started");
        initVariables();

        initPollyClient();
        setupNewMediaPlayer();
//        String Greeting = "What beverage would you like?";
//        setupPlayButton(Greeting);
        new WaitingTime().execute(6);
        coffeeList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :
                        dataSnapshot.getChildren()) {
                    Beverage beverage = snap.getValue(Beverage.class);
                    String tempCoffeeName = beverage.getBeverage_name().toLowerCase();
                    beverage.setBeverage_name(tempCoffeeName);
                    if(beverageList.size() > 0){
                        if(!beverageExists(beverage)){
                            if(beverage.getBeverage_category().equals("cold")){
                                String coffeeName = beverage.getBeverage_name().toLowerCase();
                                beverage.setBeverage_name(coffeeName);
                                coffeeNames.add(beverage.getBeverage_name());
                                beverageList.add(beverage);
                            }
                        }
                    }else {
                        if(beverage == null){
                            Toast.makeText(ctx, "Coffee Will Not Be Served Today", Toast.LENGTH_SHORT).show();
                        }else {
                            if(beverage.getBeverage_category().equals("cold")){
                                String coffeeName = beverage.getBeverage_name().toLowerCase();
                                beverage.setBeverage_name(coffeeName);
                                coffeeNames.add(beverage.getBeverage_name());
                                beverageList.add(beverage);
                            }
                        }
                    }
                }

                adapter = new OkoaColdMenuAdapter(beverageList, DoubleshotCategoryCold.this);

                viewPager.setAdapter(adapter);
                viewPager.setPadding(130,0,130,0);
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        beverage = beverageList.get(position);
                        if(beverage.getPrice_small().equals(Long.valueOf(0))){
                            cupSmall.setVisibility(View.GONE);
                            txtViewPriceSmall.setVisibility(View.GONE);
                            txtView_rands_small.setVisibility(View.GONE);
                            txtView_beverage_price_small.setVisibility(View.GONE);
                        }else {
                            cupSmall.setVisibility(View.VISIBLE);
                            txtViewPriceSmall.setVisibility(View.VISIBLE);
                            txtViewPriceSmall.setText(beverage.getPrice_small().toString());
                            txtView_beverage_price_small.setVisibility(View.VISIBLE);
                            txtView_rands_small.setVisibility(View.VISIBLE);
                        }
                        if(beverage.getPrice_tall().equals(Long.valueOf(0))){
                            cupTall.setVisibility(View.GONE);
                            txtView_beverage_price_tall.setVisibility(View.GONE);
                            txtViewPriceLarge.setVisibility(View.GONE);
                            txtView_beverage_rands_tall.setVisibility(View.GONE);
                        }else {
                            txtViewPriceLarge.setText(beverage.getPrice_tall().toString());
                            cupTall.setVisibility(View.VISIBLE);
                            txtView_beverage_price_tall.setVisibility(View.VISIBLE);
                            txtViewPriceLarge.setVisibility(View.VISIBLE);
                            txtView_beverage_rands_tall.setVisibility(View.VISIBLE);
                        }


                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private Boolean beverageExists(Beverage beverage){
        for (int i = 0; i < beverageList.size(); i++) {
            if(beverageList.get(i).getBeverage_name().equals(beverage.getBeverage_name())){
                beverage.setBeverage_name(beverage.getBeverage_name());
                // Update the array
                beverageList.set(i,beverage);
                return true;
            }
        }
        return false;
    }
    /**
     * Initialize global variables to be used
     */
    private void initVariables() {
        btnBruce = findViewById(R.id.btnSpeak);

        validCoffeeSize = false;
        validCoffeeName = false;

        speechProcessorService = new SpeechProcessorService();

        coffeeOrder = new CoffeeOrder();


        speechProcessorService = new SpeechProcessorService();

        cupSmall = findViewById(R.id.small_size_coffee_cup);
        cupTall = findViewById(R.id.large_size_coffee_cup);

        txtView_beverage_price_small = findViewById(R.id.txtView_beverage_price_small);
        txtView_beverage_price_tall = findViewById(R.id.txtView_beverage_price_large);

        txtView_rands_small = findViewById(R.id.randsSmall);
        txtView_beverage_rands_tall = findViewById(R.id.randTall);

        relativeLayout = findViewById(R.id.relLayoutConvo);
        animationDrawable = (AnimationDrawable)  relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
        viewPager = findViewById(R.id.viewPager);
        tableRow = findViewById(R.id.tblRowCoffeeCupParent);
        helpDialog = new Dialog(this);
        progressBar = findViewById(R.id.okoa_cold_progress);
        database = FirebaseDatabase.getInstance();
        coffeeList = database.getReference("CoffeeMenuDoubleshot");
        doubleshotCoffeeOrders = database.getReference("TRANSLATE_COFFEE_NAMES");
        mAuth = FirebaseAuth.getInstance();
        coffeeNames = new ArrayList<>();

        txtViewPriceSmall = (TextView)findViewById(R.id.txtView_beverage_price_small);
        txtViewPriceLarge = (TextView)findViewById(R.id.txtView_beverage_price_large);

        ctx = DoubleshotCategoryCold.this;
        beverageList = new ArrayList<>();
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

    private void decodeUserInput(String s) {
        String normalized = s.toLowerCase();
        String coffeeName = speechProcessorService.getCoffeeName(normalized,coffeeNames);

        if(validCoffeeName || coffeeName!=null){
            validCoffeeName = true;
            String coffeeSize = speechProcessorService.getCoffeeSize(normalized);
            if(coffeeSize != null || validCoffeeSize){
                validCoffeeSize = true;
                if(validCoffeeName && validCoffeeSize){
                    Long coffeePrice = speechProcessorService.getCoffeePrice(coffeeName,coffeeSize,beverageList);
                    // Prepare order
                    String description = "1x " + coffeeSize + " " + coffeeName;

                    //String[] splitEmail = mAuth.getCurrentUser().getEmail().split("@");
                    //String userName = splitEmail[0];
                    String userName = "MO";
                    if(s.contains("yes") || s.contains("yeah") || s.contains("sure")){
                        // Proceed to confirm the order
                        coffeeOrder.setOrder_Description(description);
                        coffeeOrder.setOrder_CustomerUsername(userName);
                        coffeeOrder.setOrder_Total(coffeePrice);
                        coffeeOrder.setOrder_State("Ordered");
                        coffeeOrder.setOrder_Store("Doubleshot Coffee & Tea");
                        if(new Database(ctx).databaseExists(ctx)){
                            new Database(getBaseContext()).addToCart(coffeeOrder);
                            if(mAuth.getCurrentUser() != null){
                                // Process and confirm user order
                                OrderService orderService = new OrderService();
                                orderService.process_order(coffeeOrder,doubleshotCoffeeOrders);
                            }else {
                                // Take to the sign-in service
                                Intent intent = new Intent(this,LoginActivity.class);
                                intent.putExtra("order","ordered");
                                startActivity(intent);
                            }
                        }else {
                            Toast.makeText(ctx, "Database does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        String bruceConfirmation = "Just to confirm : That's one " +
                                coffeeSize + " " + coffeeName + ". Is that correct?";
                        setupPlayButton(bruceConfirmation);
                    }
                }

            }else {
                String missedCoffeeSize = "Is that a small or tall size cup of coffee";
                setupPlayButton(missedCoffeeSize);
            }
        }else {
            String didNotGetEntireOrder = "I'm sorry I didn't get your order. Please include the name " +
                    "and whether you prefer small or tall";
            setupPlayButton(didNotGetEntireOrder);
        }
    }

    class WaitingTime extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... params) {
            viewPager.setVisibility(View.GONE);
            tableRow.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            for (int count = 1; count <= params[0]; count++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            tableRow.setVisibility(View.VISIBLE);
            tableRow.animate().alpha(1.0f).setDuration(12000);
            viewPager.animate().alpha(1.0f).setDuration(12000);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
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
