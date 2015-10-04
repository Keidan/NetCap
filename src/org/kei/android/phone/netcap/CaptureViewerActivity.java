package org.kei.android.phone.netcap;

import org.kei.android.phone.netcap.dialog.CustomDialog;
import org.kei.android.phone.netcap.fab.FabHelper;
import org.kei.android.phone.netcap.listview.ListViewAdapter;
import org.kei.android.phone.netcap.listview.CaptureListViewItem;
import org.kei.android.phone.netcap.listview.CaptureListViewLoader;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

/**
 *******************************************************************************
 * @file CaptureViewerActivity.java
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
public class CaptureViewerActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
  public static final String                   KEY_FILE       = "capture_file";
  private String                               file           = null;
  private ListView                             captureLV      = null;
  private ListViewAdapter<CaptureListViewItem> adapter        = null;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_captureviewer);
    FabHelper.getInstance().install(this);
    Fx.updateTransition(this, true);
    Bundle b = getIntent().getExtras();
    if(b != null) {
      if (b.containsKey(KEY_FILE))
        file = b.getString(KEY_FILE);
    }
    if(file == null) file = getIntent().getData().getEncodedPath();
    captureLV = (ListView)findViewById(R.id.captureLV);
    adapter = new ListViewAdapter<CaptureListViewItem>(this, R.layout.rowlayout_capture);
    adapter.setFilterId(CaptureListViewItem.FILTER_BY_ALL);
    CaptureListViewLoader loader = new CaptureListViewLoader(this, adapter, file);
    captureLV.setAdapter(adapter);
    loader.execute();
    captureLV.setOnItemClickListener(this);
    captureLV.setOnItemLongClickListener(this);
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

  public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
    final CaptureListViewItem item = (CaptureListViewItem)captureLV.getItemAtPosition(pos);
    ApplicationCtx.startActivity(this, item.getLayer(), item.getId(), CaptureDetailsActivity.class);
  }
  
  @Override
  public boolean onItemLongClick(AdapterView<?> adapter, View v, int pos, long id) {
    final CaptureListViewItem item = (CaptureListViewItem)captureLV.getItemAtPosition(pos);
    ApplicationCtx.startActivity(this, item.getPayload(), item.getId(), PayloadViewerActivity.class);
    return true;
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
    final CustomDialog cd = new CustomDialog(this);
    cd.setTitle("Search");
    cd.setHeigth(500);
    cd.setContentHeigth(300);
    final View content = cd.setViewFromRes(R.layout.search_dialog);
    content.invalidate();
    final EditText searchET = (EditText) content.findViewById(R.id.searchET);
    cd.setCancelable(false);
    cd.setPositiveOnClickListener(new android.view.View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String search = searchET.getText().toString();
        final boolean idCB = ((CheckBox) content.findViewById(R.id.idCB)).isChecked();
        final boolean timeCB = ((CheckBox) content.findViewById(R.id.timeCB)).isChecked();
        final boolean protocolCB = ((CheckBox) content.findViewById(R.id.protocolCB)).isChecked();
        final boolean infoCB = ((CheckBox) content.findViewById(R.id.infoCB)).isChecked();
        int flags = 0;
        if(idCB) flags |= CaptureListViewItem.FILTER_BY_ID;
        if(timeCB) flags |= CaptureListViewItem.FILTER_BY_TIME;
        if(protocolCB) flags |= CaptureListViewItem.FILTER_BY_PROTOCOL;
        if(infoCB) flags |= CaptureListViewItem.FILTER_BY_INFO;
        adapter.setFilterId(flags);
        adapter.filter(search);
        cd.cancel();
      }
    });
    cd.setNegativeOnClickListener(new android.view.View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cd.cancel();
      }
    });
    cd.show();
  }
}
