LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := YalpStore
LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := YalpStore

yalpstore_root  := $(LOCAL_PATH)
yalpstore_dir   := app
yalpstore_out   := $(OUT_DIR)/target/common/obj/APPS/$(LOCAL_MODULE)_intermediates
yalpstore_build := $(yalpstore_root)/$(yalpstore)/build
yalpstore_apk   := build/outputs/apk/release/app-release-unsigned.apk

$(yalpstore_root)/$(yalpstore_dir)/$(yalpstore_apk):
	rm -Rf $(yalpstore_build)
	mkdir -p $(yalpstore_out)
	ln -s $(yalpstore_out) $(yalpstore_build)
	echo "sdk.dir=$(ANDROID_HOME)" > $(yalpstore_root)/local.properties
	cd $(yalpstore_root) && git submodule update --recursive --init
	cd $(yalpstore_root)/$(yalpstore_dir) && JAVA_TOOL_OPTIONS="$(JAVA_TOOL_OPTIONS) -Dfile.encoding=UTF8" ../gradlew assembleRelease

LOCAL_CERTIFICATE := platform
LOCAL_SRC_FILES := $(yalpstore_dir)/$(yalpstore_apk)
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)

include $(BUILD_PREBUILT)