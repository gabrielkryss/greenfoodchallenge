package com.ecoone.mindfulmealplanner.pledge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecoone.mindfulmealplanner.R;

import java.util.ArrayList;
import java.util.List;


public class PledgeFragment extends Fragment {

    public PledgeFragment() {
        // Required empty public constructor
    }

    public static PledgeFragment newInstance() {

        Bundle args = new Bundle();

        PledgeFragment fragment = new PledgeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pledge, container, false );

        ViewPager viewPager = rootView.findViewById(R.id.pledge_viewpager);
        setupViewPager(viewPager);

        TabLayout myTabs = rootView.findViewById(R.id.tab_bar);
        myTabs.setupWithViewPager(viewPager);

//        ViewGroup vg = (ViewGroup) myTabs.getChildAt(0);
//
//        int tabsCount = vg.getChildCount();
//        for (int j = 0; j < tabsCount; j++) {
//            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
//            int tabChildsCount = vgTab.getChildCount();
//            for (int i = 0; i < tabChildsCount; i++) {
//                View tabViewChild = vgTab.getChildAt(i);
//                if (tabViewChild instanceof TextView) {
//                    Log.i("testActivity", "test: " + ((TextView) tabViewChild).getTextSize());
//                    Log.i("testActivity", "test: " + ((TextView) tabViewChild).getTypeface());
//                }
//            }
//        }


        /*
        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("mypledge").setIndicator("My Pledge"),
                MyPledgeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("discover").setIndicator("Discover"),
                DiscoverFragment.class, null);
        */
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter myAdapter = new Adapter(getChildFragmentManager());
        myAdapter.addFragment(new MyPledgeFragment(), "My Pledge");
        myAdapter.addFragment(new DiscoverFragment(), "Discover");
        viewPager.setAdapter(myAdapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
