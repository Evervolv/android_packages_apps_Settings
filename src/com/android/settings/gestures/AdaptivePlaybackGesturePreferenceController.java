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

package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import evervolv.provider.EVSettings;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.VideoPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.RadioButtonPreference;

public class AdaptivePlaybackGesturePreferenceController extends AbstractPreferenceController
        implements RadioButtonPreference.OnClickListener, LifecycleObserver, OnResume, OnPause, PreferenceControllerMixin {

    private static final String KEY_NO_TIMEOUT = "adaptive_playback_timeout_none";
    private static final String KEY_30_SECS = "adaptive_playback_timeout_30_secs";
    private static final String KEY_1_MIN = "adaptive_playback_timeout_1_min";
    private static final String KEY_2_MIN = "adaptive_playback_timeout_2_min";
    private static final String KEY_5_MIN = "adaptive_playback_timeout_5_min";
    private static final String KEY_10_MIN = "adaptive_playback_timeout_10_min";

    private final String PREF_KEY_VIDEO = "gesture_adaptive_playback_video";
    private final String KEY = "gesture_adaptive_playback_category";
    private final Context mContext;

    static final int ADAPTIVE_PLAYBACK_TIMEOUT_NONE = 0;
    static final int ADAPTIVE_PLAYBACK_TIMEOUT_30_SECS = 30000;
    static final int ADAPTIVE_PLAYBACK_TIMEOUT_1_MIN = 60000;
    static final int ADAPTIVE_PLAYBACK_TIMEOUT_2_MIN = 120000;
    static final int ADAPTIVE_PLAYBACK_TIMEOUT_5_MIN = 300000;
    static final int ADAPTIVE_PLAYBACK_TIMEOUT_10_MIN = 600000;

    private VideoPreference mVideoPreference;

    private PreferenceCategory mPreferenceCategory;
    private RadioButtonPreference mTimeoutNonePref;
    private RadioButtonPreference mTimeout30SecPref;
    private RadioButtonPreference mTimeout1MinPref;
    private RadioButtonPreference mTimeout2MinPref;
    private RadioButtonPreference mTimeout5MinPref;
    private RadioButtonPreference mTimeout10MinPref;

    private SettingObserver mSettingObserver;

    public AdaptivePlaybackGesturePreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        mContext = context;

        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        if (!isAvailable()) {
            return;
        }
        mPreferenceCategory = screen.findPreference(getPreferenceKey());
        mTimeoutNonePref = makeRadioPreference(KEY_NO_TIMEOUT, R.string.adaptive_playback_timeout_none);
        mTimeout30SecPref = makeRadioPreference(KEY_30_SECS, R.string.adaptive_playback_timeout_30_secs);
        mTimeout1MinPref = makeRadioPreference(KEY_1_MIN, R.string.adaptive_playback_timeout_1_min);
        mTimeout2MinPref = makeRadioPreference(KEY_2_MIN, R.string.adaptive_playback_timeout_2_min);
        mTimeout5MinPref = makeRadioPreference(KEY_5_MIN, R.string.adaptive_playback_timeout_5_min);
        mTimeout10MinPref = makeRadioPreference(KEY_10_MIN, R.string.adaptive_playback_timeout_10_min);

        if (mPreferenceCategory != null) {
            mSettingObserver = new SettingObserver(mPreferenceCategory);
        }

        mVideoPreference = screen.findPreference(getVideoPrefKey());
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY;
    }

    public String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override
    public void onRadioButtonClicked(RadioButtonPreference preference) {
        int adaptivePlaybackTimeout = keyToSetting(preference.getKey());
        if (adaptivePlaybackTimeout != EVSettings.System.getIntForUser(mContext.getContentResolver(),
            EVSettings.System.ADAPTIVE_PLAYBACK_TIMEOUT, ADAPTIVE_PLAYBACK_TIMEOUT_30_SECS,
                    UserHandle.USER_CURRENT)) {
            EVSettings.System.putIntForUser(mContext.getContentResolver(),
                    EVSettings.System.ADAPTIVE_PLAYBACK_TIMEOUT, adaptivePlaybackTimeout,
                    UserHandle.USER_CURRENT);
        }
    }

    @Override
    public void updateState(Preference preference) {
        int adaptivePlayback = EVSettings.System.getIntForUser(mContext.getContentResolver(),
                EVSettings.System.ADAPTIVE_PLAYBACK_ENABLED, 0, UserHandle.USER_CURRENT);
        int adaptivePlaybackTimeout = EVSettings.System.getIntForUser(mContext.getContentResolver(),
                EVSettings.System.ADAPTIVE_PLAYBACK_TIMEOUT, ADAPTIVE_PLAYBACK_TIMEOUT_30_SECS,
                UserHandle.USER_CURRENT);
        boolean isEnabled = adaptivePlayback != 0;
        boolean isTimeoutNone = isEnabled && adaptivePlaybackTimeout == ADAPTIVE_PLAYBACK_TIMEOUT_NONE;
        boolean isTimeout30Sec = isEnabled && adaptivePlaybackTimeout == ADAPTIVE_PLAYBACK_TIMEOUT_30_SECS;
        boolean isTimeout1Min = isEnabled && adaptivePlaybackTimeout == ADAPTIVE_PLAYBACK_TIMEOUT_1_MIN;
        boolean isTimeout2Min = isEnabled && adaptivePlaybackTimeout == ADAPTIVE_PLAYBACK_TIMEOUT_2_MIN;
        boolean isTimeout5Min = isEnabled && adaptivePlaybackTimeout == ADAPTIVE_PLAYBACK_TIMEOUT_5_MIN;
        boolean isTimeout10Min = isEnabled && adaptivePlaybackTimeout == ADAPTIVE_PLAYBACK_TIMEOUT_10_MIN;
        if (mTimeoutNonePref != null && mTimeoutNonePref.isChecked() != isTimeoutNone) {
            mTimeoutNonePref.setChecked(isTimeoutNone);
        }
        if (mTimeout30SecPref != null && mTimeout30SecPref.isChecked() != isTimeout30Sec) {
            mTimeout30SecPref.setChecked(isTimeout30Sec);
        }
        if (mTimeout1MinPref != null && mTimeout1MinPref.isChecked() != isTimeout1Min) {
            mTimeout1MinPref.setChecked(isTimeout1Min);
        }
        if (mTimeout2MinPref != null && mTimeout2MinPref.isChecked() != isTimeout2Min) {
            mTimeout2MinPref.setChecked(isTimeout2Min);
        }
        if (mTimeout5MinPref != null && mTimeout5MinPref.isChecked() != isTimeout5Min) {
            mTimeout5MinPref.setChecked(isTimeout5Min);
        }
        if (mTimeout10MinPref != null && mTimeout10MinPref.isChecked() != isTimeout10Min) {
            mTimeout10MinPref.setChecked(isTimeout10Min);
        }

        if (adaptivePlayback == 0) {
            EVSettings.System.putIntForUser(mContext.getContentResolver(),
                    EVSettings.System.ADAPTIVE_PLAYBACK_TIMEOUT, ADAPTIVE_PLAYBACK_TIMEOUT_30_SECS,
                    UserHandle.USER_CURRENT);
            mTimeoutNonePref.setEnabled(false);
            mTimeout30SecPref.setEnabled(false);
            mTimeout1MinPref.setEnabled(false);
            mTimeout2MinPref.setEnabled(false);
            mTimeout5MinPref.setEnabled(false);
            mTimeout10MinPref.setEnabled(false);
        } else {
            mTimeoutNonePref.setEnabled(true);
            mTimeout30SecPref.setEnabled(true);
            mTimeout1MinPref.setEnabled(true);
            mTimeout2MinPref.setEnabled(true);
            mTimeout5MinPref.setEnabled(true);
            mTimeout10MinPref.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        if (mSettingObserver != null) {
            mSettingObserver.register(mContext.getContentResolver());
            mSettingObserver.onChange(false, null);
        }

        if (mVideoPreference != null) {
            mVideoPreference.onViewVisible();
        }
    }

    @Override
    public void onPause() {
        if (mSettingObserver != null) {
            mSettingObserver.unregister(mContext.getContentResolver());
        }

        if (mVideoPreference != null) {
            mVideoPreference.onViewInvisible();
        }
    }

    private int keyToSetting(String key) {
        switch (key) {
            case KEY_NO_TIMEOUT:
                return ADAPTIVE_PLAYBACK_TIMEOUT_NONE;
            case KEY_30_SECS:
                return ADAPTIVE_PLAYBACK_TIMEOUT_30_SECS;
            case KEY_1_MIN:
                return ADAPTIVE_PLAYBACK_TIMEOUT_1_MIN;
            case KEY_2_MIN:
                return ADAPTIVE_PLAYBACK_TIMEOUT_2_MIN;
            case KEY_5_MIN:
                return ADAPTIVE_PLAYBACK_TIMEOUT_5_MIN;
            case KEY_10_MIN:
                return ADAPTIVE_PLAYBACK_TIMEOUT_10_MIN;
            default:
                return ADAPTIVE_PLAYBACK_TIMEOUT_30_SECS;
        }
    }

    private RadioButtonPreference makeRadioPreference(String key, int titleId) {
        RadioButtonPreference pref = new RadioButtonPreference(mPreferenceCategory.getContext());
        pref.setKey(key);
        pref.setTitle(titleId);
        pref.setOnClickListener(this);
        mPreferenceCategory.addPreference(pref);
        return pref;
    }

    private class SettingObserver extends ContentObserver {
        private final Uri ADAPTIVE_PLAYBACK = EVSettings.System.getUriFor(
                EVSettings.System.ADAPTIVE_PLAYBACK_ENABLED);
        private final Uri ADAPTIVE_PLAYBACK_TIMEOUT = EVSettings.System.getUriFor(
                EVSettings.System.ADAPTIVE_PLAYBACK_TIMEOUT);

        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            mPreference = preference;
        }

        public void register(ContentResolver cr) {
            cr.registerContentObserver(ADAPTIVE_PLAYBACK, false, this);
            cr.registerContentObserver(ADAPTIVE_PLAYBACK_TIMEOUT, false, this);
        }

        public void unregister(ContentResolver cr) {
            cr.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri == null || ADAPTIVE_PLAYBACK.equals(uri)
                    || ADAPTIVE_PLAYBACK_TIMEOUT.equals(uri)) {
                updateState(mPreference);
            }
        }
    }
}
