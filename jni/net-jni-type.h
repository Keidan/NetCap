/**
 *******************************************************************************
 * @file net-jni-type.h
 * @author Keidan
 * @date 07/09/2015
 * @par Project
 * NetCap
 *
 * @par Copyright
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
#ifndef __NET_JNI_TYPE_H__
#define __NET_JNI_TYPE_H__

#include <jni.h>


struct JniException{
    jclass clazz;
    jmethodID constructor;
    jmethodID setLocation;
};

struct ArrayList {
    jclass clazz;
    jmethodID constructor;
    jmethodID add;
};

struct NetworkInterface {
    jclass clazz;
    jmethodID constructor;
    jmethodID setName;
    jmethodID setIPv4;
    jmethodID setMac;
    jmethodID setBroadcast;
    jmethodID setMask;
    jmethodID setFamily;
    jmethodID setMetric;
    jmethodID setMTU;
    jmethodID setFlags;
    jmethodID setIndex;
};
struct PCAPHeader {
    jclass clazz;
    jmethodID constructor;
    jmethodID setMagicNumber;
    jmethodID setVersionMajor;
    jmethodID setVersionMinor;
    jmethodID setThiszone;
    jmethodID setSigfigs;
    jmethodID setSnapLength;
    jmethodID setNetwork;
};
struct PCAPPacketHeader {
    jclass clazz;
    jmethodID constructor;
    jmethodID setTsSec;
    jmethodID setTsUsec;
    jmethodID setInclLen;
    jmethodID setOrigLen;
};
struct ICapture {
    jclass clazz;
    jmethodID captureProcess;
    jmethodID captureEnd;
};

#endif /* __NET_JNI_TYPE_H__ */
