package com.dyszlewskiR.edu.scientling.presentation.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyszlewskiR.edu.scientling.R;

import java.util.ArrayList;
import java.util.List;

public class ProgressDetailsFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public ProgressDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_details, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        mTabLayout.setupWithViewPager(mViewPager);
        setupViewPager(mViewPager);
        return view;
    }

    private final int LESSONS_RESOURCE = R.string.lessons_progress;
    private final int DIFFICULTY_RESOURCE = R.string.difficulty_progress;
    private final int CATEGORY_RESOURCE = R.string.category_progress;

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        //TODO
        adapter.addFragment(new LessonsProgressFragment(), getString(LESSONS_RESOURCE));
        adapter.addFragment(new DifficultyProgressFragment(), getString(DIFFICULTY_RESOURCE));
        adapter.addFragment(new CategoriesProgressFragment(),getString(CATEGORY_RESOURCE));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentsList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position){
            return mFragmentsList.get(position);
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentsList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getCount() {
            return mFragmentsList.size();
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }
}
