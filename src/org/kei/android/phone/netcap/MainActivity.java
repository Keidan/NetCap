package org.kei.android.phone.netcap;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.NetworkInterface;
import org.kei.android.phone.netcap.R;
import org.kei.android.phone.netcap.chooser.FileChooser;
import org.kei.android.phone.netcap.chooser.FileChooserActivity;
import org.kei.android.phone.netcap.utils.Fx;
import org.kei.android.phone.netcap.utils.Tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 *******************************************************************************
 * @file MainActivity.java
 * @author Keidan
 * @date 09/09/2015
 * @par Project
 * NetCap
 *
 * @par Copyright
 * Copyright 2011-2013 Keidan, all right reserved
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
public class MainActivity extends Activity {
  private Spinner                        devicesSp = null;
  private TextView                       browseTV  = null;
  private ArrayAdapter<NetworkInterface> adapter   = null;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Fx.updateTransition(this, true);

    devicesSp = (Spinner) findViewById(R.id.devicesSp);
    browseTV = (TextView) findViewById(R.id.browseTV);
    browseTV.setText(FileChooser.DEFAULT_ROOT.getAbsolutePath());
    // Create an ArrayAdapter using the string array and a default spinner layout
    adapter = new ArrayAdapter<NetworkInterface>(this, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    devicesSp.setAdapter(adapter);
    actionRefresh(null);
  }
  
  public void actionBrowse(final View v) {
    Map<String, String> extra = new HashMap<String, String>();
    extra.put(FileChooser.FILECHOOSER_TYPE_KEY, "" + FileChooser.FILECHOOSER_TYPE_DIRECTORY_ONLY);
    extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Load");    
    extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Use this folder:? ");
    extra.put(FileChooser.FILECHOOSER_SHOW_KEY, "" + FileChooser.FILECHOOSER_SHOW_DIRECTORY_ONLY);
    Tools.switchToForResult(this, FileChooserActivity.class,
        extra, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_DIRECTORY);
  }
  
  public void actionRefresh(final View v) {
    adapter.clear();
    try {
      List<NetworkInterface> ifaces = NetworkHelper.getInterfaces();
      if(ifaces != null && ifaces.size() != 0)
        adapter.add(new NetworkInterface(getResources().getText(R.string.any).toString(), true));
      for(NetworkInterface ni : ifaces) adapter.add(ni);
    } catch (Throwable e) {
      e.printStackTrace();
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
  }
  
  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    // Check which request we're responding to
    if (requestCode == FileChooserActivity.FILECHOOSER_SELECTION_TYPE_DIRECTORY) {
      if (resultCode == RESULT_OK) {
        final String file = data
            .getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
        browseTV.setText(file);
      }
    }
  }


  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finish();
    Fx.updateTransition(this, false);
  }
}
