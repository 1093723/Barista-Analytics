package mini.com.baristaanalytics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class user_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }

    public void backToMenu(View view){
        finish();
    }

    public void editPassword(View view){
        Intent intent = new Intent(this, user_password_edit.class);
        startActivity(intent);
    }

}
