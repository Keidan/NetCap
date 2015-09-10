package org.kei.android.phone.jni.net.layer;

/**
 *******************************************************************************
 * @file Layer.java
 * @author Keidan
 * @date 07/09/2015
 * @par Project
 * NetCap
 *
 * @par Copyright
 * Copyright 2011-2013 Keidan, all right reserved
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
  private int             type          = TYPE_ETHERNET;

  /**
   * Allocate the object with the layer type.
   *
   * @param type
   *          The type.
   */
  public Layer(final int type) {
    this.type = type;
  }

  /**
   * Get the packet type.
   *
   * @return int
   */
  public int getType() {
    return type;
  }
}
