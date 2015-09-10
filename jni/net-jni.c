/**
 *******************************************************************************
 * @file net-jni.c
 * @author Keidan
 * @date 07/09/2015
 * @par Project
 * NetCap
 *
 * @par Copyright
 * Copyright 2011-2013 Keidan, all right reserved
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
#include <string.h>
#include <jni.h>
#include "net.h"
#define LOG_E(TAG, fmt, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##__VA_ARGS__);
#define GET_STRING(str) (str == NULL ? "(null)" : str)


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
    jmethodID getName;
    jmethodID getIPv4;
    jmethodID getMac;
    jmethodID getBroadcast;
    jmethodID getMask;
    jmethodID getFamily;
    jmethodID getMetric;
    jmethodID getMTU;
    jmethodID getFlags;
    jmethodID getIndex;
};

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
static struct NetworkInterface networkInterface = { NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL };
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
  networkInterface.getName = (*env)->GetMethodID (env, networkInterface.clazz, "getName", "()Ljava/lang/String;");
  networkInterface.getIPv4 = (*env)->GetMethodID (env, networkInterface.clazz, "getIPv4", "()Ljava/lang/String;");
  networkInterface.getMac = (*env)->GetMethodID (env, networkInterface.clazz, "getMac", "()Ljava/lang/String;");
  networkInterface.getBroadcast = (*env)->GetMethodID (env, networkInterface.clazz, "getBroadcast", "()Ljava/lang/String;");
  networkInterface.getMask = (*env)->GetMethodID (env, networkInterface.clazz, "getMask", "()Ljava/lang/String;");
  networkInterface.getFamily = (*env)->GetMethodID (env, networkInterface.clazz, "getFamily", "()I");
  networkInterface.getMetric = (*env)->GetMethodID (env, networkInterface.clazz, "getMetric", "()I");
  networkInterface.getMTU = (*env)->GetMethodID (env, networkInterface.clazz, "getMTU", "()I");
  networkInterface.getFlags = (*env)->GetMethodID (env, networkInterface.clazz, "getFlags", "()I");
  networkInterface.getIndex = (*env)->GetMethodID (env, networkInterface.clazz, "getIndex", "()I");

  if(attached)
    (*java_vm)->DetachCurrentThread(java_vm);
  return JNI_VERSION_1_6;
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
 * Method:    setInterface
 * Signature: (Lorg/kei/android/phone/jni/net/NetworkInterface;)V
 */
JNIEXPORT void JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_setInterface(JNIEnv *env, jclass clazz, jobject iface) {

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
 * Method:    decodeLayer
 * Signature: (I[BI)Lorg/kei/android/phone/jni/net/layer/Layer;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_decodeLayer(JNIEnv *env, jclass clazz, jint type, jbyteArray buffer, jint offset) {
  return NULL;
}

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    formatToHex
 * Signature: ([BI)Ljava/lang/StringBuilder;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_formatToHex(JNIEnv *env, jclass clazz, jbyteArray buffer, jint offset) {
  return NULL;
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
  LOG_E("PLOP", "Name: '%s', IP: '%s', MAC: '%s', BCAST: '%s', MASK: '%s', Family: %d, Metric: %d, MTU: %d, Flags: %d, Index: %d",
		  	  nd->name, nd->ip4, nd->mac, nd->bcast, nd->mask, nd->family, nd->metric, nd->mtu, nd->flags, nd->index);
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
