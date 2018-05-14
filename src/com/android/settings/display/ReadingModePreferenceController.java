/*
 * Copyright (C) 2016 The Android Open Source Project
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

import static evervolv.provider.EVSettings.System.DISPLAY_READING_MODE;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import evervolv.hardware.HardwareManager;
import evervolv.provider.EVSettings;

import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;

/**
 * A controller to manage the switch for showing battery percentage in the status bar.
 */
public class ReadingModePreferenceController extends BasePreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private HardwareManager mHardware;

    public ReadingModePreferenceController(Context context) {
        super(context, DISPLAY_READING_MODE);
        mHardware = HardwareManager.getInstance(context);
    }

    @Override
    public int getAvailabilityStatus() {
        return mHardware.isSupported(HardwareManager.FEATURE_READING_ENHANCEMENT) ?
                AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void updateState(Preference preference) {
        ((SwitchPreference) preference).setChecked(
                    mHardware.get(HardwareManager.FEATURE_READING_ENHANCEMENT));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        mHardware.set(HardwareManager.FEATURE_READING_ENHANCEMENT, (Boolean) newValue);
        return true;
    }
}
