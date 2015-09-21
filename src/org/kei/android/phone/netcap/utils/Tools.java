package org.kei.android.phone.netcap.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kei.android.phone.netcap.dialog.CustomDialog;
import org.kei.android.phone.netcap.utils.fx.Fx;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file Tools.java
 * @author Keidan
 * @date 01/09/2015
 * @par Project
 * NetCap
 *
 * @par 
 * Copyright 2015 Keidan, all right reserved
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
public class Tools {
  public static final String TAG          = Tools.class.getSimpleName();
  public static final File   DEFAULT_ROOT = Environment
                                              .getExternalStorageDirectory();

  public static void showAlertDialog(final Activity a, final String title,
      final String message) {
    final CustomDialog ad = new CustomDialog(a);
    ad.setCancelable(false); // This blocks the 'BACK' button
    if (title != null)
      ad.setTitle(title);
    TextView tv = new TextView(a);
    tv.setText(message);
    tv.setTextColor(a.getResources().getColor(android.R.color.darker_gray));
    tv.setTextSize(15);
    ad.setTextView(tv);
    ad.setPositiveOnClickListener(new android.view.View.OnClickListener() {
      public void onClick(View v) {
        ad.cancel();
      }
    });
    ad.hideNegativeButton();
    ad.show();
  }

  public static void showConfirmDialog(final Activity a, final String title,
      String message, final android.view.View.OnClickListener yes,
      final android.view.View.OnClickListener no) {
    final CustomDialog ad = new CustomDialog(a);
    ad.setCancelable(false); // This blocks the 'BACK' button
    if (title != null)
      ad.setTitle(title);
    TextView tv = new TextView(a);
    tv.setText(message);
    tv.setTextColor(a.getResources().getColor(android.R.color.darker_gray));
    tv.setTextSize(15);
    ad.setTextView(tv);
    ad.setPositiveOnClickListener(new android.view.View.OnClickListener() {
      public void onClick(View v) {
        ad.cancel();
        if (yes != null)
          yes.onClick(v);
      }
    });
    ad.setNegativeOnClickListener(new android.view.View.OnClickListener() {
      public void onClick(View v) {
        ad.cancel();
        if (no != null)
          no.onClick(v);
      }
    });
    ad.show();
  }

  public static boolean isMountedSdcard() {
    final String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state))
      return true;
    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
      return true;
    else
      return false;
  }

  public static void switchTo(final Activity activity, final Class<?> c) {
    switchTo(activity, c, null);
  }

  public static void switchTo(final Activity activity, final Class<?> c,
      final Map<String, String> extra) {
    final Intent i = new Intent(activity.getApplicationContext(), c);
    if (extra != null) {
      Set<String> keysSet = extra.keySet();
      for(Iterator<String> keys = keysSet.iterator(); keys.hasNext();) {
        String key = keys.next();
        i.putExtra(key, extra.get(key));
      }
    }
    activity.startActivity(i);
    Fx.updateTransition(activity, true);
  }

  public static void switchToForResult(final Activity activity,
      final Class<?> c, final int requestCode) {
    switchToForResult(activity, c, null, requestCode);
  }

  public static void switchToForResult(final Activity activity,
      final Class<?> c, final Map<String, String> extra,
      final int requestCode) {
    final Intent i = new Intent(activity.getApplicationContext(), c);
    if (extra != null) {
      Set<String> keysSet = extra.keySet();
      for(Iterator<String> keys = keysSet.iterator(); keys.hasNext();) {
        String key = keys.next();
        i.putExtra(key, extra.get(key));
      }
    }
    activity.startActivityForResult(i, requestCode);
    Fx.updateTransition(activity, true);
  }
  
  public static boolean gainRoot() {
    Process p;
    try {
      // Preform su to get root privledges
      p = Runtime.getRuntime().exec("su");
      
      // Attempt to write a file to a root-only
      DataOutputStream os = new DataOutputStream(p.getOutputStream());
      os.writeBytes("echo \"\" >/system/sd/.root.test\n");
      // Close the terminal
      os.writeBytes("exit\n");
      os.flush();
      try {
        p.waitFor();
        int ex = p.exitValue();
        if (ex != 255) {
          try {
            p = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("rm -f /system/sd/.root.test\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
          } catch (IOException e) {
          }
          return true;
        } else {
          Log.e(TAG, "Errno: " + ex);
          return false;
        }
      } catch (InterruptedException e) {
        Log.e(TAG, "Exception: " + e.getMessage(), e);
        return false;
      }
    } catch (IOException e) {
      Log.e(TAG, "Exception: " + e.getMessage(), e);
      return false;
    }
    
  }
}
