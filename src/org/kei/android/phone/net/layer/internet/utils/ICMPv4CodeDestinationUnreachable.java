package org.kei.android.phone.net.layer.internet.utils;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

/**
 *******************************************************************************
 * @file ICMPv4CodeDestinationUnreachable.java
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
public class ICMPv4CodeDestinationUnreachable implements IICMPCodes {

  @SuppressLint("UseSparseArrays")
  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<Integer, String>();
    codes.put(0, "Net Unreachable");
    codes.put(1, "Host Unreachable");
    codes.put(2, "Protocol Unreachable");
    codes.put(3, "Port Unreachable");
    codes.put(4, "Fragmentation Needed and Don't Fragment was Set");
    codes.put(5, "Source Route Failed");
    codes.put(6, "Destination Network Unknown");
    codes.put(7, "Destination Host Unknown");
    codes.put(8, "Source Host Isolated");
    codes.put(9, "Communication with Destination Network is Administratively Prohibited");
    codes.put(10, "Communication with Destination Host is Administratively Prohibited");
    codes.put(11, "Destination Network Unreachable for Type of Service");
    codes.put(12, "Destination Host Unreachable for Type of Service");
    codes.put(13, "Communication Administratively Prohibited");
    codes.put(14, "Host Precedence Violation");
    codes.put(15, "Precedence cutoff in effect");
    return codes;
    
  }
  
}
