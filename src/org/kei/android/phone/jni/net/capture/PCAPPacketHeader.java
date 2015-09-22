package org.kei.android.phone.jni.net.capture;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *******************************************************************************
 * @file PCAPPacketHeader.java
 * @author Keidan
 * @date 10/09/2015
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
public class PCAPPacketHeader {
  @SuppressLint("SimpleDateFormat")
  private static final SimpleDateFormat SDF     = new SimpleDateFormat(
                                                    "yyyy.MM.dd HH:mm:ss");
  private long                          tsSec   = 0;
  private long                          tsUsec  = 0;
  private long                          inclLen = 0;
  private long                          origLen = 0;
  
  public String getTime() {
    return SDF.format(new Date(tsSec * 1000)) + "." + tsUsec;
  }
  
  /**
   * Get the timestamp seconds.
   * @return unsigned int
   */
  public long getTsSec() {
    return tsSec;
  }
  
  /**
   * Set the timestamp seconds.
   * @param tsSec New value.
   */
  public void setTsSec(long tsSec) {
    this.tsSec = tsSec;
  }
  
  /**
   * Get the timestamp microseconds.
   * @return unsigned int
   */
  public long getTsUsec() {
    return tsUsec;
  }
  
  /**
   * Set the timestamp microseconds.
   * @param tsUsec New value.
   */
  public void setTsUsec(long tsUsec) {
    this.tsUsec = tsUsec;
  }
  
  /**
   * Get the number of octets of packet saved in file.
   * @return unsigned int
   */
  public long getInclLen() {
    return inclLen;
  }
  
  /**
   * Set the number of octets of packet saved in file.
   * @param inclLen New value.
   */
  public void setInclLen(long inclLen) {
    this.inclLen = inclLen;
  }
  
  /**
   * Get the actual length of packet.
   * @return unsigned int
   */
  public long getOrigLen() {
    return origLen;
  }
  
  /**
   * Set the actual length of packet.
   * @param origLen New value.
   */
  public void setOrigLen(long origLen) {
    this.origLen = origLen;
  }
  
  
}
