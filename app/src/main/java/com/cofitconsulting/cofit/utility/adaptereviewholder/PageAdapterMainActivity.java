package com.cofitconsulting.cofit.utility.adaptereviewholder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cofitconsulting.cofit.user.registro_finanziario.TasseFragment;
import com.cofitconsulting.cofit.user.registro_finanziario.crediti.CreditiFragment;
import com.cofitconsulting.cofit.user.registro_finanziario.debiti.DebitiFragment;

public class PageAdapterMainActivity extends FragmentPagerAdapter {

    private int tabsNumber;

    public PageAdapterMainActivity(@NonNull FragmentManager fm, int behavior, int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new TasseFragment();

            case 1: return new CreditiFragment();

            case 2: return new DebitiFragment();

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


