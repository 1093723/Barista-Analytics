package mini.com.baristaanalytics;

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
import Services.ServiceHelper;

public class RegisterCustomerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Context ctx;
    DatabaseReference databaseCustomer;
    Activity  activity;
    ArrayList<EditText>registrationDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        mAuth = FirebaseAuth.getInstance();
        this.ctx = this;
        this.activity  = this;
        databaseCustomer = FirebaseDatabase.getInstance().getReference("CUSTOMER");
        registrationDetails = new ArrayList<>();
    }

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
        ServiceHelper helper = new ServiceHelper();

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
                            Intent sign_in = new Intent(ctx,LoginActivity.class);
                            startActivity(sign_in);

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

}
