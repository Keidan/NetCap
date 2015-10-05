package org.kei.android.phone.jni.net.layer.internet.utils;

/**
 *******************************************************************************
 * @file ICMPv6OptionLabel.java
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
public enum ICMPv6OptionLabel {
  UNKNOWN(-1, "Unknown"),
  DEST_UNREACHABLE(1, "Destination Unreachable"),
  PACKET_TOO_BIG(2, "Packet Too Big"),
  TIME_EXCEEDED(3, "Time Exceeded"),
  PARAMETER_PROBLEM(4, "Parameter Problem"),
  RESV_EIEM(127, "Reserved for expansion of ICMPv6 error messages"),
  ECHO_REQUEST(128, "Echo Request"),
  ECHO_REPLY(129, "Echo Reply"),
  MLQ(130, "Multicast Listener Query"),
  MLR(131, "Multicast Listener Report"),
  MLD(132, "Multicast Listener Done"),
  RS(133, "Router Solicitation"),
  RA(134, "Router Advertisement"),
  NS(135, "Neighbor Solicitation"),
  NA(136, "Neighbor Advertisement"),
  RM(137, "Redirect Message"),
  RR(138, "Router Renumbering"),
  ICMP_NIQ(139, "ICMP Node Information Query"),
  ICMP_NIR(140, "ICMP Node Information Response"),
  INDSM(141, "Inverse Neighbor Discovery Solicitation Message"),
  INDAM(142, "Inverse Neighbor Discovery Advertisement Message"),
  MLR_V2(143, "Version 2 Multicast Listener Report"),
  HAADREQM(144, "Home Agent Address Discovery Request Message"),
  HAADREPM(145, "Home Agent Address Discovery Reply Message"),
  MPS(146, "Mobile Prefix Solicitation"),
  MPA(147, "Mobile Prefix Advertisement"),
  CPSM(148, "Certification Path Solicitation Message"),
  CPAM(149, "Certification Path Advertisement Message"),
  MRA(151, "Multicast Router Advertisement"),
  MRS(152, "Multicast Router Solicitation"),
  MRT(153, "Multicast Router Termination"),
  FMIPV6_MSG(154, "FMIPv6 Messages"),
  RPL_CM(155, "RPL Control Message"),
  ILNPV6_LUM(156, "ILNPv6 Locator Update Message"),
  DAR(157, "Duplicate Address Request"),
  DAC(158, "Duplicate Address Confirmation"),
  MPL_CM(159, "MPL Control Message");
  
  
  ICMPv6OptionLabel(int num, String text) {
    this.num = num;
    this.text = text;
  }
  
  private String text;
  private int num;
  
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
  
  public static ICMPv6OptionLabel findByNumber(int num) {
    ICMPv6OptionLabel [] values = ICMPv6OptionLabel.values();
    for(ICMPv6OptionLabel l : values) {
      if(l.getNum() == num) return l;
    }
    return UNKNOWN;
  }
}
