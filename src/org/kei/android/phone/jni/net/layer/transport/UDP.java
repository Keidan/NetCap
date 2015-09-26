package org.kei.android.phone.jni.net.layer.transport;

import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file UDP.java
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
    super(TYPE_UDP);
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
