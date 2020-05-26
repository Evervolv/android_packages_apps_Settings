/*
 * SPDX-FileCopyrightText: 2024 Evervolv
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.display.darkmode

import android.content.Context
import com.android.settings.R
import com.android.settings.core.TogglePreferenceController
import evervolv.provider.EVSettings

class DarkModeBlackThemePreferenceController(
    context: Context,
    key: String,
) : TogglePreferenceController(context, key) {

    override fun getAvailabilityStatus() =
        if (runCatching {
            mContext.packageManager.getApplicationInfo(
                "org.lineageos.overlay.customization.blacktheme", 0
            ).enabled
        }.getOrDefault(false)) AVAILABLE_UNSEARCHABLE else UNSUPPORTED_ON_DEVICE

    override fun isChecked(): Boolean =
        EVSettings.Secure.getInt(
            mContext.contentResolver,
            EVSettings.Secure.BERRY_BLACK_THEME,
            0
        ) == 1

    override fun setChecked(isChecked: Boolean): Boolean =
        EVSettings.Secure.putInt(
            mContext.contentResolver,
            EVSettings.Secure.BERRY_BLACK_THEME,
            if (isChecked) 1 else 0
        )

    override fun getSliceHighlightMenuRes(): Int {
        return R.string.menu_key_display
    }
}
