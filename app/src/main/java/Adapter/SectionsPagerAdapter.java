package Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprised of 2 'sections'
 * 1. Order History Fragment
 * 2. Current Orders Fragment
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter{
    private static String TAG = "SectionsPagerAdapter";
    private List<Fragment> mFragmentsList = new ArrayList<>();
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentsList.size();
    }

    public void addFragments(Fragment fragment){
        mFragmentsList.add(fragment);
    }
}
