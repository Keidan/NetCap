package org.kei.android.phone.netcap;

import org.kei.android.phone.netcap.fab.FabHelper;
import org.kei.android.phone.netcap.listview.ListViewAdapter;
import org.kei.android.phone.netcap.listview.CaptureListViewItem;
import org.kei.android.phone.netcap.listview.CaptureListViewLoader;
import org.kei.android.phone.netcap.utils.Tools;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
  private ListView           captureLV        = null;
  private ListViewAdapter<CaptureListViewItem>           adapter        = null;
  

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_captureviewer);
    FabHelper.getInstance().install(this);
    Fx.updateTransition(this, true);
    Bundle b = getIntent().getExtras();
    if(b != null) {
      if (b.containsKey(KEY_FILE))
        file = b.getString(KEY_FILE);
    }
    captureLV = (ListView)findViewById(R.id.captureLV);
    adapter = new ListViewAdapter<CaptureListViewItem>(this, R.layout.rowlayout_capture);
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
  

  public void fabAction1(View view) {
    captureLV.post(new Runnable() {
      @Override
      public void run() {
        captureLV.setSelection(adapter.getCount() - 1);
      }
    });
  }

  public void fabAction2(View view) {
    captureLV.post(new Runnable() {
      @Override
      public void run() {
        captureLV.setSelection(0);
      }
    });
  }

  public void fabAction3(View view) {
      Log.d("RRRR", "Action 3");
  }
}
