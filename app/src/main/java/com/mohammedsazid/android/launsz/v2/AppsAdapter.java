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

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.AppDetail;
import com.mohammedsazid.android.launsz.R;

import java.util.List;
import java.util.Map;

public class AppsAdapter extends RecyclerView.Adapter {

    private List<AppDetail> apps;

    public AppsAdapter(List<AppDetail> apps) {
        this.apps = apps;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlphabetsViewHolder viewHolder = (AlphabetsViewHolder) holder;
        AppDetail app = apps.get(position);

        viewHolder.appLabelTv.setText(app.label);
        viewHolder.appIconIv.setImageDrawable(app.icon);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_rv_item, parent, false);

        AlphabetsViewHolder viewHolder = new AlphabetsViewHolder(view, viewType);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        // 2 icon types are added to existing list of alphabets
        return apps.size();
    }

    static class AlphabetsViewHolder extends RecyclerView.ViewHolder {

        protected int viewType;
        TextView appLabelTv;
        ImageView appIconIv;

        public AlphabetsViewHolder(View itemView, int viewType) {
            super(itemView);

            this.viewType = viewType;
            appLabelTv = (TextView) itemView.findViewById(R.id.apps_rv_item_label);
            appIconIv = (ImageView) itemView.findViewById(R.id.apps_rv_item_icon);

            itemView.setClickable(true);
        }

    }

}
