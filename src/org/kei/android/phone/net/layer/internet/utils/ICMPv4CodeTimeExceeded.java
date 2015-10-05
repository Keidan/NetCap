package org.kei.android.phone.net.layer.internet.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

/**
 *******************************************************************************
 * @file ICMPv4CodeTimeExceeded.java
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
public class ICMPv4CodeTimeExceeded implements IICMPCodes {

  @SuppressLint("UseSparseArrays")
  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<Integer, String>();
    codes.put(0, "Time to Live exceeded in Transit");
    codes.put(1, "Fragment Reassembly Time Exceeded");
    return codes;
  }
  
}
  