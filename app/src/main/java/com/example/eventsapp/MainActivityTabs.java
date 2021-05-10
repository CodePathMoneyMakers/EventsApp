package com.example.eventsapp;

import android.os.Bundle;
import com.example.eventsapp.adapters.vViewPagerAdapter;
import com.example.eventsapp.fragments.ComposeFragment;
import com.example.eventsapp.fragments.HomeFragment;
import com.example.eventsapp.fragments.SearchFragment;
import com.example.eventsapp.fragments.UserProfileFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivityTabs extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        setUpTabs();

    }

    private void setUpTabs() {
        vViewPagerAdapter adapter = new vViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "");
        adapter.addFragment(new SearchFragment(), "");
        adapter.addFragment(new ComposeFragment(), "");
        adapter.addFragment(new UserProfileFragment(), "");

        ViewPager  viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_homefill);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
        tabLayout.getTabAt(2).setIcon(R.drawable.composenew);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_person);

    }
}
