package fr.ralala.netcap.net.layer.application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class DHCPv4 extends Layer {
  public static final int PORT_SRV = 67;
  public static final int PORT_CLI = 68;
  public static final int REQUEST = 1;
  public static final int REPLY = 2;
  private int mHeaderLength;
  private byte[] mOptionBytes;
  private int mOpcode;
  private int mHtype;
  private int mHlen;
  private int mHops;
  private int mId;
  private int mSecs;
  private int mFlags;
  private String mCiaddr;
  private String mYiaddr;
  private String mSiaddr;
  private String mGiaddr;
  private String mChaddr;
  private String mChaddrPadding;
  private String mSname;
  private String mFile;

  public DHCPv4() {
    super();
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Message type: Boot " + (getOpcode() == REPLY ? "Reply" : "Request") + " (" + getOpcode() + ")");
    lines.add("  Hardware type: 0x" + String.format(Locale.US, "%02x", getHType()));
    lines.add("  Hardware address length: " + getHLen());
    lines.add("  Hops: " + getHOps());
    lines.add("  Transaction id: 0x" + String.format(Locale.US, "%08x", getID()));
    lines.add("  Flags: 0x" + String.format(Locale.US, "%04x", getFlags()));
    lines.add("  Client IP address: " + getCIAddr());
    lines.add("  Your IP address: " + getYIAddr());
    lines.add("  Next server IP address: " + getSIAddr());
    lines.add("  Relay agent IP address: " + getGIAddr());
    lines.add("  Client MAC address: " + getCHAddr());
    lines.add("  Padding: " + getCHAddrPadding());
    lines.add("  Server host name: " + getSName());
    lines.add("  Boot file name: " + getFile());
    if (getOptionBytes() != null && getOptionBytes().length > 0) {
      lines.add("  Options: " + getOptionBytes().length + " bytes");
      List<String> l = NetworkHelper.formatBuffer(getOptionBytes());
      for (String s : l) lines.add("    " + s);
    }
  }

  private String padding(byte[] bytes) {
    StringBuilder s = new StringBuilder();
    for (byte b : bytes) s.append((int) b);
    return s.toString();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("DHCPv4", "Dynamic Host Configuration Protocol v4");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    return "DHCP " + (getOpcode() == REPLY ? "Reply" : "Request") + " - Transaction ID 0x" + String.format(Locale.US, "%04x", getID());
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return mHeaderLength;
  }

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    byte[] temp2 = new byte[2];
    byte[] temp4 = new byte[4];
    byte[] temp6 = new byte[6];
    byte[] temp10 = new byte[10];
    byte[] temp64 = new byte[64];
    byte[] temp128 = new byte[128];
    int offset = 0;

    mOpcode = buffer[offset++];
    mHtype = buffer[offset++];
    mHlen = buffer[offset++];
    mHops = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    mId = NetworkHelper.getInt(temp4, 0);
    offset += temp4.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mSecs = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mFlags = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset += temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      mCiaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      mCiaddr = ex.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset += temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      mYiaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      mYiaddr = ex.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset += temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      mSiaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      mSiaddr = ex.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset += temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      mGiaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      mGiaddr = ex.getMessage();
    }
    System.arraycopy(buffer, offset, temp6, 0, temp6.length);
    mChaddr = String.format(Locale.US, "%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
    offset += temp6.length;
    System.arraycopy(buffer, offset, temp10, 0, temp10.length);
    mChaddrPadding = padding(temp10);
    offset += temp10.length;
    System.arraycopy(buffer, offset, temp64, 0, temp64.length);
    mSname = new String(temp64);
    offset += temp64.length;
    System.arraycopy(buffer, offset, temp128, 0, temp128.length);
    mFile = new String(temp128);
    offset += temp128.length;

    int dif = Math.abs(offset - buffer.length);
    if (dif > 0) {
      mOptionBytes = new byte[dif];
      System.arraycopy(buffer, offset, mOptionBytes, 0, mOptionBytes.length);
      offset += mOptionBytes.length;
    } else
      mOptionBytes = new byte[0];
    mHeaderLength = offset;
  }

  /**
   * @return the optionBytes
   */
  public byte[] getOptionBytes() {
    return mOptionBytes;
  }

  /**
   * @param optionBytes the optionBytes to set
   */
  public void setOptionBytes(byte[] optionBytes) {
    mOptionBytes = optionBytes;
  }

  /**
   * @return the opcode
   */
  public int getOpcode() {
    return mOpcode;
  }

  /**
   * @param opcode the opcode to set
   */
  public void setOpcode(int opcode) {
    mOpcode = opcode;
  }

  /**
   * @return the htype
   */
  public int getHType() {
    return mHtype;
  }

  /**
   * @param htype the htype to set
   */
  public void setHType(int htype) {
    mHtype = htype;
  }

  /**
   * @return the hlen
   */
  public int getHLen() {
    return mHlen;
  }

  /**
   * @param hlen the hlen to set
   */
  public void setHLen(int hlen) {
    mHlen = hlen;
  }

  /**
   * @return the hops
   */
  public int getHOps() {
    return mHops;
  }

  /**
   * @param hops the hops to set
   */
  public void setHOps(int hops) {
    mHops = hops;
  }

  /**
   * @return the id
   */
  public int getID() {
    return mId;
  }

  /**
   * @param id the id to set
   */
  public void setID(int id) {
    mId = id;
  }

  /**
   * @return the secs
   */
  public int getSecs() {
    return mSecs;
  }

  /**
   * @param secs the secs to set
   */
  public void setSecs(int secs) {
    mSecs = secs;
  }

  /**
   * @return the flags
   */
  public int getFlags() {
    return mFlags;
  }

  /**
   * @param flags the flags to set
   */
  public void setFlags(int flags) {
    mFlags = flags;
  }

  /**
   * @return the ciaddr
   */
  public String getCIAddr() {
    return mCiaddr;
  }

  /**
   * @param ciaddr the ciaddr to set
   */
  public void setCIAddr(String ciaddr) {
    mCiaddr = ciaddr;
  }

  /**
   * @return the yiaddr
   */
  public String getYIAddr() {
    return mYiaddr;
  }

  /**
   * @param yiaddr the yiaddr to set
   */
  public void setYIAddr(String yiaddr) {
    mYiaddr = yiaddr;
  }

  /**
   * @return the siaddr
   */
  public String getSIAddr() {
    return mSiaddr;
  }

  /**
   * @param siaddr the siaddr to set
   */
  public void setSIAddr(String siaddr) {
    mSiaddr = siaddr;
  }

  /**
   * @return the giaddr
   */
  public String getGIAddr() {
    return mGiaddr;
  }

  /**
   * @param giaddr the giaddr to set
   */
  public void setGIAddr(String giaddr) {
    mGiaddr = giaddr;
  }

  /**
   * @return the chaddr
   */
  public String getCHAddr() {
    return mChaddr;
  }

  /**
   * @param chaddr the chaddr to set
   */
  public void setCHAddr(String chaddr) {
    mChaddr = chaddr;
  }

  /**
   * @return the chaddrPadding
   */
  public String getCHAddrPadding() {
    return mChaddrPadding;
  }

  /**
   * @param chaddrPadding the chaddrPadding to set
   */
  public void setCHAddrPadding(String chaddrPadding) {
    mChaddrPadding = chaddrPadding;
  }

  /**
   * @return the sname
   */
  public String getSName() {
    return mSname;
  }

  /**
   * @param sname the sname to set
   */
  public void setSName(String sname) {
    mSname = sname;
  }

  /**
   * @return the file
   */
  public String getFile() {
    return mFile;
  }

  /**
   * @param file the file to set
   */
  public void setFile(String file) {
    mFile = file;
  }

}
