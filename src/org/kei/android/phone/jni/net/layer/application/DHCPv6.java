package org.kei.android.phone.jni.net.layer.application;

import java.util.List;

import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file DHCPv6.java
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
public class DHCPv6 extends Layer {

  public DHCPv6() {
    super(TYPE_DHCPv6);
  }
  
  @Override
  public String getFullName() {
    return "Dynamic Host Configuration Protocol v6";
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    
  }

  @Override
  public String getProtocolText() {
    return "DHCPv6";
  }

  @Override
  public String getDescriptionText() {
    return null;
  }
}
