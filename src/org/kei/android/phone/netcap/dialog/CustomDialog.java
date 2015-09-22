package org.kei.android.phone.netcap.dialog;

import org.kei.android.phone.netcap.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file CustomDialog.java
 * @author Keidan
 * @date 01/09/2015
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
public class CustomDialog extends Dialog {

  private Button         yesBT     = null;
  private Button         noBT      = null;
  private TextView       titleTV   = null;
  private RelativeLayout contentRL = null;

  public CustomDialog(Activity a) {
    super(a);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.custom_dialog);
    yesBT = (Button) findViewById(R.id.yesBT);
    noBT = (Button) findViewById(R.id.noBT);
    titleTV = (TextView) findViewById(R.id.titleTV);
    contentRL = (RelativeLayout) findViewById(R.id.contentRL);

    final int size = 24;
    final float density = a.getResources().getDisplayMetrics().density;
    final Drawable drawable = a.getResources().getDrawable(R.drawable.ic_launcher, null);
    final int width = Math.round(size * density);
    final int height = Math.round(size * density);
    drawable.setBounds(0, 0, width, height);
    titleTV.setCompoundDrawables(drawable, null, null, null);
  }

  public void hideNegativeButton() {
    noBT.setVisibility(View.GONE);
    yesBT.setText(R.string.ok);
  }

  public void setPositiveOnClickListener(
      final android.view.View.OnClickListener li) {
    yesBT.setOnClickListener(li);
  }

  public void setNegativeOnClickListener(
      final android.view.View.OnClickListener li) {
    noBT.setOnClickListener(li);
  }

  public void setTitle(String title) {
    titleTV.setText(title);
  }

  public RelativeLayout getContent() {
    return contentRL;
  }

  public View setViewFromRes(int id) {
    View child = getLayoutInflater().inflate(id, null);
    contentRL.addView(child);
    return child;
  }

  public void setTextView(final TextView tv) {
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT);
    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
    contentRL.addView(tv, params);
  }

  public void setHeigth(final int height) {
    LinearLayout ll = (LinearLayout) getWindow().getDecorView().findViewById(
        R.id.rootLL);
    ll.getLayoutParams().height = height;
    ll.requestLayout();
  }

  public void setWidth(final int width) {
    LinearLayout ll = (LinearLayout) getWindow().getDecorView().findViewById(
        R.id.rootLL);
    ll.getLayoutParams().width = width;
    ll.requestLayout();
  }

  public void setContentHeigth(final int height) {
    contentRL.getLayoutParams().height = height;
    contentRL.requestLayout();
  }

  public void setContentWidth(final int width) {
    contentRL.getLayoutParams().width = width;
    contentRL.requestLayout();
  }
}
