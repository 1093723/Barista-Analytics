package Services;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import Actors.Customer;

public class CustomerService {
    //this class id ofr customer specific activities like customer login, sign out, registration

    public void registerCustomer(Context ctx,DatabaseReference databaseCustomer, EditText textFirstName, EditText textLastName,
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
        String Password = textPassword.getText().toString().trim();
        String confirmPassword = textConfirmPassword.toString().trim();
        String phone = textPhone.getText().toString().trim();

        boolean temp =true;

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
        else if(TextUtils.isEmpty(Password)){
            Toast.makeText(ctx, "Password Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(ctx, "Phone No Is Required",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(ctx, "Password Field is Required ",Toast.LENGTH_LONG).show();
        }
        else if(!TextUtils.isEmpty(confirmPassword)){
            String id = databaseCustomer.push().getKey();

            Customer customer = new Customer(firstName, lastName, age, email, username, Password, confirmPassword, phone);

            databaseCustomer.child(id).setValue(customer);

            Toast.makeText(ctx, "You are now registered", Toast.LENGTH_LONG).show();
        }
    }

    public void signOutCustomer(String username){

    }
    public void signInCustomer(String username){

    }
    public void forgotPasswordCustomer(String username){

    }
}
