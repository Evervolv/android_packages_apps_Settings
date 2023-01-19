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

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import com.android.settings.core.BasePreferenceController;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ButtonNavigationSettingsSwapControllerTest {

    private static final String KEY_SWAP_BACK_RECENTS = "swap_back_recents";

    private Context mContext;
    private Resources mResources;
    private ButtonNavigationSettingsSwapController mController;

    @Before
    public void setUp() {
        mContext = spy(ApplicationProvider.getApplicationContext());
        mResources = mock(Resources.class);
        when(mContext.getResources()).thenReturn(mResources);

        mController = new ButtonNavigationSettingsSwapController(
                mContext, KEY_SWAP_BACK_RECENTS);
    }

    @Test
    public void isChecked_valueTrue_shouldReturnTrue() {
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.NAV_BAR_BUTTON_SWAP_ENABLED, 1);
        assertThat(mController.isChecked()).isTrue();
    }

    @Test
    public void isChecked_valueFalse_shouldReturnFalse() {
        Settings.Secure.putInt(mContext.getContentResolver(),
                Settings.Secure.NAV_BAR_BUTTON_SWAP_ENABLED, 0);
        assertThat(mController.isChecked()).isFalse();
    }

    @Test
    public void setChecked_valueFalse_shouldSetFalse() {
        mController.setChecked(false);
        assertThat(Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.NAV_BAR_BUTTON_SWAP_ENABLED, -1)).isEqualTo(0);
    }

    @Test
    public void setChecked_valueTrue_shouldSetTrue() {
        mController.setChecked(true);
        assertThat(Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.NAV_BAR_BUTTON_SWAP_ENABLED, -1)).isEqualTo(1);
    }

    @Test
    public void getAvailabilityStatus_swap_returnsAvailable() {
        when(mResources.getBoolean(
                com.android.internal.R.bool.config_navBarButtonSwapAvailable)).thenReturn(
                true);
        assertThat(mController.getAvailabilityStatus())
                .isEqualTo(BasePreferenceController.AVAILABLE);
    }

    @Test
    public void getAvailabilityStatus_swap_returnsDisabled() {
        when(mResources.getBoolean(
                com.android.internal.R.bool.config_navBarButtonSwapAvailable)).thenReturn(
                false);
        assertThat(mController.getAvailabilityStatus())
                .isEqualTo(BasePreferenceController.UNSUPPORTED_ON_DEVICE);
    }

}
