package fr.ralala.netcap.net.layer.internet;

import android.graphics.Color;

import java.util.List;
import java.util.Locale;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.internet.utils.ICMPv6OptionLabel;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class ICMPv6 extends Layer {
  private int mHeaderLength;
  private int mType;
  private int mCode;
  private int mChecksum;
  private byte[] mOptionBytes;

  public ICMPv6() {
    super();
  }

  /**
   * Returns the header length.
   * @return int
   */
  @Override
  public int getHeaderLength() {
    return mHeaderLength;
  }

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("ICMPv6", "Internet Control Message Protocol v6");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    mBackground = Color.parseColor("#FCE0FF");
    mForeground = Color.parseColor("#000000");
    return ICMPv6OptionLabel.findByNumber(getType()).getText();
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(final List<String> lines) {
    final ICMPv6OptionLabel label = ICMPv6OptionLabel.findByNumber(getType());
    lines.add("  Type: " + label.getText() + " (" + getType() + ")");
    String c = label.getCode(getCode());
    if (c != null) lines.add("  Code: " + c + " (" + getCode() + ")");
    else lines.add("  Code: " + getCode());
    lines.add("  Checksum: 0x" + String.format(Locale.US, "%04x", getChecksum()));
    if (getOptionBytes() != null && getOptionBytes().length > 0) {
      lines.add("  Options: " + getOptionBytes().length + " bytes");
      final List<String> l = NetworkHelper.formatBuffer(getOptionBytes());
      for (final String s : l)
        lines.add("    " + s);
    }
  }

  /**
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    final byte[] temp2 = new byte[2];
    int offset = 0;

    mType = buffer[offset++];
    if (mType < 0) mType &= 0xFF;
    mCode = buffer[offset++];
    NetworkHelper.zcopy(buffer, offset, temp2, 0, temp2.length);
    mChecksum = NetworkHelper.ntohs2(temp2);
    offset += temp2.length;
    final int dif = Math.abs(offset - buffer.length);
    if (dif > 0) {
      mOptionBytes = new byte[dif];
      System.arraycopy(buffer, offset, mOptionBytes, 0, mOptionBytes.length);
      offset += mOptionBytes.length;
    } else
      mOptionBytes = new byte[0];
    mHeaderLength = offset;
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
   * @return the code
   */
  public int getCode() {
    return mCode;
  }

  /**
   * @param code the code to set
   */
  public void setCode(final int code) {
    mCode = code;
  }

  /**
   * @return the checksum
   */
  public int getChecksum() {
    return mChecksum;
  }

  /**
   * @param checksum the checksum to set
   */
  public void setChecksum(final int checksum) {
    mChecksum = checksum;
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
  public void setOptionBytes(final byte[] optionBytes) {
    mOptionBytes = optionBytes;
  }

}
