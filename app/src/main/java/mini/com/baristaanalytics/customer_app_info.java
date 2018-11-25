package mini.com.baristaanalytics;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class customer_app_info extends Fragment {
    private static final String ARG_PARAM1 = "CUST_REGISTER_INFO_FRAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_app_info, container, false);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.button_customer_check);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextFirstName = getActivity().findViewById(R.id.input_first_name_customer);
                String firstName =  editTextFirstName.getText().toString();

                EditText editTextLastName = getActivity().findViewById(R.id.input_last_name_customer);
                String lastName = editTextLastName.getText().toString();

                EditText editTextAge = getActivity().findViewById(R.id.input_age_customer);
                String age = editTextAge.getText().toString();

                EditText editTextPhone = getActivity().findViewById(R.id.input_phone_number_customer);
                // String phone = getActivity().findViewById(R.id.input_phone_number).toString();

                EditText editTextEmail = getActivity().findViewById(R.id.input_customer_email);
                String email = editTextEmail.getText().toString();

                EditText editTextUserName = getActivity().findViewById(R.id.input_customer_username);
                String username = editTextUserName.getText().toString();

                EditText editTextPassword = getActivity().findViewById(R.id.input_customer_password);
                String password = editTextPassword.getText().toString();

                Log.d(ARG_PARAM1, "First Name is: " + firstName);
                Log.d(ARG_PARAM1, "Age is: " + age);
                Log.d(ARG_PARAM1, "Email is: "+ email);
            }
        });
        return view;
    }

    /*@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String firstName = getActivity().findViewById(R.id.input_first_name).toString();
        String lastName = getActivity().findViewById(R.id.input_last_name).toString();
        String age = getActivity().findViewById(R.id.input_age).toString();
        String phone = getActivity().findViewById(R.id.input_phone_number).toString();

        String email = getActivity().findViewById(R.id.input_customer_email).toString();
        String username = getActivity().findViewById(R.id.input_customer_username).toString();
        String password = getActivity().findViewById(R.id.input_customer_password).toString();

        Log.d(ARG_PARAM1, "First Name is: " + firstName);
        Log.d(ARG_PARAM1, "Age is: " + age);
        Log.d(ARG_PARAM1, "Email is: "+ email);



    }*/
}
