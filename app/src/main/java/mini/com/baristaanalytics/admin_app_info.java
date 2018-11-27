package mini.com.baristaanalytics;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class admin_app_info extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_app_info, container, false);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.button_check_admin);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextFirstName = getActivity().findViewById(R.id.input_first_name_admin);
                String firstName =  editTextFirstName.getText().toString();

                EditText editTextLastName = getActivity().findViewById(R.id.input_last_name_admin);
                String lastName = editTextLastName.getText().toString();

                EditText editTextAge = getActivity().findViewById(R.id.input_age_admin);
                String age = editTextAge.getText().toString();

                EditText editTextPhone = getActivity().findViewById(R.id.input_phone_number_admin);
                // String phone = getActivity().findViewById(R.id.input_phone_number).toString();

                EditText editTextEmail = getActivity().findViewById(R.id.input_email_admin);
                String email = editTextEmail.getText().toString();

                EditText editTextUserName = getActivity().findViewById(R.id.input_username_admin);
                String username = editTextUserName.getText().toString();

                EditText editTextPassword = getActivity().findViewById(R.id.input_password_admin);
                String password = editTextPassword.getText().toString();

                Log.d(ARG_PARAM1, "First Name is: " + firstName);
                Log.d(ARG_PARAM1, "Age is: " + age);
                Log.d(ARG_PARAM1, "Email is: "+ email);
            }
        });

        final ViewPager viewPager = RegisterCustomerActivityNew.mInstance.findViewById(R.id.register_admin_new);
        final int prevFragement = viewPager.getCurrentItem() - 1;

        FloatingActionButton floatingActionButtonBack = view.findViewById(R.id.button_admin_back);
        floatingActionButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(prevFragement);
            }
        });
        return view;
    }
}
