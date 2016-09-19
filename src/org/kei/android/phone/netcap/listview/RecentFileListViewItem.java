package org.kei.android.phone.netcap.listview;

import java.io.File;
import java.util.Locale;

import org.kei.android.phone.netcap.R;

import android.view.View;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file RecentFileListViewItem.java
 * @author Keidan
 * @date 27/01/2016
 * @par Project NetCap
 *
 * @par Copyright 2016 Keidan, all right reserved
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
public class RecentFileListViewItem implements IListViewItem {

  private static final int SIZE_1KB = 0x400;
  private static final int SIZE_1MB = 0x100000;
  private static final int SIZE_1GB = 0x40000000;
  private long             time     = 0L;
  private String           file     = null;
  private String           filename = null;
  private String           key      = null;

  @Override
  public void updateItem(final View layoutItem, final Object object) {
    final RecentFileListViewItem me = (RecentFileListViewItem) object;
    final TextView text1 = (TextView) layoutItem.findViewById(R.id.firstLine);
    final TextView text2 = (TextView) layoutItem.findViewById(R.id.secondLine);
    // text1.setTextSize(15);
    final File f = new File(me.getFile());
    text1.setText(f.getName());
    text2.setText(convertToHuman(f.length()));
  }

  @Override
  public boolean isFilterable(final Object o, final int filterId, final String text) {
    return true;
  }

  /**
   * @return the time
   */
  public long getTime() {
    return time;
  }

  /**
   * @param time
   *          the time to set
   */
  public void setTime(final long time) {
    this.time = time;
  }

  /**
   * @return the file
   */
  public String getFile() {
    return file;
  }

  /**
   * @param file
   *          the file to set
   */
  public void setFile(final String file) {
    this.file = file;
  }

  /**
   * @return the fielname
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @param filename
   *          the filename to set
   */
  public void setFilename(final String filename) {
    this.filename = filename;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key
   *          the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  private static String convertToHuman(long l) {
    final double d = l;
    String sf = "";
    if (d < 0)
      sf = "0";
    else if (d < SIZE_1KB)
      sf = String.format(Locale.US, "%do", (int) d);
    else if (d < SIZE_1MB)
      sf = String.format("%d", (int) (d / SIZE_1KB)) + " Ko";
    else if (d < SIZE_1GB)
      sf = String.format("%d", (int) (d / SIZE_1MB)) + " Mo";
    else
      sf = String.format("%d", (int) (d / SIZE_1GB)) + " Go";
    return sf;
  }
}
