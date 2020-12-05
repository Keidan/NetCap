package fr.ralala.netcap.ui.listview;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.capture.CaptureFile;
import fr.ralala.netcap.net.capture.ICapture;
import fr.ralala.netcap.net.capture.PCAPPacketHeader;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.net.layer.Payload;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class CaptureListViewLoader extends AsyncTask<Void, Void, Void> implements ICapture {
  private static final String DEFAULT_LINE = Color.parseColor("#FFFFFF") + "|" + Color.parseColor("#000000") + "|" + "ERR|ERR";
  private final CaptureFile mCapture = new CaptureFile();
  private final String mFile;
  private int mCaptureCount = 0;
  private final WeakReference<AppCompatActivity> mActivityRef;
  private final ListViewAdapter<CaptureListViewItem> mAdapter;
  private final List<CaptureListViewItem> mItems;
  private ProgressDialog mDialog;

  public CaptureListViewLoader(final AppCompatActivity activity,
                               final ListViewAdapter<CaptureListViewItem> adapter, final String file) {
    mFile = file;
    mActivityRef = new WeakReference<>(activity);
    mAdapter = adapter;
    mItems = new ArrayList<>();
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    mDialog = ProgressDialog.show(mActivityRef.get(), "", "Please wait");
    mItems.clear();
  }

  @Override
  protected void onPostExecute(Void result) {
    mCapture.close();
    mActivityRef.get().runOnUiThread(() -> {
      mAdapter.setItemList(mItems);
      mDialog.dismiss();
    });
    //dialog.dismiss();
  }

  @Override
  protected Void doInBackground(Void... arg0) {
    try {
      mCapture.open(mFile);
      while (mCapture.process(CaptureListViewLoader.this)) ;
    } catch (Throwable e) {
      e.printStackTrace();
      captureEnd(null);
    }
    return null;
  }


  @Override
  public void captureProcess(final CaptureFile capture,
                             final PCAPPacketHeader pheader, final byte[] buffer) {
    final CaptureListViewItem item = new CaptureListViewItem();
    ++mCaptureCount;
    item.setId("" + mCaptureCount);
    item.setTime(pheader.getTime());
    Payload payload = new Payload();
    payload.decodeLayer(buffer, null);
    Layer layer = NetworkHelper.getLayer(buffer);
    Layer last = NetworkHelper.getLastLayer(layer);
    String info = last == null ? DEFAULT_LINE : last.compute("|");
    int tcolor = Color.BLACK, bcolor = Color.WHITE;
    int idx = info.indexOf('|');
    if (idx != -1) {
      tcolor = Integer.parseInt(info.substring(0, idx));
      info = info.substring(idx + 1);
      idx = info.indexOf('|');
      if (idx != -1) {
        bcolor = Integer.parseInt(info.substring(0, idx));
        info = info.substring(idx + 1);
        idx = info.indexOf('|');
        if (idx != -1) {
          item.setProtocol(info.substring(0, idx));
          info = info.substring(idx + 1);
        }
      }
    }
    item.setTColor(tcolor);
    item.setBColor(bcolor);
    item.setInfo(info);
    item.setPheader(pheader);
    item.setLayer(layer);
    item.setPayload(payload);
    mItems.add(item);
    /*if ((captureCount % 1000) == 0)
      handler.sendEmptyMessage(0);*/
  }

  @Override
  public void captureEnd(final CaptureFile capture) {
    mCaptureCount = 0;
    capture.close();
  }

}
