package org.kei.android.phone.jni.net.layer.application.utils;

/**
 *******************************************************************************
 * @file DHCPv6Option.java
 * @author Keidan
 * @date 04/10/2015
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
public class DHCPv6Option {
  private int code;
  private int length;
  private byte[] datas;
  private String sdatas;
  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }
  /**
   * @param code the code to set
   */
  public void setCode(int code) {
    this.code = code;
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
   * @return the datas
   */
  public byte[] getDatas() {
    return datas;
  }
  /**
   * @param datas the datas to set
   */
  public void setDatas(byte[] datas) {
    this.datas = datas;
  }
  /**
   * @return the datas
   */
  public String getSDatas() {
    return sdatas;
  }
  /**
   * @param datas the datas to set
   */
  public void setSDatas(String sdatas) {
    this.sdatas = sdatas;
  }
  
}
