package org.kei.android.phone.net.capture;

/**
 *******************************************************************************
 * @file ICapture.java
 * @author Keidan
 * @date 11/09/2015
 * @par Project NetCap
 *
 * @par  
 *      Copyright 2015 Keidan, all right reserved
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
public interface ICapture {
  /**
   * Capture process callback.
   * 
   * @param capture
   *          Current instance.
   * @param pheader
   *          PCAP Packet Header.
   * @param layer
   *          packet layer.
   */
  public void captureProcess(final CaptureFile capture, final PCAPPacketHeader pheader, final byte[] layer);
  
  public void captureEnd(final CaptureFile capture);
}
