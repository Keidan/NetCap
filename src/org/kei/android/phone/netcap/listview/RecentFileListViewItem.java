package org.kei.android.phone.netcap.listview;

import org.kei.android.phone.netcap.R;

import android.widget.LinearLayout;
import android.widget.TextView;

public class RecentFileListViewItem implements IListViewItem {

  private long   time = 0L;
  private String file = null;
  private String key = null;
  
  @Override
  public void updateItem(final LinearLayout layoutItem, final Object object) {
    final RecentFileListViewItem me = (RecentFileListViewItem) object;
    final TextView text1 = (TextView) layoutItem.findViewById(R.id.label1);
    text1.setTextSize(15);
    text1.setText(me.getFile());
  }
  
  @Override
  public boolean isFilterable(final Object o, final int filterId,
      final String text) {
    return true;
  }
  
  /**
   * @return the time
   */
  public long getTime() {
    return time;
  }
  
  /**
   * @param time
   *          the time to set
   */
  public void setTime(final long time) {
    this.time = time;
  }
  
  /**
   * @return the file
   */
  public String getFile() {
    return file;
  }
  
  /**
   * @param file
   *          the file to set
   */
  public void setFile(final String file) {
    this.file = file;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

}
