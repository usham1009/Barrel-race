package com.dxauxm.barrelgame;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/*
 * Author - Usha
 * Purpose - The high score activity, to show the top ten high score
 */
public class HighScore extends Activity {

//	private final Context context = this;
	private ListView listView1;
	private TextView textHeading,textName, textTimeTaken;
	
	// to count the no of clicks to reverse the sort 
	private static int noOfClicksName=0, noOFClicksTimeTaken=0;
	
	// Custom ArrayAdapter for Person List
	PersonAdaptor adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		
		// Creating path for file and directory
        createPath(); // if file and path don't exist they're created
		
		Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/Segoe-UI-Symbol.ttf");
	    textHeading = (TextView)findViewById(R.id.textHeading);
	    textName = (TextView)findViewById(R.id.textName);
	    textTimeTaken = (TextView)findViewById(R.id.textTimeTaken);
	    textHeading.setTypeface(typeFace2);
	    textName.setTypeface(typeFace2);
	    textTimeTaken.setTypeface(typeFace2);
	}

	/*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : On Resume will fire the table redraw, and sets the onClick listener for the name and time text heading
	*/
    @Override
	protected void onResume() {
    	super.onResume();

    	// Initialize to create the table.
		makeTable();

		// To refresh the List in case a data has been added or removed
		adapter.notifyDataSetChanged();
		
		// When Name is clicked sort by First name, toggles the sort from A-Z and from Z-A
		textName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				++noOfClicksName;
				// calls the sorting function
				sortListByName();
			}
		});
		
		// When Phone No is clicked sort by First name, toggles the sort from A-z and from Z-A
		textTimeTaken.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				++noOFClicksTimeTaken;
				// calls the sorting function
				sortListByTimeTaken();
			}
		});
    } // end of onResume()
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.high_score, menu);
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
     * Author - Usha M
     * Date  - 30/Nov/2014
     * Purpose - Used for reading to the contact List. A stub function.
     */
    public List<Person> readContact(){
    	
    	FileIO fileIO = new FileIO();
    	List<Person> personList = fileIO.readFile( Environment.getExternalStorageDirectory() );
    	
    	fileIO.sortListByTimeTaken(personList);
    	
    	return personList;
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Creates a custom ArrayAdaptor List
	*/
    public boolean makeTable()
    {
    	// set the sort count to 0
    	noOfClicksName=0;
    	noOFClicksTimeTaken=0;
    	
    	//For setting the font
    	Typeface fontHeading=Typeface.createFromAsset(getAssets(),"fonts/Segoe-UI-Symbol.ttf");
    	Typeface fontBody=Typeface.createFromAsset(getAssets(),"fonts/Segoe-Regular.ttf");
    	
    	// read list form the disk
    	final List<Person> listPerson = readContact();
    	
        
    	// Call the Person Adaptor, Person Adaptor is our Customized ArrayAdaptor!
    	adapter = new PersonAdaptor(this, R.layout.listview_item_row, listPerson, fontHeading, fontBody);
        
    	listView1 = (ListView)findViewById(R.id.listView1);
             
        listView1.setAdapter(adapter);
        
        // set onTouch or onClick lister
        listView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                
            	// Call Edit function
            	System.out.println("click");
            }
        });
        
        //register for onLoongClick give context
        registerForContextMenu(listView1);  
        
        sortListByTimeTakenLowest();
    	return true;
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Sorts the list by Name, both form A-Z and from Z-A
	*/
    private void sortListByName()
    {
    	adapter.sort(new Comparator<Person>() {
    	    @Override
    	    public int compare(Person lhs, Person rhs) {
    	    	if(noOfClicksName%2==0)
    	    		return rhs.getName().compareTo(lhs.getName());
    	    	else
    	    		return lhs.getName().compareTo(rhs.getName());
    	    }
    	});
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Sorts the list by Phone No, both from 0-9 and from 9-0
	*/
    private void sortListByTimeTaken()
    {
    	adapter.sort(new Comparator<Person>() {
    	    @Override
    	    public int compare(Person lhs, Person rhs) {
    	    	if(noOFClicksTimeTaken%2==0)
    	    		return rhs.getHighScore().compareTo(lhs.getHighScore());
    	    	else
    	    		return lhs.getHighScore().compareTo(rhs.getHighScore());
    	    }
    	});
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Sorts the list in asacending by Time taken
	*/
    private void sortListByTimeTakenLowest()
    {
    	adapter.sort(new Comparator<Person>() {
    	    @Override
    	    public int compare(Person lhs, Person rhs) {
    	    		return lhs.getHighScore().compareTo(rhs.getHighScore());
    	    }
    	});
    }
    
    /*
     * Author - Usha M
     * Purpose - Used for creating the path. A stub function.
     */
    public boolean createPath()
    {
    	FileIO fileIO = new FileIO();
    	fileIO.createPath( Environment.getExternalStorageDirectory() );
    	Log.i("Create Path: " , Environment.getExternalStorageDirectory().toString() );
    	return true;
    }
    
}
