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

import com.android.settings.core.BasePreferenceController;

import com.evervolv.internal.util.ResourceUtils;
import evervolv.hardware.DisplayMode;
import evervolv.hardware.HardwareManager;

public class DisplayModePreferenceController extends BasePreferenceController {

    private static final String COLOR_PROFILE_TITLE = "live_display_color_profile_%s_title";

    private HardwareManager mHardware;

    public DisplayModePreferenceController(Context context, String key) {
        super(context, key);
        mHardware = HardwareManager.getInstance(context);
    }

    @Override
    public int getAvailabilityStatus() {
        final int[] availableColorModes = mContext.getResources().getIntArray(
                com.android.internal.R.array.config_availableColorModes);
        return availableColorModes.length <= 0
                && mHardware.isSupported(HardwareManager.FEATURE_DISPLAY_MODES) ?
                AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    public CharSequence getSummary() {
        final DisplayMode currentMode = mHardware.getCurrentDisplayMode() != null
                    ? mHardware.getCurrentDisplayMode() : mHardware.getDefaultDisplayMode();
        return ResourceUtils.getLocalizedString(
                mContext.getResources(), currentMode.name, COLOR_PROFILE_TITLE);
    }
}
