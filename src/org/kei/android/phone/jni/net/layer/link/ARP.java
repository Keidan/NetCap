package org.kei.android.phone.jni.net.layer.link;

import java.util.List;

import org.kei.android.phone.jni.net.layer.Layer;

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
  public static final int REQUEST = 1;
  public static final int REPLY   = 2;
  
  private int    formatOfHardwareAddress = 0;
  private int    formatOfProtocolAddress = 0;
  private int    lengthOfHardwareAddress = 0;
  private int    lengthOfProtocolAddress = 0;
  private int    opcode                  = 0;
  private String senderHardwareAddress   = null;
  private String senderIPAddress         = null;
  private String targetHardwareAddress   = null;
  private String targetIPAddress         = null;
  
  public ARP() {
    super(TYPE_ARP);
  }
  
  @Override
  public String getFullName() {
    return "Address Resolution Protocol";
  }

  @Override
  public String getProtocolText() {
    return "ARP";
  }

  @Override
  public String getDescriptionText() {
    if(getOpcode() == REQUEST)
      return "Who is " + getTargetIPAddress() + "?";
    else if(getOpcode() == REPLY)
      return getSenderIPAddress() + " is " + getSenderHardwareAddress();
    else
      return "Unknown";
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Hardware type: 0x" + String.format("%04x", getFormatOfHardwareAddress()));
    lines.add("  Protocol type: 0x" + String.format("%04x", getFormatOfProtocolAddress()));
    lines.add("  Hardware size: " + String.format("%x", getLengthOfHardwareAddress()));
    lines.add("  Protocol size: " + String.format("%x", getLengthOfProtocolAddress()));
    lines.add("  Opcode: " + (getOpcode() == REPLY ? "reply" : (getOpcode() == REQUEST ? "request" : "unknown")));
    if(getSenderHardwareAddress() != null) {
      lines.add("  Sender MAC address: " + getSenderHardwareAddress());
      lines.add("  Sender IP address: " + getSenderIPAddress());
      lines.add("  Target MAC address: " + getTargetHardwareAddress());
      lines.add("  Target IP address: " + getTargetIPAddress());
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
