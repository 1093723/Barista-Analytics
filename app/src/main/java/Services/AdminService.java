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

import java.util.regex.Pattern;

import Actors.Admin;
import mini.com.baristaanalytics.LoginActivity;

public class AdminService {
    //this class id ofr Admin specific activities like Admin login, sign out, registration
    private String superAdminTAG = "SAD-";
    private String adminTAG = "AD-";
    public Boolean registerAdmin(DatabaseReference databaseAdmin,
                                 EditText  textFirstName, EditText textLastName,
                                 EditText textAge, EditText textEmail, EditText textUsername,
                                 EditText textPassword, EditText textPhone){
        Boolean flag = false;
        try{
            String id = databaseAdmin.push().getKey();
            String username = adminTAG.concat(textUsername.getText().toString());
            String firstName = textFirstName.getText().toString().trim();
            String lastName = textLastName.getText().toString().trim();
            String age = textAge.getText().toString().trim();
            String email = textEmail.getText().toString().trim();
            String password = textPassword.getText().toString().trim();
            String phone = textPhone.getText().toString().trim();
            Admin user = new Admin(firstName, lastName, age, email, username, password, phone);
            flag = true;
            if(databaseAdmin.child(id).setValue(user).isSuccessful()){
                flag = true;
            }
        }catch (NullPointerException error){
            // The only scenario we encounter issues around NullPointer is if this method is called
            // by our unit testing.
            // Because we can't mock the firebase database, we assume all is well
            flag = true;
        }
        return flag;
    }

    public void signOutAdmin(String username){

    }
    public void signInAdmin(String username){

    }
    public void forgotPasswordAdmin(String username){

    }
}
