/*
 * Copyright (C) 2017 The Android Open Source Project
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

import com.android.settings.core.BasePreferenceController;

import evervolv.hardware.HardwareManager;

public class DisplayModePreferenceController extends BasePreferenceController {

    private HardwareManager mHardware;

    public DisplayModePreferenceController(Context context, String key) {
        super(context, key);
        mHardware = HardwareManager.getInstance(context);
    }

    @Override
    public int getAvailabilityStatus() {
        return mHardware.getDisplayModes() != null && mHardware.getDisplayModes().length > 0 ? AVAILABLE : DISABLED_FOR_USER;
    }

    @Override
    public CharSequence getSummary() {
        return mHardware.getCurrentDisplayMode() != null
                    ? mHardware.getCurrentDisplayMode().name : mHardware.getDefaultDisplayMode().name;
    }
}
