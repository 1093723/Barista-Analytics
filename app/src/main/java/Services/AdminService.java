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

import Actors.Admin;
import mini.com.baristaanalytics.LoginActivity;

public class AdminService {
    //this class id ofr Admin specific activities like Admin login, sign out, registration
    private String superAdminTAG = "SAD-";
    private String adminTAG = "AD-";
    private String adminRole = "ADMIN";
    private Context ctx;

    public Boolean registerAdmin(Activity act, Context ctx, FirebaseAuth mAuth, DatabaseReference databaseAdmin, EditText textFirstName, EditText textLastName,
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
        String confirmPassword = textConfirmPassword.toString().trim();
        String phone = textPhone.getText().toString().trim();
        this.ctx = ctx;
        Boolean flag = false;

        final Context context = this.ctx;
        final FirebaseAuth auth = mAuth;

        if(TextUtils.isEmpty(firstName)){
            Toast.makeText(ctx, "First Name Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(lastName)){
            Toast.makeText(ctx, "Last Name Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(age)){
            Toast.makeText(ctx, "Age Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(ctx, "Email Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(username)){
            Toast.makeText(ctx, "UserName Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(ctx, "Password Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(ctx, "Phone No Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(ctx, "Password Field is Required ",Toast.LENGTH_LONG).show();
        }
        if(password.equals(confirmPassword)){
            String id = databaseAdmin.push().getKey();
            username = adminTAG.concat(username);
            Admin user = new Admin(firstName, lastName, age, email, username, password, phone);
            flag = true;
            databaseAdmin.child(id).setValue(user);
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
