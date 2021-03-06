package fr.ralala.netcap.net.layer.internet.utils;

import java.util.Map;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public enum ICMPv6OptionLabel {
  UNKNOWN(-1, "Unknown", null),
  DEST_UNREACHABLE(1, "Destination Unreachable", new ICMPCodeDestinationUnreachable(true)),
  PACKET_TOO_BIG(2, "Packet Too Big", null),
  TIME_EXCEEDED(3, "Time Exceeded", new ICMPCodeTimeExceeded(true)),
  PARAMETER_PROBLEM(4, "Parameter Problem", new ICMPCodeParameterProblem(true)),
  RESV_EIEM(127, "Reserved for expansion of ICMPv6 error messages", null),
  ECHO_REQUEST(128, "Echo Request", null),
  ECHO_REPLY(129, "Echo Reply", null),
  MLQ(130, "Multicast Listener Query", null),
  MLR(131, "Multicast Listener Report", null),
  MLD(132, "Multicast Listener Done", null),
  RS(133, "Router Solicitation", null),
  RA(134, "Router Advertisement", null),
  NS(135, "Neighbor Solicitation", null),
  NA(136, "Neighbor Advertisement", null),
  RM(137, "Redirect Message", null),
  RR(138, "Router Renumbering", new ICMPCodeRouterRenumbering(true)),
  ICMP_NIQ(139, "ICMP Node Information Query", new ICMPCodeNodeInformationQuery(true)),
  ICMP_NIR(140, "ICMP Node Information Response", new ICMPCodeNodeInformationResponse(true)),
  INDSM(141, "Inverse Neighbor Discovery Solicitation Message", null),
  INDAM(142, "Inverse Neighbor Discovery Advertisement Message", null),
  MLR_V2(143, "Version 2 Multicast Listener Report", null),
  HAADREQM(144, "Home Agent Address Discovery Request Message", null),
  HAADREPM(145, "Home Agent Address Discovery Reply Message", null),
  MPS(146, "Mobile Prefix Solicitation", null),
  MPA(147, "Mobile Prefix Advertisement", null),
  CPSM(148, "Certification Path Solicitation Message", null),
  CPAM(149, "Certification Path Advertisement Message", null),
  MRA(151, "Multicast Router Advertisement", null),
  MRS(152, "Multicast Router Solicitation", null),
  MRT(153, "Multicast Router Termination", null),
  FMIPV6_MSG(154, "FMIPv6 Messages", null),
  RPL_CM(155, "RPL Control Message", null),
  ILNPV6_LUM(156, "ILNPv6 Locator Update Message", null),
  DAR(157, "Duplicate Address Request", null),
  DAC(158, "Duplicate Address Confirmation", null),
  MPL_CM(159, "MPL Control Message", null);


  private final String mText;
  private final int mNum;
  private final IICMPCodes mCodes;

  ICMPv6OptionLabel(int num, String text, IICMPCodes codes) {
    mNum = num;
    mText = text;
    mCodes = codes;
  }

  /**
   * @return the text
   */
  public String getText() {
    return mText;
  }

  /**
   * @return the num
   */
  public int getNum() {
    return mNum;
  }

  /**
   * @return the code
   */
  public String getCode(int code) {
    if (mCodes != null) {
      Map<Integer, String> codes = mCodes.getCodes();
      if (codes != null && !codes.containsKey(code))
        return codes.get(code);
    }
    return null;
  }


  public static ICMPv6OptionLabel findByNumber(int num) {
    ICMPv6OptionLabel[] values = ICMPv6OptionLabel.values();
    for (ICMPv6OptionLabel l : values) {
      if (l.getNum() == num) return l;
    }
    return UNKNOWN;
  }
}
