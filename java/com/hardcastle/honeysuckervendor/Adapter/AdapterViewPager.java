package com.hardcastle.honeysuckervendor.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hardcastle.honeysuckervendor.Fragment.FutureTripsFragment;
import com.hardcastle.honeysuckervendor.Fragment.TodaysTripFragment;

/**
 * Created by admin on 6/13/2016.
 */
public class AdapterViewPager extends FragmentStatePagerAdapter {

    public AdapterViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return new TodaysTripFragment();

            case 1:
                return new FutureTripsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
