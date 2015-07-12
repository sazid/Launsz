/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Mohammed Sazid-Al-Rashid
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mohammedsazid.android.launsz;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.view.WindowManager;


public class SettingsActivity extends Activity {

    public static Context mContext;
    private SharedPreferences sharedPrefs;

    public static void restartApp() {
        AlarmManager alm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alm.set(
                AlarmManager.RTC,
                System.currentTimeMillis() + 1000,
                PendingIntent.getActivity(mContext, 0, new Intent(mContext, HomeActivity.class), 0)
        );
        Process.killProcess(Process.myPid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        dimBackground(sharedPrefs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SettingsActivity.mContext = this;

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        dimBackground(sharedPrefs);
    }

    private void dimBackground(SharedPreferences sharedPrefs) {
        float dim_percentage = sharedPrefs.getInt(
                getString(R.string.bg_dim_amount_key),
                0
        );

        boolean dim_enabled = sharedPrefs.getBoolean(
                getString(R.string.bg_dim_key),
                false
        );

        if (dim_enabled) {
            WindowManager.LayoutParams windowManager = getWindow().getAttributes();
            windowManager.dimAmount = (dim_percentage / 100);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    @Override
    public void onBackPressed() {
        restartApp();
    }
}
