package org.kei.android.phone.jni.net.layer.application;

import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file DHCPv4.java
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
public class DHCPv4 extends Layer {

  public DHCPv4() {
    super(TYPE_DHCPv4);
  }

  @Override
  public String getProtocolText() {
    return "DHCPv4";
  }

  @Override
  public String getDescriptionText() {
    return null;
  }

}
