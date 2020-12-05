package fr.ralala.netcap.ui.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class ListViewAdapter<T extends IListViewItem> extends BaseAdapter {

  private List<T> mItemList;
  private final List<T> mItemListBack;
  private final LayoutInflater mInflater;
  private final int mLid;
  private int mFilterId = 0;


  public ListViewAdapter(final Context ctx, final int lid) {
    mItemList = new ArrayList<T>();
    mItemListBack = new ArrayList<T>();
    mLid = lid;
    mInflater = LayoutInflater.from(ctx);
  }

  public void clear() {
    if (mItemList != null)
      mItemList.clear();
    if (mItemListBack != null)
      mItemListBack.clear();
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    if (mItemList != null)
      return mItemList.size();
    return 0;
  }

  @Override
  public T getItem(final int position) {
    if (mItemList != null)
      return mItemList.get(position);
    return null;
  }

  @Override
  public long getItemId(final int position) {
    if (mItemList != null)
      return mItemList.get(position).hashCode();
    return 0;
  }

  @Override
  public View getView(final int position, final View convertView,
                      final ViewGroup parent) {
    View layoutItem;
    if (convertView == null)
      layoutItem = mInflater.inflate(mLid, parent, false);
    else
      layoutItem = convertView;
    final T t = mItemList.get(position);
    t.updateItem(layoutItem, t);

    return layoutItem;

  }

  public List<T> getItemList() {
    return mItemList;
  }

  public void setFilterId(final int filterId) {
    mFilterId = filterId;
  }

  public int getFilterId() {
    return mFilterId;
  }

  public void removeItem(final T item) {
    mItemListBack.remove(item);
    mItemList.remove(item);
    notifyDataSetChanged();
  }

  public void addItem(final T item) {
    mItemListBack.add(item);
    mItemList.add(item);
    notifyDataSetChanged();
  }

  public void sort(Comparator<T> comp) {
    mItemList.sort(comp);
    mItemListBack.sort(comp);
    notifyDataSetChanged();
  }

  public void setItemList(final List<T> itemList) {
    mItemList = itemList;
    mItemListBack.clear();
    mItemListBack.addAll(itemList);
    notifyDataSetChanged();
  }

  // Filter Class
  public void filter(String charText) {
    charText = charText.toLowerCase(Locale.getDefault());
    mItemList.clear();
    if (charText.length() == 0) {
      mItemList.addAll(mItemListBack);
    } else {
      for (final T t : mItemListBack) {
        if (t.isFilterable(t, mFilterId, charText)) {
          mItemList.add(t);
        }
      }
    }
    notifyDataSetChanged();
  }
}