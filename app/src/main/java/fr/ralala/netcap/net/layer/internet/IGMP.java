package fr.ralala.netcap.net.layer.internet;

import android.graphics.Color;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class IGMP extends Layer {
  public static final int QUERY = 0x11;
  public static final int REPORT_V1 = 0x12;
  public static final int REPORT_V2 = 0x16;
  public static final int REPORT_V3 = 0x22;
  public static final int LEAVE_GROUP = 0x17;

  private int mType = 0;
  private int mMaxRespTime = 0;
  private int mChecksum = 0;
  private String mGroupAddress = "0.0.0.0";
  private int mResv = 0;
  private boolean mS = false;
  private int mQrv = 0;
  private int mQqic = 0;
  private int mNumberOfSources = 0;
  private final List<String> mSourceAddress;
  private int headerLength = 0;

  public IGMP() {
    super();
    mSourceAddress = new ArrayList<>();
    mBackground = Color.parseColor("#FFF3D6");
    mForeground = Color.BLACK;
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    String text;
    switch (getType()) {
      case REPORT_V1:
        text = "IGMPv1";
      case REPORT_V2:
      case QUERY:
        text = "IGMPv2";
      case REPORT_V3:
        text = "IGMPv3";
      default:
        text = "IGMP";
    }
    return new Protocol(text, "Internet Group Management Protocol");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    switch (getType()) {
      case QUERY:
        return "Membership Query, general";
      case REPORT_V2:
        return "Membership Report group " + getGroupAddress();
      case REPORT_V3:
        return "Membership Report groups " + getNumberOfSources();
      default:
        return "Membership Unknown";
    }
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    if (getType() == QUERY)
      lines.add("  Type: Membership Query (0x" + String.format(Locale.US, "%02x", getType()) + ")");
    else
      lines.add("  Type: Membership Report (0x" + String.format(Locale.US, "%02x", getType()) + ")");
    lines.add("  Max Response Time: " + ((float) (getMaxRespTime() / 10)) + " sec (0x" + String.format(Locale.US, "%02x", getMaxRespTime()) + ")");
    lines.add("  Header checksum: 0x" + String.format(Locale.US, "%04x", getChecksum()));
    lines.add("  Multicast address: " + getGroupAddress());
    if (getNumberOfSources() != 0) {
      lines.add("  Num Src: " + getNumberOfSources());
      for (String s : getSourceAddress()) lines.add("  Source Address: " + s);
    }
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return headerLength;
  }

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    int tos = 0;
    if (owner instanceof IPv4)
      tos = ((IPv4) owner).getTOS();
    else if (owner instanceof IPv6)
      tos = ((IPv6) owner).getFlowLbl_1();
    int offset = 0;
    byte[] temp2 = new byte[2];
    byte[] temp4 = new byte[4];

    mType = buffer[offset++];
    if (tos == 0xc0 && mType != QUERY) {
      mMaxRespTime = buffer[offset++];
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      mChecksum = NetworkHelper.ntohs2(temp2);
      offset += temp2.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset += temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        mGroupAddress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        mGroupAddress = e.getMessage();
      }


      mResv = (byte) (buffer[offset++] & 0x0f);
      int n = buffer[offset++];
      mS = BigInteger.valueOf(n).testBit(4);
      n = BigInteger.valueOf(n).clearBit(4).intValue();
      mQrv = n;
      mQqic = buffer[offset++];
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      mNumberOfSources = NetworkHelper.ntohs2(temp2);
      offset += temp2.length;
      for (int i = 0; i < mNumberOfSources; i++) {
        System.arraycopy(buffer, offset, temp4, 0, temp4.length);
        offset += temp4.length;
        try {
          InetAddress address = InetAddress.getByAddress(temp4);
          addSourceAddress(address.getHostAddress());
        } catch (UnknownHostException e) {
          e.printStackTrace();
          addSourceAddress(e.getMessage());
        }
      }
    } else if (mType == REPORT_V3) {
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      mChecksum = NetworkHelper.ntohs2(temp2);
      offset += temp2.length;
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      mNumberOfSources = NetworkHelper.ntohs2(temp2);
      offset += temp2.length;
      for (int i = 0; i < mNumberOfSources; i++) {
        System.arraycopy(buffer, offset, temp4, 0, temp4.length);
        offset += temp4.length;
        try {
          InetAddress address = InetAddress.getByAddress(temp4);
          addSourceAddress(address.getHostAddress());
        } catch (UnknownHostException e) {
          e.printStackTrace();
          addSourceAddress(e.getMessage());
        }
      }
    } else {
      mMaxRespTime = buffer[offset++];
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      mChecksum = NetworkHelper.ntohs2(temp2);
      offset += temp2.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset += temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        mGroupAddress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        mGroupAddress = e.getMessage();
      }
    }
    headerLength = offset;
    byte[] sub_buffer = resizeBuffer(buffer);
    if (sub_buffer != null) {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  public int getType() {
    return mType;
  }

  public void setType(final int type) {
    mType = type;
  }

  public int getMaxRespTime() {
    return mMaxRespTime;
  }

  public void setMaxRespTime(final int maxRespTime) {
    mMaxRespTime = maxRespTime;
  }

  public int getChecksum() {
    return mChecksum;
  }

  public void setChecksum(final int checksum) {
    mChecksum = checksum;
  }

  public String getGroupAddress() {
    return mGroupAddress;
  }

  public void setGroupAddress(final String groupAddress) {
    mGroupAddress = groupAddress;
  }

  public int getResv() {
    return mResv;
  }

  public void setResv(final int resv) {
    mResv = resv;
  }

  public boolean isS() {
    return mS;
  }

  public void setS(final boolean s) {
    mS = s;
  }

  public int getQRV() {
    return mQrv;
  }

  public void setQRV(final int qrv) {
    mQrv = qrv;
  }

  public int getQQIC() {
    return mQqic;
  }

  public void setQQIC(final int qqic) {
    mQqic = qqic;
  }

  public int getNumberOfSources() {
    return mNumberOfSources;
  }

  public void setNumberOfSources(final int numberOfSources) {
    mNumberOfSources = numberOfSources;
  }

  public List<String> getSourceAddress() {
    return mSourceAddress;
  }

  public void addSourceAddress(final String sourceAddress) {
    mSourceAddress.add(sourceAddress);
  }

}
