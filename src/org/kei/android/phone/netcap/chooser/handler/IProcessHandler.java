package org.kei.android.phone.netcap.chooser.handler;

/**
 *******************************************************************************
 * @file IProcessHandler.java
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
public interface IProcessHandler {

  public ErrorStatus doCompute(Object userObject);

  public void onSuccess();

  public void onCancel();

  public void onError();
}
