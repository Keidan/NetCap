package org.kei.android.phone.netcap.chooser;

import java.io.File;

import org.kei.android.phone.netcap.chooser.handler.ErrorStatus;
import org.kei.android.phone.netcap.chooser.handler.IProcessHandler;
import org.kei.android.phone.netcap.chooser.handler.ProcessHandler;
import org.kei.android.phone.netcap.utils.Fx;
import org.kei.android.phone.netcap.utils.Tools;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 *******************************************************************************
 * @file FileChooserActivity.java
 * @author Keidan
 * @date 10/09/2015
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
public class FileChooserActivity extends FileChooser implements IProcessHandler {

  public static final String FILECHOOSER_RESULT_KEY                     = "file";
  public static final int    FILECHOOSER_RESULT_TYPE_FILE_SELECTED      = 1;
  public static final int    FILECHOOSER_RESULT_TYPE_DIRECTORY_SELECTED = 2;
  private ProcessHandler     handler                                    = null;
  private Option             opt                                        = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handler = new ProcessHandler(this);
    Fx.updateTransition(this, true);
  }

  @Override
  protected void onFileSelected(final Option opt) {
    handler.compute(this, opt);
  }

  @Override
  public void onBackPressed() {
    File parent = currentDir.getParentFile();
    if (parent == null || parent.equals(DEFAULT_ROOT.getParentFile())) {
      super.onBackPressed();
      finish();
      Fx.updateTransition(this, false);
    } else {
      currentDir = parent;
      fill(currentDir);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Fx.updateTransition(this, false);
  }

  @Override
  public ErrorStatus doCompute(final Object userObject) {
    opt = (Option) userObject;
    if (opt == null)
      return ErrorStatus.CANCEL; /* cancel action */
    if (!Tools.isMountedSdcard())
      return ErrorStatus.ERROR_NOT_MOUNTED;

    final File file = new File(new File(opt.getPath()).getParent(),
        opt.getName());
    if (!file.canRead())
      return ErrorStatus.ERROR_CANT_READ;
    return ErrorStatus.NO_ERROR;
  }

  @Override
  public void onSuccess() {
    Log.d(getClass().getSimpleName(), "onSuccess: Rebuild the hmi");
    final Intent returnIntent = new Intent();
    int result = RESULT_CANCELED;
    if (opt != null) {
      final File file = new File(new File(opt.getPath()).getParent(),
          opt.getName());
      returnIntent.putExtra(FILECHOOSER_RESULT_KEY, file.getAbsolutePath());
      result = RESULT_OK;
    }
    Log.d(getClass().getSimpleName(), "Send result");
    setResult(result, returnIntent);
    finish();
    opt = null;
    Fx.updateTransition(this, false);
  }

  @Override
  public void onCancel() {

  }

  @Override
  public void onError() {
    opt = null;
    Log.d(getClass().getSimpleName(), "onError: bye :(");
    final Intent returnIntent = new Intent();
    Log.d(getClass().getSimpleName(), "Send result");
    setResult(RESULT_CANCELED, returnIntent);
    onBackPressed();
    Fx.updateTransition(this, false);
  }
}
