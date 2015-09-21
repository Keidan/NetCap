package org.kei.android.phone.netcap.utils.fx;

import org.kei.android.phone.netcap.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;

/**
 *******************************************************************************
 * @file Fx.java
 * @author Keidan
 * @date 10/09/2015
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
public class Fx {
  
  
  public static Drawable getDrawable(final Activity owner, final int id) {
    return owner.getResources().getDrawable(id, null);
  }

  /*public static Drawable getIconByFile(final Activity owner, final String sfile) {
    final File file = new File(sfile);
    Uri uri = Uri.fromFile(file);
    final Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(uri);
    intent.setType("image/png");

    final List<ResolveInfo> matches = owner.getPackageManager()
        .queryIntentActivities(intent, 0);
    for (ResolveInfo match : matches) {
      return match.loadIcon(owner.getPackageManager());
    }
    return null;
  }*/

  public static void updateTransition(final Activity owner, final boolean in) {
    if (in)
      owner.overridePendingTransition(R.anim.enter_in, R.anim.enter_out);
    else
      owner.overridePendingTransition(R.anim.leave_in, R.anim.leave_out);
  }
  
}
