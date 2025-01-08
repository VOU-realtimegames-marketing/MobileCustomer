package com.example.customer.controller.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.customer.controller.fragment.FragmentGame;
import com.example.customer.controller.fragment.FragmentHome;
import com.example.customer.controller.fragment.FragmentLocation;
import com.example.customer.controller.fragment.FragmentProfile;
import com.example.customer.controller.fragment.FragmentVoucher;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:return new FragmentHome();
            case 1:return new FragmentVoucher();
            case 2:return new FragmentGame();
            case 3:return new FragmentLocation();
            case 4:return new FragmentProfile();
            default:return new FragmentHome();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
