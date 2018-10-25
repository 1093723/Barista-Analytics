package mini.com.baristaanalytics.Order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mini.com.baristaanalytics.R;

public class History_Orders_Fragment extends Fragment {
    private static final String TAG = "ORDER_HISTORY_FRAGMENT";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history,container,false);
        return view;
    }
}
