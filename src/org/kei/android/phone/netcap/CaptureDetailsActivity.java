package org.kei.android.phone.netcap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kei.android.atk.view.EffectActivity;
import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.netcap.listview.ExpandableListAdapter;

import android.os.Bundle;
import android.widget.ExpandableListView;

/**
 *******************************************************************************
 * @file CaptureDetailsActivity.java
 * @author Keidan
 * @date 30/09/2015
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
public class CaptureDetailsActivity extends EffectActivity {
  
  private ExpandableListAdapter listAdapter;
  private ExpandableListView expListView;
  private List<String> listDataHeader;
  private HashMap<String, List<String>> listDataChild;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_capturedetails);
    setTitle(getResources().getText(R.string.app_name) + " - " + ApplicationCtx.getAppId(this));

    // get the listview
    expListView = (ExpandableListView) findViewById(R.id.detailsELV);

    // preparing list data
    prepareListData();

    listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

    // setting list adapter
    expListView.setAdapter(listAdapter);
    
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  /*
   * Preparing the list data
   */
  private void prepareListData() {
    listDataHeader = new ArrayList<String>();
    listDataChild = new HashMap<String, List<String>>();
    Layer layers = ApplicationCtx.getAppLayer(this);
    Layer layer = null;
    do {
      if (layers != null) {
        layer = layers;
        List<String> lines = new ArrayList<String>();
        layer.buildDetails(lines);
        listDataHeader.add(layer.getFullName());
        listDataChild.put(layer.getFullName(), lines);
      }
    } while ((layers = layers.getNext()) != null);
  }
}
