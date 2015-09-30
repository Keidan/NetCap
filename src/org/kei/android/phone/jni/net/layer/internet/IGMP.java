package org.kei.android.phone.jni.net.layer.internet;

import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.jni.net.layer.Layer;

import android.graphics.Color;

/**
 *******************************************************************************
 * @file IGMP.java
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
  private int             s               = 0;
  private int             qrv             = 0;
  private int             qqic            = 0;
  private int             numberOfSources = 0;
  private List<String>    sourceAdress    = null;

  public IGMP() {
    super(TYPE_IGMP);
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
      case IGMP.REPORT_V1: return "IGMPv1";
      case IGMP.REPORT_V2: return "IGMPv2";
      case IGMP.QUERY: return "IGMPv2";
      case IGMP.REPORT_V3: return "IGMPv3";
      default: return "IGMP";
    }
  }

  @Override
  public String getDescriptionText() {
    switch(getType()) {
      case IGMP.QUERY: return "Membership Query, general";
      case IGMP.REPORT_V2: return "Membership Report group " + getGroupAdress();
      case IGMP.REPORT_V3: return "Membership Report groups " + getNumberOfSources();
      default: return "Membership Unknown";
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

  public int getS() {
    return s;
  }

  public void setS(final int s) {
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
