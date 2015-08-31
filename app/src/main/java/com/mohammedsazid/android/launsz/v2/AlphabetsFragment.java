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
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohammedsazid.android.launsz.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlphabetsFragment extends Fragment {

    private AppsService appsService;
    boolean isAppsServiceBound = false;

    private List<String> alphabetsList;
    private String[] alphabets = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

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

        return view;
    }

    private void loadGridView() {
        alphabetsList = new ArrayList<String>();
        alphabetsList.addAll(Arrays.asList(alphabets));

        // Set the layout manager and other stuffs
        alphabetsRv.setLayoutManager(new GridLayoutManager(
                getActivity(),
                4,
                GridLayoutManager.VERTICAL,
                false));
        alphabetsRv.setHasFixedSize(true);

        // Bind to the service
        Intent serviceIntent = new Intent(getActivity(), AppsService.class);
        getActivity().bindService(serviceIntent, appsServiceConnection, Context.BIND_AUTO_CREATE);

        AlphabetsAdapter adapter = new AlphabetsAdapter(getActivity(), alphabetsList);
        alphabetsRv.setAdapter(adapter);
    }

    private void loadAppsList() {

    }

    // Every service needs to be bound through a ServiceConnection object
    private ServiceConnection appsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AppsService.AppsServiceBinder binder = (AppsService.AppsServiceBinder) service;
            appsService = binder.getService();
            isAppsServiceBound = true;

            appsService.logMsg("This is a simple message sent from the fragment through the service.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isAppsServiceBound = false;
        }
    };

}
