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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlphabetsFragment extends Fragment {

    boolean isAppsServiceBound = false;
    RecyclerView alphabetsRv;
//    TextView loadingTv;
    private AppsService appsService;

    // Every service needs to be bound through a ServiceConnection object
    public AlphabetsFragment() {
    }

    private void bindViews(View rootView) {
        alphabetsRv = (RecyclerView) rootView.findViewById(R.id.alphabets_rv);
//        loadingTv = (TextView) rootView.findViewById(R.id.alphabets_loading_text);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        // Set the layout manager and other stuffs
        List<String> alphabetsList = new ArrayList<>();
        alphabetsList.addAll(Arrays.asList(appsService.alphabets));
        AlphabetsAdapter adapter = new AlphabetsAdapter(getActivity(), alphabetsList);
        alphabetsRv.setAdapter(adapter);
        alphabetsRv.setLayoutManager(new GridLayoutManager(
                getActivity(),
                4,
                GridLayoutManager.VERTICAL,
                false));
        alphabetsRv.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
