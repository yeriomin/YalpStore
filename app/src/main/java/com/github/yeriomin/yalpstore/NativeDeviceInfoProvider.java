package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.github.yeriomin.playstoreapi.AndroidBuildProto;
import com.github.yeriomin.playstoreapi.AndroidCheckinProto;
import com.github.yeriomin.playstoreapi.AndroidCheckinRequest;
import com.github.yeriomin.playstoreapi.DeviceConfigurationProto;
import com.github.yeriomin.playstoreapi.DeviceInfoProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NativeDeviceInfoProvider implements DeviceInfoProvider {

    // Getting this requires a permission and google services to be installed
    static private final int GOOGLE_SERVICES_VERSION_CODE = 80711500;

    private Context context;
    private String localeString;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setLocaleString(String localeString) {
        this.localeString = localeString;
    }

    public int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    public String getUserAgentString() {
        return "Android-Finsky/7.1.15 ("
            + "api=3"
            + ",versionCode=" + GOOGLE_SERVICES_VERSION_CODE
            + ",sdk=" + Build.VERSION.SDK_INT
            + ",device=" + Build.DEVICE
            + ",hardware=" + Build.HARDWARE
            + ",product=" + Build.PRODUCT
            + ")";
    }

    public AndroidCheckinRequest generateAndroidCheckinRequest() {
        return AndroidCheckinRequest
            .newBuilder()
            .setId(0)
            .setCheckin(
                AndroidCheckinProto.newBuilder()
                    .setBuild(
                        AndroidBuildProto.newBuilder()
                            .setId(Build.FINGERPRINT)
                            .setProduct(Build.HARDWARE)
                            .setCarrier(Build.BRAND)
                            .setRadio(Build.RADIO)
                            .setBootloader(Build.BOOTLOADER)
                            .setDevice(Build.DEVICE)
                            .setSdkVersion(Build.VERSION.SDK_INT)
                            .setModel(Build.MODEL)
                            .setManufacturer(Build.MANUFACTURER)
                            .setBuildProduct(Build.PRODUCT)
                            .setClient("android-google")
                            .setOtaInstalled(false)
                            .setTimestamp(System.currentTimeMillis() / 1000)
                            .setGoogleServices(GOOGLE_SERVICES_VERSION_CODE)
                    )
                    .setLastCheckinMsec(0)
                    .setCellOperator("310260") // Getting this and the next two requires permission
                    .setSimOperator("310260")
                    .setRoaming("mobile-notroaming")
                    .setUserNumber(0)
            )
            .setLocale(this.localeString)
            .setTimeZone(TimeZone.getDefault().getID())
            .setVersion(3)
            .setDeviceConfiguration(getDeviceConfigurationProto())
            .setFragment(0)
            .build();
    }

    public DeviceConfigurationProto getDeviceConfigurationProto() {
        DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();
        PackageManager packageManager = this.context.getPackageManager();
        FeatureInfo[] featuresList = packageManager.getSystemAvailableFeatures();
        List<String> featureStringList = new ArrayList<>();
        for (FeatureInfo feature : featuresList) {
            if (feature.name != null) {
                featureStringList.add(feature.name);
            }
        }
        List<String> localeStringList = new ArrayList<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            String localeString = locale.toString();
            if (localeString.length() <= 5) {
                localeStringList.add(localeString);
            }
        }
        List<String> platforms = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 21) {
            platforms = Arrays.asList(Build.SUPPORTED_ABIS);
        } else {
            if (null != Build.CPU_ABI && !Build.CPU_ABI.isEmpty()) {
                platforms.add(Build.CPU_ABI);
            }
            if (null != Build.CPU_ABI2 && !Build.CPU_ABI2.isEmpty()) {
                platforms.add(Build.CPU_ABI2);
            }
        }

        return DeviceConfigurationProto.newBuilder()
            .setTouchScreen(3)
            .setKeyboard(1)
            .setNavigation(1)
            .setScreenLayout(2)
            .setHasHardKeyboard(false)
            .setHasFiveWayNavigation(false)
            .setScreenDensity((int) (metrics.density * 160f))
            .setScreenWidth(metrics.widthPixels)
            .setScreenHeight(metrics.heightPixels)
            .addAllNativePlatform(platforms)
            .addAllSystemSharedLibrary(Arrays.asList(
                "ConnectivityExt",
                "activation.jar",
                "android-support-v13.jar",
                "android-support-v4.jar",
                "android-support-v7-recyclerview.jar",
                "cloud-common.jar",
                "com.android.media.remotedisplay",
                "com.android.mediadrm.signer",
                "android.test.runner",
                "com.android.future.usb.accessory",
                "com.android.location.provider",
                "com.android.nfc_extras",
                "com.google.android.maps",
                "com.google.android.media.effects",
                "com.google.widevine.software.drm",
                "javax.obex"
            ))
            .addAllSystemAvailableFeature(featureStringList)
            .addAllSystemSupportedLocale(localeStringList)
            .setGlEsVersion(196609) // Getting this and next list requires messing with ndk
            .addAllGlExtension(Arrays.asList(
                "GL_AMD_compressed_ATC_texture",
                "GL_AMD_performance_monitor",
                "GL_ANDROID_extension_pack_es31a",
                "GL_APPLE_texture_2D_limited_npot",
                "GL_ARB_vertex_buffer_object",
                "GL_ARM_shader_framebuffer_fetch_depth_stencil",
                "GL_EXT_YUV_target",
                "GL_EXT_blit_framebuffer_params",
                "GL_EXT_buffer_storage",
                "GL_EXT_color_buffer_float",
                "GL_EXT_color_buffer_half_float",
                "GL_EXT_copy_image",
                "GL_EXT_debug_label",
                "GL_EXT_debug_marker",
                "GL_EXT_discard_framebuffer",
                "GL_EXT_disjoint_timer_query",
                "GL_EXT_draw_buffers_indexed",
                "GL_EXT_geometry_shader",
                "GL_EXT_gpu_shader5",
                "GL_EXT_multisampled_render_to_texture",
                "GL_EXT_primitive_bounding_box",
                "GL_EXT_robustness",
                "GL_EXT_sRGB",
                "GL_EXT_sRGB_write_control",
                "GL_EXT_shader_framebuffer_fetch",
                "GL_EXT_shader_io_blocks",
                "GL_EXT_tessellation_shader",
                "GL_EXT_texture_border_clamp",
                "GL_EXT_texture_buffer",
                "GL_EXT_texture_cube_map_array",
                "GL_EXT_texture_filter_anisotropic",
                "GL_EXT_texture_format_BGRA8888",
                "GL_EXT_texture_norm16",
                "GL_EXT_texture_sRGB_R8",
                "GL_EXT_texture_sRGB_decode",
                "GL_EXT_texture_type_2_10_10_10_REV",
                "GL_KHR_blend_equation_advanced",
                "GL_KHR_blend_equation_advanced_coherent",
                "GL_KHR_debug",
                "GL_KHR_no_error",
                "GL_KHR_texture_compression_astc_hdr",
                "GL_KHR_texture_compression_astc_ldr",
                "GL_OES_EGL_image",
                "GL_OES_EGL_image_external",
                "GL_OES_EGL_sync",
                "GL_OES_blend_equation_separate",
                "GL_OES_blend_func_separate",
                "GL_OES_blend_subtract",
                "GL_OES_compressed_ETC1_RGB8_texture",
                "GL_OES_compressed_paletted_texture",
                "GL_OES_depth24",
                "GL_OES_depth_texture",
                "GL_OES_depth_texture_cube_map",
                "GL_OES_draw_texture",
                "GL_OES_element_index_uint",
                "GL_OES_framebuffer_object",
                "GL_OES_get_program_binary",
                "GL_OES_matrix_palette",
                "GL_OES_packed_depth_stencil",
                "GL_OES_point_size_array",
                "GL_OES_point_sprite",
                "GL_OES_read_format",
                "GL_OES_rgb8_rgba8",
                "GL_OES_sample_shading",
                "GL_OES_sample_variables",
                "GL_OES_shader_image_atomic",
                "GL_OES_shader_multisample_interpolation",
                "GL_OES_standard_derivatives",
                "GL_OES_stencil_wrap",
                "GL_OES_surfaceless_context",
                "GL_OES_texture_3D",
                "GL_OES_texture_compression_astc",
                "GL_OES_texture_cube_map",
                "GL_OES_texture_env_crossbar",
                "GL_OES_texture_float",
                "GL_OES_texture_float_linear",
                "GL_OES_texture_half_float",
                "GL_OES_texture_half_float_linear",
                "GL_OES_texture_mirrored_repeat",
                "GL_OES_texture_npot",
                "GL_OES_texture_stencil8",
                "GL_OES_texture_storage_multisample_2d_array",
                "GL_OES_vertex_array_object",
                "GL_OES_vertex_half_float",
                "GL_OVR_multiview",
                "GL_OVR_multiview2",
                "GL_OVR_multiview_multisampled_render_to_texture",
                "GL_QCOM_alpha_test",
                "GL_QCOM_extended_get",
                "GL_QCOM_tiled_rendering",
                "GL_EXT_multi_draw_arrays",
                "GL_EXT_shader_texture_lod",
                "GL_IMG_multisampled_render_to_texture",
                "GL_IMG_program_binary",
                "GL_IMG_read_format",
                "GL_IMG_shader_binary",
                "GL_IMG_texture_compression_pvrtc",
                "GL_IMG_texture_format_BGRA8888",
                "GL_IMG_texture_npot",
                "GL_IMG_vertex_array_object",
                "GL_OES_byte_coordinates",
                "GL_OES_extended_matrix_palette",
                "GL_OES_fixed_point",
                "GL_OES_fragment_precision_high",
                "GL_OES_mapbuffer",
                "GL_OES_matrix_get",
                "GL_OES_query_matrix",
                "GL_OES_required_internalformat",
                "GL_OES_single_precision",
                "GL_OES_stencil8"
            ))
            .build();
    }
}
