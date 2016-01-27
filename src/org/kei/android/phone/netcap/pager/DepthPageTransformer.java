package org.kei.android.phone.netcap.pager;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 *******************************************************************************
 * @file DepthPageTransformer.java
 * @author http://developer.android.com/training/animation/screen-slide.html
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
public class DepthPageTransformer implements PageTransformer {
  private static final float MIN_SCALE = 0.75f;

  @Override
  public void transformPage(final View view, final float position) {
    final int pageWidth = view.getWidth();

    if (position < -1) { // [-Infinity,-1)
      // This page is way off-screen to the left.
      view.setAlpha(0);

    } else if (position <= 0) { // [-1,0]
      // Use the default slide transition when moving to the left page
      view.setAlpha(1);
      view.setTranslationX(0);
      view.setScaleX(1);
      view.setScaleY(1);

    } else if (position <= 1) { // (0,1]
      // Fade the page out.
      view.setAlpha(1 - position);

      // Counteract the default slide transition
      view.setTranslationX(pageWidth * -position);

      // Scale the page down (between MIN_SCALE and 1)
      final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
          * (1 - Math.abs(position));
      view.setScaleX(scaleFactor);
      view.setScaleY(scaleFactor);

    } else { // (1,+Infinity]
      // This page is way off-screen to the right.
      view.setAlpha(0);
    }
  }
}
