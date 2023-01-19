/*
 * Copyright (C) 2022 The Android Open Source Project
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

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

/**
 * Configures right/left position of back and recents buttons.
 */
public class ButtonNavigationSettingsSwapController extends TogglePreferenceController {

    public ButtonNavigationSettingsSwapController(Context context, String key) {
        super(context, key);
    }

    @Override
    public boolean isChecked() {
        return Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.NAV_BAR_BUTTON_SWAP_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.Secure.putIntForUser(mContext.getContentResolver(),
                Settings.Secure.NAV_BAR_BUTTON_SWAP_ENABLED, isChecked ? 1 : 0,
                UserHandle.USER_CURRENT);
    }

    @Override
    public int getAvailabilityStatus() {
        if (mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_navBarButtonSwapAvailable)) {
            return AVAILABLE;
        }

        return UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }
}
