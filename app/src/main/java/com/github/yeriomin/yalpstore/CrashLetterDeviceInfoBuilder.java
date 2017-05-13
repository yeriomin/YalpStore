package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class CrashLetterDeviceInfoBuilder extends CrashLetterBuilder {

    static private Map<String, String> staticProperties = new HashMap<>();

    static {
        staticProperties.put("Client", "android-google");
        staticProperties.put("GSF.version", "16");
        staticProperties.put("CellOperator", "310260");
        staticProperties.put("SimOperator", "310260");
        staticProperties.put("Roaming", "mobile-notroaming");
        staticProperties.put("TimeZone", TimeZone.getDefault().getID());
        staticProperties.put("GL.Version", "131072");
        staticProperties.put("GL.Extensions", "GL_AMD_compressed_ATC_texture,GL_AMD_performance_monitor,GL_ANDROID_extension_pack_es31a,GL_APPLE_texture_2D_limited_npot,GL_ARB_vertex_buffer_object,GL_ARM_shader_framebuffer_fetch_depth_stencil,GL_EXT_YUV_target,GL_EXT_blit_framebuffer_params,GL_EXT_buffer_storage,GL_EXT_color_buffer_float,GL_EXT_color_buffer_half_float,GL_EXT_copy_image,GL_EXT_debug_label,GL_EXT_debug_marker,GL_EXT_discard_framebuffer,GL_EXT_disjoint_timer_query,GL_EXT_draw_buffers_indexed,GL_EXT_geometry_shader,GL_EXT_gpu_shader5,GL_EXT_multisampled_render_to_texture,GL_EXT_primitive_bounding_box,GL_EXT_robustness,GL_EXT_sRGB,GL_EXT_sRGB_write_control,GL_EXT_shader_framebuffer_fetch,GL_EXT_shader_io_blocks,GL_EXT_tessellation_shader,GL_EXT_texture_border_clamp,GL_EXT_texture_buffer,GL_EXT_texture_cube_map_array,GL_EXT_texture_filter_anisotropic,GL_EXT_texture_format_BGRA8888,GL_EXT_texture_norm16,GL_EXT_texture_sRGB_R8,GL_EXT_texture_sRGB_decode,GL_EXT_texture_type_2_10_10_10_REV,GL_KHR_blend_equation_advanced,GL_KHR_blend_equation_advanced_coherent,GL_KHR_debug,GL_KHR_no_error,GL_KHR_texture_compression_astc_hdr,GL_KHR_texture_compression_astc_ldr,GL_OES_EGL_image,GL_OES_EGL_image_external,GL_OES_EGL_sync,GL_OES_blend_equation_separate,GL_OES_blend_func_separate,GL_OES_blend_subtract,GL_OES_compressed_ETC1_RGB8_texture,GL_OES_compressed_paletted_texture,GL_OES_depth24,GL_OES_depth_texture,GL_OES_depth_texture_cube_map,GL_OES_draw_texture,GL_OES_element_index_uint,GL_OES_framebuffer_object,GL_OES_get_program_binary,GL_OES_matrix_palette,GL_OES_packed_depth_stencil,GL_OES_point_size_array,GL_OES_point_sprite,GL_OES_read_format,GL_OES_rgb8_rgba8,GL_OES_sample_shading,GL_OES_sample_variables,GL_OES_shader_image_atomic,GL_OES_shader_multisample_interpolation,GL_OES_standard_derivatives,GL_OES_stencil_wrap,GL_OES_surfaceless_context,GL_OES_texture_3D,GL_OES_texture_compression_astc,GL_OES_texture_cube_map,GL_OES_texture_env_crossbar,GL_OES_texture_float,GL_OES_texture_float_linear,GL_OES_texture_half_float,GL_OES_texture_half_float_linear,GL_OES_texture_mirrored_repeat,GL_OES_texture_npot,GL_OES_texture_stencil8,GL_OES_texture_storage_multisample_2d_array,GL_OES_vertex_array_object,GL_OES_vertex_half_float,GL_OVR_multiview,GL_OVR_multiview2,GL_OVR_multiview_multisampled_render_to_texture,GL_QCOM_alpha_test,GL_QCOM_extended_get,GL_QCOM_tiled_rendering,GL_EXT_multi_draw_arrays,GL_EXT_shader_texture_lod,GL_IMG_multisampled_render_to_texture,GL_IMG_program_binary,GL_IMG_read_format,GL_IMG_shader_binary,GL_IMG_texture_compression_pvrtc,GL_IMG_texture_format_BGRA8888,GL_IMG_texture_npot,GL_IMG_vertex_array_object,GL_OES_byte_coordinates,GL_OES_extended_matrix_palette,GL_OES_fixed_point,GL_OES_fragment_precision_high,GL_OES_mapbuffer,GL_OES_matrix_get,GL_OES_query_matrix,GL_OES_required_internalformat,GL_OES_single_precision,GL_OES_stencil8");
    }

    public CrashLetterDeviceInfoBuilder(Context context) {
        super(context);
    }

    @Override
    public String build() {
        StringBuilder body = new StringBuilder();
        Map<String, String> deviceInfo = getDeviceInfo();
        for (String key : deviceInfo.keySet()) {
            body.append(key).append(" = ").append(deviceInfo.get(key)).append("\n");
        }
        body.append("\n\n");
        return body.toString();
    }

    @Override
    protected File getFile() {
        try {
            return File.createTempFile("device-" + Build.DEVICE + "-", ".properties");
        } catch (IOException e) {
            return null;
        }
    }

    private Map<String, String> getDeviceInfo() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("UserReadableName", Build.MANUFACTURER + " " + Build.PRODUCT + " (api " + Integer.toString(Build.VERSION.SDK_INT) + ")");
        values.putAll(getBuildValues());
        values.putAll(getConfigurationValues());
        values.putAll(getDisplayMetricsValues());
        values.putAll(getPackageManagerValues());
        values.putAll(staticProperties);
        return values;
    }

    private Map<String, String> getBuildValues() {
        Map<String, String> values = new LinkedHashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            values.put("Build.HARDWARE", Build.HARDWARE);
            values.put("Build.RADIO", Build.RADIO);
            values.put("Build.BOOTLOADER", Build.BOOTLOADER);
        }
        values.put("Build.FINGERPRINT", Build.FINGERPRINT);
        values.put("Build.BRAND", Build.BRAND);
        values.put("Build.DEVICE", Build.DEVICE);
        values.put("Build.VERSION.SDK_INT", Integer.toString(Build.VERSION.SDK_INT));
        values.put("Build.MODEL", Build.MODEL);
        values.put("Build.MANUFACTURER", Build.MANUFACTURER);
        values.put("Build.PRODUCT", Build.PRODUCT);
        return values;
    }

    private Map<String, String> getConfigurationValues() {
        Map<String, String> values = new LinkedHashMap<>();
        Configuration config = context.getResources().getConfiguration();
        values.put("TouchScreen", Integer.toString(config.touchscreen));
        values.put("Keyboard", Integer.toString(config.keyboard));
        values.put("Navigation", Integer.toString(config.navigation));
        values.put("ScreenLayout", Integer.toString(config.screenLayout & 15));
        values.put("HasHardKeyboard", Boolean.toString(config.keyboard == Configuration.KEYBOARD_QWERTY));
        values.put("HasFiveWayNavigation", Boolean.toString(config.navigation == Configuration.NAVIGATIONHIDDEN_YES));
        return values;
    }

    private Map<String, String> getDisplayMetricsValues() {
        Map<String, String> values = new LinkedHashMap<>();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        values.put("Screen.Density", Integer.toString((int) (metrics.density * 160f)));
        values.put("Screen.Width", Integer.toString(metrics.widthPixels));
        values.put("Screen.Height", Integer.toString(metrics.heightPixels));
        return values;
    }

    private Map<String, String> getPackageManagerValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Platforms", TextUtils.join(",", NativeDeviceInfoProvider.getPlatforms()));
        values.put("SharedLibraries", TextUtils.join(",", NativeDeviceInfoProvider.getSharedLibraries(context)));
        values.put("Features", TextUtils.join(",", context.getPackageManager().getSystemSharedLibraryNames()));
        values.put("Locales", TextUtils.join(",", NativeDeviceInfoProvider.getLocales()));
        return values;
    }
}
