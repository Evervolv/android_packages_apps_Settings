/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;
import android.net.Uri;
import android.text.format.DateFormat;
import android.text.TextUtils;

import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.DeviceInfoUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VendorPatchLevelPreferenceController extends BasePreferenceController {

    private static final String TAG = "VendorPatchCtrl";

    private static final String KEY_VENDOR_SECURITY_PATCH =
            "ro.vendor.build.security_patch";

    private String mVendorPatch;

    public VendorPatchLevelPreferenceController(Context context, String key) {
        super(context, key);
        mVendorPatch = SystemProperties.get(KEY_VENDOR_SECURITY_PATCH);
        if (!TextUtils.isEmpty(mVendorPatch)) {
            try {
                SimpleDateFormat template = new SimpleDateFormat("yyyy-MM-dd");
                Date patchLevelDate = template.parse(mVendorPatch);
                String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy");
                mVendorPatch = DateFormat.format(format, patchLevelDate).toString();
            } catch (ParseException e) {
                // parsing failed, use raw string
            }
        }
    }

    @Override
    public int getAvailabilityStatus() {
        String systemPatch = DeviceInfoUtils.getSecurityPatch();
        return !TextUtils.isEmpty(mVendorPatch) && !systemPatch.equals(mVendorPatch)
                ? AVAILABLE : CONDITIONALLY_UNAVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        return mVendorPatch;
    }
}
