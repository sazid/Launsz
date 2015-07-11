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

package com.mohammedsazid.android.launsz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends Activity {

    public static final String EXTRA_INITIAL_ALPHABET = "initial_alphabet";

    private Context mContext;
    private GridView alphabetGridView;
    private List<String> alphabetsList;
    private String[] alphabets = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "*"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;

        loadAlphabetsGridView();
        addClickListener();
    }

    private void loadAlphabetsGridView() {
        alphabetGridView = (GridView) findViewById(R.id.alphabets_gridView);

        alphabetsList = new ArrayList<String>();
        alphabetsList.addAll(Arrays.asList(alphabets));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.alphabets_list_item, alphabetsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                int heightPerRow = parent.getHeight();
                int totalRowNum = 7;

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.alphabets_list_item, null);
                }

                // Set the height of each child view of the GridView
                convertView.setMinimumHeight(heightPerRow / totalRowNum);

                // Name of the app
                TextView alphabetLabelView = (TextView)
                        convertView.findViewById(R.id.item_alphabet_textView);
                alphabetLabelView.setText(alphabetsList.get(position));

                return convertView;
            }
        };

        alphabetGridView.setAdapter(adapter);
    }

    private void addClickListener() {
        alphabetGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String alphabet = alphabetsList.get(position);

                Intent i = new Intent(mContext, AppsListActivity.class);
                i.putExtra(HomeActivity.EXTRA_INITIAL_ALPHABET, alphabet);

                startActivity(i);
            }
        });
    }

}
