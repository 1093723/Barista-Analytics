package mini.com.baristaanalytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import Services.AdminService;

public class RegisterAdminActivity extends AppCompatActivity {

    Context ctx;
    Activity activity;
    private FirebaseAuth mAuth;
    DatabaseReference databaseAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);
        this.ctx = this;
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        databaseAdmin = FirebaseDatabase.getInstance().getReference("ADMIN");
    }

    public void addAdministrator(View view) {
        final EditText textFirstName = (EditText) findViewById(R.id.textFirstName);
        final EditText textLastName = (EditText) findViewById(R.id.textLastName);
        final EditText textAge = (EditText) findViewById(R.id.textAge);
        final EditText textEmail = (EditText) findViewById(R.id.textEmail);
        final EditText textUsername = (EditText) findViewById(R.id.textUsername);
        final EditText textPassword = (EditText) findViewById(R.id.textPassword);
        final EditText textConfirmPassword = (EditText) findViewById(R.id.textConfirmPassword);
        final EditText textPhone = (EditText) findViewById(R.id.textPhone);
        AdminService registration = new AdminService();
        Boolean check = registration.registerAdmin(activity,ctx, mAuth,databaseAdmin,textFirstName,textLastName,textAge,
                textEmail,textUsername,textPassword,textConfirmPassword,textPhone);
        if(check){
            String email = textEmail.getText().toString();
            String password = textPassword.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(ctx, "You're now registered",
                                        Toast.LENGTH_LONG).show();
                                Intent sign_in = new Intent(ctx,LoginActivity.class);
                                startActivity(sign_in);

                            } else {
                                // If sign in fails, display a message to the user.
                                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(ctx, "User already exists",
                                            Toast.LENGTH_LONG).show();
                                }

                            }

                            // ...
                        }
                    });
        }
    }
}
