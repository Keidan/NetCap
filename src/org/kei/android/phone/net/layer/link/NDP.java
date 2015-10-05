package org.kei.android.phone.net.layer.link;

import java.util.List;

import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.Payload;

/**
 *******************************************************************************
 * @file NDP.java
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
public class NDP extends Layer {

  public NDP() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Neighbor Discovery Protocol";
  }

  @Override
  public String getProtocolText() {
    return "NDP";
  }

  @Override
  public String getDescriptionText() {
    return null;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    
  }
  
  @Override
  public int getHeaderLength() {
    return 0;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {

    byte [] sub_buffer = resizeBuffer(buffer);
    if(sub_buffer != null) {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

}
