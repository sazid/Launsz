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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.R;

import java.util.List;
import java.util.Map;

public class AlphabetsAdapter extends RecyclerView.Adapter {

    //    private static final int HISTORY_TYPE = 1;
//    private static final int ALL_TYPE = 2;
//    private static final int MENU_TYPE = 3;
    public static final String ALPHABET_CHARACTER = "alphabet_character";
    private static final int ALPHABET_TYPE = 0;
    FragmentActivity activity;
    List<String> alphabetsList;
    private Map<String, Integer> alphabetsMap;

    public AlphabetsAdapter(FragmentActivity activity, Map<String, Integer> alphabetsMap, List<String> alphabetsList) {
        this.activity = activity;
        this.alphabetsMap = alphabetsMap;
        this.alphabetsList = alphabetsList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AlphabetsViewHolder viewHolder = (AlphabetsViewHolder) holder;
        View.OnClickListener onClickListener;

        // TODO: Allow user to turn on/off haptic feedback
        final boolean hapticFeedbackEnabled = false;
        if (hapticFeedbackEnabled) {
            holder.itemView.setHapticFeedbackEnabled(true);
        }

        switch (viewHolder.viewType) {
            case ALPHABET_TYPE:
                // because first and the last items are icons
//                final int posForAlphabets = position - 1;
                final String alphabet = alphabetsList.get(position);
                viewHolder.alphabetTv.setText(alphabet);
                final boolean isDisabled = alphabetsMap.get(alphabet) <= 0;

                // If there's no app starting with a given alphabet, disable that alphabet
                if (isDisabled) {
                    viewHolder.itemView.setClickable(false);
                    viewHolder.itemView.setFocusable(false);

                    // Change the color based on user preference
                    viewHolder.alphabetTv.setTextColor(Color.DKGRAY);
                } else {
                    onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (hapticFeedbackEnabled) {
                                viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                            }

                            Bundle bundle = new Bundle();
                            bundle.putString(ALPHABET_CHARACTER, alphabet);
                            Fragment fragment = new AppsFragment();
                            fragment.setArguments(bundle);

                            activity.getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.slide_in_bottom, R.anim.slide_out_top,
                                            R.anim.slide_in_top, R.anim.slide_out_bottom
                                    )
                                    .addToBackStack("apps_fragment")
                                    .add(R.id.alphabets_fragment_container, fragment)
                                    .commit();

                            TextView appDockAllAppsTv = (TextView) activity.findViewById(R.id.app_dock_all_apps_tv);
                            appDockAllAppsTv.setText(alphabet);
                        }
                    };

                    viewHolder.itemView.setOnClickListener(onClickListener);
                }
                break;
//            case MENU_TYPE:
//                viewHolder.iconIv.setImageResource(R.drawable.ic_settings_white);
//
//                onClickListener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new MaterialDialog.Builder(activity)
//                                .title("Menu")
//                                .items(R.array.menu_items)
//                                .itemsCallback(new MaterialDialog.ListCallback() {
//                                    @Override
//                                    public void onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
//                                        Intent i;
//
//                                        switch (position) {
//                                            case 0:
////                                        i = new Intent(activity, SettingsActivity.class);
////                                        activity.startActivity(i);
//                                                Toast.makeText(activity, "Under development :p", Toast.LENGTH_SHORT).show();
//                                                break;
//                                            case 1:
//                                                i = new Intent(Intent.ACTION_SET_WALLPAPER);
//                                                activity.startActivity(Intent.createChooser(i, "Select Wallpaper"));
//                                                break;
//                                            case 2:
//                                                new MaterialDialog.Builder(activity)
//                                                        .title("About")
//                                                        .content(R.string.about_summary)
//                                                        .show();
//                                                break;
//                                            default:
//                                                break;
//                                        }
//                                    }
//                                })
//                                .show();
//                    }
//                };
//
//                viewHolder.itemView.setOnClickListener(onClickListener);
//                viewHolder.iconIv.setOnClickListener(onClickListener);
//                break;
//            case HISTORY_TYPE:
//                viewHolder.iconIv.setImageResource(R.drawable.ic_history);
//                break;
//            case ALL_TYPE:
//                viewHolder.iconIv.setImageResource(R.drawable.ic_globe_white);
//                onClickListener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        activity.getSupportFragmentManager().beginTransaction()
//                                .setCustomAnimations(
//                                        R.anim.slide_in_bottom, R.anim.slide_out_top,
//                                        R.anim.slide_in_top, R.anim.slide_out_bottom
//                                )
//                                .addToBackStack("apps_fragment")
//                                .add(R.id.alphabets_fragment_container, new AppsFragment())
//                                .commit();
//                    }
//                };
//
//                viewHolder.itemView.setOnClickListener(onClickListener);
//                viewHolder.iconIv.setOnClickListener(onClickListener);
//
//                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        int layoutResourceId;

        if (viewType == ALPHABET_TYPE) {
            layoutResourceId = R.layout.alphabets_list_item;
        } else {
            layoutResourceId = R.layout.alphabets_icon_list_item;
        }

        view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);

        AlphabetsViewHolder viewHolder = new AlphabetsViewHolder(view, viewType);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        // For every another type of view add it to the original size
        // 0 icon types are added to existing list of alphabets
        return alphabetsMap.size();
    }

    @Override
    public int getItemViewType(int position) {

        /*if (position == 0) {
            return HISTORY_TYPE;
        } else if (position == 1) {
        if (position == 0) {
            return ALL_TYPE;
        } else if (position == (getItemCount() - 1)) {
            return MENU_TYPE;
        } */

        return ALPHABET_TYPE;
    }

    static class AlphabetsViewHolder extends RecyclerView.ViewHolder {

        protected int viewType;
        TextView alphabetTv;
        ImageView iconIv;

        public AlphabetsViewHolder(View itemView, int viewType) {
            super(itemView);

            this.viewType = viewType;

            if (viewType == ALPHABET_TYPE) {
                alphabetTv = (TextView) itemView.findViewById(R.id.item_alphabet_textView);
            } else {
                iconIv = (ImageView) itemView.findViewById(R.id.item_alphabet_icon_imageview);
            }

            itemView.setClickable(true);
        }

    }

}
