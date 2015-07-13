package com.mohammedsazid.android.launsz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AppsListActivity extends Activity {

    private SharedPreferences sharedPrefs;
    private PackageManager packageManager;
    private List<AppDetail> apps;
    private ListView listView;
    private TextView alphabetTextView;
//    private OnSwipeTouchListener swipeTouchListener;

    private String filterAlphabet = null;

    @Override
    protected void onResume() {
        super.onResume();
        HelperClass.dimBackground(this, sharedPrefs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        filterAlphabet = getIntent().getStringExtra(HomeActivity.EXTRA_INITIAL_ALPHABET);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        HelperClass.dimBackground(this, sharedPrefs);

        loadApps();
        loadListView();
        addClickListener();
//        addSwipeListener();
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        swipeTouchListener.getGestureDetector().onTouchEvent(ev);
//        return super.dispatchTouchEvent(ev);
//    }

    private void loadApps() {
        packageManager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);

        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();

            app.label = ri.loadLabel(packageManager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(packageManager);

            if (filterAlphabet == null || filterAlphabet.equals("*")) {
                apps.add(app);
            } else if (app.label.toString().startsWith(filterAlphabet)) {
                apps.add(app);
            }
        }

        java.util.Collections.sort(apps);
    }

    private void loadListView() {
        final int color_enabled = sharedPrefs.getInt(
                getString(R.string.color_enabled_key),
                Color.WHITE
        );

        listView = (ListView) findViewById(R.id.apps_listview);
        alphabetTextView = (TextView) findViewById(R.id.alphabet_textview);
        alphabetTextView.setText(filterAlphabet);
        alphabetTextView.setTextColor(color_enabled);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(
                this,
                R.layout.apps_list_item,
                apps
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.apps_list_item, null);
                }

                // Icon of the app
                ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80);
//                appIcon.setLayoutParams(params);

                // Name of the app
                TextView appLabel = (TextView) convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);
                appLabel.setTextColor(color_enabled);

                // Package name of the app
//                TextView appName = (TextView) convertView.findViewById(R.id.item_app_name);
//                appName.setText(apps.get(position).name);

                return convertView;
            }
        };

        listView.setAdapter(adapter);
    }

    private void addClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = apps.get(position).name.toString();
                Intent i = packageManager.getLaunchIntentForPackage(packageName);
                startActivity(i);

                overridePendingTransition(
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right
                );
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = apps.get(position).name.toString();
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + packageName));

                startActivity(i);

                return true;
            }
        });
    }

//    private void addSwipeListener() {
//        swipeTouchListener = new OnSwipeTouchListener(this) {
//            public void onSwipeRight() {
//                onBackPressed();
//            }
//        };
//
//        listView.setOnTouchListener(swipeTouchListener);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}
