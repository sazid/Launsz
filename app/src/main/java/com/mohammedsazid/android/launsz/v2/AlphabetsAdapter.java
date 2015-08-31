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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.R;

public class AlphabetsAdapter extends RecyclerView.Adapter {

    public AlphabetsAdapter() {
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlphabetsViewHolder viewHolder = (AlphabetsViewHolder) holder;
        viewHolder.alphabetTv.setText(String.valueOf(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alphabets_list_item, parent, false);

        AlphabetsViewHolder viewHolder = new AlphabetsViewHolder(view);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    // TODO: implement get item type
    /*
    > The very first icon will be a history icon (which will contain the user's most recently
      installed apps
    > The last icon will be a globe (which contains all the apps)
     */

    static class AlphabetsViewHolder extends RecyclerView.ViewHolder {

        TextView alphabetTv;

        public AlphabetsViewHolder(View itemView) {
            super(itemView);

            alphabetTv = (TextView) itemView.findViewById(R.id.item_alphabet_textView);
        }

    }

}
