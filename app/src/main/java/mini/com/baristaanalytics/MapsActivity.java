package mini.com.baristaanalytics;

import android.app.Dialog;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public String TAG = "SEARCH COFFEE PLACES: ACTIVITY";
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // vars
    private Boolean mPermissionGranted = false;
    private GoogleMap gMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady : Map is ready");
        gMap = googleMap;
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
     * This method is to initialize the user interface of the Maps API
     */
    private void initialiaze_map(){
        Log.d(TAG, "initialize_map(): initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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
