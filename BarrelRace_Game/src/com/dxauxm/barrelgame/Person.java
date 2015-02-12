package com.dxauxm.barrelgame;


/*
 * Author : Usha
 * Date : 29/Nov/2014
 * Purpose - This is a bean class for Person information. Class is used for storing Person Info. 
 * 
 */
public class Person implements Comparable<Person>{

	private String name;
	private String timeTaken;
    
    public Person(){
        super();
    }

    public Person(String name, String timeTaken) {
		super();
		this.name = name;
		this.timeTaken = timeTaken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHighScore() {
		return timeTaken;
	}

	public void setHighScore(String timeTaken) {
		this.timeTaken = timeTaken;
	}


	@Override
	public int compareTo(Person another) {
		
		String compareHighScore = ((Person) another).getHighScore(); 
		//ascending order
		return this.timeTaken.compareTo(compareHighScore);
	}
	
//	public static Comparator<Person> PersonScoreComparator  = new Comparator<Person>() {
//				
//				public int compare(Person lhs, Person rhs) {
//				
//				//ascending order
//				return lhs.getHighScore().compareTo(rhs.getHighScore());
//				
//				//descending order
//				//return fruitName2.compareTo(fruitName1);
//				}
//				
//	};
	
}// end of class