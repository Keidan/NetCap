package org.kei.android.phone.jni.net.layer.application;

import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;
import org.kei.android.phone.jni.net.layer.application.utils.DHCPv6Option;

/**
 *******************************************************************************
 * @file DHCPv6.java
 * @author Keidan
 * @date 07/09/2015
 * @par Project
 * NetCap
 *
 * @par 
 * Copyright 2015 Keidan, all right reserved
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY.
 *
 * License summary : 
 *    You can modify and redistribute the sources code and binaries.
 *    You can send me the bug-fix
 *
 * Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class DHCPv6 extends Layer {
  public static final int    PORT_SRV                                          = 547;
  public static final int    PORT_CLI                                          = 546;
  public static final int    SOLICIT                                           = 1;
  public static final int    ADVERTISE                                         = 2;
  public static final int    REQUEST                                           = 3;
  public static final int    REPLY                                             = 7;
  public static final int    RELEASE                                           = 8;
  public static final int    OPTION_CLIENT_ID                                  = 1;
  public static final int    OPTION_SERVER_ID                                  = 2;
  public static final int    OPTION_REQUEST                                    = 6;
  public static final int    OPTION_ELAPSED_TIME                               = 8;
  public static final int    OPTION_STATUS_CODE                                = 13;
  public static final int    OPTION_IDENTITY_ASSOCIATION_FOR_PREFIX_DELEGATION = 25;
  public static final int    LINK_LAYER_ADDRESS_PLUS_TIME                      = 1;
  private int                headerLength;
  private int                opcode;
  private int                id;
  private String             cid;
  private List<DHCPv6Option> options                                           = new ArrayList<DHCPv6Option>();

  public DHCPv6() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Dynamic Host Configuration Protocol v6";
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    byte temp2 [] = new byte[2];
    byte temp4 [] = new byte[4];
    byte temp6 [] = new byte[6];
    String s = null;
    switch(getOpcode()) {
      case SOLICIT:
        s = "Solicit";
        break;
      case ADVERTISE:
        s = "Advertise";
        break;
      case REQUEST:
        s = "Request";
        break;
      case REPLY:
        s = "Reply";
        break;
      case RELEASE:
        s = "Release";
        break;
    }
    lines.add("  Message type: " + s + " (" + getOpcode() + ")");
    lines.add("  Transaction id: 0x" + String.format("%06x", getID()));
    for(DHCPv6Option opt : options) {
      if(opt.getCode() == OPTION_CLIENT_ID) {
        lines.add(" -Client Identifier");
        lines.add("    Option: Client Identifier (" + opt.getCode() + ")");
      } else if(opt.getCode() == OPTION_REQUEST) {
        lines.add(" -Option Request");
        lines.add("    Option: Option Request (" + opt.getCode() + ")");
      } else if(opt.getCode() == OPTION_ELAPSED_TIME) {
        lines.add(" -Elapsed time");
        lines.add("    Option: Elapsed time (" + opt.getCode() + ")");
      } else if(opt.getCode() == OPTION_IDENTITY_ASSOCIATION_FOR_PREFIX_DELEGATION) {
        lines.add(" -Identity Association for Prefix Delegation");
        lines.add("    Option: Identity Association for Prefix Delegation (" + opt.getCode() + ")");
      } else if(opt.getCode() == OPTION_SERVER_ID) {
        lines.add(" -Server Identifier");
        lines.add("    Option: Server Identifier (" + opt.getCode() + ")");
      } else if(opt.getCode() == OPTION_STATUS_CODE) {
        lines.add(" -Status code");
        lines.add("    Option: Status code (" + opt.getCode() + ")");
      } else {
        lines.add(" -Unknown");
        lines.add("    Option: Unknown (" + opt.getCode() + ")");
      }
      int offset = 0;
      lines.add("    Length: " + opt.getLength());
      lines.add("    Value: " + opt.getSDatas());
      if(opt.getCode() == OPTION_CLIENT_ID || opt.getCode() == OPTION_SERVER_ID) {
        lines.add("    DUID: " + opt.getSDatas());
        NetworkHelper.zcopy(opt.getDatas(), offset, temp2, 0, temp2.length);
        int type = NetworkHelper.ntohs2(temp2);
        offset+=temp2.length;
        if(type == LINK_LAYER_ADDRESS_PLUS_TIME) {
          lines.add("    DUID Type: link-layer address plus time (1)");
          NetworkHelper.zcopy(opt.getDatas(), offset, temp2, 0, temp2.length);
          int htype = NetworkHelper.ntohs2(temp2);
          offset+=temp2.length;
          lines.add("    Hardware type: " + htype);
          NetworkHelper.zcopy(opt.getDatas(), offset, temp4, 0, temp4.length);
          int time = NetworkHelper.ntohs2(temp4);
          offset+=temp4.length;
          lines.add("    DUID Time: " + time);
          System.arraycopy(opt.getDatas(), offset, temp6, 0, temp6.length);
          String addr = String.format("%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
          offset+=temp6.length;
          lines.add("    Address: " + addr);
        } else
          lines.add("    DUID Type: " + type);
      } else if(opt.getCode() == OPTION_REQUEST) {
        NetworkHelper.zcopy(opt.getDatas(), offset, temp2, 0, temp2.length);
        int type = NetworkHelper.ntohs2(temp2);
        offset+=temp2.length;
        lines.add("    Requested Option code: " + type);
        NetworkHelper.zcopy(opt.getDatas(), offset, temp2, 0, temp2.length);
        type = NetworkHelper.ntohs2(temp2);
        offset+=temp2.length;
        lines.add("    Requested Option code: " + type);
      } else if(opt.getCode() == OPTION_ELAPSED_TIME) {
        NetworkHelper.zcopy(opt.getDatas(), offset, temp2, 0, temp2.length);
        int time = NetworkHelper.ntohs2(temp2);
        lines.add("    Elapsed time: " + time + " ms");
      } else if(opt.getCode() == OPTION_STATUS_CODE) {
        NetworkHelper.zcopy(opt.getDatas(), offset, temp2, 0, temp2.length);
        offset+=temp2.length;
        int code = NetworkHelper.ntohs2(temp2);
        lines.add("    Status code: " + code);
        byte bytes [] = new byte[opt.getLength() - 2];
        System.arraycopy(opt.getDatas(), offset, bytes, 0, bytes.length);
        offset+=bytes.length;
        lines.add("    Status message: " + new String(bytes));
      }
    }
  }

  @Override
  public String getProtocolText() {
    return "DHCPv6";
  }

  @Override
  public String getDescriptionText() {
    String s = null;
    switch(getOpcode()) {
      case SOLICIT:
        s = "Solicit";
        break;
      case ADVERTISE:
        s = "Advertise";
        break;
      case REQUEST:
        s = "Request";
        break;
      case REPLY:
        s = "Reply";
        break;
      case RELEASE:
        s = "Release";
        break;
    }
    return s == null ? null : s + " XID: 0x" + String.format("%06x", getID()) + " CID: " + getCID();
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    byte temp2 [] = new byte[2];
    byte temp4 [] = new byte[4];
    int offset = 0;
    
    opcode = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp4, 0, 3);
    id = NetworkHelper.getInt(temp4, 0) >> 8;
    offset+= 3;
    
    do {
      DHCPv6Option opt = new DHCPv6Option();
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      opt.setCode(NetworkHelper.ntohs2(temp2));
      offset+=temp2.length;
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      opt.setLength(NetworkHelper.ntohs2(temp2));
      offset+=temp2.length;
      byte [] value = new byte[opt.getLength()];
      System.arraycopy(buffer, offset, value, 0, value.length);
      offset += value.length;
      opt.setDatas(value);
      String sDatas = "";
      for(byte b : value) sDatas += String.format("%02x", b);
      opt.setSDatas(sDatas);
      if(opt.getCode() == OPTION_CLIENT_ID)
        cid = opt.getSDatas();
      options.add(opt);
    } while(offset < buffer.length);
    
    headerLength = offset;
  }

  /**
   * @return the opcode
   */
  public int getOpcode() {
    return opcode;
  }

  /**
   * @param opcode the opcode to set
   */
  public void setOpcode(int opcode) {
    this.opcode = opcode;
  }

  /**
   * @return the id
   */
  public int getID() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setID(int id) {
    this.id = id;
  }

  /**
   * @return the cid
   */
  public String getCID() {
    return cid;
  }

  /**
   * @param cid the cid to set
   */
  public void setCID(String cid) {
    this.cid = cid;
  }

  /**
   * @return the options
   */
  public List<DHCPv6Option> getOptions() {
    return options;
  }

  /**
   * @param options the options to set
   */
  public void setOptions(List<DHCPv6Option> options) {
    this.options = options;
  }
  
}
