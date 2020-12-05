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
public class ICMPCodeNodeInformationResponse implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodeNodeInformationResponse(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<>();
    if (isIPv6()) {
      codes.put(0, "A successful reply. The Reply Data field may or may not be empty.");
      codes.put(1, "The Responder refuses to supply the answer. The Reply Data field will be empty.");
      codes.put(2, "The Qtype of the Query is unknown to the Responder. The Reply Data field will be empty.");
    }
    return codes;
  }

}