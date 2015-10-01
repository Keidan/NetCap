package org.kei.android.phone.netcap;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.netcap.R;
import org.kei.android.phone.netcap.chooser.FileChooser;
import org.kei.android.phone.netcap.chooser.FileChooserActivity;
import org.kei.android.phone.netcap.utils.Tools;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 *******************************************************************************
 * @file InputTabActivity.java
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
public class InputTabActivity extends Activity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_input);
    Fx.updateTransition(this, true);
  }
  
  public void actionBrowseInputCapture(final View v) {
    Map<String, String> extra = new HashMap<String, String>();
    extra.put(FileChooser.FILECHOOSER_TYPE_KEY, "" + FileChooser.FILECHOOSER_TYPE_FILE_ONLY);
    extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Load");    
    extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Use this file:? ");
    extra.put(FileChooser.FILECHOOSER_SHOW_KEY, "" + FileChooser.FILECHOOSER_SHOW_FILE_AND_DIRECTORY);
    Tools.switchToForResult(this, FileChooserActivity.class,
        extra, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE);
  }
  
  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    // Check which request we're responding to
    if (requestCode == FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE) {
      if (resultCode == RESULT_OK) {
        final String file = data
            .getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
        if(file.isEmpty()) {
          Tools.showAlertDialog(this, "Error", "Empty source file!");
          return;
        }
        File fsrc = new File(file);
        if(!fsrc.isFile()) {
          Tools.showAlertDialog(this, "Error", "The source file is not a valid file!");
          return;
        } else if(!fsrc.canRead()) {
          Tools.showAlertDialog(this, "Error", "Unable to read the capture file!");
          return;
        }
        try {
          if(!NetworkHelper.isPCAP(fsrc.getAbsolutePath())) {
            Tools.showAlertDialog(this, "Error", "The selected file is not in a PCAP format.");
            return;
          }
        } catch(Throwable t) {
          t.printStackTrace();
          Tools.showAlertDialog(this, "Error", "Exception occurred: " + t.getMessage());
          return;
        }
        Map<String, String> extra = new HashMap<String, String>();
        extra.put(CaptureViewerActivity.KEY_FILE, fsrc.getAbsolutePath());
        Tools.switchTo(this, CaptureViewerActivity.class, extra);
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
