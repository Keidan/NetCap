package fr.ralala.netcap.ui.listview;

import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import fr.ralala.netcap.R;
import fr.ralala.netcap.net.capture.PCAPPacketHeader;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class RecentFileListViewItem implements IListViewItem {

  private static final int SIZE_1KB = 0x400;
  private static final int SIZE_1MB = 0x100000;
  private static final int SIZE_1GB = 0x40000000;
  private long mTime;
  private String mFile;
  private final String mFilename;
  private final String mKey;

  public RecentFileListViewItem(final String key, final String data) {
    this.mKey = key;
    String[] split = data.split(Pattern.quote("|"));
    this.mTime = Long.parseLong(split[0]);
    this.mFile = split[1];
    this.mFilename = new File(this.mFile).getName();
  }

  @Override
  public void updateItem(final View layoutItem, final Object object) {
    final RecentFileListViewItem me = (RecentFileListViewItem) object;
    final TextView text1 = layoutItem.findViewById(R.id.firstLine);
    final TextView text2 = layoutItem.findViewById(R.id.secondLine);
    // text1.setTextSize(15);
    final File f = new File(me.getFile());
    text1.setText(f.getName());
    final String last = PCAPPacketHeader.SDF.format(new Date(me.getTime()));
    text2.setText((convertToHuman(f.length()) + " - Last: " + last));
  }

  @Override
  public boolean isFilterable(final Object o, final int filterId, final String text) {
    return true;
  }

  /**
   * @return the time
   */
  public long getTime() {
    return mTime;
  }

  /**
   * @param time the time to set
   */
  public void setTime(final long time) {
    mTime = time;
  }

  /**
   * @return the file
   */
  public String getFile() {
    return mFile;
  }

  /**
   * @param file the file to set
   */
  public void setFile(final String file) {
    mFile = file;
  }

  /**
   * @return the fielname
   */
  public String getFilename() {
    return mFilename;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return mKey;
  }

  private static String convertToHuman(long l) {
    String sf;
    if (l < 0)
      sf = "0";
    else if (l < SIZE_1KB)
      sf = String.format(Locale.US, "%do", (int) l);
    else if (l < SIZE_1MB)
      sf = String.format(Locale.US, "%d Ko", (int) (l / SIZE_1KB));
    else if (l < SIZE_1GB)
      sf = String.format(Locale.US, "%d Mo", (int) (l / SIZE_1MB));
    else
      sf = String.format(Locale.US, "%d Go", (int) (l / SIZE_1GB));
    return sf;
  }
}
