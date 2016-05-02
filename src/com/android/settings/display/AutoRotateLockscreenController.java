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

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import evervolv.provider.EVSettings;

public class AutoRotateLockscreenController extends TogglePreferenceController implements
        Preference.OnPreferenceChangeListener {

    private boolean mAutoRotateDefault;

    public AutoRotateLockscreenController(Context context, String key) {
        super(context, key);
        mAutoRotateDefault = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_enableLockScreenRotation);
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }

    @Override
    public boolean isChecked() {
        boolean keyguardRotationEnabled =
                EVSettings.System.getInt(mContext.getContentResolver(),
                EVSettings.System.LOCKSCREEN_ROTATION, mAutoRotateDefault ? 1 : 0) != 0;
        return keyguardRotationEnabled;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return EVSettings.System.putInt(mContext.getContentResolver(),
                EVSettings.System.LOCKSCREEN_ROTATION, isChecked ? 1 : 0);
    }
}
