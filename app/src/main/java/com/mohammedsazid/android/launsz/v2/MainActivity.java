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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mohammedsazid.android.launsz.AppDetail;
import com.mohammedsazid.android.launsz.R;

import java.util.List;


public class MainActivity extends FragmentActivity {

    FrameLayout alphabetsFragmentContainer;
    ImageView previousTrackIv;
    ImageView playPauseIv;
    ImageView nextTrackIv;
    RecyclerView appDockRv;
    RelativeLayout container;
    private List<AppDetail> apps;
    private AppsService appsService;
    private boolean isAppsServiceBound = false;
    private ServiceConnection appsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AppsService.AppsServiceBinder binder = (AppsService.AppsServiceBinder) service;
            appsService = binder.getService();
            isAppsServiceBound = true;

            appsService.getAppsDetails(new ICallback() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFinish() {
                    // Once the service has finished loading the apps (if not already),
                    // get the list and create the adapter.

                    /*
                     * TODO: Create a new method and call it from here.
                     * The method will get the list of most used apps from the db and populate
                     * the app dock.
                     *
                     * Move all code from here into that new method.
                     */

                    apps = appsService.apps;

                    if (apps.size() == 0) {
                        TextView tv = new TextView(MainActivity.this);
//                        tv.setTextSize(24.0f);
                        tv.setTextColor(Color.WHITE);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        tv.setLayoutParams(params);
                        tv.setText("Your most used apps will appear here ;)");
                        tv.setGravity(Gravity.CENTER);

                        container.removeAllViews();
                        container.addView(tv);
                        container.requestLayout();
                    } else {
                        AppsAdapter adapter = new AppsAdapter(MainActivity.this, apps, true);
                        appDockRv.setAdapter(adapter);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isAppsServiceBound = false;
        }
    };

    private void bindViews() {
        container = (RelativeLayout) findViewById(R.id.app_dock_container);
        alphabetsFragmentContainer = (FrameLayout) findViewById(R.id.alphabets_fragment_container);
        previousTrackIv = (ImageView) findViewById(R.id.previousTrackBtn);
        playPauseIv = (ImageView) findViewById(R.id.playPauseTrackBtn);
        nextTrackIv = (ImageView) findViewById(R.id.nextTrackBtn);
        appDockRv = (RecyclerView) findViewById(R.id.app_dock_rv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        loadAppDock();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_bottom, R.anim.slide_out_top,
                        R.anim.slide_in_top, R.anim.slide_out_bottom
                )
                .replace(R.id.alphabets_fragment_container, new AlphabetsFragment())
                .commit();

        // Start the service to keep it running in the background
        Intent appsServiceIntent = new Intent(this, AppsService.class);
        startService(appsServiceIntent);
    }

    private void loadAppDock() {
        appDockRv.setHasFixedSize(true);
        appDockRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Bind to the service
        Intent serviceIntent = new Intent(this, AppsService.class);

        // Start the service if it's not already running
        startService(serviceIntent);
        bindService(serviceIntent, appsServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void musicControlCallbacks(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.previousTrackBtn:
                musicControl("previous");
                break;
            case R.id.playPauseTrackBtn:
                musicControl("playpause");
                break;
            case R.id.nextTrackBtn:
                musicControl("next");
                break;
            default:
                return;
        }
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
                    sendBroadcast(i);
                }
                break;
            case "next":
                if (mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDNEXT);
                    sendBroadcast(i);
                }
                break;
            case "previous":
                if (mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDPREVIOUS);
                    sendBroadcast(i);
                }
                break;
            default:
                throw new IllegalArgumentException("Please use these commands only: 'playpause', 'next', 'stop', 'previous");
        }
    }

}
