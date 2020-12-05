package fr.ralala.netcap.net.layer.internet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;
import fr.ralala.netcap.net.layer.internet.utils.IPv6Option;
import fr.ralala.netcap.net.layer.transport.TCP;
import fr.ralala.netcap.net.layer.transport.UDP;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class IPv6 extends Layer {
  public static final int IPPROTO_UDP = 17;
  public static final int IPPROTO_TCP = 6;
  public static final int IPPROTO_IGMP = 2;
  public static final int IPPROTO_ICMPV6 = 58;
  private int mPriority;
  private int mVersion;
  private int mFlowLbl_1;
  private int mFlowLbl_2;
  private int mFlowLbl_3;
  private int mPayloadLen;
  private int mNexthdr;
  private int mHopLimit;
  private String mSource;
  private String mDestination;
  private int mHeaderLength;
  private int mHbhLength;
  private final List<IPv6Option> mOptions = new ArrayList<IPv6Option>();

  public IPv6() {
    super();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("IPv6", "Internet Protocol v" + getVersion() + " (Src: " + mSource + ", Dst: " + mDestination + ")");
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
    lines.add("  Priority: " + getPriority());
    lines.add("  Flowlabel: 0x" + String.format(Locale.US, "%02x%02x%02x", getFlowLbl_1(), getFlowLbl_2(), getFlowLbl_3()));
    lines.add("  Payload length: " + getPayloadLen());
    if (getOptions().isEmpty())
      lines.add("  Next header: " + getNexthdr());
    else
      lines.add("  Next header: Hop-by-Hop option");
    lines.add("  Hop limit: " + getHopLimit());
    lines.add("  Source: " + getSource());
    lines.add("  Destination: " + getDestination());
    if (!getOptions().isEmpty()) {
      lines.add("  Hop-by-Hop option");
      lines.add("    Next header: " + getNexthdr());
      lines.add("    Length: " + getHBHLength());
      for (IPv6Option opt : getOptions()) {
        lines.add("    IPv6 Option");
        lines.add("      Type: " + opt.getType());
        lines.add("      Length: " + opt.getLength());
        if (opt.getLength() != 0) {
          StringBuilder s = new StringBuilder();
          byte[] mld = opt.getMLD();
          for (byte b : mld) s.append(String.format(Locale.US, "%02x ", b));
          s = new StringBuilder(s.toString().trim());
          lines.add("      Value: " + s);
        } else
          lines.add("      Value: MISSING");
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
    byte[] temp16 = new byte[16];
    byte[] temp2 = new byte[2];
    int offset = 0;
    byte ihl_priority = buffer[offset++];
    mVersion = (byte) (ihl_priority >> 4);
    mPriority = (byte) (ihl_priority & 0x0f);

    mFlowLbl_1 = buffer[offset++];
    mFlowLbl_2 = buffer[offset++];
    mFlowLbl_3 = buffer[offset++];


    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mPayloadLen = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;

    mNexthdr = buffer[offset++];
    mHopLimit = buffer[offset++];
    if (mHopLimit < 0) mHopLimit &= 0xFF;


    NetworkHelper.zcopy(buffer, offset, temp16, 0, temp16.length);
    offset += temp16.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp16);
      mSource = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      mSource = e.getMessage();
    }
    NetworkHelper.zcopy(buffer, offset, temp16, 0, temp16.length);
    offset += temp16.length;
    try {
      InetAddress address = InetAddress.getByAddress(temp16);
      mDestination = address.getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      mDestination = e.getMessage();
    }
    if (mNexthdr == 0) {
      int opts = (buffer.length - offset) - (mPayloadLen - 8);
      mNexthdr = buffer[offset++];
      mHbhLength = buffer[offset++];
      for (int i = 0; i < opts; i++) {
        IPv6Option opt = new IPv6Option();
        opt.setType(buffer[offset++]);
        opt.setLength(buffer[offset++]);
        if (opt.getLength() == 0) {
          mOptions.add(opt);
          break;
        }
        byte[] mld = new byte[opt.getLength()];
        NetworkHelper.zcopy(buffer, offset, mld, 0, mld.length);
        offset += mld.length;
        opt.setMLD(mld);
        mOptions.add(opt);
      }
    }
    mHeaderLength = offset;
    if (mNexthdr == IPPROTO_UDP) {
      byte[] sub_buffer = resizeBuffer(buffer);
      UDP udp = new UDP();
      udp.decodeLayer(sub_buffer, this);
      setNext(udp);
    } else if (mNexthdr == IPPROTO_TCP) {
      byte[] sub_buffer = resizeBuffer(buffer);
      TCP tcp = new TCP();
      tcp.decodeLayer(sub_buffer, this);
      setNext(tcp);
    } else if (mNexthdr == IPPROTO_IGMP) {
      IGMP igmp = new IGMP();
      offset += 4; // options
      mHeaderLength = offset;
      byte[] sub_buffer = resizeBuffer(buffer);
      igmp.decodeLayer(sub_buffer, this);
      setNext(igmp);
    } else if (mNexthdr == IPPROTO_ICMPV6) {
      byte[] sub_buffer = resizeBuffer(buffer);
      ICMPv6 icmp = new ICMPv6();
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
   * @return the options
   */
  public List<IPv6Option> getOptions() {
    return mOptions;
  }

  /**
   * Get the source IPv6 addr.
   *
   * @return String
   */
  public String getSource() {
    return mSource;
  }

  /**
   * Set the source IPv6 addr.
   *
   * @param source The address
   */
  public void setSource(final String source) {
    mSource = source;
  }

  /**
   * Get the destination IPv6 addr.
   *
   * @return String
   */
  public String getDestination() {
    return mDestination;
  }

  /**
   * Set the destination IPv6 addr.
   *
   * @param destination The address
   */
  public void setDestination(final String destination) {
    mDestination = destination;
  }

  /**
   * Get the priority.
   *
   * @return int
   */
  public int getPriority() {
    return mPriority;
  }

  /**
   * Set the priority.
   *
   * @param priority The value.
   */
  public void setPriority(final int priority) {
    mPriority = priority;
  }

  /**
   * Get the version.
   *
   * @return int
   */
  public int getVersion() {
    return mVersion;
  }

  /**
   * Set the version.
   *
   * @param version The value.
   */
  public void setVersion(final int version) {
    mVersion = version;
  }

  /**
   * Get the FlowLbl[0].
   *
   * @return int
   */
  public int getFlowLbl_1() {
    return mFlowLbl_1;
  }

  /**
   * Set the FlowLbl[0].
   *
   * @param flowLbl_1 The value.
   */
  public void setFlowLbl_1(final int flowLbl_1) {
    mFlowLbl_1 = flowLbl_1;
  }

  /**
   * Get the FlowLbl[1].
   *
   * @return int
   */
  public int getFlowLbl_2() {
    return mFlowLbl_2;
  }

  /**
   * Set the FlowLbl[1].
   *
   * @param flowLbl_2 The value.
   */
  public void setFlowLbl_2(final int flowLbl_2) {
    mFlowLbl_2 = flowLbl_2;
  }

  /**
   * Get the FlowLbl[2].
   *
   * @return int
   */
  public int getFlowLbl_3() {
    return mFlowLbl_3;
  }

  /**
   * Set the FlowLbl[2].
   *
   * @param flowLbl_3 The value.
   */
  public void setFlowLbl_3(final int flowLbl_3) {
    mFlowLbl_3 = flowLbl_3;
  }

  /**
   * Get the payload length.
   *
   * @return int
   */
  public int getPayloadLen() {
    return mPayloadLen;
  }

  /**
   * Set the payload length.
   *
   * @param payloadLen The value.
   */
  public void setPayloadLen(final int payloadLen) {
    mPayloadLen = payloadLen;
  }

  /**
   * Get the next hdr.
   *
   * @return int
   */
  public int getNexthdr() {
    return mNexthdr;
  }

  /**
   * Set the next hdr.
   *
   * @param nexthdr The value.
   */
  public void setNexthdr(final int nexthdr) {
    mNexthdr = nexthdr;
  }

  /**
   * Get the hop limit.
   *
   * @return int
   */
  public int getHopLimit() {
    return mHopLimit;
  }

  /**
   * Set the hop limit.
   *
   * @param hopLimit The value.
   */
  public void setHopLimit(final int hopLimit) {
    mHopLimit = hopLimit;
  }

  /**
   * @return the hbhLength
   */
  public int getHBHLength() {
    return mHbhLength;
  }

  /**
   * @param hbhLength the hbhLength to set
   */
  public void setHBHLength(int hbhLength) {
    mHbhLength = hbhLength;
  }

}
