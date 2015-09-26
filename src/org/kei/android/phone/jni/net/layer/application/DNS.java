package org.kei.android.phone.jni.net.layer.application;

import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file DNS.java
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
public class DNS extends Layer {
  public static final int QTYPE_ADDRESS    = 1; /* Ipv4 address */
  public static final int QTYPE_NAMESERVER = 2; /* Nameserver */
  public static final int QTYPE_CNAME      = 5; /* canonical name */
  public static final int QTYPE_SOA        = 6; /* start of authority zone */
  public static final int QTYPE_PTR        = 12; /* domain name pointer */
  public static final int QTYPE_MX         = 15; /* Mail server */

  private int             id;                   // identification number
  private boolean         rd;
  private boolean         tc;
  private boolean         aa;
  private int             opcode;
  private boolean         qr;
  private int             rcode;
  private boolean         cd;
  private boolean         ad;
  private boolean         z;
  private boolean         ra;
  private int             qCount;
  private int             ansCount;
  private int             authCount;
  private int             addCount;

  public DNS() {
    super(TYPE_DNS);
  }
  
  /**
   * @return the id
   */
  public int getID() {
    return id;
  }
  
  /**
   * @param id
   *          the id to set
   */
  public void setID(final int id) {
    this.id = id;
  }
  
  /**
   * @return the rd
   */
  public boolean isRD() {
    return rd;
  }
  
  /**
   * @param rd
   *          the rd to set
   */
  public void setRD(final boolean rd) {
    this.rd = rd;
  }
  
  /**
   * @return the tc
   */
  public boolean isTC() {
    return tc;
  }
  
  /**
   * @param tc
   *          the tc to set
   */
  public void setTC(final boolean tc) {
    this.tc = tc;
  }
  
  /**
   * @return the aa
   */
  public boolean isAA() {
    return aa;
  }
  
  /**
   * @param aa
   *          the aa to set
   */
  public void setAA(final boolean aa) {
    this.aa = aa;
  }
  
  /**
   * @return the opcode
   */
  public int getOpcode() {
    return opcode;
  }
  
  /**
   * @param opcode
   *          the opcode to set
   */
  public void setOpcode(final int opcode) {
    this.opcode = opcode;
  }
  
  /**
   * @return the qr
   */
  public boolean isQR() {
    return qr;
  }
  
  /**
   * @param qr
   *          the qr to set
   */
  public void setQR(final boolean qr) {
    this.qr = qr;
  }
  
  /**
   * @return the rcode
   */
  public int getRCode() {
    return rcode;
  }
  
  /**
   * @param rcode
   *          the rcode to set
   */
  public void setRCode(final int rcode) {
    this.rcode = rcode;
  }
  
  /**
   * @return the cd
   */
  public boolean isCD() {
    return cd;
  }
  
  /**
   * @param cd
   *          the cd to set
   */
  public void setCD(final boolean cd) {
    this.cd = cd;
  }
  
  /**
   * @return the ad
   */
  public boolean isAD() {
    return ad;
  }
  
  /**
   * @param ad
   *          the ad to set
   */
  public void setAD(final boolean ad) {
    this.ad = ad;
  }
  
  /**
   * @return the z
   */
  public boolean isZ() {
    return z;
  }
  
  /**
   * @param z
   *          the z to set
   */
  public void setZ(final boolean z) {
    this.z = z;
  }
  
  /**
   * @return the ra
   */
  public boolean isRA() {
    return ra;
  }
  
  /**
   * @param ra
   *          the ra to set
   */
  public void setRA(final boolean ra) {
    this.ra = ra;
  }
  
  /**
   * @return the qCount
   */
  public int getQCount() {
    return qCount;
  }
  
  /**
   * @param qCount
   *          the qCount to set
   */
  public void setQCount(final int qCount) {
    this.qCount = qCount;
  }
  
  /**
   * @return the ansCount
   */
  public int getAnsCount() {
    return ansCount;
  }
  
  /**
   * @param ansCount
   *          the ansCount to set
   */
  public void setAnsCount(final int ansCount) {
    this.ansCount = ansCount;
  }
  
  /**
   * @return the authCount
   */
  public int getAuthCount() {
    return authCount;
  }
  
  /**
   * @param authCount
   *          the authCount to set
   */
  public void setAuthCount(final int authCount) {
    this.authCount = authCount;
  }
  
  /**
   * @return the addCount
   */
  public int getAddCount() {
    return addCount;
  }
  
  /**
   * @param addCount
   *          the addCount to set
   */
  public void setAddCount(final int addCount) {
    this.addCount = addCount;
  }
  
}
