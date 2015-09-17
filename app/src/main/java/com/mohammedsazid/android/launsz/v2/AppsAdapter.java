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

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mohammedsazid.android.launsz.AppDetail;
import com.mohammedsazid.android.launsz.R;
import com.mohammedsazid.android.launsz.v2.data.AppsInfoProvider;
import com.mohammedsazid.android.launsz.v2.data.LaunszContract;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter {

    FragmentActivity activity;
    boolean appDock = false;
    Uri path;
    private List<AppDetail> apps;

    public AppsAdapter(FragmentActivity activity, List<AppDetail> apps, boolean appDock) {
        this.apps = apps;
        this.activity = activity;
        this.appDock = appDock;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlphabetsViewHolder viewHolder = (AlphabetsViewHolder) holder;
        final AppDetail app = apps.get(position);

        path = Uri.withAppendedPath(Uri.parse(activity.getFilesDir().toString()), app.name.toString());
//        Log.d(AppsAdapter.class.getSimpleName(), path.toString());
        Glide.with(activity).load(path.toString()).into(viewHolder.appIconIv);

        if (!appDock) {
            if (app.name.toString().equals(activity.getApplicationContext().getPackageName())) {
                viewHolder.appLabelTv.setText(activity.getString(R.string.title_activity_settings));
            } else {
                viewHolder.appLabelTv.setText(app.label);
            }
        }

        try {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String packageName = app.name.toString();
                        Intent intent;

                        // Do not store the data for launching the settings activity
                        if (packageName.equals(activity.getApplicationContext().getPackageName())) {
                            // TODO: Launch SettingsActivity
                            Toast.makeText(activity, "Under development :p", Toast.LENGTH_SHORT).show();
//                            intent = new Intent(activity, SettingsActivity.class);
//                            activity.startActivity(intent);
                        } else {
                            intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
                            if (activity != null) {
                                ContentValues values = new ContentValues();
                                /*
                                The package name here is used by the content provider to either
                                insert or update the launch count of the particular app.

                                If the row is being inserted for the first time, the provider will set
                                the initial launch count to 1 by itself.

                                The handling of this insert/update works through the use of the particular
                                content uri.
                                 */
                                values.put(LaunszContract.AppsInfo.COLUMN_APP_PACKAGE_NAME, app.name.toString());
                                values.put(LaunszContract.AppsInfo.COLUMN_APP_LABEL, app.label.toString());

                                // Insert or update - handled automatically by the content provider
                                activity.getContentResolver().insert(
                                        Uri.parse(AppsInfoProvider.CONTENT_URI.toString() + "/apps/insert_or_update"),
                                        values
                                );

                                activity.startActivity(intent);

                                activity.overridePendingTransition(
                                        R.anim.slide_in_bottom, R.anim.slide_out_top
                                );
                            }
                        }
                    } catch (Exception e) {
                        activity.getSupportFragmentManager().popBackStack();
                        Toast.makeText(activity, "Oops, my mistake :/", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        String packageName = app.name.toString();
                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + packageName));

                        activity.startActivity(i);
                        activity.overridePendingTransition(
                                R.anim.slide_in_bottom, R.anim.slide_out_top
                        );
                    } catch (Exception e) {
                        activity.getSupportFragmentManager().popBackStack();
                        Toast.makeText(activity, "Oops, my mistake :/", Toast.LENGTH_SHORT).show();
                    }

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
        int layoutResource = appDock ? R.layout.appdock_rv_item : R.layout.apps_rv_item;
        view = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new AlphabetsViewHolder(view, viewType);
    }

    @Override
    public int getItemCount() {
        // 2 icon types are added to existing list of alphabets
        return apps.size();
    }

}
