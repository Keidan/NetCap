package org.kei.android.phone.netcap;

import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kei.android.phone.netcap.R;
import org.kei.android.phone.netcap.chooser.FileChooser;
import org.kei.android.phone.netcap.chooser.FileChooserActivity;
import org.kei.android.phone.netcap.utils.Tools;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 *******************************************************************************
 * @file OutputTabActivity.java
 * @author Keidan
 * @date 09/09/2015
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
public class OutputTabActivity extends Activity {
  private Spinner                        devicesSp             = null;
  private TextView                       browseOutputCaptureTV = null;
  private ArrayAdapter<String> adapter               = null;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_output);
    Fx.updateTransition(this, true);

    devicesSp = (Spinner) findViewById(R.id.devicesSp);
    browseOutputCaptureTV = (TextView) findViewById(R.id.browseOutputCaptureTV);
    browseOutputCaptureTV.setText(Tools.DEFAULT_ROOT.getAbsolutePath());
    // Create an ArrayAdapter using the string array and a default spinner layout
    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    devicesSp.setAdapter(adapter);
    actionRefresh(null);
  }
  
  public void actionStartCapture(final View v) {
    String sdest = browseOutputCaptureTV.getText().toString();
    if(sdest.isEmpty()) {
      Tools.showAlertDialog(this, "Error", "Empty destination folder!");
      return;
    }
    File fdest = new File(sdest);
    if(!fdest.isDirectory()) {
      Tools.showAlertDialog(this, "Error", "The destination folder is not a valid directory!");
      return;
    } else if(!fdest.canWrite()) {
      Tools.showAlertDialog(this, "Error", "Unable to write into the destination folder!");
      return;
    }
    PackageManager m = getPackageManager();
    String s = getPackageName();
    try {
        PackageInfo p = m.getPackageInfo(s, 0);
        s = p.applicationInfo.dataDir;
    } catch (PackageManager.NameNotFoundException e) {
    }
    File netcap = new File(s, "netcap");
    if(!netcap.exists()) {
      Tools.showAlertDialog(this, "Error", "'" + netcap.getAbsolutePath() + "' was not found!");
      return;
    }
    /*
    String ifaces = "";
    NetworkInterface ni = (NetworkInterface)devicesSp.getSelectedItem();
    if(ni.isAny()) {
      for(int i = 0; i < adapter.getCount(); i++) {
        ni = adapter.getItem(i);
        if(ni.isAny()) continue;
        ifaces += ni.getName();
        if(i < adapter.getCount() - 1) ifaces += ",";
      }
    } else
      ifaces = ni.getName();*/
    //Log.i(getClass().getSimpleName(), "ifaces:"+ifaces);
  }
  
  public void actionBrowseOutputCapture(final View v) {
    Map<String, String> extra = new HashMap<String, String>();
    extra.put(FileChooser.FILECHOOSER_TYPE_KEY, "" + FileChooser.FILECHOOSER_TYPE_DIRECTORY_ONLY);
    extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Save");    
    extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Use this folder:? ");
    extra.put(FileChooser.FILECHOOSER_SHOW_KEY, "" + FileChooser.FILECHOOSER_SHOW_DIRECTORY_ONLY);
    Tools.switchToForResult(this, FileChooserActivity.class,
        extra, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_DIRECTORY);
  }
  
  public void actionRefresh(final View v) {
    adapter.clear();
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      if(interfaces != null && interfaces.size() != 0)
        adapter.add(getResources().getText(R.string.any).toString());
      for(NetworkInterface ni : interfaces) if(ni.isUp()) adapter.add(ni.getName());
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
        browseOutputCaptureTV.setText(file);
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

