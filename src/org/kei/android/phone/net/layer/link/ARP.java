package org.kei.android.phone.net.layer.link;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.kei.android.phone.net.NetworkHelper;
import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.Payload;

/**
 *******************************************************************************
 * @file java
 * @author Keidan
 * @date 07/09/2015
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
public class ARP extends Layer {
  public static final int REQUEST                 = 1;
  public static final int REPLY                   = 2;
  
  private int             formatOfHardwareAddress = 0;
  private int             formatOfProtocolAddress = 0;
  private int             lengthOfHardwareAddress = 0;
  private int             lengthOfProtocolAddress = 0;
  private int             opcode                  = 0;
  private String          senderHardwareAddress   = null;
  private String          senderIPAddress         = null;
  private String          targetHardwareAddress   = null;
  private String          targetIPAddress         = null;
  private int             headerLength            = 0;
  
  public ARP() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Address Resolution Protocol (" + (getOpcode() == REPLY ? "reply" : (getOpcode() == REQUEST ? "request" : "unknown")) + ")";
  }

  @Override
  public String getProtocolText() {
    return "ARP";
  }

  @Override
  public String getDescriptionText() {
    if(getOpcode() == REQUEST) {
      String s = "Who has " + getTargetIPAddress() + "? ";
      if(getSenderIPAddress() != null) s += "Tell " + getSenderIPAddress();
      return s;
    } else if(getOpcode() == REPLY)
      return getSenderIPAddress() + " is at " + getSenderHardwareAddress();
    else
      return "Unknown";
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Hardware type: 0x" + String.format("%04x", getFormatOfHardwareAddress()));
    lines.add("  Protocol type: 0x" + String.format("%04x", getFormatOfProtocolAddress()));
    lines.add("  Hardware size: " + String.format("%x", getLengthOfHardwareAddress()));
    lines.add("  Protocol size: " + String.format("%x", getLengthOfProtocolAddress()));
    lines.add("  Opcode: " + (getOpcode() == REPLY ? "reply" : (getOpcode() == REQUEST ? "request" : "unknown")) + " (0x" + String.format("%04x", getOpcode()) + ")");
    if(getSenderHardwareAddress() != null) {
      lines.add("  Sender MAC address: " + getSenderHardwareAddress());
      lines.add("  Sender IP address: " + getSenderIPAddress());
      lines.add("  Target MAC address: " + getTargetHardwareAddress());
      lines.add("  Target IP address: " + getTargetIPAddress());
    }
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    int offset = 0;
    byte temp2 [] = new byte[2];
    byte temp4 [] = new byte[4];
    byte temp6 [] = new byte[6];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    formatOfHardwareAddress = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    formatOfProtocolAddress = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    lengthOfHardwareAddress = buffer[offset++];
    lengthOfProtocolAddress = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    opcode = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    
    if((opcode == 1 || opcode == 2) && lengthOfProtocolAddress == 4) {
      System.arraycopy(buffer, offset, temp6, 0, temp6.length);
      senderHardwareAddress = String.format("%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
      offset+=temp6.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset+=temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        senderIPAddress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        senderIPAddress = e.getMessage();
      }
      
      System.arraycopy(buffer, offset, temp6, 0, temp6.length);
      targetHardwareAddress = String.format("%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
      offset+=temp6.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset+=temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        targetIPAddress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        targetIPAddress = e.getMessage();
      }
    }
    headerLength = offset;
    byte [] sub_buffer = resizeBuffer(buffer);
    if(sub_buffer != null) {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  /**
   * @return the formatOfHardwareAddress
   */
  public int getFormatOfHardwareAddress() {
    return formatOfHardwareAddress;
  }

  /**
   * @param formatOfHardwareAddress
   *          the formatOfHardwareAddress to set
   */
  public void setFormatOfHardwareAddress(final int formatOfHardwareAddress) {
    this.formatOfHardwareAddress = formatOfHardwareAddress;
  }

  /**
   * @return the formatOfProtocolAddress
   */
  public int getFormatOfProtocolAddress() {
    return formatOfProtocolAddress;
  }

  /**
   * @param formatOfProtocolAddress
   *          the formatOfProtocolAddress to set
   */
  public void setFormatOfProtocolAddress(final int formatOfProtocolAddress) {
    this.formatOfProtocolAddress = formatOfProtocolAddress;
  }

  /**
   * @return the lengthOfHardwareAddress
   */
  public int getLengthOfHardwareAddress() {
    return lengthOfHardwareAddress;
  }

  /**
   * @param lengthOfHardwareAddress
   *          the lengthOfHardwareAddress to set
   */
  public void setLengthOfHardwareAddress(final int lengthOfHardwareAddress) {
    this.lengthOfHardwareAddress = lengthOfHardwareAddress;
  }

  /**
   * @return the lengthOfProtocolAddress
   */
  public int getLengthOfProtocolAddress() {
    return lengthOfProtocolAddress;
  }

  /**
   * @param lengthOfProtocolAddress
   *          the lengthOfProtocolAddress to set
   */
  public void setLengthOfProtocolAddress(final int lengthOfProtocolAddress) {
    this.lengthOfProtocolAddress = lengthOfProtocolAddress;
  }

  /**
   * @return the Opcode
   */
  public int getOpcode() {
    return opcode;
  }

  /**
   * @param Opcode
   *          the Opcode to set
   */
  public void setOpcode(final int opcode) {
    this.opcode = opcode;
  }
  
  /**
   * @return the senderHardwareAddress
   */
  public String getSenderHardwareAddress() {
    return senderHardwareAddress;
  }
  
  /**
   * @param senderHardwareAddress
   *          the senderHardwareAddress to set
   */
  public void setSenderHardwareAddress(final String senderHardwareAddress) {
    this.senderHardwareAddress = senderHardwareAddress;
  }
  
  /**
   * @return the senderIPAddress
   */
  public String getSenderIPAddress() {
    return senderIPAddress;
  }
  
  /**
   * @param senderIPAddress
   *          the senderIPAddress to set
   */
  public void setSenderIPAddress(final String senderIPAddress) {
    this.senderIPAddress = senderIPAddress;
  }
  
  /**
   * @return the targetHardwareAddress
   */
  public String getTargetHardwareAddress() {
    return targetHardwareAddress;
  }
  
  /**
   * @param targetHardwareAddress
   *          the targetHardwareAddress to set
   */
  public void setTargetHardwareAddress(final String targetHardwareAddress) {
    this.targetHardwareAddress = targetHardwareAddress;
  }
  
  /**
   * @return the targetIPAddress
   */
  public String getTargetIPAddress() {
    return targetIPAddress;
  }
  
  /**
   * @param targetIPAddress
   *          the targetIPAddress to set
   */
  public void setTargetIPAddress(final String targetIPAddress) {
    this.targetIPAddress = targetIPAddress;
  }

}
