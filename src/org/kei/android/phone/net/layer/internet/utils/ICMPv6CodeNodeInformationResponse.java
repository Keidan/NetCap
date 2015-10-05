package org.kei.android.phone.net.layer.internet.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

/**
 *******************************************************************************
 * @file ICMPv6CodeNodeInformationResponse.java
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
public class ICMPv6CodeNodeInformationResponse implements IICMPCodes {

  @SuppressLint("UseSparseArrays")
  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<Integer, String>();
    codes.put(0, "A successful reply. The Reply Data field may or may not be empty.");
    codes.put(1, "The Responder refuses to supply the answer. The Reply Data field will be empty.");
    codes.put(2, "The Qtype of the Query is unknown to the Responder. The Reply Data field will be empty.   ");
    return codes;
  }
  
}