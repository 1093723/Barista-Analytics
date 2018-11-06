package mini.com.baristaanalytics.Order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mini.com.baristaanalytics.R;

public class CurrentOrdersFragment extends Fragment {
    private static final String TAG = "CURRENT_ORDERS_FRAGMENT";
    FirebaseDatabase database;
    DatabaseReference current_orders;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_current_order,container,false);

        database = FirebaseDatabase.getInstance();
        current_orders = database.getReference("CoffeeMenuOkoa");
        return view;
    }
}
