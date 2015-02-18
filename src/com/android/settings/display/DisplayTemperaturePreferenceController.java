/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.display;

import static evervolv.hardware.LiveDisplayManager.MODE_OFF;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import evervolv.hardware.LiveDisplayManager;

public class DisplayTemperaturePreferenceController extends BasePreferenceController {

    private static final int STEP = 100;

    private LiveDisplayManager mLiveDisplayManager;

    private Preference mPreference;

    public DisplayTemperaturePreferenceController(Context context, String key) {
        super(context, key);
        mLiveDisplayManager = LiveDisplayManager.getInstance(context);
    }

    @Override
    public int getAvailabilityStatus() {
        if (!mContext.getResources().getBoolean(
                com.evervolv.platform.internal.R.bool.config_enableLiveDisplay)) {
            return CONDITIONALLY_UNAVAILABLE;
        }
        return !ColorDisplayManager.isNightDisplayAvailable(mContext)
                ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        final int currentMode = mLiveDisplayManager.getMode();
        mPreference = screen.findPreference(getPreferenceKey());
        mPreference.setEnabled(currentMode != MODE_OFF);
    }

    @Override
    public final void updateState(Preference preference) {
        final int day = mLiveDisplayManager.getDayColorTemperature();
        final int night = mLiveDisplayManager.getNightColorTemperature();

        mPreference.setSummary(mContext.getResources().getString(
                R.string.live_display_color_temperature_summary,
                roundUp(day), roundUp(night)));
    }

    private int roundUp(int value) {
        return ((value + STEP / 2) / STEP) * STEP;
    }
}
