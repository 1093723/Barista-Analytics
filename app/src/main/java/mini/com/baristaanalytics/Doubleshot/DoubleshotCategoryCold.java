package mini.com.baristaanalytics.Doubleshot;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.speech.RecognitionListener;
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

import Adapter.CoffeeMenuAdapter;
import Database.Database;
import Model.Beverage;
import Model.CoffeeOrder;
import Services.OrderService;
import Services.SpeechProcessorService;
import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.Account_Management.LoginActivity;
import mini.com.baristaanalytics.Account_Management.RegisterCustomerActivity;
import mini.com.baristaanalytics.Order.CustomerOrders;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;
import mini.com.baristaanalytics.R;
import utilities.MyApplication;

public class DoubleshotCategoryCold extends AppCompatActivity implements
        RecognitionListener,ConnectivityReceiver.ConnectivityReceiverListener {
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
    private CoffeeMenuAdapter adapter;
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
    protected void onStart(){
        super.onStart();
        btnBruce.setEnabled(true);
        btnBruce.setClickable(true);
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
        btnBruce.setClickable(true);
        btnBruce.setEnabled(true);
        if(animationDrawable != null){
            animationDrawable.start();
        }
    }
    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ctx,"fadein-to-fadeout");
        if (speech != null) {
            speech.destroy();
            mediaPlayer.release();
            Log.d("Log", "destroy");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubleshot_category_cold);
        Log.d(TAG,"onCreate:Activity Started");
        initVariables();
        setupBruce();
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
                        OrderService orderService = new OrderService();
                        if(!orderService.beverageExists(beverage,beverageList)){
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

                adapter = new CoffeeMenuAdapter(beverageList, DoubleshotCategoryCold.this);

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

    private void setupBruce() {
        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        /*
        Minimum time to listen in millis. Here 5 seconds
         */
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);

        btnBruce.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View p1)
            {
                progressBar.setVisibility(View.VISIBLE);
                speech.startListening(recognizerIntent);
                btnBruce.setEnabled(false);

            }
        });
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
        doubleshotCoffeeOrders = database.getReference("DoubleshotCoffeeOrders");
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
    /**
     * Setup responding to the user
     */
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
                    // Proceed to confirm the order
                    if(coffeePrice != null && coffeeName != null){
                        String userName = "MO";
                        String description = "1x " + coffeeSize + " " + coffeeName;
                        coffeeOrder.setOrder_Description(description);
                        coffeeOrder.setOrder_CustomerUsername(userName);
                        coffeeOrder.setOrder_Total(coffeePrice);
                        coffeeOrder.setOrder_State("Ordered");
                        coffeeOrder.setOrder_Store("Doubleshot Coffee & Tea");
                        coffeeOrder.setOrder_Rating(Float.valueOf(0));
                        coffeeOrder.setOrder_Date("23/11/20180");
                        new Database(getBaseContext()).clearCart();
                        new Database(getBaseContext()).addToCart(coffeeOrder);
                    }


                    if(s.contains("yes") || s.contains("yeah") || s.contains("sure")){
                        if(mAuth.getCurrentUser() != null){
                            // Process and confirm user order
                            OrderService orderService = new OrderService();
                            coffeeOrder.setUUID(mAuth.getUid());
                            String[] splitted = mAuth.getCurrentUser().getEmail().split("@");
                            coffeeOrder.setOrder_CustomerUsername(splitted[0]);
                            boolean confirmOrder = orderService.process_order(coffeeOrder,doubleshotCoffeeOrders);
                            if(confirmOrder){
                                Intent intent = new Intent(this,CustomerOrders.class);
                                resetOrder();
                                startActivity(intent);
                            }else {
                                String failedOrder = "Your order could not be processed at this time";
                                setupPlayButton(failedOrder);
                            }

                        }else {
                            // Take to the sign-in service
                            Intent intent = new Intent(this,LoginActivity.class);
                            intent.putExtra("coffeePlaceName","Doubleshot Coffee & Tea");
                            startActivity(intent);
                        }
                    }else if(s.contains("no") || s.contains("do not")){
                        Intent intent = new Intent(this,RegisterCustomerActivity.class);
                        intent.putExtra("coffeePlaceName","Doubleshot Coffee & Tea");
                        startActivity(intent);
                    }
                    else {
                        String bruceConfirmation = "We're almost there. Do ' a sign-in account?";
                        setupPlayButton(bruceConfirmation);
                    }
                }
                else {
                    String missedCoffeeSize = "Is that a small or tall size cup of coffee";
                    setupPlayButton(missedCoffeeSize);
                }
            }
        }else {
            String didNotGetEntireOrder = "I'm sorry I didn't get your order. Please include the name " +
                    "and whether you prefer small or tall";
            setupPlayButton(didNotGetEntireOrder);
        }
    }
    private void resetOrder() {
        this.coffeeOrder.setOrder_Rating(null);
        this.coffeeOrder.setOrder_Rating(null);
        this.coffeeOrder.setOrder_Rating(null);
        this.coffeeOrder.setOrder_Rating(null);
        this.coffeeOrder.setOrder_Rating(null);
        this.coffeeOrder.setOrder_Rating(null);
        this.coffeeOrder.setOrder_Rating(null);
        this.coffeeOrder.setOrder_Rating(null);
    }

    private void showToast(boolean isConnected) {
        String message = "Checking";
        if (isConnected) {
        message = "Good! Connected to Internet";
        } else {
        message = "Sorry! Please connect to the internet to proceed";
        }
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    /*******************************************************
     * Bruce Functionality : Speech To Text W/O Dialogue
     ******************************************************/

    @Override
    public void onBeginningOfSpeech() {
        Log.d("Log", "onBeginningOfSpeech");
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
            Log.d("Log", "onBufferReceived: " + buffer);
            }

    @Override
    public void onEndOfSpeech() {
    Log.d("Log", "onEndOfSpeech");
    progressBar.setVisibility(View.INVISIBLE);
    btnBruce.setEnabled(true);
//        decodeUserInput(userInput);
//        Toast.makeText(ctx, userInput, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d("Log", "FAILED " + errorMessage);
        progressBar.setVisibility(View.INVISIBLE);
        Log.i(TAG,errorMessage);
        //returnedText.setText(errorMessage);
        Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT).show();
        btnBruce.setEnabled(true);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.d("Log", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
            Log.d("Log", "onPartialResults");

            ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = "";
            /* To get all close matchs
            for (String result : matches)
            {
                text += result + "\n";
            }
            */


            //returnedText.setText(text);
            }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.d("Log", "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("Log", "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = matches.get(0); //  Remove this line while uncommenting above    codes
        decodeUserInput(text);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d("Log", "onRmsChanged: " + rmsdB);
        //progressBar.setProgress((int) rmsdB);

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
        case SpeechRecognizer.ERROR_AUDIO:
            message = "Audio recording error";
            break;
        case SpeechRecognizer.ERROR_CLIENT:
            message = "Client side error";
            break;
        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
            message = "Insufficient permissions";
            break;
        case SpeechRecognizer.ERROR_NETWORK:
            message = "Network error";
            break;
        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            message = "Network timeout";
            break;
        case SpeechRecognizer.ERROR_NO_MATCH:
            message = "No match";
            break;
        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            message = "RecognitionService busy";
            break;
        case SpeechRecognizer.ERROR_SERVER:
            message = "error from server";
            break;
        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            message = "No speech input";
            break;
        default:
            message = "Didn't understand, please try again.";
            break;
            }
            return message;
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
            .withVoiceId(voices.get(36).getId())
            // Set format to MP3.
            .withOutputFormat(OutputFormat.Mp3);

            // Get the presigned URL for synthesized speech audio stream.
            URL presignedSynthesizeSpeechUrl =
            client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

            Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

            // Create a media player to play the synthesized audio stream.
            if (!mediaPlayer.isPlaying()) {
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

    public void SignIn(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showToast(isConnected);
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
            setupPlayButton("Let's get Something to cool down the summer heat from Doubleshot");
            return null;
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


}
