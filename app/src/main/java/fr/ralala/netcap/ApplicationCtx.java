package fr.ralala.netcap;

import android.app.Application;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import fr.ralala.netcap.net.layer.Layer;
import fr.ralala.netcap.ui.utils.FabHelper;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class ApplicationCtx extends Application {
  private Layer mCurrentLayer;
  private String mId;
  private FabHelper mFabHelper = null;

  public void onCreate() {
    super.onCreate();
    mFabHelper = new FabHelper();
  }

  public FabHelper getFabHelper() {
    return mFabHelper;
  }

  public Layer getLayer() {
    return mCurrentLayer;
  }

  public void setLayer(final Layer currentLayer) {
    mCurrentLayer = currentLayer;
  }

  /**
   * @return the id
   */
  public String getId() {
    return mId;
  }

  /**
   * @param id the id to set
   */
  public void setId(final String id) {
    mId = id;
  }

  public static void startActivity(final AppCompatActivity a, final Layer currentLayer, final String id,
                                   final Class<?> clazz) {
    final ApplicationCtx app = (ApplicationCtx) a.getApplicationContext();
    app.setLayer(currentLayer);
    app.setId(id);
    final Intent i = new Intent(a.getApplicationContext(), clazz);
    a.startActivity(i);
  }
}
