/*
 * Yalp Store
 * Copyright (C) 2018 Sergey Yeriomin <yeriomin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.github.yeriomin.yalpstore;

import android.opengl.GLES10;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class EglExtensionRetriever {

    public static List<String> getEglExtensions() {
        Set<String> glExtensions = new HashSet<>();
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        if (egl10 == null) {
            return new ArrayList<>();
        }
        EGLDisplay display = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl10.eglInitialize(display, new int[2]);
        int cf[] = new int[1];
        if (egl10.eglGetConfigs(display, null, 0, cf)) {
            EGLConfig[] configs = new EGLConfig[cf[0]];
            if (egl10.eglGetConfigs(display, configs, cf[0], cf)) {
                int[] a1 = new int[] {EGL10.EGL_WIDTH, EGL10.EGL_PBUFFER_BIT, EGL10.EGL_HEIGHT, EGL10.EGL_PBUFFER_BIT, EGL10.EGL_NONE};
                int[] a2 = new int[] {12440, EGL10.EGL_PIXMAP_BIT, EGL10.EGL_NONE};
                int[] a3 = new int[1];
                for (int i = 0; i < cf[0]; i++) {
                    egl10.eglGetConfigAttrib(display, configs[i], EGL10.EGL_CONFIG_CAVEAT, a3);
                    if (a3[0] != EGL10.EGL_SLOW_CONFIG) {
                        egl10.eglGetConfigAttrib(display, configs[i], EGL10.EGL_SURFACE_TYPE, a3);
                        if ((1 & a3[0]) != 0) {
                            egl10.eglGetConfigAttrib(display, configs[i], EGL10.EGL_RENDERABLE_TYPE, a3);
                            if ((1 & a3[0]) != 0) {
                                addExtensionsForConfig(egl10, display, configs[i], a1, null, glExtensions);
                            }
                            if ((4 & a3[0]) != 0) {
                                addExtensionsForConfig(egl10, display, configs[i], a1, a2, glExtensions);
                            }
                        }
                    }
                }
            }
        }
        egl10.eglTerminate(display);
        List<String> sorted = new ArrayList<>(glExtensions);
        Collections.sort(sorted);
        return sorted;
    }

    private static void addExtensionsForConfig(EGL10 egl10, EGLDisplay egldisplay, EGLConfig eglconfig, int ai[], int ai1[], Set<String> set) {
        EGLContext eglContext = egl10.eglCreateContext(egldisplay, eglconfig, EGL10.EGL_NO_CONTEXT, ai1);
        if (eglContext == EGL10.EGL_NO_CONTEXT) {
            return;
        }
        javax.microedition.khronos.egl.EGLSurface eglSurface = egl10.eglCreatePbufferSurface(egldisplay, eglconfig, ai);
        if (eglSurface == EGL10.EGL_NO_SURFACE) {
            egl10.eglDestroyContext(egldisplay, eglContext);
        } else {
            egl10.eglMakeCurrent(egldisplay, eglSurface, eglSurface, eglContext);
            String s = GLES10.glGetString(7939);
            if (!TextUtils.isEmpty(s)) {
                String as[] = s.split(" ");
                int i = as.length;
                Collections.addAll(set, as);
            }
            egl10.eglMakeCurrent(egldisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            egl10.eglDestroySurface(egldisplay, eglSurface);
            egl10.eglDestroyContext(egldisplay, eglContext);
        }
    }
}
