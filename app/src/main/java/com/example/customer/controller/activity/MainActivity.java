package com.example.customer.controller.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.customer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends FragmentActivity {
    private ViewPager2 viewPager;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=(ViewPager2) findViewById(R.id.viewPager);
        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavigationView);
        ViewPagerAdapter adapter=new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Menu menu = bottomNavigationView.getMenu();
                if (item == menu.findItem(R.id.home)) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (item == menu.findItem(R.id.voucher)) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (item == menu.findItem(R.id.game)) {
                    viewPager.setCurrentItem(2);
                    return true;
                } else if (item == menu.findItem(R.id.location)) {
                    viewPager.setCurrentItem(3);
                    return true;
                } else if (item == menu.findItem(R.id.profile)) {
                    viewPager.setCurrentItem(4);
                    return true;
                }
                return false;
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });

    }
}