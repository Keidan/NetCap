package org.kei.android.phone.netcap.chooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kei.android.phone.netcap.R;
import org.kei.android.phone.netcap.utils.Fx;
import org.kei.android.phone.netcap.utils.Tools;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

/**
 *******************************************************************************
 * @file FileChooser.java
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
public class FileChooser extends ListActivity {
  public static final String            FILECHOOSER_TYPE_KEY                = "type";
  public static final String            FILECHOOSER_TITLE_KEY               = "title";
  public static final String            FILECHOOSER_MESSAGE_KEY             = "message";
  public static final int               FILECHOOSER_TYPE_FILE_ONLY          = 0;
  public static final int               FILECHOOSER_TYPE_DIRECTORY_ONLY     = 1;
  public static final int               FILECHOOSER_TYPE_FILE_AND_DIRECTORY = 2;
  protected static final File           DEFAULT_ROOT                        = Environment.getExternalStoragePublicDirectory(
                                                                                      Environment.DIRECTORY_DOWNLOADS);
  protected File                        currentDir                          = null;
  private FileArrayAdapter              adapter                             = null;
  private String                        confirmMessage                      = null;
  private String                        confirmTitle                      = null;
  private int                           type                                = FILECHOOSER_TYPE_FILE_AND_DIRECTORY;
  private final OnItemLongClickListener longClick                           = new OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(
        final AdapterView<?> parent, final View v, final int position, final long id) {
      final Option o = adapter.getItem(position);
      if (o.getPath() == null)
        return false;
      boolean folder = false;
      if (o.getData().equalsIgnoreCase("folder"))
        folder = true;
      if (!o.getData().equalsIgnoreCase("parent directory")) {
        if (folder && type == FILECHOOSER_TYPE_FILE_ONLY)
          return false;
        if (!folder && type == FILECHOOSER_TYPE_DIRECTORY_ONLY)
          return false;
        final android.view.View.OnClickListener yes = new android.view.View.OnClickListener() {
          @Override
          public void onClick(
              android.view.View view) {
            onFileSelected(o);
          }
        };
        final android.view.View.OnClickListener no = new android.view.View.OnClickListener() {
          @Override
          public void onClick(
              android.view.View view) {
            onFileSelected(null);
          }
        };
        Tools.showConfirmDialog(
                FileChooser.this,
                confirmTitle, confirmMessage + "\n" + o.getPath(),
                yes,
                no);

      }
      return true;
    }
  };

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle b = getIntent().getExtras();
    if (b != null && b.containsKey(FILECHOOSER_TYPE_KEY))
      type = Integer.parseInt(b.getString(FILECHOOSER_TYPE_KEY));
    if (b != null && b.containsKey(FILECHOOSER_TITLE_KEY))
      confirmTitle = b.getString(FILECHOOSER_TITLE_KEY);
    if (b != null && b.containsKey(FILECHOOSER_MESSAGE_KEY))
      confirmMessage = b.getString(FILECHOOSER_MESSAGE_KEY);
    
    if(confirmTitle == null) confirmTitle = "title";
    if(confirmMessage == null) confirmMessage = "message";
    currentDir = DEFAULT_ROOT;
    getListView().setLongClickable(true);
    fill(currentDir);
    this.getListView().setOnItemLongClickListener(longClick);
  }

  protected void fill(final File f) {
    final File[] dirs = f.listFiles();
    this.setTitle(f.getAbsolutePath());
    final List<Option> dir = new ArrayList<Option>();
    final List<Option> fls = new ArrayList<Option>();
    try {
      for (final File ff : dirs) {
        if (ff.isDirectory())
          dir.add(new Option(ff.getName(), "Folder", ff.getAbsolutePath(), Fx.getDrawable(this, R.drawable.folder)));
        else {
          fls.add(new Option(ff.getName(), "File Size: " + ff.length(), ff
              .getAbsolutePath(), Fx.getDrawable(this, R.drawable.file)));
        }
      }
    } catch (final Exception e) {

    }
    Collections.sort(dir);
    Collections.sort(fls);
    dir.addAll(fls);
    dir.add(
        0,
        new Option("..", "Parent Directory", f.getParent(), Fx.getDrawable(this, R.drawable.folder)));
    // if(adapter == null) {
    adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view, dir);
    this.setListAdapter(adapter);
    // } else
    // adapter.reload(dir);
  }

  @Override
  protected void onListItemClick(final ListView l, final View v,
      final int position, final long id) {
    super.onListItemClick(l, v, position, id);
    final Option o = adapter.getItem(position);
    if (o.getPath() == null)
      return;
    if (o.getData().equalsIgnoreCase("folder")
        || o.getData().equalsIgnoreCase("parent directory")) {
      currentDir = new File(o.getPath());
      fill(currentDir);
    }
  }

  protected void onFileSelected(final Option opt) {
  }
}
