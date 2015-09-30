package org.kei.android.phone.netcap;

import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.netcap.dialog.CustomDialog;
import org.kei.android.phone.netcap.fab.FabHelper;
import org.kei.android.phone.netcap.listview.CaptureListViewItem;
import org.kei.android.phone.netcap.listview.CaptureListViewLoader;
import org.kei.android.phone.netcap.listview.ListViewAdapter;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

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
public class CaptureDetailsActivity extends Activity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setContentView(R.layout.activity_capturedetails);
    Fx.updateTransition(this, true);
    
    TextView detailsTV = (TextView)findViewById(R.id.detailsTV);
    
    Layer layers = ApplicationCtx.getAppLayer(this);
    Layer layer = null;
    do {
      if (layers != null) {
        layer = layers;
        detailsTV.append(layer.getFullName() + "\n");
      }
    } while ((layers = layers.getNext()) != null);
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
