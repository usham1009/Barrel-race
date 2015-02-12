
package com.dxauxm.barrelgame;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;
import android.view.WindowManager;

/*
* Author : Usha
* Date : 30/Nov/2014
* Purpose : Activity for the New Game. Custom View is used which in turn uses Canvas to draw.
*/
public class NewGame extends Activity {
//    private static final String TAG = "appsrox.example.accelerometer.MainActivity";
//    private WakeLock mWakeLock;
	
    private SimulationView mSimulationView;
    protected static String Name = "";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		PowerManager mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
//		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
		super.onCreate(savedInstanceState);
		
		// Get the message from the intent
	    Bundle bundle = getIntent().getExtras();
	    Name = bundle.getString("Name");
	    
		
	    mSimulationView = new SimulationView(NewGame.this);
	    // Removing action bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    // Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(mSimulationView);
	    
	    LocalBroadcastManager.getInstance(this).registerReceiver(new MessageHandler(),
                new IntentFilter("kill"));
	}
	 
	@Override
	protected void onResume() {
	    super.onResume();
//	    mWakeLock.acquire();
	    mSimulationView.startSimulation();
	}
	 
	@Override
	protected void onPause() {
	    super.onPause();
	    mSimulationView.stopSimulation();
	}    
	

    private void killActivity() {
        finish();
    }

    public class MessageHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			killActivity();
		}
    }
}