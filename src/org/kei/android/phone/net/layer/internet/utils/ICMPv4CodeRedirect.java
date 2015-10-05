package org.kei.android.phone.net.layer.internet.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

/**
 *******************************************************************************
 * @file ICMPv4CodeRedirect.java
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
public class ICMPv4CodeRedirect implements IICMPCodes {

  @SuppressLint("UseSparseArrays")
  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<Integer, String>();
    codes.put(0, "Redirect Datagram for the Network (or subnet)");
    codes.put(1, "Redirect Datagram for the Host");
    codes.put(2, "Redirect Datagram for the Type of Service and Network");
    codes.put(3, "Redirect Datagram for the Type of Service and Host");
    return codes;
  }
  
}
