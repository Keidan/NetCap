package fr.ralala.netcap.ui.pager;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author http://developer.android.com/training/animation/screen-slide.html
 * ******************************************************************************
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
  private final List<Fragment> mFragments;

  public ScreenSlidePagerAdapter(final FragmentManager fm, List<Fragment> fragments) {
    super(fm);
    mFragments = fragments;
  }

  @Override
  public Fragment getItem(int position) {
    return mFragments.get(position);
  }

  @Override
  public int getCount() {
    return mFragments.size();
  }

  @Override
  public CharSequence getPageTitle(final int position) {
    switch (position) {
      case 0:
        return "Analyse";
      case 1:
        return "Capture";
      default:
        return null;
    }
  }
}