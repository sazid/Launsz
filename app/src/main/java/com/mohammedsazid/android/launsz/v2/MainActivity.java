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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mohammedsazid.android.launsz.R;


public class MainActivity extends FragmentActivity {

    FrameLayout alphabetsFragmentContainer;
    PackageModificationReceiver modificationReceiver;

    ImageView previousTrackIv;
    ImageView playPauseIv;
    ImageView nextTrackIv;

    private void bindViews() {
        alphabetsFragmentContainer = (FrameLayout) findViewById(R.id.alphabets_fragment_container);
        previousTrackIv = (ImageView) findViewById(R.id.previousTrackBtn);
        playPauseIv = (ImageView) findViewById(R.id.playPauseTrackBtn);
        nextTrackIv = (ImageView) findViewById(R.id.nextTrackBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        filter.addDataScheme("package");

        modificationReceiver = new PackageModificationReceiver();
        registerReceiver(modificationReceiver, filter);

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
