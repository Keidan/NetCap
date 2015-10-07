package org.kei.android.phone.net.layer.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kei.android.phone.net.NetworkHelper;
import org.kei.android.phone.net.layer.Layer;
import org.kei.android.phone.net.layer.transport.TCP;

import android.graphics.Color;

/**
 *******************************************************************************
 * @file HTTP.java
 * @author Keidan
 * @date 07/10/2015
 * @par Project NetCap
 *
 * @par Copyright 2015 Keidan, all right reserved
 *
 *      This software is distributed in the hope that it will be useful, but
 *      WITHOUT ANY WARRANTY.
 *
 *      License summary : You can modify and redistribute the sources code and
 *      binaries. You can send me the bug-fix
 *
 *      Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class HTTP extends Layer {
  public static final int HTTP_PORT = 80;
  public static final int HTTP_ALT_PORT = 8080;
  
  private int             headerLength;
  private String page = null;
  private String protocol = null;
  private String version = null;
  private String method = null;
  private int code = 0;
  private String description = null;
  private Map<String, String> headers = null;
  private Layer owner = null;
  private int offset = 0;
  private byte[]          bodyBytes;
  
  
  public HTTP() {
    headers = new HashMap<String, String>();
  }
  
  @Override
  public String getFullName() {
    return "Hypertext Transfer Protocol";
  }

  @Override
  public String getProtocolText() {
    return "HTTP";
  }

  @Override
  public String getDescriptionText() {
    background = Color.parseColor("#E4FFC7");
    foreground = Color.BLACK;
    if(getPage() != null && getProtocol() != null && getVersion() != null && getMethod() != null)
      return getMethod() + " " + getPage() + " " + getProtocol() + "/" + getVersion();
    if(getCode() != 0 && getProtocol() != null && getVersion() != null && getDescription() != null)
      return getProtocol() + "/" + getVersion() + " " + getCode() + " " + getDescription();
    else if(owner != null)
      return owner.getDescriptionText();
    else
      return null;
  }
  
  @Override
  public void buildDetails(List<String> lines) {
    if(getPage() != null && getProtocol() != null && getVersion() != null && getMethod() != null) {
      lines.add("  " + getMethod() + " " + getPage() + " " + getProtocol() + "/" + getVersion());
      lines.add("    Method: " + getMethod());
      lines.add("    URI: " + getPage());
      lines.add("    Version: " + getProtocol() + "/" + getVersion());
    } else if(getCode() != 0 && getProtocol() != null && getVersion() != null && getDescription() != null) {
      lines.add("  " + getProtocol() + "/" + getVersion() + " " + getCode() + " " + getDescription());
      lines.add("    Code: " + getCode());
      lines.add("    Description: " + getDescription());
      lines.add("    Version: " + getProtocol() + "/" + getVersion());
    }
    if(!headers.isEmpty()) {
      lines.add("  Headers: " + headers.size());
      for(String key: headers.keySet())
        lines.add("    " + key + ": " + headers.get(key));
    }
    byte [] body = getBodyBytes();
    if(body != null && body.length != 0) {
      lines.add("  Body: " + body.length + " bytes");
      List<String> l = NetworkHelper.formatBuffer(body);
      for(String s : l) lines.add("    " + s);
    }
  }
  
  @Override
  public int getHeaderLength() {
    return headerLength;
  }
  
  @Override
  public void decodeLayer(final byte [] buffer, final Layer owner) {
    this.owner = owner;
    offset = 0;
    if(TCP.class.isInstance(owner) && ((TCP)owner).isPSH()) {
      String l = readLine(buffer);
      if(l != null && !l.isEmpty()) {
        if(!isAsciiPrintable(l)) {
          offset -= l.length();
        } else {
          if(l.startsWith("HTTP/")) {
            int idx = l.indexOf(' ');
            if(idx != -1) {
              String [] split = l.substring(0, idx).split("/");
              protocol = split[0];
              version = split[1];
              l = l.substring(idx + 1);
              idx = l.indexOf(' ');
              if(idx != -1) {
                code = Integer.parseInt(l.substring(0, idx));
                description = l.substring(idx + 1);
              }
            }
          } else {
            int idx = l.indexOf(' ');
            if(idx != -1) {
              method = l.substring(0, idx);
              l = l.substring(idx + 1);
              idx = l.lastIndexOf(' ');
              if(idx != -1) {
                page = l.substring(0, idx);
                String [] split = l.substring(idx + 1).split("/");
                protocol = split[0];
                version = split[1];
              }
            }
          }
          while(true) {
            l = readLine(buffer);
            if(l == null  || l.isEmpty()) break;
            int idx = l.indexOf(':');
            if(idx != -1)
              headers.put(l.substring(0, idx).trim(), l.substring(idx + 1).trim());
          }
        }
      }
    }
    headerLength = offset;
    int d = Math.abs(offset - buffer.length);
    if (d != 0) {
      bodyBytes = new byte[d];
      System.arraycopy(buffer, offset, bodyBytes, 0, bodyBytes.length);
    } else
      bodyBytes = new byte[0];
  }
  
  private String readLine(byte [] buffer) {
    String s = "";
    while(offset < buffer.length) {
      if(buffer[offset] == 0x0d && buffer[offset+1] == 0x0a) {
        offset+= 2;
        break;
      }
      s += (char)buffer[offset++];
    }
    return s;
  }
  private static boolean isAsciiPrintable(String str) {
    if (str == null)
      return false;
    int sz = str.length();
    for (int i = 0; i < sz; i++)
      if (isAsciiPrintable(str.charAt(i)) == false)
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
    return page;
  }

  /**
   * @param page the page to set
   */
  public void setPage(String page) {
    this.page = page;
  }

  /**
   * @return the protocol
   */
  public String getProtocol() {
    return protocol;
  }

  /**
   * @param protocol the protocol to set
   */
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @return the method
   */
  public String getMethod() {
    return method;
  }

  /**
   * @param method the method to set
   */
  public void setMethod(String method) {
    this.method = method;
  }

  /**
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * @param code the code to set
   */
  public void setCode(int code) {
    this.code = code;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the headers
   */
  public Map<String, String> getHeaders() {
    return headers;
  }

  /**
   * @return the bodyBytes
   */
  public byte[] getBodyBytes() {
    return bodyBytes;
  }

}
