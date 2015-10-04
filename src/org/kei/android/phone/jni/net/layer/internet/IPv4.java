package org.kei.android.phone.jni.net.layer.internet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;
import org.kei.android.phone.jni.net.layer.transport.TCP;
import org.kei.android.phone.jni.net.layer.transport.UDP;

/**
 *******************************************************************************
 * @file IPv4.java
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
public class IPv4 extends Layer {
  public static final int IPPROTO_UDP   = 17;
  public static final int IPPROTO_TCP   = 6;
  public static final int IPPROTO_IGMP  = 2;
  public static final int IPPROTO_ICMP  = 1;
  public static final int IP_RF         = 0x8000;
  public static final int IP_DF         = 0x4000;
  public static final int IP_MF         = 0x2000;
  private int             ihl;
  private int             version;
  private short           tos;
  private int             totLength;
  private int             ident;
  private int             id;
  private int             fragOff;
  private int             ttl;
  private int             protocol;
  private int             checksum;
  private String          source;
  private String          destination;
  private int             headerLength;
  
  private boolean         reservedBit   = false;
  private boolean         dontFragment  = false;
  private boolean         moreFragments = false;
  
  public IPv4() {
    super();
  }
  
  @Override
  public String getFullName() {
    return "Internet Protocol v" + getVersion() +" (Src: " + source + ", Dst: " + destination + ")";
  }

  @Override
  public String getProtocolText() {
    return "IPv" + getVersion();
  }

  @Override
  public String getDescriptionText() {
    return null;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Version: " + getVersion());
    lines.add("  Header length: " + (getIHL() * 4) + " bytes");
    lines.add("  Differentiated Services Field:");
    lines.add("    Total Length: " + getTotLength());
    lines.add("    Identification: 0x" + String.format("%04x", getIdent()) + " (" + getIdent() + ")");
    lines.add("  Flags: ");
    lines.add("    " + (isReservedBit() ? "1" : "0") + "... Reserved bit: " + (isReservedBit() ? "Set" : "Not Set"));
    lines.add("    ." + (isDontFragment() ? "1" : "0") + ".. Don't fragment: " + (isDontFragment() ? "Set" : "Not Set"));
    lines.add("    .." + (isMoreFragments() ? "1" : "0") + ". More fragments: " + (isMoreFragments() ? "Set" : "Not Set"));
    lines.add("  Fragment offset: " + getFragOff());
    lines.add("  Time to live: " + getTTL());
    lines.add("  Protocol: " + getProtocol());
    lines.add("  Header checksum: 0x" + String.format("%04x", getChecksum()));
    lines.add("  Source: " + getSource());
    lines.add("  Destination: " + getDestination());
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    byte temp4 [] = new byte[4];
    byte temp2 [] = new byte[2];
    int offset = 0;
    byte ihl_version = buffer[offset++];
    version = (byte)(ihl_version >> 4);
    ihl = (byte)(ihl_version & 0x0f);
    NetworkHelper.zcopy(buffer, offset, temp2, 0, 1);
    tos = NetworkHelper.ntohs(temp2);
    offset++;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    totLength = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    ident = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    fragOff = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    int foff = fragOff;
    id = ((foff >> 8) & 0xff);
    fragOff = (foff&IP_DF) != 0 ? 0 : foff;
    reservedBit = (foff&IP_RF) != 0;
    dontFragment = (foff&IP_DF) != 0;
    moreFragments = (foff&IP_MF) != 0;
    
    NetworkHelper.zcopy(buffer, offset, temp2, 0, 1);
    ttl = NetworkHelper.getValue(temp2);
    ttl = (short)(ttl & 0xff); 
    offset++;
    protocol = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    checksum = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset+= temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      source = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      source = e.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset+=temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      destination = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      destination = e.getMessage();
    }
    headerLength = offset;
    if(protocol == IPPROTO_UDP) {
      byte [] sub_buffer = resizeBuffer(buffer);
      UDP udp = new UDP();
      udp.decodeLayer(sub_buffer, this);
      setNext(udp);
    } else if(protocol == IPPROTO_TCP) {
      byte [] sub_buffer = resizeBuffer(buffer);
      TCP tcp = new TCP();
      tcp.decodeLayer(sub_buffer, this);
      setNext(tcp);
    } else if(protocol == IPPROTO_IGMP) {
      IGMP igmp = new IGMP();
      offset += 4; // options
      headerLength = offset;
      byte [] sub_buffer = resizeBuffer(buffer);
      igmp.decodeLayer(sub_buffer, this);
      setNext(igmp);
    } else if(protocol == IPPROTO_ICMP) {
      byte [] sub_buffer = resizeBuffer(buffer);
      ICMPv4 icmp = new ICMPv4();
      icmp.decodeLayer(sub_buffer, this);
      setNext(icmp);
    } else {
      byte [] sub_buffer = resizeBuffer(buffer);
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  /**
   * Get the source IPv4 addr.
   *
   * @return String
   */
  public String getSource() {
    return source;
  }

  /**
   * Set the source IPv4 addr.
   *
   * @param source
   *          The address
   */
  public void setSource(final String source) {
    this.source = source;
  }

  /**
   * Get the destination IPv4 addr.
   *
   * @return String
   */
  public String getDestination() {
    return destination;
  }

  /**
   * Set the destination IPv4 addr.
   *
   * @param destination
   *          The address
   */
  public void setDestination(final String destination) {
    this.destination = destination;
  }
  
  /**
   * Get the TOS value.
   * 
   * @return int
   */
  public short getTOS() {
    return tos;
  }
  
  /**
   * Set the TOS value.
   * 
   * @param tos
   *          The value.
   */
  public void setTOS(final short tos) {
    this.tos = tos;
  }
  
  /**
   * Get the Total Length value.
   * 
   * @return int
   */
  public int getTotLength() {
    return totLength;
  }
  
  /**
   * Set the Total Length value.
   * 
   * @param totLength
   *          The value.
   */
  public void setTotLength(final int totLength) {
    this.totLength = totLength;
  }
  
  /**
   * Get the Flags value.
   * 
   * @return int
   */
  public int getFlags() {
    return id;
  }
  
  /**
   * Set the id value.
   * 
   * @param id
   *          The value.
   */
  public void setFlags(final int id) {
    this.id = id;
  }
  
  /**
   * @return the ident
   */
  public int getIdent() {
    return ident;
  }

  /**
   * @param ident the ident to set
   */
  public void setIdent(int ident) {
    this.ident = ident;
  }

  /**
   * Get the FragOffset value.
   * 
   * @return int
   */
  public int getFragOff() {
    return fragOff;
  }
  
  /**
   * Set the FragOffset value.
   * 
   * @param fragOff
   *          The value.
   */
  public void setFragOff(final int fragOff) {
    this.fragOff = fragOff;
  }
  
  /**
   * Get the TTL value.
   * 
   * @return int
   */
  public int getTTL() {
    return ttl;
  }
  
  /**
   * Set the ttl value.
   * 
   * @param ttl
   *          The value.
   */
  public void setTTL(final int ttl) {
    this.ttl = ttl;
  }
  
  /**
   * Get the Protocol value.
   * 
   * @return int
   */
  public int getProtocol() {
    return protocol;
  }
  
  /**
   * Set the protocol value.
   * 
   * @param protocol
   *          The value.
   */
  public void setProtocol(final int protocol) {
    this.protocol = protocol;
  }
  
  /**
   * Get the checksum value.
   * 
   * @return int
   */
  public int getChecksum() {
    return checksum;
  }
  
  /**
   * Set the checksum value.
   * 
   * @param checksum
   *          The value.
   */
  public void setChecksum(final int checksum) {
    this.checksum = checksum;
  }

  public boolean isReservedBit() {
    return reservedBit;
  }

  public void setReservedBit(boolean reservedBit) {
    this.reservedBit = reservedBit;
  }

  public boolean isDontFragment() {
    return dontFragment;
  }

  public void setDontFragment(boolean dontFragment) {
    this.dontFragment = dontFragment;
  }

  public boolean isMoreFragments() {
    return moreFragments;
  }

  public void setMoreFragments(boolean moreFragments) {
    this.moreFragments = moreFragments;
  }

  public int getIHL() {
    return ihl;
  }

  public void setIHL(int ihl) {
    this.ihl = ihl;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

}
