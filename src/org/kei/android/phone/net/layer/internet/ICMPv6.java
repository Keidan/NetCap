package org.kei.android.phone.net.layer.internet;

import java.util.List;

import org.kei.android.phone.net.NetworkHelper;
import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.internet.utils.ICMPv6OptionLabel;

import android.graphics.Color;

/**
 *******************************************************************************
 * @file ICMPv6.java
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
public class ICMPv6 extends Layer {
  private int    headerLength;
  private int    type;
  private int    code;
  private int    checksum;
  private byte[] optionBytes;
  
  public ICMPv6() {
    super();
  }

  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public String getFullName() {
    return "Internet Control Message Protocol v6";
  }
  
  @Override
  public String getProtocolText() {
    return "ICMPv6";
  }
  
  @Override
  public String getDescriptionText() {
    background = Color.parseColor("#FCE0FF");
    foreground = Color.parseColor("#000000");
    return ICMPv6OptionLabel.findByNumber(getType()).getText();
  }

  @Override
  public void buildDetails(final List<String> lines) {
    final ICMPv6OptionLabel label = ICMPv6OptionLabel.findByNumber(getType());
    lines.add("  Type: " + label.getText() + " (" + getType() + ")");
    lines.add("  Code: " + getCode());
    lines.add("  Checksum: 0x" + String.format("%04x", getChecksum()));
    if (getOptionBytes() != null && getOptionBytes().length > 0) {
      lines.add("  Options: " + getOptionBytes().length + " bytes");
      final List<String> l = NetworkHelper.formatBuffer(getOptionBytes());
      for (final String s : l)
        lines.add("    " + s);
    }
  }

  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    final byte temp2[] = new byte[2];
    int offset = 0;

    type = buffer[offset++];
    if(type < 0) type &= 0xFF;
    code = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    checksum = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    final int dif = Math.abs(offset - buffer.length);
    if (dif > 0) {
      optionBytes = new byte[dif];
      System.arraycopy(buffer, offset, optionBytes, 0, optionBytes.length);
      offset += optionBytes.length;
    } else
      optionBytes = new byte[0];
    headerLength = offset;
  }
  
  /**
   * @return the type
   */
  public int getType() {
    return type;
  }
  
  /**
   * @param type
   *          the type to set
   */
  public void setType(final int type) {
    this.type = type;
  }
  
  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }
  
  /**
   * @param code
   *          the code to set
   */
  public void setCode(final int code) {
    this.code = code;
  }
  
  /**
   * @return the checksum
   */
  public int getChecksum() {
    return checksum;
  }
  
  /**
   * @param checksum
   *          the checksum to set
   */
  public void setChecksum(final int checksum) {
    this.checksum = checksum;
  }
  
  /**
   * @return the optionBytes
   */
  public byte[] getOptionBytes() {
    return optionBytes;
  }
  
  /**
   * @param optionBytes
   *          the optionBytes to set
   */
  public void setOptionBytes(final byte[] optionBytes) {
    this.optionBytes = optionBytes;
  }

}
