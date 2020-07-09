/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.settings.security.screenlock;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;

import evervolv.app.ContextConstants;

public class ScreenOffFodPreferenceController extends BasePreferenceController {
    private static final String KEY_FOD_GESTURE = "fod_gesture";
    private Context mContext;

    private FingerprintManager mFingerprintManager;
    private PackageManager mPackageManager;

    public ScreenOffFodPreferenceController(Context context) {
        super(context, KEY_FOD_GESTURE);
        mContext = context;

        mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
        mPackageManager = context.getPackageManager();
    }

    @Override
    public int getAvailabilityStatus() {
        return (mFingerprintManager != null
                && mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints()
                && mPackageManager.hasSystemFeature(ContextConstants.Features.FOD))
                        ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }
}
