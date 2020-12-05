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
public class ICMPCodePhoturis implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodePhoturis(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    codes.put(0, "Reserved");
    codes.put(1, "Unknown security parameters index");
    codes.put(2, "Valid security parameters, but authentication failed");
    codes.put(3, "Valid security parameters, but decryption failed");
    return codes;
  }

}