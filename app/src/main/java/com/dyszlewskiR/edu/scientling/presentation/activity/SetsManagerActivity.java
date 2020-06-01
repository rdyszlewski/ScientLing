package com.dyszlewskiR.edu.scientling.presentation.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.fragment.SetsManagerFragment;
import com.dyszlewskiR.edu.scientling.presentation.fragment.SetsServerFragment;
import com.dyszlewskiR.edu.scientling.service.preferences.LogPref;

public class SetsManagerActivity extends AppCompatActivity {

    private final int TAB_ONE = R.string.sets_tab;
    private final int TAB_TWO = R.string.server_tab;



    private TabLayout mTabLayout;
    //private NonSwipeableViewPager mViewPager;
    private ViewGroup mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets_manager);
        setupToolbar();
        setupControls();
        setPagerAdapter();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    private void setupControls(){
        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(TAB_ONE)));
        //jeżeli użytkownik jest zalogowany zostanie wyświetlohna zakładka Wysłane
        //jeżeli użytkonik nie jest zalogowany ukrywany jest cały TabLayout, zeby nie wyświetlać niepotrzebnie jednej zakładki
        if(LogPref.getLogin(getBaseContext())!= null){
            mTabLayout.addTab(mTabLayout.newTab().setText(getString(TAB_TWO)));
            mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        } else {
            mTabLayout.setVisibility(View.INVISIBLE);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SetsManagerFragment()).commit();
    }

    private void setPagerAdapter(){
        //TODO metoda przeterminowana
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SetsManagerFragment()).commit(); break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SetsServerFragment()).commit(); break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
