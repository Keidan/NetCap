package org.kei.android.phone.netcap.listview;

import java.util.Locale;

import org.kei.android.phone.net.capture.PCAPPacketHeader;
import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.Payload;
import org.kei.android.phone.netcap.R;

import android.graphics.Color;
import android.view.View;
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
  public static final int  FILTER_BY_ID       = 1;
  public static final int  FILTER_BY_TIME     = 2;
  public static final int  FILTER_BY_PROTOCOL = 4;
  public static final int  FILTER_BY_INFO     = 8;
  public static final int  FILTER_BY_ALL      = 15;
  private PCAPPacketHeader pheader            = null;
  private Layer            layer              = null;
  private Payload          payload            = null;
  private String           id                 = "(null)";
  private String           time               = "(null)";
  private String           protocol           = "(null)";
  private String           info               = "(null)";
  private int              bcolor             = Color.WHITE;
  private int              tcolor             = Color.BLACK;
  
  @Override
  public void updateItem(final View layoutItem, final Object object) {
    final CaptureListViewItem me = (CaptureListViewItem)object;
    TextView idTV = (TextView)layoutItem.findViewById(R.id.idTV);
    TextView timeTV = (TextView)layoutItem.findViewById(R.id.timeTV);
    TextView protocolTV = (TextView)layoutItem.findViewById(R.id.protocolTV);
    TextView infoTV = (TextView)layoutItem.findViewById(R.id.infoTV);
    
    idTV.setText(me.getId());
    timeTV.setText(me.getTime());
    protocolTV.setText(me.getProtocol());
    infoTV.setText(me.getInfo());
    layoutItem.setBackgroundColor(bcolor);
    idTV.setTextColor(tcolor);
    timeTV.setTextColor(tcolor);
    protocolTV.setTextColor(tcolor);
    infoTV.setTextColor(tcolor);
  }
  
  public PCAPPacketHeader getPheader() {
    return pheader;
  }
  
  public void setPheader(final PCAPPacketHeader pheader) {
    this.pheader = pheader;
  }
  
  public Payload getPayload() {
    return payload;
  }

  public void setPayload(final Payload payload) {
    this.payload = payload;
  }

  public Layer getLayer() {
    return layer;
  }
  
  public void setLayer(final Layer layer) {
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
  
  public int getBColor() {
    return bcolor;
  }

  public void setBColor(int bcolor) {
    this.bcolor = bcolor;
  }

  public int getTColor() {
    return tcolor;
  }

  public void setTColor(int tcolor) {
    this.tcolor = tcolor;
  }

  @Override
  public boolean isFilterable(Object o, final int filterId, final String text) {
    CaptureListViewItem clvi = (CaptureListViewItem)o;
    Locale l = Locale.US;
    String t = text.toLowerCase(l);
    boolean b = false;
    if((filterId & FILTER_BY_ID) == FILTER_BY_ID)
      b = clvi.id.toLowerCase(l).contains(t);
    if(!b && (filterId & FILTER_BY_TIME) == FILTER_BY_TIME)
      b = clvi.time.toLowerCase(l).contains(t);
    if(!b && (filterId & FILTER_BY_PROTOCOL) == FILTER_BY_PROTOCOL)
      b = clvi.protocol.toLowerCase(l).contains(t);
    if(!b && (filterId & FILTER_BY_INFO) == FILTER_BY_INFO)
      b = clvi.info.toLowerCase(l).contains(t);
    return b;
  }
  
}
