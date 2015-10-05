package org.kei.android.phone.net;

/**
 *******************************************************************************
 * @file NetworkInterface.java
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
public class NetworkInterface {
  /* interface is up */
  public static final int FLAG_UP          = 0x1;
  /* broadcast address valid */
  public static final int FLAG_BROADCAST   = 0x2;
  /* turn on debugging */
  public static final int FLAG_DEBUG       = 0x4;
  /* is a loopback net */
  public static final int FLAG_LOOPBACK    = 0x8;
  /* interface is has p-p link */
  public static final int FLAG_POINTOPOINT = 0x10;
  /* avoid use of trailers */
  public static final int FLAG_NOTRAILERS  = 0x20;
  /* interface RFC2863 OPER_UP */
  public static final int FLAG_RUNNING     = 0x40;
  /* no ARP protocol */
  public static final int FLAG_NOARP       = 0x80;
  /* receive all packets */
  public static final int FLAG_PROMISC     = 0x100;
  /* receive all multicast packets */
  public static final int FLAG_ALLMULTI    = 0x200;
  /* master of a load balancer */
  public static final int FLAG_MASTER      = 0x400;
  /* slave of a load balancer */
  public static final int FLAG_SLAVE       = 0x800;
  /* Supports multicast */
  public static final int FLAG_MULTICAST   = 0x1000;
  /* can set media type */
  public static final int FLAG_PORTSEL     = 0x2000;
  /* auto media select active */
  public static final int FLAG_AUTOMEDIA   = 0x4000;
  /* dialup device with changing addresses */
  public static final int FLAG_DYNAMIC     = 0x8000;
  /* driver signals L1 up */
  public static final int FLAG_LOWER_UP    = 0x10000;
  /* driver signals dormant */
  public static final int FLAG_DORMANT     = 0x20000;
  /* echo sent packets */
  public static final int FLAG_ECHO        = 0x40000;
  public static final int FLAG_VOLATILE    = (FLAG_LOOPBACK | FLAG_POINTOPOINT
                                               | FLAG_BROADCAST | FLAG_ECHO
                                               | FLAG_MASTER | FLAG_SLAVE
                                               | FLAG_RUNNING | FLAG_LOWER_UP | FLAG_DORMANT);
  private String          name             = "(null)";
  private String          ipv4             = "0.0.0.0";
  private String          mac              = "00:00:00:00:00:00";
  private String          broadcast        = "0.0.0.0";
  private String          mask             = "0.0.0.0";
  private int             family           = 0;
  private int             metric           = 0;
  private int             mtu              = 0;
  private int             flags            = 0;
  private int             index            = -1;
  private boolean         any              = false;
  
  public NetworkInterface() {
    
  }
  

  public NetworkInterface(final String name, final boolean any) {
    this.name = name;
    this.any = any;
  }
  
  /**
   * Get the any state.
   * @return boolean
   */
  public boolean isAny() {
    return any;
  }

  /**
   * Change the interface name (not saved).
   * 
   * @param name
   *          java/lang/String
   */
  public void setName(final String name) {
    this.name = name;
  }
  
  /**
   * Get the interface name.
   * 
   * @return java/lang/String
   */
  public String getName() {
    return name;
  }

  /**
   * Get the interface IPv4.
   * 
   * @return java/lang/String
   */
  public String getIPv4() {
    return ipv4;
  }

  /**
   * Change the interface IPv4
   * 
   * @param ipv4
   *          java/lang/String
   */
  public void setIPv4(final String ipv4) {
    this.ipv4 = ipv4;
  }

  /**
   * Get the interface MAC address.
   * 
   * @return java/lang/String
   */
  public String getMac() {
    return mac;
  }

  /**
   * Change the interface MAC address.
   * 
   * @param mac
   *          java/lang/String
   */
  public void setMac(final String mac) {
    this.mac = mac;
  }

  /**
   * Get the broadcast address.
   * 
   * @return java/lang/String
   */
  public String getBroadcast() {
    return broadcast;
  }

  /**
   * Change the broadcast address.
   * 
   * @param broadcast
   *          java/lang/String
   */
  public void setBroadcast(final String broadcast) {
    this.broadcast = broadcast;
  }

  /**
   * Get the subnet mask address.
   * 
   * @return java/lang/String
   */
  public String getMask() {
    return mask;
  }

  /**
   * Change the subnet mask address.
   * 
   * @param mask
   *          java/lang/String
   */
  public void setMask(final String mask) {
    this.mask = mask;
  }

  /**
   * Get the interface family.
   * 
   * @return int
   */
  public int getFamily() {
    return family;
  }

  /**
   * Change the interface family.
   * 
   * @param family
   *          int
   */
  public void setFamily(final int family) {
    this.family = family;
  }

  /**
   * Get the interface metric.
   * 
   * @return int
   */
  public int getMetric() {
    return metric;
  }

  /**
   * Change the interface metric.
   * 
   * @param metric
   *          int
   */
  public void setMetric(final int metric) {
    this.metric = metric;
  }

  /**
   * Get the interface MTU.
   * 
   * @return int
   */
  public int getMTU() {
    return mtu;
  }

  /**
   * Change the interface MTU.
   * 
   * @param mtu
   *          int
   */
  public void setMTU(final int mtu) {
    this.mtu = mtu;
  }

  /**
   * Get the interface falgs.
   * 
   * @return int
   */
  public int getFlags() {
    return flags;
  }

  /**
   * Change the interface falgs.
   * 
   * @param flags
   *          int
   */
  public void setFlags(final int flags) {
    this.flags = flags;
  }

  /**
   * Get the interface index.
   * 
   * @return int
   */
  public int getIndex() {
    return index;
  }

  /**
   * Change the interface index.
   * 
   * @param index
   *          int
   */
  public void setIndex(final int index) {
    this.index = index;
  }
  
  public String toString() {
    return name;
  }

}
