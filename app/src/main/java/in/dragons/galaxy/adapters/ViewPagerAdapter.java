package in.dragons.galaxy.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.dragons.galaxy.R;
import in.dragons.galaxy.fragment.CategoryListFragment;
import in.dragons.galaxy.fragment.HomeFragment;
import in.dragons.galaxy.fragment.SearchFragment;
import in.dragons.galaxy.fragment.UpdatableAppsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new UpdatableAppsFragment();
            case 2:
                return new CategoryListFragment();
            case 3:
                return new SearchFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.action_home);
            case 1:
                return mContext.getString(R.string.action_updates);
            case 2:
                return mContext.getString(R.string.action_categories);
            case 3:
                return mContext.getString(R.string.search_title);
            default:
                return null;
        }
    }

}
