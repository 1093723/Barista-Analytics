package Services;

import android.widget.EditText;
import com.google.firebase.database.DatabaseReference;
import Model.Customer;
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
}
