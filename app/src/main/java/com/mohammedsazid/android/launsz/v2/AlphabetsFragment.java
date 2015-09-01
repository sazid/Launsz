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
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohammedsazid.android.launsz.R;

import java.util.List;
import java.util.Map;

public class AlphabetsFragment extends Fragment {

    private AppsService appsService;
    boolean isAppsServiceBound = false;

    private List<String> alphabetsList;
    private Map<String, Integer> alphabetsMap;

    RecyclerView alphabetsRv;

    private void bindViews(View rootView) {
        alphabetsRv = (RecyclerView) rootView.findViewById(R.id.alphabets_rv);
    }

    public AlphabetsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alphabets, container, false);

        bindViews(view);
        loadGridView();

        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() == 0 && appsService != null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    if (appsService != null && pref.getBoolean(AppsService.FORCE_REFRESH, false)) {
                        appsService.getAppsDetails(new ICallback() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onFinish() {
                                alphabetsList = appsService.alphabetsList;
                                alphabetsMap = appsService.alphabetsMap;

                                AlphabetsAdapter adapter = new AlphabetsAdapter(getActivity(), alphabetsMap, alphabetsList);
                                alphabetsRv.setAdapter(adapter);
                            }
                        });

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(AppsService.FORCE_REFRESH, false);
                        editor.commit();
                    }
                }
            }
        });

        return view;
    }

    private void loadGridView() {
        // Set the layout manager and other stuffs
        alphabetsRv.setLayoutManager(new GridLayoutManager(
                getActivity(),
                4,
                GridLayoutManager.VERTICAL,
                false));
        alphabetsRv.setHasFixedSize(true);

        loadAppsList();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (appsService != null && pref.getBoolean(AppsService.FORCE_REFRESH, false)) {
            appsService.getAppsDetails(new ICallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    alphabetsList = appsService.alphabetsList;
                    alphabetsMap = appsService.alphabetsMap;

                    AlphabetsAdapter adapter = new AlphabetsAdapter(getActivity(), alphabetsMap, alphabetsList);
                    alphabetsRv.setAdapter(adapter);
                }
            });

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(AppsService.FORCE_REFRESH, false);
            editor.commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        getActivity().unbindService(appsServiceConnection);
    }

    private void loadAppsList() {
        // Bind to the service
        Intent serviceIntent = new Intent(getActivity(), AppsService.class);

        // Start the service if it's not already running
        getActivity().startService(serviceIntent);
        getActivity().bindService(serviceIntent, appsServiceConnection, Context.BIND_AUTO_CREATE);
    }

    // Every service needs to be bound through a ServiceConnection object
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
                    alphabetsList = appsService.alphabetsList;
                    alphabetsMap = appsService.alphabetsMap;

                    AlphabetsAdapter adapter = new AlphabetsAdapter(getActivity(), alphabetsMap, alphabetsList);
                    alphabetsRv.setAdapter(adapter);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isAppsServiceBound = false;
        }
    };

}
