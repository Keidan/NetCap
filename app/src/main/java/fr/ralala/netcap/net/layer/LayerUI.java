package fr.ralala.netcap.net.layer;

import android.graphics.Color;

import java.util.List;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public abstract class LayerUI {
  protected int mBackground = Color.parseColor("#DAEEFF");
  protected int mForeground = Color.parseColor("#000000");

  public static class Protocol {
    public String shortName;
    public String fullName;

    public Protocol(String s, String f) {
      shortName = s;
      fullName = f;
    }
  }

  /**
   * Returns the description text.
   * @return String
   */
  public abstract String getDescriptionText();

  /**
   * Returns the protocol name (short+full).
   * @return Protocol
   */
  public abstract Protocol getProtocolUI();

  /**
   * Builds the protocol details.
   * @param lines Lines to be used by the function (output variable)
   */
  public abstract void buildDetails(List<String> lines);

  public String compute(final String sep) {
    String preloadProto = getProtocolUI().shortName;
    String preloadDesc = getDescriptionText();
    return mForeground + sep + mBackground + sep + preloadProto + sep + preloadDesc;
  }

  /**
   * Returns the background color.
   * @return int
   */
  public int getBackground() {
    return mBackground;
  }

  /**
   * Sets the background color.
   * @param background The new color.
   */
  public void setBackground(final int background) {
    mBackground = background;
  }

  /**
   * Returns the foreground color.
   * @return int
   */
  public int getForeground() {
    return mForeground;
  }

  /**
   * Sets the foreground color.
   * @param foreground The new color.
   */
  public void setForeground(final int foreground) {
    mForeground = foreground;
  }

}
