package mini.com.baristaanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.Account.LoginActivity;

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
