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
        String firstName = textFirstName.getText().toString().trim();
        String lastName = textLastName.getText().toString().trim();
        String age = textAge.getText().toString().trim();
        String email = textEmail.getText().toString().trim();
        String username = textUsername.getText().toString().trim();
        String password = textPassword.getText().toString().trim();
        String confirmPassword = textConfirmPassword.getText().toString().trim();
        String phone = textPhone.getText().toString().trim();
        this.ctx = ctx;
        Boolean flag = false;

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
        else if(TextUtils.isEmpty(password)){
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
        if(password.equals(confirmPassword)){
            String id = databaseCustomer.push().getKey();
            Customer user = new Customer(firstName, lastName, age, email, username,
                    password, phone);
            databaseCustomer.child(id).setValue(user);

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
