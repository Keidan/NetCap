/**
 *******************************************************************************
 * @file net-jni.c
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
#include <android/log.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <jni.h>
#include "net.h"
#include "org_kei_android_phone_jni_net_capture_PCAPHeader.h"
#include "net-jni-type.h"

#define LOG_E(TAG, fmt, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##__VA_ARGS__);
#define GET_STRING(str) (str == NULL ? "(null)" : str)
#define TOJLONG(value) (jlong)(unsigned long long)value


/**
 * Throw a JniException.
 * @param env The JNI env.
 * @param message The error message.
 */
static void __ThrowJniException(JNIEnv* env, const char* file, const char* fct, int line, const char* message);
#define ThrowJniException(env, message) __ThrowJniException(env, __FILE__, __func__, __LINE__, message)


/**
 * Initialize the java NetworkInterface object.
 * @param env JNI env.
 * @param nd The native network device.
 * @return The java object.
 */
jobject initializeNetworkInterface(JNIEnv *env, struct net_iface_s *nd);


static struct JniException jniException = { NULL, NULL, NULL };
static struct ArrayList arrayList = { NULL, NULL, NULL };
static struct NetworkInterface networkInterface = { NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL };
static struct PCAPHeader pcapHeader = { NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL };
static struct PCAPPacketHeader pcapPacketHeader = { NULL, NULL, NULL, NULL, NULL, NULL };
static struct ICapture icapture = { NULL, NULL, NULL };
static struct ILayer layer = { NULL, NULL, NULL };
static struct Ethernet ethernet = { NULL, NULL, NULL, NULL, NULL };
static struct IPv4 ip4 = { NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL };
static struct IPv6 ip6 = { NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL };
static JavaVM *java_vm = NULL;

JNIEXPORT jint JNICALL JNI_OnLoad (JavaVM * vm, void * reserved) {
  jclass tmpC = NULL;
  int attached = 0;
  java_vm = vm;
  JNIEnv* env;
  int status = (*java_vm)->GetEnv(java_vm, (void **)&env, JNI_VERSION_1_6);
  if(status < 0) {
    status = (*java_vm)->AttachCurrentThread(java_vm, &env, NULL);
    if(status < 0) return JNI_ERR;
    attached = 1;
  }
  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/JniException");
  jniException.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  jniException.constructor = (*env)->GetMethodID (env, jniException.clazz, "<init>", "(Ljava/lang/String;)V");
  jniException.setLocation = (*env)->GetMethodID (env, jniException.clazz, "jni_setLocation", "(Ljava/lang/String;Ljava/lang/String;I)V");

  tmpC = (*env)->FindClass(env, "java/util/ArrayList");
  arrayList.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  arrayList.constructor = (*env)->GetMethodID (env, arrayList.clazz, "<init>", "()V");
  arrayList.add = (*env)->GetMethodID (env, arrayList.clazz, "add", "(Ljava/lang/Object;)Z");

  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/NetworkInterface");
  networkInterface.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  networkInterface.constructor = (*env)->GetMethodID (env, networkInterface.clazz, "<init>", "()V");
  networkInterface.setName = (*env)->GetMethodID (env, networkInterface.clazz, "setName", "(Ljava/lang/String;)V");
  networkInterface.setIPv4 = (*env)->GetMethodID (env, networkInterface.clazz, "setIPv4", "(Ljava/lang/String;)V");
  networkInterface.setMac = (*env)->GetMethodID (env, networkInterface.clazz, "setMac", "(Ljava/lang/String;)V");
  networkInterface.setBroadcast = (*env)->GetMethodID (env, networkInterface.clazz, "setBroadcast", "(Ljava/lang/String;)V");
  networkInterface.setMask = (*env)->GetMethodID (env, networkInterface.clazz, "setMask", "(Ljava/lang/String;)V");
  networkInterface.setFamily = (*env)->GetMethodID (env, networkInterface.clazz, "setFamily", "(I)V");
  networkInterface.setMetric = (*env)->GetMethodID (env, networkInterface.clazz, "setMetric", "(I)V");
  networkInterface.setMTU = (*env)->GetMethodID (env, networkInterface.clazz, "setMTU", "(I)V");
  networkInterface.setFlags = (*env)->GetMethodID (env, networkInterface.clazz, "setFlags", "(I)V");
  networkInterface.setIndex = (*env)->GetMethodID (env, networkInterface.clazz, "setIndex", "(I)V");


  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/capture/PCAPHeader");
  pcapHeader.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  pcapHeader.constructor = (*env)->GetMethodID (env, pcapHeader.clazz, "<init>", "()V");
  pcapHeader.setMagicNumber = (*env)->GetMethodID (env, pcapHeader.clazz, "setMagicNumber", "(J)V");
  pcapHeader.setVersionMajor = (*env)->GetMethodID (env, pcapHeader.clazz, "setVersionMajor", "(I)V");
  pcapHeader.setVersionMinor = (*env)->GetMethodID (env, pcapHeader.clazz, "setVersionMinor", "(I)V");
  pcapHeader.setThiszone = (*env)->GetMethodID (env, pcapHeader.clazz, "setThiszone", "(I)V");
  pcapHeader.setSigfigs = (*env)->GetMethodID (env, pcapHeader.clazz, "setSigfigs", "(J)V");
  pcapHeader.setSnapLength = (*env)->GetMethodID (env, pcapHeader.clazz, "setSnapLength", "(J)V");
  pcapHeader.setNetwork = (*env)->GetMethodID (env, pcapHeader.clazz, "setNetwork", "(J)V");

  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/capture/PCAPPacketHeader");
  pcapPacketHeader.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  pcapPacketHeader.constructor = (*env)->GetMethodID (env, pcapPacketHeader.clazz, "<init>", "()V");
  pcapPacketHeader.setTsSec = (*env)->GetMethodID (env, pcapPacketHeader.clazz, "setTsSec", "(J)V");
  pcapPacketHeader.setTsUsec = (*env)->GetMethodID (env, pcapPacketHeader.clazz, "setTsUsec", "(J)V");
  pcapPacketHeader.setInclLen = (*env)->GetMethodID (env, pcapPacketHeader.clazz, "setInclLen", "(J)V");
  pcapPacketHeader.setOrigLen = (*env)->GetMethodID (env, pcapPacketHeader.clazz, "setOrigLen", "(J)V");

  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/capture/ICapture");
  icapture.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  icapture.captureProcess = (*env)->GetMethodID (env, icapture.clazz, "captureProcess", "(Lorg/kei/android/phone/jni/net/capture/CaptureFile;Lorg/kei/android/phone/jni/net/capture/PCAPPacketHeader;[B)V");
  icapture.captureEnd = (*env)->GetMethodID (env, icapture.clazz, "captureEnd", "(Lorg/kei/android/phone/jni/net/capture/CaptureFile;)V");

  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/layer/Layer");
  layer.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  layer.setLength = (*env)->GetMethodID (env, layer.clazz, "setLength", "(I)V");
  layer.setNext = (*env)->GetMethodID (env, layer.clazz, "setNext", "(Lorg/kei/android/phone/jni/net/layer/Layer;)V");

  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/layer/link/Ethernet");
  ethernet.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  ethernet.constructor = (*env)->GetMethodID (env, ethernet.clazz, "<init>", "()V");
  ethernet.setSource = (*env)->GetMethodID (env, ethernet.clazz, "setSource", "(Ljava/lang/String;)V");
  ethernet.setDestination = (*env)->GetMethodID (env, ethernet.clazz, "setDestination", "(Ljava/lang/String;)V");
  ethernet.setProto = (*env)->GetMethodID (env, ethernet.clazz, "setProto", "(I)V");

  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/layer/internet/IPv4");
  ip4.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  ip4.constructor = (*env)->GetMethodID (env, ip4.clazz, "<init>", "()V");
  ip4.setSource = (*env)->GetMethodID (env, ip4.clazz, "setSource", "(Ljava/lang/String;)V");
  ip4.setDestination = (*env)->GetMethodID (env, ip4.clazz, "setDestination", "(Ljava/lang/String;)V");
  ip4.setTOS = (*env)->GetMethodID (env, ip4.clazz, "setTOS", "(I)V");
  ip4.setTotLength = (*env)->GetMethodID (env, ip4.clazz, "setTotLength", "(I)V");
  ip4.setID = (*env)->GetMethodID (env, ip4.clazz, "setID", "(I)V");
  ip4.setFragOff = (*env)->GetMethodID (env, ip4.clazz, "setFragOff", "(I)V");
  ip4.setTTL = (*env)->GetMethodID (env, ip4.clazz, "setTTL", "(I)V");
  ip4.setProtocol = (*env)->GetMethodID (env, ip4.clazz, "setProtocol", "(I)V");
  ip4.setChecksum = (*env)->GetMethodID (env, ip4.clazz, "setChecksum", "(I)V");
  ip4.setReservedBit = (*env)->GetMethodID (env, ip4.clazz, "setReservedBit", "(Z)V");
  ip4.setDontFragment = (*env)->GetMethodID (env, ip4.clazz, "setDontFragment", "(Z)V");
  ip4.setMoreFragments = (*env)->GetMethodID (env, ip4.clazz, "setMoreFragments", "(Z)V");
  ip4.setHeaderLength = (*env)->GetMethodID (env, ip4.clazz, "setHeaderLength", "(I)V");

  tmpC = (*env)->FindClass(env, "org/kei/android/phone/jni/net/layer/internet/IPv6");
  ip6.clazz = (*env)->NewGlobalRef(env, tmpC);
  (*env)->DeleteLocalRef(env, tmpC);
  ip6.constructor = (*env)->GetMethodID (env, ip6.clazz, "<init>", "()V");
  ip6.setSource = (*env)->GetMethodID (env, ip6.clazz, "setSource", "(Ljava/lang/String;)V");
  ip6.setDestination = (*env)->GetMethodID (env, ip6.clazz, "setDestination", "(Ljava/lang/String;)V");
  ip6.setPriority = (*env)->GetMethodID (env, ip6.clazz, "setPriority", "(I)V");
  ip6.setVersion = (*env)->GetMethodID (env, ip6.clazz, "setVersion", "(I)V");
  ip6.setFlowLbl_1 = (*env)->GetMethodID (env, ip6.clazz, "setFlowLbl_1", "(I)V");
  ip6.setFlowLbl_2 = (*env)->GetMethodID (env, ip6.clazz, "setFlowLbl_2", "(I)V");
  ip6.setFlowLbl_3 = (*env)->GetMethodID (env, ip6.clazz, "setFlowLbl_3", "(I)V");
  ip6.setPayloadLen = (*env)->GetMethodID (env, ip6.clazz, "setPayloadLen", "(I)V");
  ip6.setNexthdr = (*env)->GetMethodID (env, ip6.clazz, "setNexthdr", "(I)V");
  ip6.setHopLimit = (*env)->GetMethodID (env, ip6.clazz, "setHopLimit", "(I)V");

  if(attached)
    (*java_vm)->DetachCurrentThread(java_vm);
  return JNI_VERSION_1_6;
}

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    isPCAP
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_isPCAP(JNIEnv *env, jclass clazz, jstring name) {
  const char *nativeName = (*env)->GetStringUTFChars(env, name, 0);
  int r = net_is_pcap(nativeName);
  (*env)->ReleaseStringUTFChars(env, name, nativeName);
  if(r == -1) {
    ThrowJniException(env, n_errno);
  	return JNI_FALSE;
  }
  return r ? JNI_TRUE : JNI_FALSE;
}

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    getPCAPHeader
 * Signature: (Ljava/lang/String;)Lorg/kei/android/phone/jni/net/capture/PCAPHeader;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_getPCAPHeader(JNIEnv *env, jclass clazz, jstring name) {
  const char *nativeName = (*env)->GetStringUTFChars(env, name, 0);
  pcap_hdr_t ghdr;
  int r = net_read_pcap_header(nativeName, &ghdr);
  (*env)->ReleaseStringUTFChars(env, name, nativeName);
  if(r == -1) {
	ThrowJniException(env, n_errno);
	return NULL;
  }

  jobject pcap = (*env)->NewObject(env, pcapHeader.clazz, pcapHeader.constructor);
  if(pcap == NULL) {
    ThrowJniException(env, "Unable to allocate the new org.kei.android.phone.jni.net.NetworkInterface");
    return pcap;
  }
  //LOG_E("JNICALL", "Magic: '%d', MAJOR: '%d', MINOR: '%d', thiszone: '%d', sigfigs: '%d', snaplen: %d, network: %d",
  //  ghdr.magic_number, ghdr.version_major, ghdr.version_minor, ghdr.thiszone, ghdr.sigfigs, ghdr.snaplen, ghdr.network);
  (*env)->CallVoidMethod(env, pcap, pcapHeader.setMagicNumber, TOJLONG(ghdr.magic_number));
  (*env)->CallVoidMethod(env, pcap, pcapHeader.setVersionMajor, ghdr.version_major);
  (*env)->CallVoidMethod(env, pcap, pcapHeader.setVersionMinor, ghdr.version_minor);
  (*env)->CallVoidMethod(env, pcap, pcapHeader.setThiszone, ghdr.thiszone);
  (*env)->CallVoidMethod(env, pcap, pcapHeader.setSigfigs, TOJLONG(ghdr.sigfigs));
  (*env)->CallVoidMethod(env, pcap, pcapHeader.setSnapLength, TOJLONG(ghdr.snaplen));
  (*env)->CallVoidMethod(env, pcap, pcapHeader.setNetwork, TOJLONG(ghdr.network));
  return pcap;
}

/*
 * Class:     org_kei_android_phone_jni_net_capture_CaptureFile
 * Method:    open0
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_kei_android_phone_jni_net_capture_CaptureFile_open0(JNIEnv *env, jobject thiz, jstring name) {
  const char *nativeName = (*env)->GetStringUTFChars(env, name, 0);
  int fd = open(nativeName, O_RDONLY);
  if(fd == -1) {
    sprintf(n_errno, "Unable to open the file '%s': (%d) %s", nativeName, errno, strerror(errno));
    (*env)->ReleaseStringUTFChars(env, name, nativeName);
    ThrowJniException(env, n_errno);
    return -1;
  }
  (*env)->ReleaseStringUTFChars(env, name, nativeName);
  return fd;
}

/*
 * Class:     org_kei_android_phone_jni_net_capture_CaptureFile
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_kei_android_phone_jni_net_capture_CaptureFile_close(JNIEnv *env, jobject thiz, jint fd) {
  if(fd != -1) close(fd);
}

/*
 * Class:     org_kei_android_phone_jni_net_capture_CaptureFile
 * Method:    decodeCapture
 * Signature: (ILorg/kei/android/phone/jni/net/capture/ICapture;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_kei_android_phone_jni_net_capture_CaptureFile_decodeCapture(JNIEnv *env, jobject thiz, jint fd, jobject callback) {
  pcap_hdr_t pcap_hdr;
  pcaprec_hdr_t pcaprec_hdr;
  int reads;
  unsigned int offset;

  jobject pcaprec = (*env)->NewObject(env, pcapPacketHeader.clazz, pcapPacketHeader.constructor);
  if(pcaprec == NULL) {
    ThrowJniException(env, "Unable to allocate the new org.kei.android.phone.jni.net.capture.PCAPPacketHeader");
    return JNI_FALSE;
  }

  if(lseek(fd, 0, SEEK_CUR) < sizeof(pcap_hdr_t)) {
    reads = read(fd, &pcap_hdr, sizeof(pcap_hdr_t));
    if(reads == -1 || reads != sizeof(pcap_hdr_t)) {
      (*env)->CallVoidMethod(env, callback, icapture.captureEnd, thiz);
      return JNI_FALSE;
    }
    //LOG_E("JNICALL", "Magic: '%d', MAJOR: '%d', MINOR: '%d', thiszone: '%d', sigfigs: '%d', snaplen: %d, network: %d",
  	//	  pcap_hdr.magic_number, pcap_hdr.version_major, pcap_hdr.version_minor, pcap_hdr.thiszone, pcap_hdr.sigfigs, pcap_hdr.snaplen, pcap_hdr.network);
  }
  reads = read(fd, &pcaprec_hdr, sizeof(pcaprec_hdr_t));
  if(reads == -1 || reads != sizeof(pcaprec_hdr_t)) {
    (*env)->CallVoidMethod(env, callback, icapture.captureEnd, thiz);
    return JNI_FALSE;
  }
  (*env)->CallVoidMethod(env, pcaprec, pcapPacketHeader.setTsSec, TOJLONG(pcaprec_hdr.ts_sec));
  (*env)->CallVoidMethod(env, pcaprec, pcapPacketHeader.setTsUsec, TOJLONG(pcaprec_hdr.ts_usec));
  (*env)->CallVoidMethod(env, pcaprec, pcapPacketHeader.setInclLen, TOJLONG(pcaprec_hdr.incl_len));
  (*env)->CallVoidMethod(env, pcaprec, pcapPacketHeader.setOrigLen, TOJLONG(pcaprec_hdr.orig_len));
  //LOG_E("JNICALL", "ts_sec: '%d', ts_usec: '%d', incl_len: '%d', orig_len: '%d'",
  //		  pcaprec_hdr.ts_sec, pcaprec_hdr.ts_usec, pcaprec_hdr.incl_len, pcaprec_hdr.orig_len);

  char *payload = malloc(pcaprec_hdr.incl_len);
  if((payload = malloc(pcaprec_hdr.incl_len)) == NULL) {
    sprintf(n_errno, "FATAL: Unable to alloc memory (length:%d)", pcaprec_hdr.incl_len);
    ThrowJniException(env, n_errno);
    return JNI_FALSE;
  }
  /* RAZ the buffer. */
  bzero(payload, pcaprec_hdr.incl_len);
  offset = 0;
  while(offset < pcaprec_hdr.incl_len) {
    reads = read(fd, payload + offset, pcaprec_hdr.incl_len - offset);
    if(reads == -1) break;
  	offset += reads;
  }
  jbyteArray bytes = (*env)->NewByteArray(env, pcaprec_hdr.incl_len);
  if(bytes == NULL) {
    free(payload);
    sprintf(n_errno, "FATAL: Unable to alloc memory (length:%d)", pcaprec_hdr.incl_len);
    ThrowJniException(env, n_errno);
    return JNI_FALSE;
  }
  (*env)->SetByteArrayRegion (env, bytes, 0, pcaprec_hdr.incl_len, (const jbyte *)payload);
  (*env)->CallVoidMethod(env, callback, icapture.captureProcess, thiz, pcaprec, bytes);
  free(payload);
  return JNI_TRUE;
}

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    getInterface
 * Signature: (Ljava/lang/String;)Lorg/kei/android/phone/jni/net/NetworkInterface;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_getInterface(JNIEnv *env, jclass clazz, jstring name) {
  const char *nativeName = (*env)->GetStringUTFChars(env, name, 0);
  struct net_iface_s *devices = NULL;
  if(net_list_ifaces(&devices) == -1) {
    (*env)->ReleaseStringUTFChars(env, name, nativeName);
    ThrowJniException(env, n_errno);
    return NULL;
  }
  
  struct net_iface_s *node, *tdevices = devices;
  while(tdevices) {
    node = tdevices;
    tdevices = tdevices->next;
    if(!strcmp(nativeName, node->name)) {
      if(net_read_iface(node) == -1) {
	net_release_ifaces(devices);
	(*env)->ReleaseStringUTFChars(env, name, nativeName);
	ThrowJniException(env, n_errno);
	return NULL;
      }
      net_release_ifaces(devices);
      (*env)->ReleaseStringUTFChars(env, name, nativeName);
      return initializeNetworkInterface(env, node);
    }
  }
  net_release_ifaces(devices);
  (*env)->ReleaseStringUTFChars(env, name, nativeName);
  return NULL;
}

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    getInterfaces
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_getInterfaces(JNIEnv *env, jclass clazz) {
  jobject alist = (*env)->NewObject(env, arrayList.clazz, arrayList.constructor);
  if(alist == NULL) {
    ThrowJniException(env, "Unable to allocate the new java.util.ArrayList");
    return alist;
  }
  struct net_iface_s *devices = NULL;
  if(net_list_ifaces(&devices) == -1) {
    ThrowJniException(env, n_errno);
    return alist;
  }
  struct net_iface_s *node, *tdevices = devices;
  while(tdevices) {
    node = tdevices;
    tdevices = tdevices->next;
    if(net_read_iface(node) == -1) {
      net_release_ifaces(devices);
      ThrowJniException(env, n_errno);
      return alist;
    }
    (*env)->CallBooleanMethod(env, alist, arrayList.add, initializeNetworkInterface(env, node));
  }
  net_release_ifaces(devices);
  return alist;
}

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    formatToHex
 * Signature: ([BI)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_formatToHex(JNIEnv *env, jclass clazz, jbyteArray buffer, jint offset) {
  jobject alist = (*env)->NewObject(env, arrayList.clazz, arrayList.constructor);
  if(alist == NULL) {
    ThrowJniException(env, "Unable to allocate the new java.util.ArrayList");
    return alist;
  }
  jbyte* rawjBytes = (*env)->GetByteArrayElements(env, buffer, NULL) + offset;
  //do stuff to raw bytes*
  int len = (*env)->GetArrayLength(env, buffer) - offset;
  int i = 0, max = 16, dlen = max*4 + 3, loop = len;
  unsigned char *p = (unsigned char *)rawjBytes;
  char datas [dlen]; /* spaces + \0 */
  memset(datas, 0, dlen);
  while(loop--) {
    unsigned char c = *(p++);
    sprintf(datas, "%s%02x ", datas, c);
    /* next line */
    if(i == max) {
      // add
      (*env)->CallBooleanMethod(env, alist, arrayList.add, (*env)->NewStringUTF (env, datas));
      /* re init */
      i = 0;
      memset(datas, 0, dlen);
    }
    /* next */
    else i++;
  }
  (*env)->ReleaseByteArrayElements(env, buffer, rawjBytes, 0);
  return alist;
}


/**
 * Throw a JniException.
 * @param env The JNI env.
 * @param file The file.
 * @param fct The function name.
 * @param line The line number
 * @param message The error message.
 */
static void __ThrowJniException(JNIEnv* env, const char* file, const char* fct, int line, const char* message) {
  jobject exception = (*env)->NewObject (env, jniException.clazz, jniException.constructor, (*env)->NewStringUTF(env, message));
  (*env)->CallVoidMethod (env, exception, jniException.setLocation, (*env)->NewStringUTF (env, file), (*env)->NewStringUTF (env, fct), line);
  (*env)->Throw (env, (jthrowable) exception);
}

/**
 * Initialize the java NetworkInterface object.
 * @param env JNI env.
 * @param nd The native network device.
 * @return The java object.
 */
jobject initializeNetworkInterface(JNIEnv *env, struct net_iface_s *nd) {
	  /* Get the NetworkInterface class */
  jobject ni = (*env)->NewObject(env, networkInterface.clazz, networkInterface.constructor);
  if(ni == NULL) {
    ThrowJniException(env, "Unable to allocate the new org.kei.android.phone.jni.net.NetworkInterface");
    return ni;
  }
  (*env)->CallVoidMethod(env, ni, networkInterface.setName, (*env)->NewStringUTF (env, GET_STRING(nd->name)));
  (*env)->CallVoidMethod(env, ni, networkInterface.setIPv4, (*env)->NewStringUTF (env, GET_STRING(nd->ip4)));
  (*env)->CallVoidMethod(env, ni, networkInterface.setMac, (*env)->NewStringUTF (env, GET_STRING(nd->mac)));
  (*env)->CallVoidMethod(env, ni, networkInterface.setBroadcast, (*env)->NewStringUTF (env, GET_STRING(nd->bcast)));
  (*env)->CallVoidMethod(env, ni, networkInterface.setMask, (*env)->NewStringUTF (env, GET_STRING(nd->mask)));
  (*env)->CallVoidMethod(env, ni, networkInterface.setFamily, nd->family);
  (*env)->CallVoidMethod(env, ni, networkInterface.setMetric, nd->metric);
  (*env)->CallVoidMethod(env, ni, networkInterface.setMTU, nd->mtu);
  (*env)->CallVoidMethod(env, ni, networkInterface.setFlags, nd->flags);
  (*env)->CallVoidMethod(env, ni, networkInterface.setIndex, nd->index);
  return ni;
}

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    decodeLayer
 * Signature: ([B)Lorg/kei/android/phone/jni/net/layer/Layer;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_decodeLayer(JNIEnv *env, jclass clazz, jbyteArray buffer) {
	/*struct iphdr;
	union tcp_word_hdr *utcp;// = 	(union tcp_word_hdr*)(buffer + offset);
	struct tcphdr *tcp;// = &utcp->hdr;
	struct udphdr *udp;
	struct icmphdr *icmp4;
	struct arphdrs *arps;*/

  char cbuffer_64[64];
  int buff_len = (*env)->GetArrayLength(env, buffer);
  jbyte* rawjBytes = (*env)->GetByteArrayElements(env, buffer, NULL);
  unsigned char *p = (unsigned char *)rawjBytes;

  struct ethhdr *eth = (struct ethhdr *)p;
  int size = sizeof(struct ethhdr);
  int offset = size;

  jobject jeth = (*env)->NewObject(env, ethernet.clazz, ethernet.constructor);
  (*env)->CallVoidMethod(env, jeth, layer.setLength, size);
  (*env)->CallVoidMethod(env, jeth, ethernet.setProto, ntohs(eth->h_proto));
  sprintf(cbuffer_64, "%02x:%02x:%02x:%02x:%02x:%02x", eth->h_source[0], eth->h_source[1], eth->h_source[2], eth->h_source[3], eth->h_source[4], eth->h_source[5]);
  (*env)->CallVoidMethod(env, jeth, ethernet.setSource, (*env)->NewStringUTF (env, cbuffer_64));
  sprintf(cbuffer_64, "%02x:%02x:%02x:%02x:%02x:%02x", eth->h_dest[0], eth->h_dest[1], eth->h_dest[2], eth->h_dest[3], eth->h_dest[4], eth->h_dest[5]);
  (*env)->CallVoidMethod(env, jeth, ethernet.setDestination, (*env)->NewStringUTF (env, cbuffer_64));

  if(ntohs(eth->h_proto) == ETH_P_IP || ntohs(eth->h_proto) == ETH_P_IPV6) {
    jobject jip;
    struct iphdr *ipv4 = (struct iphdr*)(p + offset);
    unsigned char protocol = 0;
    if(ipv4->version == 4) {
      size = sizeof(struct iphdr);
      offset = size;
      jip = (*env)->NewObject(env, ip4.clazz, ip4.constructor);
      (*env)->CallVoidMethod(env, jip, layer.setLength, size);
      inet_ntop(AF_INET, &ipv4->saddr, cbuffer_64, INET_ADDRSTRLEN);
      (*env)->CallVoidMethod(env, jip, ip4.setSource, (*env)->NewStringUTF (env, cbuffer_64));
      inet_ntop(AF_INET, &ipv4->daddr, cbuffer_64, INET_ADDRSTRLEN);
      (*env)->CallVoidMethod(env, jip, ip4.setDestination, (*env)->NewStringUTF (env, cbuffer_64));
      (*env)->CallVoidMethod(env, jip, ip4.setTOS, ntohs(ipv4->tos));
      (*env)->CallVoidMethod(env, jip, ip4.setTotLength, ntohs(ipv4->tot_len));
      (*env)->CallVoidMethod(env, jip, ip4.setID, ipv4->id);
      (*env)->CallVoidMethod(env, jip, ip4.setFragOff, ntohs(ipv4->frag_off));
      (*env)->CallVoidMethod(env, jip, ip4.setTTL, ipv4->ttl);
      (*env)->CallVoidMethod(env, jip, ip4.setProtocol, ipv4->protocol);
      (*env)->CallVoidMethod(env, jip, ip4.setChecksum, ipv4->check);
      (*env)->CallVoidMethod(env, jip, ip4.setReservedBit, !!(ipv4->id&IP_RF));
      (*env)->CallVoidMethod(env, jip, ip4.setDontFragment, !!(ipv4->id&IP_DF));
      (*env)->CallVoidMethod(env, jip, ip4.setMoreFragments, !!(ipv4->id&IP_MF));
      (*env)->CallVoidMethod(env, jip, ip4.setHeaderLength, (int)(ipv4->ihl + sizeof(struct iphdr)));
      protocol = ipv4->protocol;
    } else {
      struct ipv6hdr *ipv6 = (struct ipv6hdr*)(p + offset);
	  size = sizeof(struct ipv6hdr);
	  offset = size;
	  jip = (*env)->NewObject(env, ip6.clazz, ip6.constructor);
      (*env)->CallVoidMethod(env, jip, layer.setLength, size);
      inet_ntop(AF_INET6, &ipv6->saddr, cbuffer_64, INET6_ADDRSTRLEN);
      (*env)->CallVoidMethod(env, jip, ip6.setSource, (*env)->NewStringUTF (env, cbuffer_64));
      inet_ntop(AF_INET6, &ipv6->daddr, cbuffer_64, INET6_ADDRSTRLEN);
      (*env)->CallVoidMethod(env, jip, ip6.setDestination, (*env)->NewStringUTF (env, cbuffer_64));
      (*env)->CallVoidMethod(env, jip, ip6.setPriority, ipv6->priority);
      (*env)->CallVoidMethod(env, jip, ip6.setVersion, ipv6->version);
      (*env)->CallVoidMethod(env, jip, ip6.setFlowLbl_1, ipv6->flow_lbl[0]);
      (*env)->CallVoidMethod(env, jip, ip6.setFlowLbl_2, ipv6->flow_lbl[1]);
      (*env)->CallVoidMethod(env, jip, ip6.setFlowLbl_3, ipv6->flow_lbl[2]);
      (*env)->CallVoidMethod(env, jip, ip6.setPayloadLen, ipv6->payload_len);
      (*env)->CallVoidMethod(env, jip, ip6.setNexthdr, ipv6->nexthdr);
      (*env)->CallVoidMethod(env, jip, ip6.setHopLimit, ipv6->hop_limit);
      protocol = ipv6->hop_limit;
    }
    (*env)->CallVoidMethod(env, jeth, layer.setNext, jip);
  }
  return jeth;
}

