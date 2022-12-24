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
import evervolv.hardware.LiveDisplayConfig;
import evervolv.hardware.LiveDisplayManager;

public class DisplayModePreferenceController extends BasePreferenceController {

    private static final String COLOR_PROFILE_TITLE = "live_display_color_profile_%s_title";

    private LiveDisplayManager mLiveDisplayManager;
    private LiveDisplayConfig mConfig;

    public DisplayModePreferenceController(Context context, String key) {
        super(context, key);
        mLiveDisplayManager = LiveDisplayManager.getInstance(context);
        mConfig = mLiveDisplayManager.getConfig();
    }

    @Override
    public int getAvailabilityStatus() {
        if (!mContext.getResources().getBoolean(
                com.evervolv.platform.internal.R.bool.config_enableLiveDisplay)) {
            return CONDITIONALLY_UNAVAILABLE;
        }
        return mConfig.hasFeature(LiveDisplayManager.FEATURE_DISPLAY_MODES)
                ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public CharSequence getSummary() {
        DisplayMode currentMode = mLiveDisplayManager.getCurrentDisplayMode();
        if (currentMode != null) {
            currentMode = mLiveDisplayManager.getDefaultDisplayMode();
        }
        return ResourceUtils.getLocalizedString(
                mContext.getResources(), currentMode.name, COLOR_PROFILE_TITLE);
    }
}
