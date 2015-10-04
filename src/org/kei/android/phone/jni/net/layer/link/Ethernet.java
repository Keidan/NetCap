package org.kei.android.phone.jni.net.layer.link;

import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;
import org.kei.android.phone.jni.net.layer.internet.IPv4;
import org.kei.android.phone.jni.net.layer.internet.IPv6;

/**
 *******************************************************************************
 * @file Ethernet.java
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
public class Ethernet extends Layer {
  public static final int HEADER_LENGTH = 14;
  public static final int ETH_P_IP    = 0x0800;
  public static final int ETH_P_IPV6  = 0x86DD;
  public static final int ETH_P_ARP   = 0x0806;
  private String          source      = "";
  private String          destination = "";
  private int             proto       = 0;
  
  public Ethernet() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Ethernet (Src: " + source + ", Dst: " + destination + ")";
  }

  @Override
  public String getProtocolText() {
    return "Ethernet";
  }

  @Override
  public String getDescriptionText() {
    return null;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Source: " + getSource());
    lines.add("  Destination: " + getDestination());    
    if(proto == ETH_P_IP)
      lines.add("  Type: IP (" + String.format("0x%04d", ETH_P_IP) + ")");
    else if(proto == ETH_P_IPV6)
      lines.add("  Type: IPv6 (" + String.format("0x%04d", ETH_P_IPV6) + ")");
    else if(proto == ETH_P_ARP)
      lines.add("  Type: ARP (" + String.format("0x%04d", ETH_P_ARP) + ")");
    else
      lines.add("  Type: (" + String.format("0x%04d", getProto()) + ")");
  }
  
  @Override
  public int getHeaderLength() {
    return 14;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    byte temp6 [] = new byte[6];
    byte temp2 [] = new byte[2];
    System.arraycopy(buffer, 0, temp6, 0, temp6.length);
    source = String.format("%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
    System.arraycopy(buffer, 6, temp6, 0, temp6.length);
    destination = String.format("%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
    System.arraycopy(buffer, 12, temp2, 0, temp2.length);
    proto = NetworkHelper.ntohs2(temp2);
    byte [] sub_buffer = resizeBuffer(buffer);
    if(proto == ETH_P_IP) {
      IPv4 ip = new IPv4();
      ip.decodeLayer(sub_buffer, this);
      setNext(ip);
    } else if(proto == ETH_P_IPV6) {
      IPv6 ip = new IPv6();
      ip.decodeLayer(sub_buffer, this);
      setNext(ip);
    } else if(proto == ETH_P_ARP) {
      ARP arp = new ARP();
      arp.decodeLayer(sub_buffer, this);
      setNext(arp);
    } else {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  /**
   * Get the source eth addr.
   * 
   * @return String
   */
  public String getSource() {
    return source;
  }
  
  /**
   * Set the source eth addr.
   * 
   * @param source
   *          The address
   */
  public void setSource(final String source) {
    this.source = source;
  }
  
  /**
   * Get the destination eth addr.
   * 
   * @return String
   */
  public String getDestination() {
    return destination;
  }
  
  /**
   * Set the destination eth addr.
   * 
   * @param destination
   *          The address
   */
  public void setDestination(final String destination) {
    this.destination = destination;
  }
  
  /**
   * Get the packet type ID field.
   * 
   * @return int
   */
  public int getProto() {
    return proto;
  }
  
  /**
   * Set the packet type ID field.
   * 
   * @param proto
   *          The new packet type ID field.
   */
  public void setProto(final int proto) {
    this.proto = proto;
  }
  
}
