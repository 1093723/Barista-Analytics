package mini.com.baristaanalytics;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegisterCustomerActivityNew extends AppCompatActivity {

    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer_new);

        viewPager = findViewById(R.id.register_customer_new);
        viewPager.setAdapter(new RegisterCustomerAdapter(getSupportFragmentManager()));
    }

    private class RegisterCustomerAdapter extends FragmentPagerAdapter {
        public RegisterCustomerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos){
            switch (pos){
                case 0: return new customer_personal_info();
                case 1: return new customer_app_info();
                default: return new customer_personal_info();
            }
        }

        @Override
        public int getCount(){
            return 2;
        }
    }
}
