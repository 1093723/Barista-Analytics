package mini.com.baristaanalytics.Order;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mini.com.baristaanalytics.R;

public class CustomerOrderFragment extends android.support.v4.app.Fragment{
    private static final String TAG = "CUSTOMER_ORDERS_FRAGMENT";
    FirebaseDatabase database;
    DatabaseReference current_orders;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_orders,container,false);
        database = FirebaseDatabase.getInstance();
        current_orders = database.getReference("CoffeeMenuOkoa");
        return view;
    }
}
