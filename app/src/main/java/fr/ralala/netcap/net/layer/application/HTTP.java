package fr.ralala.netcap.net.layer.application;

import android.graphics.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.transport.TCP;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class HTTP extends Layer {
  public static final int HTTP_PORT = 80;
  public static final int HTTP_ALT_PORT = 8080;

  private int mHeaderLength;
  private String mPage = null;
  private String mProtocol = null;
  private String mVersion = null;
  private String mMethod = null;
  private int mCode = 0;
  private String mDescription = null;
  private final Map<String, String> mHeaders;
  private Layer mOwner = null;
  private int mOffset = 0;
  private byte[] mBodyBytes;


  public HTTP() {
    mHeaders = new HashMap<>();
  }
  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  @Override
  public Protocol getProtocolUI() {
    return new Protocol("HTTP", "Hypertext Transfer Protocol");
  }

  /**
   * Returns the description text.
   * @return String
   */
  @Override
  public String getDescriptionText() {
    mBackground = Color.parseColor("#E4FFC7");
    mForeground = Color.BLACK;
    if (getPage() != null && getProtocol() != null && getVersion() != null && getMethod() != null)
      return getMethod() + " " + getPage() + " " + getProtocol() + "/" + getVersion();
    if (getCode() != 0 && getProtocol() != null && getVersion() != null && getDescription() != null)
      return getProtocol() + "/" + getVersion() + " " + getCode() + " " + getDescription();
    else if (mOwner != null)
      return mOwner.getDescriptionText();
    else
      return null;
  }

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  @Override
  public void buildDetails(List<String> lines) {
    if (getPage() != null && getProtocol() != null && getVersion() != null && getMethod() != null) {
      lines.add("  " + getMethod() + " " + getPage() + " " + getProtocol() + "/" + getVersion());
      lines.add("    Method: " + getMethod());
      lines.add("    URI: " + getPage());
      lines.add("    Version: " + getProtocol() + "/" + getVersion());
    } else if (getCode() != 0 && getProtocol() != null && getVersion() != null && getDescription() != null) {
      lines.add("  " + getProtocol() + "/" + getVersion() + " " + getCode() + " " + getDescription());
      lines.add("    Code: " + getCode());
      lines.add("    Description: " + getDescription());
      lines.add("    Version: " + getProtocol() + "/" + getVersion());
    }
    if (!mHeaders.isEmpty()) {
      lines.add("  Headers: " + mHeaders.size());
      for (String key : mHeaders.keySet())
        lines.add("    " + key + ": " + mHeaders.get(key));
    }
    byte[] body = getBodyBytes();
    if (body != null && body.length != 0) {
      lines.add("  Body: " + body.length + " bytes");
      List<String> l = NetworkHelper.formatBuffer(body);
      for (String s : l) lines.add("    " + s);
    }
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
   * Decodes the input buffer.
   * @param buffer Input buffer.
   * @param owner Top Layer.
   */
  @Override
  public void decodeLayer(final byte[] buffer, final Layer owner) {
    mOwner = owner;
    mOffset = 0;
    if (owner instanceof TCP && ((TCP) owner).isPSH()) {
      String l = readLine(buffer);
      if (l != null && !l.isEmpty()) {
        if (!isAsciiPrintable(l)) {
          mOffset -= l.length();
        } else {
          if (l.startsWith("HTTP/")) {
            int idx = l.indexOf(' ');
            if (idx != -1) {
              String[] split = l.substring(0, idx).split("/");
              mProtocol = split[0];
              mVersion = split[1];
              l = l.substring(idx + 1);
              idx = l.indexOf(' ');
              if (idx != -1) {
                mCode = Integer.parseInt(l.substring(0, idx));
                mDescription = l.substring(idx + 1);
              }
            }
          } else {
            int idx = l.indexOf(' ');
            if (idx != -1) {
              mMethod = l.substring(0, idx);
              l = l.substring(idx + 1);
              idx = l.lastIndexOf(' ');
              if (idx != -1) {
                mPage = l.substring(0, idx);
                String[] split = l.substring(idx + 1).split("/");
                mProtocol = split[0];
                mVersion = split[1];
              }
            }
          }
          while (true) {
            l = readLine(buffer);
            if (l == null || l.isEmpty()) break;
            int idx = l.indexOf(':');
            if (idx != -1)
              mHeaders.put(l.substring(0, idx).trim(), l.substring(idx + 1).trim());
          }
        }
      }
    }
    mHeaderLength = mOffset;
    int d = Math.abs(mOffset - buffer.length);
    if (d != 0) {
      mBodyBytes = new byte[d];
      System.arraycopy(buffer, mOffset, mBodyBytes, 0, mBodyBytes.length);
    } else
      mBodyBytes = new byte[0];
  }

  private String readLine(byte[] buffer) {
    StringBuilder s = new StringBuilder();
    while (mOffset < buffer.length) {
      if (buffer[mOffset] == 0x0d && buffer[mOffset + 1] == 0x0a) {
        mOffset += 2;
        break;
      }
      s.append((char) buffer[mOffset++]);
    }
    return s.toString();
  }

  private static boolean isAsciiPrintable(String str) {
    if (str == null)
      return false;
    int sz = str.length();
    for (int i = 0; i < sz; i++)
      if (!isAsciiPrintable(str.charAt(i)))
        return false;
    return true;
  }

  private static boolean isAsciiPrintable(char ch) {
    return ch >= 32 && ch < 127;
  }

  /**
   * @return the page
   */
  public String getPage() {
    return mPage;
  }

  /**
   * @param page the page to set
   */
  public void setPage(String page) {
    mPage = page;
  }

  /**
   * @return the protocol
   */
  public String getProtocol() {
    return mProtocol;
  }

  /**
   * @param protocol the protocol to set
   */
  public void setProtocol(String protocol) {
    mProtocol = protocol;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return mVersion;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    mVersion = version;
  }

  /**
   * @return the method
   */
  public String getMethod() {
    return mMethod;
  }

  /**
   * @param method the method to set
   */
  public void setMethod(String method) {
    mMethod = method;
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
  public void setCode(int code) {
    mCode = code;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return mDescription;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    mDescription = description;
  }

  /**
   * @return the headers
   */
  public Map<String, String> getHeaders() {
    return mHeaders;
  }

  /**
   * @return the bodyBytes
   */
  public byte[] getBodyBytes() {
    return mBodyBytes;
  }

}
