package fr.ralala.netcap.ui.activities;


import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import fr.ralala.netcap.ApplicationCtx;
import fr.ralala.netcap.R;
import fr.ralala.netcap.ui.listview.CaptureListViewItem;
import fr.ralala.netcap.ui.listview.CaptureListViewLoader;
import fr.ralala.netcap.ui.listview.ListViewAdapter;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class CaptureViewerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
  public static final String KEY_FILE = "capture_file";
  private String mFile = null;
  private ListView mLvCapture = null;
  private ListViewAdapter<CaptureListViewItem> mAdapter= null;
  private final Search mPreviousSearch = new Search();
  private ApplicationCtx mApp;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_captureviewer);

    mApp = (ApplicationCtx) getApplicationContext();
    mApp.getFabHelper().install(this);

    Bundle b = getIntent().getExtras();
    if(b != null) {
      if (b.containsKey(KEY_FILE))
        mFile = b.getString(KEY_FILE);
    }
    if(mFile == null) mFile = getIntent().getData().getEncodedPath();

    ActionBar ab = getSupportActionBar();
    if(ab != null)
      ab.setDisplayHomeAsUpEnabled(true);

    mLvCapture = findViewById(R.id.lvCapture);
    mAdapter = new ListViewAdapter<>(this, R.layout.rowlayout_capture);
    mAdapter.setFilterId(CaptureListViewItem.FILTER_BY_ALL);
    CaptureListViewLoader loader = new CaptureListViewLoader(this, mAdapter, mFile);
    mLvCapture.setAdapter(mAdapter);
    loader.execute();
    mLvCapture.setOnItemClickListener(this);
    mLvCapture.setOnItemLongClickListener(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; go home
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if(mApp.getFabHelper().isExpanded()) {
      mApp.getFabHelper().collapseFab();
    } else {
      super.onBackPressed();
      finish();
    }
  }

  @Override
  public void onItemClick(AdapterView<?> adapter, View v, int pos, long id) {
    final CaptureListViewItem item = (CaptureListViewItem)mLvCapture.getItemAtPosition(pos);
    ApplicationCtx.startActivity(this, item.getLayer(), item.getId(), CaptureDetailsActivity.class);
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> adapter, View v, int pos, long id) {
    final CaptureListViewItem item = (CaptureListViewItem)mLvCapture.getItemAtPosition(pos);
    Vibrator vibrator = ((Vibrator) getSystemService(VIBRATOR_SERVICE));
    if(vibrator != null)
      vibrator.vibrate(50);
    ApplicationCtx.startActivity(this, item.getPayload(), item.getId(), PayloadViewerActivity.class);
    return true;
  }

  public void fabActionDown(View view) {
    mLvCapture.post(() -> mLvCapture.setSelection(mAdapter.getCount() - 1));
  }

  public void fabActionUp(View view) {
    mLvCapture.post(() -> mLvCapture.setSelection(0));
  }

  public void fabActionSearch(View v) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View alertLayout = getLayoutInflater().inflate(R.layout.search_dialog, null);
    builder.setView(alertLayout);

    final TextInputEditText tieSearch = alertLayout.findViewById(R.id.tieSearch);
    final CheckBox cbId = alertLayout.findViewById(R.id.cbId);
    final CheckBox cbInfo = alertLayout.findViewById(R.id.cbInfo);
    final CheckBox cbProtocol = alertLayout.findViewById(R.id.cbProtocol);
    final CheckBox cbTime = alertLayout.findViewById(R.id.cbTime);

    cbId.setChecked(mPreviousSearch.id);
    cbInfo.setChecked(mPreviousSearch.info);
    cbProtocol.setChecked(mPreviousSearch.protocol);
    cbTime.setChecked(mPreviousSearch.time);
    tieSearch.setText(mPreviousSearch.text);
    // Set up the buttons
    builder.setPositiveButton(getString(R.string.ok), null);
    builder.setNegativeButton(getString(R.string.cancel), null);
    final AlertDialog mAlertDialog = builder.create();
    mAlertDialog.setOnShowListener((dialog) -> {
      Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
      b.setOnClickListener((view) -> {
        mPreviousSearch.id = cbId.isChecked();
        mPreviousSearch.info = cbInfo.isChecked();
        mPreviousSearch.protocol = cbProtocol.isChecked();
        mPreviousSearch.time = cbTime.isChecked();
        mPreviousSearch.text = Objects.requireNonNull(tieSearch.getText()).toString();

        int flags = 0;
        if(mPreviousSearch.id) flags |= CaptureListViewItem.FILTER_BY_ID;
        if(mPreviousSearch.time) flags |= CaptureListViewItem.FILTER_BY_TIME;
        if(mPreviousSearch.protocol) flags |= CaptureListViewItem.FILTER_BY_PROTOCOL;
        if(mPreviousSearch.info) flags |= CaptureListViewItem.FILTER_BY_INFO;
        mAdapter.setFilterId(flags);
        mAdapter.filter(mPreviousSearch.text);
        mAlertDialog.dismiss();
      });
    });
    mAlertDialog.setCanceledOnTouchOutside(false);
    mAlertDialog.show();
  }


  private static class Search {
    boolean id = true;
    boolean time = true;
    boolean protocol = true;
    boolean info = true;
    String text = "";
  }
}
