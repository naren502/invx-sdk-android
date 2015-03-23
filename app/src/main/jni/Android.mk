LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := openvx
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../openvx/$(TARGET_ARCH_ABI)/libopenvx.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := vxu
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../openvx/$(TARGET_ARCH_ABI)/libvxu.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := openvx-c_model
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../openvx/$(TARGET_ARCH_ABI)/libopenvx-c_model.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := openvx-debug
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../openvx/$(TARGET_ARCH_ABI)/libopenvx-debug.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := openvx-extras
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../openvx/$(TARGET_ARCH_ABI)/libopenvx-extras.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)

LOCAL_MODULE := NDK
LOCAL_SRC_FILES := \
     com_machineswithvision_openvx_JOVX.c

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../openvx/include
LOCAL_SHARED_LIBRARIES := openvx vxu openvx-extras openvx-debug openvx-c_model
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)
