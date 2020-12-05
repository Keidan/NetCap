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
public class ICMPCodeNodeInformationQuery implements IICMPCodes {

  private boolean mIpv6;

  public ICMPCodeNodeInformationQuery(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    if (isIPv6()) {
      codes.put(0, "The Data field contains an IPv6 address which is the Subject of this Query.");
      codes.put(1, "The Data field contains a name which is the Subject of this Query, or is empty, as in the case of a NOOP.");
      codes.put(2, "The Data field contains an IPv4 address which is the Subject of this Query.");
    }
    return codes;
  }

}