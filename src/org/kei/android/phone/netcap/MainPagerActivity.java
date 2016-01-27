package org.kei.android.phone.netcap;

import java.util.List;
import java.util.Vector;

import org.kei.android.atk.view.IThemeActivity;
import org.kei.android.atk.view.chooser.FileChooserActivity;
import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.phone.netcap.pager.DepthPageTransformer;
import org.kei.android.phone.netcap.pager.ScreenSlidePagerAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 *******************************************************************************
 * @file MainPagerActivity.java
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
public class MainPagerActivity extends FragmentActivity implements IThemeActivity, OnPageChangeListener {
  private static final String CURRENT_PAGER_TAB_KEY = "CURRENT_PAGER_TAB_KEY";
  private static final int    BACK_TIME_DELAY       = 2000;
  private static long         lastBackPressed       = -1;
  private ViewPager           mPager                = null;
  private PagerAdapter        mPagerAdapter         = null;
  private List<Fragment>      fragments             = null;
  private SharedPreferences   prefs                 = null;
  
  static {
    Fx.default_animation = Fx.ANIMATION_FADE;
  }
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    themeUpdate();
    super.onCreate(savedInstanceState);
    Fx.updateTransition(this, true);
    setContentView(R.layout.activity_maintab);
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    fragments = new Vector<Fragment>();
    fragments.add(new InputFragment(this));
    fragments.add(new OutputFragment(this));
    
    mPager = (ViewPager) findViewById(R.id.pager);
    PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
    pagerTabStrip.setDrawFullUnderline(false);
    TypedArray ta = getTheme().obtainStyledAttributes(new int [] { R.attr.pagerTitleColor });
    pagerTabStrip.setTabIndicatorColor(ta.getColor(0, Color.BLACK));
    ta.recycle();
    mPager.setPageTransformer(true, new DepthPageTransformer());
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
    mPager.setAdapter(mPagerAdapter);
    mPager.setCurrentItem(prefs.getInt(CURRENT_PAGER_TAB_KEY, 0));
    mPager.setOnPageChangeListener(this);
  }
  
  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    // Check which request we're responding to
    if (requestCode == FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE) {
      if (resultCode == RESULT_OK) {
        final String msg = data
            .getStringExtra(FileChooserActivity.FILECHOOSER_USER_MESSAGE);
        for(Fragment f : fragments) {
          if(f.getClass().getSimpleName().equals(msg) && InputFragment.class.isInstance(f)) {
            final String file = data
                .getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
            ((InputFragment)f).validateAndOpen(file);
            break;
          } else if(f.getClass().getSimpleName().equals(msg) && OutputFragment.class.isInstance(f)) {
            final String file = data
                .getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
            ((OutputFragment)f).setFile(file);
            break;
          }
        }
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    Fx.updateTransition(this, false);
  }
  
  protected void onDestroy() {
    super.onDestroy();
    for(Fragment f : fragments) {
      if(OutputFragment.class.isInstance(f))
        ((OutputFragment)f).delete(false);
    }
  }
  
  @Override
  public void onBackPressed() {
    if (!exitOnDoubleBack()) {
      super.onBackPressed();
    } else {
      if (lastBackPressed + BACK_TIME_DELAY > System.currentTimeMillis()) {
        super.onBackPressed();
      } else {
        Tools.toast(getBaseContext(), getToastIconId(),
            getResources().getText(getOnDoubleBackExitTextId()));
      }
      lastBackPressed = System.currentTimeMillis();
    }
  }
  
  protected boolean exitOnDoubleBack() {
    return true;
  }
  
  protected int getToastIconId() {
    return R.drawable.ic_launcher;
  }
  
  protected int getOnDoubleBackExitTextId() {
    return org.kei.android.atk.R.string.onDoubleBackExitText;
  }

  @Override
  public void themeUpdate() {
    Fx.switchTheme(this, R.style.AppBaseTheme, false);
  }
  
  @Override
  public int getAnime(final AnimationType at) {
    return Fx.getAnimationFromPref(this, at);
  }
  
  @Override
  public void onPageSelected(final int position) {
    final Editor e = prefs.edit();
    e.putInt(CURRENT_PAGER_TAB_KEY, position);
    e.commit();
  }
  
  @Override
  public void onPageScrollStateChanged(final int state) {
  }
  
  @Override
  public void onPageScrolled(final int position, final float positionOffset,
      final int positionOffsetPixels) {
  }
}
