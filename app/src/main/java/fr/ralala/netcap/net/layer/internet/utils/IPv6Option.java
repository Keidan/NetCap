package fr.ralala.netcap.net.layer.internet.utils;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class IPv6Option {
  private int mType;
  private int mLength;
  private byte[] mMld;

  /**
   * @return the type
   */
  public int getType() {
    return mType;
  }

  /**
   * @param type the type to set
   */
  public void setType(int type) {
    mType = type;
  }

  /**
   * @return the length
   */
  public int getLength() {
    return mLength;
  }

  /**
   * @param length the length to set
   */
  public void setLength(int length) {
    mLength = length;
  }

  /**
   * @return the mld
   */
  public byte[] getMLD() {
    return mMld;
  }

  /**
   * @param mld the mld to set
   */
  public void setMLD(byte[] mld) {
    mMld = mld;
  }
}
