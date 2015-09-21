package org.kei.android.phone.jni.net;

import java.util.List;

import org.kei.android.phone.jni.JniException;
import org.kei.android.phone.jni.net.capture.PCAPHeader;
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
   * Decode the buffer in accordance to the layer type.
   * 
   * @param type
   *          The layer type.
   * @param buffer
   *          The buffer.
   * @return The layer.
   * @throws JniException
   *           If an exception has occurred.
   */
  private static native Layer decodeLayer(int type, byte[] buffer) throws JniException;

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

  /**
   * Extract the Ethernet layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.link.Ethernet}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static Ethernet decodeEthernet(final byte[] buffer) throws JniException {
    return (Ethernet) decodeLayer(Layer.TYPE_ETHERNET, buffer);
  }

  /**
   * Extract the IPv4 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.IPv4}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static IPv4 decodeIPv4(final byte[] buffer) throws JniException {
    return (IPv4) decodeLayer(Layer.TYPE_IPv4, buffer);
  }

  /**
   * Extract the IPv6 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.IPv6}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static IPv6 decodeIPv6(final byte[] buffer) throws JniException {
    return (IPv6) decodeLayer(Layer.TYPE_IPv6, buffer);
  }

  /**
   * Extract the ICMPv4 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.ICMPv4}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static ICMPv4 decodeICMPv4(final byte[] buffer) throws JniException {
    return (ICMPv4) decodeLayer(Layer.TYPE_ICMPv4, buffer);
  }

  /**
   * Extract the ICMPv6 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.internet.ICMPv6}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static ICMPv6 decodeICMPv6(final byte[] buffer) throws JniException {
    return (ICMPv6) decodeLayer(Layer.TYPE_ICMPv6, buffer);
  }

  /**
   * Extract the TCP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.transport.TCP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static TCP decodeTCP(final byte[] buffer) throws JniException {
    return (TCP) decodeLayer(Layer.TYPE_TCP, buffer);
  }

  /**
   * Extract the UDP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.transport.UDP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static UDP decodeUDP(final byte[] buffer) throws JniException {
    return (UDP) decodeLayer(Layer.TYPE_UDP, buffer);
  }

  /**
   * Extract the ARP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.transport.TCP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static ARP decodeARP(final byte[] buffer) throws JniException {
    return (ARP) decodeLayer(Layer.TYPE_ARP, buffer);
  }

  /**
   * Extract the DHCPv4 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.application.DHCPv4}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static DHCPv4 decodeDHCPv4(final byte[] buffer) throws JniException {
    return (DHCPv4) decodeLayer(Layer.TYPE_DHCPv4, buffer);
  }

  /**
   * Extract the DHCPv6 layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.application.DHCPv6
}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static DHCPv6 decodeDHCPv6(final byte[] buffer) throws JniException {
    return (DHCPv6) decodeLayer(Layer.TYPE_DHCPv6, buffer);
  }

  /**
   * Extract the NDP layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.link.NDP}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static NDP decodeNDP(final byte[] buffer) throws JniException {
    return (NDP) decodeLayer(Layer.TYPE_NDP, buffer);
  }

  /**
   * Extract the DNS layer of the input buffer.
   * 
   * @param buffer
   *          The buffer.
   * @return {@link org.kei.android.phone.jni.net.layer.application.DNS}
   * @throws JniException
   *           If an exception has occurred.
   */
  public static DNS decodeDNS(final byte[] buffer) throws JniException {
    return (DNS) decodeLayer(Layer.TYPE_DNS, buffer);
  }

}
