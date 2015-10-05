package org.kei.android.phone.net.layer.transport;

import java.util.List;
import java.util.Locale;

import org.kei.android.phone.net.NetworkHelper;
import org.kei.android.phone.net.Service;
import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.Payload;
import org.kei.android.phone.net.layer.application.DNS;

import android.graphics.Color;

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
public class TCP extends Layer {
  public static final int FLAG_CWR = 0x0080;
  public static final int FLAG_ECE = 0x0040;
  public static final int FLAG_URG = 0x0020;
  public static final int FLAG_ACK = 0x0010;
  public static final int FLAG_PSH = 0x0008;
  public static final int FLAG_RST = 0x0004;
  public static final int FLAG_SYN = 0x0002;
  public static final int FLAG_FIN = 0x0001;
  private int             source;
  private int             destination;
  private boolean         cwr      = false;
  private boolean         ece      = false;
  private boolean         urg      = false;
  private boolean         ack      = false;
  private boolean         psh      = false;
  private boolean         rst      = false;
  private boolean         syn      = false;
  private boolean         fin      = false;
  private int             seq;
  private int             ackSeq;
  private int             window;
  private int             check;
  private int             urgPtr;
  private int             doff;
  private int             reserved;
  private int             flags;
  private byte[]          optionBytes;
  
  public TCP() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Transmission Control Protocol (Src: " + source + ", Dst: " + destination + ")";
  }

  @Override
  public String getProtocolText() {
    return "TCP";
  }

  @Override
  public String getDescriptionText() {
    final int defbg = background;
    String desc = "";
    Service srv = Service.findByPort(getSource());
    if (srv == Service.NOT_FOUND)
      desc += getSource();
    else {
      desc += srv.getName() + "(" + srv.getPort() + ")";
      if (srv.getName().toLowerCase(Locale.US).contains("https")) {
        background = Color.parseColor("#A40000");
        foreground = Color.parseColor("#FFFC9C");
      } else if (srv.getName().toLowerCase(Locale.US).contains("http")) {
        background = Color.parseColor("#E4FFC7");
        foreground = Color.BLACK;
      }
    }
    desc += " > ";
    srv = Service.findByPort(getDestination());
    if (srv == Service.NOT_FOUND)
      desc += getDestination();
    else {
      desc += srv.getName() + "(" + srv.getPort() + ")";
      if (srv.getName().toLowerCase(Locale.US).contains("https")) {
        background = Color.parseColor("#A40000");
        foreground = Color.parseColor("#FFFC9C");
      } else if (srv.getName().toLowerCase(Locale.US).contains("http")) {
        background = Color.parseColor("#E4FFC7");
        foreground = Color.BLACK;
      }
    }
    desc += " [";
    if (background == defbg) {
      background = Color.parseColor("#E7E6FF");
      foreground = Color.BLACK;
    }
    if (isSYN()) desc += "SYN, ";
    if (isPSH()) desc += "PSH, ";
    if (isACK()) desc += "ACK, ";
    if (isCWR()) desc += "CWR, ";
    if (isECE()) desc += "ECE, ";
    if (isRST()) desc += "RST, ";
    if (isURG()) desc += "URG, ";
    if (isFIN()) desc += "FIN, ";
    if (desc.endsWith(", "))
      desc = desc.substring(0, desc.length() - 2);
    desc += "]";
    return desc;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Source port: " + getSource());
    lines.add("  Destination port: " + getDestination());
    lines.add("  Sequence number: " + getSeq());
    lines.add("  Acknowledgement number: " + getAckSeq());
    lines.add("  Header Length: " + getHeaderLength() + " bytes");
    lines.add("  Flags:");
    lines.add("    " + (isCWR() ? "1" : "0") + "... .... = Congestion Window Reduced: " + (isCWR() ? "Set" : "Not Set"));
    lines.add("    ." + (isECE() ? "1" : "0") + ".. .... = ECN-Echo: " + (isECE() ? "Set" : "Not Set"));
    lines.add("    .." + (isURG() ? "1" : "0") + ". .... = Urgent: " + (isURG() ? "Set" : "Not Set"));
    lines.add("    ..." + (isACK() ? "1" : "0") + " .... = Acknowledgement: " + (isACK() ? "Set" : "Not Set"));
    lines.add("    .... " + (isPSH() ? "1" : "0") + "... = Push: " + (isPSH() ? "Set" : "Not Set"));
    lines.add("    .... ." + (isRST() ? "1" : "0") + ".. = Reset: " + (isRST() ? "Set" : "Not Set"));
    lines.add("    .... .." + (isSYN() ? "1" : "0") + ". = Syn: " + (isSYN() ? "Set" : "Not Set"));
    lines.add("    .... ..." + (isFIN() ? "1" : "0") + " = Fin: " + (isFIN() ? "Set" : "Not Set"));
    lines.add("  Window size: " + getWindow());
    lines.add("  Checksum: 0x" + String.format("%04x", getCheck()));
    lines.add("  Urg ptr: " + getUrgPtr());
    if(getOptionBytes() != null && getOptionBytes().length > 0) {
      lines.add("  Options: " + getOptionBytes().length + " bytes");
      List<String> l = NetworkHelper.formatBuffer(getOptionBytes());
      for(String s : l) lines.add("    " + s);
    }
  }
  
  @Override
  public int getHeaderLength() {
    return doff * 4;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    int offset = 0;
    byte temp2 [] = new byte[2];
    byte temp4 [] = new byte[4];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    source = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    destination = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    seq = NetworkHelper.ntohl(temp4);
    offset+=temp4.length;
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    ackSeq = NetworkHelper.ntohl(temp4);
    offset+=temp4.length;

    byte doff_reserved = buffer[offset++];
    doff = Math.abs((byte)(doff_reserved >> 4));
    reserved = (byte)(doff_reserved & 0x0f);
    int flags = buffer[offset++]& 0x3F;

    cwr = (flags&FLAG_CWR) != 0;
    ece = (flags&FLAG_ECE) != 0;
    urg = (flags&FLAG_URG) != 0;
    ack = (flags&FLAG_ACK) != 0;
    psh = (flags&FLAG_PSH) != 0;
    rst = (flags&FLAG_RST) != 0;
    syn = (flags&FLAG_SYN) != 0;
    fin = (flags&FLAG_FIN) != 0;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    window = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    check = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    urgPtr = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    
    if (doff > 5) {
      optionBytes = new byte[(doff - 5) * 4];
      System.arraycopy(buffer, offset, optionBytes, 0, optionBytes.length);
      offset+=optionBytes.length;
    } else
      optionBytes = new byte[0];
    
    byte [] sub_buffer = resizeBuffer(buffer);
    if(sub_buffer != null) {
      if(source == DNS.PORT || destination == DNS.PORT) {
        DNS dns = new DNS();
        dns.decodeLayer(sub_buffer, this);
        setNext(dns);
      } else {
        Payload p = new Payload();
        p.decodeLayer(sub_buffer, this);
        setNext(p);
      }
    }
  }

  /**
   * Get the source port.
   *
   * @return int
   */
  public int getSource() {
    return source;
  }

  /**
   * Set the source port.
   *
   * @param source
   *          the source to set
   */
  public void setSource(final int source) {
    this.source = source;
  }

  /**
   * Get the destination port.
   *
   * @return int
   */
  public int getDestination() {
    return destination;
  }

  /**
   * Set the destination port.
   *
   * @param destination
   *          the destination to set
   */
  public void setDestination(final int destination) {
    this.destination = destination;
  }

  /**
   * @return the cwr
   */
  public boolean isCWR() {
    return cwr;
  }

  /**
   * @param cwr
   *          the cwr to set
   */
  public void setCWR(final boolean cwr) {
    this.cwr = cwr;
  }

  /**
   * @return the ece
   */
  public boolean isECE() {
    return ece;
  }

  /**
   * @param ece
   *          the ece to set
   */
  public void setECE(final boolean ece) {
    this.ece = ece;
  }

  /**
   * @return the urg
   */
  public boolean isURG() {
    return urg;
  }

  /**
   * @param urg
   *          the urg to set
   */
  public void setURG(final boolean urg) {
    this.urg = urg;
  }

  /**
   * @return the ack
   */
  public boolean isACK() {
    return ack;
  }

  /**
   * @param ack
   *          the ack to set
   */
  public void setACK(final boolean ack) {
    this.ack = ack;
  }

  /**
   * @return the psh
   */
  public boolean isPSH() {
    return psh;
  }

  /**
   * @param psh
   *          the psh to set
   */
  public void setPSH(final boolean psh) {
    this.psh = psh;
  }

  /**
   * @return the rst
   */
  public boolean isRST() {
    return rst;
  }

  /**
   * @param rst
   *          the rst to set
   */
  public void setRST(final boolean rst) {
    this.rst = rst;
  }

  /**
   * @return the syn
   */
  public boolean isSYN() {
    return syn;
  }

  /**
   * @param syn
   *          the syn to set
   */
  public void setSYN(final boolean syn) {
    this.syn = syn;
  }

  /**
   * @return the fin
   */
  public boolean isFIN() {
    return fin;
  }

  /**
   * @param fin
   *          the fin to set
   */
  public void setFIN(final boolean fin) {
    this.fin = fin;
  }

  /**
   * @return the seq
   */
  public int getSeq() {
    return seq;
  }

  /**
   * @param seq
   *          the seq to set
   */
  public void setSeq(final int seq) {
    this.seq = seq;
  }

  /**
   * @return the ackSeq
   */
  public int getAckSeq() {
    return ackSeq;
  }

  /**
   * @param ackSeq
   *          the ackSeq to set
   */
  public void setAckSeq(final int ackSeq) {
    this.ackSeq = ackSeq;
  }

  /**
   * @return the window
   */
  public int getWindow() {
    return window;
  }

  /**
   * @param window
   *          the window to set
   */
  public void setWindow(final int window) {
    this.window = window;
  }

  /**
   * @return the check
   */
  public int getCheck() {
    return check;
  }

  /**
   * @param check
   *          the check to set
   */
  public void setCheck(final int check) {
    this.check = check;
  }

  /**
   * @return the urgPtr
   */
  public int getUrgPtr() {
    return urgPtr;
  }

  /**
   * @param urgPtr
   *          the urgPtr to set
   */
  public void setUrgPtr(final int urgPtr) {
    this.urgPtr = urgPtr;
  }

  /**
   * @return the doff
   */
  public int getDOFF() {
    return doff;
  }

  /**
   * @param doff the doff to set
   */
  public void setDOFF(int doff) {
    this.doff = doff;
  }

  /**
   * @return the reserved
   */
  public int getReserved() {
    return reserved;
  }

  /**
   * @param reserved the reserved to set
   */
  public void setReserved(int reserved) {
    this.reserved = reserved;
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

}
