package com.dxauxm.barrelgame;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/*
 * Author - Usha
 * Purpose - The Credits activity, to give credit to the authors of the game
 */
public class Credits extends Activity {
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credits);
		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Segoe-Regular.ttf");
		TextView creditsHeading = (TextView) findViewById(R.id.textView1);
		TextView credits2 = (TextView) findViewById(R.id.textView2);
		TextView credits3 = (TextView) findViewById(R.id.textView3);
		TextView credits4 = (TextView) findViewById(R.id.textView4);
		TextView textArray[] = new TextView[4];

		textArray[0] = creditsHeading;
		textArray[1] = credits2;
		textArray[2] = credits3;
		textArray[3] = credits4;

		for (int i = 0; i < 4; i++) {
			textArray[i].setTypeface(typeFace);
			textArray[i].setTextColor(Color.BLACK);
		}
		
		credits2.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
		credits3.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
		credits4.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
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
}
