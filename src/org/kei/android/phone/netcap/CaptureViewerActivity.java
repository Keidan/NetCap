package org.kei.android.phone.netcap;

import org.kei.android.phone.netcap.listview.ListViewAdapter;
import org.kei.android.phone.netcap.listview.CaptureListViewItem;
import org.kei.android.phone.netcap.listview.CaptureListViewLoader;
import org.kei.android.phone.netcap.utils.Tools;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;

/**
 *******************************************************************************
 * @file CaptureActivity.java
 * @author Keidan
 * @date 11/09/2015
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
public class CaptureViewerActivity extends Activity  {
  public static final String KEY_FILE         = "capture_file";
  private String             file             = Tools.DEFAULT_ROOT
                                                  .getAbsolutePath();
  

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_captureviewer);
    Fx.updateTransition(this, true);
    Bundle b = getIntent().getExtras();
    if(b != null) {
      if (b.containsKey(KEY_FILE))
        file = b.getString(KEY_FILE);
    }
    ListView captureLV = (ListView)findViewById(R.id.captureLV);
    ListViewAdapter<CaptureListViewItem> adapter = new ListViewAdapter<CaptureListViewItem>(this, R.layout.rowlayout_capture);
    CaptureListViewLoader loader = new CaptureListViewLoader(this, adapter, file);
    captureLV.setAdapter(adapter);
    loader.execute();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Fx.updateTransition(this, false);
    finish();
  }
}
