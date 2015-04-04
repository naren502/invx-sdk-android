LOCAL_PATH := $(call my-dir)

include $(LOCAL_PATH)/../invx/Android.mk

# ------

include $(CLEAR_VARS)

LOCAL_MODULE := NDK
LOCAL_SRC_FILES := \
     com_machineswithvision_openvx_JOVX.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../invx/include
LOCAL_SHARED_LIBRARIES := openvx vxu openvx-extras openvx-debug openvx-c_model invx
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
