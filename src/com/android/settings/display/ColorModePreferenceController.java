/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.settings.display;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;

import androidx.annotation.VisibleForTesting;

import com.android.settings.core.BasePreferenceController;

import com.google.common.primitives.Ints;

import evervolv.hardware.LiveDisplayConfig;
import evervolv.hardware.LiveDisplayManager;

public class ColorModePreferenceController extends BasePreferenceController {

    private LiveDisplayManager mLiveDisplayManager;
    private LiveDisplayConfig mConfig;
    private boolean mLiveDisplayEnabled;

    public ColorModePreferenceController(Context context, String key) {
        super(context, key);

        mLiveDisplayManager = LiveDisplayManager.getInstance(context);
        mConfig = mLiveDisplayManager.getConfig();
        mLiveDisplayEnabled = false;
        if (!mContext.getResources().getBoolean(
                com.evervolv.platform.internal.R.bool.config_enableLiveDisplay)) {
            mLiveDisplayEnabled = mConfig.hasFeature(LiveDisplayManager.FEATURE_DISPLAY_MODES);
        }
    }

    @Override
    public int getAvailabilityStatus() {
        if (mLiveDisplayEnabled) {
            return CONDITIONALLY_UNAVAILABLE;
        }

        final int[] availableColorModes = mContext.getResources().getIntArray(
                com.android.internal.R.array.config_availableColorModes);
        return mContext.getSystemService(ColorDisplayManager.class)
                .isDeviceColorManaged()
                && availableColorModes.length > 0
                && !ColorDisplayManager.areAccessibilityTransformsEnabled(mContext)
                && !mLiveDisplayEnabled ?
                AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    public CharSequence getSummary() {
        if (mLiveDisplayEnabled) {
            return mContext.getText(com.android.settings.R.string.summary_empty);
        }

        final int[] availableColorModes = mContext.getResources().getIntArray(
                com.android.internal.R.array.config_availableColorModes);
        final String[] availableVendorColorModes = mContext.getResources().getStringArray(
                com.android.settings.R.array.available_vendor_color_modes);
        if (availableVendorColorModes.length == availableColorModes.length) {
            int index = Ints.indexOf(availableColorModes, getColorMode());
            if (index != -1) {
                return availableVendorColorModes[index];
            }
            return mContext.getText(com.android.settings.R.string.summary_empty);
        }
        return ColorModeUtils.getColorModeMapping(mContext.getResources()).get(getColorMode());
    }

    @VisibleForTesting
    public int getColorMode() {
        return mContext.getSystemService(ColorDisplayManager.class).getColorMode();
    }
}
