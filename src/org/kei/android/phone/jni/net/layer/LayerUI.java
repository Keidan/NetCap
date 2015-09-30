package org.kei.android.phone.jni.net.layer;

import android.graphics.Color;

/**
 *******************************************************************************
 * @file LayerUI.java
 * @author Keidan
 * @date 30/09/2015
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
public abstract class LayerUI {
  protected int background = Color.parseColor("#DAEEFF");
  protected int foreground = Color.parseColor("#000000");

  public abstract String getProtocolText();

  public abstract String getDescriptionText();

  public String compute(final String sep) {
    String preloadProto = getProtocolText();
    String preloadDesc = getDescriptionText();
    return foreground + sep + background + sep + preloadProto + sep + preloadDesc;
  }

  /**
   * @return the background
   */
  public int getBackground() {
    return background;
  }

  /**
   * @param background
   *          the background to set
   */
  public void setBackground(final int background) {
    this.background = background;
  }

  /**
   * @return the foreground
   */
  public int getForeground() {
    return foreground;
  }

  /**
   * @param foreground
   *          the foreground to set
   */
  public void setForeground(final int foreground) {
    this.foreground = foreground;
  }

}
