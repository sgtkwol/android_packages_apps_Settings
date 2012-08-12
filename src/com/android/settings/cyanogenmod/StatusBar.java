/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.android.settings.cyanogenmod;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";

    private static final String STATUS_BAR_BATTERY = "status_bar_battery";

    private static final String STATUSBAR_NOTIFICATION_ICONS = "status_bar_max_notifications";

    private static final String STATUS_BAR_CLOCK = "status_bar_show_clock";

    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";

    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";

    private static final String COMBINED_BAR_AUTO_HIDE = "combined_bar_auto_hide";

    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";

    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";

    private static final String STATUS_BAR_TRANSPARENCY = "pref_statusbar_transparency";

    private static final String STATUS_BAR_HW_RENDERING = "pref_statusbar_hw_rendering";

    private ListPreference mStatusBarAmPm;

    private ListPreference mStatusBarBattery;

    private ListPreference mStatusBarNotificationIcons;

    private ListPreference mStatusBarCmSignal;

    private CheckBoxPreference mStatusBarClock;

    private CheckBoxPreference mStatusBarBrightnessControl;

    private CheckBoxPreference mCombinedBarAutoHide;

    private ListPreference mStatusbarTransparency;

    private CheckBoxPreference mStatusBarHwRendering;

    private CheckBoxPreference mStatusBarNotifCount;

    private PreferenceCategory mPrefCategoryGeneral;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarClock = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_CLOCK);
        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarAmPm = (ListPreference) prefSet.findPreference(STATUS_BAR_AM_PM);
        mStatusBarBattery = (ListPreference) prefSet.findPreference(STATUS_BAR_BATTERY);
        mStatusBarNotificationIcons = (ListPreference) prefSet.findPreference(STATUSBAR_NOTIFICATION_ICONS);
        mCombinedBarAutoHide = (CheckBoxPreference) prefSet.findPreference(COMBINED_BAR_AUTO_HIDE);
        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);
        mStatusbarTransparency = (ListPreference) prefSet.findPreference(STATUS_BAR_TRANSPARENCY);
        mStatusBarHwRendering = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_HW_RENDERING);

        mStatusBarClock.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
        mStatusBarHwRendering.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_HW_RENDERING, 0) == 1);
        mStatusbarTransparency.setEnabled(mStatusBarHwRendering.isChecked());

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.TIME_12_24) == 24) {
                mStatusBarAmPm.setEnabled(false);
                mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
            }
        } catch (SettingNotFoundException e ) {
        }

        int statusbarTransparency = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_TRANSPARENCY, 100);
        mStatusbarTransparency.setValue(String.valueOf(statusbarTransparency));
        mStatusbarTransparency.setOnPreferenceChangeListener(this);

        int statusBarAmPm = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM, 2);
        mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
        mStatusBarAmPm.setOnPreferenceChangeListener(this);

        int statusBarBattery = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int maxNotIcons = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.MAX_NOTIFICATION_ICONS, 2);
        mStatusBarNotificationIcons.setValue(String.valueOf(maxNotIcons));
        mStatusBarNotificationIcons.setOnPreferenceChangeListener(this);

        int signalStyle = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mCombinedBarAutoHide.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.COMBINED_BAR_AUTO_HIDE, 0) == 1));

        mStatusBarNotifCount = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1));

        mPrefCategoryGeneral = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);

        if (Utils.isScreenLarge() && mStatusbarTransparency != null) {
            mPrefCategoryGeneral.removePreference(mStatusBarBrightnessControl);
            mPrefCategoryGeneral.removePreference(mStatusBarCmSignal);
            mPrefCategoryGeneral.removePreference(mStatusbarTransparency);
        } else {
            mPrefCategoryGeneral.removePreference(mCombinedBarAutoHide);
            mPrefCategoryGeneral.removePreference(mStatusBarNotificationIcons);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusbarTransparency) {
            int statusBarTransparency = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.STATUS_BAR_TRANSPARENCY, statusBarTransparency);
            return true;
	} else if (preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_AM_PM, statusBarAmPm);
            return true;
        } else if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY, statusBarBattery);
            return true;
        } else if (preference == mStatusBarNotificationIcons) {
            int maxNotIcons = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.MAX_NOTIFICATION_ICONS, maxNotIcons);
            return true;
        } else if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            return true;
        }
        return true;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mStatusBarHwRendering) {
            value = mStatusBarHwRendering.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_HW_RENDERING, value ? 1 : 0);
            mStatusbarTransparency.setEnabled(mStatusBarHwRendering.isChecked());
            return true;
        } else if (preference == mStatusBarClock) {
            value = mStatusBarClock.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            value = mStatusBarBrightnessControl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mCombinedBarAutoHide) {
            value = mCombinedBarAutoHide.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.COMBINED_BAR_AUTO_HIDE, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNotifCount) {
            value = mStatusBarNotifCount.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
