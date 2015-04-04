LOCAL_PATH := $(call my-dir)

include $(LOCAL_PATH)/../invx/Android.mk

# ------

include $(CLEAR_VARS)

LOCAL_MODULE := vxview
LOCAL_SRC_FILES := \
     CameraActivity.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../invx/include
LOCAL_SHARED_LIBRARIES := openvx vxu openvx-extras openvx-debug openvx-c_model invx
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
