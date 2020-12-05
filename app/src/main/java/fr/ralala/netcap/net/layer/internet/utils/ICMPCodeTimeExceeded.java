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
public class ICMPCodeTimeExceeded implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodeTimeExceeded(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    if (!isIPv6()) {
      codes.put(0, "Time to Live exceeded in Transit");
      codes.put(1, "Fragment Reassembly Time Exceeded");
    } else {
      codes.put(0, "Hop limit exceeded in transit");
      codes.put(1, "Fragment reassembly time exceeded");
    }
    return codes;
  }

}
  