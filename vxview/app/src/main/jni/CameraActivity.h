//
//  com_machineswithvision_vxview_CameraActivity.h
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
/* Header for class com_machineswithvision_vxview_CameraActivity */

#ifndef _Included_com_machineswithvision_vxview_CameraActivity
#define _Included_com_machineswithvision_vxview_CameraActivity
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_machineswithvision_vxview_CameraActivity
 * Method:    createVXContext
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_CameraActivity_createVXContext
  (JNIEnv *, jclass);

/*
 * Class:     com_machineswithvision_vxview_CameraActivity
 * Method:    processBytes
 * Signature: (Ljava/nio/ByteBuffer;II)V
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_CameraActivity_processBytes
  (JNIEnv *, jclass, jobject, jint, jint);

/*
 * Class:     com_machineswithvision_vxview_CameraActivity
 * Method:    releaseVXContext
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_CameraActivity_releaseVXContext
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
