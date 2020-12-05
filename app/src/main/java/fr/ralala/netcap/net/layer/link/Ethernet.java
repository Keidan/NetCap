package fr.ralala.netcap.net.layer.link;

import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;
import fr.ralala.netcap.net.layer.internet.IPv4;
import fr.ralala.netcap.net.layer.internet.IPv6;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class Ethernet extends Layer {
  public static final int HEADER_LENGTH = 14;
  public static final int ETH_P_IP = 0x0800;
  public static final int ETH_P_IPV6 = 0x86DD;
  public static final int ETH_P_ARP = 0x0806;
  private String mSource = "";
  private String mDestination = "";
  private int mProto = 0;

  public Ethernet() {
    super();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("Ethernet", "Ethernet (Src: " + mSource + ", Dst: " + mDestination + ")");
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
    lines.add("  Source: " + getSource());
    lines.add("  Destination: " + getDestination());
    if (mProto == ETH_P_IP)
      lines.add("  Type: IP (" + String.format(Locale.US, "0x%04d", ETH_P_IP) + ")");
    else if (mProto == ETH_P_IPV6)
      lines.add("  Type: IPv6 (" + String.format(Locale.US, "0x%04d", ETH_P_IPV6) + ")");
    else if (mProto == ETH_P_ARP)
      lines.add("  Type: ARP (" + String.format(Locale.US, "0x%04d", ETH_P_ARP) + ")");
    else
      lines.add("  Type: (" + String.format(Locale.US, "0x%04d", getProto()) + ")");
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return 14;
  }

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    byte[] temp6 = new byte[6];
    byte[] temp2 = new byte[2];
    System.arraycopy(buffer, 0, temp6, 0, temp6.length);
    mSource = String.format(Locale.US, "%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
    System.arraycopy(buffer, 6, temp6, 0, temp6.length);
    mDestination = String.format(Locale.US, "%02x:%02x:%02x:%02x:%02x:%02x", temp6[0], temp6[1], temp6[2], temp6[3], temp6[4], temp6[5]);
    System.arraycopy(buffer, 12, temp2, 0, temp2.length);
    mProto = NetworkHelper.ntohs2(temp2);
    byte[] sub_buffer = resizeBuffer(buffer);
    if (mProto == ETH_P_IP) {
      IPv4 ip = new IPv4();
      ip.decodeLayer(sub_buffer, this);
      setNext(ip);
    } else if (mProto == ETH_P_IPV6) {
      IPv6 ip = new IPv6();
      ip.decodeLayer(sub_buffer, this);
      setNext(ip);
    } else if (mProto == ETH_P_ARP) {
      ARP arp = new ARP();
      arp.decodeLayer(sub_buffer, this);
      setNext(arp);
    } else {
      Payload p = new Payload();
      p.decodeLayer(sub_buffer, this);
      setNext(p);
    }
  }

  /**
   * Get the source eth addr.
   *
   * @return String
   */
  public String getSource() {
    return mSource;
  }

  /**
   * Set the source eth addr.
   *
   * @param source The address
   */
  public void setSource(final String source) {
    mSource = source;
  }

  /**
   * Get the destination eth addr.
   *
   * @return String
   */
  public String getDestination() {
    return mDestination;
  }

  /**
   * Set the destination eth addr.
   *
   * @param destination The address
   */
  public void setDestination(final String destination) {
    mDestination = destination;
  }

  /**
   * Get the packet type ID field.
   *
   * @return int
   */
  public int getProto() {
    return mProto;
  }

  /**
   * Set the packet type ID field.
   *
   * @param proto The new packet type ID field.
   */
  public void setProto(final int proto) {
    mProto = proto;
  }

}
