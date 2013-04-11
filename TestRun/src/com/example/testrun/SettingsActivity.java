package com.example.testrun;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	private ListView pList;
	private ActivityManager activityManager;
	private List<RunningAppProcessInfo> rapiList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        pList = (ListView)findViewById(R.id.pList);
        rapiList = new ArrayList<RunningAppProcessInfo>();
        
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void refreshList() {
    	List<String> names = new ArrayList<String>();
    	String[] namesArray = new String[0];
    	
    	rapiList.clear();

    	for(RunningAppProcessInfo rapi : activityManager.getRunningAppProcesses()) {
    		rapiList.add(rapi);
    		names.add(rapi.processName);
    	}
    	
    	Collections.sort(names);
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    			R.layout.rowlayout, R.id.label, names.toArray(namesArray));
    	
    	pList.setAdapter(adapter);
    }
    
    public void infoClicked(View view) {
    	RelativeLayout parentRow = (RelativeLayout)view.getParent();
        CharSequence rapiName = ((TextView)parentRow.getChildAt(0)).getText();
        
        //Credit to:
        //http://stackoverflow.com/questions/4421527/start-android-application-info-screen
    	Intent intent = new Intent();
    	final int apiLevel = Build.VERSION.SDK_INT;
    	
    	if (apiLevel >= 9) {
    		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    	    Uri uri = Uri.fromParts("package", rapiName.toString(), null);
    	    intent.setData(uri);
    	} else { 
	    	final String appPkgName = (apiLevel == 8 ? "pkg" : "com.android.setttings.ApplicationPkgName");
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
	        intent.putExtra(appPkgName, rapiName);
    	}
    	
    	startActivity(intent);
    }
    
    public void killClicked(View view) {
        RelativeLayout parentRow = (RelativeLayout)view.getParent();
        CharSequence rapiName = ((TextView)parentRow.getChildAt(0)).getText();
        
        for(RunningAppProcessInfo rapi : rapiList)
            if(rapi.processName.contentEquals(rapiName)){
                killProcess(rapi, false);
                break;
            }

        refreshList();
    }
    
    private void killProcess(RunningAppProcessInfo rapi, boolean force) {
        //TODO: Actual error handling
    	
    	if (rapi.processName.equals("stericson.busybox")) return; // don't kill the rooting application!!!
        try {
            Process rootProcess = Runtime.getRuntime().exec(new String[] { "su" });
            
            String command;
            if(force)
            	command = "kill -9 " + rapi.pid;
            else
            	command = "kill " + rapi.pid;
            
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(rootProcess.getOutputStream()), 2048);
            bw.write(command);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            //Handle a writer error
            Toast.makeText(getBaseContext(), "IO Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Handle not having root
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void fkillClicked(View view) {
    	RelativeLayout parentRow = (RelativeLayout)view.getParent();
        CharSequence rapiName = ((TextView)parentRow.getChildAt(0)).getText();
        
        for(RunningAppProcessInfo rapi : rapiList)
            if(rapi.processName.contentEquals(rapiName)){
                killProcess(rapi, true);
                break;
            }

        refreshList();
    }
    
    public void appSettings(View view) {
    	
    }
    
    public void appAbout(View view) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("About");
    	builder.setMessage("ProcDroid is an Android process management application developed for" +
    			" COMP 3000 by David Carson, Devin Denis, and Sergey Vershinin.\n\nThe interface icons used by the ProcDroid are taken from the Glyphish icon set available under the CC licence.");
    	builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
    	
    	builder.create().show();
    }

    public void appHelp(View view) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	LayoutInflater inflater = this.getLayoutInflater();
    	builder.setTitle("Help");
    	builder.setView(inflater.inflate(R.layout.helpbutton, null));
    	builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
    	
    	builder.create().show();
    }
    
    public void appRefresh(View view) {
    	refreshList();
    }
    
}
