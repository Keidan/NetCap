package org.kei.android.phone.jni.net.layer.transport;

import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.Service;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;
import org.kei.android.phone.jni.net.layer.application.DHCPv4;
import org.kei.android.phone.jni.net.layer.application.DHCPv6;
import org.kei.android.phone.jni.net.layer.application.DNS;

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
public class UDP extends Layer {
  private int source;
  private int destination;
  private int length;
  private int checksum;

  public UDP() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "User Datagram Protocol (Src: " + source + ", Dst: " + destination + ")";
  }

  @Override
  public String getProtocolText() {
    return "UDP";
  }

  @Override
  public String getDescriptionText() {
    String desc = "";
    Service srv = Service.findByPort(getSource());
    if(srv == Service.NOT_FOUND) desc += getSource();
    else desc += srv.getName() + "(" + srv.getPort() + ")";
    desc += " > ";
    srv = Service.findByPort(getDestination());
    if(srv == Service.NOT_FOUND) desc += getDestination();
    else desc += srv.getName() + "(" + srv.getPort() + ")";
    return desc;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Source port: " + getSource());
    lines.add("  Destination port: " + getDestination());
    lines.add("  Length: " + getLength());
    lines.add("  Checksum: 0x" + String.format("%04x", getChecksum()));
  }
  
  @Override
  public int getHeaderLength() {
    return 8;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    int offset = 0;
    byte temp2 [] = new byte[2];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    source = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    destination = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    length = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    checksum = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    
    byte [] sub_buffer = resizeBuffer(buffer);
    if(sub_buffer != null) {
      if(source == DNS.PORT || destination == DNS.PORT) {
        DNS dns = new DNS();
        dns.decodeLayer(sub_buffer, this);
        setNext(dns);
      } else if(source == DHCPv4.PORT_CLI || destination == DHCPv4.PORT_CLI || source == DHCPv4.PORT_SRV || destination == DHCPv4.PORT_SRV) {
        DHCPv4 dhcp = new DHCPv4();
        dhcp.decodeLayer(sub_buffer, this);
        setNext(dhcp);
      } else if(source == DHCPv6.PORT_CLI || destination == DHCPv6.PORT_CLI || source == DHCPv6.PORT_SRV || destination == DHCPv6.PORT_SRV) {
        DHCPv6 dhcp = new DHCPv6();
        dhcp.decodeLayer(sub_buffer, this);
        setNext(dhcp);
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
   * Get the length.
   * 
   * @return int
   */
  public int getLength() {
    return length;
  }
  
  /**
   * Set the length.
   * 
   * @param length
   *          the length to set
   */
  public void setLength(final int length) {
    this.length = length;
  }
  
  /**
   * Get the Checksum.
   * 
   * @return int
   */
  public int getChecksum() {
    return checksum;
  }
  
  /**
   * Set the checksum.
   * 
   * @param checksum
   *          the checksum to set
   */
  public void setChecksum(final int checksum) {
    this.checksum = checksum;
  }
  
}
