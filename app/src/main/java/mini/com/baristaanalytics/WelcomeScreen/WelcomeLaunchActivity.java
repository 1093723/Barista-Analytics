package mini.com.baristaanalytics.WelcomeScreen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.List;

import Database.Database;
import Model.CoffeeOrder;
import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.R;

public class WelcomeLaunchActivity extends AppCompatActivity {

    private ViewPager introVideoViewPager;
    private VideoView videoView;
    private ScalableVideoView scalableVideoView;
    private Context ctx;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video_intro);
        initVariables();
        try {
            scalableVideoView.setRawData(R.raw.background);
            scalableVideoView.setLooping(true);
            scalableVideoView.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    scalableVideoView.start();
                }
            });
        } catch (IOException ioe) {
            // handle error
        }

        // String path = "android.resource://" + getPackageName() + "/" + "/" + R.raw.background;
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background);
        getRecordingPermission();
        deleteFromDatabase();
        //videoView.setVideoURI(uri);
        //videoView.start();
    }

    private void deleteFromDatabase() {
        new Database(this).clearCart();
    }

    private void writeToDatabase() {
        CoffeeOrder coffeeOrder = new CoffeeOrder();
        coffeeOrder.setOrder_Date("date");
        coffeeOrder.setOrder_Store("store");
        coffeeOrder.setOrder_CustomerUsername("uname");
        coffeeOrder.setUUID("uuid");
        coffeeOrder.setOrder_Description("desc");
        coffeeOrder.setOrder_State("state");
        coffeeOrder.setOrder_Rating(Float.valueOf(0));
        coffeeOrder.setOrder_Total(Long.valueOf(250));

        new Database(ctx).addToCart(coffeeOrder);
    }

    private void readFromDatabase() {
        List<CoffeeOrder> coffeeOrderList = new Database(this).getCarts();
    }

    private void getRecordingPermission() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    public void initVariables(){

        scalableVideoView = findViewById(R.id.intro_video);
        ctx = getApplicationContext();
        introVideoViewPager = findViewById(R.id.intro_viewPager);
        introVideoViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

    }

    @Override
    public void finish() {
        super.finish();
        //CustomIntent.customType(ctx,"fadein-to-fadeout");
    }
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos){
            switch (pos){
                case 0: return new welcome_screen_1();
                case 1: return new welcome_screen_2();
                case 2: return new welcome_screen_3();
                default: return new welcome_screen_1();
            }
        }

        @Override
        public int getCount(){
            return 3;
        }
    }
}
