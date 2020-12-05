package fr.ralala.netcap.net.layer.internet.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class ICMPCodeRedirect implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodeRedirect(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    codes.put(0, "Redirect Datagram for the Network (or subnet)");
    codes.put(1, "Redirect Datagram for the Host");
    codes.put(2, "Redirect Datagram for the Type of Service and Network");
    codes.put(3, "Redirect Datagram for the Type of Service and Host");
    return codes;
  }

}
