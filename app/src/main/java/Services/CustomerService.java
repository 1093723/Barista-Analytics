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

/**
 * This class is for these customer-specific queries:
 * 1. Register
 * 2. Sign-In
 * 3. Sign-Out
 */
public class CustomerService {
    public Boolean registerCustomer(DatabaseReference databaseCustomer, EditText textFirstName, EditText textLastName,
                                    EditText textAge,
                                    EditText textEmail,
                                    EditText textUsername,
                                    EditText textPassword,
                                    EditText textPhone){
        Boolean flag = false;
        try{
            String firstName = textFirstName.getText().toString().trim();
            String lastName = textLastName.getText().toString().trim();
            String age = textAge.getText().toString().trim();
            String email = textEmail.getText().toString().trim();
            String username = textUsername.getText().toString().trim();
            String password = textPassword.getText().toString().trim();
            String phone = textPhone.getText().toString().trim();
            String id = databaseCustomer.push().getKey();
            Customer user = new Customer(firstName, lastName, age, email, username,
                    password, phone);
            flag = true;
            if(databaseCustomer.child(id).setValue(user).isSuccessful()){
                flag = true;
            }
        }catch (NullPointerException e){
            flag = true;
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
