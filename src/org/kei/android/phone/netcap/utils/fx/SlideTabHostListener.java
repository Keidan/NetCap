package org.kei.android.phone.netcap.utils.fx;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
/**
 *******************************************************************************
 * @file SlideTabHostListener.java
 * @author Keidan
 * @date 20/09/2015
 * @par Project
 * NetCap
 *
 * @par 
 * Copyright 2015 Keidan, all right reserved
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY.
 *
 * License summary : 
 *    You can modify and redistribute the sources code and binaries.
 *    You can send me the bug-fix
 *
 * Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class SlideTabHostListener implements OnTabChangeListener {
  private static final int    ANIMATION_TIME = 240;
  private View                previousView   = null;
  private View                currentView    = null;
  private int                 currentTab     = 0;
  /** Called when the activity is first created. */
  private TabHost             tabHost        = null;
  private OnTabChangeListener userAction     = null;

  public SlideTabHostListener(final TabHost tabhost,
      final OnTabChangeListener userAction) {
    this.tabHost = tabhost;
    this.userAction = userAction;
    previousView = tabHost.getCurrentView();
  }

  @Override
  public void onTabChanged(final String tabId) {
    if (userAction != null)
      userAction.onTabChanged(tabId);
    currentView = tabHost.getCurrentView();
    if (tabHost.getCurrentTab() > currentTab) {
      previousView.setAnimation(outToLeftAnimation());
      currentView.setAnimation(inFromRightAnimation());
    } else {
      previousView.setAnimation(outToRightAnimation());
      currentView.setAnimation(inFromLeftAnimation());
    }
    previousView = currentView;
    currentTab = tabHost.getCurrentTab();
  }

  /**
   * Custom animation that animates in from right
   * 
   * @return Animation the Animation object
   */
  private Animation inFromRightAnimation() {
    final Animation inFromRight = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
    return setProperties(inFromRight);
  }

  /**
   * Custom animation that animates out to the right
   * 
   * @return Animation the Animation object
   */
  private Animation outToRightAnimation() {
    final Animation outToRight = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
    return setProperties(outToRight);
  }

  /**
   * Custom animation that animates in from left
   * 
   * @return Animation the Animation object
   */
  private Animation inFromLeftAnimation() {
    final Animation inFromLeft = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,
        0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
        0.0f);
    return setProperties(inFromLeft);
  }

  /**
   * Custom animation that animates out to the left
   * 
   * @return Animation the Animation object
   */
  private Animation outToLeftAnimation() {
    final Animation outtoLeft = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
        -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f);
    return setProperties(outtoLeft);
  }

  /**
   * Helper method that sets some common properties
   * 
   * @param animation
   *          the animation to give common properties
   * @return the animation with common properties
   */
  private Animation setProperties(final Animation animation) {
    animation.setDuration(ANIMATION_TIME);
    animation.setInterpolator(new AccelerateInterpolator());
    return animation;
  }
}
