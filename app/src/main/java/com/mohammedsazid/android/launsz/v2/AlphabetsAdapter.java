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

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.R;

import java.util.List;
import java.util.Map;

public class AlphabetsAdapter extends RecyclerView.Adapter {

    private static final int ALPHABET_TYPE = 0;
//    private static final int HISTORY_TYPE = 1;
    private static final int ALL_TYPE = 2;
    private static final int MENU_TYPE = 3;

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
        AlphabetsViewHolder viewHolder = (AlphabetsViewHolder) holder;

        switch (viewHolder.viewType) {
            case ALPHABET_TYPE:
                // because first and the last items are icons
                int posForAlphabets = position - 1;

                viewHolder.alphabetTv.setText(alphabetsList.get(posForAlphabets));
                break;
            case MENU_TYPE:
                viewHolder.iconIv.setImageResource(R.drawable.ic_settings_white);
                break;
//            case HISTORY_TYPE:
//                viewHolder.iconIv.setImageResource(R.drawable.ic_history);
//                break;
            case ALL_TYPE:
                viewHolder.iconIv.setImageResource(R.drawable.ic_globe_white);
                break;
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
        // 2 icon types are added to existing list of alphabets
        return alphabetsMap.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {

        /*if (position == 0) {
            return HISTORY_TYPE;
        } else if (position == 1) {*/
        if (position == 0) {
            return ALL_TYPE;
        } else if (position == (getItemCount() - 1)) {
            return MENU_TYPE;
        }

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
