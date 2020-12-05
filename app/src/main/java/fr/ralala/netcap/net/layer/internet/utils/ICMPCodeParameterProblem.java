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
public class ICMPCodeParameterProblem implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodeParameterProblem(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    if (!isIPv6()) {
      codes.put(0, "Pointer indicates the error");
      codes.put(1, "Missing a Required Option");
      codes.put(2, "Bad Length");
    } else {
      codes.put(0, "Erroneous header field encountered");
      codes.put(1, "Unrecognized Next Header type encountered");
      codes.put(2, "Unrecognized IPv6 option encountered");
      codes.put(3, "IPv6 First Fragment has incomplete IPv6 Header Chain");
    }
    return codes;
  }

}
  