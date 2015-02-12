package com.dxauxm.barrelgame;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/*
* Author : Usha
* Date : 30/Nov/2014
* Purpose : An activity which explains the rules of the game
*/
public class Rules extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);
		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"fonts/Segoe-Regular.ttf");
		TextView ruleheading = (TextView) findViewById(R.id.textView1);
		TextView rule1 = (TextView) findViewById(R.id.textView2);
		TextView rule2 = (TextView) findViewById(R.id.textView3);
		TextView rule3 = (TextView) findViewById(R.id.textView4);
		TextView rule4 = (TextView) findViewById(R.id.textView5);
		TextView rule5 = (TextView) findViewById(R.id.textView6);

		TextView textArray[] = new TextView[6];

		textArray[0] = ruleheading;
		textArray[1] = rule1;
		textArray[2] = rule2;
		textArray[3] = rule3;
		textArray[4] = rule4;
		textArray[5] = rule5;

		for (int i = 0; i < 6; i++) {
			textArray[i].setTypeface(typeFace);
			textArray[i].setTextColor(Color.BLACK);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
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
