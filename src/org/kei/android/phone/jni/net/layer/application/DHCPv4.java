package org.kei.android.phone.jni.net.layer.application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file DHCPv4.java
 * @author Keidan
 * @date 07/09/2015
 * @par Project
 * NetCap
 *
 * @par 
 * Copyright 2015 Keidan, all right reserved
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY.
 *
 * License summary : 
 *    You can modify and redistribute the sources code and binaries.
 *    You can send me the bug-fix
 *
 * Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class DHCPv4 extends Layer {
  public static final int PORT_SRV = 67;
  public static final int PORT_CLI = 68;
  public static final int REQUEST  = 1;
  public static final int REPLY    = 2;
  private int             headerLength;
  private byte[]          optionBytes;
  private int             opcode;
  private int             htype;
  private int             hlen;
  private int             hops;
  private int             id;
  private int             secs;
  private int             flags;
  private String          ciaddr;
  private String          yiaddr;
  private String          siaddr;
  private String          giaddr;
  private String          chaddr;
  private String          chaddrPadding;
  private String          sname;
  private String          file;
  
  public DHCPv4() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Dynamic Host Configuration Protocol v4";
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Message type: Boot " + (getOpcode() == REPLY ? "Reply" : "Request") + " (" + getOpcode() + ")");
    lines.add("  Hardware type: 0x" + String.format("%02x", getHType()));
    lines.add("  Hardware address length: " + getHLen());
    lines.add("  Hops: " + getHOps());
    lines.add("  Transaction id: 0x" + String.format("%08x", getID()));
    lines.add("  Flags: 0x" + String.format("%04x", getFlags()));
    lines.add("  Client IP address: " + getCIAddr());
    lines.add("  Your IP address: " + getYIAddr());
    lines.add("  Next server IP address: " + getSIAddr());
    lines.add("  Relay agent IP address: " + getGIAddr());
    lines.add("  Client MAC address: " + getCHAddr());
    lines.add("  Padding: " + getCHAddrPadding());
    lines.add("  Server host name: " + getSName());
    lines.add("  Boot file name: " + getFile());
    if(getOptionBytes() != null && getOptionBytes().length > 0) {
      lines.add("  Options: " + getOptionBytes().length + " bytes");
      List<String> l = NetworkHelper.formatBuffer(getOptionBytes());
      for(String s : l) lines.add("    " + s);
    }
  }
  
  private String padding(byte[] bytes) {
    String s = "";
    for(byte b : bytes) s += String.valueOf((int)b);
    return s;
  }

  @Override
  public String getProtocolText() {
    return "DHCPv4";
  }

  @Override
  public String getDescriptionText() {
    return "DHCP " + (getOpcode() == REPLY ? "Reply" : "Request") + " - Transaction ID 0x" + String.format("%04x", getID()); 
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    byte temp2 [] = new byte[2];
    byte temp4 [] = new byte[4];
    byte temp6 [] = new byte[6];
    byte temp10 [] = new byte[10];
    byte temp64 [] = new byte[64];
    byte temp128 [] = new byte[128];
    int offset = 0;
    
    opcode = buffer[offset++];
    htype = buffer[offset++];
    hlen = buffer[offset++];
    hops = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    id = NetworkHelper.getInt(temp4, 0);
    offset+=temp4.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    secs = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    flags = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset+= temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      ciaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      ciaddr = ex.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset+= temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      yiaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      yiaddr = ex.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset+= temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      siaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      siaddr = ex.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset+= temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      giaddr = address.getHostAddress();
    } catch (UnknownHostException ex) {
      ex.printStackTrace();
      giaddr = ex.getMessage();
    }
    System.arraycopy(buffer, offset, temp6, 0, temp6.length);
    chaddr = String.format("%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
    offset+= temp6.length;
    System.arraycopy(buffer, offset, temp10, 0, temp10.length);
    chaddrPadding = padding(temp10);
    offset+= temp10.length;
    System.arraycopy(buffer, offset, temp64, 0, temp64.length);
    sname = new String(temp64);
    offset+= temp64.length;
    System.arraycopy(buffer, offset, temp128, 0, temp128.length);
    file = new String(temp128);
    offset+= temp128.length;
    
    int dif = Math.abs(offset - buffer.length);
    if (dif > 0) {
      optionBytes = new byte[dif];
      System.arraycopy(buffer, offset, optionBytes, 0, optionBytes.length);
      offset+=optionBytes.length;
    } else
      optionBytes = new byte[0];
    headerLength = offset;
  }

  /**
   * @return the optionBytes
   */
  public byte[] getOptionBytes() {
    return optionBytes;
  }

  /**
   * @param optionBytes the optionBytes to set
   */
  public void setOptionBytes(byte[] optionBytes) {
    this.optionBytes = optionBytes;
  }

  /**
   * @return the opcode
   */
  public int getOpcode() {
    return opcode;
  }

  /**
   * @param opcode the opcode to set
   */
  public void setOpcode(int opcode) {
    this.opcode = opcode;
  }

  /**
   * @return the htype
   */
  public int getHType() {
    return htype;
  }

  /**
   * @param htype the htype to set
   */
  public void setHType(int htype) {
    this.htype = htype;
  }

  /**
   * @return the hlen
   */
  public int getHLen() {
    return hlen;
  }

  /**
   * @param hlen the hlen to set
   */
  public void setHLen(int hlen) {
    this.hlen = hlen;
  }

  /**
   * @return the hops
   */
  public int getHOps() {
    return hops;
  }

  /**
   * @param hops the hops to set
   */
  public void setHOps(int hops) {
    this.hops = hops;
  }

  /**
   * @return the id
   */
  public int getID() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setID(int id) {
    this.id = id;
  }

  /**
   * @return the secs
   */
  public int getSecs() {
    return secs;
  }

  /**
   * @param secs the secs to set
   */
  public void setSecs(int secs) {
    this.secs = secs;
  }

  /**
   * @return the flags
   */
  public int getFlags() {
    return flags;
  }

  /**
   * @param flags the flags to set
   */
  public void setFlags(int flags) {
    this.flags = flags;
  }

  /**
   * @return the ciaddr
   */
  public String getCIAddr() {
    return ciaddr;
  }

  /**
   * @param ciaddr the ciaddr to set
   */
  public void setCIAddr(String ciaddr) {
    this.ciaddr = ciaddr;
  }

  /**
   * @return the yiaddr
   */
  public String getYIAddr() {
    return yiaddr;
  }

  /**
   * @param yiaddr the yiaddr to set
   */
  public void setYIAddr(String yiaddr) {
    this.yiaddr = yiaddr;
  }

  /**
   * @return the siaddr
   */
  public String getSIAddr() {
    return siaddr;
  }

  /**
   * @param siaddr the siaddr to set
   */
  public void setSIAddr(String siaddr) {
    this.siaddr = siaddr;
  }

  /**
   * @return the giaddr
   */
  public String getGIAddr() {
    return giaddr;
  }

  /**
   * @param giaddr the giaddr to set
   */
  public void setGIAddr(String giaddr) {
    this.giaddr = giaddr;
  }

  /**
   * @return the chaddr
   */
  public String getCHAddr() {
    return chaddr;
  }

  /**
   * @param chaddr the chaddr to set
   */
  public void setCHAddr(String chaddr) {
    this.chaddr = chaddr;
  }

  /**
   * @return the chaddrPadding
   */
  public String getCHAddrPadding() {
    return chaddrPadding;
  }

  /**
   * @param chaddrPadding the chaddrPadding to set
   */
  public void setCHAddrPadding(String chaddrPadding) {
    this.chaddrPadding = chaddrPadding;
  }

  /**
   * @return the sname
   */
  public String getSName() {
    return sname;
  }

  /**
   * @param sname the sname to set
   */
  public void setSName(String sname) {
    this.sname = sname;
  }

  /**
   * @return the file
   */
  public String getFile() {
    return file;
  }

  /**
   * @param file the file to set
   */
  public void setFile(String file) {
    this.file = file;
  }

}
