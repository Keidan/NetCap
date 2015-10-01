package org.kei.android.phone.jni.net.layer.application;

import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.jni.net.layer.Layer;

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
  private boolean         zero;
  private boolean         ra;
  private int             qCount;
  private int             ansCount;
  private int             authCount;
  private int             addCount;
  private List<DNSEntry>  queries;
  private List<DNSEntry>  answers;
  private List<DNSEntry>  authorities;
  private List<DNSEntry>  additionals;

  public DNS() {
    super(TYPE_DNS);
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
    if(!queries.isEmpty() && answers.isEmpty() && authorities.isEmpty() && additionals.isEmpty()) {
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
    lines.add("  Flags:");
    lines.add("    RD: " + (isRD() ? "Set" : "Not Set"));
    lines.add("    TC: " + (isTC() ? "Set" : "Not Set"));
    lines.add("    AA: " + (isAA() ? "Set" : "Not Set"));
    lines.add("    OPCODE: 0x" + String.format("%04x", getOpcode()));
    lines.add("    QR: " + (isQR() ? "Set" : "Not Set"));
    lines.add("    RCODE: 0x" + String.format("%04x", getRCode()));
    lines.add("    ZERO: " + (isZero() ? "Set" : "Not Set"));
    lines.add("    RA: " + (isRA() ? "Set" : "Not Set"));
    lines.add("  Questions: " + getQCount());
    lines.add("  Answer RRs: " + getAnsCount());
    lines.add("  Authority RRs: " + getAuthCount());
    lines.add("  Additional RRs: " + getAddCount());
    if(!getQueries().isEmpty()) {
      lines.add("  Queries");
      for(DNSEntry e : getQueries()) {
        lines.add("    Name: " + e.getName());
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
        lines.add("    Name: 0x" + String.format("%04x", e.getNameOffset()));
        lines.add("    Type: " + e.getTypeString());
        lines.add("    Class: " + e.getClazzString());
        lines.add("    Time to live: " + ((int)e.getTTL() / 60) + " minutes, " + ((int)e.getTTL() % 60) + " seconds");
        lines.add("    Data length: " + e.getDataLength());
        lines.add("    Addr: " + e.getAddress());
      }
    }
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
  public boolean isZero() {
    return zero;
  }
  
  /**
   * @param zero
   *          the zero to set
   */
  public void setZero(final boolean zero) {
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
