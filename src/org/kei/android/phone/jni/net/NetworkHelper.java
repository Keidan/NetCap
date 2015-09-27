package org.kei.android.phone.jni.net;

import java.util.List;
import java.util.Locale;

import org.kei.android.phone.jni.JniException;
import org.kei.android.phone.jni.net.capture.PCAPHeader;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.application.DNS;
import org.kei.android.phone.jni.net.layer.link.ARP;
import org.kei.android.phone.jni.net.layer.transport.TCP;
import org.kei.android.phone.jni.net.layer.transport.UDP;

import android.graphics.Color;
import android.util.Log;

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
  
  public static String getColorsProtocolAndDesc(final byte[] buffer, char split) {
    String prot = "ERR";
    String desc = "";
    int dbcolor = Color.parseColor("#DAEEFF");
    int tcolor = Color.BLACK, bcolor = dbcolor;
    try {
      Layer layer = NetworkHelper.decodeLayer(buffer);
      Layer last = null;
      do {
        if(layer != null && layer.getType() != Layer.TYPE_PAYLOAD) {
          last = layer;
          if(last != null && (last.getType() == Layer.TYPE_UDP || last.getType() == Layer.TYPE_TCP)) {
            desc = "";
            int s = (last.getType() == Layer.TYPE_UDP) ? ((UDP)last).getSource() : ((TCP)last).getSource();
            int d = (last.getType() == Layer.TYPE_UDP) ? ((UDP)last).getDestination() : ((TCP)last).getDestination();
            Service srv = Service.findByPort(s);
            if(srv ==Service.NOT_FOUND) desc += s;
            else {
              desc += srv.getName() + "(" + srv.getPort() + ")";
              if(srv.getName().toLowerCase(Locale.US).contains("http"))
                bcolor = Color.parseColor("#E4FFC7");
            }
            desc += " > ";
            srv = Service.findByPort(d);
            if(srv ==Service.NOT_FOUND) desc += d;
            else {
              desc += srv.getName() + "(" + srv.getPort() + ")";
              if(srv.getName().toLowerCase(Locale.US).contains("http"))
                bcolor = Color.parseColor("#E4FFC7");
            }
            if(last.getType() == Layer.TYPE_TCP) {
              TCP tcp = (TCP)last;
              desc += " [";
              if(bcolor == dbcolor)
                bcolor = Color.parseColor("#E7E6FF");
              if(tcp.isSYN()) desc += "SYN, ";
              if(tcp.isPSH()) desc += "PSH, ";
              if(tcp.isACK()) desc += "ACK, ";
              if(tcp.isCWR()) desc += "CWR, ";
              if(tcp.isECE()) desc += "ECE, ";
              if(tcp.isRST()) desc += "RST, ";
              if(tcp.isURG()) desc += "URG, ";
              if(tcp.isFIN()) desc += "FIN, ";
              if(desc.endsWith(", ")) desc = desc.substring(0, desc.length() - 2);
              desc += "]";
            }
          } else if(last != null && last.getType() == Layer.TYPE_ARP) {
            desc = "";
            ARP arp = (ARP)last;
            if(arp.getOpcode() == ARP.REQUEST)
              desc += "Who is " + arp.getTargetIPAddress() + "?";
            else if(arp.getOpcode() == ARP.REPLY)
              desc += arp.getSenderIPAddress() + " is " + arp.getSenderHardwareAddress();
            else
              desc += "Unknown";
          } else if(last != null && last.getType() == Layer.TYPE_DNS) {
            desc = "";
            DNS dns = (DNS) last;
            if(!dns.isQR()) {
              desc += "Standard query 0x" + String.format("%04x", dns.getID());
            } else
              desc += "Standard query response 0x" + String.format("%04x", dns.getID());
          }
        }
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
    if(desc.isEmpty()) desc = "ERR";
    return String.valueOf(tcolor) + split + String.valueOf(bcolor) + split + prot + split + desc;
  }
}
