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

package com.android.settings.gestures;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import evervolv.provider.EVSettings;

import com.android.internal.annotations.VisibleForTesting;

public class SwipeToScreenshotPreferenceController extends GesturePreferenceController {
    private static final String PREF_KEY_VIDEO = "swipe_to_screenshot_video";

    @VisibleForTesting
    protected static final String ENABLED_SETTING = EVSettings.System.SWIPE_TO_SCREENSHOT;

    @VisibleForTesting
    protected static final String TOGGLE_KEY = "swipe_to_screenshot";

    public SwipeToScreenshotPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), TOGGLE_KEY);
    }

    @Override
    protected String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return EVSettings.System.putInt(mContext.getContentResolver(), ENABLED_SETTING,
                isChecked ? 1 : 0);
    }

    @Override
    public boolean isChecked() {
        int enabled = EVSettings.Secure.getInt(mContext.getContentResolver(), ENABLED_SETTING, 0);
        return enabled != 0;
    }
}
