package com.learning.dino.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dbulj on 25/10/2014.
 */
public class NerdLauncherFragment extends ListFragment {

    private static final String TAG = "NerdLauncherFragment";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "I've found " + activities.size() + " activities.");

        //sort the activity lable names returned by Package Manager as part of ResolveInfo
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(), b.loadLabel(pm).toString());
            }
        });

        //create ArrayAdapter that will create simple list item views to display the
        //label of an activity and set this adapter on the ListView
        //CHALLANGE 1, Ch23 - Replace this ArrayAdapter constructor call with the one bellow
        //ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(getActivity()
        //        , android.R.layout.simple_list_item_1
        //        , activities){
        ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(getActivity()
                , android.R.layout.activity_list_item
                , android.R.id.text1
                , activities){
            public View getView(int pos, View convertView, ViewGroup parent){
                PackageManager pm = getActivity().getPackageManager();
                View v = super.getView(pos, convertView, parent);

                //CHALLANGE 1, Ch23
                ResolveInfo ri = getItem(pos);
                LinearLayout l = (LinearLayout) v;
                l.setOrientation(LinearLayout.HORIZONTAL);
                ImageView iv = (ImageView)l.findViewById(android.R.id.icon);
                //int width = 72;
                //int height = 72;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                l.setPadding(10, 20, 10, 20);
                iv.setLayoutParams(params);
                iv.setImageDrawable(ri.loadIcon(pm));

                //Documentation says that simple_list_item_1 is a TextView, so cast it
                //so that you can set its text value
                //CHALLANGE 1, Ch 23 - Since we use now activity_list_item instead of simple_list_item_1,
                //your view can not be cast to TextView.  Instead it is a LinearLayout and then use
                //findViewById on it to find the text1 view.
                //TextView tv = (TextView)v;
                TextView tv = (TextView)l.findViewById(android.R.id.text1);
                tv.setTextSize(22);
                //ResolveInfo ri = getItem(pos);
                tv.setText(ri.loadLabel(pm));
                return v;
            }
        };
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        ResolveInfo ri = (ResolveInfo)l.getAdapter().getItem(position);
        ActivityInfo ai = ri.activityInfo;

        if (ai == null){
            return;
        }

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClassName(ai.applicationInfo.packageName, ai.name);
        //Add FLAG_ACTIVITY_NEW_TASK to start activity in a new task instead in NerdLauncher's task.
        //This way, when you start an activity from NerdLanucher and pull up Task Manager, you will
        //see separate task for that activity.
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
    }
}
