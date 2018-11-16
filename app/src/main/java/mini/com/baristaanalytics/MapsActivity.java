package mini.com.baristaanalytics;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Barista;
import Adapter.ImageAdapter;
import Services.MapsServices;
import Services.SpeechProcessorService;
import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.Doubleshot.DoubleshotCategoryCold;
import mini.com.baristaanalytics.Doubleshot.DoubleshotCategoryHot;
import mini.com.baristaanalytics.Okoa.OkoaCategoryCold;
import mini.com.baristaanalytics.Okoa.OkoaCategoryHot;
import utilities.ConnectivityReceiver;
import utilities.MessageItem;
import utilities.MyApplication;
import utilities.Upload;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        ConnectivityReceiver.ConnectivityReceiverListener,
        RecognitionListener{
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private Boolean proceed;
    private String message;
    private List<Upload> mUploads;
    //Order Specific
    private String hot_cold_option;
    // Firebase vars
    private DatabaseReference mDatabaseRef;
    private DatabaseReference commandsDatabaseRef;
    // vars
    private String TAG = "SEARCH COFFEE PLACES: ACTIVITY";
    private EditText textInputSearch;
    private Context ctx;

    // Location-Permission related vars
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mPermissionGranted = false;


    // Google Maps
    private FusedLocationProviderClient mproviderClient;
    private GoogleMap gMap;
    private MapsServices mapsServices;
    public String place;

    // Speech to text
    // Array of input speech from user
    private List<MessageItem> message_items = new ArrayList<>();
    private final int REQ_CODE_SPEECH_INPUT = 100;

    // AWS Polly vars
    CognitoCachingCredentialsProvider credentialsProvider;
    private List<Voice> voices;
    // Amazon Polly permissions.
    private AmazonPollyPresigningClient client;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    static final int REQUEST_PERMISSION_KEY = 1;
    private ImageButton btn;
    // AWS Media Player
    private MediaPlayer mediaPlayer;
    //AWS Lex tools
    private Dialog helpDialog;
    private Boolean isOkoaOrDoubleshotHotDrink;
    private Boolean isOkoaOrDoubleshotColdDrink;
    private Boolean isHotOrColdQuestionDoubleshot;
    private Boolean isHotOrColdQuestionOkoa;
    // Variables related to interacting with Bruce
    private SpeechProcessorService speechProcessorService;
    private Intent toOkoaHotCategory,toOkoaColdCategory,toDoubleshotHotCategory,
                    toDoubleshotColdCategory;
    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupVoicesList();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady : Map is ready");
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(ctx,R.raw.maps_style);
        gMap = googleMap;
        gMap.setMapStyle(styleOptions);
        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            } });
        if (mPermissionGranted) {
            // Permission granted by the user
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);
            gMap.setBuildingsEnabled(true);
        }

        if(gMap != null){
            gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.activity_view_place, null);
                    TextView placeName = v.findViewById(R.id.place_name);
                    RatingBar ratingBar = v.findViewById(R.id.ratingBar);
                    place = marker.getTitle();
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    final ImageView imageView = v.findViewById(R.id.photo);
                    placeName.setText(marker.getTitle());
                    if(place.contains("Okoa")){
                        String okoa_coffee_place = "Corner of North West Engineering Building, Yale Rd, ";
                        String address = "https://b.zmtcdn.com/data/pictures/6/18276956/313e117fe15fdcc7d54248298062f7b9_featured_v2.jpg";
                        final TextView txtView  = v.findViewById(R.id.place_address);
                        txtView.setText(okoa_coffee_place);
                        ratingBar.setRating(mapsServices.getLocations().get(0).getRating());
                        try {

                            URL url = new URL(address);
                            imageView.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }else {
                        String address = "http://4.bp.blogspot.com/-FMz3ya9WnUc/TzdqPqE3QeI/AAAAAAAAAWw/MhM1pR_vN3g/s1600/double-shot-1.gif";
                        String double_coffee_place = "15 Melle Street, Corner, Juta St, Braamfontein";
                        final TextView txtView  = v.findViewById(R.id.place_address);
                        txtView.setText(double_coffee_place);
                        ratingBar.setRating(mapsServices.getLocations().get(1).getRating());
                        try {

                            URL url = new URL(address);
                            imageView.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    return v;
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG,"onCreate: Activity started");

        init();
        checkConnection();
        initPollyClient();
        setupNewMediaPlayer();

        if(mapsServices.isServiceOK(this)){
            get_permission_location();
        }
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        commandsDatabaseRef = database.getReference("COMMANDS");

        mDatabaseRef = database.getReference("COFFEEPLACES");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mapsServices.process_locations(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        commandsDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                speechProcessorService.initializeAcceptedCommands(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * This method invokes the 'help' dialogue to guide the user on the supported commands
     * @param v
     */
    public void help_tutorial(View v){
        TextView textclose;
        helpDialog.setContentView(R.layout.activity_help_tutorial_maps);
        textclose = (TextView) helpDialog.findViewById(R.id.Xclose);
        textclose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                helpDialog.dismiss();
                animationDrawable.start();
            }
        });
        animationDrawable.stop();
        helpDialog.show();

    }

    /***
     * Internet-related permissions
     */
    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showToast(isConnected);

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
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ctx,"fadein-to-fadeout");
    }
    @Override
    protected void onStop(){
        super.onStop();
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

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showToast(isConnected);
    }
    /**
     *This is for searching for available coffee places
     */
    private void init(){
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }


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

        message= "";
        relativeLayout = findViewById(R.id.relLayoutConvo);
        animationDrawable = (AnimationDrawable)  relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
        toOkoaColdCategory = new Intent(this,OkoaCategoryCold.class);
        toOkoaHotCategory = new Intent(this,OkoaCategoryHot.class);
        toDoubleshotHotCategory = new Intent(this,DoubleshotCategoryHot.class);
        toDoubleshotColdCategory = new Intent(this,DoubleshotCategoryCold.class);
        isHotOrColdQuestionDoubleshot = false;
        isHotOrColdQuestionOkoa = false;
        speechProcessorService = new SpeechProcessorService();        ctx = this;
        btn = findViewById(R.id.btnSpeak);
        mUploads = new ArrayList<>();
        textInputSearch = findViewById(R.id.textInputSearch);
        mapsServices = new MapsServices();
        proceed = false;
        isOkoaOrDoubleshotHotDrink = false;
        isOkoaOrDoubleshotColdDrink = false;
        helpDialog = new Dialog(this);
        Log.d(TAG, "init(): initializing the editor listener");
        textInputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if(actionID == EditorInfo.IME_ACTION_SEARCH
                        || actionID ==  EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    // Execute method for searching for the address
                    geoLocate();
                }
                return false;
            }
        });
        mapsServices.hideSoftKeyboard(this);
    }

    private void geoLocate() {
        Log.d(TAG,"geoLocate(): navigating to location.");
        String lcoation_search = textInputSearch.getText().toString();
        if(!lcoation_search.isEmpty()){
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = new ArrayList<>();
            try {
                Log.d(TAG, "geoLocate(): Could not find location");
                addressList = geocoder.getFromLocationName(lcoation_search,1);

            }catch (IOException e){
                Log.d(TAG, "geoLocate(): Could not find location");
            }
            if(addressList.size() > 0){
                Address address = addressList.get(0);
                Log.d(TAG,"geoLocate(): location found" + address.toString());
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
                mapsServices.moveCamera(this,gMap,new LatLng(address.getLatitude(),address.getLongitude()), DEFAULT_ZOOM,
                        address.getAddressLine(0));
            }
        }
    }

    /**
     * This method supplements the geoLocate() in that it shows on the map nearby Baristas instead of the devices' location
     * @param locations arraylist with locations of nearby baristas
     */
    private void geoLocate(ArrayList<Barista> locations) {
        Integer size = locations.size();
        // Check if nearby locations are available
        if(!locations.isEmpty()){
            for (int i = 0; i < size; i++) {
                String location = locations.get(i).getAddressLine();
                Geocoder geocoder = new Geocoder(this);
                List<Address> addressList = new ArrayList<>();
                try {
                    Log.d(TAG, "geoLocate(): Could not find location : " + location);
                    addressList = geocoder.getFromLocationName(location,2);
                }catch (IOException e){
                    Log.d(TAG, "geoLocate(): Could not find location : " +  location);
                }
                if(addressList.size() > 0){
                    Address address = addressList.get(0);
                    Log.d(TAG,"geoLocate(): location found" + address.toString());
                    //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
                    mapsServices.moveCamera(this,gMap,new LatLng(address.getLatitude(),address.getLongitude()), DEFAULT_ZOOM,
                            locations.get(i).getName());
                }
            }
        }else {
            Toast.makeText(this, "No available coffee places available", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * This method is for getting the device location
     */
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation(): getting the current devices' location");
        mproviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mPermissionGranted){
                Task location = mproviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "getDeviceLocation(): Location found");
                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation != null){
                                mapsServices.moveCamera(ctx,gMap,new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My Location");
                            }else {
                                Toast.makeText(MapsActivity.this,
                                        "Please turn on location services.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.d(TAG, "getDeviceLocation(): Location not found");
                            Toast.makeText(MapsActivity.this,
                                    "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation(): Security Exception: " + e.getMessage());

        }
    }
    /**
     * This method is to initialize the user interface of the Maps API
     */
    private void initialiaze_map(){
        Log.d(TAG, "initialize_map(): initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * This method is for getting user permissions for the device location
     * It keeps a record of the 2 permissions pertaining to a location and then
     * it checks if we have the permission in the current context
     *
     * It will initialize the map if we have permissions and not, otherwise
     */
    public void get_permission_location(){
        Log.d(TAG, "get_permission_location(): Getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            // We have access to the Fine_Location(Precise location)
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                // We have location based on wifi accuracy, cellphone towers
                mPermissionGranted = true;
                initialiaze_map();
            }else {
                ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult(): Request user permission");
        mPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < permissions.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult(): Permission Denied");
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult(): Permission Permission Granted");
                    mPermissionGranted = true;
                    // Initialize the map since we have user permissions
                    initialiaze_map();
                }
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(animationDrawable != null) animationDrawable.stop();
        if (speech != null) {
            speech.destroy();
            Log.d("Log", "destroy");
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("Log", "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("Log", "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("Log", "onEndOfSpeech");
        decodeUserInput(message);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d("Log", "FAILED " + errorMessage);
        //progressBar.setVisibility(View.INVISIBLE);
        //returnedText.setText(errorMessage);
        //recordbtn.setEnabled(true);
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
        text = matches.get(0); //  Remove this line while uncommenting above    codes
        message = text;

        //returnedText.setText(text);
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.d("Log", "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.d("Log", "onResults");

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


    public  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Showing google speech input dialog
     * */
    public void promptSpeechInput(View view) {
        boolean isConnected = ConnectivityReceiver.isConnected();

        if(isConnected){
            /*Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt));
            animationDrawable.stop();
            try {
                //initRecyclerView();
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.speech_not_supported),
                        Toast.LENGTH_SHORT).show();
            }*/
            speech.startListening(recognizerIntent);

        }else {
            showToast(isConnected);
        }

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
                    Log.d(TAG,result.get(0));

                    decodeUserInput(result.get(0));
                }
                break;
            }

        }
    }

    private void decodeUserInput(String userInput) {
        String norm_input = userInput.toLowerCase();

        String okoa_or_doubleshot = "Would you like to place an order from Okoa Or Doubleshot";

        if(isOkoaOrDoubleshotHotDrink){
            // User responded to the Okoa/Doubleshot question
            Log.d(TAG,norm_input);
            if(speechProcessorService.isOkoaRequested(norm_input)){
                setupPlayButton("Hot drinks from Okoa coming up");// Reset boolean checker
                isOkoaOrDoubleshotHotDrink = false;
                startActivity(toOkoaHotCategory);
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else if(speechProcessorService.isDoubleshotRequested(norm_input)){
                setupPlayButton("Doubleshot coming in hot. Get it? Cause you chose hot beverages " +
                        "and I said coming in 'HOT' but the drinks are actually cold?");
                // Reset boolean checker
                isOkoaOrDoubleshotHotDrink = false;
                startActivity(toDoubleshotHotCategory);
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else {
                setupPlayButton("I didn't quite get that");
            }
        }else if(isOkoaOrDoubleshotColdDrink){
            // User responded to the Okoa/Doubleshot question
            if(speechProcessorService.isOkoaRequested(norm_input)){
                setupPlayButton("Cool drinks from Okoa coming up");
                // Reset boolean checker
                isOkoaOrDoubleshotColdDrink = false;
                startActivity(toOkoaColdCategory);
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else if(speechProcessorService.isDoubleshotRequested(norm_input)){
                setupPlayButton("Doubleshot coming in hot. Get it?");
                isOkoaOrDoubleshotColdDrink = false;
                startActivity(toDoubleshotColdCategory);
                // Reset boolean checker
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else {
                setupPlayButton("I didn't quite get that");
            }
        }else if(isHotOrColdQuestionDoubleshot){
            // User requested something from double shot
            // Now we need to check if they ordered something hot or cold
            // And take them to the respective menus
            if(speechProcessorService.isHotBeverageRequired(norm_input)){
                // Take to Hot beverage menu Doubleshot
                isHotOrColdQuestionDoubleshot = false;
                startActivity(toDoubleshotHotCategory);
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else if(speechProcessorService.isColdBeverageRequired(norm_input)){
                // Take to cold doubleshot beverages
                isHotOrColdQuestionDoubleshot = false;
                startActivity(toDoubleshotColdCategory);
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else {
                // Check with user ro specify something hot or cold
                // from doubleshot
                String question = "Would you like something warm or cold" +
                        "from doubleshot?";
                setupPlayButton(question);
            }
        }else if(isHotOrColdQuestionOkoa){
            // User requested something from Okoa
            // Now we need to check if they ordered something hot or cold
            // And take them to the respective menus
            if(speechProcessorService.isHotBeverageRequired(norm_input)){
                // Take to Hot beverage menu Okoa
                isHotOrColdQuestionOkoa = false;
                startActivity(toOkoaHotCategory);
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else if(speechProcessorService.isColdBeverageRequired(norm_input)){
                // Take to Cold beverage menu Okoa
                isHotOrColdQuestionOkoa = false;
                startActivity(toOkoaColdCategory);
                CustomIntent.customType(ctx,"fadein-to-fadeout");
            }else {
                // Check with user ro specify something hot or cold
                // from okoa
                String question = "Would you like something warm or cold" +
                        "from Okoa?";
                Log.i(TAG, speechProcessorService.getRequestHotBeverages().get(1));
                isHotOrColdQuestionOkoa = true;
                setupPlayButton(question);
            }
        }else {
                if(speechProcessorService.isGreeting(norm_input)){
                    setupPlayButton("What would you like today");
                }else if(speechProcessorService.isHelpRequired(norm_input)){
                    setupPlayButton("I'll help you in a second");
                    help_tutorial(mRecyclerView);
                }else if(speechProcessorService.isOkoaHot(norm_input)){
                    // Handle smart order of Hot beverages from Okoa
                    startActivity(toOkoaHotCategory);
                    CustomIntent.customType(ctx,"fadein-to-fadeout");

                }else if(speechProcessorService.isOkoaCold(norm_input)){
                    // Handle smart order of Cold beverages from Okoa
                    startActivity(toOkoaColdCategory);
                    CustomIntent.customType(ctx,"fadein-to-fadeout");
                }else if(speechProcessorService.isDoubleshotHot(norm_input)){
                    // Handle smart order of hot beverages from Doubleshot
                    startActivity(toDoubleshotHotCategory);
                    CustomIntent.customType(ctx,"fadein-to-fadeout");
                }else if(speechProcessorService.isDoubleshotCold(norm_input)){
                    // Handle smart order of cold beverages from Doubleshot
                    startActivity(toDoubleshotColdCategory);
                    CustomIntent.customType(ctx,"fadein-to-fadeout");
                }else if(speechProcessorService.isHotBeverageRequired(norm_input)){
                    setupPlayButton(okoa_or_doubleshot);
                    isOkoaOrDoubleshotHotDrink = true;
                }else if(speechProcessorService.isColdBeverageRequired(norm_input)){
                    setupPlayButton(okoa_or_doubleshot);
                    isOkoaOrDoubleshotColdDrink = true;
                }else if(speechProcessorService.isDoubleshotRequested(norm_input)){
                    setupPlayButton("Would you like something cool or warm from doubleshot?");
                    isHotOrColdQuestionDoubleshot = true;
                }else if(speechProcessorService.isOkoaRequested(norm_input)){
                    setupPlayButton("Would you like a hot or cold beverage from Okoa?");
                    isHotOrColdQuestionOkoa = true;
                }else if(speechProcessorService.isAvailablePlaces(norm_input)){
                    String supported = "I currently support these locations on the map." +
                            "I hope they're near your address";
                    setupPlayButton(supported);
                    geoLocate(mapsServices.getLocations());
                }else {
                    Log.i(TAG,norm_input);
                    setupPlayButton("I didn't quite get that");
                }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(r.menu.main, menu);
        return true;
    }

    public void getDeviceLocation(View view) {
        getDeviceLocation();
    }

    /********************AWS POLLY Text To Speech************************/



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
    private void setupPlayButton(String words){
        MessageItem item = new MessageItem(words);

        message_items.add(item);
        if(voices != null){
            if(words.equals("welcome")){
                String welcome_message = "Hello. I'm Bruce. Your voice-assistant Barista throughout your " +
                        "use of the application. I can help you search for coffee places and view available coffee shops near you";
                // Create speech synthesis request.
                SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                        new SynthesizeSpeechPresignRequest()
                                // Set text to synthesize.
                                .withText(welcome_message)
                                // Set voice selected by the user.
                                .withVoiceId(voices.get(36).getId())
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
                                .withVoiceId(voices.get(36).getId())
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
                    //mediaPlayer = MediaPlayer.create(ctx,R.raw.awsconfiguration);
                    // Set media player's data source to previously obtained URL.
                    mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
                } catch (IOException e) {
                    Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
                }

                // Start the playback asynchronously (since the data source is a network stream).
                mediaPlayer.prepareAsync();
            }

        }else {
            Toast.makeText(ctx, "Unable to get voice response.", Toast.LENGTH_LONG).show();
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
                //setupNewMediaPlayer();
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
