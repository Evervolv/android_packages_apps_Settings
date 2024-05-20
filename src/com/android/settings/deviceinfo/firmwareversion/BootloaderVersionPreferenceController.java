package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.android.settings.core.BasePreferenceController;

public class BootloaderVersionPreferenceController extends BasePreferenceController {

    public BootloaderVersionPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        boolean validBootloader =
            !TextUtils.isEmpty(Build.BOOTLOADER) && !Build.BOOTLOADER.equals("unknown");
        return validBootloader ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public CharSequence getSummary() {
        return Build.BOOTLOADER;
    }
}
