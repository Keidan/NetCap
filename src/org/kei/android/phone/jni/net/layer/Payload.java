package org.kei.android.phone.jni.net.layer;

import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;

/**
 *******************************************************************************
 * @file Payload.java
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
public class Payload extends Layer {
  private byte[] datas = null;
  
  public Payload() {
    super(TYPE_PAYLOAD);
  }

  @Override
  public String getFullName() {
    return "Payload";
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    byte [] buffer = getDatas();
    List<String> l = NetworkHelper.formatBuffer(buffer);
    for(String s : l) lines.add(s);
  }

  /**
   * Get the byte representation of the datas.
   * 
   * @return byte []
   */
  public byte[] getDatas() {
    return datas;
  }
  
  /**
   * Set the byte representation of the datas
   * 
   * @param datas
   *          The datas
   */
  public void setDatas(final byte[] datas) {
    this.datas = datas;
  }


  @Override
  public String getProtocolText() {
    return "PL";
  }


  @Override
  public String getDescriptionText() {
    return "data length: " + getLayerLength();
  }
}
