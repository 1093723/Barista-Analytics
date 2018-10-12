package mini.com.baristaanalytics.Registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Services.CustomerService;
import Services.ActorsServiceHelper;
import mini.com.baristaanalytics.LoginActivity;
import mini.com.baristaanalytics.Okoa.OkoaCoffeeDetails;
import mini.com.baristaanalytics.R;

public class RegisterCustomerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Context ctx;
    DatabaseReference databaseCustomer;
    Activity  activity;
    ArrayList<EditText>registrationDetails;
    private String beverageName,beverageDescription,beveragePriceSmall,beveragePriceTall,beverageImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        setValues(intent);
        this.ctx = this;
        this.activity  = this;
        databaseCustomer = FirebaseDatabase.getInstance().getReference("CUSTOMER");
        registrationDetails = new ArrayList<>();
    }

    /**
     * This method is prompted when the 'register' button is clicked on the Activity
     * Get all Edit-texts
     * Validate them
     * Register the user details onto firebase
     * Register user for email-password sign-in
     * @param view
     */
    public void addcustomer(View view) {
        /*********************Prepare fields for validation by Service Helper**********************/
        final EditText textFirstName = (EditText) findViewById(R.id.textFirstName);
        registrationDetails.add(textFirstName);
        final EditText textLastName = (EditText) findViewById(R.id.textLastName);
        registrationDetails.add(textLastName);
        final EditText textAge = (EditText) findViewById(R.id.textAge);
        registrationDetails.add(textAge);
        final EditText textEmail = (EditText) findViewById(R.id.textEmail);
        registrationDetails.add(textEmail);
        final EditText textUsername = (EditText) findViewById(R.id.textUsername);
        registrationDetails.add(textUsername);
        final EditText textPassword = (EditText) findViewById(R.id.textPassword);
        registrationDetails.add(textPassword);
        final EditText textConfirmPassword = (EditText) findViewById(R.id.textConfirmPassword);
        registrationDetails.add(textConfirmPassword);
        final EditText textPhone = (EditText) findViewById(R.id.textPhone);
        registrationDetails.add(textPhone);
        /******************************************************************************************/
        ActorsServiceHelper helper = new ActorsServiceHelper();

        if(helper.validate_edittext(registrationDetails)){
            // No field is left empty
            if(helper.validate_password(textPassword,textConfirmPassword)){
                // Passwords match
                if(helper.validate_phone(textPhone)){
                    // Phone number is valid
                    CustomerService registration = new CustomerService();
                    Boolean check = registration.registerCustomer(databaseCustomer,textFirstName,textLastName,textAge,
                            textEmail,textUsername,textPassword,textPhone);
                    if(check){
                        // The registration was successful using their user details
                        String email = textEmail.getText().toString();
                        String password = textPassword.getText().toString();
                        signUpWithUserNamePassword(email,password);
                    }else {
                        Toast.makeText(ctx, "Please Check Your Connection And Try Again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private boolean signUpWithUserNamePassword(String email, String password) {
        Boolean check = true;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(ctx, "Welcome. You're now registered",
                                    Toast.LENGTH_LONG).show();
                            Intent goToOkoaCoffeeDetails = new Intent(ctx,OkoaCoffeeDetails.class);
                            goToOkoaCoffeeDetails.putExtra("beverage_name", beverageName);
                            goToOkoaCoffeeDetails.putExtra("beverage_description", beverageDescription);
                            goToOkoaCoffeeDetails.putExtra("beverage_image", beverageImage);
                            goToOkoaCoffeeDetails.putExtra("price_small", beveragePriceSmall);
                            goToOkoaCoffeeDetails.putExtra("price_tall", beveragePriceTall);
                            goToOkoaCoffeeDetails.putExtra("orderQuantity", 1);
                            startActivity(goToOkoaCoffeeDetails);

                        } else {
                            // If sign in fails, display a message to the user.
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(ctx, "Admin already exists",
                                        Toast.LENGTH_LONG).show();
                                Intent sign_in = new Intent(ctx,LoginActivity.class);
                                startActivity(sign_in);
                            }
                        }
                    }
                });
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            check = false;
        }
        return check;
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
