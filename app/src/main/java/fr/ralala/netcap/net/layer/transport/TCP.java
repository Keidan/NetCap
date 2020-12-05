package fr.ralala.netcap.net.layer.transport;

import android.graphics.Color;

import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.Service;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;
import fr.ralala.netcap.net.layer.application.DNS;
import fr.ralala.netcap.net.layer.application.HTTP;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class TCP extends Layer {
  public static final int FLAG_CWR = 0x0080;
  public static final int FLAG_ECE = 0x0040;
  public static final int FLAG_URG = 0x0020;
  public static final int FLAG_ACK = 0x0010;
  public static final int FLAG_PSH = 0x0008;
  public static final int FLAG_RST = 0x0004;
  public static final int FLAG_SYN = 0x0002;
  public static final int FLAG_FIN = 0x0001;
  private int mSource;
  private int mDestination;
  private boolean mCwr = false;
  private boolean mEce = false;
  private boolean mUrg = false;
  private boolean mAck = false;
  private boolean mPsh = false;
  private boolean mRst = false;
  private boolean mSyn = false;
  private boolean mFin = false;
  private int mSeq;
  private int mAckSeq;
  private int mWindow;
  private int mCheck;
  private int mUrgPtr;
  private int mDoff;
  private int mReserved;
  private int mFlags;
  private byte[] mOptionBytes;

  public TCP() {
    super();
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("TCP", "Transmission Control Protocol (Src: " + mSource + ", Dst: " + mDestination + ")");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    final int defbg = mBackground;
    String desc = "";
    Service srv = Service.findByPort(getSource());
    if (srv == Service.NOT_FOUND)
      desc += getSource();
    else {
      desc += srv.getName() + "(" + srv.getPort() + ")";
      if (srv.getName().toLowerCase(Locale.US).contains("https")) {
        mBackground = Color.parseColor("#A40000");
        mForeground = Color.parseColor("#FFFC9C");
      }
    }
    desc += " > ";
    srv = Service.findByPort(getDestination());
    if (srv == Service.NOT_FOUND)
      desc += getDestination();
    else {
      desc += srv.getName() + "(" + srv.getPort() + ")";
      if (srv.getName().toLowerCase(Locale.US).contains("https")) {
        mBackground = Color.parseColor("#A40000");
        mForeground = Color.parseColor("#FFFC9C");
      }
    }
    desc += " [";
    if (mBackground == defbg) {
      mBackground = Color.parseColor("#E7E6FF");
      mForeground = Color.BLACK;
    }
    if (isSYN()) desc += "SYN, ";
    if (isPSH()) desc += "PSH, ";
    if (isACK()) desc += "ACK, ";
    if (isCWR()) desc += "CWR, ";
    if (isECE()) desc += "ECE, ";
    if (isRST()) desc += "RST, ";
    if (isURG()) desc += "URG, ";
    if (isFIN()) desc += "FIN, ";
    if (desc.endsWith(", "))
      desc = desc.substring(0, desc.length() - 2);
    desc += "]";
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
    lines.add("  Sequence number: " + getSeq());
    lines.add("  Acknowledgement number: " + getAckSeq());
    lines.add("  Header Length: " + getHeaderLength() + " bytes");
    lines.add("  Flags:");
    lines.add("    " + (isCWR() ? "1" : "0") + "... .... = Congestion Window Reduced: " + (isCWR() ? "Set" : "Not Set"));
    lines.add("    ." + (isECE() ? "1" : "0") + ".. .... = ECN-Echo: " + (isECE() ? "Set" : "Not Set"));
    lines.add("    .." + (isURG() ? "1" : "0") + ". .... = Urgent: " + (isURG() ? "Set" : "Not Set"));
    lines.add("    ..." + (isACK() ? "1" : "0") + " .... = Acknowledgement: " + (isACK() ? "Set" : "Not Set"));
    lines.add("    .... " + (isPSH() ? "1" : "0") + "... = Push: " + (isPSH() ? "Set" : "Not Set"));
    lines.add("    .... ." + (isRST() ? "1" : "0") + ".. = Reset: " + (isRST() ? "Set" : "Not Set"));
    lines.add("    .... .." + (isSYN() ? "1" : "0") + ". = Syn: " + (isSYN() ? "Set" : "Not Set"));
    lines.add("    .... ..." + (isFIN() ? "1" : "0") + " = Fin: " + (isFIN() ? "Set" : "Not Set"));
    lines.add("  Window size: " + getWindow());
    lines.add("  Checksum: 0x" + String.format(Locale.US, "%04x", getCheck()));
    lines.add("  Urg ptr: " + getUrgPtr());
    if (getOptionBytes() != null && getOptionBytes().length > 0) {
      lines.add("  Options: " + getOptionBytes().length + " bytes");
      List<String> l = NetworkHelper.formatBuffer(getOptionBytes());
      for (String s : l) lines.add("    " + s);
    }
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return mDoff * 4;
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
    byte[] temp4 = new byte[4];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mSource = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mDestination = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    mSeq = NetworkHelper.ntohl(temp4);
    offset += temp4.length;
    NetworkHelper.zcopy(buffer, offset, temp4, 0, temp4.length);
    mAckSeq = NetworkHelper.ntohl(temp4);
    offset += temp4.length;

    byte doff_reserved = buffer[offset++];
    mDoff = Math.abs((byte) (doff_reserved >> 4));
    mReserved = (byte) (doff_reserved & 0x0f);
    int flags = buffer[offset++] & 0x3F;

    mCwr = (flags & FLAG_CWR) != 0;
    mEce = (flags & FLAG_ECE) != 0;
    mUrg = (flags & FLAG_URG) != 0;
    mAck = (flags & FLAG_ACK) != 0;
    mPsh = (flags & FLAG_PSH) != 0;
    mRst = (flags & FLAG_RST) != 0;
    mSyn = (flags & FLAG_SYN) != 0;
    mFin = (flags & FLAG_FIN) != 0;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mWindow = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mCheck = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mUrgPtr = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;

    if (mDoff > 5) {
      mOptionBytes = new byte[(mDoff - 5) * 4];
      System.arraycopy(buffer, offset, mOptionBytes, 0, mOptionBytes.length);
    } else
      mOptionBytes = new byte[0];

    byte[] sub_buffer = resizeBuffer(buffer);
    if (sub_buffer != null) {
      if (mSource == DNS.PORT || mDestination == DNS.PORT) {
        DNS dns = new DNS();
        dns.decodeLayer(sub_buffer, this);
        setNext(dns);
      } else if (mSource == HTTP.HTTP_PORT || mSource == HTTP.HTTP_ALT_PORT || mDestination == HTTP.HTTP_PORT || mDestination == HTTP.HTTP_ALT_PORT) {
        HTTP http = new HTTP();
        http.decodeLayer(sub_buffer, this);
        setNext(http);
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
   * @return the cwr
   */
  public boolean isCWR() {
    return mCwr;
  }

  /**
   * @param cwr the cwr to set
   */
  public void setCWR(final boolean cwr) {
    mCwr = cwr;
  }

  /**
   * @return the ece
   */
  public boolean isECE() {
    return mEce;
  }

  /**
   * @param ece the ece to set
   */
  public void setECE(final boolean ece) {
    mEce = ece;
  }

  /**
   * @return the urg
   */
  public boolean isURG() {
    return mUrg;
  }

  /**
   * @param urg the urg to set
   */
  public void setURG(final boolean urg) {
    mUrg = urg;
  }

  /**
   * @return the ack
   */
  public boolean isACK() {
    return mAck;
  }

  /**
   * @param ack the ack to set
   */
  public void setACK(final boolean ack) {
    mAck = ack;
  }

  /**
   * @return the psh
   */
  public boolean isPSH() {
    return mPsh;
  }

  /**
   * @param psh the psh to set
   */
  public void setPSH(final boolean psh) {
    mPsh = psh;
  }

  /**
   * @return the rst
   */
  public boolean isRST() {
    return mRst;
  }

  /**
   * @param rst the rst to set
   */
  public void setRST(final boolean rst) {
    mRst = rst;
  }

  /**
   * @return the syn
   */
  public boolean isSYN() {
    return mSyn;
  }

  /**
   * @param syn the syn to set
   */
  public void setSYN(final boolean syn) {
    mSyn = syn;
  }

  /**
   * @return the fin
   */
  public boolean isFIN() {
    return mFin;
  }

  /**
   * @param fin the fin to set
   */
  public void setFIN(final boolean fin) {
    mFin = fin;
  }

  /**
   * @return the seq
   */
  public int getSeq() {
    return mSeq;
  }

  /**
   * @param seq the seq to set
   */
  public void setSeq(final int seq) {
    mSeq = seq;
  }

  /**
   * @return the ackSeq
   */
  public int getAckSeq() {
    return mAckSeq;
  }

  /**
   * @param ackSeq the ackSeq to set
   */
  public void setAckSeq(final int ackSeq) {
    mAckSeq = ackSeq;
  }

  /**
   * @return the window
   */
  public int getWindow() {
    return mWindow;
  }

  /**
   * @param window the window to set
   */
  public void setWindow(final int window) {
    mWindow = window;
  }

  /**
   * @return the check
   */
  public int getCheck() {
    return mCheck;
  }

  /**
   * @param check the check to set
   */
  public void setCheck(final int check) {
    mCheck = check;
  }

  /**
   * @return the urgPtr
   */
  public int getUrgPtr() {
    return mUrgPtr;
  }

  /**
   * @param urgPtr the urgPtr to set
   */
  public void setUrgPtr(final int urgPtr) {
    mUrgPtr = urgPtr;
  }

  /**
   * @return the doff
   */
  public int getDOFF() {
    return mDoff;
  }

  /**
   * @param doff the doff to set
   */
  public void setDOFF(int doff) {
    mDoff = doff;
  }

  /**
   * @return the reserved
   */
  public int getReserved() {
    return mReserved;
  }

  /**
   * @param reserved the reserved to set
   */
  public void setReserved(int reserved) {
    mReserved = reserved;
  }

  /**
   * @return the flags
   */
  public int getFlags() {
    return mFlags;
  }

  /**
   * @param flags the flags to set
   */
  public void setFlags(int flags) {
    mFlags = flags;
  }

  /**
   * @return the optionBytes
   */
  public byte[] getOptionBytes() {
    return mOptionBytes;
  }

  /**
   * @param optionBytes the optionBytes to set
   */
  public void setOptionBytes(byte[] optionBytes) {
    mOptionBytes = optionBytes;
  }

}
