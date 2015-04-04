INVX_PATH := $(call my-dir)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := openvx
LOCAL_SRC_FILES := $(INVX_PATH)/libs/$(TARGET_ARCH_ABI)/libopenvx.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := vxu
LOCAL_SRC_FILES := $(INVX_PATH)/../invx/libs/$(TARGET_ARCH_ABI)/libvxu.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := openvx-c_model
LOCAL_SRC_FILES := $(INVX_PATH)/../invx/libs/$(TARGET_ARCH_ABI)/libopenvx-c_model.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := openvx-debug
LOCAL_SRC_FILES := $(INVX_PATH)/../invx/libs/$(TARGET_ARCH_ABI)/libopenvx-debug.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := openvx-extras
LOCAL_SRC_FILES := $(INVX_PATH)/../invx/libs/$(TARGET_ARCH_ABI)/libopenvx-extras.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------

include $(CLEAR_VARS)
LOCAL_MODULE := invx
LOCAL_SRC_FILES := $(INVX_PATH)/../invx/libs/$(TARGET_ARCH_ABI)/libinvx.so
$(warning $(TARGET_ARCH_ABI))
include $(PREBUILT_SHARED_LIBRARY)

# ------
