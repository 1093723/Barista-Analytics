package mini.com.baristaanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.net.URISyntaxException;

public class ProcessOrderActivity extends AppCompatActivity{
    public String TAG = "ProcessOrderActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_orders);
        Log.d(TAG,"onCreate: Activity started");
        try {
            Intent place  = Intent.getIntentOld("MapsActivity");
            String x = place.getStringExtra("Okoa");
            Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
