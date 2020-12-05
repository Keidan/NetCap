package fr.ralala.netcap.net.layer;

import fr.ralala.netcap.net.NetworkHelper;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public abstract class Layer extends LayerUI {
  private Layer mNext = null;

  /**
   * Allocate the object.
   */
  public Layer() {
  }

  /**
   * Returns the header length.
   * @return int
   */
  public abstract int getHeaderLength();

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  public abstract void decodeLayer(final byte[] buffer, Layer owner);

  /**
   * Resizes the input buffer.
   * @param buffer The buffer to resize.
   * @return byte[]
   */
  protected byte[] resizeBuffer(byte[] buffer) {
    if (buffer.length - getHeaderLength() > 0) {
      byte[] sub_buffer = new byte[buffer.length - getHeaderLength()];
      NetworkHelper.zcopy(buffer, getHeaderLength(), sub_buffer, 0, sub_buffer.length);
      return sub_buffer;
    }
    return null;
  }

  /**
   * Returns the next layer.
   * @return Layer
   */
  public Layer getNext() {
    return mNext;
  }

  /**
   * Sets the next layer
   * @param next The next Layer.
   */
  public void setNext(final Layer next) {
    mNext = next;
  }

}
