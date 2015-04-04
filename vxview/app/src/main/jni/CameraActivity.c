//
//  CameraActivity.c
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

#include "CameraActivity.h"

#include <android/log.h>
#include <stdio.h>
#include "VX/vx.h"

void vx_test_canny(int width,int height,void* bytes);

/**
 * Global data for this application
 */
vx_context context = 0;
void *inputBuffer = 0;
void *outputBuffer = 0;
vx_graph graph = 0;
vx_image input = 0;
vx_image edges = 0;
vx_image dx = 0;
vx_image dy = 0;
vx_image mag = 0;

/**
 * OpenVX context creation
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_CameraActivity_createVXContext
  (JNIEnv *env, jclass cls)
{
    context = vxCreateContext();
}

/**
 *
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_CameraActivity_processBytes
   (JNIEnv *env, jclass cls, jobject buffer, jint width, jint height)
{


    void* ptr = (*env)->GetDirectBufferAddress(env,buffer);

    if (ptr) {
        vx_test_canny(width, height, ptr);
     } else {
        __android_log_print(ANDROID_LOG_VERBOSE, "vxview", "ptr is NULL!D\n");
     }
}

/**
 *
 */
JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_CameraActivity_releaseVXContext
  (JNIEnv *env, jclass cls)
{

    if (input) {
        vxReleaseImage(&input);
        input = 0;
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

/**
 * Initialises the OpenVX graph, creating input and output image nodes with the specified size
 */
void initialiseGraph(int width, int height) {
    __android_log_print(ANDROID_LOG_VERBOSE, "vxview", "Creating graph\n");

    vx_imagepatch_addressing_t in_addrs[] = {
        {width, height, sizeof(vx_uint8), width*sizeof(vx_uint8), VX_SCALE_UNITY, VX_SCALE_UNITY, 1, 1}
    };

    vx_imagepatch_addressing_t out_addrs[] = {
        {width, height, sizeof(vx_uint8), width*sizeof(vx_uint8), VX_SCALE_UNITY, VX_SCALE_UNITY, 1, 1}
    };

    if (!inputBuffer) inputBuffer = calloc(width*height, sizeof(vx_uint8));
    if (!outputBuffer) outputBuffer = calloc(width*height, sizeof(vx_uint8));

    void* in_ptrs[] = { // Each plane!
        inputBuffer
    };

    void* out_ptrs[] = { // Each plane!
        outputBuffer
    };

    graph = vxCreateGraph(context);

    input = vxCreateImageFromHandle(context, VX_DF_IMAGE_U8, in_addrs, in_ptrs, VX_IMPORT_TYPE_HOST);
    if (vxGetStatus((vx_reference)input) != VX_SUCCESS)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, "vxview", "ERROR: input vx_image not initialised!\n");
        vxReleaseImage(&input);
    }

    edges = vxCreateImageFromHandle(context, VX_DF_IMAGE_U8, out_addrs, out_ptrs, VX_IMPORT_TYPE_HOST);
    if (vxGetStatus((vx_reference)edges) != VX_SUCCESS)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, "vxview", "ERROR: edges vx_image not initialised!\n");
        vxReleaseImage(&edges);
    }

    if (input&&edges)// input && output)
    {

        vx_threshold hyst = vxCreateThreshold(context, VX_THRESHOLD_TYPE_RANGE, VX_TYPE_UINT8);
        vx_int32 lower = 50, upper = 100;
        vxSetThresholdAttribute(hyst, VX_THRESHOLD_ATTRIBUTE_THRESHOLD_LOWER, &lower, sizeof(lower));
        vxSetThresholdAttribute(hyst, VX_THRESHOLD_ATTRIBUTE_THRESHOLD_UPPER, &upper, sizeof(upper));

        // Add a Canny node to the graph
        if (!vxCannyEdgeDetectorNode(graph, input, hyst, 3, VX_NORM_L1,edges))
        {
            __android_log_print(ANDROID_LOG_VERBOSE, "vxview", "ERROR: failed to create node!\n");
        }

        // Verify the graph for safe execution
        if (vxVerifyGraph(graph) != VX_SUCCESS) {
            __android_log_print(ANDROID_LOG_VERBOSE, "vxview", "ERROR: input verifying graph!\n");
        }
    }
}

// --------------------------

void vx_test_canny(int width,int height,void* bytes)
{
            if (context) {

                if (!graph) initialiseGraph(width, height);

                if (graph) {

                    memcpy(inputBuffer, bytes, width*height*sizeof(vx_uint8));

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
