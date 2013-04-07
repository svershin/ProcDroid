package com.example.testrun;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.graphics.Color;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SettingsActivity extends Activity {
	private ListView pList;
	private ActivityManager activityManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        pList = (ListView)findViewById(R.id.pList);
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void refreshList() {
    	List<String> RAPIList = new ArrayList<String>();
    	String[] RAPIs = new String[0];
    	for(RunningAppProcessInfo rapi : activityManager.getRunningAppProcesses())
    		RAPIList.add(rapi.processName);
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    			R.layout.rowlayout, R.id.label, RAPIList.toArray(RAPIs));
    	
    	pList.setAdapter(adapter);
    }
    
    public void infoClicked(View view) {
    	// Info button has been clicked
    	// For now, just color the row blue
    	RelativeLayout parentRow = (RelativeLayout)view.getParent();
    	parentRow.setBackgroundColor(Color.BLUE);
    }
    
    public void killClicked(View view) {
    	// Kill button has been clicked
    	// For now, just color the row red
    	RelativeLayout parentRow = (RelativeLayout)view.getParent();
    	parentRow.setBackgroundColor(Color.RED);
    }
    
}
