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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeActivity extends Activity {

    public static final String EXTRA_INITIAL_ALPHABET = "initial_alphabet";

    private SharedPreferences sharedPrefs;
    private PackageManager packageManager;
    private List<AppInfo> apps;
    private Set<String> matchedAlphabets;
    private OnSwipeTouchListener swipeTouchListener;
    private boolean musicCtrlEnabled;

    private Context mContext;
    private GridView alphabetGridView;
    private List<String> alphabetsList;
    private String[] alphabets = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "#", "*",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        musicCtrlEnabled = sharedPrefs.getBoolean(getString(R.string.music_ctrl_key), false);
        HelperClass.dimBackground(this, sharedPrefs);

        loadApps();
        loadAlphabetsGridView();
        addClickListener();
        addSwipeListener(alphabetGridView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HelperClass.dimBackground(this, sharedPrefs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (musicCtrlEnabled) {
            swipeTouchListener.getGestureDetector().onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        return;
//        super.onBackPressed();
    }

    private void addSwipeListener(View v) {
        if (!musicCtrlEnabled) {
            return;
        }

        swipeTouchListener = new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                Toast.makeText(HomeActivity.this, "Next", Toast.LENGTH_SHORT).show();
                musicControl("next");
            }

            public void onSwipeLeft() {
                Toast.makeText(HomeActivity.this, "Previous", Toast.LENGTH_SHORT).show();
                musicControl("previous");
            }

            public void onSwipeTop() {
                Toast.makeText(HomeActivity.this, "Play/Pause", Toast.LENGTH_SHORT).show();
                musicControl("playpause");
            }

            public void onSwipeBottom() {
                Toast.makeText(HomeActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                musicControl("stop");
            }
        };

        v.setOnTouchListener(swipeTouchListener);
    }

    private void loadApps() {
        packageManager = getPackageManager();
        apps = new ArrayList<AppInfo>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);

        List<String> initials = new ArrayList<String>();

        for (ResolveInfo ri : availableActivities) {
            AppInfo app = new AppInfo();

            app.label = ri.loadLabel(packageManager);
            app.name = ri.activityInfo.packageName;
//            app.icon = ri.activityInfo.loadIcon(packageManager);

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
                } else if ("#".equals(alphabetsList.get(position))) {
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

                int color_enabled = sharedPrefs.getInt(
                        getString(R.string.color_enabled_key), Color.WHITE);
                int color_disabled = sharedPrefs.getInt(
                        getString(R.string.color_disabled_key), Color.DKGRAY);
                boolean bold_alphabets = sharedPrefs.getBoolean(
                        getString(R.string.alphabet_bold_key),
                        false
                );

                alphabetLabelView.setTextColor(color_enabled);
                if (!matched) {
                    alphabetLabelView.setEnabled(false);
                    alphabetLabelView.setTextColor(color_disabled);
                }

                if (bold_alphabets) {
                    alphabetLabelView.setTypeface(null, Typeface.BOLD);
                }

                return convertView;
            }

            @Override
            public boolean isEnabled(int position) {
//                return super.isEnabled(position);
                if ("*".equals(alphabetsList.get(position))) {
                    return true;
                } else if ("#".equals(alphabetsList.get(position))) {
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

    private void musicControl(String mode) {
        Intent downIntent;
        Intent upIntent;

        KeyEvent downEvent;
        KeyEvent upEvent;
        long eventtime = SystemClock.uptimeMillis();

        final String CMDTOGGLEPAUSE = "togglepause";
        final String CMDPAUSE = "pause";
        final String CMDPREVIOUS = "previous";
        final String CMDNEXT = "next";
        final String SERVICECMD = "com.android.music.musicservicecommand";
        final String CMDNAME = "command";
        final String CMDSTOP = "stop";

        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (mode) {
            case "playpause":
                downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                downEvent = new KeyEvent(
                        eventtime,
                        eventtime,
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                        0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);

                upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                upEvent = new KeyEvent(
                        eventtime,
                        eventtime,
                        KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                        0);
                upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                sendOrderedBroadcast(upIntent, null);
                break;
            case "stop":
                if (mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDSTOP);
                    HomeActivity.this.sendBroadcast(i);
                }
                break;
            case "next":
                if (mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDNEXT);
                    HomeActivity.this.sendBroadcast(i);
                }
                break;
            case "previous":
                if (mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDPREVIOUS);
                    HomeActivity.this.sendBroadcast(i);
                }
                break;
            default:
                throw new IllegalArgumentException("Please use these commands only: 'playpause', 'next', 'stop', 'previous");
        }
    }

    private void addClickListener() {
        alphabetGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String alphabet = alphabetsList.get(position);

                if ("#".equals(alphabet)) {
//                    HomeActivity.this.musicControl("playpause");
                    //TODO: Do something with the #
                    HomeActivity.this.showMenu();
                } else {
                    Intent i = new Intent(mContext, AppsListActivity.class);
                    i.putExtra(HomeActivity.EXTRA_INITIAL_ALPHABET, alphabet);

                    startActivity(i);
                }

            }
        });

/*
        alphabetGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if ("*".equals(alphabetsList.get(position))) {

                    HomeActivity.this.showMenu();

                    return true;
                }


                return false;
            }
        });
*/
    }

    private void showMenu() {
        new MaterialDialog.Builder(mContext)
                .title("Menu")
                .items(R.array.menu_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
//                                    Toast.makeText(mContext, String.valueOf(i) + ": " + charSequence, Toast.LENGTH_SHORT).show();

                        Intent i;

                        switch (position) {
                            case 0:
                                i = new Intent(mContext, SettingsActivity.class);
                                startActivity(i);
                                break;
                            case 1:
                                i = new Intent(Intent.ACTION_SET_WALLPAPER);
                                startActivity(Intent.createChooser(i, "Select Wallpaper"));
                                break;
                            case 2:
                                new MaterialDialog.Builder(mContext)
                                        .title("About")
                                        .content(R.string.about_summary)
                                        .show();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
    }

}
