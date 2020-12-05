package fr.ralala.netcap.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import fr.ralala.netcap.ApplicationCtx;
import fr.ralala.netcap.R;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.ui.listview.ExpandableListAdapter;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class CaptureDetailsActivity extends AppCompatActivity {

  private List<String> mListDataHeader;
  private HashMap<String, List<String>> mListDataChild;
  private ApplicationCtx mApp;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_capturedetails);
    mApp = (ApplicationCtx)getApplicationContext();
    setTitle(getResources().getText(R.string.app_name) + " - " + mApp.getId());

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    // get the listview
    ExpandableListView expListView = findViewById(R.id.elvDetails);

    // preparing list data
    prepareListData();

    ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);

    // setting list adapter
    expListView.setAdapter(listAdapter);
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
    super.onBackPressed();
    finish();
  }

  /*
   * Preparing the list data
   */
  private void prepareListData() {
    mListDataHeader = new ArrayList<>();
    mListDataChild = new HashMap<>();
    Layer layers = mApp.getLayer();
    Layer layer;
    do {
      if (layers != null) {
        layer = layers;
        List<String> lines = new ArrayList<>();
        layer.buildDetails(lines);
        mListDataHeader.add(layer.getProtocolUI().fullName);
        mListDataChild.put(layer.getProtocolUI().fullName, lines);
      }
    } while ((layers = layers.getNext()) != null);
  }
}
