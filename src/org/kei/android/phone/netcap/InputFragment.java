package org.kei.android.phone.netcap;


import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.view.chooser.FileChooser;
import org.kei.android.atk.view.chooser.FileChooserActivity;
import org.kei.android.phone.net.capture.CaptureFile;
import org.kei.android.phone.netcap.R;
import org.kei.android.phone.netcap.listview.ListViewAdapter;
import org.kei.android.phone.netcap.listview.RecentFileListViewItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 *******************************************************************************
 * @file InputFragment.java
 * @author Keidan
 * @date 27/01/2016
 * @par Project NetCap
 *
 * @par Copyright 2016 Keidan, all right reserved
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
public class InputFragment extends Fragment  implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
  private final static String                     KEY_RECENT  = "key.recent.";
  private SharedPreferences                       preferences = null;
  private SharedPreferences.Editor                editor      = null;
  private ListViewAdapter<RecentFileListViewItem> adapter     = null;
  private Comparator<RecentFileListViewItem>      comparator  = null;
  private ListView                                recentLV    = null;
  private MainPagerActivity                       owner       = null;
  
  public InputFragment(final MainPagerActivity owner) {
    this.owner = owner;
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
              R.layout.fragment_input, container, false);

      return rootView;
  }
  
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Context context = owner.getApplicationContext();
    
    preferences = owner.getPreferences(Context.MODE_PRIVATE);
    editor = preferences.edit();

    recentLV = (ListView)getView().findViewById(R.id.recentLV);
    adapter = new ListViewAdapter<RecentFileListViewItem>(context, R.layout.listview_simple_row);
    recentLV.setAdapter(adapter);
    recentLV.setOnItemClickListener(this);
    recentLV.setOnItemLongClickListener(this);
    ((Button)getView().findViewById(R.id.loadCaptureBT)).setOnClickListener(this);
    comparator = new Comparator<RecentFileListViewItem>() {
      
      @Override
      public int compare(RecentFileListViewItem lhs, RecentFileListViewItem rhs) {
        if(lhs.getTime() < rhs.getTime()) return 1;
        if(lhs.getTime() > rhs.getTime()) return -1;
        return 0;
      }
    };
    
  }

  public void onResume() {
    super.onResume();
    adapter.clear();
    int i = 0;
    while(preferences.contains(KEY_RECENT + i)) {
      String c = preferences.getString(KEY_RECENT + i, null);
      if(c != null && !c.isEmpty()) {
        String [] split = c.split(Pattern.quote("|"));
        RecentFileListViewItem r = new RecentFileListViewItem();
        r.setTime(Long.parseLong(split[0]));
        r.setFile(split[1]);
        r.setKey(KEY_RECENT + i);
        adapter.addItemSort(r, comparator);
      }
      i++;
    }
  }
  
  private void searchAndAdd(String file) {
    adapter.clear();
    int i = 0;
    boolean found = false;
    while(preferences.contains(KEY_RECENT + i)) {
      String c = preferences.getString(KEY_RECENT + i, null);
      if(c != null && !c.isEmpty()) {
        String [] split = c.split(Pattern.quote("|"));
        RecentFileListViewItem r = new RecentFileListViewItem();
        r.setTime(Long.parseLong(split[0]));
        r.setFile(split[1]);
        if(file.equals(r.getFile())) {
          r.setTime(new Date().getTime());
          editor.remove(r.getKey());
          editor.commit();
          editor.putString(r.getKey(), r.getTime() + "|" + r.getFile());
          editor.commit();
          found = true;
        }
        adapter.addItemSort(r, comparator);
      }
      i++;
    }
    if(!found) {
      RecentFileListViewItem r = new RecentFileListViewItem();
      r.setTime(new Date().getTime());
      r.setFile(file);
      r.setKey(KEY_RECENT + i);
      editor.putString(r.getKey(), r.getTime() + "|" + r.getFile());
      editor.commit();
      adapter.addItemSort(r, comparator);
    }
  }

  public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
    final RecentFileListViewItem item = (RecentFileListViewItem)recentLV.getItemAtPosition(pos);
    validateAndOpen(item.getFile());
  }
  
  @Override
  public boolean onItemLongClick(AdapterView<?> ad, View v, int pos, long id) {
    final RecentFileListViewItem item = (RecentFileListViewItem)recentLV.getItemAtPosition(pos);
    final android.view.View.OnClickListener yes = new android.view.View.OnClickListener() {
      @Override
      public void onClick(android.view.View view) {
        editor.remove(item.getKey());
        editor.commit();
        adapter.removeItemSort(item, comparator);
      }
    };
    Tools.showConfirmDialog(owner, "Remove entry", "Remove entry:\n" + item.getFile(), yes, null);
    return true;
  }
  
  public void onClick(final View v) {
    Map<String, String> extra = new HashMap<String, String>();
    extra.put(FileChooser.FILECHOOSER_TYPE_KEY, "" + FileChooser.FILECHOOSER_TYPE_FILE_ONLY);
    extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Load");    
    extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Use this file:? ");
    extra.put(FileChooser.FILECHOOSER_SHOW_KEY, "" + FileChooser.FILECHOOSER_SHOW_FILE_AND_DIRECTORY);
    extra.put(FileChooser.FILECHOOSER_USER_MESSAGE, getClass().getSimpleName());
    Tools.switchToForResult(owner, FileChooserActivity.class,
        extra, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE);
  }
  
  public void validateAndOpen(String file) {
    if(file.isEmpty()) {
      Tools.showAlertDialog(owner, "Error", "Empty source file!");
      return;
    }
    File fsrc = new File(file);
    if(!fsrc.isFile()) {
      Tools.showAlertDialog(owner, "Error", "The source file is not a valid file!");
      return;
    } else if(!fsrc.canRead()) {
      Tools.showAlertDialog(owner, "Error", "Unable to read the capture file!");
      return;
    }
    try {
      if(!CaptureFile.isPCAP(fsrc.getAbsolutePath())) {
        Tools.showAlertDialog(owner, "Error", "The selected file is not in a PCAP format.");
        return;
      }
    } catch(Throwable t) {
      t.printStackTrace();
      Tools.showAlertDialog(owner, "Error", "Exception occurred: " + t.getMessage());
      return;
    }
    searchAndAdd(fsrc.getAbsolutePath());
    Map<String, String> extra = new HashMap<String, String>();
    extra.put(CaptureViewerActivity.KEY_FILE, fsrc.getAbsolutePath());
    Tools.switchTo(owner, CaptureViewerActivity.class, extra);
  }
  
  protected int getToastIconId() {
    return R.drawable.ic_launcher;
  }
}
