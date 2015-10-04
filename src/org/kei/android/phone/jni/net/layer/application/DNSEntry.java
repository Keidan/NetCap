package org.kei.android.phone.jni.net.layer.application;

/**
 *******************************************************************************
 * @file DNSEntry.java
 * @author Keidan
 * @date 01/10/2015
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
public class DNSEntry {
  private String name       = null;
  private int    type       = 0;
  private int    clazz      = 0;
  private int    ttl        = 0;
  private int    dataLength = 0;
  private String address    = null;
  
  public DNSEntry() {
    
  }
  
  public String getTypeString() {
    switch(type) {
      case 1: return "A";
      case 2: return "NS";
      case 5: return "CNAME";
      case 6: return "SOA";
      case 12: return "PTR";
      case 15: return "MX";
    }
    return "UNKNOWN";
  }
  
  public String getClazzString() {
    if(clazz == 0x0000) return "AR";
    else if(clazz == 0x0001) return "IN";
    else if(clazz == 0x0002) return "AV";
    else if(clazz == 0x0003) return "CH";
    else if(clazz == 0x0004) return "HS";
    else if(clazz >= 0x0005 && clazz <= 0x007F) return "Data CLASSes only";
    else if(clazz >= 0x0080 && clazz <= 0x00FD) return "QCLASSes only";
    else if(clazz == 0x00FE) return "QCLASS None";
    else if(clazz == 0x00FF) return "QCLASS Any";
    else if(clazz >= 0x0100 && clazz <= 0x7FFF) return "Assigned by IETF";
    else if(clazz >= 0x8000 && clazz <= 0xFEFF) return "Assigned based on Specification";
    else if(clazz >= 0xFF00 && clazz <= 0xFFFE) return "Private Use";
    else if(clazz == 0xFFFF) return "Can only be assigned by an IETF";
    return "UNKNOWN";
  }

  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(final String name) {
    this.name = name;
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
   * @return the clazz
   */
  public int getClazz() {
    return clazz;
  }

  /**
   * @param clazz
   *          the clazz to set
   */
  public void setClazz(final int clazz) {
    this.clazz = clazz;
  }

  /**
   * @return the ttl
   */
  public int getTTL() {
    return ttl;
  }

  /**
   * @param ttl
   *          the ttl to set
   */
  public void setTTL(final int ttl) {
    this.ttl = ttl;
  }

  /**
   * @return the dataLength
   */
  public int getDataLength() {
    return dataLength;
  }

  /**
   * @param dataLength
   *          the dataLength to set
   */
  public void setDataLength(final int dataLength) {
    this.dataLength = dataLength;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @param address
   *          the address to set
   */
  public void setAddress(final String address) {
    this.address = address;
  }

}
