package fr.ralala.netcap.net.layer.link;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
public class ARP extends Layer {
  public static final int REQUEST = 1;
  public static final int REPLY = 2;

  private int mFormatOfHardwareAddress = 0;
  private int mFormatOfProtocolAddress = 0;
  private int mLengthOfHardwareAddress = 0;
  private int mLengthOfProtocolAddress = 0;
  private int mOpcode = 0;
  private String mSenderHardwareAddress = null;
  private String mSenderIPAddress = null;
  private String mTargetHardwareAddress = null;
  private String mTargetIPAddress = null;
  private int mHeaderLength = 0;

  public ARP() {
    super();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("ARP", "Address Resolution Protocol (" + (getOpcode() == REPLY ? "reply" : (getOpcode() == REQUEST ? "request" : "unknown")) + ")");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    if (getOpcode() == REQUEST) {
      String s = "Who has " + getTargetIPAddress() + "? ";
      if (getSenderIPAddress() != null) s += "Tell " + getSenderIPAddress();
      return s;
    } else if (getOpcode() == REPLY)
      return getSenderIPAddress() + " is at " + getSenderHardwareAddress();
    else
      return "Unknown";
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Hardware type: 0x" + String.format(Locale.US, "%04x", getFormatOfHardwareAddress()));
    lines.add("  Protocol type: 0x" + String.format(Locale.US, "%04x", getFormatOfProtocolAddress()));
    lines.add("  Hardware size: " + String.format(Locale.US, "%x", getLengthOfHardwareAddress()));
    lines.add("  Protocol size: " + String.format(Locale.US, "%x", getLengthOfProtocolAddress()));
    lines.add("  Opcode: " + (getOpcode() == REPLY ? "reply" : (getOpcode() == REQUEST ? "request" : "unknown")) + " (0x" + String.format(Locale.US, "%04x", getOpcode()) + ")");
    if (getSenderHardwareAddress() != null) {
      lines.add("  Sender MAC address: " + getSenderHardwareAddress());
      lines.add("  Sender IP address: " + getSenderIPAddress());
      lines.add("  Target MAC address: " + getTargetHardwareAddress());
      lines.add("  Target IP address: " + getTargetIPAddress());
    }
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
    int offset = 0;
    byte[] temp2 = new byte[2];
    byte[] temp4 = new byte[4];
    byte[] temp6 = new byte[6];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mFormatOfHardwareAddress = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mFormatOfProtocolAddress = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    mLengthOfHardwareAddress = buffer[offset++];
    mLengthOfProtocolAddress = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mOpcode = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;

    if ((mOpcode == 1 || mOpcode == 2) && mLengthOfProtocolAddress == 4) {
      System.arraycopy(buffer, offset, temp6, 0, temp6.length);
      mSenderHardwareAddress = String.format(Locale.US, "%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
      offset += temp6.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset += temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        mSenderIPAddress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        mSenderIPAddress = e.getMessage();
      }

      System.arraycopy(buffer, offset, temp6, 0, temp6.length);
      mTargetHardwareAddress = String.format(Locale.US, "%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
      offset += temp6.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset += temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        mTargetIPAddress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        mTargetIPAddress = e.getMessage();
      }
    }
    mHeaderLength = offset;
    byte[] sub_buffer = resizeBuffer(buffer);
    if (sub_buffer != null) {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  /**
   * @return the formatOfHardwareAddress
   */
  public int getFormatOfHardwareAddress() {
    return mFormatOfHardwareAddress;
  }

  /**
   * @param formatOfHardwareAddress the formatOfHardwareAddress to set
   */
  public void setFormatOfHardwareAddress(final int formatOfHardwareAddress) {
    mFormatOfHardwareAddress = formatOfHardwareAddress;
  }

  /**
   * @return the formatOfProtocolAddress
   */
  public int getFormatOfProtocolAddress() {
    return mFormatOfProtocolAddress;
  }

  /**
   * @param formatOfProtocolAddress the formatOfProtocolAddress to set
   */
  public void setFormatOfProtocolAddress(final int formatOfProtocolAddress) {
    mFormatOfProtocolAddress = formatOfProtocolAddress;
  }

  /**
   * @return the lengthOfHardwareAddress
   */
  public int getLengthOfHardwareAddress() {
    return mLengthOfHardwareAddress;
  }

  /**
   * @param lengthOfHardwareAddress the lengthOfHardwareAddress to set
   */
  public void setLengthOfHardwareAddress(final int lengthOfHardwareAddress) {
    mLengthOfHardwareAddress = lengthOfHardwareAddress;
  }

  /**
   * @return the lengthOfProtocolAddress
   */
  public int getLengthOfProtocolAddress() {
    return mLengthOfProtocolAddress;
  }

  /**
   * @param lengthOfProtocolAddress the lengthOfProtocolAddress to set
   */
  public void setLengthOfProtocolAddress(final int lengthOfProtocolAddress) {
    mLengthOfProtocolAddress = lengthOfProtocolAddress;
  }

  /**
   * @return the Opcode
   */
  public int getOpcode() {
    return mOpcode;
  }

  /**
   * @param opcode the Opcode to set
   */
  public void setOpcode(final int opcode) {
    mOpcode = opcode;
  }

  /**
   * @return the senderHardwareAddress
   */
  public String getSenderHardwareAddress() {
    return mSenderHardwareAddress;
  }

  /**
   * @param senderHardwareAddress the senderHardwareAddress to set
   */
  public void setSenderHardwareAddress(final String senderHardwareAddress) {
    mSenderHardwareAddress = senderHardwareAddress;
  }

  /**
   * @return the senderIPAddress
   */
  public String getSenderIPAddress() {
    return mSenderIPAddress;
  }

  /**
   * @param senderIPAddress the senderIPAddress to set
   */
  public void setSenderIPAddress(final String senderIPAddress) {
    mSenderIPAddress = senderIPAddress;
  }

  /**
   * @return the targetHardwareAddress
   */
  public String getTargetHardwareAddress() {
    return mTargetHardwareAddress;
  }

  /**
   * @param targetHardwareAddress the targetHardwareAddress to set
   */
  public void setTargetHardwareAddress(final String targetHardwareAddress) {
    mTargetHardwareAddress = targetHardwareAddress;
  }

  /**
   * @return the targetIPAddress
   */
  public String getTargetIPAddress() {
    return mTargetIPAddress;
  }

  /**
   * @param targetIPAddress the targetIPAddress to set
   */
  public void setTargetIPAddress(final String targetIPAddress) {
    mTargetIPAddress = targetIPAddress;
  }

}
