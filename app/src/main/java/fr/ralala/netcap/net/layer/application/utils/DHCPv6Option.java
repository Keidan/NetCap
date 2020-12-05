package fr.ralala.netcap.net.layer.application.utils;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class DHCPv6Option {
  private int mCode;
  private int mLength;
  private byte[] mData;
  private String mSdata;

  /**
   * @return the code
   */
  public int getCode() {
    return mCode;
  }

  /**
   * @param code the code to set
   */
  public void setCode(int code) {
    mCode = code;
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
   * @return the data
   */
  public byte[] getData() {
    return mData;
  }

  /**
   * @param data the data to set
   */
  public void setData(byte[] data) {
    mData = data;
  }

  /**
   * @return the data
   */
  public String getSData() {
    return mSdata;
  }

  /**
   * @param sdata the data to set
   */
  public void setSData(String sdata) {
    mSdata = sdata;
  }

}
