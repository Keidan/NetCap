package fr.ralala.netcap.ui.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import fr.ralala.netcap.R;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class FabHelper {
  private static final String TRANSLATION_Y = "translationY";
  private ImageButton mFab;
  private boolean mExpanded = false;
  private View mFabAction1;
  private View mFabAction2;
  private View mFabAction3;
  private float mOffset1;
  private float mOffset2;
  private float mOffset3;
  private AppCompatActivity mActivity;

  public FabHelper() {
  }

  public void install(final AppCompatActivity activity) {
    mActivity = activity;
    final ViewGroup fabContainer = (ViewGroup) activity.findViewById(R.id.fab_container);
    mFab = (ImageButton) activity.findViewById(R.id.fab);
    mFabAction1 = activity.findViewById(R.id.fab_action_1);
    mFabAction2 = activity.findViewById(R.id.fab_action_2);
    mFabAction3 = activity.findViewById(R.id.fab_action_3);
    mFab.setOnClickListener(v -> {
      if (!mExpanded) {
        expandFab();
      } else {
        collapseFab();
      }
    });
    fabContainer.getViewTreeObserver().addOnPreDrawListener(
        new ViewTreeObserver.OnPreDrawListener() {
          @Override
          public boolean onPreDraw() {
            fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
            mOffset1 = mFab.getY() - mFabAction1.getY();
            mFabAction1.setTranslationY(mOffset1);
            mOffset2 = mFab.getY() - mFabAction2.getY();
            mFabAction2.setTranslationY(mOffset2);
            mOffset3 = mFab.getY() - mFabAction3.getY();
            mFabAction3.setTranslationY(mOffset3);
            return true;
          }
        });
  }

  public boolean isExpanded() {
    return mExpanded;
  }

  public void collapseFab() {
    mExpanded = false;
    mFab.setImageResource(R.drawable.animated_minus);
    final AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(createCollapseAnimator(mFabAction1, mOffset1),
        createCollapseAnimator(mFabAction2, mOffset2),
        createCollapseAnimator(mFabAction3, mOffset3));
    animatorSet.start();
    animateFab();
  }

  private void expandFab() {
    mExpanded = true;
    mFab.setImageResource(R.drawable.animated_plus);
    final AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(createExpandAnimator(mFabAction1, mOffset1),
        createExpandAnimator(mFabAction2, mOffset2),
        createExpandAnimator(mFabAction3, mOffset3));
    animatorSet.start();
    animateFab();
  }

  private Animator createCollapseAnimator(final View view, final float offset) {
    return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset).setDuration(
        mActivity.getResources().getInteger(
            android.R.integer.config_mediumAnimTime));
  }

  private Animator createExpandAnimator(final View view, final float offset) {
    return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0).setDuration(
        mActivity.getResources().getInteger(
            android.R.integer.config_mediumAnimTime));
  }

  private void animateFab() {
    final Drawable drawable = mFab.getDrawable();
    if (drawable instanceof Animatable) {
      ((Animatable) drawable).start();
    }
  }
}
