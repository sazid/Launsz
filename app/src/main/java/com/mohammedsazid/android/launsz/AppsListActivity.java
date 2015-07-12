package com.mohammedsazid.android.launsz;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
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

    private PackageManager packageManager;
    private List<AppDetail> apps;
    private ListView listView;

    private String filterAlphabet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        filterAlphabet = getIntent().getStringExtra(HomeActivity.EXTRA_INITIAL_ALPHABET);

        loadApps();
        loadListView();
        addClickListener();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_apps_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
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
        listView = (ListView) findViewById(R.id.apps_listview);

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
    }

}
