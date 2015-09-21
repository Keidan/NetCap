package org.kei.android.phone.netcap.chooser;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

/**
 *******************************************************************************
 * @file Option.java
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
public class Option implements Comparable<Option> {
  private String   name;
  private String   data;
  private String   path;
  private Drawable icon;

  public Option(final String n, final String d, final String p, final Drawable i) {
    name = n;
    data = d;
    path = p;
    icon = i;
  }

  public String getName() {
    return name;
  }

  public String getData() {
    return data;
  }

  public String getPath() {
    return path;
  }

  public Drawable getIcon() {
    return icon;
  }

  public boolean isValid() {
    return name != null && path != null;
  }

  @SuppressLint("DefaultLocale")
  @Override
  public int compareTo(final Option o) {
    if (this.name != null)
      return this.name.toLowerCase(Locale.getDefault()).compareTo(
          o.getName().toLowerCase());
    else
      throw new IllegalArgumentException();
  }
}
