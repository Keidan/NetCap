package fr.ralala.netcap.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.fragment.app.Fragment;
import fr.ralala.netcap.R;
import fr.ralala.netcap.ui.activities.MainPagerActivity;
import fr.ralala.netcap.ui.chooser.AbstractFileChooserActivity;
import fr.ralala.netcap.ui.chooser.FileChooserActivity;
import fr.ralala.netcap.ui.utils.UI;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class OutputFragment extends Fragment implements View.OnClickListener {
  private static final String NETCAP_READY = "NetCap ready with PID ";
  private final MainPagerActivity mOwner;
  private Spinner mSpDevices = null;
  private TextView mTvBrowseOutputCapture = null;
  private CheckBox mCbPromiscuous = null;
  private ArrayAdapter<String> mAdapter = null;
  private ToggleButton mBtCapture = null;
  private Button mBtRefresh = null;
  private TextView mTvShowResult = null;
  private final StringBuilder mIfaces = new StringBuilder();
  private final CircularArray<String> mBuffer = new CircularArray<>(25);
  private Command mCommand = null;
  private String mPname = null;
  private String mPid = null;

  public OutputFragment(final MainPagerActivity owner) {
    mOwner = owner;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(
        R.layout.fragment_output, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Context context = mOwner.getApplicationContext();

    View v = getView();
    if(v == null)
      return;

    mBtRefresh = v.findViewById(R.id.btRefresh);
    mCbPromiscuous = v.findViewById(R.id.cbPromiscuous);
    mSpDevices = v.findViewById(R.id.spDevices);
    mTvShowResult = v.findViewById(R.id.tvShowResult);
    mTvBrowseOutputCapture = v.findViewById(R.id.tvBrowseOutputCapture);
    mTvBrowseOutputCapture.setText(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
    mTvBrowseOutputCapture.setOnClickListener(this);
    mBtCapture = v.findViewById(R.id.btCapture);
    mBtCapture.setOnClickListener(this);
    mBtRefresh.setOnClickListener(this);

    mTvShowResult.setMovementMethod(new ScrollingMovementMethod());

    // Create an ArrayAdapter using the string array and a default spinner
    // layout
    mAdapter = new ArrayAdapter<>(context,
        R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    mAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    mSpDevices.setAdapter(mAdapter);
    onClick(mBtRefresh);
  }

  @Override
  public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    if (requestCode == FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE) {
      if (resultCode == AppCompatActivity.RESULT_OK) {
        final String file = data.getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
        setFile(file);
      }
    }
  }

  private void displayBuffer() {
    String [] array = new String[mBuffer.size()];
    for(int i = 0; i < array.length; i++)
      array[i] = mBuffer.get(i);
    final StringBuilder sb = new StringBuilder();
    for (final String s1 : array)
      sb.append(" ").append(s1).append("\n");
    mTvShowResult.setText(sb.toString());
  }

  private void logException(Throwable e) {
    final String ex = "Exception: " + e.getMessage();
    Log.e(getClass().getSimpleName(), ex, e);
    mOwner.runOnUiThread(() -> {
      //UI.toastLong(mOwner, ex);
      mBuffer.addLast(ex);
      displayBuffer();
      mBtCapture.setChecked(false);
    });
  }

  public void delete() {
    if(mCommand != null) {
      mCommand.resetCommand();
      mCommand.finish();
      mCommand = null;
    }
    try {
      RootTools.closeAllShells();
    } catch (IOException e) {
      logException(e);
    }
    if(mPname != null) {
      //RootTools.killProcess(pname);
      //pname = null;
      mCommand = new Command(0, false, "kill -15 " + mPid);
      try {
        RootTools.getShell(true).add(mCommand);
        RootTools.closeAllShells();
      } catch (Exception e) {
        logException(e);
      }
    }
  }

  @Override
  public void onClick(final View v) {
    if(v.equals(mBtRefresh)) {
      mAdapter.clear();
      try {
        final List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        if (interfaces.size() != 0)
          mAdapter.add(getResources().getText(R.string.any).toString());
        for (final NetworkInterface ni : interfaces)
          if (ni.isUp())
            mAdapter.add(ni.getName());
      } catch (final Throwable e) {
        logException(e);
      }
    }
    else if(v.equals(mTvBrowseOutputCapture)) {
      Intent i = new Intent(mOwner, FileChooserActivity.class);
      i.putExtra(AbstractFileChooserActivity.FILECHOOSER_TYPE_KEY, "" + AbstractFileChooserActivity.FILECHOOSER_TYPE_DIRECTORY_ONLY);
      i.putExtra(AbstractFileChooserActivity.FILECHOOSER_TITLE_KEY, getString(R.string.chooser_save_title));
      i.putExtra(AbstractFileChooserActivity.FILECHOOSER_MESSAGE_KEY, getString(R.string.chooser_use_folder) + ":? ");
      i.putExtra(AbstractFileChooserActivity.FILECHOOSER_SHOW_KEY, "" + AbstractFileChooserActivity.FILECHOOSER_SHOW_DIRECTORY_ONLY);
      i.putExtra(AbstractFileChooserActivity.FILECHOOSER_DEFAULT_DIR, mTvBrowseOutputCapture.getText().toString());
      mOwner.startActivityFromFragment(this, i, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_DIRECTORY);
    } else if(v.equals(mBtCapture)) {
      if(!mBtCapture.isChecked()) {
        mBtCapture.setChecked(true);
        delete();
      } else {
        String sdest = mTvBrowseOutputCapture.getText().toString();
        if (sdest.isEmpty()) {
          UI.showAlertDialog(mOwner, R.string.error, getString(R.string.empty_source_folder));
          mBtCapture.setChecked(false);
          return;
        }

        File legacy = new File(sdest.replaceFirst("emulated/([0-9]+)/", "emulated/legacy/"));
        File fdest = new File(sdest);
        Log.i(getClass().getSimpleName(), "Test directory '"  + legacy + "'");
        if (legacy.isDirectory()) fdest = legacy;
        if (!fdest.isDirectory()) {
          UI.showAlertDialog(mOwner, R.string.error,
              getString(R.string.source_file_is_not_a_valid_folder));
          mBtCapture.setChecked(false);
          return;
        } else if (!fdest.canWrite()) {
          UI.showAlertDialog(mOwner, R.string.error,
              getString(R.string.unable_to_write_into_the_destination_folder));
          mBtCapture.setChecked(false);
          return;
        }

        if(!RootTools.isAccessGiven()) {
          UI.toast(mOwner, R.string.root_toast_error);
          mBuffer.addLast(getString(R.string.root_error));
          displayBuffer();
          mBtCapture.setChecked(false);
          return;
        }

        final PackageManager m = mOwner.getPackageManager();
        String s = mOwner.getPackageName();
        try {
          final PackageInfo p = m.getPackageInfo(s, 0);
          s = p.applicationInfo.dataDir;
        } catch (final PackageManager.NameNotFoundException ignored) {
        }
        s = s + "/netcap";
        copyFile(Build.SUPPORTED_ABIS[0] + "/netcap", s, mOwner);
        final File netcap = new File(s);
        if (!netcap.exists()) {
          UI.showAlertDialog(mOwner, R.string.error, "'netcap' for '"
              + Build.SUPPORTED_ABIS[0] + "' was not found!");
          return;
        }
        mPname = netcap.getAbsolutePath();
        boolean b = netcap.setExecutable(true);
        Log.w(getClass().getSimpleName(), "setExecutable: " + (b ? "SUCCESS" : "ERROR"));
        mIfaces.setLength(0);
        String ni = (String) mSpDevices.getSelectedItem();
        if (ni.equals(getResources().getText(R.string.any).toString())) {
          for (int i = 0; i < mAdapter.getCount(); i++) {
            ni = mAdapter.getItem(i);
            if (ni.equals(getResources().getText(R.string.any).toString()))
              continue;
            mIfaces.append(ni);
            if (i < mAdapter.getCount() - 1)
              mIfaces.append(",");
          }
        } else
          mIfaces.append(ni);


        final File outputFile = new File(fdest, new SimpleDateFormat(
            "yyyyMMdd_hhmmssa'_netcap.pcap'", Locale.US).format(new Date()));
        mBuffer.clear();
        new Thread(() -> {
          try {
            String cmd = netcap.getAbsolutePath() + " -i " + mIfaces.toString() + " -f " + outputFile.getAbsolutePath() + " -d";
            if(mCbPromiscuous.isChecked()) cmd += " -p";
            cmd += " 2>&1";
            mCommand = new Command(0, cmd) {
              @Override
              public void commandOutput(int id, String line) {
                super.commandOutput(id, line);
                if(line.startsWith(NETCAP_READY))
                  mPid = line.substring(NETCAP_READY.length());
                mBuffer.addLast(line);
                mOwner.runOnUiThread(() -> displayBuffer());
              }
              public void commandCompleted(int id, int exitcode) {
                super.commandCompleted(id, exitcode);
                mOwner.runOnUiThread(() -> mBtCapture.setChecked(false));
              }
              public void commandTerminated(int id, String reason) {
                super.commandTerminated(id, reason);
              }
            };
            RootTools.getShell(true).add(mCommand);
          } catch(final Exception e) {
            logException(e);
          }
        }).start();
      }
    }
  }

  public void setFile(String file) {
    mTvBrowseOutputCapture.setText(file);
  }

  private static void copyFile(final String assetPath, final String localPath,
                               final Context context) {
    try {
      final InputStream in = context.getAssets().open(assetPath);
      File f = new File(localPath);
      if(f.exists()) {
        boolean b = f.delete();
        Log.w(OutputFragment.class.getSimpleName(), "File delete: " + (b ? "SUCCESS" : "ERROR"));
      }
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
