package mini.com.baristaanalytics.Order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import Adapter.SectionsPagerAdapter;
import mini.com.baristaanalytics.R;

public class CustomerOrders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);
        setupViewPager();
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter  = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragments(new CustomerOrderFragment());
        //adapter.addFragments(new MessagesFragment());
        //adapter.addFragments(new OrderHistoryFragment());
        ViewPager viewPager = (ViewPager)findViewById(R.id.container);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_current_orders);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_converation);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_pervious_orders);
    }
}
