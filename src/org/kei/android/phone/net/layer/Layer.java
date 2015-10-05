package org.kei.android.phone.net.layer;

import org.kei.android.phone.net.NetworkHelper;

/**
 *******************************************************************************
 * @file Layer.java
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
public abstract class Layer extends LayerUI {
  private Layer           next          = null;
  
  /**
   * Allocate the object.
   */
  public Layer() {
  }
  
  public abstract int getHeaderLength();
  
  public abstract void decodeLayer(final byte [] buffer, Layer owner);
  
  
  protected byte[] resizeBuffer(byte [] buffer) {
    if(buffer.length - getHeaderLength() > 0) {
      byte [] sub_buffer = new byte[buffer.length - getHeaderLength()];
      NetworkHelper.zcopy(buffer, getHeaderLength(), sub_buffer, 0, sub_buffer.length);
      return sub_buffer;
    }
    return null;
  }
  
  /**
   * Get the next layer.
   * 
   * @return {@link org.kei.android.phone.net.layer.Layer}
   */
  public Layer getNext() {
    return next;
  }
  
  /**
   * Set the next layer
   * 
   * @param next
   *          the Layer.
   */
  public void setNext(final Layer next) {
    this.next = next;
  }

}
