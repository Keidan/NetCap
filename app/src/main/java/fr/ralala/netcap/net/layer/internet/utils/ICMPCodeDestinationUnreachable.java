package fr.ralala.netcap.net.layer.internet.utils;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class ICMPCodeDestinationUnreachable implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodeDestinationUnreachable(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    if (!isIPv6()) {
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
    } else {
      codes.put(0, "No route to destination");
      codes.put(1, "Communication with destination administratively prohibited");
      codes.put(2, "Beyond scope of source address");
      codes.put(3, "Address unreachable");
      codes.put(4, "Port unreachable");
      codes.put(5, "Source address failed ingress/egress policy");
      codes.put(6, "Reject route to destination");
      codes.put(7, "Error in Source Routing Header");
    }
    return codes;

  }

}
