package mini.com.baristaanalytics.Account_Management;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mini.com.baristaanalytics.Account_Management.PasswordEditActivity;
import mini.com.baristaanalytics.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }

    public void backToMenu(View view){
        finish();
    }

    public void editPassword(View view){
        Intent intent = new Intent(this, PasswordEditActivity.class);
        startActivity(intent);
    }

}
