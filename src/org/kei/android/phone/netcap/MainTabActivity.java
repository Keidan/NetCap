package org.kei.android.phone.netcap;

import org.kei.android.atk.view.EffectTabActivity;
import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.atk.utils.fx.SlideTabHostListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

/**
 *******************************************************************************
 * @file MainTabActivity.java
 * @author Keidan
 * @date 20/09/2015
 * @par Project NetCap
 *
 * @par Copyright 2015 Keidan, all right reserved
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
@SuppressWarnings("deprecation")
public class MainTabActivity extends EffectTabActivity implements OnTabChangeListener {
  private TabHost tabHost;
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maintab);
    
    // Get TabHost Refference
    tabHost = getTabHost();
    
    TabHost.TabSpec spec;
    Intent intent;
    
    /************* TAB1 ************/
    // Create Intents to launch an Activity for the tab (to be reused)
    intent = new Intent().setClass(this, InputTabActivity.class);
    spec = tabHost.newTabSpec("First").setIndicator("Input")
        .setContent(intent);
    
    // Add intent to tab
    tabHost.addTab(spec);
    
    /************* TAB2 ************/
    intent = new Intent().setClass(this, OutputTabActivity.class);
    spec = tabHost.newTabSpec("Second").setIndicator("Output")
        .setContent(intent);
    tabHost.addTab(spec);
    
    // Set Tab1 as Default tab and change image
    tabHost.getTabWidget().setCurrentTab(0);
    tabHost.setOnTabChangedListener(new SlideTabHostListener(tabHost, this));
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
    Fx.updateTransition(this, false);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    Fx.updateTransition(this, false);
  }
  
  @Override
  public void onTabChanged(final String tabId) {
    if (!tabId.equals("First")) {
      final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), 0);
    }
  }
}
