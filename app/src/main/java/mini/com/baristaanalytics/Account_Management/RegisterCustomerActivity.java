package mini.com.baristaanalytics.Account_Management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.List;

import Database.Database;
import Model.CoffeeOrder;
import Services.CustomerService;
import Services.ActorsServiceHelper;
import Services.OrderService;
import mini.com.baristaanalytics.Order.CustomerOrders;
import mini.com.baristaanalytics.R;

public class RegisterCustomerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context ctx;
    private DatabaseReference databaseCustomer,orderReference;
    private FirebaseDatabase database;
    private Activity  activity;
    private ProgressBar mProgressView;
    ArrayList<EditText>registrationDetails;
    private String beverageName,beverageDescription,beveragePriceSmall,beveragePriceTall,beverageImage;
    String from_order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        this.ctx = this;
        mProgressView = findViewById(R.id.signUp_progress);
        Intent intent = getIntent();
        ctx = this;
        database = FirebaseDatabase.getInstance();

        if(intent != null){
            from_order = intent.getStringExtra("coffeePlaceName");
        }
        mAuth = FirebaseAuth.getInstance();
        setValues(intent);
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
        showProgress(true);
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
                        showProgress(false);
                        Toast.makeText(ctx, "Please Check Your Connection And Try Again", Toast.LENGTH_LONG).show();
                    }
                }else {
                    textPhone.setError("Invalid phone number");
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
                            if(from_order != null){
                                if(from_order.contains("Okoa")){
                                    // Submit order to the okoa database
                                    orderReference = database.getReference("OkoaCoffeeOrders");
                                    submitOrder();
                                    Intent intent = new Intent(ctx,CustomerOrders.class);
                                    startActivity(intent);
                                }else {
                                    // Take to doubleshot
                                    orderReference = database.getReference("DoubleshotCoffeeOrders");
                                    submitOrder();
                                    Intent intent = new Intent(ctx,CustomerOrders.class);
                                    startActivity(intent);
                                }
                                Toast.makeText(ctx, from_order, Toast.LENGTH_SHORT).show();
                            }else {
                                Intent intent = new Intent(ctx,UserProfileActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            showProgress(false);
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

    private void submitOrder() {
        Database database = new Database(ctx);

        List<CoffeeOrder> coffeeOrders = new Database(this).getCarts();
        OrderService orderService = new OrderService();
        coffeeOrders.get(0).setUUID(mAuth.getUid());
        String[] uName = mAuth.getCurrentUser().getEmail().split("@");
        coffeeOrders.get(0).setOrder_CustomerUsername(uName[0]);

        orderService.process_order(coffeeOrders.get(0),orderReference);

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

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);



            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
