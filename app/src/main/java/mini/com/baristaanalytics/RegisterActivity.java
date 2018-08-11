package mini.com.baristaanalytics;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Services.CustomerService;

public class RegisterActivity extends AppCompatActivity {

    Context ctx;
    DatabaseReference databaseCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.ctx = this;

        databaseCustomer = FirebaseDatabase.getInstance().getReference("customer");

    }

    public void addcustomer(View view) {
        final EditText textFirstName = (EditText) findViewById(R.id.textFirstName);
        final EditText textLastName = (EditText) findViewById(R.id.textLastName);
        final EditText textAge = (EditText) findViewById(R.id.textAge);
        final EditText textEmail = (EditText) findViewById(R.id.textEmail);
        final EditText textUsername = (EditText) findViewById(R.id.textUsername);
        final EditText textPassword = (EditText) findViewById(R.id.textPassword);
        final EditText textConfirmPassword = (EditText) findViewById(R.id.textConfirmPassword);
        final EditText textPhone = (EditText) findViewById(R.id.textPhone);

        CustomerService registration = new CustomerService();
        registration.registerCustomer(ctx,databaseCustomer,textFirstName,textLastName,textAge,
                textEmail,textUsername,textPassword,textConfirmPassword,textPhone);
    }

}
