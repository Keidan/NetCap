package fr.ralala.netcap.net.layer.internet.utils;

import java.util.Map;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public interface IICMPCodes {

  boolean isIPv6();

  Map<Integer, String> getCodes();

}
