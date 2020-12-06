package fr.ralala.netcap.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseIntArray;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import fr.ralala.netcap.R;
import fr.ralala.netcap.ui.fragments.InputFragment;
import fr.ralala.netcap.ui.fragments.OutputFragment;
import fr.ralala.netcap.ui.pager.ScreenSlidePagerAdapter;
import fr.ralala.netcap.ui.utils.UI;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class MainPagerActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
  private static final int BACK_TIME_DELAY = 2000;
  private static final int PERMISSIONS_REQUEST = 30;
  private final SparseIntArray mErrorString = new SparseIntArray();
  private static long mLastBackPressed = -1;
  private static final String CURRENT_PAGER_TAB_KEY = "CURRENT_PAGER_TAB_KEY";
  private SharedPreferences mPrefs = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maintab);
    mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    List<Fragment> fragments = new Vector<>();
    fragments.add(new InputFragment(this));
    fragments.add(new OutputFragment(this));

    ViewPager pager = findViewById(R.id.pager);
    PagerTabStrip pagerTabStrip = findViewById(R.id.pagerTabStrip);
    pagerTabStrip.setDrawFullUnderline(false);
    PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
    pager.setAdapter(pagerAdapter);
    pager.setCurrentItem(mPrefs.getInt(CURRENT_PAGER_TAB_KEY, 0));
    pager.addOnPageChangeListener(this);

    String[] perms = new String[]{
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
    };
    requestAppPermissions(perms, R.string.permissions , PERMISSIONS_REQUEST);
  }

  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    if (mLastBackPressed + BACK_TIME_DELAY > System.currentTimeMillis()) {
      super.onBackPressed();
    } else {
      UI.toast(this, R.string.on_double_back_exit_text);
    }
    mLastBackPressed = System.currentTimeMillis();
  }

  @Override
  public void onPageSelected(final int position) {
    final Editor e = mPrefs.edit();
    e.putInt(CURRENT_PAGER_TAB_KEY, position);
    e.apply();
  }

  @Override
  public void onPageScrollStateChanged(final int state) {
  }

  @Override
  public void onPageScrolled(final int position, final float positionOffset,
                             final int positionOffsetPixels) {
  }



  /* ******* PERMISSIONS ******** */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    int permissionCheck = PackageManager.PERMISSION_GRANTED;
    for (int permission : grantResults) {
      permissionCheck = permissionCheck + permission;
    }
    if ((grantResults.length > 0) && permissionCheck == PackageManager.PERMISSION_GRANTED) {
      Log.i(getClass().getSimpleName(), "onPermissionsGranted: " + requestCode);
    } else {
      Snackbar.make(findViewById(android.R.id.content), mErrorString.get(requestCode),
          Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
          (v) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
          }).show();
    }
  }

  public void requestAppPermissions(final String[] requestedPermissions,
                                    final int stringId, final int requestCode) {
    mErrorString.put(requestCode, stringId);
    int permissionCheck = PackageManager.PERMISSION_GRANTED;
    boolean shouldShowRequestPermissionRationale = false;
    for (String permission : requestedPermissions) {
      permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
      shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      if (shouldShowRequestPermissionRationale) {
        Snackbar.make(findViewById(android.R.id.content), stringId,
            Snackbar.LENGTH_INDEFINITE).setAction("GRANT",
            (v) -> ActivityCompat.requestPermissions(this, requestedPermissions, requestCode)).show();
      } else {
        ActivityCompat.requestPermissions(this, requestedPermissions, requestCode);
      }
    } else {
      Log.i(getClass().getSimpleName(), "onPermissionsGranted: " + requestCode);
    }
  }
}
