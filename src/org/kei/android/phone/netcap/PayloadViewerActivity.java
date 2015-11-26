package org.kei.android.phone.netcap;

import org.kei.android.atk.view.EffectActivity;
import org.kei.android.phone.net.NetworkHelper;
import org.kei.android.phone.net.layer.Payload;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *******************************************************************************
 * @file PayloadViewerActivity.java
 * @author Keidan
 * @date 01/10/2015
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
public class PayloadViewerActivity extends EffectActivity {

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_payloadviewer);
    setTitle(getResources().getText(R.string.app_name) + " - " + ApplicationCtx.getAppId(this));
    ListView payloadLV = (ListView)findViewById(R.id.payloadLV);
    Payload p = (Payload)ApplicationCtx.getAppLayer(this);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        R.layout.listview_simple_row, R.id.label1, 
        NetworkHelper.formatBuffer(p.getDatas()));
    payloadLV.setAdapter(adapter); 
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }
}
