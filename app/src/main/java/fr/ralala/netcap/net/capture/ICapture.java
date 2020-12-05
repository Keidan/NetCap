package fr.ralala.netcap.net.capture;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public interface ICapture {
  /**
   * Capture process callback.
   *
   * @param capture Current instance.
   * @param pheader PCAP Packet Header.
   * @param layer   packet layer.
   */
  void captureProcess(final CaptureFile capture, final PCAPPacketHeader pheader, final byte[] layer);

  void captureEnd(final CaptureFile capture);
}
