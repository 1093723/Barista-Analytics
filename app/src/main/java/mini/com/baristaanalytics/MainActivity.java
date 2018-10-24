package mini.com.baristaanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mini.com.baristaanalytics.Okoa.OkoaCoffeeDetails;
import mini.com.baristaanalytics.Registration.RegisterAdminActivity;
import mini.com.baristaanalytics.Registration.RegisterCustomerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void registerClickFunction(View view) {
        Intent x = new Intent(this, RegisterCustomerActivity.class );
        startActivity(x);
    }
    public void signInFunction(View view) {
        Intent x = new Intent(this, LoginActivity.class );
        startActivity(x);
    }
    public void registerVendor(View view){
        Intent x = new Intent(this, RegisterAdminActivity.class);
        startActivity(x);
    }

    public void SpeechToTxt(View view) {
        Intent x = new Intent(this, SpeechAPI.class);
        startActivity(x);
    }

    public void showMeMaps(View view) {
        Intent x = new Intent(this, MapsActivity.class);
        startActivity(x);
    }

    public void ViewImages(View view) {
        Intent x = new Intent(this,MapsActivity.class);
        startActivity(x);
    }

    public void goToOkoaDetails(View view) {
        Intent x  =new Intent(this, OkoaCoffeeDetails.class);
        startActivity(x);
    }
}
