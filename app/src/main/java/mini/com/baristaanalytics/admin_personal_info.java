package mini.com.baristaanalytics;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class admin_personal_info extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_personal_info, container, false);
        final ViewPager viewPager = RegisterCustomerActivityNew.mInstance.findViewById(R.id.register_customer_new);
        final int nextFragement = viewPager.getCurrentItem() + 1;
        FloatingActionButton floatingActionButton = view.findViewById(R.id.button_next_customer);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(nextFragement);
            }
        });
        return view;
    }

}
