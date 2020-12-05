package fr.ralala.netcap.net.layer.transport;

import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.Service;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;
import fr.ralala.netcap.net.layer.application.DHCPv4;
import fr.ralala.netcap.net.layer.application.DHCPv6;
import fr.ralala.netcap.net.layer.application.DNS;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class UDP extends Layer {
  private int mSource;
  private int mDestination;
  private int mLength;
  private int mChecksum;

  public UDP() {
    super();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("UDP", "User Datagram Protocol (Src: " + mSource + ", Dst: " + mDestination + ")");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    String desc = "";
    Service srv = Service.findByPort(getSource());
    if (srv == Service.NOT_FOUND) desc += getSource();
    else desc += srv.getName() + "(" + srv.getPort() + ")";
    desc += " > ";
    srv = Service.findByPort(getDestination());
    if (srv == Service.NOT_FOUND) desc += getDestination();
    else desc += srv.getName() + "(" + srv.getPort() + ")";
    return desc;
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    lines.add("  Source port: " + getSource());
    lines.add("  Destination port: " + getDestination());
    lines.add("  Length: " + getLength());
    lines.add("  Checksum: 0x" + String.format(Locale.US, "%04x", getChecksum()));
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return 8;
  }

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    int offset = 0;
    byte[] temp2 = new byte[2];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mSource = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mDestination = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mLength = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mChecksum = NetworkHelper.ntohs2(temp2);

    byte[] sub_buffer = resizeBuffer(buffer);
    if (sub_buffer != null) {
      if (mSource == DNS.PORT || mDestination == DNS.PORT) {
        DNS dns = new DNS();
        dns.decodeLayer(sub_buffer, this);
        setNext(dns);
      } else if (mSource == DHCPv4.PORT_CLI || mDestination == DHCPv4.PORT_CLI || mSource == DHCPv4.PORT_SRV || mDestination == DHCPv4.PORT_SRV) {
        DHCPv4 dhcp = new DHCPv4();
        dhcp.decodeLayer(sub_buffer, this);
        setNext(dhcp);
      } else if (mSource == DHCPv6.PORT_CLI || mDestination == DHCPv6.PORT_CLI || mSource == DHCPv6.PORT_SRV || mDestination == DHCPv6.PORT_SRV) {
        DHCPv6 dhcp = new DHCPv6();
        dhcp.decodeLayer(sub_buffer, this);
        setNext(dhcp);
      } else {
        Payload p = new Payload();
        p.decodeLayer(sub_buffer, this);
        setNext(p);
      }
    }
  }

  /**
   * Get the source port.
   *
   * @return int
   */
  public int getSource() {
    return mSource;
  }

  /**
   * Set the source port.
   *
   * @param source the source to set
   */
  public void setSource(final int source) {
    mSource = source;
  }

  /**
   * Get the destination port.
   *
   * @return int
   */
  public int getDestination() {
    return mDestination;
  }

  /**
   * Set the destination port.
   *
   * @param destination the destination to set
   */
  public void setDestination(final int destination) {
    mDestination = destination;
  }

  /**
   * Get the length.
   *
   * @return int
   */
  public int getLength() {
    return mLength;
  }

  /**
   * Set the length.
   *
   * @param length the length to set
   */
  public void setLength(final int length) {
    mLength = length;
  }

  /**
   * Get the Checksum.
   *
   * @return int
   */
  public int getChecksum() {
    return mChecksum;
  }

  /**
   * Set the checksum.
   *
   * @param checksum the checksum to set
   */
  public void setChecksum(final int checksum) {
    mChecksum = checksum;
  }

}
