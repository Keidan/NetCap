package fr.ralala.netcap.ui.chooser;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import fr.ralala.netcap.R;

/**
 *******************************************************************************
 * <p><b>Project NetCap</b><br/>
 * Listview adapter
 * </p>
 * @author Keidan
 *
 *******************************************************************************
 */
public class FileChooserArrayAdapter extends ArrayAdapter<FileChooserItem> {

  private final Context mContext;
  private final int mId;
  private final List<FileChooserItem> mItems;

  private static class ViewHolder {
    ImageView icon;
    TextView name;
    TextView data;
  }

  /**
   * Creates the array adapter.
   * @param context The Android context.
   * @param textViewResourceId The resource id of the container.
   * @param objects The objects list.
   */
  public FileChooserArrayAdapter(final Context context, final int textViewResourceId,
                                 final List<FileChooserItem> objects) {
    super(context, textViewResourceId, objects);
    mContext = context;
    mId = textViewResourceId;
    mItems = objects;
  }

  /**
   * Returns an items at a specific position.
   * @param i The item index.
   * @return The item.
   */
  @Override
  public FileChooserItem getItem(final int i) {
    return mItems.get(i);
  }



  @Override
  public int getPosition(FileChooserItem item) {
    return super.getPosition(item);
  }

  @Override
  public int getCount() {
    return mItems.size();
  }

  @Override
  public long getItemId(int position) {
    return super.getItemId(position);
  }

  /**
   * Returns the current view.
   * @param position The view position.
   * @param convertView The view to convert.
   * @param parent The parent.
   * @return The new view.
   */
  @Override
  public @NonNull
  View getView(final int position, final View convertView,
               @NonNull final ViewGroup parent) {
    View v = convertView;
    ViewHolder holder;
    if (v == null) {
      final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      assert inflater != null;
      v = inflater.inflate(mId, null);
      holder = new ViewHolder();
      holder.data = v.findViewById(R.id.data);
      holder.icon = v.findViewById(R.id.icon);
      holder.name = v.findViewById(R.id.name);
      v.setTag(holder);
    } else {
      holder = (ViewHolder)v.getTag();
    }
    final FileChooserItem o = mItems.get(position);
    if (o != null) {
      holder.icon.setImageDrawable(o.getIcon());
      holder.name.setText(o.getName());
      holder.data.setText(o.getData());
    }
    return v;
  }

}
