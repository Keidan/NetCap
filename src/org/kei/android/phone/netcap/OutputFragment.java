package org.kei.android.phone.netcap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.view.chooser.FileChooser;
import org.kei.android.atk.view.chooser.FileChooserActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 *******************************************************************************
 * @file OutputFragment.java
 * @author Keidan
 * @date 27/01/2016
 * @par Project NetCap
 *
 * @par Copyright 2016 Keidan, all right reserved
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
public class OutputFragment extends Fragment implements
    OnClickListener {
  private Spinner              devicesSp             = null;
  private TextView             browseOutputCaptureTV = null;
  private CheckBox             promiscuousCB         = null;
  private ArrayAdapter<String> adapter               = null;
  private ToggleButton         captureBT             = null;
  private Button               refreshBT             = null;
  private TextView             showResult            = null;
  private CircularFifoBuffer   buffer                = null;
  private Process              process               = null;
  private String               ifaces                = "";
  private MainPagerActivity    owner                 = null;
  
  public OutputFragment(final MainPagerActivity owner) {
    this.owner = owner;
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
              R.layout.fragment_output, container, false);

      return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Context context = owner.getApplicationContext();

    refreshBT = (Button) getView().findViewById(R.id.refreshBT);
    promiscuousCB = (CheckBox) getView().findViewById(R.id.promiscuousCB);
    devicesSp = (Spinner) getView().findViewById(R.id.devicesSp);
    showResult = (TextView) getView().findViewById(R.id.showResult);
    browseOutputCaptureTV = (TextView) getView().findViewById(R.id.browseOutputCaptureTV);
    browseOutputCaptureTV.setText(Tools.DEFAULT_DOWNLOAD.getAbsolutePath());
    browseOutputCaptureTV.setOnClickListener(this);
    captureBT = (ToggleButton) getView().findViewById(R.id.captureBT);
    captureBT.setOnClickListener(this);
    refreshBT.setOnClickListener(this);
    
    showResult.setMovementMethod(new ScrollingMovementMethod());
    
    buffer = new CircularFifoBuffer(20);
    // Create an ArrayAdapter using the string array and a default spinner
    // layout
    adapter = new ArrayAdapter<String>(context,
        android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    adapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    devicesSp.setAdapter(adapter);
    onClick(refreshBT);
  }
  
  public void delete(boolean uncheck) {
    if(process != null) {
      process.destroy();
      process = null;
    }
    if(uncheck)
      owner.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          captureBT.setChecked(false);
        }
      });
  }

  @Override
  public void onClick(final View v) {
    if(v.equals(refreshBT)) {
      adapter.clear();
      try {
        final List<NetworkInterface> interfaces = Collections
            .list(NetworkInterface.getNetworkInterfaces());
        if (interfaces != null && interfaces.size() != 0)
          adapter.add(getResources().getText(R.string.any).toString());
        for (final NetworkInterface ni : interfaces)
          if (ni.isUp())
            adapter.add(ni.getName());
      } catch (final Throwable e) {
        Log.e(OutputFragment.class.getSimpleName(), "Exception: " + e.getMessage(), e);
        Tools.toast(owner, R.drawable.ic_launcher, "Exception: " + e.getMessage(), Tools.TOAST_LENGTH_LONG);
      }
    }
    else if(v.equals(browseOutputCaptureTV)) {
      final Map<String, String> extra = new HashMap<String, String>();
      extra.put(FileChooser.FILECHOOSER_TYPE_KEY, ""
          + FileChooser.FILECHOOSER_TYPE_DIRECTORY_ONLY);
      extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Save");
      extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Use this folder:? ");
      extra.put(FileChooser.FILECHOOSER_DEFAULT_DIR, browseOutputCaptureTV
          .getText().toString());
      extra.put(FileChooser.FILECHOOSER_SHOW_KEY, ""
          + FileChooser.FILECHOOSER_SHOW_DIRECTORY_ONLY);
      extra.put(FileChooser.FILECHOOSER_USER_MESSAGE, getClass().getSimpleName());
      Tools.switchToForResult(owner, FileChooserActivity.class, extra,
          FileChooserActivity.FILECHOOSER_SELECTION_TYPE_DIRECTORY);
      
    } else if(v.equals(captureBT)) {
      if(!captureBT.isChecked()) {
        delete(false);
      } else {
        final String sdest = browseOutputCaptureTV.getText().toString();
        if (sdest.isEmpty()) {
          Tools.showAlertDialog(owner, "Error", "Empty destination folder!");
          return;
        }
        final File fdest = new File(sdest);
        if (!fdest.isDirectory()) {
          Tools.showAlertDialog(owner, "Error",
              "The destination folder is not a valid directory!");
          return;
        } else if (!fdest.canWrite()) {
          Tools.showAlertDialog(owner, "Error",
              "Unable to write into the destination folder!");
          return;
        }
        final PackageManager m = owner.getPackageManager();
        String s = owner.getPackageName();
        try {
          final PackageInfo p = m.getPackageInfo(s, 0);
          s = p.applicationInfo.dataDir;
        } catch (final PackageManager.NameNotFoundException e) {
        }
        s = s + "/netcap";
        copyFile(Build.SUPPORTED_ABIS[0] + "/netcap", s, owner);
        final File netcap = new File(s);
        if (!netcap.exists()) {
          Tools.showAlertDialog(owner, "Error", "'netcap' for '"
              + Build.SUPPORTED_ABIS[0] + "' was not found!");
          return;
        }
        netcap.setExecutable(true);
  
        ifaces = "";
        String ni = (String) devicesSp.getSelectedItem();
        if (ni.equals(getResources().getText(R.string.any).toString())) {
          for (int i = 0; i < adapter.getCount(); i++) {
            ni = adapter.getItem(i);
            if (ni.equals(getResources().getText(R.string.any).toString()))
              continue;
            ifaces += ni;
            if (i < adapter.getCount() - 1)
              ifaces += ",";
          }
        } else
          ifaces = ni;
        final File outputFile = new File(fdest, new SimpleDateFormat(
            "yyyyMMdd_hhmmssa'_netcap.pcap'", Locale.US).format(new Date()));
        
        Log.i(getClass().getSimpleName(), "ifaces:" + ifaces);
        buffer.clear();
        if(!Tools.gainRoot()) {
          Resources r = getResources();
          Tools.toast(owner, R.drawable.ic_launcher, r.getString(R.string.root_toast_error));
          showResult.setText(" " + r.getString(R.string.root_error));
          captureBT.setChecked(false);
          return;
        }
        new Thread(new Runnable() {
          public void run() {
            try {
              String cmd = "su -c '";
              cmd += netcap.getAbsolutePath() + " -i " + ifaces + " -f " + outputFile.getAbsolutePath() + " -d";
              if(promiscuousCB.isChecked()) cmd += " -p";
              cmd += "'";
              Log.i(getClass().getSimpleName(), "cmd:" + cmd);
              process = Runtime.getRuntime().exec(cmd);
              BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
              String line = null;
              while((line = bufferedReader.readLine()) != null) {
                buffer.add(line);
                owner.runOnUiThread(new Runnable() {
                  @SuppressWarnings("unchecked")
                  @Override
                  public void run() {
                    final String[] lines = (String[]) buffer.toArray(new String[] {});
                    final StringBuilder sb = new StringBuilder();
                    for (final String s : lines)
                      sb.append(" ").append(s).append("\n");
                    showResult.setText(sb.toString());
                  }
                });
              }
              if(process.waitFor() != 0)
                delete(true);
            } catch(Exception e) {
              Log.e(OutputFragment.class.getSimpleName(), "Exception: " + e.getMessage(), e);
              Tools.toast(owner, R.drawable.ic_launcher, "Exception: " + e.getMessage(), Tools.TOAST_LENGTH_LONG);
            }
          }
        }).start();
      }
    }
  }

  public void setFile(String file) {
    browseOutputCaptureTV.setText(file);
  }

  private static void copyFile(final String assetPath, final String localPath,
      final Context context) {
    try {
      final InputStream in = context.getAssets().open(assetPath);
      File f = new File(localPath);
      if(f.exists()) f.delete();
      final FileOutputStream out = new FileOutputStream(f);
      int read;
      final byte[] buffer = new byte[4096];
      while ((read = in.read(buffer)) > 0) {
        out.write(buffer, 0, read);
      }
      out.close();
      in.close();

    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
