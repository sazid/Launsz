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

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mohammedsazid.android.launsz.AppDetail;
import com.mohammedsazid.android.launsz.R;

import java.util.List;
import java.util.Map;

public class AppsAdapter extends RecyclerView.Adapter {

    private List<AppDetail> apps;
    FragmentActivity activity;

    public AppsAdapter(FragmentActivity activity, List<AppDetail> apps) {
        this.apps = apps;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlphabetsViewHolder viewHolder = (AlphabetsViewHolder) holder;
        final AppDetail app = apps.get(position);

        viewHolder.appLabelTv.setText(app.label);
        viewHolder.appIconIv.setImageDrawable(app.icon);

        try {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String packageName = app.name.toString();
                    Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
                    activity.startActivity(intent);

                    activity.overridePendingTransition(
                            R.anim.slide_in_bottom, R.anim.slide_out_top
                    );
                }
            };

            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String packageName = app.name.toString();
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + packageName));

                    activity.startActivity(i);
                    activity.overridePendingTransition(
                            R.anim.slide_in_bottom, R.anim.slide_out_top
                    );

                    return true;
                }
            };

            viewHolder.itemView.setOnClickListener(onClickListener);
            viewHolder.itemView.setOnLongClickListener(onLongClickListener);
        } catch (Exception e) {
            Toast.makeText(activity, "Oops :/", Toast.LENGTH_SHORT).show();
        }
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
