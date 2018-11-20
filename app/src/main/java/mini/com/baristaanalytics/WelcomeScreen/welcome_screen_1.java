package mini.com.baristaanalytics.WelcomeScreen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mini.com.baristaanalytics.R;


public class welcome_screen_1 extends Fragment {
    private static final String ARG_PARAM1 = "WELCOME_SCREEN_FRAGMENT_1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_screen_1, container, false);
    }
}