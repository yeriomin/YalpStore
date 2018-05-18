package com.dragons.aurora.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dragons.aurora.R;
import com.dragons.aurora.fragment.TopFreeApps;
import com.dragons.aurora.fragment.TopGrossingApps;
import com.dragons.aurora.fragment.TopTrendingApps;

public class CategoryFilterAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public CategoryFilterAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TopFreeApps();
        } else if (position == 1) {
            return new TopTrendingApps();
        } else {
            return new TopGrossingApps();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.category_topFree);
            case 1:
                return mContext.getString(R.string.category_trending);
            case 2:
                return mContext.getString(R.string.category_topGrossing);
            default:
                return null;
        }
    }

}