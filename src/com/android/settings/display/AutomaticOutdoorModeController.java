/*
 * Copyright (C) 2016 The Android Open Source Project
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

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import evervolv.hardware.LiveDisplayConfig;
import evervolv.hardware.LiveDisplayManager;

public class AutomaticOutdoorModeController extends TogglePreferenceController implements
        Preference.OnPreferenceChangeListener {

    private LiveDisplayManager mLiveDisplayManager;
    private LiveDisplayConfig mConfig;

    public AutomaticOutdoorModeController(Context context, String key) {
        super(context, key);
        mLiveDisplayManager = LiveDisplayManager.getInstance(context);
        mConfig = mLiveDisplayManager.getConfig();
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
    }

    @Override
    public int getAvailabilityStatus() {
        if (!mContext.getResources().getBoolean(
                com.evervolv.platform.internal.R.bool.config_enableLiveDisplay)) {
            return CONDITIONALLY_UNAVAILABLE;
        }
        return !ColorDisplayManager.isNightDisplayAvailable(mContext) && mConfig.hasFeature(LiveDisplayManager.MODE_OUTDOOR)
                ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }

    @Override
    public boolean isChecked() {
        return mLiveDisplayManager.getFeature(LiveDisplayManager.MODE_OUTDOOR);
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return mLiveDisplayManager.setFeature(LiveDisplayManager.MODE_OUTDOOR, isChecked);
    }
}
