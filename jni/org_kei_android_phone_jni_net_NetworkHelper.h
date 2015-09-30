/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_kei_android_phone_jni_net_NetworkHelper */

#ifndef _Included_org_kei_android_phone_jni_net_NetworkHelper
#define _Included_org_kei_android_phone_jni_net_NetworkHelper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    getInterface
 * Signature: (Ljava/lang/String;)Lorg/kei/android/phone/jni/net/NetworkInterface;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_getInterface
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    getInterfaces
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_getInterfaces
  (JNIEnv *, jclass);

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    isPCAP
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_isPCAP
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    getPCAPHeader
 * Signature: (Ljava/lang/String;)Lorg/kei/android/phone/jni/net/capture/PCAPHeader;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_getPCAPHeader
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_kei_android_phone_jni_net_NetworkHelper
 * Method:    decodeLayer
 * Signature: ([B)Lorg/kei/android/phone/jni/net/layer/Layer;
 */
JNIEXPORT jobject JNICALL Java_org_kei_android_phone_jni_net_NetworkHelper_decodeLayer
  (JNIEnv *, jclass, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
