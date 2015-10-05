package org.kei.android.phone.net.layer.internet.utils;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

/**
 *******************************************************************************
 * @file ICMPv6CodeDestinationUnreachable.java
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
public class ICMPv6CodeDestinationUnreachable implements IICMPCodes {

  @SuppressLint("UseSparseArrays")
  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<Integer, String>();
    codes.put(0, "No route to destination");
    codes.put(1, "Communication with destination administratively prohibited");
    codes.put(2, "Beyond scope of source address");
    codes.put(3, "Address unreachable");
    codes.put(4, "Port unreachable");
    codes.put(5, "Source address failed ingress/egress policy");
    codes.put(6, "Reject route to destination");
    codes.put(7, "Error in Source Routing Header");
    return codes;
    
  }
  
}
