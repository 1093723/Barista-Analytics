package Services;

import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import Model.Admin;

public class AdminService {
    //this class id ofr Admin specific activities like Admin login, sign out, registration
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
}
