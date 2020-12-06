package fr.ralala.netcap.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import fr.ralala.netcap.ApplicationCtx;
import fr.ralala.netcap.R;
import fr.ralala.netcap.net.NetworkHelper;
import fr.ralala.netcap.net.layer.Payload;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class PayloadViewerActivity extends AppCompatActivity {

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_payloadviewer);

    ActionBar ab = getSupportActionBar();
    if(ab != null)
      ab.setDisplayHomeAsUpEnabled(true);

    final ApplicationCtx app = (ApplicationCtx) getApplicationContext();
    setTitle(getResources().getText(R.string.app_name) + " - " + app.getId());
    ListView payloadLV = findViewById(R.id.lbPayload);
    Payload p = (Payload)app.getLayer();
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        R.layout.list_payload, R.id.label1,
        NetworkHelper.formatBuffer(p.getData()));
    payloadLV.setAdapter(adapter);
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
}
