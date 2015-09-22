package org.kei.android.phone.netcap.fab;

import org.kei.android.phone.netcap.R;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

/**
 *******************************************************************************
 * @file FabHelper.java
 * @author Keidan
 * @date 22/09/2015
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
public class FabHelper {
  private static final String TRANSLATION_Y = "translationY";
  private ImageButton         fab;
  private boolean             expanded      = false;
  private View                fabAction1;
  private View                fabAction2;
  private View                fabAction3;
  private float               offset1;
  private float               offset2;
  private float               offset3;
  private Activity            activity;
  private static FabHelper    instance = null;

  private FabHelper() {
    
  }
  
  public static FabHelper getInstance() {
    if(instance == null) instance = new FabHelper();
    return instance;
  }
  
  public void install(final Activity activity) {
    this.activity = activity;
    final ViewGroup fabContainer = (ViewGroup) activity
        .findViewById(R.id.fab_container);
    fab = (ImageButton) activity.findViewById(R.id.fab);
    fabAction1 = activity.findViewById(R.id.fab_action_1);
    fabAction2 = activity.findViewById(R.id.fab_action_2);
    fabAction3 = activity.findViewById(R.id.fab_action_3);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        expanded = !expanded;
        if (expanded) {
          expandFab();
        } else {
          collapseFab();
        }
      }
    });
    fabContainer.getViewTreeObserver().addOnPreDrawListener(
        new ViewTreeObserver.OnPreDrawListener() {
          @Override
          public boolean onPreDraw() {
        fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
        offset1 = fab.getY() - fabAction1.getY();
        fabAction1.setTranslationY(offset1);
        offset2 = fab.getY() - fabAction2.getY();
        fabAction2.setTranslationY(offset2);
        offset3 = fab.getY() - fabAction3.getY();
        fabAction3.setTranslationY(offset3);
        return true;
          }
        });
  }

  private void collapseFab() {
    fab.setImageResource(R.drawable.animated_minus);
    final AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(createCollapseAnimator(fabAction1, offset1),
        createCollapseAnimator(fabAction2, offset2),
        createCollapseAnimator(fabAction3, offset3));
    animatorSet.start();
    animateFab();
  }
  
  private void expandFab() {
    fab.setImageResource(R.drawable.animated_plus);
    final AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(createExpandAnimator(fabAction1, offset1),
        createExpandAnimator(fabAction2, offset2),
        createExpandAnimator(fabAction3, offset3));
    animatorSet.start();
    animateFab();
  }
  
  private Animator createCollapseAnimator(final View view, final float offset) {
    return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset).setDuration(
        activity.getResources().getInteger(
            android.R.integer.config_mediumAnimTime));
  }
  
  private Animator createExpandAnimator(final View view, final float offset) {
    return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0).setDuration(
        activity.getResources().getInteger(
            android.R.integer.config_mediumAnimTime));
  }
  
  private void animateFab() {
    final Drawable drawable = fab.getDrawable();
    if (drawable instanceof Animatable) {
      ((Animatable) drawable).start();
    }
  }
}
