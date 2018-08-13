package mini.com.baristaanalytics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        TextView temp = (TextView)findViewById(R.id.txtDisplayName);
        temp.setText("Welcome " + mAuth.getCurrentUser().getEmail().toString());

    }
}
