/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.settings.security;

import android.content.Context;
import android.os.SELinux;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class SelinuxStatusPreferenceController extends BasePreferenceController {

    private static final String TAG = "SelinuxStatusPreferenceController";

    private int mIcon;
    private int mSummary;

    public SelinuxStatusPreferenceController(Context context, String key) {
        super(context, key);
        if (SELinux.isSELinuxEnabled() && SELinux.isSELinuxEnforced()) {
            mIcon = R.drawable.ic_shield_check;
            mSummary = R.string.selinux_status_enforcing;
        } else if (SELinux.isSELinuxEnabled()) {
            mIcon = R.drawable.ic_shield_warning;
            mSummary = R.string.selinux_status_permissive;
        } else {
            mIcon = R.drawable.ic_shield_alert;
            mSummary = R.string.selinux_status_disabled;
        }
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        screen.findPreference(getPreferenceKey()).setIcon(mIcon);
    }

    @Override
    public CharSequence getSummary() {
        return mContext.getString(mSummary);
    }
}
