package com.example.eventsapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.eventsapp.fragments.ComposeFragment;
import com.example.eventsapp.fragments.HomeFragment;
import com.example.eventsapp.fragments.UserProfileFragment;
import com.example.eventsapp.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    public  FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setItemIconTintList(null);
//      bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        break;

                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.action_userProfile:
                        fragment = new UserProfileFragment();
                        break;

                    case R.id.action_search:
                    default:
                        fragment = new SearchFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }


    /*private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = new Fragment();

                    switch (item.getItemId()){
                        case R.layout.fragments_home:
                            Log.e(TAG, "home fragment");

                            selectedFragment = new HomeFragment();
                            break;
                        case R.layout.fragments_search:
                            Log.e(TAG, "search fragment");

                            selectedFragment = new SearchFragment();
                            break;
                        case R.layout.fragments_pendingvents:
                            Log.e(TAG, "pending events fragment");

                            selectedFragment = new PendingEventsFragment();
                            break;
                        case R.layout.fragments_compose:
                            Log.e(TAG, "compose fragment");

                            selectedFragment = new ComposeFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, selectedFragment).commit();
                    return true;
                }
            };*/


}

