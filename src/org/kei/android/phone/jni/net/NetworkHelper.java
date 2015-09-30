package org.kei.android.phone.jni.net;

import java.util.ArrayList;
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
  
  public static List<String> formatBuffer(final byte [] buffer) {
    int max = 16, length = buffer.length;
    String line = "", eline = "";
    List<String> lines = new ArrayList<String>();
    int i = 0, j = 0;
    while(length > 0) {
      byte c = buffer[j++];
      line += String.format("%02x ", c);
      /* only the visibles char */
      if(c >= 0x20 && c <= 0x7e) eline += (char)c;
      else eline += (char)0x2e; /* . */
      if(i == max - 1) {
        lines.add(line + " " + eline);
        line = eline = "";
        i = 0;
      } else i++;
      /* add a space in the midline */
      if(i == max / 2) {
        line += " ";
        eline += " ";
      }
      length--;
    }
    /* align 'line'*/
    if(i != 0 && (i < max || i <= buffer.length)) {
      String off = "";
      while(i++ <= max) off += "   "; /* 3 spaces ex: "00 " */
      if(line.endsWith(" ")) line = line.substring(0, line.length() - 1);
      lines.add(line + off + eline);
    }
    return lines;
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
