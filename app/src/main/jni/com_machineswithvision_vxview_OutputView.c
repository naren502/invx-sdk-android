#include "com_machineswithvision_vxview_OutputView.h"

#include <android/log.h>

JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_OutputView_processBytes
   (JNIEnv *env, jobject obj, jobject buffer)
{


    char* ptr = (char*)(*env)->GetDirectBufferAddress(env,buffer);

    if (ptr) {
        jlong size = (*env)->GetDirectBufferCapacity(env,buffer);

        int i;
        for(i=0;i<size;i++) ptr[i]=(char)(255-(0xFF&(int)ptr[i]));
     } else {
        __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ptr is NULL!D\n");
     }
}