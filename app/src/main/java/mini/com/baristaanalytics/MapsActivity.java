package mini.com.baristaanalytics;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final int ERROR_DIALOG_REQUEST = 9001;
    private static final float DEFAULT_ZOOM = 15f;
    public String TAG = "SEARCH COFFEE PLACES: ACTIVITY";
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // vars
    private Boolean mPermissionGranted = false;
    private GoogleMap gMap;
    private FusedLocationProviderClient mproviderClient;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady : Map is ready");
        gMap = googleMap;
        if (mPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_api);
        Log.d(TAG,"onCreate: Activity started");
        if(isServiceOK()){
            get_permission_location();
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
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
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
     * This method recenters the position of the zoom to the devices' currrent location on the map
     * @param latLng (contains the longitude and latitude values)
     * @param zoom
     */
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera(); moving camera to lat: " + latLng.latitude +
                ". Longitude: " + latLng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
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
     * This method checks if the user can access google maps functionality
     * @return boolean True: Maps functionality can be accessed
     *                 False: Maps functionality can't be accessed
     *
     */
    public boolean isServiceOK(){
        Log.d(TAG,"isServiceOK");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available == ConnectionResult.SUCCESS){
            // Nothing is wrong
            Log.d(TAG, "Google Play Services OK");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // Error occured but we can fix it
            Log.d(TAG, "Error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            // Maps API Can't Be Reached
            Toast.makeText(this, "We cant make a map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
