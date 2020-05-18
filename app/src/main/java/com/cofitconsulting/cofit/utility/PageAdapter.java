package com.cofitconsulting.cofit.utility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cofitconsulting.cofit.user.fragment.CreditiFragment;
import com.cofitconsulting.cofit.user.fragment.DebitiFragment;
import com.cofitconsulting.cofit.user.fragment.TasseFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int tabsNumber;

    public PageAdapter(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new CreditiFragment();

            case 1: return new DebitiFragment();

            case 2: return new TasseFragment();

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}


