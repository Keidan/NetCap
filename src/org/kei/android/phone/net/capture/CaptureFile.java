package org.kei.android.phone.net.capture;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
  private RandomAccessFile raf = null;

  public CaptureFile(final String filename) throws IOException {
    open(filename);
  }
  
  public CaptureFile() {
  }

  /**
   * Open the capture engine.
   */
  public void open(final String filename) throws IOException {
    raf = new RandomAccessFile(new File(filename), "r");
  }

  public void close() {
    if(raf != null) {
      try { raf.close(); } catch(IOException e) { }
      raf = null;
    }
  }

  /**
   * Process the capture (this method bloc)
   * @param ic
   *          Capture callback.
   */
  public boolean process(final ICapture ic) throws IOException {
    PCAPPacketHeader pph = new PCAPPacketHeader();

    if(raf.getFilePointer() < 24) {
      try {
        raf.seek(24);
      } catch(IOException e) { 
        ic.captureEnd(this);
        return false; 
      }
    }
    try {
      pph.setTsSec(getIntFrom(raf));
      pph.setTsUsec(getIntFrom(raf));
      pph.setInclLen(getIntFrom(raf));
      pph.setOrigLen(getIntFrom(raf));
    } catch(IOException e) { 
      ic.captureEnd(this);
      return false; 
    }
    if(pph.getInclLen() == 0) {
      ic.captureEnd(this);
      return false; 
    }
    byte [] bytes = new byte[(int)pph.getInclLen()];
    for(int i = 0; i < bytes.length; i++) bytes[i] = 0;

    int offset = 0;
    while(offset < pph.getInclLen()) {
      int reads = raf.read(bytes, offset, (int)(pph.getInclLen() - offset));
      if(reads == -1) break;
      offset += reads;
    }
    ic.captureProcess(this, pph, bytes);
    return true;
  }
  

  private int getIntFrom(RandomAccessFile raf) throws IOException {
    byte temp4 [] = new byte[4];
    raf.read(temp4);
    return ((temp4[3] & 0xFF) << 24) | ((temp4[2] & 0xFF) << 16)
        | ((temp4[1] & 0xFF) << 8) | (temp4[0] & 0xFF);
  }

  /**
   * Get the PCAP header from the file.
   * 
   * @return {@link org.kei.android.phone.jni.net.captrue.PCAPHeader}
   * @throws JniException
   *           If an exception has occurred.
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
   * @throws JniException
   *           If an exception has occurred.
   */
  public static boolean isPCAP(final String filename) throws IOException {
    File file = new File(filename);
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    long magic_number = raf.readInt() & 0xFFFFFFFFL;
    raf.close();
    if(magic_number != (PCAPHeader.PCAP_MAGIC_NATIVE & 0xFFFFFFFFL) && magic_number != (PCAPHeader.PCAP_MAGIC_SWAPPED & 0xFFFFFFFFL))
      return false;
    return true;
  }
}
