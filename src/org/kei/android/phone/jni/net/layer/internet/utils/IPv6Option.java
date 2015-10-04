package org.kei.android.phone.jni.net.layer.internet.utils;

/**
 *******************************************************************************
 * @file IPv6Option.java
 * @author Keidan
 * @date 04/10/2015
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
public class IPv6Option {
  private int type;
  private int length;
  private byte [] mld;
  
  /**
   * @return the type
   */
  public int getType() {
    return type;
  }
  /**
   * @param type the type to set
   */
  public void setType(int type) {
    this.type = type;
  }
  /**
   * @return the length
   */
  public int getLength() {
    return length;
  }
  /**
   * @param length the length to set
   */
  public void setLength(int length) {
    this.length = length;
  }
  /**
   * @return the mld
   */
  public byte[] getMLD() {
    return mld;
  }
  /**
   * @param mld the mld to set
   */
  public void setMLD(byte[] mld) {
    this.mld = mld;
  }
}
