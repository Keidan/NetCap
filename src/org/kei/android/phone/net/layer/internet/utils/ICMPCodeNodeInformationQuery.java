package org.kei.android.phone.net.layer.internet.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

/**
 *******************************************************************************
 * @file ICMPCodeNodeInformationQuery.java
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
public class ICMPCodeNodeInformationQuery implements IICMPCodes {

  private boolean ipv6 = false;
  
  public ICMPCodeNodeInformationQuery(boolean ipv6) {
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
    } else {
      codes.put(0, "The Data field contains an IPv6 address which is the Subject of this Query.");
      codes.put(1, "The Data field contains a name which is the Subject of this Query, or is empty, as in the case of a NOOP.");
      codes.put(2, "The Data field contains an IPv4 address which is the Subject of this Query.");
    }
    return codes;
  }
  
}