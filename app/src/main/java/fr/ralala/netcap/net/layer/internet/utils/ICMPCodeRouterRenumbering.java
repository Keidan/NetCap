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
public class ICMPCodeRouterRenumbering implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodeRouterRenumbering(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    if (isIPv6()) {
      codes.put(0, "Router Renumbering Command");
      codes.put(1, "Router Renumbering Result");
      codes.put(255, "Sequence Number Reset");
    }
    return codes;
  }

}