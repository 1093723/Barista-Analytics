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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okoa_coffee_details);

        //Create new intent with details of coffee
        Intent intent = getIntent();

        // need this for getting a user to register or
        // check that the user is logged in so that we can process the order
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String intent_beverage_name = intent.getStringExtra("beverage_name");
        String intent_beverage_description = intent.getStringExtra("beverage_description");
        String intent_beverage_image = intent.getStringExtra("beverage_image");
        // String intent_beverage_category = intent.getStringExtra("beverage_category");
        String intent_price_small = intent.getStringExtra("price_small");
        // String intent_price_medium = intent.getStringExtra("price_medium");
        String intent_price_tall = intent.getStringExtra("price_tall");

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
                if(firebaseAuth.getCurrentUser().getUid().isEmpty()) {
                    Intent registrationPage = new Intent(OkoaCoffeeDetails.this, RegisterCustomerActivity.class);
                    startActivity(registrationPage);
                }
                else {
                    // Bruce stuff, that is, needs to confirm the order
                }

            }
        });

        // Load the stuff into the new activity
        Picasso.with(getBaseContext()).load(Uri.parse(intent_beverage_image)).into(imageView);
        beverage_name_tall.setText(intent_beverage_name + " - Tall");
        beverage_name_small.setText(intent_beverage_name + " - Small");
        beverage_price_small.setText(intent_price_small);
        beverage_price_tall.setText(intent_price_tall);
        beverage_description.setText(intent_beverage_description);




    }
}
