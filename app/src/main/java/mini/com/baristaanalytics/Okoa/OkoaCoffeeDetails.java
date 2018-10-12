package mini.com.baristaanalytics.Okoa;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import mini.com.baristaanalytics.R;
import mini.com.baristaanalytics.Registration.RegisterCustomerActivity;

public class OkoaCoffeeDetails extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String beverageName,beverageDescription,beveragePriceSmall,beveragePriceTall,beverageImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okoa_coffee_details);

        //Create new intent with details of coffee
        Intent intent = getIntent();
        setValues(intent);
        // need this for getting a user to register or
        // check that the user is logged in so that we can process the order
        mAuth = FirebaseAuth.getInstance();

        ImageView imageView = findViewById(R.id.app_bar_coffee_image);
        TextView beverage_name_small = findViewById(R.id.beverage_name_small);
        TextView beverage_name_tall = findViewById(R.id.beverage_name_tall);
        TextView beverage_price_small = findViewById(R.id.beverage_price_small);
        TextView beverage_price_tall = findViewById(R.id.beverage_price_large);
        TextView beverage_description = findViewById(R.id.beverage_description);

        FloatingActionButton buttonCart = findViewById(R.id.btnCart);
        /*FloatingActionButton needs to either:
            1. Send the user to the reigster activity so that they can register.
                After registering, they need to login so we know that we get the UID

            2. Confirm the order details in the OkoaDetailConfirmed activity (not created)
                After that, send them to the OrderConfirmed page.
         */
        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth != null){
                    if(mAuth.getCurrentUser() != null) {
                    // Bruce stuff, that is, needs to confirm the order

                    }else {
                        Intent registrationPage = new Intent(OkoaCoffeeDetails.this, RegisterCustomerActivity.class);
                        registrationPage.putExtra("beverage_name", beverageName);
                        registrationPage.putExtra("beverage_description", beverageDescription);
                        registrationPage.putExtra("beverage_image", beverageImage);
                        registrationPage.putExtra("price_small", beveragePriceSmall);
                        registrationPage.putExtra("price_tall", beveragePriceTall);
                        registrationPage.putExtra("orderQuantity", 1);
                        startActivity(registrationPage);
                    }
                }
                else {
                    Intent registrationPage = new Intent(OkoaCoffeeDetails.this, RegisterCustomerActivity.class);
                    startActivity(registrationPage);
                }
                }
        });

        // Load the stuff into the new activity
        Picasso.with(getBaseContext()).load(Uri.parse(beverageImage)).into(imageView);
        beverage_name_tall.setText(beverageName + " - Tall");
        beverage_name_small.setText(beverageName + " - Small");
        beverage_price_small.setText(beveragePriceSmall);
        beverage_price_tall.setText(beveragePriceTall);
        beverage_description.setText(beverageDescription);
    }

    private void setValues(Intent intent) {
        beverageName = intent.getStringExtra("beverage_name");
        beverageDescription = intent.getStringExtra("beverage_description");
        beverageImage = intent.getStringExtra("beverage_image");
        // String intent_beverage_category = intent.getStringExtra("beverage_category");
        beveragePriceSmall = intent.getStringExtra("price_small");
        // String intent_price_medium = intent.getStringExtra("price_medium");
        beveragePriceTall = intent.getStringExtra("price_tall");
    }
}
