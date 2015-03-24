//
//  com_machineswithvision_openvx_JOVX.h
//
//  Created by Anthony Ashbrook on 24/03/2015.
//
//  Copyright (c) 2015 Machines with Vision. All rights reserved.
//
// THE MATERIALS ARE PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
// MATERIALS OR THE USE OR OTHER DEALINGS IN THE MATERIALS.
//

#include <jni.h>
/* Header for class com_machineswithvision_openvx_JOVX */

#ifndef _Included_com_machineswithvision_openvx_JOVX
#define _Included_com_machineswithvision_openvx_JOVX
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_machineswithvision_openvx_JOVX
 * Method:    jovxCreateContext
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_openvx_JOVX_jovxCreateContext
  (JNIEnv *, jclass);

/*
 * Class:     com_machineswithvision_openvx_JOVX
 * Method:    processBytes
 * Signature: (Ljava/nio/ByteBuffer;III)V
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_openvx_JOVX_processBytes
  (JNIEnv *, jclass, jobject, jint, jint, jint);

/*
 * Class:     com_machineswithvision_openvx_JOVX
 * Method:    jovxReleaseContext
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_openvx_JOVX_jovxReleaseContext
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
