package org.kei.android.phone.jni;

/**
 *******************************************************************************
 * @file JniException.java
 * @author Keidan
 * @date 07/09/2015
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
public class JniException extends Exception {
  private static final long serialVersionUID = 4560941556321613651L;
  
  public JniException(String message) {
    super(message);
  }

  /**
   * Method called from the JNI code, when an exception has occurred
   * @param file The file name.
   * @param fctName The function name.
   * @param line The line number.
   */
  public void jni_setLocation(final String file, final String fctName, int line) {
    final StackTraceElement[] cStack = getStackTrace();
    final StackTraceElement[] nStack = new StackTraceElement[cStack.length + 1];
    System.arraycopy(cStack, 0, nStack, 1, cStack.length);
    int idx;
    String tFile = file.replace('\\', '/');
    if ((idx = tFile.lastIndexOf('/')) > -1)
      tFile = tFile.substring(idx + 1);
    nStack[0] = new StackTraceElement("<native>", fctName, tFile, line);
    setStackTrace(nStack);
  }
  
}
