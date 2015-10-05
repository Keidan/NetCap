package org.kei.android.phone.net.layer.internet;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.net.NetworkHelper;
import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.Payload;

import android.graphics.Color;

/**
 *******************************************************************************
 * @file java
 * @author Keidan
 * @date 29/09/2015
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
public class IGMP extends Layer {
  public static final int QUERY           = 0x11;
  public static final int REPORT_V1       = 0x12;
  public static final int REPORT_V2       = 0x16;
  public static final int REPORT_V3       = 0x22;
  public static final int LEAVE_GROUP     = 0x17;

  private int             type            = 0;
  private int             maxRespTime     = 0;
  private int             checksum        = 0;
  private String          groupAdress     = "0.0.0.0";
  private int             resv            = 0;
  private boolean         s               = false;
  private int             qrv             = 0;
  private int             qqic            = 0;
  private int             numberOfSources = 0;
  private List<String>    sourceAdress    = null;
  private int             headerLength    = 0;

  public IGMP() {
    super();
    sourceAdress = new ArrayList<String>();
    background = Color.parseColor("#FFF3D6");
    foreground = Color.BLACK;
  }
  
  @Override
  public String getFullName() {
    return "Internet Group Management Protocol";
  }

  @Override
  public String getProtocolText() {
    switch(getType()) {
      case REPORT_V1: return "IGMPv1";
      case REPORT_V2: return "IGMPv2";
      case QUERY: return "IGMPv2";
      case REPORT_V3: return "IGMPv3";
      default: return "IGMP";
    }
  }

  @Override
  public String getDescriptionText() {
    switch(getType()) {
      case QUERY: return "Membership Query, general";
      case REPORT_V2: return "Membership Report group " + getGroupAdress();
      case REPORT_V3: return "Membership Report groups " + getNumberOfSources();
      default: return "Membership Unknown";
    }
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    if(getType() == QUERY)
      lines.add("  Type: Membership Query (0x" + String.format("%02x", getType()) + ")");
    else
      lines.add("  Type: Membership Report (0x" + String.format("%02x", getType()) + ")");
    lines.add("  Max Response Time: " + ((float)(getMaxRespTime() / 10)) + " sec (0x" + String.format("%02x", getMaxRespTime()) + ")");
    lines.add("  Header checksum: 0x" + String.format("%04x", getChecksum()));
    lines.add("  Multicast address: " + getGroupAdress());
    if(getNumberOfSources() != 0) {
      lines.add("  Num Src: " + getNumberOfSources());
      for(String s : getSourceAdress()) lines.add("  Source Address: " + s);
    }
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    int tos = 0;
    if(IPv4.class.isInstance(owner))
      tos = ((IPv4)owner).getTOS();
    else if(IPv6.class.isInstance(owner))
      tos = ((IPv6)owner).getFlowLbl_1();
    int offset = 0;
    byte temp2 [] = new byte[2];
    byte temp4 [] = new byte[4];
    
    type = buffer[offset++];
    if(tos == 0xc0 && type != QUERY) {
      maxRespTime = buffer[offset++];
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      checksum = NetworkHelper.ntohs2(temp2);
      offset+=temp2.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset+=temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        groupAdress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        groupAdress = e.getMessage();
      }
      

      resv = (byte)(buffer[offset++] & 0x0f);
      int n = buffer[offset++];
      s = BigInteger.valueOf(n).testBit(4);
      n = BigInteger.valueOf(n).clearBit(4).intValue();
      qrv = n;
      qqic = buffer[offset++];
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      numberOfSources = NetworkHelper.ntohs2(temp2);
      offset+=temp2.length;
      for(int i = 0; i < numberOfSources; i++) {
        System.arraycopy(buffer, offset, temp4, 0, temp4.length);
        offset+=temp4.length;
        try {
          InetAddress address = InetAddress.getByAddress(temp4);
          addSourceAdress(address.getHostAddress());
        } catch (UnknownHostException e) {
          e.printStackTrace();
          addSourceAdress(e.getMessage());
        }
      }
    } else if(type == REPORT_V3) {
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      checksum = NetworkHelper.ntohs2(temp2);
      offset+=temp2.length;
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      numberOfSources = NetworkHelper.ntohs2(temp2);
      offset+=temp2.length;
      for(int i = 0; i < numberOfSources; i++) {
        System.arraycopy(buffer, offset, temp4, 0, temp4.length);
        offset+=temp4.length;
        try {
          InetAddress address = InetAddress.getByAddress(temp4);
          addSourceAdress(address.getHostAddress());
        } catch (UnknownHostException e) {
          e.printStackTrace();
          addSourceAdress(e.getMessage());
        }
      }
    } else {
      maxRespTime = buffer[offset++];
      NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
      checksum = NetworkHelper.ntohs2(temp2);
      offset+=temp2.length;
      System.arraycopy(buffer, offset, temp4, 0, temp4.length);
      offset+=temp4.length;
      try {
        InetAddress address = InetAddress.getByAddress(temp4);
        groupAdress = address.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        groupAdress = e.getMessage();
      }
    }
    headerLength = offset;
    byte [] sub_buffer = resizeBuffer(buffer);
    if(sub_buffer != null) {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  public int getType() {
    return type;
  }

  public void setType(final int type) {
    this.type = type;
  }

  public int getMaxRespTime() {
    return maxRespTime;
  }

  public void setMaxRespTime(final int maxRespTime) {
    this.maxRespTime = maxRespTime;
  }

  public int getChecksum() {
    return checksum;
  }

  public void setChecksum(final int checksum) {
    this.checksum = checksum;
  }

  public String getGroupAdress() {
    return groupAdress;
  }

  public void setGroupAdress(final String groupAdress) {
    this.groupAdress = groupAdress;
  }

  public int getResv() {
    return resv;
  }

  public void setResv(final int resv) {
    this.resv = resv;
  }

  public boolean isS() {
    return s;
  }

  public void setS(final boolean s) {
    this.s = s;
  }

  public int getQRV() {
    return qrv;
  }

  public void setQRV(final int qrv) {
    this.qrv = qrv;
  }

  public int getQQIC() {
    return qqic;
  }

  public void setQQIC(final int qqic) {
    this.qqic = qqic;
  }

  public int getNumberOfSources() {
    return numberOfSources;
  }

  public void setNumberOfSources(final int numberOfSources) {
    this.numberOfSources = numberOfSources;
  }

  public List<String> getSourceAdress() {
    return sourceAdress;
  }

  public void addSourceAdress(final String sourceAdress) {
    this.sourceAdress.add(sourceAdress);
  }

}
