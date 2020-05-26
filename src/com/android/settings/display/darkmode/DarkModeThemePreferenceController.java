/*
 * Copyright (C) 2021 The LineageOS Project
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

package com.android.settings.display.darkmode;

import static evervolv.provider.EVSettings.System.BERRY_BLACK_THEME;

import android.content.Context;

import androidx.preference.DropDownPreference;
import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;

import evervolv.provider.EVSettings;

/**
 * Controller to change and update the fast charging toggle
 */
public class DarkModeThemePreferenceController extends BasePreferenceController
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_DARK_UI_THEME = "dark_ui_theme";

    public DarkModeThemePreferenceController(Context context) {
        super(context, KEY_DARK_UI_THEME);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        int themeEnabled =
                EVSettings.System.getInt(mContext.getContentResolver(), BERRY_BLACK_THEME, 0);
        ((DropDownPreference) preference).setValue(themeEnabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final int enable = Integer.valueOf((String) newValue);
        return EVSettings.System.putInt(mContext.getContentResolver(),
                BERRY_BLACK_THEME, enable);
    }
}
