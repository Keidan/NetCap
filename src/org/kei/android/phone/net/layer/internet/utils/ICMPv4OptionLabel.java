package org.kei.android.phone.net.layer.internet.utils;

import java.util.Map;

/**
 *******************************************************************************
 * @file ICMPv4OptionLabel.java
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
public enum ICMPv4OptionLabel {
  UNKNOWN(-1, "Unknown", null),
  ECHO_REPLY(0, "Echo Reply", null),
  DESTINATION_UNREACHABLE(3, "Destination Unreachable", new ICMPCodeDestinationUnreachable(false)),
  SOURCE_QUENCH(4, "Source Quench", null),
  REDIRECT(5, "Redirect", new ICMPCodeRedirect(false)),
  ALTERNATE_HOST_ADDRESS(6, "Alternate Host Address", new ICMPCodeAlternateHostAddress(false)),
  ECHO(8, "Echo", null),
  ROUTER_ADVERTISEMENT(9, "Router Advertisement", null),
  REOUTER_SELECTION(10, "Router Selection", null),
  TIME_EXCEEDED(11, "Time Exceeded", new ICMPCodeTimeExceeded(false)),
  PARAMETER_PROBLEM(12, "Parameter Problem", new ICMPCodeParameterProblem(false)),
  TIMESTAMP(13, "Timestamp", null),
  TIMESTAMP_REPLY(14, "Timestamp Reply", null),
  INFORMATION_REQUEST(15, "Information Request", null),
  INFORMATION_REPLY(16, "Information Reply", null),
  ADDRESS_MASK_REQUEST(17, "Address Mask Request", null),
  ADDRESS_MASK_REPLY(18, "Address Mask Reply", null),
  TRACEROUTE(30, "Traceroute", null),
  DATAGRAM_CONVERSION_ERROR(31, "Datagram Conversion Error", null),
  MOBILE_HOST_REDIRECT(32, "Mobile Host Redirect", null),
  IPv6_WHERE_ARE_YOU(33, "IPv6 Where-Are-You", null),
  IPv6_I_AM_HERE(34, "IPv6 I-Am-Here", null),
  MOBILE_REGISTRATION_REQUEST(35, "Mobile Registration Request", null),
  MOBILE_REGISTRATION_REPLY(36, "Mobile Registration Reply", null),
  DOMAIN_NAME_REQUEST(37, "Domain Name Request", null),
  DOMAIN_NAME_REPLY(38, "Domain Name Reply", null),
  SKIP(39, "SKIP", null),
  PHOTURIS(40, "Photuris", new ICMPCodePhoturis(false));
 
  ICMPv4OptionLabel(int num, String text, IICMPCodes codes) {
    this.num = num;
    this.text = text;
    this.codes = codes;
  }
  
  private String    text;
  private int       num;
  private IICMPCodes codes;
  
  /**
   * @return the text
   */
  public String getText() {
    return text;
  }
  
  /**
   * @return the num
   */
  public int getNum() {
    return num;
  }

  /**
   * @return the code
   */
  public String getCode(int code) {
    if(codes != null) {
      Map<Integer, String> codes = this.codes.getCodes();
      if(codes != null && !codes.containsKey(codes))
        return codes.get(code);
    }
    return null;
  }
  
  public static ICMPv4OptionLabel findByNumber(int num) {
    ICMPv4OptionLabel [] values = ICMPv4OptionLabel.values();
    for(ICMPv4OptionLabel l : values) {
      if(l.getNum() == num) return l;
    }
    return UNKNOWN;
  }
}
