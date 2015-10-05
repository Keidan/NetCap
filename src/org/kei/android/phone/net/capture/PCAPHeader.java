package org.kei.android.phone.net.capture;

/**
 *******************************************************************************
 * @file PCAPHeader.java
 * @author Keidan
 * @date 10/09/2015
 * @par Project
 * NetCap
 *
 * @par Copyright 2015 Keidan, all right reserved
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
public class PCAPHeader {
  public static final int MAX_SIGNED_INT = Integer.MAX_VALUE;
  public static final int MAX_SIGNED_SHORT = Short.MAX_VALUE;
  /**
   * Major version of the PCAP file.
   */
  public final static int PCAP_VERSION_MAJOR     = 2;
  /**
   * Minor version of the PCAP file.
   */
  public final static int PCAP_VERSION_MINOR     = 4;
  /**
   * PCAP Magic.
   */
  public final static long PCAP_MAGIC_NATIVE     = 0xa1b2c3d4;
  /**
   * PCAP Magic.
   */
  public final static long PCAP_MAGIC_SWAPPED    = 0xd4c3b2a1;
  /**
   * Capture type.
   */
  public final static int PCAP_LINKTYPE_ETHERNET = 1;
  /**
   * Capture size.
   */
  public final static int PCAP_SNAPLEN           = 65535;
  private long            magicNumber;
  private int             versionMajor;
  private int             versionMinor;
  private int             thiszone;
  private long            sigfigs;
  private long            snapLength;
  private long            network;
  
  /**
   * Get the magic number.
   *
   * @return unsigned int
   */
  public long getMagicNumber() {
    return magicNumber;
  }
  
  /**
   * Set the magic number.
   *
   * @param magicNumber
   *          New value.
   */
  public void setMagicNumber(final long magicNumber) {
    this.magicNumber = magicNumber;
  }
  
  /**
   * Get the major version number.
   *
   * @return unsigned short
   */
  public int getVersionMajor() {
    return versionMajor;
  }
  
  /**
   * Set the major version number.
   *
   * @param versionMajor
   *          New value.
   */
  public void setVersionMajor(final int versionMajor) {
    this.versionMajor = versionMajor;
  }
  
  /**
   * Get the minor version number.
   *
   * @return unsigned short
   */
  public int getVersionMinor() {
    return versionMinor;
  }
  
  /**
   * Set the minor version number.
   *
   * @param versionMinor
   *          New value.
   */
  public void setVersionMinor(final int versionMinor) {
    this.versionMinor = versionMinor;
  }
  
  /**
   * Get the GMT to local correction.
   *
   * @return unsigned short
   */
  public int getThiszone() {
    return thiszone;
  }
  
  /**
   * Set the GMT to local correction.
   *
   * @param thiszone
   *          New value.
   */
  public void setThiszone(final int thiszone) {
    this.thiszone = thiszone;
  }
  
  /**
   * Get the accuracy of timestamps.
   *
   * @return unsigned int
   */
  public long getSigfigs() {
    return sigfigs;
  }
  
  /**
   * Set the accuracy of timestamps.
   *
   * @param sigfigs
   *          New value.
   */
  public void setSigfigs(final long sigfigs) {
    this.sigfigs = sigfigs;
  }
  
  /**
   * Get the max length of captured packets, in octets.
   *
   * @return unsigned int
   */
  public long getSnapLength() {
    return snapLength;
  }
  
  /**
   * Set the max length of captured packets, in octets.
   *
   * @param snapLength
   *          New value.
   */
  public void setSnapLength(final long snapLength) {
    this.snapLength = snapLength;
  }
  
  /**
   * Get the data link type.
   *
   * @return unsigned int
   */
  public long getNetwork() {
    return network;
  }
  
  /**
   * Set the data link type.
   *
   * @param network
   *          New value.
   */
  public void setNetwork(final long network) {
    this.network = network;
  }
  
}
