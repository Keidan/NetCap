package org.kei.android.phone.jni.net.layer.transport;

import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file TCP.java
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
public class TCP extends Layer {
  private int source;
  private int destination;
  private boolean cwr = false;
  private boolean ece = false;
  private boolean urg = false;
  private boolean ack = false;
  private boolean psh = false;
  private boolean rst = false;
  private boolean syn = false;
  private boolean fin = false;
  private int seq;
  private int ackSeq;
  private int window;
  private int check;
  private int urgPtr;
  
  public TCP() {
    super(TYPE_TCP);
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
   * @return the cwr
   */
  public boolean isCWR() {
    return cwr;
  }

  /**
   * @param cwr the cwr to set
   */
  public void setCWR(boolean cwr) {
    this.cwr = cwr;
  }

  /**
   * @return the ece
   */
  public boolean isECE() {
    return ece;
  }

  /**
   * @param ece the ece to set
   */
  public void setECE(boolean ece) {
    this.ece = ece;
  }

  /**
   * @return the urg
   */
  public boolean isURG() {
    return urg;
  }

  /**
   * @param urg the urg to set
   */
  public void setURG(boolean urg) {
    this.urg = urg;
  }

  /**
   * @return the ack
   */
  public boolean isACK() {
    return ack;
  }

  /**
   * @param ack the ack to set
   */
  public void setACK(boolean ack) {
    this.ack = ack;
  }

  /**
   * @return the psh
   */
  public boolean isPSH() {
    return psh;
  }

  /**
   * @param psh the psh to set
   */
  public void setPSH(boolean psh) {
    this.psh = psh;
  }

  /**
   * @return the rst
   */
  public boolean isRST() {
    return rst;
  }

  /**
   * @param rst the rst to set
   */
  public void setRST(boolean rst) {
    this.rst = rst;
  }

  /**
   * @return the syn
   */
  public boolean isSYN() {
    return syn;
  }

  /**
   * @param syn the syn to set
   */
  public void setSYN(boolean syn) {
    this.syn = syn;
  }

  /**
   * @return the fin
   */
  public boolean isFIN() {
    return fin;
  }

  /**
   * @param fin the fin to set
   */
  public void setFIN(boolean fin) {
    this.fin = fin;
  }

  /**
   * @return the seq
   */
  public int getSeq() {
    return seq;
  }

  /**
   * @param seq the seq to set
   */
  public void setSeq(int seq) {
    this.seq = seq;
  }

  /**
   * @return the ackSeq
   */
  public int getAckSeq() {
    return ackSeq;
  }

  /**
   * @param ackSeq the ackSeq to set
   */
  public void setAckSeq(int ackSeq) {
    this.ackSeq = ackSeq;
  }

  /**
   * @return the window
   */
  public int getWindow() {
    return window;
  }

  /**
   * @param window the window to set
   */
  public void setWindow(int window) {
    this.window = window;
  }

  /**
   * @return the check
   */
  public int getCheck() {
    return check;
  }

  /**
   * @param check the check to set
   */
  public void setCheck(int check) {
    this.check = check;
  }

  /**
   * @return the urgPtr
   */
  public int getUrgPtr() {
    return urgPtr;
  }

  /**
   * @param urgPtr the urgPtr to set
   */
  public void setUrgPtr(int urgPtr) {
    this.urgPtr = urgPtr;
  }

}
