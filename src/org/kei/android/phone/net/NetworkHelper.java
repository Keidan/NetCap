package org.kei.android.phone.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.Payload;
import org.kei.android.phone.net.layer.link.Ethernet;

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
    Ethernet eth = new Ethernet();
    eth.decodeLayer(buffer, null);
    return eth;
  }

  public static Layer getLastLayer(Layer layer) {
    Layer last = null;
    do {
      if (layer != null && !Payload.class.isInstance(layer)) {
        last = layer;
      }
    } while ((layer = layer.getNext()) != null);
    return last;
  }
  
  public static void zcopy(byte [] src, int srcPos, byte [] dst, int dstPos, int length) {
    for(int i = 0; i < dst.length; i++) dst[i] = (byte)0x00;
    System.arraycopy(src, srcPos, dst, dstPos, length);
  }
  
  public static byte[] htonl(int x) {
    byte [] res = new byte[4];
    for (int i=0; i<4; i++) {
      res[i] = (byte)(x >>> 24);
      x <<= 8;
    }
    return res;
  }
  
  public static int ntohl(byte [] x) {
    int res = 0;
    for (int i=0; i<4; i++) {
      res <<= 8;
      res |= (int)x[i];
    }
    return res;
  }
  
  public static short getValue(final byte[] data) {
    short value = data[1];
    value = (short)((value << 8) | data[0]);
    return value;
  }
  
  public static short ntohs(byte[] value) {
    ByteBuffer buf = ByteBuffer.wrap(value);
    return buf.getShort();
  }
  
  public static int ntohs2(byte[] value) {
    return ntohs(value) & 0x0000ffff;
  }

  public static byte[] htons(short sValue) {
    byte[] baValue = new byte[2];
    ByteBuffer buf = ByteBuffer.wrap(baValue);
    return buf.putShort(sValue).array();
  }
  
  public static byte setBit(byte b, int pos, boolean value) {
    if(value)
      return (byte)(b | (1 << pos));
    return (byte)(b & ~(1 << pos));
  }
  
  public static int getUShort(byte[] msg, int pos) {
    return (((msg[pos] & 0xFF) << 8) | (msg[pos + 1] & 0xFF));
  }
  public static int getInt(byte[] msg, int pos) {
    return ((getUShort(msg, pos) << 16) | getUShort(msg, pos + 2));
  }
}
