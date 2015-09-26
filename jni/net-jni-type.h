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

struct ILayer {
    jclass clazz;
    jmethodID setLayerLength;
    jmethodID setNext;
};

struct Ethernet {
    jclass clazz;
    jmethodID constructor;
    jmethodID setSource;
    jmethodID setDestination;
    jmethodID setProto;
};

struct IPv4 {
    jclass clazz;
    jmethodID constructor;
    jmethodID setSource;
    jmethodID setDestination;
    jmethodID setTOS;
    jmethodID setTotLength;
    jmethodID setID;
    jmethodID setFragOff;
    jmethodID setTTL;
    jmethodID setProtocol;
    jmethodID setChecksum;

    jmethodID setReservedBit;
    jmethodID setDontFragment;
    jmethodID setMoreFragments;
    jmethodID setHeaderLength;
};

struct IPv6 {
    jclass clazz;
    jmethodID constructor;
    jmethodID setSource;
    jmethodID setDestination;
    jmethodID setPriority;
    jmethodID setVersion;
    jmethodID setFlowLbl_1;
    jmethodID setFlowLbl_2;
    jmethodID setFlowLbl_3;
    jmethodID setPayloadLen;
    jmethodID setNexthdr;
    jmethodID setHopLimit;
};

struct UDP {
    jclass clazz;
    jmethodID constructor;
    jmethodID setSource;
    jmethodID setDestination;
    jmethodID setLength;
    jmethodID setChecksum;
};

struct TCP {
    jclass clazz;
    jmethodID constructor;
    jmethodID setSource;
    jmethodID setDestination;
	jmethodID setCWR;
	jmethodID setECE;
	jmethodID setURG;
	jmethodID setACK;
	jmethodID setPSH;
	jmethodID setRST;
	jmethodID setSYN;
	jmethodID setFIN;
	jmethodID setSeq;
	jmethodID setAckSeq;
	jmethodID setWindow;
	jmethodID setCheck;
	jmethodID setUrgPtr;
};

struct Payload {
    jclass clazz;
    jmethodID constructor;
    jmethodID setDatas;
};

struct ARP {
    jclass clazz;
    jmethodID constructor;
    jmethodID setFormatOfHardwareAddress;
    jmethodID setFormatOfProtocolAddress;
    jmethodID setLengthOfHardwareAddress;
    jmethodID setLengthOfProtocolAddress;
    jmethodID setOpcode;
    jmethodID setSenderHardwareAddress;
    jmethodID setSenderIPAddress;
    jmethodID setTargetHardwareAddress;
    jmethodID setTargetIPAddress;
};

struct DNS {
    jclass clazz;
    jmethodID constructor;
    jmethodID setID;
    jmethodID setRD;
    jmethodID setTC;
    jmethodID setAA;
    jmethodID setOpcode;
    jmethodID setQR;
    jmethodID setRCode;
    jmethodID setCD;
    jmethodID setAD;
    jmethodID setZ;
    jmethodID setRA;
    jmethodID setQCount;
    jmethodID setAnsCount;
    jmethodID setAuthCount;
    jmethodID setAddCount;
};

#endif /* __NET_JNI_TYPE_H__ */
