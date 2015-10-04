package org.kei.android.phone.jni.net.layer.application;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.jni.net.NetworkHelper;
import org.kei.android.phone.jni.net.layer.Layer;
import org.kei.android.phone.jni.net.layer.Payload;
import org.kei.android.phone.jni.net.layer.application.utils.DNSEntry;


/**
 *******************************************************************************
 * @file java
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
public class DNS extends Layer {
  public static final int PORT             = 53;
  public static final int QTYPE_ADDRESS    = 1; /* Ipv4 address */
  public static final int QTYPE_NAMESERVER = 2; /* Nameserver */
  public static final int QTYPE_CNAME      = 5; /* canonical name */
  public static final int QTYPE_SOA        = 6; /* start of authority zone */
  public static final int QTYPE_PTR        = 12; /* domain name pointer */
  public static final int QTYPE_MX         = 15; /* Mail server */

  private int             id;                   // identification number
  private boolean         rd;
  private boolean         tc;
  private boolean         aa;
  private int             opcode;
  private boolean         qr;
  private int             rcode;
  private int             zero;
  private boolean         ra;
  private int             qCount;
  private int             ansCount;
  private int             authCount;
  private int             addCount;
  private List<DNSEntry>  queries;
  private List<DNSEntry>  answers;
  private List<DNSEntry>  authorities;
  private List<DNSEntry>  additionals;
  private int             headerLength;
  private int             flags;
  private int             offset;

  public DNS() {
    super();
    queries = new ArrayList<DNSEntry>();
    answers = new ArrayList<DNSEntry>();
    authorities = new ArrayList<DNSEntry>();
    additionals = new ArrayList<DNSEntry>();
  }
  
  @Override
  public String getFullName() {
    return "Domain Name System (" + ((qr && !rd) ? "query" : "response") + ")";
  }

  @Override
  public String getProtocolText() {
    return "DNS";
  }

  @Override
  public String getDescriptionText() {
    StringBuilder sb = new StringBuilder();
    if(!isQR()) {
      for(DNSEntry e : queries)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getName());
      return "Standard query 0x" + String.format("%04x", getID()) + sb.toString();
    } else {
      for(DNSEntry e : answers)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getAddress());
      for(DNSEntry e : authorities)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getAddress());
      for(DNSEntry e : additionals)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getAddress());
      return "Standard query response 0x" + String.format("%04x", getID()) + sb.toString();
    }
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Transaction ID: 0x" + String.format("%04x", getID()));
    lines.add("  Flags: 0x" + String.format("%04x", getFlags()));
    
    lines.add("    " + (isQR() ? "1" : "0") + "... .... .... .... = Response: Message is a " + (isQR() ? "response" : "query"));
    lines.add("    ." + (BigInteger.valueOf(flags).testBit(4) ? "1" : "0") + 
        (BigInteger.valueOf(flags).testBit(3) ? "1" : "0") + (BigInteger.valueOf(flags).testBit(11) ? "1" : "0") + 
        " " + (BigInteger.valueOf(flags).testBit(11) ? "1" : "0") + "... .... .... = Opcode (" + opcode + ")");
    lines.add("    .... ." + (isAA() ? "1" : "0") + ".. .... .... = Authoritative");
    lines.add("    .... .." + (isTC() ? "1" : "0") + ". .... .... = Truncated");
    lines.add("    .... ..." + (isRD() ? "1" : "0") + " .... .... = Recursion desired");
    lines.add("    .... .... " + (isRD() ? "1" : "0") + "... .... = Recursion available");
    lines.add("    .... .... ." + (BigInteger.valueOf(zero).testBit(3) ? "1" : "0") + 
        (BigInteger.valueOf(zero).testBit(2) ? "1" : "0") + (BigInteger.valueOf(zero).testBit(1) ? "1" : "0") + " .... = Zero (" + zero + ")");
    lines.add("    .... .... .... " + (BigInteger.valueOf(rcode).testBit(4) ? "1" : "0") + (BigInteger.valueOf(rcode).testBit(3) ? "1" : "0") + 
        (BigInteger.valueOf(rcode).testBit(2) ? "1" : "0") + (BigInteger.valueOf(rcode).testBit(1) ? "1" : "0") + " = Reply code (" + rcode + ")");
    lines.add("  Questions: " + getQCount());
    lines.add("  Answer RRs: " + getAnsCount());
    lines.add("  Authority RRs: " + getAuthCount());
    lines.add("  Additional RRs: " + getAddCount());
    if(!getQueries().isEmpty()) {
      lines.add("  Queries");
      for(DNSEntry e : getQueries()) {
        lines.add("   -Name: " + e.getName());
        lines.add("    Type: " + e.getTypeString());
        lines.add("    Class: " + e.getClazzString());
      }
    }
    addDNSEntry("Answer", getAnswers(), lines);
    addDNSEntry("Authority", getAuthorities(), lines);
    addDNSEntry("Additional", getAdditionals(), lines);
  }
  
  private void addDNSEntry(final String label, final List<DNSEntry> entries, final List<String> lines) {
    if(!entries.isEmpty()) {
      lines.add("  " + label);
      for(DNSEntry e : entries) {
        lines.add("   -Name: " + e.getName());
        lines.add("    Type: " + e.getTypeString());
        lines.add("    Class: " + e.getClazzString());
        lines.add("    Time to live: " + ((int)e.getTTL() / 60) + " minutes, " + ((int)e.getTTL() % 60) + " seconds)");
        lines.add("    Data length: " + e.getDataLength());
        lines.add("    Addr: " + e.getAddress());
      }
    }
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    byte temp2 [] = new byte[2];
    offset = 0;
    

    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    id = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    flags = NetworkHelper.ntohs2(temp2);
    
    offset+=temp2.length;
    qr = BigInteger.valueOf(flags).testBit(15);
    opcode = NetworkHelper.setBit((byte)0, 3, BigInteger.valueOf(flags).testBit(14));
    opcode = NetworkHelper.setBit((byte)opcode, 2, BigInteger.valueOf(flags).testBit(13));
    opcode = NetworkHelper.setBit((byte)opcode, 1, BigInteger.valueOf(flags).testBit(12));
    opcode = NetworkHelper.setBit((byte)opcode, 0, BigInteger.valueOf(flags).testBit(11));
    aa = BigInteger.valueOf(flags).testBit(10);
    tc = BigInteger.valueOf(flags).testBit(9);
    rd = BigInteger.valueOf(flags).testBit(8);
    ra = BigInteger.valueOf(flags).testBit(7);
    

    zero = NetworkHelper.setBit((byte)0, 0, BigInteger.valueOf(flags).testBit(6));
    zero = NetworkHelper.setBit((byte)zero, 1, BigInteger.valueOf(flags).testBit(5));
    zero = NetworkHelper.setBit((byte)zero, 2, BigInteger.valueOf(flags).testBit(4));
    
    rcode = NetworkHelper.setBit((byte)0, 3, BigInteger.valueOf(flags).testBit(3));
    rcode = NetworkHelper.setBit((byte)rcode, 2, BigInteger.valueOf(flags).testBit(2));
    rcode = NetworkHelper.setBit((byte)rcode, 1, BigInteger.valueOf(flags).testBit(1));
    rcode = NetworkHelper.setBit((byte)rcode, 0, BigInteger.valueOf(flags).testBit(0));
    

    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    qCount = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    ansCount = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    authCount = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    addCount = NetworkHelper.ntohs2(temp2);
    offset+=temp2.length;
    
    if(qCount != 0) {
      for(int i = 0; i < qCount; i++) {
        DNSEntry e = new DNSEntry();
        // get the name
        List<String> n = new ArrayList<String>();
        int l = normalizeFromDNS(buffer, offset, n);
        offset+=l;
        e.setName(formatFromDNS(n));
        NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
        e.setType(NetworkHelper.ntohs2(temp2));
        offset+=temp2.length;
        NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
        e.setClazz(NetworkHelper.ntohs2(temp2));
        offset+=temp2.length;
        addQuery(e);
      }
    }
    decodeResponse(buffer, ansCount, answers);
    decodeResponse(buffer, authCount, authorities);
    decodeResponse(buffer, addCount, additionals);
    headerLength = offset;
    byte [] sub_buffer = resizeBuffer(buffer);
    if(sub_buffer != null) {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }
  
  private void decodeResponse(byte buffer[], int count, List<DNSEntry> list) {
    byte temp2 [] = new byte[2];
    byte temp4 [] = new byte[4];
    if(count != 0) {
      for(int i = 0; i < count; i++) {
        DNSEntry e = new DNSEntry();
        // get the name
        List<String> n = new ArrayList<String>();
        int l = normalizeFromDNS(buffer, offset, n);
        offset+=l;
        e.setName(formatFromDNS(n));
        NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
        e.setType(NetworkHelper.ntohs2(temp2));
        offset+=temp2.length;
        NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
        e.setClazz(NetworkHelper.ntohs2(temp2));
        offset+=temp2.length;
        NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
        //e.setTTL(NetworkHelper.ntohl(temp4));
        e.setTTL(NetworkHelper.getInt(temp4, 0));
        
        offset+=temp4.length;
        NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
        e.setDataLength(NetworkHelper.ntohs2(temp2));
        offset+=temp2.length;
        if(e.getType() == QTYPE_CNAME) {
          n = new ArrayList<String>();
          l = normalizeFromDNS(buffer, offset, n);
          offset+=l;
          e.setAddress(formatFromDNS(n));
        } else {
          NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
          offset+= temp4.length;
          try {
            InetAddress address = InetAddress.getByAddress(temp4);
            e.setAddress(address.getHostAddress());
          } catch (UnknownHostException ex) {
            ex.printStackTrace();
            e.setAddress(ex.getMessage());
          }
        }
        list.add(e);
      }
    }
  }
  

  public static String formatFromDNS(List<String> n) {
    String name = "";
    for(int j = 0; j < n.size(); j++) {
      name += n.get(j);
      if(j != n.size() - 1)
        name += ".";
    }
    return name;
  }
  public static int normalizeFromDNS(byte[] msg, int offset, List<String> n) {
    int endPos = -1;
    int pos = offset;
    while (pos < msg.length) {
      int type = msg[pos] & 0xFF;
      if (type == 0) { // end of name
        ++pos;
        break;
      } else if (type <= 63) { // regular label
        ++pos;
        n.add(new String(msg, pos, type, StandardCharsets.ISO_8859_1));
        pos += type;
      } else if ((msg[pos] & 0xC0) == 0xC0) { // name compression
        endPos = pos + 2;
        //pos = getUShort(pos) & 0x3FFF;
        if(msg.length < pos+1 && (msg[pos+1] & 0x0C) == 0x0C) break;
        n.add("c00c");
        break;
      }
    }
    if (endPos == -1) endPos = pos;
    endPos = endPos - offset;
    return endPos;
  }
  
  /**
   * @return the flags
   */
  public int getFlags() {
    return flags;
  }

  /**
   * @param flags the flags to set
   */
  public void setFlags(int flags) {
    this.flags = flags;
  }

  /**
   * @return the queries
   */
  public List<DNSEntry> getQueries() {
    return queries;
  }

  /**
   * @param query the query to add
   */
  public void addQuery(DNSEntry query) {
    this.queries.add(query);
  }

  /**
   * @return the answers
   */
  public List<DNSEntry> getAnswers() {
    return answers;
  }

  /**
   * @param answer the answer to add
   */
  public void addAnswer(DNSEntry answer) {
    this.answers.add(answer);
  }

  /**
   * @return the authorities
   */
  public List<DNSEntry> getAuthorities() {
    return authorities;
  }

  /**
   * @param authority the authority to add
   */
  public void addAuthority(DNSEntry authority) {
    this.authorities.add(authority);
  }

  /**
   * @return the additionals
   */
  public List<DNSEntry> getAdditionals() {
    return additionals;
  }

  /**
   * @param additional the additional to set
   */
  public void addAdditional(DNSEntry additional) {
    this.additionals.add(additional);
  }

  /**
   * @return the id
   */
  public int getID() {
    return id;
  }
  
  /**
   * @param id
   *          the id to set
   */
  public void setID(final int id) {
    this.id = id;
  }
  
  /**
   * @return the rd
   */
  public boolean isRD() {
    return rd;
  }
  
  /**
   * @param rd
   *          the rd to set
   */
  public void setRD(final boolean rd) {
    this.rd = rd;
  }
  
  /**
   * @return the tc
   */
  public boolean isTC() {
    return tc;
  }
  
  /**
   * @param tc
   *          the tc to set
   */
  public void setTC(final boolean tc) {
    this.tc = tc;
  }
  
  /**
   * @return the aa
   */
  public boolean isAA() {
    return aa;
  }
  
  /**
   * @param aa
   *          the aa to set
   */
  public void setAA(final boolean aa) {
    this.aa = aa;
  }
  
  /**
   * @return the opcode
   */
  public int getOpcode() {
    return opcode;
  }
  
  /**
   * @param opcode
   *          the opcode to set
   */
  public void setOpcode(final int opcode) {
    this.opcode = opcode;
  }
  
  /**
   * @return the qr
   */
  public boolean isQR() {
    return qr;
  }
  
  /**
   * @param qr
   *          the qr to set
   */
  public void setQR(final boolean qr) {
    this.qr = qr;
  }
  
  /**
   * @return the rcode
   */
  public int getRCode() {
    return rcode;
  }
  
  /**
   * @param rcode
   *          the rcode to set
   */
  public void setRCode(final int rcode) {
    this.rcode = rcode;
  }
  
  /**
   * @return the zero
   */
  public int getZero() {
    return zero;
  }
  
  /**
   * @param zero
   *          the zero to set
   */
  public void setZero(final int zero) {
    this.zero = zero;
  }
  
  /**
   * @return the ra
   */
  public boolean isRA() {
    return ra;
  }
  
  /**
   * @param ra
   *          the ra to set
   */
  public void setRA(final boolean ra) {
    this.ra = ra;
  }
  
  /**
   * @return the qCount
   */
  public int getQCount() {
    return qCount;
  }
  
  /**
   * @param qCount
   *          the qCount to set
   */
  public void setQCount(final int qCount) {
    this.qCount = qCount;
  }
  
  /**
   * @return the ansCount
   */
  public int getAnsCount() {
    return ansCount;
  }
  
  /**
   * @param ansCount
   *          the ansCount to set
   */
  public void setAnsCount(final int ansCount) {
    this.ansCount = ansCount;
  }
  
  /**
   * @return the authCount
   */
  public int getAuthCount() {
    return authCount;
  }
  
  /**
   * @param authCount
   *          the authCount to set
   */
  public void setAuthCount(final int authCount) {
    this.authCount = authCount;
  }
  
  /**
   * @return the addCount
   */
  public int getAddCount() {
    return addCount;
  }
  
  /**
   * @param addCount
   *          the addCount to set
   */
  public void setAddCount(final int addCount) {
    this.addCount = addCount;
  }
  
}
