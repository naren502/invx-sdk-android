#include "com_machineswithvision_openvx_JOVX.h"

#include <android/log.h>
#include <stdio.h>
#include "VX/vx.h"

void vx_test_canny(int width,int height,int depth,void* bytes);

vx_context context = 0;
void *inputBuffer = 0;
vx_graph graph = 0;
vx_image hframe = 0;
vx_image edges = 0;

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
        //jlong size = (*env)->GetDirectBufferCapacity(env,buffer);

        //int i;
        //for(i=0;i<size;i++) ptr[i]=(char)(255-(0xFF&(int)ptr[i]));
        vx_test_canny(width, height, depth, ptr);
     } else {
        __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ptr is NULL!D\n");
     }
}

JNIEXPORT void JNICALL Java_com_machineswithvision_openvx_JOVX_jovxReleaseContext
  (JNIEnv *env, jclass cls)
{

    if (hframe) vxReleaseImage(&hframe);
    if (edges) vxReleaseImage(&edges);

    if (context) {
        vxReleaseContext(&context);
        context = 0;
    }

    if (inputBuffer) free(&inputBuffer);
}

// --------------------------

void vx_test_canny(int width,int height,int depth,void* bytes)
{
            vx_imagepatch_addressing_t addrs[] = {
                {width,height,sizeof(vx_uint8)*depth,width * sizeof(vx_uint8)*depth,VX_SCALE_UNITY,VX_SCALE_UNITY, 1, 1}
            };

            if (!inputBuffer) inputBuffer = calloc(width*height, sizeof(vx_uint8));

            void* src_ptrs[] = { // Each plane!
                inputBuffer
            };

            memcpy(inputBuffer, bytes, width*height*sizeof(vx_uint8));

            // Openvx
            if (context) {

                if (!graph) {
                    graph = vxCreateGraph(context);

                    hframe = vxCreateImageFromHandle(context,VX_DF_IMAGE_U8,addrs,src_ptrs,VX_IMPORT_TYPE_HOST);
                    if (vxGetStatus((vx_reference)hframe) != VX_SUCCESS)
                    {
                        __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: input vx_image not initialised!\n");
                        vxReleaseImage(&hframe);
                    }

                    if (hframe)// input && output)
                    {
                        edges = vxCreateImage(context, width, height, VX_DF_IMAGE_U8);

                        vx_threshold hyst = vxCreateThreshold(context, VX_THRESHOLD_TYPE_RANGE, VX_TYPE_UINT8);
                        vx_int32 lower = 40, upper = 250;
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

                if (vxProcessGraph(graph) != VX_SUCCESS) {
                    __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: error processing graph!\n");
                }

                vx_rectangle_t rect;
                rect.start_x = rect.start_y = 0;
                rect.end_x = width;
                rect.end_y = height;

                vxAccessImagePatch(edges,&rect,0,&addrs[0],&bytes,VX_READ_ONLY);
			} else {
			    __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "Failed to create openvx context");
			}
}

// ---------
