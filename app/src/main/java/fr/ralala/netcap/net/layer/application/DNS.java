package fr.ralala.netcap.net.layer.application;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;
import fr.ralala.netcap.net.layer.application.utils.DNSEntry;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class DNS extends Layer {
  public static final int PORT = 53;
  public static final int QTYPE_ADDRESS = 1; /* Ipv4 address */
  public static final int QTYPE_NAMESERVER = 2; /* Nameserver */
  public static final int QTYPE_CNAME = 5; /* canonical name */
  public static final int QTYPE_SOA = 6; /* start of authority zone */
  public static final int QTYPE_PTR = 12; /* domain name pointer */
  public static final int QTYPE_MX = 15; /* Mail server */

  private int mId;                   // identification number
  private boolean mRd;
  private boolean mTc;
  private boolean mAa;
  private int mOpcode;
  private boolean mQr;
  private int mRcode;
  private int mZero;
  private boolean mRa;
  private int mQCount;
  private int mAnsCount;
  private int mAuthCount;
  private int mAddCount;
  private final List<DNSEntry> mQueries;
  private final List<DNSEntry> mAnswers;
  private final List<DNSEntry> mAuthorities;
  private final List<DNSEntry> mAdditional;
  private int mHeaderLength;
  private int mFlags;
  private int mOffset;

  public DNS() {
    super();
    mQueries = new ArrayList<>();
    mAnswers = new ArrayList<>();
    mAuthorities = new ArrayList<>();
    mAdditional = new ArrayList<>();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("DNS", "Domain Name System (" + ((mQr && !mRd) ? "query" : "response") + ")");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    StringBuilder sb = new StringBuilder();
    if (!isQR()) {
      for (DNSEntry e : mQueries)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getName());
      return "Standard query 0x" + String.format(Locale.US, "%04x", getID()) + sb.toString();
    } else {
      for (DNSEntry e : mAnswers)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getAddress());
      for (DNSEntry e : mAuthorities)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getAddress());
      for (DNSEntry e : mAdditional)
        sb.append(" ").append(e.getTypeString()).append(" ").append(e.getAddress());
      return "Standard query response 0x" + String.format(Locale.US, "%04x", getID()) + sb.toString();
    }
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Transaction ID: 0x" + String.format(Locale.US, "%04x", getID()));
    lines.add("  Flags: 0x" + String.format(Locale.US, "%04x", getFlags()));

    lines.add("    " + (isQR() ? "1" : "0") + "... .... .... .... = Response: Message is a " + (isQR() ? "response" : "query"));
    lines.add("    ." + (BigInteger.valueOf(mFlags).testBit(4) ? "1" : "0") +
        (BigInteger.valueOf(mFlags).testBit(3) ? "1" : "0") + (BigInteger.valueOf(mFlags).testBit(11) ? "1" : "0") +
        " " + (BigInteger.valueOf(mFlags).testBit(11) ? "1" : "0") + "... .... .... = Opcode (" + mOpcode + ")");
    lines.add("    .... ." + (isAA() ? "1" : "0") + ".. .... .... = Authoritative");
    lines.add("    .... .." + (isTC() ? "1" : "0") + ". .... .... = Truncated");
    lines.add("    .... ..." + (isRD() ? "1" : "0") + " .... .... = Recursion desired");
    lines.add("    .... .... " + (isRD() ? "1" : "0") + "... .... = Recursion available");
    lines.add("    .... .... ." + (BigInteger.valueOf(mZero).testBit(3) ? "1" : "0") +
        (BigInteger.valueOf(mZero).testBit(2) ? "1" : "0") + (BigInteger.valueOf(mZero).testBit(1) ? "1" : "0") + " .... = Zero (" + mZero + ")");
    lines.add("    .... .... .... " + (BigInteger.valueOf(mRcode).testBit(4) ? "1" : "0") + (BigInteger.valueOf(mRcode).testBit(3) ? "1" : "0") +
        (BigInteger.valueOf(mRcode).testBit(2) ? "1" : "0") + (BigInteger.valueOf(mRcode).testBit(1) ? "1" : "0") + " = Reply code (" + mRcode + ")");
    lines.add("  Questions: " + getQCount());
    lines.add("  Answer RRs: " + getAnsCount());
    lines.add("  Authority RRs: " + getAuthCount());
    lines.add("  Additional RRs: " + getAddCount());
    if (!getQueries().isEmpty()) {
      lines.add("  Queries");
      for (DNSEntry e : getQueries()) {
        lines.add("   -Name: " + e.getName());
        lines.add("    Type: " + e.getTypeString());
        lines.add("    Class: " + e.getClazzString());
      }
    }
    addDNSEntry("Answer", getAnswers(), lines);
    addDNSEntry("Authority", getAuthorities(), lines);
    addDNSEntry("Additional", getAdditional(), lines);
  }

  private void addDNSEntry(final String label, final List<DNSEntry> entries, final List<String> lines) {
    if (!entries.isEmpty()) {
      lines.add("  " + label);
      for (DNSEntry e : entries) {
        lines.add("   -Name: " + e.getName());
        lines.add("    Type: " + e.getTypeString());
        lines.add("    Class: " + e.getClazzString());
        lines.add("    Time to live: " + ((int) e.getTTL() / 60) + " minutes, " + ((int) e.getTTL() % 60) + " seconds)");
        lines.add("    Data length: " + e.getDataLength());
        lines.add("    Addr: " + e.getAddress());
      }
    }
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return mHeaderLength;
  }

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    byte[] temp2 = new byte[2];
    mOffset = 0;


    NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
    mId = NetworkHelper.ntohs2(temp2);
    mOffset += temp2.length;

    NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
    mFlags = NetworkHelper.ntohs2(temp2);

    mOffset += temp2.length;
    mQr = BigInteger.valueOf(mFlags).testBit(15);
    mOpcode = NetworkHelper.setBit((byte) 0, 3, BigInteger.valueOf(mFlags).testBit(14));
    mOpcode = NetworkHelper.setBit((byte) mOpcode, 2, BigInteger.valueOf(mFlags).testBit(13));
    mOpcode = NetworkHelper.setBit((byte) mOpcode, 1, BigInteger.valueOf(mFlags).testBit(12));
    mOpcode = NetworkHelper.setBit((byte) mOpcode, 0, BigInteger.valueOf(mFlags).testBit(11));
    mAa = BigInteger.valueOf(mFlags).testBit(10);
    mTc = BigInteger.valueOf(mFlags).testBit(9);
    mRd = BigInteger.valueOf(mFlags).testBit(8);
    mRa = BigInteger.valueOf(mFlags).testBit(7);


    mZero = NetworkHelper.setBit((byte) 0, 0, BigInteger.valueOf(mFlags).testBit(6));
    mZero = NetworkHelper.setBit((byte) mZero, 1, BigInteger.valueOf(mFlags).testBit(5));
    mZero = NetworkHelper.setBit((byte) mZero, 2, BigInteger.valueOf(mFlags).testBit(4));

    mRcode = NetworkHelper.setBit((byte) 0, 3, BigInteger.valueOf(mFlags).testBit(3));
    mRcode = NetworkHelper.setBit((byte) mRcode, 2, BigInteger.valueOf(mFlags).testBit(2));
    mRcode = NetworkHelper.setBit((byte) mRcode, 1, BigInteger.valueOf(mFlags).testBit(1));
    mRcode = NetworkHelper.setBit((byte) mRcode, 0, BigInteger.valueOf(mFlags).testBit(0));


    NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
    mQCount = NetworkHelper.ntohs2(temp2);
    mOffset += temp2.length;
    NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
    mAnsCount = NetworkHelper.ntohs2(temp2);
    mOffset += temp2.length;
    NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
    mAuthCount = NetworkHelper.ntohs2(temp2);
    mOffset += temp2.length;
    NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
    mAddCount = NetworkHelper.ntohs2(temp2);
    mOffset += temp2.length;

    if (mQCount != 0) {
      for (int i = 0; i < mQCount; i++) {
        DNSEntry e = new DNSEntry();
        // get the name
        List<String> n = new ArrayList<String>();
        int l = normalizeFromDNS(buffer, mOffset, n);
        mOffset += l;
        e.setName(formatFromDNS(n));
        NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
        e.setType(NetworkHelper.ntohs2(temp2));
        mOffset += temp2.length;
        NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
        e.setClazz(NetworkHelper.ntohs2(temp2));
        mOffset += temp2.length;
        addQuery(e);
      }
    }
    decodeResponse(buffer, mAnsCount, mAnswers);
    decodeResponse(buffer, mAuthCount, mAuthorities);
    decodeResponse(buffer, mAddCount, mAdditional);
    mHeaderLength = mOffset;
    byte[] sub_buffer = resizeBuffer(buffer);
    if (sub_buffer != null) {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  private void decodeResponse(byte[] buffer, int count, List<DNSEntry> list) {
    byte[] temp2 = new byte[2];
    byte[] temp4 = new byte[4];
    if (count != 0) {
      for (int i = 0; i < count; i++) {
        DNSEntry e = new DNSEntry();
        // get the name
        List<String> n = new ArrayList<String>();
        int l = normalizeFromDNS(buffer, mOffset, n);
        mOffset += l;
        e.setName(formatFromDNS(n));
        NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
        e.setType(NetworkHelper.ntohs2(temp2));
        mOffset += temp2.length;
        NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
        e.setClazz(NetworkHelper.ntohs2(temp2));
        mOffset += temp2.length;
        NetworkHelper.zcopy(buffer, mOffset, temp4, 0, temp4.length);
        //e.setTTL(NetworkHelper.ntohl(temp4));
        e.setTTL(NetworkHelper.getInt(temp4, 0));

        mOffset += temp4.length;
        NetworkHelper.zcopy(buffer, mOffset, temp2, 0, temp2.length);
        e.setDataLength(NetworkHelper.ntohs2(temp2));
        mOffset += temp2.length;
        if (e.getType() == QTYPE_CNAME) {
          n = new ArrayList<String>();
          l = normalizeFromDNS(buffer, mOffset, n);
          mOffset += l;
          e.setAddress(formatFromDNS(n));
        } else {
          NetworkHelper.zcopy(buffer, mOffset, temp4, 0, temp4.length);
          mOffset += temp4.length;
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
    StringBuilder name = new StringBuilder();
    for (int j = 0; j < n.size(); j++) {
      name.append(n.get(j));
      if (j != n.size() - 1)
        name.append(".");
    }
    return name.toString();
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
        if (msg.length < pos + 1 && (msg[pos + 1] & 0x0C) == 0x0C) break;
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
    return mFlags;
  }

  /**
   * @param flags the flags to set
   */
  public void setFlags(int flags) {
    mFlags = flags;
  }

  /**
   * @return the queries
   */
  public List<DNSEntry> getQueries() {
    return mQueries;
  }

  /**
   * @param query the query to add
   */
  public void addQuery(DNSEntry query) {
    mQueries.add(query);
  }

  /**
   * @return the answers
   */
  public List<DNSEntry> getAnswers() {
    return mAnswers;
  }

  /**
   * @param answer the answer to add
   */
  public void addAnswer(DNSEntry answer) {
    mAnswers.add(answer);
  }

  /**
   * @return the authorities
   */
  public List<DNSEntry> getAuthorities() {
    return mAuthorities;
  }

  /**
   * @param authority the authority to add
   */
  public void addAuthority(DNSEntry authority) {
    mAuthorities.add(authority);
  }

  /**
   * @return the additional
   */
  public List<DNSEntry> getAdditional() {
    return mAdditional;
  }

  /**
   * @param additional the additional to set
   */
  public void addAdditional(DNSEntry additional) {
    mAdditional.add(additional);
  }

  /**
   * @return the id
   */
  public int getID() {
    return mId;
  }

  /**
   * @param id the id to set
   */
  public void setID(final int id) {
    mId = id;
  }

  /**
   * @return the rd
   */
  public boolean isRD() {
    return mRd;
  }

  /**
   * @param rd the rd to set
   */
  public void setRD(final boolean rd) {
    mRd = rd;
  }

  /**
   * @return the tc
   */
  public boolean isTC() {
    return mTc;
  }

  /**
   * @param tc the tc to set
   */
  public void setTC(final boolean tc) {
    mTc = tc;
  }

  /**
   * @return the aa
   */
  public boolean isAA() {
    return mAa;
  }

  /**
   * @param aa the aa to set
   */
  public void setAA(final boolean aa) {
    mAa = aa;
  }

  /**
   * @return the opcode
   */
  public int getOpcode() {
    return mOpcode;
  }

  /**
   * @param opcode the opcode to set
   */
  public void setOpcode(final int opcode) {
    mOpcode = opcode;
  }

  /**
   * @return the qr
   */
  public boolean isQR() {
    return mQr;
  }

  /**
   * @param qr the qr to set
   */
  public void setQR(final boolean qr) {
    mQr = qr;
  }

  /**
   * @return the rcode
   */
  public int getRCode() {
    return mRcode;
  }

  /**
   * @param rcode the rcode to set
   */
  public void setRCode(final int rcode) {
    mRcode = rcode;
  }

  /**
   * @return the zero
   */
  public int getZero() {
    return mZero;
  }

  /**
   * @param zero the zero to set
   */
  public void setZero(final int zero) {
    mZero = zero;
  }

  /**
   * @return the ra
   */
  public boolean isRA() {
    return mRa;
  }

  /**
   * @param ra the ra to set
   */
  public void setRA(final boolean ra) {
    mRa = ra;
  }

  /**
   * @return the qCount
   */
  public int getQCount() {
    return mQCount;
  }

  /**
   * @param qCount the qCount to set
   */
  public void setQCount(final int qCount) {
    mQCount = qCount;
  }

  /**
   * @return the ansCount
   */
  public int getAnsCount() {
    return mAnsCount;
  }

  /**
   * @param ansCount the ansCount to set
   */
  public void setAnsCount(final int ansCount) {
    mAnsCount = ansCount;
  }

  /**
   * @return the authCount
   */
  public int getAuthCount() {
    return mAuthCount;
  }

  /**
   * @param authCount the authCount to set
   */
  public void setAuthCount(final int authCount) {
    mAuthCount = authCount;
  }

  /**
   * @return the addCount
   */
  public int getAddCount() {
    return mAddCount;
  }

  /**
   * @param addCount the addCount to set
   */
  public void setAddCount(final int addCount) {
    mAddCount = addCount;
  }

}
