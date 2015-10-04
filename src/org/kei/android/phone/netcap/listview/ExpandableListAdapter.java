package org.kei.android.phone.netcap.listview;

import java.util.HashMap;
import java.util.List;
import org.kei.android.phone.netcap.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class ExpandableListAdapter extends BaseExpandableListAdapter {
  
  private Context context;
  private List<String> listDataHeader; // header titles
  // child data in format of header title, child title
  private HashMap<String, List<String>> listDataChild;

  public ExpandableListAdapter(Context context, List<String> listDataHeader,
          HashMap<String, List<String>> listChildData) {
      this.context = context;
      this.listDataHeader = listDataHeader;
      this.listDataChild = listChildData;
  }

  @Override
  public Object getChild(int groupPosition, int childPosititon) {
      return this.listDataChild.get(this.listDataHeader.get(groupPosition))
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
          LayoutInflater infalInflater = (LayoutInflater) this.context
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          convertView = infalInflater.inflate(R.layout.list_item_details, null);
      }

      TextView txtListChild = (TextView) convertView
              .findViewById(R.id.lblListItemTV);

      txtListChild.setText(childText);
      return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
      return this.listDataChild.get(this.listDataHeader.get(groupPosition))
              .size();
  }

  @Override
  public Object getGroup(int groupPosition) {
      return this.listDataHeader.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
      return this.listDataHeader.size();
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
          LayoutInflater infalInflater = (LayoutInflater) this.context
                  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          convertView = infalInflater.inflate(R.layout.list_group_details, null);
      }

      TextView lblListHeader = (TextView) convertView
              .findViewById(R.id.lblListHeaderTV);
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