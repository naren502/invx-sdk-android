//
//  com_machineswithvision_openvx_JOVX.c
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

#include "com_machineswithvision_openvx_JOVX.h"

#include <android/log.h>
#include <stdio.h>
#include "VX/vx.h"

void vx_test_canny(int width,int height,int depth,void* bytes);

vx_context context = 0;
void *inputBuffer = 0;
void *outputBuffer = 0;
vx_graph graph = 0;
vx_image hframe = 0;
vx_image edges = 0;
vx_image dx = 0;
vx_image dy = 0;
vx_image mag = 0;

JNIEXPORT void JNICALL Java_com_machineswithvision_openvx_JOVX_jovxCreateContext
  (JNIEnv *env, jclass cls)
{
    context = vxCreateContext();
}

JNIEXPORT void JNICALL Java_com_machineswithvision_openvx_JOVX_processBytes
   (JNIEnv *env, jclass cls, jobject buffer, jint width, jint height, jint depth)
{


    void* ptr = (*env)->GetDirectBufferAddress(env,buffer);

    if (ptr) {
        vx_test_canny(width, height, depth, ptr);
     } else {
        __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ptr is NULL!D\n");
     }
}

JNIEXPORT void JNICALL Java_com_machineswithvision_openvx_JOVX_jovxReleaseContext
  (JNIEnv *env, jclass cls)
{

    if (hframe) {
        vxReleaseImage(&hframe);
        hframe = 0;
    }

    if (edges) {
        vxReleaseImage(&edges);
        edges = 0;
    }

    if (dx) {
        vxReleaseImage(&dx);
        dx = 0;
    }

    if (dy) {
        vxReleaseImage(&dy);
        dy = 0;
    }

    if (mag) {
        vxReleaseImage(&mag);
        mag = 0;
    }

    if (context) {
        vxReleaseContext(&context);
        context = 0;
    }

    if (inputBuffer) {
        free(inputBuffer);
        inputBuffer = 0;
    }

    if (outputBuffer) {
        free(outputBuffer);
        outputBuffer = 0;
    }

    graph = 0;
}

// --------------------------

void vx_test_canny(int width,int height,int depth,void* bytes)
{
            vx_imagepatch_addressing_t addrs[] = {
                {width,height,sizeof(vx_uint8)*depth,width * sizeof(vx_uint8)*depth,VX_SCALE_UNITY,VX_SCALE_UNITY, 1, 1}
            };

            vx_imagepatch_addressing_t out_addrs[] = {
                {width,height,sizeof(vx_uint8)*depth,width * sizeof(vx_uint8)*depth,VX_SCALE_UNITY,VX_SCALE_UNITY, 1, 1}
            };

            if (!inputBuffer) inputBuffer = calloc(width*height, sizeof(vx_uint8));
            if (!outputBuffer) outputBuffer = calloc(width*height, sizeof(vx_uint8));

            void* src_ptrs[] = { // Each plane!
                inputBuffer
            };

            void* out_ptrs[] = { // Each plane!
                outputBuffer
            };

            memcpy(inputBuffer, bytes, width*height*sizeof(vx_uint8));

            // Openvx
            if (context) {

                if (!graph) {
                    __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "Creating graph\n");

                    graph = vxCreateGraph(context);

                    hframe = vxCreateImageFromHandle(context,VX_DF_IMAGE_U8,addrs,src_ptrs,VX_IMPORT_TYPE_HOST);
                    if (vxGetStatus((vx_reference)hframe) != VX_SUCCESS)
                    {
                        __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: input vx_image not initialised!\n");
                        vxReleaseImage(&hframe);
                    }

                    edges = vxCreateImageFromHandle(context,VX_DF_IMAGE_U8,out_addrs,out_ptrs,VX_IMPORT_TYPE_HOST);
                    if (vxGetStatus((vx_reference)edges) != VX_SUCCESS)
                    {
                        __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: edges vx_image not initialised!\n");
                        vxReleaseImage(&edges);
                    }

                    if (hframe&&edges)// input && output)
                    {

                        vx_threshold hyst = vxCreateThreshold(context, VX_THRESHOLD_TYPE_RANGE, VX_TYPE_UINT8);
                        vx_int32 lower = 50, upper = 100;
                        vxSetThresholdAttribute(hyst, VX_THRESHOLD_ATTRIBUTE_THRESHOLD_LOWER, &lower, sizeof(lower));
                        vxSetThresholdAttribute(hyst, VX_THRESHOLD_ATTRIBUTE_THRESHOLD_UPPER, &upper, sizeof(upper));

                        if (!vxCannyEdgeDetectorNode(graph,hframe,hyst,3,VX_NORM_L1,edges))
                        {
                            __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: failed to create node!\n");
                        }

                        if (vxVerifyGraph(graph) != VX_SUCCESS) {
                            __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: input verifying graph!\n");
                        }
                    }
                }

                if (graph) {

                    if (vxProcessGraph(graph) != VX_SUCCESS) {
                     __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: error processing graph!\n");
                    }

                    vx_rectangle_t rect;
                    rect.start_x = rect.start_y = 0;
                    rect.end_x = width;
                    rect.end_y = height;

                    memcpy(bytes, outputBuffer, width*height*sizeof(vx_uint8));

                }
			} else {
			    __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "No context. Skipping processing.");
			}
}

// ---------
