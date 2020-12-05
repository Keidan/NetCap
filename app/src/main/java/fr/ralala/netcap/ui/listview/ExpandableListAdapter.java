package fr.ralala.netcap.ui.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import fr.ralala.netcap.R;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

  private final Context mContext;
  private final List<String> mListDataHeader; // header titles
  // child data in format of header title, child title
  private final HashMap<String, List<String>> mListDataChild;

  public ExpandableListAdapter(Context context, List<String> listDataHeader,
                               HashMap<String, List<String>> listChildData) {
    mContext = context;
    mListDataHeader = listDataHeader;
    mListDataChild = listChildData;
  }

  @Override
  public Object getChild(int groupPosition, int childPosititon) {
    return mListDataChild.get(mListDataHeader.get(groupPosition))
        .get(childPosititon);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public View getChildView(int groupPosition, final int childPosition,
                           boolean isLastChild, View convertView, ViewGroup parent) {

    final String childText = (String) getChild(groupPosition, childPosition);

    if (convertView == null) {
      LayoutInflater infalInflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final ViewGroup root = null;
      convertView = infalInflater.inflate(R.layout.list_item_details, root);
    }

    TextView txtListChild = (TextView) convertView
        .findViewById(R.id.tvLblListItem);

    txtListChild.setText(childText);
    return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return mListDataChild.get(mListDataHeader.get(groupPosition)).size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return mListDataHeader.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return mListDataHeader.size();
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded,
                           View convertView, ViewGroup parent) {
    String headerTitle = (String) getGroup(groupPosition);
    if (convertView == null) {
      LayoutInflater infalInflater = (LayoutInflater) mContext
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final ViewGroup root = null;
      convertView = infalInflater.inflate(R.layout.list_group_details, root);
    }

    TextView lblListHeader = (TextView) convertView
        .findViewById(R.id.tvLblListHeader);
    lblListHeader.setText(headerTitle);

    return convertView;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }
}