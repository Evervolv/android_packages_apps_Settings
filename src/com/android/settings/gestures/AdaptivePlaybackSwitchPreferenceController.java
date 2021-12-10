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
import android.os.UserHandle;
import android.provider.Settings;
import android.widget.Switch;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import evervolv.provider.EVSettings;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

public class AdaptivePlaybackSwitchPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, OnMainSwitchChangeListener {

    private static final String KEY = "gesture_adaptive_playback_switch";
    private final Context mContext;

    @VisibleForTesting
    MainSwitchPreference mSwitch;

    public AdaptivePlaybackSwitchPreferenceController(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        if (isAvailable()) {
            Preference pref = screen.findPreference(getPreferenceKey());
            if (pref != null) {
                pref.setOnPreferenceClickListener(preference -> {
                    int adaptivePlayback = EVSettings.System.getIntForUser(mContext.getContentResolver(),
                            EVSettings.System.ADAPTIVE_PLAYBACK_ENABLED, 0, UserHandle.USER_CURRENT);
                    boolean isChecked = adaptivePlayback != 0;
                    EVSettings.System.putIntForUser(mContext.getContentResolver(),
                            EVSettings.System.ADAPTIVE_PLAYBACK_ENABLED, isChecked
                                    ? 0 : 1, UserHandle.USER_CURRENT);
                    return true;
                });
                mSwitch = (MainSwitchPreference) pref;
                mSwitch.setTitle(mContext.getString(R.string.adaptive_playback_main_switch_title));
                mSwitch.addOnSwitchChangeListener(this);
                updateState(mSwitch);
            }
        }
    }

    public void setChecked(boolean isChecked) {
        if (mSwitch != null) {
            mSwitch.updateStatus(isChecked);
        }
    }

    @Override
    public void updateState(Preference preference) {
        int adaptivePlayback = EVSettings.System.getIntForUser(mContext.getContentResolver(),
                EVSettings.System.ADAPTIVE_PLAYBACK_ENABLED, 0, UserHandle.USER_CURRENT);
        setChecked(adaptivePlayback != 0);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        EVSettings.System.putIntForUser(mContext.getContentResolver(),
                EVSettings.System.ADAPTIVE_PLAYBACK_ENABLED, isChecked ? 1 : 0, UserHandle.USER_CURRENT);
    }
}
