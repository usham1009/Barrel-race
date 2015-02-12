package com.dxauxm.barrelgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/*
 * Author - Usha, Dhruv
 * Date : 30/Nov/2014
 * Purpose : Function creates the directory and file at the path if they don't exist, and insert dummy data inside the file.
 *  Debugging Mechanism has been added, contains logging at all most all levels
 */
public class FileIO {
	
	/*
	 * Author - Usha
	 * Date : 29/Nov/2014
	 * Purpose : Function creates the directory and file at the path if they don't exist; and inserts dummy data inside the file.
	 */
	public boolean createPath(File path)
    {
		try {
			File directory = new File(path, "HighScore");
			File file = new File(path, "HighScore.txt");
			
			Log.i("Path of directory : ", path.toString());
			Log.i("Path of file : ", file.toString());
			
			/*
			 * For testing , deleting the file in each increment
			 */
//			System.out.println();
//			Log.i("Remove this line ", "Deleting file for testing first...");
//			file.delete(); 
			
    	
	    	// Checks if directory doesn't exist, if it doesn't then calls the create dir. function
	    	if(!directory.exists())
	    	{
	    		// Creates a dir. referenced by this file
	    		directory.mkdir();
	    	}
	    	// Checks if file doesn't exist, if it doesn't then calls the create file function
	    	if(!file.exists())
	    	{
	    		// Creates a file
	    		file.createNewFile();
	    	}
	    	
	    	// if false that means file is empty, insert it with dummy data
	    	if(fileIsEmpty(file))
	    	{
	    		Log.i("Is file empty ? :", "True");
	    		Log.i("Saving dummy records to file : ", "");
	    		String line = "Usha|12.131";
	    		saveToDisk(path, line);
	    		line = "Dhruv|12.133";
	    		saveToDisk(path, line);
	    		line = "Dost|20.101";
	    		saveToDisk(path, line);
	    		line = "Soniya|30.0";
	    		saveToDisk(path, line);
	    		line = "Ajay|21.0";
	    		saveToDisk(path, line);
	    		line = "Guru|14.124";
	    		saveToDisk(path, line);
	    		
	    	}
	    	else
	    		Log.i("Is file empty ? :", "False");

	    	return true; // if everything successful, return true
		}
		catch(Exception e)
		{
			Log.e("dir/file not created : ", e.getMessage());
			return false;
		}
    }
	
	/*
	 * Author - Dhruv
	 * Date : 29/Nov/2014
	 * Purpose : Function to save a record to disk. Parameters are the file path and the line to be saved. Return true if save successful
	 */
	public boolean saveToDisk( File path, String lineToWrite )
	{
		File file = new File(path, "HighScore.txt");
		try{
			
			// Assume default encoding
			// true denotes file will not be overwritten each time, but will append data in same file.
			FileWriter fileWriter =  new FileWriter(file, true);
			
			//wrap FileWriter in BufferedWriter
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			//write the line to file
			writer.write(lineToWrite);
			//add a new line at the end
			writer.newLine();
			
			// close and flush connections
	        fileWriter.flush();
	        writer.flush();
			writer.close();
			fileWriter.close();
			Log.i("Record written to file : ", "success");
			Log.i("Path where file was stored : ", file.toString());
			
			return true;
		}
		catch(Exception e)
		{
			Log.e("Record not written to file : ", e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Author - Usha
	 * Date : 30/Oct/2014
	 * Purpose : Function to check if file on disk is empty. Parameters are the file path. Returns true if file was empty
	 */
	public boolean fileIsEmpty(File file)
	{
		Boolean flg = true; // flag which tells us if the file is completely empty or not. True assumes file is empty
		try
		{
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			String line ;
			
			while((line = reader.readLine())!=null)
			{
				String delimited[] = null;
//				System.out.println("line"+line);
				// Split each line into it's constituent properties, then the result is stored in a string array.
				delimited = line.split("\\|");
				
//				System.out.println("length "+delimited.length);
				
				// This will make sure to take only the correct data in the correct format
				if(delimited.length >= 1)
				{
					flg=false;
				}
			}
			// close and flush connections
			reader.close();
			fileReader.close();
			Log.i("Path where file was check for empty : ", file.toString());
			return flg;
		}
		catch(Exception e)
		{
			Log.e("File Error : ", e.getMessage());
			e.printStackTrace();
			return flg;	
		}
	}
	
	/*
	 * Author - Dhruv
	 * Date : 29/Nov/2014
	 * Purpose : Function to read the file from disk. Parameters are the file path. Returns the List read.
	 */
	public List<Person> readFile(File path)
	{
		File file = new File(path, "HighScore.txt");
		List<Person> personList = new  ArrayList<Person>();
		try
		{
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			
			while((line = reader.readLine())!=null)
			{
				String delimited[] = null;
				// Split each line into it's constituent properties, then the result is stored in a string array.
				delimited = line.split("\\|");
				// This will make sure to take only the correct data in the correct format
				if(delimited.length == 2)
				{
					//	String array is used to initialize a Person object.
					Person p1 = new Person( delimited[0], delimited[1] );
					// Each Person object is stored in a List of Person
					personList.add(p1);
				}
			}
			// close and flush connections
			reader.close();
			fileReader.close();
			Log.i("File read successfully", "");
			Log.i("Path where file was read from : ", file.toString());
			return personList;
		}
		catch(Exception e)
		{
			Log.e("File not Read : ", e.getMessage());
			return null;	
		}
	}
	
	/*
	 * Author - Usha
	 * Date : 30/Nov/2014
	 * Purpose : Function to sort the names in ascending order
	 */
	public List<Person> sortListByTimeTaken(List<Person> lists)
    {
    	Collections.sort(lists);
    	
    	System.out.println("Printing Sorted");
    	for(Person p :lists)
    	{
    		System.out.println(p.getName() + " "+ p.getHighScore());
    	}
    	
    	return lists;
    }
	
	/*
	 * Author - Dhruv
	 * Date : 30/Nov/2014
	 * Purpose : used to store only the top 10 high score in the file
	 */
	public boolean storeHighScore( File path, String lineToWrite )
	{
		try{
			saveToDisk(path, lineToWrite);
			List<Person> readList = readFile(path);
			System.out.println("Before Sorted");
	    	for(Person p :readList)
	    	{
	    		System.out.println(p.getName() + " "+ p.getHighScore());
	    	}
			List<Person> sortedList=null;
			sortedList = sortListByTimeTaken(readList);
			
			File file = new File(path, "HighScore.txt");
			
			// Delete the existing File
			file.delete();
			
			// create a new file
			file.createNewFile();
			
			int i=0;
			for(Person p : sortedList)
		    {
				String newLine = p.getName()+"|"+p.getHighScore(); 
				if(i==10)
					break;
				else
					saveToDisk(path, newLine);
				i++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
}// end of Class FileIO
