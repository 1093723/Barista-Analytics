package mini.com.baristaanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
<<<<<<< HEAD

=======
>>>>>>> 4e316861ced1803630dfae3c817a39d2c20bdb01
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
<<<<<<< HEAD

    public void registerClickFunction(View view) {
        Intent x = new Intent(this, RegisterActivity.class );
        startActivity(x);
=======
    public void registerClickFunction(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
>>>>>>> 4e316861ced1803630dfae3c817a39d2c20bdb01
    }
    public void signInFunction(View v){
        Intent x = new Intent(this,LoginActivity.class);
        startActivity(x);
    }
}
