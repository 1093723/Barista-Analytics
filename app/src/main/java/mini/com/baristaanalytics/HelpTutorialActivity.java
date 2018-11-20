package mini.com.baristaanalytics;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import maes.tech.intentanim.CustomIntent;

public class HelpTutorialActivity extends AppCompatActivity {
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_tutorial_maps);
        ctx = this;
    }

}
