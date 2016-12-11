package com.github.yeriomin.playstoreapi;

/**
 * @author akdeniz
 */
public class Utils {

    /*
*

    id: "Xiaomi/gemini/gemini:6.0/MRA58K/V7.2.8.0.MAACNDB:user/release-keys"
    product: "qcom"
    carrier: "Xiaomi"
    radio: "TH20.c1.3-0321_1155_57c5007"
    bootloader: "unknown"
    client: "unknown"
    timestamp: 1458643019
    googleServices: 10084448
    device: "gemini"
    sdkVersion: 23
    model: "MI 5"
    manufacturer: "Xiaomi"
    buildProduct: "gemini"
    otaInstalled: 0


Build.BOARD msm8996
Build.BOOTLOADER unknown
Build.BRAND Xiaomi
Build.DEVICE gemini
Build.DISPLAY MRA58K
Build.FINGERPRINT Xiaomi/gemini/gemini:6.0/MRA58K/V7.2.8.0.MAACNDB:user/release-keys
Build.HARDWARE qcom
Build.HOST qh-miui-ota-bd72.bj
Build.ID MRA58K
Build.MANUFACTURER Xiaomi
Build.MODEL MI 5
Build.PRODUCT gemini
Build.SERIAL d0189fa4
Build.TAGS release-keys
Build.TYPE user
Build.UNKNOWN unknown
Build.USER builder
Build.CPU_ABI arm64-v8a
Build.CPU_ABI2
Build.TIME 1458643019000
Build.VERSION.CODENAME REL
Build.VERSION.INCREMENTAL V7.2.8.0.0.MAACNDB
Build.VERSION.RELEASE 6.0
Build.VERSION.SDK_INT 23
    */

//    /**
//     * Generates android checkin request with properties of "Galaxy S3".
//     * <p>
//     * <a href=
//     * "http://www.glbenchmark.com/phonedetails.jsp?benchmark=glpro25&D=Samsung+GT-I9300+Galaxy+S+III&testgroup=system"
//     * > http://www.glbenchmark.com/phonedetails.jsp?benchmark=glpro25&D=Samsung
//     * +GT-I9300+Galaxy+S+III&testgroup=system </a>
//     */
//    public static AndroidCheckinRequest generateAndroidCheckinRequest() {
//
//        return AndroidCheckinRequest
//            .newBuilder()
//            .setId(0)
//            .setCheckin(
//                AndroidCheckinProto.newBuilder()
//                    .setBuild(
//                        AndroidBuildProto.newBuilder()
//                            .setId(Build.FINGERPRINT)
//                            .setProduct(Build.HARDWARE)
//                            .setCarrier(Build.BRAND)
//                            .setRadio(Build.RADIO)
//                            .setBootloader(Build.BOOTLOADER)
//                            .setDevice(Build.DEVICE)
//                            .setSdkVersion(Build.VERSION.SDK_INT)
//                            .setModel(Build.MODEL)
//                            .setManufacturer(Build.MANUFACTURER)
//                            .setBuildProduct(Build.PRODUCT)
//                            .setClient("android-google")
//                            .setOtaInstalled(false)
//                            .setTimestamp(new Date().getTime() / 1000)
//                            .setGoogleServices(16)
//                    )
//                    .setLastCheckinMsec(0)
//                    .setCellOperator("310260")
//                    .setSimOperator("310260")
//                    .setRoaming("mobile-notroaming")
//                    .setUserNumber(0)
//            )
//            .setLocale(Locale.getDefault().toString())
//            .setTimeZone(TimeZone.getDefault().getID())
//            .setVersion(3)
//            .setDeviceConfiguration(getDeviceConfigurationProto())
//            .setFragment(0)
//            .build();
//    }
//
//    public static AndroidCheckinRequest generateAndroidCheckinRequestOriginal() {
//
//        return AndroidCheckinRequest
//            .newBuilder()
//            .setId(0)
//            .setCheckin(
//                AndroidCheckinProto.newBuilder()
//                    .setBuild(
//                        AndroidBuildProto.newBuilder()
//                            .setId("samsung/m0xx/m0:4.0.4/IMM76D/I9300XXALF2:user/release-keys")
//                            .setProduct("smdk4x12")
//                            .setCarrier("Google")
//                            .setRadio("I9300XXALF2")
//                            .setBootloader("PRIMELA03")
//                            .setClient("android-google")
//                            .setTimestamp(new Date().getTime() / 1000)
//                            .setGoogleServices(16)
//                            .setDevice("m0")
//                            .setSdkVersion(16)
//                            .setModel("GT-I9300")
//                            .setManufacturer("Samsung")
//                            .setBuildProduct("m0xx")
//                            .setOtaInstalled(false)
//                    )
//                    .setLastCheckinMsec(0)
//                    .setCellOperator("310260")
//                    .setSimOperator("310260")
//                    .setRoaming("mobile-notroaming")
//                    .setUserNumber(0)
//            )
//            .setLocale("en_US")
//            .setTimeZone("Europe/Istanbul")
//            .setVersion(3)
//            .setDeviceConfiguration(getDeviceConfigurationProto())
//            .setFragment(0)
//            .build();
//    }
//
//    public static AndroidCheckinRequest generateAndroidCheckinRequestNviennot() {
//
//        return AndroidCheckinRequest
//            .newBuilder()
//            .setId(0)
//            .setCheckin(
//                AndroidCheckinProto.newBuilder()
//                    .setBuild(
//                        AndroidBuildProto.newBuilder()
//                            .setId("google/yakju/maguro:4.1.1/JRO03C/398337:user/release-keys")
//                            .setProduct("tuna")
//                            .setCarrier("Google")
//                            .setRadio("I9250XXLA2")
//                            .setBootloader("PRIMELA03")
//                            .setClient("android-google")
//                            .setTimestamp(new Date().getTime()/1000)
//                            .setGoogleServices(16)
//                            .setDevice("maguro")
//                            .setSdkVersion(16)
//                            .setModel("Galaxy Nexus")
//                            .setManufacturer("Samsung")
//                            .setBuildProduct("yakju")
//                            .setOtaInstalled(false)
//                    )
//                    .setLastCheckinMsec(0)
//                    .setCellOperator("310260")
//                    .setSimOperator("310260")
//                    .setRoaming("mobile-notroaming")
//                    .setUserNumber(0)
//            )
//            .setLocale("en_US")
//            .setTimeZone("Europe/Istanbul")
//            .setVersion(3)
//            .setDeviceConfiguration(getDeviceConfigurationProto())
//            .setFragment(0)
//            .build();
//    }
//
//    public static AndroidCheckinRequest generateAndroidCheckinRequestRacoon() {
//
//        return AndroidCheckinRequest
//            .newBuilder()
//            .setId(0)
//            .setCheckin(
//                AndroidCheckinProto.newBuilder()
//                    .setBuild(
//                        AndroidBuildProto.newBuilder()
//                            .setId("samsung/nobleltejv/noblelte:6.0.1/MMB29K/N920CXXU2BPD6:user/release-keys")
//                            .setProduct("noblelte")
//                            .setCarrier("Google")
//                            .setRadio("I9300XXALF2")
//                            .setBootloader("PRIMELA03")
//                            .setClient("android-google")
//                            .setTimestamp(new Date().getTime() / 1000)
//                            .setGoogleServices(16)
//                            .setDevice("noblelte")
//                            .setSdkVersion(23)
//                            .setModel("SM-N920C")
//                            .setManufacturer("Samsung")
//                            .setBuildProduct("noblelte")
//                            .setOtaInstalled(false)
//                    )
//                    .setLastCheckinMsec(0)
//                    .setCellOperator("310260")
//                    .setSimOperator("310260")
//                    .setRoaming("mobile-notroaming")
//                    .setUserNumber(0)
//            )
//            .setLocale("en_US")
//            .setTimeZone("Europe/Berlin")
//            .setVersion(3)
//            .setDeviceConfiguration(getDeviceConfigurationProto())
//            .setFragment(0)
//            .build();
//    }
//
//    public static DeviceConfigurationProto getDeviceConfigurationProtoAAAAA() {
////        DisplayMetrics metrics = new DisplayMetrics();
////        WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
////        wm.getDefaultDisplay().getMetrics(metrics);
//        return DeviceConfigurationProto.newBuilder()
//            .setTouchScreen(3)
//            .setKeyboard(1)
//            .setNavigation(1)
//            .setScreenLayout(2)
//            .setHasHardKeyboard(false)
//            .setHasFiveWayNavigation(false)
//            .setScreenDensity(320)
//            .setScreenWidth(720)
//            .setScreenHeight(1184)
//            .setGlEsVersion(131072)
//            .addAllNativePlatform(Arrays.asList(Build.CPU_ABI, Build.CPU_ABI2))
////            .addAllNativePlatform(Arrays.asList("armeabi-v7a", "armeabi"))
//            .addAllSystemSharedLibrary(
//                Arrays.asList("android.test.runner", "com.android.future.usb.accessory", "com.android.location.provider",
//                    "com.android.nfc_extras", "com.google.android.maps", "com.google.android.media.effects",
//                    "com.google.widevine.software.drm", "javax.obex"))
//            .addAllSystemAvailableFeature(
//                Arrays.asList("android.hardware.bluetooth", "android.hardware.camera",
//                    "android.hardware.camera.autofocus", "android.hardware.camera.flash",
//                    "android.hardware.camera.front", "android.hardware.faketouch", "android.hardware.location",
//                    "android.hardware.location.gps", "android.hardware.location.network",
//                    "android.hardware.microphone", "android.hardware.nfc", "android.hardware.screen.landscape",
//                    "android.hardware.screen.portrait", "android.hardware.sensor.accelerometer",
//                    "android.hardware.sensor.barometer", "android.hardware.sensor.compass",
//                    "android.hardware.sensor.gyroscope", "android.hardware.sensor.light",
//                    "android.hardware.sensor.proximity", "android.hardware.telephony",
//                    "android.hardware.telephony.gsm", "android.hardware.touchscreen",
//                    "android.hardware.touchscreen.multitouch", "android.hardware.touchscreen.multitouch.distinct",
//                    "android.hardware.touchscreen.multitouch.jazzhand", "android.hardware.usb.accessory",
//                    "android.hardware.usb.host", "android.hardware.wifi", "android.hardware.wifi.direct",
//                    "android.software.live_wallpaper", "android.software.sip", "android.software.sip.voip",
//                    "com.cyanogenmod.android", "com.cyanogenmod.nfc.enhanced",
//                    "com.google.android.feature.GOOGLE_BUILD", "com.nxp.mifare", "com.tmobile.software.themes"))
//            .addAllSystemSupportedLocale(
//                Arrays.asList("af", "af_ZA", "am", "am_ET", "ar", "ar_EG", "bg", "bg_BG", "ca", "ca_ES", "cs", "cs_CZ",
//                    "da", "da_DK", "de", "de_AT", "de_CH", "de_DE", "de_LI", "el", "el_GR", "en", "en_AU", "en_CA",
//                    "en_GB", "en_NZ", "en_SG", "en_US", "es", "es_ES", "es_US", "fa", "fa_IR", "fi", "fi_FI", "fr",
//                    "fr_BE", "fr_CA", "fr_CH", "fr_FR", "hi", "hi_IN", "hr", "hr_HR", "hu", "hu_HU", "in", "in_ID",
//                    "it", "it_CH", "it_IT", "iw", "iw_IL", "ja", "ja_JP", "ko", "ko_KR", "lt", "lt_LT", "lv",
//                    "lv_LV", "ms", "ms_MY", "nb", "nb_NO", "nl", "nl_BE", "nl_NL", "pl", "pl_PL", "pt", "pt_BR",
//                    "pt_PT", "rm", "rm_CH", "ro", "ro_RO", "ru", "ru_RU", "sk", "sk_SK", "sl", "sl_SI", "sr",
//                    "sr_RS", "sv", "sv_SE", "sw", "sw_TZ", "th", "th_TH", "tl", "tl_PH", "tr", "tr_TR", "ug",
//                    "ug_CN", "uk", "uk_UA", "vi", "vi_VN", "zh_CN", "zh_TW", "zu", "zu_ZA"))
//            .addAllGlExtension(
//                Arrays.asList("GL_EXT_debug_marker", "GL_EXT_discard_framebuffer", "GL_EXT_multi_draw_arrays",
//                    "GL_EXT_shader_texture_lod", "GL_EXT_texture_format_BGRA8888",
//                    "GL_IMG_multisampled_render_to_texture", "GL_IMG_program_binary", "GL_IMG_read_format",
//                    "GL_IMG_shader_binary", "GL_IMG_texture_compression_pvrtc", "GL_IMG_texture_format_BGRA8888",
//                    "GL_IMG_texture_npot", "GL_IMG_vertex_array_object", "GL_OES_EGL_image",
//                    "GL_OES_EGL_image_external", "GL_OES_blend_equation_separate", "GL_OES_blend_func_separate",
//                    "GL_OES_blend_subtract", "GL_OES_byte_coordinates", "GL_OES_compressed_ETC1_RGB8_texture",
//                    "GL_OES_compressed_paletted_texture", "GL_OES_depth24", "GL_OES_depth_texture",
//                    "GL_OES_draw_texture", "GL_OES_egl_sync", "GL_OES_element_index_uint",
//                    "GL_OES_extended_matrix_palette", "GL_OES_fixed_point", "GL_OES_fragment_precision_high",
//                    "GL_OES_framebuffer_object", "GL_OES_get_program_binary", "GL_OES_mapbuffer",
//                    "GL_OES_matrix_get", "GL_OES_matrix_palette", "GL_OES_packed_depth_stencil",
//                    "GL_OES_point_size_array", "GL_OES_point_sprite", "GL_OES_query_matrix", "GL_OES_read_format",
//                    "GL_OES_required_internalformat", "GL_OES_rgb8_rgba8", "GL_OES_single_precision",
//                    "GL_OES_standard_derivatives", "GL_OES_stencil8", "GL_OES_stencil_wrap",
//                    "GL_OES_texture_cube_map", "GL_OES_texture_env_crossbar", "GL_OES_texture_float",
//                    "GL_OES_texture_half_float", "GL_OES_texture_mirrored_repeat", "GL_OES_vertex_array_object",
//                    "GL_OES_vertex_half_float")).build();
//    }
}
