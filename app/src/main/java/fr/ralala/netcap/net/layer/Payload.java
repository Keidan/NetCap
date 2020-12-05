package fr.ralala.netcap.net.layer;

import java.util.List;

import fr.ralala.netcap.net.NetworkHelper;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class Payload extends Layer {
  private byte[] mData = null;

  public Payload() {
    super();
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return 0;
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    byte[] buffer = getData();
    List<String> l = NetworkHelper.formatBuffer(buffer);
    lines.addAll(l);
  }

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    mData = new byte[buffer.length];
    System.arraycopy(buffer, 0, mData, 0, buffer.length);
  }

  /**
   * Gets the data buffer.
   * @return byte []
   */
  public byte[] getData() {
    return mData;
  }

  /**
   * Sets the data buffer.
   * @param data The data buffer.
   */
  public void setData(final byte[] data) {
    mData = data;
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("PL", "Payload");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    return "data length: " + getData().length;
  }
}
