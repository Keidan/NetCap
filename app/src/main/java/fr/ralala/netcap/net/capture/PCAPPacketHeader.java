package fr.ralala.netcap.net.capture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class PCAPPacketHeader {
  public static final SimpleDateFormat SDF = new SimpleDateFormat(
      "yyyy.MM.dd HH:mm:ss", Locale.US);
  private long mTsSec = 0;
  private long mTsUsec = 0;
  private long mInclLen = 0;
  private long mOrigLen = 0;

  public String getTime() {
    return SDF.format(new Date(mTsSec * 1000)) + "." + mTsUsec;
  }

  /**
   * Get the timestamp seconds.
   *
   * @return unsigned int
   */
  public long getTsSec() {
    return mTsSec;
  }

  /**
   * Set the timestamp seconds.
   *
   * @param tsSec New value.
   */
  public void setTsSec(long tsSec) {
    mTsSec = tsSec;
  }

  /**
   * Get the timestamp microseconds.
   *
   * @return unsigned int
   */
  public long getTsUsec() {
    return mTsUsec;
  }

  /**
   * Set the timestamp microseconds.
   *
   * @param tsUsec New value.
   */
  public void setTsUsec(long tsUsec) {
    mTsUsec = tsUsec;
  }

  /**
   * Get the number of octets of packet saved in file.
   *
   * @return unsigned int
   */
  public long getInclLen() {
    return mInclLen;
  }

  /**
   * Set the number of octets of packet saved in file.
   *
   * @param inclLen New value.
   */
  public void setInclLen(long inclLen) {
    mInclLen = inclLen;
  }

  /**
   * Get the actual length of packet.
   *
   * @return unsigned int
   */
  public long getOrigLen() {
    return mOrigLen;
  }

  /**
   * Set the actual length of packet.
   *
   * @param origLen New value.
   */
  public void setOrigLen(long origLen) {
    mOrigLen = origLen;
  }


}
