package fr.ralala.netcap.net.layer.application.utils;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class DNSEntry {
  private String mName = null;
  private int mType = 0;
  private int mClazz = 0;
  private int mTtl = 0;
  private int mDataLength = 0;
  private String mAddress = null;

  public DNSEntry() {

  }

  public String getTypeString() {
    switch (mType) {
      case 1:
        return "A";
      case 2:
        return "NS";
      case 5:
        return "CNAME";
      case 6:
        return "SOA";
      case 12:
        return "PTR";
      case 15:
        return "MX";
    }
    return "UNKNOWN";
  }

  public String getClazzString() {
    if (mClazz == 0x0000) return "AR";
    else if (mClazz == 0x0001) return "IN";
    else if (mClazz == 0x0002) return "AV";
    else if (mClazz == 0x0003) return "CH";
    else if (mClazz == 0x0004) return "HS";
    else if (mClazz >= 0x0005 && mClazz <= 0x007F) return "Data CLASSes only";
    else if (mClazz >= 0x0080 && mClazz <= 0x00FD) return "QCLASSes only";
    else if (mClazz == 0x00FE) return "QCLASS None";
    else if (mClazz == 0x00FF) return "QCLASS Any";
    else if (mClazz >= 0x0100 && mClazz <= 0x7FFF) return "Assigned by IETF";
    else if (mClazz >= 0x8000 && mClazz <= 0xFEFF) return "Assigned based on Specification";
    else if (mClazz >= 0xFF00 && mClazz <= 0xFFFE) return "Private Use";
    else if (mClazz == 0xFFFF) return "Can only be assigned by an IETF";
    return "UNKNOWN";
  }


  /**
   * @return the name
   */
  public String getName() {
    return mName;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    mName = name;
  }

  /**
   * @return the type
   */
  public int getType() {
    return mType;
  }

  /**
   * @param type the type to set
   */
  public void setType(final int type) {
    mType = type;
  }

  /**
   * @return the clazz
   */
  public int getClazz() {
    return mClazz;
  }

  /**
   * @param clazz the clazz to set
   */
  public void setClazz(final int clazz) {
    mClazz = clazz;
  }

  /**
   * @return the ttl
   */
  public int getTTL() {
    return mTtl;
  }

  /**
   * @param ttl the ttl to set
   */
  public void setTTL(final int ttl) {
    mTtl = ttl;
  }

  /**
   * @return the dataLength
   */
  public int getDataLength() {
    return mDataLength;
  }

  /**
   * @param dataLength the dataLength to set
   */
  public void setDataLength(final int dataLength) {
    mDataLength = dataLength;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return mAddress;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(final String address) {
    mAddress = address;
  }

}
