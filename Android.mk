LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

ifeq ($(TARGET_BOOTLOADER_BOARD_NAME),harmony)
LOCAL_MANIFEST_FILE := /no4g_nogps/AndroidManifest.xml
else ifeq ($(TARGET_BOOTLOADER_BOARD_NAME),speedy)
LOCAL_MANIFEST_FILE := /4g/AndroidManifest.xml
else ifeq ($(TARGET_BOOTLOADER_BOARD_NAME),supersonic)
LOCAL_MANIFEST_FILE := /4g/AndroidManifest.xml
endif

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := Settings
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))


