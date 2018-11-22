package mini.com.baristaanalytics.WelcomeScreen;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

import Database.Database;
import Model.CoffeeOrder;
import maes.tech.intentanim.CustomIntent;
import mini.com.baristaanalytics.R;

public class WelcomeLaunchActivity extends AppCompatActivity {

    private ViewPager introVideoViewPager;
    private VideoView videoView;
    private ScalableVideoView scalableVideoView;
    private Context ctx;
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


        //videoView.setVideoURI(uri);
        //videoView.start();
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
