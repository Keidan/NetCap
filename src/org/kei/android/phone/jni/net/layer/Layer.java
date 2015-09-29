package org.kei.android.phone.jni.net.layer;

/**
 *******************************************************************************
 * @file Layer.java
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
public abstract class Layer {
  public static final int TYPE_ETHERNET = 0x00;
  public static final int TYPE_IPv4     = 0x01;
  public static final int TYPE_IPv6     = 0x02;
  public static final int TYPE_ICMPv4   = 0x03;
  public static final int TYPE_ICMPv6   = 0x04;
  public static final int TYPE_TCP      = 0x05;
  public static final int TYPE_UDP      = 0x06;
  public static final int TYPE_ARP      = 0x07;
  public static final int TYPE_DHCPv4   = 0x08;
  public static final int TYPE_DHCPv6   = 0x09;
  public static final int TYPE_NDP      = 0x0A;
  public static final int TYPE_DNS      = 0x0B;
  public static final int TYPE_IGMP     = 0x0C;
  public static final int TYPE_PAYLOAD  = -1;
  private int             layerType     = TYPE_ETHERNET;
  private int             llength       = 0;
  private Layer           next          = null;
  private String          labelProto    = "";
  
  /**
   * Allocate the object with the layer type.
   *
   * @param type
   *          The type.
   */
  public Layer(final int type) {
    this.layerType = type;
  }
  
  /**
   * Get the packet type.
   *
   * @return int
   */
  public int getLayerType() {
    return layerType;
  }
  
  /**
   * Get the data length.
   * 
   * @return int
   */
  public int getLayerLength() {
    return llength;
  }
  
  /**
   * Set the data length.
   * 
   * @param llength
   *          The length.
   */
  public void setLayerLength(final int llength) {
    this.llength = llength;
  }
  
  /**
   * Get the next layer.
   * 
   * @return {@link org.kei.android.phone.jni.net.layer.Layer}
   */
  public Layer getNext() {
    return next;
  }
  
  /**
   * Set the next layer
   * 
   * @param next
   *          the Layer.
   */
  public void setNext(final Layer next) {
    this.next = next;
  }

  public String getLabelProto() {
    return labelProto;
  }

  public void setLabelProto(String labelProto) {
    this.labelProto = labelProto;
  }

}
