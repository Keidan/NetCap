package org.kei.android.phone.jni.net.layer.link;

import java.util.List;

import org.kei.android.phone.jni.net.layer.Layer;

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
  private String source      = "";
  private String destination = "";
  private int    proto       = 0;
  
  public Ethernet() {
    super(TYPE_ETHERNET);
  }
  
  @Override
  public String getFullName() {
    return "Ethernet";
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
    lines.add("  Type: " + String.format("0x%04d", getProto()));
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
