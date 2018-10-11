package mini.com.baristaanalytics.Okoa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import mini.com.baristaanalytics.R;

public class OkoaCoffeeDetails extends AppCompatActivity {

    private String beverageName,beverageImage,beverageDescription;
    Long beveragePriceSmall,beveragePriceLarge;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okoa_coffee_details);
        // Try get values from the previous activity
        textView = findViewById(R.id.sample);

        Intent coffeeDetails = getIntent();
        setValues(coffeeDetails);
    }

    private void setValues(Intent coffeeDetails) {
        if(coffeeDetails.hasExtra("beverageName")){
            beverageName = coffeeDetails.getStringExtra("beverageName");
        }
        if(coffeeDetails.hasExtra("beverageImage")){
            beverageImage = coffeeDetails.getStringExtra("beverageImage");
        }
        if(coffeeDetails.hasExtra("beverageDescription")){
            beverageDescription = coffeeDetails.getStringExtra("beverageDescription");
        }
        if(coffeeDetails.hasExtra("beveragePriceSmall")){
            beveragePriceSmall = coffeeDetails.getLongExtra("beveragePriceSmall",1);

        }
        if(coffeeDetails.hasExtra("beveragePriceLarge")){
            beveragePriceLarge = coffeeDetails.getLongExtra("beveragePriceLarge",1);
        }
        String x = beverageName + " " + beverageDescription + " "
                + beverageImage + " " + beveragePriceSmall.toString() + " " + beveragePriceLarge.toString();
        textView.setText(x);

    }
}
