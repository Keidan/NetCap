package org.kei.android.phone.netcap.listview;

import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.capture.CaptureFile;
import org.kei.android.phone.jni.net.capture.ICapture;
import org.kei.android.phone.jni.net.capture.PCAPPacketHeader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;

/**
 *******************************************************************************
 * @file AsyncListViewLoader.java
 * @author Keidan
 * @date 20/09/2015
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
public class CaptureListViewLoader extends AsyncTask<Void, Void, Void> implements ICapture {
  private final CaptureFile                    capture      = new CaptureFile();
  private String                               file         = null;
  private int                                  captureCount = 0;
  private Activity                             activity;
  private ListViewAdapter<CaptureListViewItem> adapter;
  private List<CaptureListViewItem>            items;
  private ProgressDialog                       dialog;
  
  public CaptureListViewLoader(final Activity activity,
      final ListViewAdapter<CaptureListViewItem> adapter, final String file) {
    this.file = file;
    this.activity = activity;
    this.adapter = adapter;
    items = new ArrayList<CaptureListViewItem>();
  }
  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    //dialog = ProgressDialog.show(activity, "", "Please wait");
    items.clear();
  }
  
  @Override
  protected void onPostExecute(Void result) {
    capture.close();
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        adapter.setItemList(items);
        //dialog.dismiss();
      }
    });
    //dialog.dismiss();
  }
  @Override
  protected Void doInBackground(Void... arg0) {
    try {
      capture.open(file);
      while(capture.process(CaptureListViewLoader.this));
    } catch(Throwable e) {
      e.printStackTrace();
      captureEnd(null);
    }
    return null;
  }

  
  @Override
  public void captureProcess(final CaptureFile capture,
      final PCAPPacketHeader pheader, final byte[] buffer) {
    final CaptureListViewItem item = new CaptureListViewItem();
    ++captureCount;
    item.setId(""+captureCount);
    item.setTime(pheader.getTime());
    String info = NetworkHelper.getColorsProtocolAndDesc(buffer, '|');
    int tcolor = Color.BLACK, bcolor = Color.WHITE;
    int idx = info.indexOf('|');
    if(idx != -1) {
      tcolor = Integer.parseInt(info.substring(0, idx));
      info = info.substring(idx + 1);
      idx = info.indexOf('|');
      if(idx != -1) {
        bcolor = Integer.parseInt(info.substring(0, idx));
        info = info.substring(idx + 1);
        idx = info.indexOf('|');
        if(idx != -1) {
          item.setProtocol(info.substring(0, idx));
          info = info.substring(idx + 1);
        }
      }
    }
    item.setTColor(tcolor);
    item.setBColor(bcolor);
    item.setInfo(info);
    item.setPheader(pheader);
    item.setLayer(buffer);
    items.add(item);
    /*if ((captureCount % 1000) == 0)
      handler.sendEmptyMessage(0);*/
  }
  
  @Override
  public void captureEnd(final CaptureFile capture) {
    captureCount = 0;
    capture.close();
  }
  
}
