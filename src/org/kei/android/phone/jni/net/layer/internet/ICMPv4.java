package org.kei.android.phone.jni.net.layer.internet;

import java.util.List;

import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;

import android.graphics.Color;

/**
 *******************************************************************************
 * @file ICMPv4.java
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
public class ICMPv4 extends Layer {

  public ICMPv4() {
    super();
  }
  
  @Override
  public int getHeaderLength() {
    return 0;
  }
  
  @Override
  public String getFullName() {
    return "Internet Control Message Protocol v4";
  }

  @Override
  public String getProtocolText() {
    return "ICMPv4";
  }

  @Override
  public String getDescriptionText() {
    background = Color.parseColor("#FCE0FF");
    foreground = Color.parseColor("#000000");
    return null;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    
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
