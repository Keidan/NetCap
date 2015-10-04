package org.kei.android.phone.netcap.listview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 *******************************************************************************
 * @file ListViewAdapter.java
 * @author Keidan
 * @date 20/09/2015
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
public class ListViewAdapter<T extends IListViewItem> extends
    BaseAdapter {
  
  private List<T>              itemList;
  private List<T>              itemListBack;
  private final LayoutInflater inflater;
  private int                  lid = 0;
  private int                  filterId = 0;
  
  
  public ListViewAdapter(final Context ctx, final int lid) {
    this.itemList = new ArrayList<T>();
    this.itemListBack = new ArrayList<T>();
    this.lid = lid;
    inflater = LayoutInflater.from(ctx);
  }
  
  public void clear() {
    if (itemList != null)
      itemList.clear();
    if (itemListBack != null)
      itemListBack.clear();
    notifyDataSetChanged();
  }
  
  @Override
  public int getCount() {
    if (itemList != null)
      return itemList.size();
    return 0;
  }
  
  @Override
  public T getItem(final int position) {
    if (itemList != null)
      return itemList.get(position);
    return null;
  }
  
  @Override
  public long getItemId(final int position) {
    if (itemList != null)
      return itemList.get(position).hashCode();
    return 0;
  }
  
  @Override
  public View getView(final int position, final View convertView,
      final ViewGroup parent) {
    
    LinearLayout layoutItem;
    if (convertView == null) {
      layoutItem = (LinearLayout) inflater.inflate(lid, parent, false);
    } else {
      layoutItem = (LinearLayout) convertView;
    }
    final T t = itemList.get(position);
    t.updateItem(layoutItem, t);
    
    return layoutItem;
    
  }
  
  public List<T> getItemList() {
    return itemList;
  }
  
  public void setFilterId(final int filterId) {
    this.filterId = filterId;
  }
  
  public int getFilterId() {
    return filterId;
  }
  
  public void removeItemSort(final T item, Comparator<T> comp) {
    itemListBack.remove(item);
    itemList.remove(item);
    Collections.sort(itemList, comp);
    Collections.sort(itemListBack, comp);
    notifyDataSetChanged();
  }
  
  public void addItemSort(final T item, Comparator<T> comp) {
    itemListBack.add(item);
    itemList.add(item);
    Collections.sort(itemList, comp);
    Collections.sort(itemListBack, comp);
    notifyDataSetChanged();
  }
  
  public void setItemList(final List<T> itemList) {
    this.itemList = itemList;
    itemListBack.clear();
    itemListBack.addAll(itemList);
    notifyDataSetChanged();
  }

  // Filter Class
  public void filter(String charText) {
    charText = charText.toLowerCase(Locale.getDefault());
    itemList.clear();
    if (charText.length() == 0) {
      itemList.addAll(itemListBack);
    } else {
      for (final T t : itemListBack) {
        if (t.isFilterable(t, filterId, charText)) {
          itemList.add(t);
        }
      }
    }
    notifyDataSetChanged();
  }
}