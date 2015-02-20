#include "com_machineswithvision_vxview_OutputView.h"

#include <android/log.h>
#include <stdio.h>
#include "VX/vx.h"

void vx_test_canny(int width,int height,int depth,void* bytes);

JNIEXPORT void JNICALL Java_com_machineswithvision_vxview_OutputView_processBytes
   (JNIEnv *env, jobject obj, jobject buffer, jint width, jint height, jint depth)
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

// --------------------------

void vx_test_canny(int width,int height,int depth,void* bytes)
{
            vx_imagepatch_addressing_t addrs[] = {
                {width,height,sizeof(vx_uint8)*depth,width * sizeof(vx_uint8)*depth,VX_SCALE_UNITY,VX_SCALE_UNITY, 1, 1}
            };

            void* src_ptrs[] = { // Each plane!
                bytes
            };

            // Openvx
			vx_context context;
            context = vxCreateContext();

            if (context) {
                vx_image hframe = vxCreateImageFromHandle(context,VX_DF_IMAGE_U8,addrs,src_ptrs,VX_IMPORT_TYPE_HOST);
                if (vxGetStatus((vx_reference)hframe) != VX_SUCCESS)
                {
                    __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: input vx_image not initialised!\n");
                    vxReleaseImage(&hframe);
                }

                if (hframe)// input && output)
                {

                    vx_image edges = vxCreateImage(context, width, height, VX_DF_IMAGE_U8);

                    vx_threshold hyst = vxCreateThreshold(context, VX_THRESHOLD_TYPE_RANGE, VX_TYPE_UINT8);
                    vx_int32 lower = 40, upper = 250;
                    vxSetThresholdAttribute(hyst, VX_THRESHOLD_ATTRIBUTE_THRESHOLD_LOWER, &lower, sizeof(lower));
                    vxSetThresholdAttribute(hyst, VX_THRESHOLD_ATTRIBUTE_THRESHOLD_UPPER, &upper, sizeof(upper));

                    if (vxuCannyEdgeDetector(context,hframe,hyst,3,VX_NORM_L1,edges)!=VX_SUCCESS)
                    {
                        __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "ERROR: failed to do function!\n");
                    }

                    vx_rectangle_t rect;
                    rect.start_x = rect.start_y = 0;
                    rect.end_x = width;
                    rect.end_y = height;

                    vxAccessImagePatch(edges,&rect,0,&addrs[0],&bytes,VX_READ_ONLY);
                }

                vxReleaseImage(&hframe);
			    vxReleaseContext(&context);
			} else {
			    __android_log_print(ANDROID_LOG_VERBOSE, "NDK", "Failed to create openvx context");
			}
}

// ---------
