package mini.com.baristaanalytics;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import Actors.Barista;
import Services.MapsServices;
import Utilities.MessageItem;

import static android.content.ContentValues.TAG;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{
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

    // Firebase
    private DatabaseReference ref;

    // Google Maps
    private FusedLocationProviderClient mproviderClient;
    private GoogleMap gMap;
    private MapsServices mapsServices;

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

    // AWS Media Player
    MediaPlayer mediaPlayer;
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupVoicesList();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady : Map is ready");
        gMap = googleMap;
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
                    TextView placeAddress = v.findViewById(R.id.place_address);
                    RatingBar ratingBar = v.findViewById(R.id.ratingBar);
                    ImageView imageView = v.findViewById(R.id.photo);
                    Button button = v.findViewById(R.id.back_to_results);

                    placeName.setText(marker.getTitle());
                    placeAddress.setText("Latitude : " + marker.getPosition().latitude +
                            ". Longitude" + marker.getPosition().longitude);

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
        ctx = this;
        textInputSearch = findViewById(R.id.textInputSearch);
        mapsServices = new MapsServices();
        initPollyClient();
        setupNewMediaPlayer();
        init();
        if(mapsServices.isServiceOK(this)){
            get_permission_location();
        }
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mapsServices.process_locations(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    /**
     *This is for searching for available coffee places
     */
    private void init(){
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
                    Log.d(TAG, "geoLocate(): Could not find location");
                    addressList = geocoder.getFromLocationName(location,1);
                }catch (IOException e){
                    Log.d(TAG, "geoLocate(): Could not find location");
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
                            mapsServices.moveCamera(ctx,gMap,new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
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
    /**
     * Showing google speech input dialog
     * */
    public void promptSpeechInput(View view) {
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
                    if((result.get(0).contains("registration") ||
                            result.get(0).contains("register")) &&
                            (result.get(0).contains("user") || result.get(0).contains("customer"))){
                        String toSpeak = "Proceeding to user registration";
                        setupPlayButton(toSpeak);
                        Intent x = new Intent(this, RegisterCustomerActivity.class);
                        startActivity(x);
                    }else if((result.get(0).contains("registration") ||
                            result.get(0).contains("register")) &&
                            (result.get(0).contains("admin") || result.get(0).contains("administrator"))){
                        Intent x = new Intent(this, RegisterAdminActivity.class);
                        String toSpeak = "Proceeding to administrator registration";
                        setupPlayButton(toSpeak);
                        startActivity(x);
                    }
                }
                break;
            }

        }
    }

    private void decodeUserInput(String s) {
        if(s.contains("show") || s.contains("available")){
            String toSpeak = "I currently support these locations on the map. I hope they are " +
                    "near your address";
            setupPlayButton(toSpeak);
            geoLocate(mapsServices.getLocations());
        }else {
            Toast.makeText(this, "Command Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
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
