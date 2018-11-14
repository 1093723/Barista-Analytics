package mini.com.baristaanalytics;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.Okoa.OkoaCoffeeDetails;
import mini.com.baristaanalytics.Registration.RegisterAdminActivity;
import mini.com.baristaanalytics.Registration.RegisterCustomerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);


    }


    public void goToBarista(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void goToCustomer(View view) {
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
        CustomIntent.customType(this,"fadein-to-fadeout");
    }
}
