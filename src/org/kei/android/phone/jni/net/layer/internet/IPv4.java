package org.kei.android.phone.jni.net.layer.internet;

import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file IPv4.java
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
public class IPv4 extends Layer {
  private int     tos;
  private int     totLength;
  private int     ident;
  private int     id;
  private int     fragOff;
  private int     ttl;
  private int     protocol;
  private int     checksum;
  private String  source;
  private String  destination;
  
  private boolean reservedBit   = false;
  private boolean dontFragment  = false;
  private boolean moreFragments = false;
  private int     headerLength  = 0;
  
  public IPv4() {
    super(TYPE_IPv4);
  }
  
  @Override
  public String getFullName() {
    return "Internet Protocol v4";
  }

  @Override
  public String getProtocolText() {
    return "IPv4";
  }

  @Override
  public String getDescriptionText() {
    return null;
  }

  /**
   * Get the source IPv4 addr.
   *
   * @return String
   */
  public String getSource() {
    return source;
  }

  /**
   * Set the source IPv4 addr.
   *
   * @param source
   *          The address
   */
  public void setSource(final String source) {
    this.source = source;
  }

  /**
   * Get the destination IPv4 addr.
   *
   * @return String
   */
  public String getDestination() {
    return destination;
  }

  /**
   * Set the destination IPv4 addr.
   *
   * @param destination
   *          The address
   */
  public void setDestination(final String destination) {
    this.destination = destination;
  }
  
  /**
   * Get the TOS value.
   * 
   * @return int
   */
  public int getTOS() {
    return tos;
  }
  
  /**
   * Set the TOS value.
   * 
   * @param tos
   *          The value.
   */
  public void setTOS(final int tos) {
    this.tos = tos;
  }
  
  /**
   * Get the Total Length value.
   * 
   * @return int
   */
  public int getTotLength() {
    return totLength;
  }
  
  /**
   * Set the Total Length value.
   * 
   * @param totLength
   *          The value.
   */
  public void setTotLength(final int totLength) {
    this.totLength = totLength;
  }
  
  /**
   * Get the Flags value.
   * 
   * @return int
   */
  public int getFlags() {
    return id;
  }
  
  /**
   * Set the id value.
   * 
   * @param id
   *          The value.
   */
  public void setFlags(final int id) {
    this.id = id;
  }
  
  /**
   * @return the ident
   */
  public int getIdent() {
    return ident;
  }

  /**
   * @param ident the ident to set
   */
  public void setIdent(int ident) {
    this.ident = ident;
  }

  /**
   * Get the FragOffset value.
   * 
   * @return int
   */
  public int getFragOff() {
    return fragOff;
  }
  
  /**
   * Set the FragOffset value.
   * 
   * @param fragOff
   *          The value.
   */
  public void setFragOff(final int fragOff) {
    this.fragOff = fragOff;
  }
  
  /**
   * Get the TTL value.
   * 
   * @return int
   */
  public int getTTL() {
    return ttl;
  }
  
  /**
   * Set the ttl value.
   * 
   * @param ttl
   *          The value.
   */
  public void setTTL(final int ttl) {
    this.ttl = ttl;
  }
  
  /**
   * Get the Protocol value.
   * 
   * @return int
   */
  public int getProtocol() {
    return protocol;
  }
  
  /**
   * Set the protocol value.
   * 
   * @param protocol
   *          The value.
   */
  public void setProtocol(final int protocol) {
    this.protocol = protocol;
  }
  
  /**
   * Get the checksum value.
   * 
   * @return int
   */
  public int getChecksum() {
    return checksum;
  }
  
  /**
   * Set the checksum value.
   * 
   * @param checksum
   *          The value.
   */
  public void setChecksum(final int checksum) {
    this.checksum = checksum;
  }

  public boolean isReservedBit() {
    return reservedBit;
  }

  public void setReservedBit(boolean reservedBit) {
    this.reservedBit = reservedBit;
  }

  public boolean isDontFragment() {
    return dontFragment;
  }

  public void setDontFragment(boolean dontFragment) {
    this.dontFragment = dontFragment;
  }

  public boolean isMoreFragments() {
    return moreFragments;
  }

  public void setMoreFragments(boolean moreFragments) {
    this.moreFragments = moreFragments;
  }

  public int getHeaderLength() {
    return headerLength;
  }

  public void setHeaderLength(int headerLength) {
    this.headerLength = headerLength;
  }
  
}
