package org.kei.android.phone.netcap.listview;

import java.util.ArrayList;
import java.util.List;

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
  private final LayoutInflater inflater;
  private int                  lid = 0;
  
  public ListViewAdapter(final Context ctx, final int lid) {
    this.itemList = new ArrayList<T>();
    this.lid = lid;
    inflater = LayoutInflater.from(ctx);
  }
  
  public void clear() {
    if (itemList != null)
      itemList.clear();
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
  
  public void setItemList(final List<T> itemList) {
    this.itemList = itemList;
    notifyDataSetChanged();
  }
  
}