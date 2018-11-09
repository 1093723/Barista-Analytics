package mini.com.baristaanalytics.Okoa;

import android.animation.ArgbEvaluator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import Adapter.OkoaColdMenuAdapter;
import Model.Beverage;
import Model.CoffeeOrder;
import Services.CategoryHelper;
import Services.OrderService;
import Services.SpeechProcessorService;
import mini.com.baristaanalytics.LoginActivity;
import mini.com.baristaanalytics.Order.CustomerOrders;
import mini.com.baristaanalytics.Order.OrderConfirmed;
import mini.com.baristaanalytics.R;
import mini.com.baristaanalytics.Registration.RegisterCustomerActivity;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;

public class OkoaCategoryHot extends AppCompatActivity {
    private String TAG = "OKOA HOT";
    /**
     * Bruce-related variables
     */
    private final int REQ_CODE_SPEECH_INPUT = 100;
    // AWS Polly vars
    private List<String> coffeeNames;

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
    String order_description;
    private Boolean final_Confirmation, checkAccount;
    private ElegantNumberButton btnSmall,btnLarge;

    /**
     * Layout-related variables
     */
    private Context ctx;
    private Beverage beverage;
    private ViewPager viewPager;
    private OkoaColdMenuAdapter adapter;
    private List<Beverage> models;
    private Integer[] colors = null;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private TextView txtViewPriceSmall,txtViewPriceLarge;
    private List<Beverage> beveragesList;

    private Boolean accountExists;
    private Boolean isAnswered;
    /**
     * Firebase-related variables
     */
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
        DatabaseReference coffeeList,coffee_Order;    // Speech to text

    /**
     * Google speech to text
     */
    private MediaPlayer mediaPlayer;
    // Array of input speech from user
    private List<MessageItem> message_items = new ArrayList<>();

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
        initVariables();
        initPollyClient();
        setupNewMediaPlayer();
        accountExists = false;
        checkAccount = false;
        isAnswered = false;
        coffeeList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap :
                        dataSnapshot.getChildren()) {
                    Beverage beverage = snap.getValue(Beverage.class);
                    if(beveragesList.size() > 0){
                        /**
                         * Flag for determining if there are coffee descriptions(from firebase)
                         * the same as the ones we have already retrieved on the layout
                         **/
                        Boolean exists = CategoryHelper.beverageExists(coffeeNames,beverage);
                        if(!exists){
                            // Add to array as the beverage does not exist
                            if(beverage.getBeverage_category().equals("hot")){
                                String coffeeName = beverage.getBeverage_name().toLowerCase();
                                beverage.setBeverage_name(coffeeName);
                                coffeeNames.add(beverage.getBeverage_name());
                                beveragesList.add(beverage);
                            }
                        }
                        else {
                            // Bevarage exists so replace the beverage at that index
                            CategoryHelper.replaceBeverage(beverage,beveragesList);
                        }
                    }else {
                        if(beverage.getBeverage_category().equals("hot")){
                            String coffeeName = beverage.getBeverage_name().toLowerCase();
                            beverage.setBeverage_name(coffeeName);
                            coffeeNames.add(beverage.getBeverage_name());
                            beveragesList.add(beverage);
                        }
                    }
                }

                adapter = new OkoaColdMenuAdapter(beveragesList, OkoaCategoryHot.this);

                viewPager = findViewById(R.id.viewPager);
                viewPager.setAdapter(adapter);
                viewPager.setPadding(130,0,130,0);
                final ImageView background_image_view = findViewById(R.id.background_image);
                /*Integer[] colors_temp = {
                        getResources().getColor(R.color.color30),
                        getResources().getColor(R.color.color2),
                        getResources().getColor(R.color.color3),
                        getResources().getColor(R.color.color5),
                        getResources().getColor(R.color.color30),
                        getResources().getColor(R.color.color2),
                        getResources().getColor(R.color.color3),
                        getResources().getColor(R.color.color5),
                        getResources().getColor(R.color.color30),
                        getResources().getColor(R.color.color2),
                        getResources().getColor(R.color.color3),
                        getResources().getColor(R.color.color5),
                }; */

                // colors = colors_temp;
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            beverage = beveragesList.get(position);
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

    /**
     * Initialize variables to be used
     */
    private void initVariables() {
        coffeeNames = new ArrayList<>();
        beveragesList = new ArrayList<>();
        ctx = OkoaCategoryHot.this;
        mAuth = FirebaseAuth.getInstance();
        txtViewPriceSmall = (TextView)findViewById(R.id.txtView_beverage_price_small);
        txtViewPriceLarge = (TextView)findViewById(R.id.txtView_beverage_price_large);
        btnLarge = (ElegantNumberButton)findViewById(R.id.number_button_large);
        btnSmall = (ElegantNumberButton)findViewById(R.id.number_button_small);
        coffeeOrder = new CoffeeOrder();
        database = FirebaseDatabase.getInstance();
        coffee_Order = database.getReference("OkoaCoffeeOrders");
        coffeeList = database.getReference("CoffeeMenuOkoa");
    }

    @Override
    public void onResume(){
        super.onResume();
        /**
         * Resume from the sign-in activity
         */
        // Check if the user is signed-ins
        if(mAuth.getCurrentUser() != null ){
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
                    String[] splitted = mAuth.getCurrentUser().getEmail().split("@");

                    coffeeOrder.setOrder_CustomerUsername(splitted[0]);
                    String complete = "All complete. Hold tight while the Okoa Barista gets your order " +
                            "ready.";
                    setupPlayButton(complete);
                    orderService.process_order(coffeeOrder,coffee_Order);
                    Intent x = new Intent(this, CustomerOrders.class);
                    startActivity(x);
                    finish();
                }
            }else {
                Toast.makeText(ctx, "Final confirmation null", Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
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
        SpeechProcessorService speechProcessor= new SpeechProcessorService(s,ctx);
        String[] orders = s.split("and");
        String orderDescription = "";
        Long orderTotal = Long.valueOf(0);
        if(mAuth.getCurrentUser() != null &&(final_Confirmation != null) && s.contains("yes")){
            OrderService orderService = new OrderService();
            coffeeOrder.setUUID(mAuth.getUid());
            String[] splitted = mAuth.getCurrentUser().getEmail().split("@");

            coffeeOrder.setOrder_CustomerUsername(splitted[0]);
            String complete = "All complete. Hold tight while the Okoa Barista gets your order " +
                    "ready.";
            setupPlayButton(complete);
            orderService.process_order(coffeeOrder,coffee_Order);
            Intent x = new Intent(this, CustomerOrders.class);
            startActivity(x);
            finish();
        }
        else if(s.contains("yes") || s.contains("yeah") || s.contains("sure")){
            final_Confirmation = true;

            if(isAnswered){
                String askSignIn = "Do you already have an account?";
                checkAccount = true;
                isAnswered = false;
                setupPlayButton(askSignIn);
            }else {
                if(checkAccount){
                    Intent x = new Intent(this,LoginActivity.class);
                    x.putExtra("sign_in","sign_in");
                    startActivity(x);
                    String instruct = "Let's get you signed-in so I can put a face to the coffee";
                    setupPlayButton(instruct);

                }else{
                    String instruct = "Let's get you signed-up so I can put a face to the coffee";
                    setupPlayButton(instruct);
                    Intent x = new Intent(this, RegisterCustomerActivity.class);
                    x.putExtra("sign_in","sign_in");
                    startActivity(x);
                }
            }


        }else {
            if(orders.length > 1){
                int orderCount = orders.length;
                for (int i = 0; i < orderCount; i++) {
                    // Get the coffee name of the order
                    String coffeeName = getCoffeeName(orders[i]);

                    // Get quantity of coffee
                    Integer quantity = speechProcessor.getCoffeeQuantity(s);
                    // Get the size of the coffee order
                    String size = speechProcessor.getCoffeeSize("prod",s);
                    // Get the price of the order
                    Long price = getCoffeePrice(coffeeName, size);
                    if(price == null){
                        price = Long.valueOf(0);
                    }
                    // Set the order description
                    orderDescription = order_description + quantity + "x " + size + " " + coffeeName;
                    if(i+1 != orderCount){
                        order_description += ",";
                    }
                    // Increment the total
                    orderTotal+=price;

                }
                coffeeOrder.setOrder_Total(orderTotal);
                coffeeOrder.setOrder_Description(orderDescription);
                //coffeeOrder.setOrder_Store("Okoa Coffee Co.");

//            userRequest = "Just to confirm. You've ordered " + large_Quantity + " large and " +
//                    small_Quantity + " small " +
//                    beverage.getBeverage_name()+"'s" + ". Is that correct?";

            }else {
                // Get the coffee name of the order
                String coffeeName = getCoffeeName(s);
                // Get quantity of coffee
                Integer quantity = speechProcessor.getCoffeeQuantity(s);
                // Get the size of the coffee order
                String size = speechProcessor.getCoffeeSize("prod",s);
                // Get the price of the order
                Long price = getCoffeePrice(coffeeName, size);

                orderDescription = orderDescription + quantity + "x " + size + " " + coffeeName +
                        ". The total is " + price + " rands";
                coffeeOrder.setOrder_Total(price);
                coffeeOrder.setOrder_Description(orderDescription);
                //coffeeOrder.setOrder_Store("Okoa Coffee Co.");
                coffeeOrder.setOrder_date(DateTime.now().toLocalDate().toString());
                coffeeOrder.setOrder_State("requested");
                // Ordered just one item
                isAnswered = true;
                String confirmation = orderDescription + ". Is that correct?";
                setupPlayButton(confirmation);
            }
        }
    }
    private Long getCoffeePrice(String coffeeName, String size) {
        for (int i = 0; i < models.size(); i++) {
            String beverage = models.get(i).getBeverage_name().toLowerCase();
            if(beverage.contains(coffeeName)){
                if(size.equals("small")){
                    return models.get(i).getPrice_small();
                }else {
                    return models.get(i).getPrice_tall();
                }
            }
        }
        return null;
    }

    private String getCoffeeName(String order) {
        for (int i = 0; i < coffeeNames.size(); i++) {
            if(order.contains(coffeeNames.get(i))){
                return coffeeNames.get(i);
            }
        }
        return "-1";
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
