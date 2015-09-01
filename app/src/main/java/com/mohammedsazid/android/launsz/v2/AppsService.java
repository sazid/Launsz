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

package com.mohammedsazid.android.launsz.v2;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mohammedsazid.android.launsz.AppDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AppsService extends Service {

    // TODO: Set this boolean to true whenever a new package is added or removed (broadcast reciever)
    private static boolean NEEDS_REFRESH = true;

    public static List<String> alphabetsList;
    /**
     * Key - The alphabet (A, B, C, D, ...)
     * Value - Number of apps in that alphabet
     */
    public static Map<String, Integer> alphabetsMap;
    public static String[] alphabets = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private PackageManager packageManager;
    public static List<AppDetail> apps;

    private final IBinder appsServiceBinder = new AppsServiceBinder();

    public AppsService() {
    }

    public void doRefresh() {
        NEEDS_REFRESH = true;
        loadAppsDetails();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadAppsDetails();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return appsServiceBinder;
    }

    public void logMsg(String msg) {
        Log.v(AppsService.class.getSimpleName(), msg);
    }

    public void loadAppsDetails() {
        packageManager = getPackageManager();

        if (NEEDS_REFRESH) {
            apps = new ArrayList<>();
            alphabetsMap = new TreeMap<>();
            alphabetsList = new ArrayList<>();

            alphabetsList.addAll(Arrays.asList(alphabets));

            for (int i = 0; i < alphabetsList.size(); i++) {
                alphabetsMap.put(alphabetsList.get(i), 0);
            }

            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);

            for (ResolveInfo ri : availableActivities) {
                AppDetail app = new AppDetail();

                app.label = ri.loadLabel(packageManager);
                app.name = ri.activityInfo.packageName;
                app.icon = ri.activityInfo.loadIcon(packageManager);

                apps.add(app);

                String initial = String.valueOf(app.label.toString().charAt(0));
                if (alphabetsMap.containsKey(initial)) {
                    alphabetsMap.put(initial, alphabetsMap.get(initial) + 1);
                }
            }

            java.util.Collections.sort(apps);

            NEEDS_REFRESH = false;
        }
    }

    public void getAppsDetails(ICallback iCallback) {
        iCallback.onStart();

        loadAppsDetails();

        iCallback.onFinish();
    }

    public class AppsServiceBinder extends Binder {
        AppsService getService() {
            return AppsService.this;
        }
    }

}
