package org.kei.android.phone.jni.net.layer.internet;

import java.util.List;

import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file ICMPv6.java
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
public class ICMPv6 extends Layer {

  public ICMPv6() {
    super(TYPE_ICMPv6);
  }

  
  @Override
  public String getFullName() {
    return "Internet Control Message Protocol v6";
  }

  @Override
  public String getProtocolText() {
    return "ICMPv6";
  }

  @Override
  public String getDescriptionText() {
    return null;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    
  }
}
