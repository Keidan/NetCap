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
  
  public static Layer getLayer(final byte[] buffer) {
    try {
      return NetworkHelper.decodeLayer(buffer);
    } catch (JniException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Layer getLastLayer(Layer layer) {
    Layer last = null;
    do {
      if (layer != null && layer.getLayerType() != Layer.TYPE_PAYLOAD) {
        last = layer;
      }
    } while ((layer = layer.getNext()) != null);
    return last;
  }
}
