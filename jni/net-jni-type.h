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

#define addPayload(buff_len, offset, p, owner) ({										\
  if((buff_len - offset) > 0) {															\
	  int l = (buff_len - offset);														\
	  jobject jpayload = (*env)->NewObject(env, payload.clazz, payload.constructor);	\
	  jbyteArray bytes = (*env)->NewByteArray(env, l);									\
	  (*env)->SetByteArrayRegion (env, bytes, 0, l, (const jbyte *)(p + offset));		\
	  (*env)->CallVoidMethod(env, jpayload, payload.setDatas, bytes);					\
	  (*env)->CallVoidMethod(env, owner, layer.setNext, jpayload);						\
	  offset += (buff_len - offset);													\
  }																						\
})

#define addDNSEntry(count, p, offset, cbuffer_64, jdns, add) ({													\
  if(ntohs(count)) {																							\
    int n;																										\
    unsigned short tempS_1, tempS_2;																			\
    unsigned int tempI_1,tempI_2;																				\
    for(n = 0; n < ntohs(count); n++) {																			\
      tempS_1 = *((unsigned short*)(p + offset));																\
      offset += sizeof(unsigned short);																			\
      struct dns_query_entry_s *e = (struct dns_query_entry_s*)(p + offset);									\
      offset += sizeof(struct dns_query_entry_s);																\
      tempI_1 = *((unsigned int*)(p + offset));																	\
      offset += sizeof(unsigned int);																			\
      tempS_2 = *((unsigned short*)(p + offset));																\
      offset += sizeof(unsigned short);																			\
      tempI_2 = *((unsigned int*)(p + offset));																	\
      offset += sizeof(unsigned int);																			\
      jobject jdnsEntry = (*env)->NewObject(env, dnsEntry.clazz, dnsEntry.constructor);							\
      (*env)->CallVoidMethod(env, jdnsEntry, dnsEntry.setNameOffset, ntohs(tempS_1));							\
      (*env)->CallVoidMethod(env, jdnsEntry, dnsEntry.setType, ntohs(e->type));									\
      (*env)->CallVoidMethod(env, jdnsEntry, dnsEntry.setClazz, ntohs(e->clazz));								\
      (*env)->CallVoidMethod(env, jdnsEntry, dnsEntry.setTTL, ntohl(tempI_1));									\
      (*env)->CallVoidMethod(env, jdnsEntry, dnsEntry.setDataLength, ntohs(tempS_2));							\
      if(ntohs(tempS_2) == 4) { 																				\
        inet_ntop(AF_INET, &tempI_2, cbuffer_64, INET_ADDRSTRLEN);												\
        (*env)->CallVoidMethod(env, jdnsEntry, dnsEntry.setAddress, (*env)->NewStringUTF (env, cbuffer_64));	\
      } else {																									\
    	  /*unsigned int l = ntohs(tempS_2);*/																		\
      	  /*offset -= sizeof(unsigned int);*/																		\
    	  /*unsigned char bu[l + 1];*/																				\
    	  /*bzero(bu, l + 1);*/																						\
		  /*memcpy(bu, (p + offset), l - 1);*/																			\
	      /*net_dns_normalize_name(bu);*/																			\
		  /*offset += l;*/																				\
          /*(*env)->CallVoidMethod(env, jdnsEntry, dnsEntry.setAddress, (*env)->NewStringUTF (env, (const char*)bu));*/\
      }																											\
      (*env)->CallVoidMethod(env, jdns, add, jdnsEntry);														\
    }																											\
  }																												\
})


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
    jmethodID setIdent;
    jmethodID setFlags;
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
    jmethodID setZero;
    jmethodID setRA;
    jmethodID setQCount;
    jmethodID setAnsCount;
    jmethodID setAuthCount;
    jmethodID setAddCount;
    jmethodID addQuery;
    jmethodID addAnswer;
    jmethodID addAuthority;
    jmethodID addAdditional;
};

struct DNSEntry {
    jclass clazz;
    jmethodID constructor;
    jmethodID setNameOffset;
    jmethodID setName;
	jmethodID setType;
	jmethodID setClazz;
	jmethodID setTTL;
	jmethodID setDataLength;
	jmethodID setAddress;
};

struct IGMP {
    jclass clazz;
    jmethodID constructor;
    jmethodID setType;
	jmethodID setMaxRespTime;
	jmethodID setChecksum;
	jmethodID setGroupAdress;
	jmethodID setResv;
	jmethodID setS;
	jmethodID setQRV;
	jmethodID setQQIC;
	jmethodID setNumberOfSources;
	jmethodID addSourceAdress;
};
#endif /* __NET_JNI_TYPE_H__ */
