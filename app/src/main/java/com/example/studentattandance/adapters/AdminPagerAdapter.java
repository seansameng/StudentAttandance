package com.example.studentattandance.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studentattandance.fragments.UserManagementFragment;
import com.example.studentattandance.fragments.ClassManagementFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserManagementFragment();
            case 1:
                return new ClassManagementFragment();
            default:
                return new UserManagementFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
