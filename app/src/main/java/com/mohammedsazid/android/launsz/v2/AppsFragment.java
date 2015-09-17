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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.AppInfo;
import com.mohammedsazid.android.launsz.R;

import java.util.List;

public class AppsFragment extends Fragment {

    private static String filterStr;
    RecyclerView appsRv;
    Bundle bundle;
    private List<AppInfo> apps;
    private AppsService appsService;
    private boolean isAppsServiceBound;
    private RelativeLayout container;
    private GridLayoutManager layoutManager;

    private ServiceConnection appsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AppsService.AppsServiceBinder binder = (AppsService.AppsServiceBinder) service;
            appsService = binder.getService();
            isAppsServiceBound = true;

            appsService.getAppsDetails(new ICallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    // Once the service has finished loading the apps (if not already),
                    // get the list and create the adapter.
                    loadAppsInfo(filterStr);
                }
            }, getActivity().getPackageManager());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isAppsServiceBound = false;
        }
    };

    public AppsFragment() {
    }

    private void loadAppsInfo(@Nullable final String filterStr) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (appsService != null && pref.getBoolean(AppsService.FORCE_REFRESH, false)) {
            appsService.getAppsDetails(new ICallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    // Once the service has finished loading the apps (if not already),
                    // get the list and create the adapter.

                    if (filterStr != null && !filterStr.isEmpty()) {
                        apps = appsService.filterApps(filterStr);
                    } else {
                        apps = appsService.apps;
                    }

                    if (apps.size() == 0) {
                        TextView tv = new TextView(getActivity());
                        tv.setTextSize(36.0f);
                        tv.setTextColor(Color.WHITE);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        tv.setLayoutParams(params);
                        tv.setText("Oops, I sense something missing!");

                        container.removeAllViews();
                        container.addView(tv);
                        container.requestLayout();
                    } else {
                        AppsAdapter adapter = new AppsAdapter(getActivity(), apps, false);
                        appsRv.setAdapter(adapter);
                    }
                }
            }, getActivity().getPackageManager());
        } else if (isAppsServiceBound) {
            if (appsService != null) {
                apps = appsService.filterApps(filterStr);
            } else {
                apps = appsService.apps;
            }

            if (apps != null && apps.size() == 0) {
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                AppsAdapter adapter = new AppsAdapter(getActivity(), apps, false);
                appsRv.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAppsInfo(filterStr);
    }

    private void bindViews(View rootView) {
        appsRv = (RecyclerView) rootView.findViewById(R.id.apps_rv);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        this.container = (RelativeLayout) view.findViewById(R.id.rv_container);

        filterStr = null;
        bundle = this.getArguments();

        if (bundle != null) {
            filterStr = bundle.getString(AlphabetsAdapter.ALPHABET_CHARACTER, null);
        }

        bindViews(view);
        loadApps();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unbindService(appsServiceConnection);
    }

    private void loadApps() {
        layoutManager = new GridLayoutManager(
                getActivity(),              // context
                4,                          // span count
                GridLayoutManager.VERTICAL, // orientation
                false
        );
        appsRv.setLayoutManager(layoutManager);
        appsRv.setHasFixedSize(true);

        // Bind to the service
        Intent serviceIntent = new Intent(getActivity(), AppsService.class);

        // Start the service if it's not already running
//        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, appsServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
