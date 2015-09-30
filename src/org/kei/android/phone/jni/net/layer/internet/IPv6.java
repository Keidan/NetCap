package org.kei.android.phone.jni.net.layer.internet;

import org.kei.android.phone.jni.net.layer.Layer;

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
  private int    priority;
  private int    version;
  private int    flowLbl_1;
  private int    flowLbl_2;
  private int    flowLbl_3;
  private int    payloadLen;
  private int    nexthdr;
  private int    hopLimit;
  private String source;
  private String destination;
  
  public IPv6() {
    super(TYPE_IPv6);
  }
  
  @Override
  public String getFullName() {
    return "Internet Protocol v6";
  }

  @Override
  public String getProtocolText() {
    return "IPv6";
  }

  @Override
  public String getDescriptionText() {
    return null;
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
