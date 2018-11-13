package Services;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import Model.Barista;
import mini.com.baristaanalytics.MapsActivity;

public class MapsServices {
    private ArrayList<Barista> locations;
    private String TAG = "SEARCH COFFEE PLACES: ACTIVITY";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public String getTAG() {
        return TAG;
    }

    public ArrayList<Barista> getLocations() {
        return locations;
    }

    public MapsServices(){
        this.locations = new ArrayList<>();
    }

    /**
     * This method retrieves all coffee locations from the database to add to the maps UI
     * whenever user requests for every available coffee place
     * @param dataSnapshot
     */
    public void process_locations(DataSnapshot dataSnapshot) {
        Integer id = 1;
        for(DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals(id.toString())){
                Barista barista = ds.getValue(Barista.class);
                this.locations.add(barista);
                Log.i(TAG,barista.getRating().toString());
            }
            id+=1;
        }
    }

    /**
     * This method checks if the user can access google maps functionality
     * @return boolean True: Maps functionality can be accessed
     *                 False: Maps functionality can't be accessed
     *
     */
    public boolean isServiceOK(Context ctx){
        Log.d(getTAG(),"isServiceOK");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx);
        if(available == ConnectionResult.SUCCESS){
            // Nothing is wrong
            Log.d(getTAG(), "Google Play Services OK");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // Error occured but we can fix it
            Log.d(getTAG(), "Error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((MapsActivity)ctx, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            // Maps API Can't Be Reached
            Toast.makeText(ctx, "We cant make a map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     *Hide the keyboard from the user
     */
    public void hideSoftKeyboard(Context ctx){
        MapsActivity mapsActivity = (MapsActivity)ctx;
        mapsActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * This method recenters the position of the zoom to the devices' currrent location on the map
     * @param latLng (contains the longitude and latitude values)
     * @param zoom
     */
    public void moveCamera(Context ctx,GoogleMap map, LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera(); moving camera to lat: " + latLng.latitude +
                ". Longitude: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            map.addMarker(options);
        }
        this.hideSoftKeyboard(ctx);
    }
}
