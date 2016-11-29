package alkamli.fahad.chat.chat.homePage;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.R;

public class ViewPagerAdapter extends FragmentPagerAdapter{

    ArrayList<Fragment> fragments=new ArrayList<Fragment>();
    ArrayList<String> tabTitles=new ArrayList<String>();
    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);

    }

    public void addFragments(Fragment fragment,String tabTitle)
    {
        fragments.add(fragment);
        tabTitles.add(tabTitle);
    }
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }






}
