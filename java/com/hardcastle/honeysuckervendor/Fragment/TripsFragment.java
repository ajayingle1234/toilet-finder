package com.hardcastle.honeysuckervendor.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hardcastle.honeysuckervendor.Adapter.AdapterViewPager;
import com.hardcastle.honeysuckervendor.R;

public class TripsFragment extends Fragment {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AdapterViewPager mAdapterViewPager;

    public TripsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TripsFragment newInstance() {
        TripsFragment fragment = new TripsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        try {
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            //setSupportActionBar(mToolbar);
            mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
            mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mAdapterViewPager = new AdapterViewPager(getFragmentManager());
            mViewPager.setAdapter(mAdapterViewPager);

            // adding functionality to tab and viewpager to manage each other when a page is changed or when a tab is selected
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

            final TabLayout.Tab tabTodaysTrip = mTabLayout.newTab();
            final TabLayout.Tab tabFutureTrip = mTabLayout.newTab();

            tabTodaysTrip.setText(getResources().getString(R.string.todays_trip));
            tabFutureTrip.setText(getResources().getString(R.string.future_trip));

            mTabLayout.addTab(tabTodaysTrip, 0);
            mTabLayout.addTab(tabFutureTrip,1);

            mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    //mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mToolbar.setTitle(getResources().getString(R.string.trips));
    }

}
