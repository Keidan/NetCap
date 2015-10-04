package org.kei.android.phone.jni.net.layer.internet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;
import org.kei.android.phone.jni.net.layer.transport.TCP;
import org.kei.android.phone.jni.net.layer.transport.UDP;

/**
 *******************************************************************************
 * @file IPv6.java
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
public class IPv6 extends Layer {
  public static final int IPPROTO_UDP  = 17;
  public static final int IPPROTO_TCP  = 6;
  public static final int IPPROTO_IGMP = 2;
  public static final int IPPROTO_ICMP = 1;
  private int             priority;
  private int             version;
  private int             flowLbl_1;
  private int             flowLbl_2;
  private int             flowLbl_3;
  private int             payloadLen;
  private int             nexthdr;
  private int             hopLimit;
  private String          source;
  private String          destination;
  private int             headerLength;
  
  public IPv6() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Internet Protocol v" + getVersion() +" (Src: " + source + ", Dst: " + destination + ")";
  }

  @Override
  public String getProtocolText() {
    return "IPv6";
  }

  @Override
  public String getDescriptionText() {
    return null;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Version: " + getVersion());
    lines.add("  Priority: " + getPriority());
    lines.add("  Flowlabel: 0x" + String.format("%02x%02x%02x", getFlowLbl_1(), getFlowLbl_2(), getFlowLbl_3()));
    lines.add("  Payload length: " + getPayloadLen());
    lines.add("  Next header: " + getNexthdr());
    lines.add("  Hop limit: " + getHopLimit());
    lines.add("  Source: " + getSource());
    lines.add("  Destination: " + getDestination());
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    byte temp16 [] = new byte[16];
    byte temp2 [] = new byte[2];
    int offset = 0;
    byte ihl_priority = buffer[offset++];
    version = (byte)(ihl_priority >> 4);
    priority = (byte)(ihl_priority & 0x0f);
    
    flowLbl_1 = buffer[offset++];
    flowLbl_2 = buffer[offset++];
    flowLbl_3 = buffer[offset++];
    
    
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    payloadLen = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;

    nexthdr = buffer[offset++];
    hopLimit = buffer[offset++];
    
    
    NetworkHelper.zcopy(buffer, offset, temp16, 0, temp16.length);
    offset+=temp16.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp16);
      source = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      source = e.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp16, 0, temp16.length);
    offset+=temp16.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp16);
      destination = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      destination = e.getMessage();
    }
    headerLength = offset;
    if(nexthdr == IPPROTO_UDP) {
      byte [] sub_buffer = resizeBuffer(buffer);
      UDP udp = new UDP();
      udp.decodeLayer(sub_buffer, this);
      setNext(udp);
    } else if(nexthdr == IPPROTO_TCP) {
      byte [] sub_buffer = resizeBuffer(buffer);
      TCP tcp = new TCP();
      tcp.decodeLayer(sub_buffer, this);
      setNext(tcp);
    } else if(nexthdr == IPPROTO_IGMP) {
      IGMP igmp = new IGMP();
      offset += 4; // options
      headerLength = offset;
      byte [] sub_buffer = resizeBuffer(buffer);
      sub_buffer = resizeBuffer(buffer);
      igmp.decodeLayer(sub_buffer, this);
      setNext(igmp);
    } else if(nexthdr == IPPROTO_ICMP) {
      byte [] sub_buffer = resizeBuffer(buffer);
      ICMPv6 icmp = new ICMPv6();
      icmp.decodeLayer(sub_buffer, this);
      setNext(icmp);
    } else {
      byte [] sub_buffer = resizeBuffer(buffer);
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }
  
  /**
   * Get the source IPv6 addr.
   *
   * @return String
   */
  public String getSource() {
    return source;
  }
  
  /**
   * Set the source IPv6 addr.
   *
   * @param source
   *          The address
   */
  public void setSource(final String source) {
    this.source = source;
  }
  
  /**
   * Get the destination IPv6 addr.
   *
   * @return String
   */
  public String getDestination() {
    return destination;
  }
  
  /**
   * Set the destination IPv6 addr.
   *
   * @param destination
   *          The address
   */
  public void setDestination(final String destination) {
    this.destination = destination;
  }
  
  /**
   * Get the priority.
   * 
   * @return int
   */
  public int getPriority() {
    return priority;
  }
  
  /**
   * Set the priority.
   * 
   * @param priority
   *          The value.
   */
  public void setPriority(final int priority) {
    this.priority = priority;
  }
  
  /**
   * Get the version.
   * 
   * @return int
   */
  public int getVersion() {
    return version;
  }
  
  /**
   * Set the version.
   * 
   * @param version
   *          The value.
   */
  public void setVersion(final int version) {
    this.version = version;
  }
  
  /**
   * Get the FlowLbl[0].
   * 
   * @return int
   */
  public int getFlowLbl_1() {
    return flowLbl_1;
  }
  
  /**
   * Set the FlowLbl[0].
   * 
   * @param flowLbl_1
   *          The value.
   */
  public void setFlowLbl_1(final int flowLbl_1) {
    this.flowLbl_1 = flowLbl_1;
  }
  
  /**
   * Get the FlowLbl[1].
   * 
   * @return int
   */
  public int getFlowLbl_2() {
    return flowLbl_2;
  }
  
  /**
   * Set the FlowLbl[1].
   * 
   * @param flowLbl_2
   *          The value.
   */
  public void setFlowLbl_2(final int flowLbl_2) {
    this.flowLbl_2 = flowLbl_2;
  }
  
  /**
   * Get the FlowLbl[2].
   * 
   * @return int
   */
  public int getFlowLbl_3() {
    return flowLbl_3;
  }
  
  /**
   * Set the FlowLbl[2].
   * 
   * @param flowLbl_3
   *          The value.
   */
  public void setFlowLbl_3(final int flowLbl_3) {
    this.flowLbl_3 = flowLbl_3;
  }
  
  /**
   * Get the payload length.
   * 
   * @return int
   */
  public int getPayloadLen() {
    return payloadLen;
  }
  
  /**
   * Set the payload length.
   * 
   * @param payloadLen
   *          The value.
   */
  public void setPayloadLen(final int payloadLen) {
    this.payloadLen = payloadLen;
  }
  
  /**
   * Get the next hdr.
   * 
   * @return int
   */
  public int getNexthdr() {
    return nexthdr;
  }
  
  /**
   * Set the next hdr.
   * 
   * @param nexthdr
   *          The value.
   */
  public void setNexthdr(final int nexthdr) {
    this.nexthdr = nexthdr;
  }
  
  /**
   * Get the hop limit.
   * 
   * @return int
   */
  public int getHopLimit() {
    return hopLimit;
  }
  
  /**
   * Set the hop limit.
   * 
   * @param hopLimit
   *          The value.
   */
  public void setHopLimit(final int hopLimit) {
    this.hopLimit = hopLimit;
  }

}
