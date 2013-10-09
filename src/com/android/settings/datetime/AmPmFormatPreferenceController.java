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
package com.android.settings.datetime;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.text.format.DateFormat;

import evervolv.provider.EVSettings;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.graph.BatteryMeterDrawableBase;

import static evervolv.provider.EVSettings.System.STATUS_BAR_AM_PM;

/**
 * A controller to manage the switch for showing am/pm next to the time.
 */

public class AmPmFormatPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private static final int NORMAL = 0;
    private static final int SMALL = 1;
    private static final int HIDDEN = 2;

    public AmPmFormatPreferenceController(Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return STATUS_BAR_AM_PM;
    }

    @Override
    public void updateState(Preference preference) {
        int setting = EVSettings.System.getInt(mContext.getContentResolver(),
                    STATUS_BAR_AM_PM, 2);
        ((ListPreference) preference).setValue(Integer.toString(setting));
        updateSummary(preference, DateFormat.is24HourFormat(mContext) ? -1 : setting);
    }

    private void updateSummary(Preference preference, int value) {
        final int summary;
        switch (value) {
            case SMALL:
                summary = R.string.status_bar_am_pm_small;
                break;
            case HIDDEN:
                summary = R.string.status_bar_am_pm_hidden;
                break;
            case NORMAL:
                summary = R.string.status_bar_am_pm_normal;
                break;
            default:
                summary = R.string.status_bar_am_pm_info;
                break;
        }
        ((ListPreference) preference).setEnabled(value > 0 ? true : false);
        ((ListPreference) preference).setSummary(summary);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        EVSettings.System.putInt(mContext.getContentResolver(), STATUS_BAR_AM_PM,
                Integer.valueOf((String) newValue));
        updateSummary(preference, Integer.valueOf((String) newValue));
        return true;
    }
}
