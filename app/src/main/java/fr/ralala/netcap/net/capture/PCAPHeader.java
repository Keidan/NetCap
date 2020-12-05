package fr.ralala.netcap.net.capture;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class PCAPHeader {
  public static final int MAX_SIGNED_INT = Integer.MAX_VALUE;
  public static final int MAX_SIGNED_SHORT = Short.MAX_VALUE;
  /**
   * Major version of the PCAP file.
   */
  public final static int PCAP_VERSION_MAJOR = 2;
  /**
   * Minor version of the PCAP file.
   */
  public final static int PCAP_VERSION_MINOR = 4;
  /**
   * PCAP Magic.
   */
  public final static long PCAP_MAGIC_NATIVE = 0xa1b2c3d4;
  /**
   * PCAP Magic.
   */
  public final static long PCAP_MAGIC_SWAPPED = 0xd4c3b2a1;
  /**
   * Capture type.
   */
  public final static int PCAP_LINKTYPE_ETHERNET = 1;
  /**
   * Capture size.
   */
  public final static int PCAP_SNAPLEN = 65535;
  private long mMagicNumber;
  private int mVersionMajor;
  private int mVersionMinor;
  private int mThiszone;
  private long mSigfigs;
  private long mSnapLength;
  private long mNetwork;

  /**
   * Get the magic number.
   *
   * @return unsigned int
   */
  public long getMagicNumber() {
    return mMagicNumber;
  }

  /**
   * Set the magic number.
   *
   * @param magicNumber New value.
   */
  public void setMagicNumber(final long magicNumber) {
    mMagicNumber = magicNumber;
  }

  /**
   * Get the major version number.
   *
   * @return unsigned short
   */
  public int getVersionMajor() {
    return mVersionMajor;
  }

  /**
   * Set the major version number.
   *
   * @param versionMajor New value.
   */
  public void setVersionMajor(final int versionMajor) {
    mVersionMajor = versionMajor;
  }

  /**
   * Get the minor version number.
   *
   * @return unsigned short
   */
  public int getVersionMinor() {
    return mVersionMinor;
  }

  /**
   * Set the minor version number.
   *
   * @param versionMinor New value.
   */
  public void setVersionMinor(final int versionMinor) {
    mVersionMinor = versionMinor;
  }

  /**
   * Get the GMT to local correction.
   *
   * @return unsigned short
   */
  public int getThiszone() {
    return mThiszone;
  }

  /**
   * Set the GMT to local correction.
   *
   * @param thiszone New value.
   */
  public void setThiszone(final int thiszone) {
    mThiszone = thiszone;
  }

  /**
   * Get the accuracy of timestamps.
   *
   * @return unsigned int
   */
  public long getSigfigs() {
    return mSigfigs;
  }

  /**
   * Set the accuracy of timestamps.
   *
   * @param sigfigs New value.
   */
  public void setSigfigs(final long sigfigs) {
    mSigfigs = sigfigs;
  }

  /**
   * Get the max length of captured packets, in octets.
   *
   * @return unsigned int
   */
  public long getSnapLength() {
    return mSnapLength;
  }

  /**
   * Set the max length of captured packets, in octets.
   *
   * @param snapLength New value.
   */
  public void setSnapLength(final long snapLength) {
    mSnapLength = snapLength;
  }

  /**
   * Get the data link type.
   *
   * @return unsigned int
   */
  public long getNetwork() {
    return mNetwork;
  }

  /**
   * Set the data link type.
   *
   * @param network New value.
   */
  public void setNetwork(final long network) {
    mNetwork = network;
  }

}
