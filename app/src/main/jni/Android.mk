LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := NDK
LOCAL_SRC_FILES := \
     com_machineswithvision_vxview_OutputView.c

LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
