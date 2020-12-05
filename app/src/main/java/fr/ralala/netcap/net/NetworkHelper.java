package fr.ralala.netcap.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;
import fr.ralala.netcap.net.layer.link.Ethernet;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class NetworkHelper {

  /**
   * Formats the buffer like the wireshark application.
   * @param buffer The buffer to be formatted.
   * @return List<String>
   */
  public static List<String> formatBuffer(final byte[] buffer) {
    int max = 16, length = buffer.length;
    StringBuilder line = new StringBuilder();
    StringBuilder eline = new StringBuilder();
    List<String> lines = new ArrayList<>();
    int i = 0, j = 0;
    while (length > 0) {
      byte c = buffer[j++];
      line.append(String.format(Locale.US, "%02x ", c));
      /* only the visible char */
      if (c >= 0x20 && c <= 0x7e) eline.append((char) c);
      else eline.append((char) 0x2e); /* . */
      if (i == max - 1) {
        lines.add(line + " " + eline);
        line = new StringBuilder(eline = new StringBuilder());
        i = 0;
      } else i++;
      /* add a space in the midline */
      if (i == max / 2) {
        line.append(" ");
        eline.append(" ");
      }
      length--;
    }
    /* align 'line'*/
    if (i != 0 && (i < max || i <= buffer.length)) {
      StringBuilder off = new StringBuilder();
      while (i++ <= max) off.append("   "); /* 3 spaces ex: "00 " */
      if (line.toString().endsWith(" "))
        line = new StringBuilder(line.substring(0, line.length() - 1));
      lines.add(line + off.toString() + eline);
    }
    return lines;
  }

  /**
   * Returns the layer based on the input buffer.
   * @param buffer The input buffer.
   * @return Layer
   */
  public static Layer getLayer(final byte[] buffer) {
    Ethernet eth = new Ethernet();
    eth.decodeLayer(buffer, null);
    return eth;
  }

  /**
   * Returns the last layer.
   * @param layer The reference layer.
   * @return Layer
   */
  public static Layer getLastLayer(Layer layer) {
    Layer last = null;
    do {
      if (layer != null && !(layer instanceof Payload)) {
        last = layer;
      }
    } while ((layer = layer.getNext()) != null);
    return last;
  }

  /**
   * Copies the src array into the dst array and sets unused bytes in the dst array to zero.
   * @param src The source array.
   * @param srcPos The source position.
   * @param dst The destination array.
   * @param dstPos The destination position.
   * @param length The length
   */
  public static void zcopy(byte[] src, int srcPos, byte[] dst, int dstPos, int length) {
    Arrays.fill(dst, (byte) 0x00);
    System.arraycopy(src, srcPos, dst, dstPos, length);
  }

  /**
   * Converts the unsigned integer hostlong from host byte order to network byte order.
   * @param x Host long.
   * @return byte[]
   */
  public static byte[] htonl(int x) {
    byte[] res = new byte[4];
    for (int i = 0; i < 4; i++) {
      res[i] = (byte) (x >>> 24);
      x <<= 8;
    }
    return res;
  }

  /**
   * Converts the unsigned integer netlong from network byte order to host byte order.
   * @param x Net long.
   * @return byte[]
   */
  public static int ntohl(byte[] x) {
    int res = 0;
    for (int i = 0; i < 4; i++) {
      res <<= 8;
      res |= (int) x[i];
    }
    return res;
  }

  /**
   * Transforms a byte array into a shorts.
   * @param data Byte array.
   * @return short
   */
  public static short getValue(final byte[] data) {
    short value = data[1];
    value = (short) ((value << 8) | data[0]);
    return value;
  }

  /**
   * Converts the unsigned short integer netshort from network byte order to host byte order.
   * @param value Net short.
   * @return short
   */
  public static short ntohs(byte[] value) {
    ByteBuffer buf = ByteBuffer.wrap(value);
    return buf.getShort();
  }

  /**
   * Converts the unsigned short integer netshort from network byte order to host byte order.
   * @param value Net short.
   * @return int
   */
  public static int ntohs2(byte[] value) {
    return ntohs(value) & 0x0000ffff;
  }

  /**
   * Converts the unsigned short integer hostshort from host byte order to network byte order.
   * @param sValue Host short.
   * @return byte[]
   */
  public static byte[] htons(short sValue) {
    byte[] baValue = new byte[2];
    ByteBuffer buf = ByteBuffer.wrap(baValue);
    return buf.putShort(sValue).array();
  }

  /**
   * Modification of a specific bit value.
   * @param b The byte to be modified.
   * @param pos The bit position.
   * @param value The new value.
   * @return The modified byte.
   */
  public static byte setBit(byte b, int pos, boolean value) {
    if (value)
      return (byte) (b | (1 << pos));
    return (byte) (b & ~(1 << pos));
  }

  /**
   * Transforms a byte array into unsigned shorts.
   * @param msg Byte array.
   * @param pos Byte array pos.
   * @return int
   */
  public static int getUShort(byte[] msg, int pos) {
    return (((msg[pos] & 0xFF) << 8) | (msg[pos + 1] & 0xFF));
  }

  /**
   * Transforms a byte array into an integer.
   * @param msg Byte array.
   * @param pos Byte array pos.
   * @return int
   */
  public static int getInt(byte[] msg, int pos) {
    return ((getUShort(msg, pos) << 16) | getUShort(msg, pos + 2));
  }
}
