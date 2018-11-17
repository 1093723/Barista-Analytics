package mini.com.baristaanalytics;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

public class main_video_intro extends AppCompatActivity {

    private ViewPager introVideoViewPager;
    private VideoView videoView;
    ScalableVideoView scalableVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video_intro);
        scalableVideoView = findViewById(R.id.intro_video);

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
         introVideoViewPager = findViewById(R.id.intro_viewPager);
         introVideoViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //videoView = findViewById(R.id.intro_video);
        /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });*/

        // String path = "android.resource://" + getPackageName() + "/" + "/" + R.raw.background;
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background);


        //videoView.setVideoURI(uri);
        //videoView.start();
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
