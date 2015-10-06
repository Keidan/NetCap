package org.kei.android.phone.net.layer.internet.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

/**
 *******************************************************************************
 * @file ICMPCodeParameterProblem.java
 * @author Keidan
 * @date 05/10/2015
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
public class ICMPCodeParameterProblem implements IICMPCodes {

  private boolean ipv6 = false;
  
  public ICMPCodeParameterProblem(boolean ipv6) {
    this.ipv6 = ipv6;
  }
  
  public boolean isIPv6() {
    return ipv6;
  }
  
  @SuppressLint("UseSparseArrays")
  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<Integer, String>();
    if(!isIPv6()) {
      codes.put(0, "Pointer indicates the error");
      codes.put(1, "Missing a Required Option");
      codes.put(2, "Bad Length");
    } else {
      codes.put(0, "Erroneous header field encountered");
      codes.put(1, "Unrecognized Next Header type encountered");
      codes.put(2, "Unrecognized IPv6 option encountered");
      codes.put(3, "IPv6 First Fragment has incomplete IPv6 Header Chain");
    }
    return codes;
  }
  
}
  