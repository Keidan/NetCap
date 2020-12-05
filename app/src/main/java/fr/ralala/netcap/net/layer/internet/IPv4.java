package fr.ralala.netcap.net.layer.internet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;
import fr.ralala.netcap.net.layer.transport.TCP;
import fr.ralala.netcap.net.layer.transport.UDP;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class IPv4 extends Layer {
  public static final int IPPROTO_UDP = 17;
  public static final int IPPROTO_TCP = 6;
  public static final int IPPROTO_IGMP = 2;
  public static final int IPPROTO_ICMP = 1;
  public static final int IP_RF = 0x8000;
  public static final int IP_DF = 0x4000;
  public static final int IP_MF = 0x2000;
  private int mIhl;
  private int mVersion;
  private short mTos;
  private int mTotLength;
  private int mIdent;
  private int mId;
  private int mFragOff;
  private int mTtl;
  private int mProtocol;
  private int mChecksum;
  private String mSource;
  private String mDestination;
  private int mHeaderLength;

  private boolean mReservedBit = false;
  private boolean mDontFragment = false;
  private boolean mMoreFragments = false;

  public IPv4() {
    super();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("IPv" + getVersion(), "Internet Protocol v" + getVersion() + " (Src: " + mSource + ", Dst: " + mDestination + ")");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    return null;
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Version: " + getVersion());
    lines.add("  Header length: " + (getIHL() * 4) + " bytes");
    lines.add("  Differentiated Services Field:");
    lines.add("    Total Length: " + getTotLength());
    lines.add("    Identification: 0x" + String.format(Locale.US, "%04x", getIdent()) + " (" + getIdent() + ")");
    lines.add("  Flags: ");
    lines.add("    " + (isReservedBit() ? "1" : "0") + "... Reserved bit: " + (isReservedBit() ? "Set" : "Not Set"));
    lines.add("    ." + (isDontFragment() ? "1" : "0") + ".. Don't fragment: " + (isDontFragment() ? "Set" : "Not Set"));
    lines.add("    .." + (isMoreFragments() ? "1" : "0") + ". More fragments: " + (isMoreFragments() ? "Set" : "Not Set"));
    lines.add("  Fragment offset: " + getFragOff());
    lines.add("  Time to live: " + getTTL());
    lines.add("  Protocol: " + getProtocol());
    lines.add("  Header checksum: 0x" + String.format(Locale.US, "%04x", getChecksum()));
    lines.add("  Source: " + getSource());
    lines.add("  Destination: " + getDestination());
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
    byte[] temp4 = new byte[4];
    byte[] temp2 = new byte[2];
    int offset = 0;
    byte ihl_version = buffer[offset++];
    mVersion = (byte) (ihl_version >> 4);
    mIhl = (byte) (ihl_version & 0x0f);
    NetworkHelper.zcopy(buffer, offset, temp2, 0, 1);
    mTos = NetworkHelper.ntohs(temp2);
    offset++;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mTotLength = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mIdent = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mFragOff = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    int foff = mFragOff;
    mId = ((foff >> 8) & 0xff);
    mFragOff = (foff & IP_DF) != 0 ? 0 : foff;
    mReservedBit = (foff & IP_RF) != 0;
    mDontFragment = (foff & IP_DF) != 0;
    mMoreFragments = (foff & IP_MF) != 0;

    NetworkHelper.zcopy(buffer, offset, temp2, 0, 1);
    mTtl = NetworkHelper.getValue(temp2);
    mTtl = (short) (mTtl & 0xff);
    offset++;
    mProtocol = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mChecksum = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;

    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset += temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      mSource = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      mSource = e.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    offset += temp4.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp4);
      mDestination = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      mDestination = e.getMessage();
    }
    mHeaderLength = offset;
    if (mProtocol == IPPROTO_UDP) {
      byte[] sub_buffer = resizeBuffer(buffer);
      UDP udp = new UDP();
      udp.decodeLayer(sub_buffer, this);
      setNext(udp);
    } else if (mProtocol == IPPROTO_TCP) {
      byte[] sub_buffer = resizeBuffer(buffer);
      TCP tcp = new TCP();
      tcp.decodeLayer(sub_buffer, this);
      setNext(tcp);
    } else if (mProtocol == IPPROTO_IGMP) {
      IGMP igmp = new IGMP();
      offset += 4; // options
      mHeaderLength = offset;
      byte[] sub_buffer = resizeBuffer(buffer);
      igmp.decodeLayer(sub_buffer, this);
      setNext(igmp);
    } else if (mProtocol == IPPROTO_ICMP) {
      byte[] sub_buffer = resizeBuffer(buffer);
      ICMPv4 icmp = new ICMPv4();
      icmp.decodeLayer(sub_buffer, this);
      setNext(icmp);
    } else {
      byte[] sub_buffer = resizeBuffer(buffer);
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
    return mSource;
  }

  /**
   * Set the source IPv4 addr.
   *
   * @param source The address
   */
  public void setSource(final String source) {
    mSource = source;
  }

  /**
   * Get the destination IPv4 addr.
   *
   * @return String
   */
  public String getDestination() {
    return mDestination;
  }

  /**
   * Set the destination IPv4 addr.
   *
   * @param destination The address
   */
  public void setDestination(final String destination) {
    mDestination = destination;
  }

  /**
   * Get the TOS value.
   *
   * @return int
   */
  public short getTOS() {
    return mTos;
  }

  /**
   * Set the TOS value.
   *
   * @param tos The value.
   */
  public void setTOS(final short tos) {
    mTos = tos;
  }

  /**
   * Get the Total Length value.
   *
   * @return int
   */
  public int getTotLength() {
    return mTotLength;
  }

  /**
   * Set the Total Length value.
   *
   * @param totLength The value.
   */
  public void setTotLength(final int totLength) {
    mTotLength = totLength;
  }

  /**
   * Get the Flags value.
   *
   * @return int
   */
  public int getFlags() {
    return mId;
  }

  /**
   * Set the id value.
   *
   * @param id The value.
   */
  public void setFlags(final int id) {
    mId = id;
  }

  /**
   * @return the ident
   */
  public int getIdent() {
    return mIdent;
  }

  /**
   * @param ident the ident to set
   */
  public void setIdent(int ident) {
    mIdent = ident;
  }

  /**
   * Get the FragOffset value.
   *
   * @return int
   */
  public int getFragOff() {
    return mFragOff;
  }

  /**
   * Set the FragOffset value.
   *
   * @param fragOff The value.
   */
  public void setFragOff(final int fragOff) {
    mFragOff = fragOff;
  }

  /**
   * Get the TTL value.
   *
   * @return int
   */
  public int getTTL() {
    return mTtl;
  }

  /**
   * Set the ttl value.
   *
   * @param ttl The value.
   */
  public void setTTL(final int ttl) {
    mTtl = ttl;
  }

  /**
   * Get the Protocol value.
   *
   * @return int
   */
  public int getProtocol() {
    return mProtocol;
  }

  /**
   * Set the protocol value.
   *
   * @param protocol The value.
   */
  public void setProtocol(final int protocol) {
    mProtocol = protocol;
  }

  /**
   * Get the checksum value.
   *
   * @return int
   */
  public int getChecksum() {
    return mChecksum;
  }

  /**
   * Set the checksum value.
   *
   * @param checksum The value.
   */
  public void setChecksum(final int checksum) {
    mChecksum = checksum;
  }

  public boolean isReservedBit() {
    return mReservedBit;
  }

  public void setReservedBit(boolean reservedBit) {
    mReservedBit = reservedBit;
  }

  public boolean isDontFragment() {
    return mDontFragment;
  }

  public void setDontFragment(boolean dontFragment) {
    mDontFragment = dontFragment;
  }

  public boolean isMoreFragments() {
    return mMoreFragments;
  }

  public void setMoreFragments(boolean moreFragments) {
    mMoreFragments = moreFragments;
  }

  public int getIHL() {
    return mIhl;
  }

  public void setIHL(int ihl) {
    mIhl = ihl;
  }

  public int getVersion() {
    return mVersion;
  }

  public void setVersion(int version) {
    mVersion = version;
  }

}
