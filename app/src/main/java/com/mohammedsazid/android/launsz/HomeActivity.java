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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeActivity extends Activity {

    public static final String EXTRA_INITIAL_ALPHABET = "initial_alphabet";

    private PackageManager packageManager;
    private List<AppDetail> apps;
    private Set<String> matchedAlphabets;

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

        loadApps();
        loadAlphabetsGridView();
        addClickListener();
    }

    private void loadApps() {
        packageManager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);

        List<String> initials = new ArrayList<String>();

        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();

            app.label = ri.loadLabel(packageManager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(packageManager);

            apps.add(app);
            initials.add(String.valueOf(app.label.toString().charAt(0)));
        }

        matchedAlphabets = new HashSet<String>();

        for (String alphabet : alphabets) {
            for (String initial : initials) {
                if (initial.equals(alphabet)) {
                    matchedAlphabets.add(initial);
                }
            }
        }

        java.util.Collections.sort(apps);
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

                boolean matched = false;

                if ("*".equals(alphabetsList.get(position))) {
                    matched = true;
                } else {
                    for (String a : matchedAlphabets) {
                        if (a.equals(alphabetsList.get(position))) {
                            matched = true;
                        }
                    }
                }

                // Name of the app
                TextView alphabetLabelView = (TextView)
                        convertView.findViewById(R.id.item_alphabet_textView);
                alphabetLabelView.setText(alphabetsList.get(position));

                if (!matched) {
                    alphabetLabelView.setEnabled(false);
                    alphabetLabelView.setTextColor(Color.DKGRAY);
                }

                return convertView;
            }

            @Override
            public boolean isEnabled(int position) {
//                return super.isEnabled(position);
                if ("*".equals(alphabetsList.get(position))) {
                    return true;
                }

                boolean matched = false;
                for (String a : matchedAlphabets) {
                    if (a.equals(alphabetsList.get(position))) {
                        matched = true;
                    }
                }

                return matched;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
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

        alphabetGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if ("*".equals(alphabetsList.get(position))) {

                    new MaterialDialog.Builder(mContext)
                            .title("Menu")
                            .items(R.array.menu_items)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
//                                    Toast.makeText(mContext, String.valueOf(i) + ": " + charSequence, Toast.LENGTH_SHORT).show();

                                    switch (position) {
                                        case 0:
                                            break;
                                        case 1:
                                            Intent i = new Intent(Intent.ACTION_SET_WALLPAPER);
                                            startActivity(Intent.createChooser(i, "Select Wallpaper"));
                                            break;
                                        case 2:
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            })
                            .show();

                    return true;
                }


                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
//        super.onBackPressed();
    }
}
