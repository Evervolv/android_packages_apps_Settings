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

import static evervolv.hardware.LiveDisplayManager.MODE_AUTO;
import static evervolv.hardware.LiveDisplayManager.MODE_OFF;
import static evervolv.hardware.LiveDisplayManager.MODE_DAY;
import static evervolv.hardware.LiveDisplayManager.MODE_NIGHT;
import static evervolv.hardware.LiveDisplayManager.MODE_OUTDOOR;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.ArrayUtils;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import evervolv.hardware.LiveDisplayConfig;
import evervolv.hardware.LiveDisplayManager;

public class LiveDisplayPreferenceController extends BasePreferenceController
        implements Preference.OnPreferenceChangeListener {

    private LiveDisplayManager mLiveDisplayManager;
    private LiveDisplayConfig mConfig;

    private int mCurrentMode;
    private String[] mModeEntries;
    private String[] mModeValues;
    private String[] mModeSummaries;

    private boolean mNightDisplayAvailable;
    private ListPreference mPreference;

    public LiveDisplayPreferenceController(Context context, String key) {
        super(context, key);
        mLiveDisplayManager = LiveDisplayManager.getInstance(context);
        mConfig = mLiveDisplayManager.getConfig();
        mNightDisplayAvailable =
                ColorDisplayManager.isNightDisplayAvailable(context);
    }

    @Override
    public int getAvailabilityStatus() {
        if (mNightDisplayAvailable) {
            if (!mConfig.hasFeature(MODE_OUTDOOR)) {
                return UNSUPPORTED_ON_DEVICE;
            }
        }
        final boolean isLiveDisplayAvailable = mContext.getResources().getBoolean(
                com.evervolv.platform.internal.R.bool.config_enableLiveDisplay);
        return isLiveDisplayAvailable ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        mModeEntries = mContext.getResources().getStringArray(
                com.evervolv.platform.internal.R.array.live_display_entries);
        mModeValues = mContext.getResources().getStringArray(
                com.evervolv.platform.internal.R.array.live_display_values);
        mModeSummaries = mContext.getResources().getStringArray(
                com.evervolv.platform.internal.R.array.live_display_summaries);

        int[] removeIdx = null;
        // Remove outdoor mode from lists if there is no support
        if (!mConfig.hasFeature(MODE_OUTDOOR)) {
            removeIdx = ArrayUtils.appendInt(removeIdx,
                    ArrayUtils.indexOf(mModeValues, String.valueOf(MODE_OUTDOOR)));
        } else if (mNightDisplayAvailable) {
            final int autoIdx = ArrayUtils.indexOf(mModeValues, String.valueOf(MODE_AUTO));
            mModeSummaries[autoIdx] = mContext.getResources().getString(R.string.live_display_outdoor_mode_summary);
        }

        // Remove night display on HWC2
        if (mNightDisplayAvailable) {
            removeIdx = ArrayUtils.appendInt(removeIdx,
                    ArrayUtils.indexOf(mModeValues, String.valueOf(MODE_DAY)));
            removeIdx = ArrayUtils.appendInt(removeIdx,
                    ArrayUtils.indexOf(mModeValues, String.valueOf(MODE_NIGHT)));
        }

        if (removeIdx != null) {
            String[] entriesTemp = new String[mModeEntries.length - removeIdx.length];
            String[] valuesTemp = new String[mModeValues.length - removeIdx.length];
            String[] summariesTemp = new String[mModeSummaries.length - removeIdx.length];
            int j = 0;
            for (int i = 0; i < mModeEntries.length; i++) {
                if (ArrayUtils.contains(removeIdx, i)) {
                    continue;
                }
                entriesTemp[j] = mModeEntries[i];
                valuesTemp[j] = mModeValues[i];
                summariesTemp[j] = mModeSummaries[i];
                j++;
            }
            mModeEntries = entriesTemp;
            mModeValues = valuesTemp;
            mModeSummaries = summariesTemp;
        }

        mPreference = screen.findPreference(getPreferenceKey());
        mPreference.setEntries(mModeEntries);
        mPreference.setEntryValues(mModeValues);
    }

    @Override
    public final void updateState(Preference preference) {
        mCurrentMode = mLiveDisplayManager.getMode();

        int index = ArrayUtils.indexOf(mModeValues, String.valueOf(mCurrentMode));
        if (index < 0) {
            index = ArrayUtils.indexOf(mModeValues, String.valueOf(MODE_OFF));
        }

        mPreference.setSummary(mModeSummaries[index]);
        mPreference.setValue(String.valueOf(mCurrentMode));
    }

    @Override
    public final boolean onPreferenceChange(Preference preference, Object newValue) {
        return mLiveDisplayManager.setMode(Integer.valueOf((String)newValue));
    }
}
