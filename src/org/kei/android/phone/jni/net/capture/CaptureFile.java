package org.kei.android.phone.jni.net.capture;

import org.kei.android.phone.jni.JniException;
import org.kei.android.phone.jni.net.NetworkHelper;

/**
 *******************************************************************************
 * @file ICapture.java
 * @author Keidan
 * @date 11/09/2015
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
public class CaptureFile {
  private int       fd        = -1;
  private PCAPHeader header = null;

  public CaptureFile(final String filename) throws JniException {
    open(filename);
  }
  
  public CaptureFile() {
  }
  

  /**
   * Open the capture engine.
   */
  public void open(final String filename) throws JniException {
    fd = open0(filename);
    header = NetworkHelper.getPCAPHeader(filename);
  }

  public void close() {
    try { close(fd); } catch(JniException e) { }
    fd = -1;
  }

  /**
   * Process the capture (this method bloc)
   * @param ic
   *          Capture callback.
   */
  public boolean process(final ICapture ic) throws JniException {
    return decodeCapture(fd, ic);
  }

  /**
   * Open the capture engine.
   */
  private native int open0(final String filename) throws JniException;

  /**
   * Close the capture engine.
   */
  private native void close(final int fd) throws JniException;

  /**
   * Decode the capture (this method bloc)
   *
   * @param fd
   *          File descriptor.
   * @param ic
   *          Capture callback.
   */
  private native boolean decodeCapture(final int fd, final ICapture ic) throws JniException;
}
