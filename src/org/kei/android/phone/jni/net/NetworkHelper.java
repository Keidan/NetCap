package org.kei.android.phone.jni.net;

import java.util.List;

import org.kei.android.phone.jni.JniException;
import org.kei.android.phone.jni.net.capture.PCAPHeader;
import org.kei.android.phone.jni.net.layer.Layer;

/**
 *******************************************************************************
 * @file NetworkHelper.java
 * @author Keidan
 * @date 07/09/2015
 * @par Project NetCap
 *
 * @par Copyright 2015 Keidan, all right reserved
 *
 *      This software is distributed in the hope that it will be useful, but
 *      WITHOUT ANY WARRANTY.
 *
 *      License summary : You can modify and redistribute the sources code and
 *      binaries. You can send me the bug-fix
 *
 *      Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class NetworkHelper {

  static {
    System.loadLibrary("netcap-jni");
  }

  /**
   * Load Network interface informations from the system.
   * 
   * @param name
   *          The interface name.
   * @return {@link org.kei.android.phone.jni.net.NetworkInterface}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native NetworkInterface getInterface(String name) throws JniException;

  /**
   * Load Network interfaces from the system.
   * 
   * @return java.util.List<
   *         {@link org.kei.android.phone.jni.net.NetworkInterface}>
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native List<NetworkInterface> getInterfaces() throws JniException;
  
  /**
   * Test if the current file is a PCAP file.
   * 
   * @return boolean
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native boolean isPCAP(final String filename) throws JniException;
  
  /**
   * Get the PCAP header from the file.
   * 
   * @return {@link org.kei.android.phone.jni.net.captrue.PCAPHeader}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native PCAPHeader getPCAPHeader(final String filename) throws JniException;

  /**
   * Decode the buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return The layer.
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native Layer decodeLayer(byte[] buffer) throws JniException;

  /**
   * Convert the input buffer to an Hex string.
   * 
   * @param buffer
   *          The buffer.
   * @return List<String>
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native List<String> formatToHex(byte[] buffer, int offset) throws JniException;
  
  public static List<String> formatToHex(byte[] buffer) throws JniException {
    return formatToHex(buffer, 0);
  }

  public static String getProtocol(final byte[] buffer) {
    String prot = "ERR";
    try {
      Layer layer = NetworkHelper.decodeLayer(buffer);
      Layer last;
      do {
        last = layer;
      } while((layer = layer.getNext()) != null);
      if(last != null) {
        switch (last.getType()) {
          case Layer.TYPE_ETHERNET: prot = ("ETH"); break;
          case Layer.TYPE_IPv4: prot = ("IPv4"); break;
          case Layer.TYPE_IPv6: prot = ("IPv6"); break;
          case Layer.TYPE_ICMPv4: prot = ("ICMPv4"); break;
          case Layer.TYPE_ICMPv6: prot = ("ICMPv6"); break;
          case Layer.TYPE_TCP: prot = ("TCP"); break;
          case Layer.TYPE_UDP: prot = ("UDP"); break;
          case Layer.TYPE_ARP: prot = ("ARP"); break;
          case Layer.TYPE_DHCPv4: prot = ("DHCPv4"); break;
          case Layer.TYPE_DHCPv6: prot = ("DHCPv6"); break;
          case Layer.TYPE_NDP: prot = ("NDP"); break;
          case Layer.TYPE_DNS: prot = ("DNS"); break;
          case Layer.TYPE_PAYLOAD: prot = ("PAYLOAD"); break;
          default: prot = ("UNKNOWN"); break;
        }
      }
    } catch (JniException e) {
      e.printStackTrace();
    }
    return prot;
  }
}
