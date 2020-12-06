package fr.ralala.netcap.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import fr.ralala.netcap.R;
import fr.ralala.netcap.net.capture.CaptureFile;
import fr.ralala.netcap.ui.activities.CaptureViewerActivity;
import fr.ralala.netcap.ui.activities.MainPagerActivity;
import fr.ralala.netcap.ui.chooser.AbstractFileChooserActivity;
import fr.ralala.netcap.ui.chooser.FileChooserActivity;
import fr.ralala.netcap.ui.listview.ListViewAdapter;
import fr.ralala.netcap.ui.listview.RecentFileListViewItem;
import fr.ralala.netcap.ui.utils.UI;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class InputFragment extends Fragment implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
  private final static String KEY_RECENT = "key.recent.";
  private ListViewAdapter<RecentFileListViewItem> mAdapter = null;
  private Comparator<RecentFileListViewItem> mComparator = null;
  private ListView mLvRecent = null;
  private final MainPagerActivity mOwner;
  private SharedPreferences mPreferences = null;

  public InputFragment(final MainPagerActivity owner) {
    mOwner = owner;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(
        R.layout.fragment_input, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Context context = mOwner.getApplicationContext();

    View v = getView();
    if(v == null)
      return;

    mPreferences = mOwner.getPreferences(Context.MODE_PRIVATE);
    mLvRecent = v.findViewById(R.id.lvRecent);
    mAdapter = new ListViewAdapter<>(context, R.layout.list_last_file);
    mLvRecent.setAdapter(mAdapter);
    mLvRecent.setOnItemClickListener(this);
    mLvRecent.setOnItemLongClickListener(this);
    v.findViewById(R.id.btLoadCapture).setOnClickListener(this);
    mComparator = (lhs, rhs) -> {
      if (lhs.getTime() < rhs.getTime()) return 1;
      else if (lhs.getTime() > rhs.getTime()) return -1;
      return 0;
    };

  }

  @Override
  public void onResume() {
    super.onResume();
    mAdapter.clear();
    int i = 0;
    while (mPreferences.contains(KEY_RECENT + i)) {
      String c = mPreferences.getString(KEY_RECENT + i, null);
      if (c != null && !c.isEmpty()) {
        mAdapter.addItem(new RecentFileListViewItem(KEY_RECENT + i, c));
      }
      i++;
    }
    mAdapter.sort(mComparator);
  }

  private void searchAndAdd(String file) {
    mAdapter.clear();
    int i = 0;
    boolean found = false;
    Editor edit = mPreferences.edit();
    while (mPreferences.contains(KEY_RECENT + i)) {
      String c = mPreferences.getString(KEY_RECENT + i, null);
      if (c != null && !c.isEmpty()) {
        RecentFileListViewItem r = new RecentFileListViewItem(KEY_RECENT + i, c);
        if (file.equals(r.getFile())) {
          r.setTime(new Date().getTime());
          found = true;
        }
        mAdapter.addItem(r);
        edit.putString(r.getKey(), r.getTime() + "|" + r.getFile());
        edit.apply();
      }
      i++;
    }
    if (!found) {
      RecentFileListViewItem r = new RecentFileListViewItem(KEY_RECENT + i, new Date().getTime() + "|" + file);
      mAdapter.addItem(r);
      edit.putString(r.getKey(), r.getTime() + "|" + r.getFile());
      edit.apply();
    }
    mAdapter.sort(mComparator);
  }

  @Override
  public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
    final RecentFileListViewItem item = (RecentFileListViewItem) mLvRecent.getItemAtPosition(pos);
    validateAndOpen(item.getFile());
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> ad, View v, int pos, long id) {
    final RecentFileListViewItem item = (RecentFileListViewItem) mLvRecent.getItemAtPosition(pos);
    final android.view.View.OnClickListener yes = view -> {
      Editor edit = mPreferences.edit();
      edit.remove(item.getKey());
      edit.apply();
      mAdapter.removeItem(item);
      mAdapter.sort(mComparator);
    };
    UI.showConfirmDialog(mOwner, "Remove entry", "Remove entry:\n" + item.getFilename(), yes, null);
    return true;
  }

  @Override
  public void onClick(final View v) {
    Intent i = new Intent(mOwner, FileChooserActivity.class);
    i.putExtra(AbstractFileChooserActivity.FILECHOOSER_TYPE_KEY, "" + AbstractFileChooserActivity.FILECHOOSER_TYPE_FILE_ONLY);
    i.putExtra(AbstractFileChooserActivity.FILECHOOSER_TITLE_KEY, getString(R.string.chooser_load_title));
    i.putExtra(AbstractFileChooserActivity.FILECHOOSER_MESSAGE_KEY, getString(R.string.chooser_use_file) + ":? ");
    i.putExtra(AbstractFileChooserActivity.FILECHOOSER_SHOW_KEY, "" + AbstractFileChooserActivity.FILECHOOSER_SHOW_FILE_AND_DIRECTORY);
    i.putExtra(AbstractFileChooserActivity.FILECHOOSER_DEFAULT_DIR, Environment.getExternalStorageDirectory().getAbsolutePath());
    mOwner.startActivityFromFragment(this, i, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE);
  }


  @Override
  public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    if (requestCode == FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE) {
      if (resultCode == AppCompatActivity.RESULT_OK) {
        final String file = data.getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
        assert file != null;
        validateAndOpen(file);
      }
    }
  }

  public void validateAndOpen(String file) {
    if (file.isEmpty()) {
      UI.showAlertDialog(mOwner, R.string.error, R.string.empty_source_file);
      return;
    }
    File fsrc = new File(file);
    if (!fsrc.isFile()) {
      UI.showAlertDialog(mOwner, R.string.error, R.string.source_file_is_not_a_valid_file);
      return;
    } else if (!fsrc.canRead()) {
      UI.showAlertDialog(mOwner, R.string.error, R.string.unable_to_read_the_capture_file);
      return;
    }
    try {
      if (!CaptureFile.isPCAP(fsrc.getAbsolutePath())) {
        UI.showAlertDialog(mOwner, R.string.error, R.string.selected_file_is_not_in_a_pcap_format);
        return;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      UI.showAlertDialog(mOwner, R.string.error, getString(R.string.exception_occurred) + ": " + t.getMessage());
      return;
    }
    searchAndAdd(fsrc.getAbsolutePath());
    Intent i = new Intent(mOwner, CaptureViewerActivity.class);
    i.putExtra(CaptureViewerActivity.KEY_FILE, fsrc.getAbsolutePath());
    mOwner.startActivityFromFragment(this, i, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE);
  }
}
