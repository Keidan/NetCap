package fr.ralala.netcap.net.capture;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * ******************************************************************************
 * <p><b>Project NetCap</b><br/> </p>
 *
 * @author Keidan
 * ******************************************************************************
 */
public class CaptureFile {
  private RandomAccessFile mRaf = null;

  public CaptureFile(final String filename) throws IOException {
    open(filename);
  }

  public CaptureFile() {
  }

  /**
   * Open the capture engine.
   */
  public void open(final String filename) throws IOException {
    mRaf = new RandomAccessFile(new File(filename), "r");
  }

  public void close() {
    if (mRaf != null) {
      try {
        mRaf.close();
      } catch (IOException ignored) {
      }
      mRaf = null;
    }
  }

  /**
   * Process the capture (this method bloc)
   *
   * @param ic Capture callback.
   */
  public boolean process(final ICapture ic) throws IOException {
    PCAPPacketHeader pph = new PCAPPacketHeader();

    if (mRaf.getFilePointer() < 24) {
      try {
        mRaf.seek(24);
      } catch (IOException e) {
        ic.captureEnd(this);
        return false;
      }
    }
    try {
      pph.setTsSec(getIntFrom(mRaf));
      pph.setTsUsec(getIntFrom(mRaf));
      pph.setInclLen(getIntFrom(mRaf));
      pph.setOrigLen(getIntFrom(mRaf));
    } catch (IOException e) {
      ic.captureEnd(this);
      return false;
    }
    if (pph.getInclLen() == 0) {
      ic.captureEnd(this);
      return false;
    }
    byte[] bytes = new byte[(int) pph.getInclLen()];

    int offset = 0;
    while (offset < pph.getInclLen()) {
      int reads = mRaf.read(bytes, offset, (int) (pph.getInclLen() - offset));
      if (reads == -1) break;
      offset += reads;
    }
    ic.captureProcess(this, pph, bytes);
    return true;
  }


  private int getIntFrom(RandomAccessFile raf) throws IOException {
    byte[] temp4 = new byte[4];
    raf.read(temp4);
    return ((temp4[3] & 0xFF) << 24) | ((temp4[2] & 0xFF) << 16)
        | ((temp4[1] & 0xFF) << 8) | (temp4[0] & 0xFF);
  }

  /**
   * Get the PCAP header from the file.
   *
   * @return PCAPHeader
   */
  public static PCAPHeader getPCAPHeader(final String filename) throws IOException {
    PCAPHeader ph = new PCAPHeader();
    File file = new File(filename);
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    ph.setMagicNumber(raf.readInt() & 0xFFFFFFFFL);
    ph.setVersionMajor(raf.readShort() & 0xFFFF);
    ph.setVersionMinor(raf.readShort() & 0xFFFF);
    ph.setThiszone(raf.readInt());
    ph.setSigfigs(raf.readInt() & 0xFFFFFF);
    ph.setSnapLength(raf.readInt() & 0xFFFFFF);
    ph.setNetwork(raf.readInt() & 0xFFFFFF);
    raf.close();
    return ph;
  }

  /**
   * Test if the current file is a PCAP file.
   *
   * @return boolean
   */
  public static boolean isPCAP(final String filename) throws IOException {
    File file = new File(filename);
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    long magic_number = raf.readInt() & 0xFFFFFFFFL;
    raf.close();
    return magic_number == (PCAPHeader.PCAP_MAGIC_NATIVE & 0xFFFFFFFFL) || magic_number == (PCAPHeader.PCAP_MAGIC_SWAPPED & 0xFFFFFFFFL);
  }
}
