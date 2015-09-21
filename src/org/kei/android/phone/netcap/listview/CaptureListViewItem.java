package org.kei.android.phone.netcap.listview;

import org.kei.android.phone.jni.net.capture.PCAPPacketHeader;
import org.kei.android.phone.netcap.R;

import android.widget.LinearLayout;
import android.widget.TextView;
/**
 *******************************************************************************
 * @file CaptureListViewItem.java
 * @author Keidan
 * @date 11/09/2015
 * @par Project NetCap
 *
 * @par  
 *      Copyright 2015 Keidan, all right reserved
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
public class CaptureListViewItem implements IListViewItem {
  private PCAPPacketHeader pheader   = null;
  private byte[]           layer     = null;
  private String           id        = "(null)";
  private String           time      = "(null)";
  private String           protocol  = "(null)";
  private String           info      = "(null)";
  

  @Override
  public void updateItem(final LinearLayout layoutItem, final Object object) {
    final CaptureListViewItem me = (CaptureListViewItem)object;
    final TextView idTV = (TextView)layoutItem.findViewById(R.id.idTV);
    final TextView timeTV = (TextView)layoutItem.findViewById(R.id.timeTV);
    final TextView protocolTV = (TextView)layoutItem.findViewById(R.id.protocolTV);
    final TextView infoTV = (TextView)layoutItem.findViewById(R.id.infoTV);
    idTV.setText(me.getId());
    timeTV.setText(me.getTime());
    protocolTV.setText(me.getProtocol());
    infoTV.setText(me.getInfo());
  }
  
  public PCAPPacketHeader getPheader() {
    return pheader;
  }
  
  public void setPheader(final PCAPPacketHeader pheader) {
    this.pheader = pheader;
  }
  
  public byte[] getLayer() {
    return layer;
  }
  
  public void setLayer(final byte[] layer) {
    this.layer = layer;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }
  
}
