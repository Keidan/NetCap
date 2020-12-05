package fr.ralala.netcap.ui.listview;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import fr.ralala.netcap.R;
import fr.ralala.netcap.net.capture.PCAPPacketHeader;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class CaptureListViewItem implements IListViewItem {
  public static final int FILTER_BY_ID = 1;
  public static final int FILTER_BY_TIME = 2;
  public static final int FILTER_BY_PROTOCOL = 4;
  public static final int FILTER_BY_INFO = 8;
  public static final int FILTER_BY_ALL = 15;
  private PCAPPacketHeader mPheader = null;
  private Layer mLayer = null;
  private Payload mPayload = null;
  private String mId = "(null)";
  private String mTime = "(null)";
  private String mProtocol = "(null)";
  private String mInfo = "(null)";
  private int mBcolor = Color.WHITE;
  private int mTcolor = Color.BLACK;

  @Override
  public void updateItem(final View layoutItem, final Object object) {
    final CaptureListViewItem me = (CaptureListViewItem) object;
    TextView idTV = layoutItem.findViewById(R.id.tvId);
    TextView timeTV = layoutItem.findViewById(R.id.tvTime);
    TextView protocolTV = layoutItem.findViewById(R.id.tvProtocol);
    TextView infoTV = layoutItem.findViewById(R.id.tvInfo);

    idTV.setText(me.getId());
    timeTV.setText(me.getTime());
    protocolTV.setText(me.getProtocol());
    infoTV.setText(me.getInfo());
    layoutItem.setBackgroundColor(mBcolor);
    idTV.setTextColor(mTcolor);
    timeTV.setTextColor(mTcolor);
    protocolTV.setTextColor(mTcolor);
    infoTV.setTextColor(mTcolor);
  }

  public PCAPPacketHeader getPheader() {
    return mPheader;
  }

  public void setPheader(final PCAPPacketHeader pheader) {
    mPheader = pheader;
  }

  public Payload getPayload() {
    return mPayload;
  }

  public void setPayload(final Payload payload) {
    mPayload = payload;
  }

  public Layer getLayer() {
    return mLayer;
  }

  public void setLayer(final Layer layer) {
    mLayer = layer;
  }

  public String getId() {
    return mId;
  }

  public void setId(String id) {
    mId = id;
  }

  public String getTime() {
    return mTime;
  }

  public void setTime(String time) {
    mTime = time;
  }

  public String getProtocol() {
    return mProtocol;
  }

  public void setProtocol(String protocol) {
    mProtocol = protocol;
  }

  public String getInfo() {
    return mInfo;
  }

  public void setInfo(String info) {
    mInfo = info;
  }

  public int getBColor() {
    return mBcolor;
  }

  public void setBColor(int bcolor) {
    mBcolor = bcolor;
  }

  public int getTColor() {
    return mTcolor;
  }

  public void setTColor(int tcolor) {
    mTcolor = tcolor;
  }

  @Override
  public boolean isFilterable(Object o, final int filterId, final String text) {
    CaptureListViewItem clvi = (CaptureListViewItem) o;
    Locale l = Locale.US;
    String t = text.toLowerCase(l);
    boolean b = false;
    if ((filterId & FILTER_BY_ID) == FILTER_BY_ID)
      b = clvi.mId.toLowerCase(l).contains(t);
    if (!b && (filterId & FILTER_BY_TIME) == FILTER_BY_TIME)
      b = clvi.mTime.toLowerCase(l).contains(t);
    if (!b && (filterId & FILTER_BY_PROTOCOL) == FILTER_BY_PROTOCOL)
      b = clvi.mProtocol.toLowerCase(l).contains(t);
    if (!b && (filterId & FILTER_BY_INFO) == FILTER_BY_INFO)
      b = clvi.mInfo.toLowerCase(l).contains(t);
    return b;
  }

}
