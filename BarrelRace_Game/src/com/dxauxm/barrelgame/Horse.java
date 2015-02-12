package com.dxauxm.barrelgame;


/*
* Author : Dhruv & Usha
* Date : 20/Nov/2014
* Purpose : Has all the properties related to the rider or horse... Has the the game realted logic
*/
public class Horse {
	/* Coefficient of Restitution : to be used for calculating the bounce back speed */
//    private static final float COR = 0.7f;
    
	/*
	 * Current position of the horse
	 */
    public float mPosX;
    public float mPosY;
    
    /*
     * The displacement of the horse in the specified time period
     */
    private float dt,dX,dY;
    
    // The current timestamp at which the 
    long timestamp;
    
    int progress1=0, progress2=0, progress3=0;
    
    // Angle made by barrel and line
    static float angleBarrel1 = 0, angleBarrel2 = 0 ,angleBarrel3 = 0 ;
    
    // Position of first hole, plus the distance which is the distance between the point and the hole
    private float horsePointX, horsePointY;
    
    private boolean collsion1=false,collsion2=false,collsion3=false,collsion4=false;
    
    //flags
    private boolean barrel1Quad1=false,barrel1Quad2=false,barrel1Quad3=false,barrel1Quad4=false,barrel1=false;
    private boolean barrel2Quad1=false,barrel2Quad2=false,barrel2Quad3=false,barrel2Quad4=false,barrel2=false;
    private boolean barrel3Quad1=false,barrel3Quad2=false,barrel3Quad3=false,barrel3Quad4=false,barrel3=false;
    
    
    /*
	* Author : Dhruv
	* Date : 28/Nov/2014
	* Purpose : The function updates the position of the horse by taking the accelerometer values, then calculating the displacement of the horse in that period and adding that displacement to the current position of the horse
	*/
    public void updatePosition(float mSensorX, float mSensorY, float mSensorZ, long mSensorTimeStamp) {
    	
    	this.timestamp = mSensorTimeStamp;// just using for printing
    	// get the difference in time
        dt = ( (System.nanoTime() - timestamp) / 1000000000.0f )  ;
        
        // Calculate the displacement 
        dX = mSensorX * dt* dt * 800;
        dY = mSensorY * dt* dt * 800;
        
        // Calculate the new positions ac cto the displacements
        mPosX += dX;
        mPosY += dY;
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Algo for calculating the collisions with the Boundary
	*/
    public void collisionBoundary() {
    	if(mPosX <= 0)
    	{
    		mPosX=1;
    		if(!collsion1)
    		{
    			SimulationView.penaltyTime +=5;
    			collsion1=true;
    		}
    	}
    	if(mPosY <= 0)
    	{
    		mPosY=1;
    		if(!collsion2)
    		{
    			SimulationView.penaltyTime +=5;
    			collsion2=true;
    		}
    	}
    	if(mPosX + SimulationView.HORSE_SIZE >= SimulationView.width)
    	{
    		mPosX=SimulationView.width - SimulationView.HORSE_SIZE;
    		if(!collsion3)
    		{
    			SimulationView.penaltyTime +=5;
    			collsion3=true;
    		}
    	}
    	if(mPosY + SimulationView.HORSE_SIZE >= SimulationView.height)
    	{
    		mPosY=SimulationView.height - SimulationView.HORSE_SIZE;
    		if(!collsion4)
    		{
    			SimulationView.penaltyTime +=5;
    			collsion4=true;
    		}
    	}
    	
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Algo for calculating the collisions with the Boundary
	*/
    public void collisionBarrel() {
    	horsePointX = mPosX + SimulationView.HORSE_SIZE/2;
    	horsePointY = mPosY + SimulationView.HORSE_SIZE/2;
    	float dist1 = 0, dist2 = 0, dist3 = 0;
    	float x2 = (SimulationView.width/4);// - (SimulationView.BARREL_SIZE/2) ;
    	float y2 = (SimulationView.height/2);// - (SimulationView.BARREL_SIZE/2) ;
    	dist1 = calculateDistance( horsePointX, horsePointY, x2, y2 );
    	
    	x2 = (3*SimulationView.width/4);// - (SimulationView.BARREL_SIZE/2);
    	y2 = (SimulationView.height/2);// - (SimulationView.BARREL_SIZE/2);
    	dist2 = calculateDistance( horsePointX, horsePointY, x2, y2 );
    	
    	x2 = (SimulationView.width/2);// - (SimulationView.BARREL_SIZE/2);
    	y2 = (SimulationView.height/4);// - (SimulationView.BARREL_SIZE/2);
    	dist3 = calculateDistance( horsePointX, horsePointY, x2, y2 );
    	
    	if(dist1 <= 55 || dist2 <= 55 || dist3 <= 55)
    	{
    		// set the hitBarrel flag to 'true' and freeze game
    		SimulationView.hitBarrel = true;
    		SimulationView.start = 6; // code for stopping and freezing screen and timer
    	}
  }
    
    /*
	* Author : Usha
	* Date : 30/Nov/2014
	* Purpose : Algo for calculating the angle with the barrels
	*/
    public float calculateAngle(float centerX, float centerY, float pointX, float pointY, int barrelNo)
    {
    	double angle2Rad = Math.atan2(centerY - horsePointY, centerX - horsePointX);
    	double angle1Rad = Math.atan2(centerY - pointY, centerX - pointX);
    	
    	float angle1 = (float) (angle1Rad*180/Math.PI);
    	float angle2 = (float) (angle2Rad*180/Math.PI);
    	
    	switch(barrelNo)
    	{
    	case 1:
    		float difference = ( angle2 - angle1 );
        	if(difference < 0 )
        		difference = ( 360 - Math.abs(angle2) );
        	return difference;
    	case 2:
    		difference = ( angle1 - angle2 );
        	if(difference < 0 )
        		difference = ( 360 - Math.abs(angle1) );
        	return difference;
    	case 3:
    		if(angle2 < 0 && angle2<=-90 )
    			difference = ( Math.abs(angle2) - Math.abs(angle1) );
    		else if(angle2 < 0 && angle2 > -90 )
			difference = 270 + ( Math.abs(angle2) );
    		else 
        		difference = 180 - Math.abs(angle2) + 90;
        	return difference;
        default:
        		return 0;
    	}
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Algo for calculating if the horse went round the barrel or not
	*/
    public void barrelRound()
    {
    	/*
    	 * Calculate the angle made by each barrel and the mid point of horse
    	 */
    	angleBarrel1 = calculateAngle(3*SimulationView.width/4, SimulationView.height/2, 
    			SimulationView.width/2, SimulationView.height/2, 1);
    	angleBarrel2 = calculateAngle((SimulationView.width/4), (SimulationView.height/2), 
    			SimulationView.width/2, SimulationView.height/2, 2);
    	angleBarrel3 = calculateAngle((SimulationView.width/2), (SimulationView.height/4), 
    			SimulationView.width/2, SimulationView.height/2, 3);
    	
    	// OLD logic for barrel 1
//    	if( (angleBarrel1 > 0 && angleBarrel1< 90) || (progress1>0)  )
//		{
//    		if(progress1 == 0)
//    		{
//    			progress1 = 25;
//    			barrel1Quad1 = true;
//    		}
//    		if(angleBarrel1 > 90 && barrel1Quad1 )
//    		{
//    			if(progress1 == 25)
//				{
//    				progress1 = 50;
//    				barrel1Quad2=true;
//				}
//    			if(angleBarrel1 > 180 && barrel1Quad2)
//    			{
//    				if(progress1 == 50)
//    				{
//    					progress1 = 75;
//    					barrel1Quad3=true;
//    				}
//    				if(angleBarrel1 >270 && barrel1Quad3)
//					{
//    					progress1=100;
//    					barrel1Quad4=true;
//					}
//    			}
//    		}
//		}
    	
    	// logic for barrel 1
    	if( (angleBarrel1 > 0 && angleBarrel1< 90 && !barrel1Quad1 && progress1<25) )
		{
			progress1 = 25;
			barrel1Quad1 = true;
		}
    	if( (angleBarrel1 > 90 && angleBarrel1< 180 && barrel1Quad1 && !barrel1Quad2 && progress1<50) )
		{
			progress1 = 50;
			barrel1Quad2 = true;
		}
    	if( (angleBarrel1 > 180 && angleBarrel1< 270 && barrel1Quad1 && barrel1Quad2 && !barrel1Quad3 && progress1<75) )
		{
			progress1 = 75;
			barrel1Quad3 = true;
		}
    	if( (angleBarrel1 > 270 && angleBarrel1< 360 && barrel1Quad1 && 
    			barrel1Quad2 && barrel1Quad3 && !barrel1Quad4 && progress1<100) )
		{
			progress1 = 100;
			barrel1Quad4 = true;
		}
    	if(barrel1Quad1 && barrel1Quad2 && barrel1Quad3 && barrel1Quad4)
    	{
    		barrel1=true;
    	}
    	
    	
    	// logic for barrel 2
    	if( (angleBarrel2 > 0 && angleBarrel2< 90 && !barrel2Quad1 && barrel1) )
		{
			progress2 = 25;
			barrel2Quad1 = true;
		}
    	if( (angleBarrel2 > 90 && angleBarrel2< 180 && barrel2Quad1 && !barrel2Quad2) )
		{
    		progress2 = 50;
			barrel2Quad2 = true;
		}
    	if( (angleBarrel2 > 180 && angleBarrel2< 270 && barrel2Quad1 && barrel2Quad2 && !barrel2Quad3) )
		{
    		progress2 = 75;
			barrel2Quad3 = true;
		}
    	if( (angleBarrel2 > 270 && angleBarrel2< 360 && barrel2Quad1 && barrel2Quad2 && barrel2Quad3 && !barrel2Quad4) )
		{
    		progress2 = 100;
			barrel2Quad4 = true;
		}
    	if(barrel2Quad1 && barrel2Quad2 && barrel2Quad3 && barrel2Quad4)
    	{
    		barrel2=true;
    	}
    	
    	// logic for barrel 3
    	if( (angleBarrel3 > 0 && angleBarrel3< 90 && !barrel3Quad1 && barrel1 && barrel2) )
		{
			progress3 = 25;
			barrel3Quad1 = true;
		}
    	if( (angleBarrel3 > 90 && angleBarrel3< 180 && barrel3Quad1 && !barrel3Quad2) )
		{
    		progress3 = 50;
			barrel3Quad2 = true;
		}
    	if( (angleBarrel3 > 180 && angleBarrel3< 270 && barrel3Quad1 && barrel3Quad2 && !barrel3Quad3) )
		{
    		progress3 = 75;
			barrel3Quad3 = true;
		}
    	if( (angleBarrel3 > 270 && angleBarrel3< 360 && barrel3Quad1 && barrel3Quad2 && barrel3Quad3 && !barrel3Quad4) )
		{
    		progress3 = 100;
			barrel3Quad4 = true;
		}
    	if(barrel3Quad1 && barrel3Quad2 && barrel3Quad3 && barrel3Quad4)
    	{
    		barrel3=true;
    	}
    	if(// progress1==100 && progress2==100 && progress3==100 &&
    			barrel1 && barrel2 && barrel3)
    	{
    		SimulationView.roundComplete=true; // flag to show round was completed
    	}
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Algo for calculating the distance between the horse and the finish line
	*/
    public void pointToLineDistance() 
    {
    	// Point A is 0, 19*height/20-HORSE_SIZE-1
    	// Point B is width,19*height/20-HORSE_SIZE-1
    	// Point of horse is width/2 - HORSE_SIZE/2, 19*height/20 - HORSE_SIZE
    	
//    	float horseX = SimulationView.width/2 - SimulationView.HORSE_SIZE/2;
//    	float horseY = 19*SimulationView.height/20 - SimulationView.HORSE_SIZE;
    	
        //double normalLength = Math.sqrt((B.x-A.x)*(B.x-A.x)+(B.y-A.y)*(B.y-A.y));
    	double normalLength = Math.sqrt((SimulationView.width-0)*(SimulationView.width-0)+0);
        
        float a = (float) (Math.abs((mPosX-0)*(0) - (mPosY-19*SimulationView.height/20
        				- SimulationView.HORSE_SIZE-1)*(SimulationView.width-0))/normalLength);
        
        // Now 101 is the defaut distace
        if(a<=101 && SimulationView.roundComplete)
        {
        	SimulationView.finishComplete=true;
        	SimulationView.start=6; // to mark to stop timer
        }
        
      }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Algo for distance formula
	*/
    public float calculateDistance(float x1, float y1, float x2, float y2)
    {
    	Double squared = Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2); // (x1-x2)^2 + (y1-y2)^2
    	float hDist = (float) Math.sqrt(squared);
    	return hDist; 
    }
    
    /*
	* Author : Usha
	* Date : 30/Nov/2014
	* Purpose : Sets the inital position of the horse
	*/
    public void setInitialPosition(float mPosX, float mPosY) {
    	// set initial positions of ball
		this.mPosX = mPosX;
		this.mPosY = mPosY;
	}
    
    /*
	* Author : Usha
	* Date : 30/Nov/2014
	* Purpose : Algo for resetting all the values of the variables to default each time a game a restarted
	*/
    public void resetEverything()
    {
        progress1=0;
        progress2=0;
        progress3=0;
        
        // Angle made by barrel and line
        angleBarrel1 = 0;
        angleBarrel2 = 0;
        angleBarrel3 = 0 ;
        
        barrel1Quad1=false;barrel1Quad2=false;barrel1Quad3=false;barrel1Quad4=false;barrel1=false;
        barrel2Quad1=false;barrel2Quad2=false;barrel2Quad3=false;barrel2Quad4=false;barrel2=false;
        barrel3Quad1=false;barrel3Quad2=false;barrel3Quad3=false;barrel3Quad4=false;barrel3=false;
        
        collsion1=false;collsion2=false;collsion3=false;collsion4=false;
    }
} // end of CLASS