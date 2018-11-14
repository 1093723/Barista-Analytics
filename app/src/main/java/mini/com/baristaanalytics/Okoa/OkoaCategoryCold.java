package mini.com.baristaanalytics.Okoa;

import android.animation.ArgbEvaluator;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import Model.Beverage;
import Model.CoffeeOrder;
import Services.OrderService;
import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.LoginActivity;
import mini.com.baristaanalytics.Order.OrderConfirmed;
import mini.com.baristaanalytics.R;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;

public class OkoaCategoryCold extends AppCompatActivity {
    private final String TAG = "OKOA_COLD_CATEGORY";
    private Dialog helpDialog;

    /**
     * Bruce-related variables
     */
    private final int REQ_CODE_SPEECH_INPUT = 100;
    // AWS Polly vars
    CognitoCachingCredentialsProvider credentialsProvider;
    private List<Voice> voices;
    // Amazon Polly permissions.
    private static final String COGNITO_POOL_ID = "CHANGEME";
    // Region of Amazon Polly.
    private static final Regions MY_REGION = Regions.US_EAST_1;
    private AmazonPollyPresigningClient client;

    /**
     * Order-related variables
     */
    private CoffeeOrder coffeeOrder;
    private String confirmation;
    private String order_description;
    private Boolean final_Confirmation;
    private ElegantNumberButton btnSmall,btnLarge;

    /**
     * Layout-related variables
     */
    ViewPager viewPager;
    private Context ctx;
    private Beverage beverage;
    private TextView txtViewPriceSmall,txtViewPriceLarge;
    private Integer[] colors = null;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private OkoaColdMenuAdapter adapter;
    private List<Beverage> beverageList;

    /**
     * Firebase-related variables
     */
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference coffeeList,coffee_Order;

    /**
     * Google speech to text
     */
    // Array of input speech from user
    private List<MessageItem> message_items = new ArrayList<>();
    // Media player
    private MediaPlayer mediaPlayer;

    private List<String> coffeeNames;

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
        setContentView(R.layout.activity_okoa_category_cold);
        helpDialog = new Dialog(this);

        initVariables();
        initPollyClient();
        setupNewMediaPlayer();


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
                        if(beverage.getBeverage_category().equals("cold")){
                            String coffeeName = beverage.getBeverage_name().toLowerCase();
                            beverage.setBeverage_name(coffeeName);
                            coffeeNames.add(beverage.getBeverage_name());
                            beverageList.add(beverage);
                        }
                    }
                }

                adapter = new OkoaColdMenuAdapter(beverageList, OkoaCategoryCold.this);

                viewPager = findViewById(R.id.viewPager);
                viewPager.setAdapter(adapter);
                viewPager.setPadding(130,0,130,0);
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        beverage = beverageList.get(position);
                        txtViewPriceLarge.setText(beverage.getPrice_tall().toString());
                        txtViewPriceSmall.setText(beverage.getPrice_small().toString());
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
    public void help_tutorial_hot(View v){
        TextView textclose;
        helpDialog.setContentView(R.layout.help_tutorial_hot);
        textclose = (TextView) helpDialog.findViewById(R.id.Xclose);
        textclose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                helpDialog.dismiss();
            }
        });
        helpDialog.show();
    }
    /**
     * Initialize global variables to be used
     */
    private void initVariables() {
        database = FirebaseDatabase.getInstance();
        coffee_Order = database.getReference("OkoaCoffeeOrders");
        coffeeList = database.getReference("CoffeeMenuOkoa");
        mAuth = FirebaseAuth.getInstance();
        coffeeNames = new ArrayList<>();

        btnLarge = (ElegantNumberButton)findViewById(R.id.number_button_large);
        btnSmall = (ElegantNumberButton)findViewById(R.id.number_button_small);
        txtViewPriceSmall = (TextView)findViewById(R.id.txtView_beverage_price_small);
        txtViewPriceLarge = (TextView)findViewById(R.id.txtView_beverage_price_large);

        ctx = OkoaCategoryCold.this;
        beverageList = new ArrayList<>();
        coffeeOrder = new CoffeeOrder();
    }

    @Override
    public void onResume(){
        super.onResume();
        /**
         * Resume from the sign-in activity
         */
        // Check if the user is signed-ins
        if(mAuth.getCurrentUser() != null && confirmation!=null){
            // User has logged in
            if(final_Confirmation != null){
                if(final_Confirmation){
                    // Get final confirmation from the user
                    String userReq = "The total is " + coffeeOrder.getOrder_Total() +
                            " rands. Is that in order?";
                    setupPlayButton(userReq);
                    final_Confirmation= false;
                }
                else {
                    // User confirmed in previous call
                    // 1. Register their order
                    // 2. Take them to the order layout
                    OrderService orderService = new OrderService();
                    coffeeOrder.setUUID(mAuth.getUid());
                    String complete = "All complete. Hold tight while the Okoa Barista gets your order " +
                            "ready.";
                    setupPlayButton(complete);
                    orderService.process_order(coffeeOrder,coffee_Order);
                    Intent x = new Intent(this, OrderConfirmed.class);
                    startActivity(x);
                    finish();
                }
            }else {

            }

        }
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ctx,"fadein-to-fadeout");
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
     * This method processes the input from a user in order to process an order
     * @param s is the input string from the user
     */
    private void decodeUserInput(String s) {
        if((s.contains("ready") || s.contains("confirm") ||
                s.contains("order") || s.contains("done")) && confirmation == null){
            // User is making an order
            // Get the screen they have slided to
            String qtyLarge = btnLarge.getNumber();
            String qtySmall = btnSmall.getNumber();
            Integer large_Quantity = Integer.parseInt(qtyLarge);
            Integer small_Quantity = Integer.parseInt(qtySmall);
            String userRequest = "";

            if(large_Quantity >0 && small_Quantity > 0){
                // Give me both large and small
                Long price_small = beverage.getPrice_small();
                Long price_lrg = beverage.getPrice_tall();
                Long total = price_lrg+price_small;
                order_description = large_Quantity + "x Tall" + beverage.getBeverage_name() + '\n'
                        + small_Quantity + "x Small " +
                        beverage.getBeverage_name();
                userRequest = "Just to confirm. You've ordered " + large_Quantity + " large and " +
                        small_Quantity + " small " +
                        beverage.getBeverage_name() + ". Is that correct?";
                coffeeOrder.setOrder_Description(order_description);
                coffeeOrder.setOrder_Total(total);
                coffeeOrder.setOrder_Store("Okoa Coffee Co.");
            }else if(large_Quantity > 0){
                // Give me a large
                Long price_lrg = beverage.getPrice_tall();
                Long lrg_Total = price_lrg*large_Quantity;
                if(large_Quantity > 1){
                    userRequest = "You've ordered " + large_Quantity + " Tall " + beverage.getBeverage_name() +"'s"
                            +". Is that correct?";
                    order_description = large_Quantity + "x Tall " + beverage.getBeverage_name();
                    coffeeOrder.setOrder_Description(order_description);
                    coffeeOrder.setOrder_Total(lrg_Total);
                }else {
                    userRequest = "You've ordered one large " + beverage.getBeverage_name()
                            +". Is that correct?";
                    order_description = large_Quantity + " Tall " + beverage.getBeverage_name();
                    coffeeOrder.setOrder_Description(order_description);
                    coffeeOrder.setOrder_Total(lrg_Total);
                }
            }
            else if(small_Quantity > 0){
                // Give me a small
                Long price_small = beverage.getPrice_small();
                Long small_Total = price_small*small_Quantity;
                if(small_Quantity > 1){
                    userRequest = "You've ordered " + small_Quantity + " small "
                            +beverage.getBeverage_name() + "'s"
                            +". Is that correct?";
                    order_description = small_Quantity + "x Small " + beverage.getBeverage_name();
                    coffeeOrder.setOrder_Description(order_description);
                    coffeeOrder.setOrder_Total(small_Total);
                }else {
                    userRequest = "You've ordered one small "
                            +beverage.getBeverage_name()
                            +". Is that correct?";
                    order_description = small_Quantity + " Small " + beverage.getBeverage_name();
                    coffeeOrder.setOrder_Description(order_description);
                    coffeeOrder.setOrder_Total(small_Total);
                }
            }
            else {
                // I haven't decided on anything
                userRequest = "Seems like you've forgotten to specify how many " + beverage.getBeverage_name() +
                        " you would like.";
            }
            setupPlayButton(userRequest);
        }else if(s.contains("yes")){
            confirmation = "yes";
            if(mAuth.getCurrentUser() != null){
                String account = "Give me a second to send your order to the Barista";
                coffeeOrder.setUUID(mAuth.getUid());
                coffeeOrder.setOrder_CustomerUsername(mAuth.getCurrentUser().getEmail());
                OrderService orderService = new OrderService();
                orderService.process_order(coffeeOrder,coffee_Order);
                Intent x = new Intent(this, OrderConfirmed.class);
                startActivity(x);
                finish();
                setupPlayButton(account);
            }else {
                String confirm_account = "Let's get you signed in before wrapping this up";
                setupPlayButton(confirm_account);
                Intent sign_in = new Intent(this, LoginActivity.class);
                sign_in.putExtra("sign_in","sign_in");
                startActivity(sign_in);
                final_Confirmation = true;
            }
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
