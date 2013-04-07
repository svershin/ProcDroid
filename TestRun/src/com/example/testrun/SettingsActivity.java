package com.example.testrun;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
    	List<String> rapiNames = new ArrayList<String>();
    	String[] RAPIs = new String[0];
    	
    	for(RunningAppProcessInfo rapi : activityManager.getRunningAppProcesses()) {
    		rapiList.add(rapi);
    		rapiNames.add(rapi.processName);
    	}
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    			R.layout.rowlayout, R.id.label, rapiNames.toArray(RAPIs));
    	
    	pList.setAdapter(adapter);
    }
    
    public void infoClicked(View view) {
    	RelativeLayout parentRow = (RelativeLayout)view.getParent();
        CharSequence rapiName = ((TextView)parentRow.getChildAt(0)).getText();
        
        for(RunningAppProcessInfo rapi : rapiList)
            if(rapi.processName.contentEquals(rapiName)){
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	String messageString = String.format("name: %s\npid: %d\nuid: %d", rapi.processName, rapi.pid, rapi.uid);
            	builder.setTitle("Process Info");
            	builder.setMessage(messageString);
            	builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            	
            	builder.create().show();
                break;
            }

        refreshList();
    }
    
    public void killClicked(View view) {
        RelativeLayout parentRow = (RelativeLayout)view.getParent();
        CharSequence rapiName = ((TextView)parentRow.getChildAt(0)).getText();
        
        for(RunningAppProcessInfo rapi : rapiList)
            if(rapi.processName.contentEquals(rapiName)){
                killProcess(rapi);
                break;
            }

        refreshList();
    }
    
    private void killProcess(RunningAppProcessInfo rapi) {
        //TODO: Actual error handling
        try {
            Process rootProcess = Runtime.getRuntime().exec(new String[] { "su" });
            
            String command = "kill -9 " + rapi.pid;
            
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
    	// fKill button has been clicked
    	// For now, just color the row green
    	RelativeLayout parentRow = (RelativeLayout)view.getParent();
    	parentRow.setBackgroundColor(Color.GREEN);
    }
    
    public void appSettings(View view) {
    	
    }
    
    public void appAbout(View view) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("About");
    	builder.setMessage("About this application");
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
    
}
