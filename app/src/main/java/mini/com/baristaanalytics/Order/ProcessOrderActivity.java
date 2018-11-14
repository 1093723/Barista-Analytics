package mini.com.baristaanalytics.Order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.net.URISyntaxException;

import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.R;

public class ProcessOrderActivity extends AppCompatActivity{
    private String TAG = "ProcessOrderActivity";
    private Context ctx;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_orders);
        Log.d(TAG,"onCreate: Activity started");
        ctx = this;
        try {
            Intent place  = Intent.getIntentOld("MapsActivity");
            String x = place.getStringExtra("Okoa");
            Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ctx,"fadein-to-fadeout");
    }
}
