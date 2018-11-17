package mini.com.baristaanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class welcome_screen_3 extends Fragment {
    private static final String ARG_PARAM1 = "WELCOME_SCREEN_FRAGMENT_3";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome_screen_3, container, false);

        // The button moves you to change password activity. Not part of the logic, just for debugging.
        // Remove the button if you are satisfied with the functionality
        Button button = (Button)view.findViewById(R.id.button_get_started);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), user_profile.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    public void toNextMenu(View view){
        Intent intent = new Intent(getActivity(), user_profile.class);
        startActivity(intent);
    }
}
