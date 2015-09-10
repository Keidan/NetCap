package org.kei.android.phone.jni.net;

import java.util.List;

import org.kei.android.phone.jni.JniException;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.application.DHCPv4;
import org.kei.android.phone.jni.net.layer.application.DHCPv6;
import org.kei.android.phone.jni.net.layer.application.DNS;
import org.kei.android.phone.jni.net.layer.internet.ICMPv4;
import org.kei.android.phone.jni.net.layer.internet.ICMPv6;
import org.kei.android.phone.jni.net.layer.internet.IPv4;
import org.kei.android.phone.jni.net.layer.internet.IPv6;
import org.kei.android.phone.jni.net.layer.link.ARP;
import org.kei.android.phone.jni.net.layer.link.Ethernet;
import org.kei.android.phone.jni.net.layer.link.NDP;
import org.kei.android.phone.jni.net.layer.transport.TCP;
import org.kei.android.phone.jni.net.layer.transport.UDP;

/**
 *******************************************************************************
 * @file NetworkHelper.java
 * @author Keidan
 * @date 07/09/2015
 * @par Project NetCap
 *
 * @par Copyright Copyright 2011-2013 Keidan, all right reserved
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
   * Save Network interface changes to the system
   * 
   * @param iface
   *          The interface.
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native void setInterface(final NetworkInterface iface) throws JniException;

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
   * Decode the buffer in accordance to the layer type.
   * 
   * @param type
   *          The layer type.
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return The layer.
   * @throws JniException
   *           If an exception has occurred.
   */
  private static native Layer decodeLayer(int type, byte[] buffer, int offset) throws JniException;

  /**
   * Convert the input buffer to an Hex string.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link java.lang.StringBuilder}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static native StringBuilder formatToHex(byte[] buffer, int offset) throws JniException;

  /**
   * Extract the Ethernet layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.link.Ethernet}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static Ethernet decodeEthernet(final byte[] buffer, final int offset) throws JniException {
    return (Ethernet) decodeLayer(Layer.TYPE_ETHERNET, buffer, offset);
  }

  /**
   * Extract the IPv4 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.IPv4}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static IPv4 decodeIPv4(final byte[] buffer, final int offset) throws JniException {
    return (IPv4) decodeLayer(Layer.TYPE_IPv4, buffer, offset);
  }

  /**
   * Extract the IPv6 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.IPv6}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static IPv6 decodeIPv6(final byte[] buffer, final int offset) throws JniException {
    return (IPv6) decodeLayer(Layer.TYPE_IPv6, buffer, offset);
  }

  /**
   * Extract the ICMPv4 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.ICMPv4}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static ICMPv4 decodeICMPv4(final byte[] buffer, final int offset) throws JniException {
    return (ICMPv4) decodeLayer(Layer.TYPE_ICMPv4, buffer, offset);
  }

  /**
   * Extract the ICMPv6 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.ICMPv6}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static ICMPv6 decodeICMPv6(final byte[] buffer, final int offset) throws JniException {
    return (ICMPv6) decodeLayer(Layer.TYPE_ICMPv6, buffer, offset);
  }

  /**
   * Extract the TCP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.transport.TCP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static TCP decodeTCP(final byte[] buffer, final int offset) throws JniException {
    return (TCP) decodeLayer(Layer.TYPE_TCP, buffer, offset);
  }

  /**
   * Extract the UDP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.transport.UDP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static UDP decodeUDP(final byte[] buffer, final int offset) throws JniException {
    return (UDP) decodeLayer(Layer.TYPE_UDP, buffer, offset);
  }

  /**
   * Extract the ARP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.transport.TCP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static ARP decodeARP(final byte[] buffer, final int offset) throws JniException {
    return (ARP) decodeLayer(Layer.TYPE_ARP, buffer, offset);
  }

  /**
   * Extract the DHCPv4 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.application.DHCPv4}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static DHCPv4 decodeDHCPv4(final byte[] buffer, final int offset) throws JniException {
    return (DHCPv4) decodeLayer(Layer.TYPE_DHCPv4, buffer, offset);
  }

  /**
   * Extract the DHCPv6 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.application.DHCPv6
}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static DHCPv6 decodeDHCPv6(final byte[] buffer, final int offset) throws JniException {
    return (DHCPv6) decodeLayer(Layer.TYPE_DHCPv6, buffer, offset);
  }

  /**
   * Extract the NDP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.link.NDP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static NDP decodeNDP(final byte[] buffer, final int offset) throws JniException {
    return (NDP) decodeLayer(Layer.TYPE_NDP, buffer, offset);
  }

  /**
   * Extract the DNS layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @param offset
   *          The start offset.
   * @return {@link org.kei.android.phone.jni.net.layer.application.DNS}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static DNS decodeDNS(final byte[] buffer, final int offset) throws JniException {
    return (DNS) decodeLayer(Layer.TYPE_DNS, buffer, offset);
  }

}
