package org.kei.android.phone.netcap.pager;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 *******************************************************************************
 * @file ScreenSlidePagerAdapter.java
 * @author Keidan
 * @date 27/01/2016
 * @par Project NetCap
 *
 * @par Copyright 2016 Keidan, all right reserved
 *
 *      This software is distributed in the hope that it will be useful, but
 *      WITHOUT ANY WARRANTY.
 *
 *      License summary : You can modify and redistribute the sources code and
 *      binaries. You can send me the bug-fix
 *
 *      Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
  private final List<Fragment> fragments;
  
  public ScreenSlidePagerAdapter(final FragmentManager fm, List<Fragment> fragments) {
    super(fm);
    this.fragments = fragments;
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