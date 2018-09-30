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

package com.github.yeriomin.yalpstore.download;

import android.text.TextUtils;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.playstoreapi.AndroidAppPatchData;
import com.github.yeriomin.playstoreapi.AppFileMetadata;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.HttpCookie;
import com.github.yeriomin.playstoreapi.Split;
import com.github.yeriomin.yalpstore.Util;

import java.util.ArrayList;
import java.util.List;

public class RequestBuilder {

    static public List<Request> build(AndroidAppDeliveryData deliveryData, boolean delta) {
        List<Request> requests = new ArrayList<>();
        requests.add((delta && deliveryData.hasPatchData()) ? getDeltaFile(deliveryData) : getApkFile(deliveryData));
        RequestObb obbRequestMain = getObbFile(deliveryData, true);
        if (null != obbRequestMain) {
            requests.add(obbRequestMain);
            RequestObb obbRequestPatch = getObbFile(deliveryData, false);
            if (null != obbRequestPatch) {
                requests.add(obbRequestPatch);
            }
        }
        for (Split split: deliveryData.getSplitList()) {
            requests.add(getSplitFile(split));
        }
        if (deliveryData.getDownloadAuthCookieCount() > 0) {
            HttpCookie cookie = deliveryData.getDownloadAuthCookie(0);
            for (Request request: requests) {
                request.setCookieString(cookie.getName() + "=" + cookie.getValue());
            }
        }
        return requests;
    }

    private static RequestDelta getDeltaFile(AndroidAppDeliveryData deliveryData) {
        RequestDelta requestDelta = new RequestDelta();
        requestDelta.setPatchFormat(getPatchFormat(deliveryData.getPatchData()));
        if (!GooglePlayAPI.PATCH_FORMAT.GDIFF.equals(requestDelta.getPatchFormat())) {
            requestDelta.setGzipped(true);
        }
        requestDelta.setUrl(deliveryData.getPatchData().getDownloadUrl());
        requestDelta.setSize(deliveryData.getPatchData().getMaxPatchSize());
        requestDelta.setHash(Util.base64StringToByteArray(deliveryData.getSha1()));
        requestDelta.setBaseHash(Util.base64StringToByteArray(deliveryData.getPatchData().getBaseSha1()));
        return requestDelta;
    }

    private static Request getApkFile(AndroidAppDeliveryData deliveryData) {
        Request apkRequest = new RequestApk();
        if (TextUtils.isEmpty(deliveryData.getDownloadUrlGzipped())) {
            apkRequest.setUrl(deliveryData.getDownloadUrl());
        } else {
            apkRequest.setGzipped(true);
            apkRequest.setUrl(deliveryData.getDownloadUrlGzipped());
        }
        apkRequest.setSize(deliveryData.getDownloadSize());
        apkRequest.setHash(Util.base64StringToByteArray(deliveryData.getSha1()));
        return apkRequest;
    }

    private static RequestSplit getSplitFile(Split split) {
        RequestSplit request = new RequestSplit();
        request.setName(split.getName());
        if (TextUtils.isEmpty(split.getDownloadUrlGzipped())) {
            request.setUrl(split.getDownloadUrl());
        } else {
            request.setGzipped(true);
            request.setUrl(split.getDownloadUrlGzipped());
        }
        request.setSize(split.getSize());
        request.setHash(Util.base64StringToByteArray(split.getSha1()));
        return request;
    }

    private static RequestObb getObbFile(AndroidAppDeliveryData deliveryData, boolean main) {
        if (main && deliveryData.getAdditionalFileCount() == 0
            || !main && deliveryData.getAdditionalFileCount() == 1
        ) {
            return null;
        }
        AppFileMetadata obbFileMetadata = deliveryData.getAdditionalFile(main ? 0 : 1);
        RequestObb obbRequest = new RequestObb();
        obbRequest.setMain(main);
        if (TextUtils.isEmpty(obbFileMetadata.getDownloadUrlGzipped())) {
            obbRequest.setUrl(obbFileMetadata.getDownloadUrl());
        } else {
            obbRequest.setGzipped(true);
            obbRequest.setUrl(obbFileMetadata.getDownloadUrlGzipped());
        }
        obbRequest.setSize(obbFileMetadata.getSize());
        obbRequest.setVersionCode(obbFileMetadata.getVersionCode());
        return obbRequest;
    }

    private static GooglePlayAPI.PATCH_FORMAT getPatchFormat(AndroidAppPatchData patchData) {
        switch (patchData.getPatchFormat()) {
            case 1:
                return GooglePlayAPI.PATCH_FORMAT.GDIFF;
            case 2:
                return GooglePlayAPI.PATCH_FORMAT.GZIPPED_GDIFF;
            case 3:
                return GooglePlayAPI.PATCH_FORMAT.GZIPPED_BSDIFF;
            default:
                throw new RuntimeException("Unsupported patch format: " + patchData.getPatchFormat());
        }
    }
}
