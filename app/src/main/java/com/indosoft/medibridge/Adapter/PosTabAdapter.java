package com.indosoft.medibridge.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.indosoft.medibridge.Fragment.CashMemoDetailsFragment;
import com.indosoft.medibridge.Fragment.PosDetailsFragment;

public class PosTabAdapter extends FragmentStateAdapter {

    public PosTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0) {
            return new PosDetailsFragment();
        } else {
            return new CashMemoDetailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
