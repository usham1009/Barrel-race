package com.dxauxm.barrelgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Author : Usha
 * Date : 25/Nov/2014
 * Purpose - This is the main screen, shows the list of options like New Game, High Score, Rules, Music play/stop, Credits, Exit Game. Each option has its own activity
 * 
 */
public class MainActivity extends Activity implements android.view.View.OnClickListener, ServiceConnection {
	
	// Indicates whether the activity is linked to service player.
	private boolean mIsBound = false;
		
	// Saves the binding instance with the service.
	private MusicService musicService;
	
	// If the player enters the name the flag is set to true
	Boolean nameFlg=false;
	
	String playerName="";
	// For storing all the buttons
	Button bNewGame, bHighScore, bHelp, bAbout, bExit, bMusic;
	Button buttonArray[] = new Button[6];
	
	Intent newGameIntent;
	
	// Used for toggling music on/off
	int toggleMusic=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Linking JAVA variables to XML Views
		bNewGame = (Button) findViewById(R.id.bNewGame);
		bHighScore = (Button) findViewById(R.id.bHighScore);
		bHelp = (Button) findViewById(R.id.bHelp);
		bAbout = (Button) findViewById(R.id.bAbout);
		bExit = (Button) findViewById(R.id.bExit);
		bMusic = (Button) findViewById(R.id.bMusic);

		bNewGame.setOnClickListener(this);
		bHighScore.setOnClickListener(this);
		bHelp.setOnClickListener(this);
		bAbout.setOnClickListener(this);
		bExit.setOnClickListener(this);
		bMusic.setOnClickListener(this);

		buttonArray[0] = bNewGame;
		buttonArray[1] = bHighScore;
		buttonArray[2] = bHelp;
		buttonArray[3] = bAbout;
		buttonArray[4] = bExit;
		buttonArray[5] = bMusic;

		
		Typeface typeFace2 = Typeface.createFromAsset(getAssets(),"fonts/Segoe-Regular.ttf");
		bExit.setText("EXIT GAME");
		
		// setting all button properties using the 
		for (int i = 0; i < 6; i++) {
			buttonArray[i].setTypeface(typeFace2);
			buttonArray[i].setTextColor(Color.BLACK);
		}
		
		// New Game intent
		newGameIntent = new Intent(this, NewGame.class);
		
		// Starting the service of the player, if not already started.
		Intent music = new Intent(this, MusicService.class);
		startService(music);
		doBindService();
	}
	
	@Override
	protected void onResume() {
    	super.onResume();
    	// so that when returning back to main screen user 
    	nameFlg=false;
    	playerName="";
	}

	@Override
	protected void onPause() {
		super.onPause();
		musicService.pause();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// Music Service is stopped on Exit and service is un-binded
		musicService.stop();
		doUnbindService();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * Author - Usha
	 * Purpose : Switch statement to switch between various user activities
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bNewGame:
			//dialog box for name if name is not set
			if(!nameFlg)
				dialogBoxName();
			break;
		case R.id.bHighScore:
			Intent highScore = new Intent(this, HighScore.class);
			startActivity(highScore);
			break;
		case R.id.bHelp:
			Intent help = new Intent(this, Rules.class);
			startActivity(help);
			break;
		case R.id.bAbout:
			Intent about = new Intent(this, Credits.class);
			startActivity(about);
			break;
		case R.id.bExit:
			dialogBox();
			break;
		case R.id.bMusic:
			toggleMusic++;
			if(toggleMusic%2==0)
			{
				musicService.start();
			}
			else
				musicService.stop();
			break;
		default:
		}
	}
	
	/*
	 * Author - 
	 * Purpose - Exit Dialog Box
	 */
	public void dialogBox()
	{
		Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Segoe-UI-Symbol.ttf");
  		// custom dialog
  		final Dialog dialog = new Dialog(this);
  		dialog.setContentView(R.layout.exit_confirmation);
  		dialog.setTitle("Are you sure you want to exit the game?");
  		
  		// set the custom dialog components - text and button, image set in xml
  		TextView text = (TextView) dialog.findViewById(R.id.text);
  		text.setTypeface(typeFace);

  		Button dialogBtnYes = (Button) dialog.findViewById(R.id.dialogBtnYes);
  		dialogBtnYes.setTypeface(typeFace);
  		// if button is clicked, close the custom dialog
  		dialogBtnYes.setOnClickListener(new OnClickListener() {
  			@Override
  			public void onClick(View v) {
  				Log.i("Exit, Yes pressed :", "Will exit the game");
  				dialog.dismiss();
  				// we cancel the dialog so that the window is not leaked
  				dialog.cancel();
  				// close the activity
  				finish();
  			}
  		});
  		
  		Button dialogBtnNo = (Button) dialog.findViewById(R.id.dialogBtnNo);
  		dialogBtnNo.setTypeface(typeFace);
  		// if button is clicked, close the custom dialog
  		dialogBtnNo.setOnClickListener(new OnClickListener() {
  			@Override
  			public void onClick(View v) {
  				Log.i("Exit, No pressed :", "Will not exit");
  				dialog.cancel();
  				dialog.dismiss();
  			}
  		});
  		dialog.show();
	}
	
	/*
	 * Author - 
	 * Purpose - A alert dialog box for name
	 */
	@SuppressLint("InflateParams") public void dialogBoxName()
	{
		LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.name_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.username);
        
     // setup a dialog window
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Done! :)", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// get user input and set it to result
								System.out.println(input.getText());
								String name = input.getText().toString();
								if(checkValidity(name.trim()))
									nameFlg=true;
								else
									dialogBoxName();
								if(nameFlg)
								{
									Bundle bundle = new Bundle();
							        bundle.putString("Name", name);
							        newGameIntent.putExtras(bundle);
							        startActivity(newGameIntent);
							        dialog.cancel();
							        dialog.dismiss();
								}
							}
						})
				.setNegativeButton("Cancel! :(",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int id) {
								dialog.cancel();
								dialog.dismiss();
							}
						});

		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();
		alertD.show();
	}
	
	/*
	 * Author : Dhruv
	 * Purpose : Checks validity, first name field should not be empty
	 */
	private boolean checkValidity(String FName)
	{
		// checks first name for nulls or if it's empty
		if(FName.equals(null) || FName.length() <= 0)
		{
			// Non-Invasive Toast to tell the user he needs to enter a data first 
			Toast.makeText(getBaseContext(), "Please enter your Name !", Toast.LENGTH_SHORT).show();
			Log.i("Error can't continue:", "Name Field was empty!");
			return false;
		}
		
		return true;
	}
	
	// local methods used in connection/disconnection activity with music service.
	
		public void doBindService()
		{
			// activity connects to the service.
	 		Intent intent = new Intent(this, MusicService.class);
			bindService(intent, this, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
		
		public void doUnbindService()
		{
			// disconnects the music service activity.
			if(mIsBound)
			{
				unbindService(this);
	      		mIsBound = false;
			}
		}

		// interface connection with the music service activity
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			musicService = ((MusicService.ServiceBinder) binder).getService();
		}
		
		public void onServiceDisconnected(ComponentName name)
		{
			musicService = null;
		}
}// end of Class
