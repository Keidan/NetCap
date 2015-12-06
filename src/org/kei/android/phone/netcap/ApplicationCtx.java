package org.kei.android.phone.netcap;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.view.EffectActivity;
import org.kei.android.phone.net.layer.Layer;

import android.app.Activity;
import android.app.Application;

/**
 *******************************************************************************
 * @file ApplicationCtx.java
 * @author Keidan
 * @date 30/09/2015
 * @par Project NetCap
 *
 * @par Copyright 2015 Keidan, all right reserved
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
public class ApplicationCtx extends Application {
  private Layer currentLayer;
  private String id;
  
  public Layer getLayer() {
    return currentLayer;
  }

  public void setLayer(final Layer currentLayer) {
    this.currentLayer = currentLayer;
  }
  
  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final String id) {
    this.id = id;
  }

  public static void startActivity(final EffectActivity a, final Layer currentLayer, final String id, 
      final Class<?> clazz) {
    final ApplicationCtx app = (ApplicationCtx) a.getApplicationContext();
    app.setLayer(currentLayer);
    app.setId(id);
    Tools.switchTo(a, clazz);
  }

  public static Layer getAppLayer(final Activity a) {
    final ApplicationCtx app = (ApplicationCtx) a.getApplicationContext();
    return app.getLayer();
  }

  public static String getAppId(final Activity a) {
    final ApplicationCtx app = (ApplicationCtx) a.getApplicationContext();
    return app.getId();
  }
}
