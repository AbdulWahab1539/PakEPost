package com.app.fypfinal.adapters;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.activities.ReceivedParcel;
import com.app.fypfinal.activities.SentParcel;

public class FragmentPagerAdapter extends FragmentStateAdapter implements Info {

    int FRAGMENT_SIZE = 2;
    public static Fragment selectedFragment = null;

    public FragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i(TAG, "createFragment: " + position);
        switch (position) {
            case 0:
                selectedFragment = new SentParcel();
                break;
            case 1:
                selectedFragment = new ReceivedParcel();
                break;
        }
        return selectedFragment;
    }

    @Override
    public int getItemCount() {
        return FRAGMENT_SIZE;
    }
}