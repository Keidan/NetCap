package fr.ralala.netcap.ui.listview;

import android.view.View;


/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public interface IListViewItem {
  void updateItem(final View layoutItem, final Object object);

  boolean isFilterable(final Object o, final int filterId, final String text);
}
