package Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import Actors.Customer;
import mini.com.baristaanalytics.LoginActivity;

public class CustomerService {
    //this class id ofr customer specific activities like customer login, sign out, registration
    Context ctx;
    public Boolean registerCustomer(Activity act,Context ctx, FirebaseAuth mAuth, DatabaseReference databaseCustomer, EditText textFirstName, EditText textLastName,
                                    EditText textAge,
                                    EditText textEmail,
                                    EditText textUsername,
                                    EditText textPassword,
                                    EditText textConfirmPassword, EditText textPhone){
        Boolean flag = false;
        String firstName = textFirstName.getText().toString().trim();
        String lastName = textLastName.getText().toString().trim();
        String age = textAge.getText().toString().trim();
        String email = textEmail.getText().toString().trim();
        String username = textUsername.getText().toString().trim();
        String Password = textPassword.getText().toString().trim();
        String confirmPassword = textConfirmPassword.getText().toString().trim();
        String phone = textPhone.getText().toString().trim();
        this.ctx = ctx;
        final Context context = this.ctx;
        final FirebaseAuth auth = mAuth;
        if(TextUtils.isEmpty(firstName)){
            textFirstName.requestFocus();
            Toast.makeText(ctx, "First Name Is Required",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(lastName)){
            textLastName.requestFocus();
            Toast.makeText(ctx, "Last Name Is Required",Toast.LENGTH_LONG).show();
            }
        else if(TextUtils.isEmpty(age)){
            textAge.requestFocus();
            Toast.makeText(ctx, "Age Is Required",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(email)){
            textEmail.requestFocus();
            Toast.makeText(ctx, "Email Is Required",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(username)){
            textUsername.requestFocus();
            Toast.makeText(ctx, "UserName Is Required",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(Password)){
            textPassword.requestFocus();
            Toast.makeText(ctx, "Password Is Required",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(phone)){
            textPhone.requestFocus();
            Toast.makeText(ctx, "Phone No Is Required",Toast.LENGTH_LONG).show();

        }
        else if(TextUtils.isEmpty(confirmPassword)){
            textLastName.requestFocus();
            Toast.makeText(ctx, "Password Field is Required ",Toast.LENGTH_LONG).show();

        }
            if(Password.equals(confirmPassword)){
                String id = databaseCustomer.push().getKey();
                Customer customer = new Customer(firstName, lastName, age, email, username, Password, phone);
                databaseCustomer.child(id).setValue(customer);
                auth.createUserWithEmailAndPassword(email, Password)
                        .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = auth.getCurrentUser();
                                    Toast.makeText(context, "You're now registered",
                                            Toast.LENGTH_LONG).show();
                                    Intent sign_in = new Intent(context,LoginActivity.class);
                                    context.startActivity(sign_in);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                        Toast.makeText(context, "User already exists",
                                                Toast.LENGTH_LONG).show();
                                    }

                                }

                                // ...
                            }
                        });
                //Toast.makeText(ctx, "You are now registered", Toast.LENGTH_LONG).show();
                flag = true;
            }else {
                textConfirmPassword.requestFocus();
                Toast.makeText(ctx, "Passwords do not match",Toast.LENGTH_LONG).show();
            }

        return flag;


    }

    public void signOutCustomer(String username){

    }
    public void signInCustomer(String username){

    }
    public void forgotPasswordCustomer(String username){

    }
}
