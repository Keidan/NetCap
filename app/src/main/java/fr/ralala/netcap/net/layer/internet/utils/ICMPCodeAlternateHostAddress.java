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
public class ICMPCodeAlternateHostAddress implements IICMPCodes {

  private final boolean mIpv6;

  public ICMPCodeAlternateHostAddress(boolean ipv6) {
    mIpv6 = ipv6;
  }

  public boolean isIPv6() {
    return mIpv6;
  }

  @SuppressLint("UseSparseArrays")
  @Override
  public Map<Integer, String> getCodes() {
    Map<Integer, String> codes = new HashMap<Integer, String>();
    codes.put(0, "Alternate Address for Host");
    return codes;
  }

}