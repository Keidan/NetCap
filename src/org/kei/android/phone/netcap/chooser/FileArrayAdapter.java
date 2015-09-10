package org.kei.android.phone.netcap.chooser;

import java.util.List;

import org.kei.android.phone.netcap.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file FileArrayAdapter.java
 * @author Keidan
 * @date 10/09/2015
 * @par Project
 * NetCap
 *
 * @par Copyright
 * Copyright 2011-2013 Keidan, all right reserved
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY.
 *
 * License summary : 
 *    You can modify and redistribute the sources code and binaries.
 *    You can send me the bug-fix
 *
 * Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class FileArrayAdapter extends ArrayAdapter<Option> {

  private final Context      c;
  private final int          id;
  private final List<Option> items;

  public FileArrayAdapter(final Context context, final int textViewResourceId,
      final List<Option> objects) {
    super(context, textViewResourceId, objects);
    c = context;
    id = textViewResourceId;
    items = objects;
  }

  @Override
  public Option getItem(final int i) {
    return items.get(i);
  }

  @Override
  public View getView(final int position, final View convertView,
      final ViewGroup parent) {
    View v = convertView;
    if (v == null) {
      final LayoutInflater vi = (LayoutInflater) c
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(id, null);
    }
    final Option o = items.get(position);
    if (o != null) {
      final ImageView i1 = (ImageView) v.findViewById(R.id.icon);
      final TextView t1 = (TextView) v.findViewById(R.id.name);
      final TextView t2 = (TextView) v.findViewById(R.id.data);
      if (i1 != null)
        i1.setImageDrawable(o.getIcon());
      if (t1 != null)
        t1.setText(o.getName());
      if (t2 != null)
        t2.setText(o.getData());

    }
    return v;
  }

}