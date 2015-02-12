package com.dxauxm.barrelgame;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
* Author : Dhruv, Usha
* Date : 30/Nov/2014
* Purpos : This class is used for drawing horse, grass, barrels, boundary, finish line on the Canvas.
*          Also updates and draws the horse as its position is updated.   
*/
public class SimulationView extends View implements SensorEventListener {
	 
	/*
	 * Everything related to sensors
	 */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;
    private long mSensorTimeStamp;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    
    /*
     * The bitmap for storing our grass, barrel, & horse pictures.
     */
    private Bitmap mGrass;
    private Bitmap bitmapBarrel;
    private Bitmap bitmapHorse;
   
 /*
  * Static variables here... all are protected
  */
    /* Storing the Size of horse and barrel */
    protected static final int HORSE_SIZE = 50;
    protected static final int BARREL_SIZE = 60;
    
    /* Width & Height of our display */
    protected static float width, height;
    
    /* Store the mid-points of our screen */
    private float mXOrigin, mYOrigin;

    /* All the paint for drawing on canvas */
    private Paint textPaint, textPaint2, redPaint, linePaint, countdownPaint, bluePaint,yelloPaint, magentaPaint, fencePaint;
    
    /*
     * Storing the variables for time
     * StartTime = stores the starting system time, the timer is started as soon as the 'Go' is issued
     * EndTime = stores the current system time
     * Total Time = stores the total time taken for playing, which is end-start time
     * String time is used for printing time on screen
     * Penalty time will keep adding the penalty time each time rider crashes the fence boundary
     * stopTimer = flag set to true when game finishes. If true will stop the timer, else time is continuously calculated
     */
    protected static long penaltyTime=0;
    private long startTime=0,endTime=0;
    private float totalTime = 0.0f;
    private String time="0.00";
    private boolean stopTimer=false;
    
    /* 
     * Boolean values for various events, all set to false initially...
     * roundComplete = is set to true when round of all three barrels completed
     * finishComplete = is set to true when above is true & player crosses the finish line
     * hitBarrel = is set to true, when the player hit the barrel
     */
    protected static boolean roundComplete=false, finishComplete=false, hitBarrel=false;
    
    /* Create a horse object from Horse class, each Horse class has the properties of the behavior of the Horse  */
    private Horse mHorseObject = new Horse();
    
    /*
     *  flgDraw = flag if true so that the screen is drawn only after that.Flag which is set only after 5 sec at start of each game.
     */
    private boolean flgDraw=false;
    /* The variable start */ 
    protected static int start=0;
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : Used for setting the bitmap for various icons and used for setting the accelerometer and paint.
	*/
    public SimulationView(Context context) {
        super(context);
        
        Bitmap horse = BitmapFactory.decodeResource( getResources(), R.drawable.horse );
        bitmapHorse = Bitmap.createScaledBitmap(horse, HORSE_SIZE, HORSE_SIZE, true);
         
        Bitmap barrel = BitmapFactory.decodeResource( getResources(), R.drawable.barrel );
        bitmapBarrel = Bitmap.createScaledBitmap(barrel, BARREL_SIZE, BARREL_SIZE, true);
        
//        WindowManager mWindowManager = (WindowManager) context.getSystemService ( Context.WINDOW_SERVICE );
//        mDisplay = mWindowManager.getDefaultDisplay();
        
        Options opts = new Options();
        opts.inDither = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        mGrass = BitmapFactory.decodeResource(getResources(), R.drawable.grass2, opts);
        
        // Register the accelerometer
        mSensorManager = (SensorManager) context.getSystemService( Context.SENSOR_SERVICE );
        mAccelerometer = mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        
        /*
         * Write paint attributes, which will be used for displaying count-down
         */
		Typeface typeFace2=Typeface.createFromAsset(getContext().getAssets(),"fonts/Segoe-Regular.ttf");
        textPaint = new Paint();
        textPaint.setTypeface(typeFace2);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        
        textPaint2 = new Paint();
        textPaint2.setTypeface(typeFace2);
        textPaint2.setStyle(Paint.Style.STROKE);
        textPaint2.setColor(Color.WHITE);
        textPaint2.setTextSize(40);
        
        countdownPaint = new Paint();
        countdownPaint.setTypeface(typeFace2);
        countdownPaint.setStyle(Paint.Style.FILL);
        countdownPaint.setColor(Color.WHITE);
        countdownPaint.setTextSize(36);
        
        redPaint = new Paint();
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(Color.RED);
        
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);
        linePaint.setColor(Color.RED);
        
        fencePaint = new Paint();
        fencePaint.setStyle(Paint.Style.STROKE);
        fencePaint.setStrokeWidth(20);
        fencePaint.setARGB(255, 92,51,23);
        
        bluePaint = new Paint();
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setStrokeWidth(3);
        bluePaint.setColor(Color.BLUE);
        
        yelloPaint = new Paint();
        yelloPaint.setStyle(Paint.Style.STROKE);
        yelloPaint.setStrokeWidth(3);
        yelloPaint.setColor(Color.YELLOW);
        
        magentaPaint = new Paint();
        magentaPaint.setStyle(Paint.Style.STROKE);
        magentaPaint.setStrokeWidth(3);
        magentaPaint.setColor(Color.MAGENTA);
        resetEverything();
	    startGame();
    }
    
    /*
     * Function is called to register the accelerometer
     */
    public void startSimulation() {
        mSensorManager.registerListener( this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME );
    }
     
    /*
     * Function is called to unregister the accelerometer
     */
    public void stopSimulation() {
        mSensorManager.unregisterListener(this);
    }
 
    /*
     * w, h --> current width & height of the view
     * 
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	
    	width = w;
    	height=h;
    	
        // Set the origin as half of the height and half of the width of the screen
    	mXOrigin = w * 0.5f;
        mYOrigin = h * 0.5f;
    }
    
    public boolean onTouch(View v, MotionEvent event) {
      float eventX = event.getX();
      float eventY = event.getY();

//      switch (event.getAction()) {
//      case MotionEvent.ACTION_DOWN:
//        return true;
//      case MotionEvent.ACTION_MOVE:
//        break;
//      case MotionEvent.ACTION_UP:
//        // nothing to do
//        break;
//      default:
//        return false;
//      }
      
      if((event.getX(0)>=640) && 
    	      (event.getY(0)>=360) && 
    	     ( event.getX(0)<=640+BARREL_SIZE) && 
    	      (event.getY(0)<=360+BARREL_SIZE))
    	      {
    	  
    	  System.out.println("point");
    	  
    	  
    	      }
    
      return false;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(finishComplete)
        {
      	  resetEverything();
      	  restartGame();
        }
        return super.onTouchEvent(event);
    }
    
   
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : The purpose of this function is to get the x,y,z accelerometer values.
	*/
    @Override
    public void onSensorChanged(SensorEvent event) {
    	
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            
            mSensorX = event.values[1];
            mSensorY = event.values[0];
            mSensorZ = event.values[2];
            
            mSensorTimeStamp = event.timestamp;
     
            long curTime = System.currentTimeMillis();
            
//            System.out.println("Timestamp: "+mSensorTimeStamp/1000000000+" mSensorX: "+mSensorX
//            		+" mSensorY: "+mSensorY+" mSensorZ: "+mSensorZ);
//            System.out.println("System.nanoTime(): "+System.nanoTime()/1000000000);
//          System.out.println("time diff: "+(System.nanoTime()-mSensorTimeStamp)/1000000000f);
     
            /*
             *  Refresh the code inside the if loop only for the time interval given, which is 1000ms or 1 sec here
             *  that is functions inside the loop are executed every 1 second
             */
            if ((curTime - lastUpdate) > 1000) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                
                // only execute this if the shake of the device is greater than the shake threshold
                if (speed > SHAKE_THRESHOLD) {
                	 //do something
                	
                }
                
                // update the last
                last_x = x;
                last_y = y;
                last_z = z;
                
//              mSensorX = x;
//              mSensorY = y;
//              mSensorZ = z;

//                System.out.println("Timestamp: "+mSensorTimeStamp/1000000000+" mSensorX: "+mSensorX
//                		+" mSensorY: "+mSensorY+" mSensorZ: "+mSensorZ);
//                System.out.println("System.nanoTime(): "+System.nanoTime()/1000000000);
//                System.out.println("time diff: "+(System.nanoTime()-mSensorTimeStamp)/1000000000f);
//                
//                mBall.print();
            
            }
        }
    }
    
    /*
     * 
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    
    /*
	* Author : Dhruv
	* Date : 30/Nov/2014
	* Purpose : This is used to draw in the canvas. We invalidate it each time and redraw the canvas.
	*/
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        /*
		 * Print the angles, for debugging only
		 *
		canvas.drawText("Angle 1: "+Horse.angleBarrel1, 2*width/20, 2*height/15, countdownPaint);
        canvas.drawText("Angle 2: "+Horse.angleBarrel2, 2*width/20, 3*height/15, countdownPaint);
        canvas.drawText("Angle 3: "+Horse.angleBarrel3, 2*width/20, 4*height/15, countdownPaint); */
        
        // Drawing the background
        setBackgroundDrawable(new BitmapDrawable(this.getResources(), mGrass));
        
        // drawing the boundary or fence
        canvas.drawLine(0,0, 0,height, fencePaint); //top
        canvas.drawLine(0,0, width,0, fencePaint); //left
        canvas.drawLine(width,0, width,height, fencePaint); //
        canvas.drawLine(0,height, width,height, fencePaint); //
        
//        canvas.drawLine(width/4,0, width/4,height, linePaint); //
//        canvas.drawLine(3*width/4, 0, 3*width/4,height, linePaint); //
        
//        canvas.drawBitmap( bitmapHorse, 0, 0, null ); // top-left
//        canvas.drawBitmap( bitmapHorse, 0, height - HORSE_SIZE, null ); // bottom-left
//        canvas.drawBitmap( bitmapHorse, width - HORSE_SIZE, 0, null ); // top-right
//        canvas.drawBitmap( bitmapHorse, width - HORSE_SIZE, height - HORSE_SIZE, null ); //bottom-right
//        canvas.drawBitmap( bitmapHorse, width/2, 0, null );
        
        // horse's initial co-ordinates
        
        /*
         * Drawing the barrels
         */
    	canvas.drawBitmap(bitmapBarrel, (width/4) - (BARREL_SIZE/2), (height/2) - (BARREL_SIZE/2), null); // Barrel 2
    	canvas.drawBitmap(bitmapBarrel, (3*width/4) - (BARREL_SIZE/2), (height/2) - (BARREL_SIZE/2), null); // Barrel 1
    	canvas.drawBitmap(bitmapBarrel, (width/2) - (BARREL_SIZE/2), (height/4) - (BARREL_SIZE/2), null); // Barrel 3
    
        /*
         * Printing Time, if stop timer is set then stop measuring time
         */
        if(!stopTimer)
        {
        	totalTime = (endTime-startTime)/1000f + penaltyTime;
        	time = Float.toString(totalTime);
        }
        canvas.drawText("Total Time: "+time+" seconds", 6*width/10, 3*height/20+20, countdownPaint);
        
        /*
         * Print the barrel positions, if barrel is hit remove the message
         */
        if(!hitBarrel)
        {
        	canvas.drawText("WELCOME "+NewGame.Name+" !!!", 4*width/10, 2*height/20-10, countdownPaint);
	        canvas.drawText("Your progress:-> ", 1*width/20, 3*height/20+20, countdownPaint);
	        canvas.drawText("Barrel 1: "+mHorseObject.progress1+"%", 1*width/20, 4*height/20+20, countdownPaint);
	        canvas.drawText("Barrel 2: "+mHorseObject.progress2+"%", 1*width/20, 5*height/20+20, countdownPaint);
	        canvas.drawText("Barrel 3: "+mHorseObject.progress3+"%", 1*width/20, 6*height/20+20, countdownPaint);
        }
        
        /*
         * Finish Line
         */
        
        canvas.drawLine( 0, 19*height/20-HORSE_SIZE-1, width,19*height/20-HORSE_SIZE-1, linePaint );

        // for displaying 3,2,1..go
    	switch(start){
	    	case 0:
	    		break;
	    	case 1:
	    		canvas.drawText("3...", mXOrigin-20, mYOrigin, textPaint);
	    		canvas.drawBitmap( bitmapHorse, width/2 - HORSE_SIZE/2, 19*height/20 - HORSE_SIZE, null );
	    		startTime = System.currentTimeMillis();
	    		endTime = System.currentTimeMillis();
	    		break;
	    	case 2:
	    		canvas.drawText("2...", mXOrigin-20, mYOrigin, textPaint);
	    		canvas.drawBitmap( bitmapHorse, width/2 - HORSE_SIZE/2, 19*height/20 - HORSE_SIZE, null );
	    		break;
	    	case 3:
	    		canvas.drawText("1...", mXOrigin-20, mYOrigin, textPaint);
	    		canvas.drawBitmap( bitmapHorse, width/2 - HORSE_SIZE/2, 19*height/20 - HORSE_SIZE, null );
	    		break;
	    	case 4:
	    		canvas.drawText("Go...", mXOrigin-20, mYOrigin, textPaint);
	    		canvas.drawBitmap( bitmapHorse, width/2 - HORSE_SIZE/2, 19*height/20 - HORSE_SIZE, null );
	    		mHorseObject.setInitialPosition( width/2 - HORSE_SIZE/2, 19*height/20 - HORSE_SIZE );
	    		break;
	    	case 5:
	    		flgDraw=true;
	    		endTime = System.currentTimeMillis()-1000;
	    		break;
	    	case 6:
	    		// game stops if barrel is hit or if player completes the race
	    		stopTimer = true; // this will stop the timer
	    		flgDraw = false; // this will stop the drawing
	    		canvas.drawBitmap( bitmapHorse, mHorseObject.mPosX, mHorseObject.mPosY, null ); // freezes the canvas
	    		
	    		if(hitBarrel)
	    		{
	    			restartGame();
	    		}
	    		
	    		if(finishComplete)
	    		{
	    			setHighScore();
	    			dialogShow();
	    		}
			default:
				break;
    	}
    	
    	if(hitBarrel)
		{
			canvas.drawText("Oops! You hit a barrel !", 5*width/20, 8*height/20, textPaint);
			canvas.drawText("Game Over! ", 7*width/20, 10*height/20, textPaint);
			canvas.drawText("The game will auto restart in 5sec ",5*width/20, 13*height/20, textPaint2);
		}
    	
    	if(finishComplete)
		{
			canvas.drawText("You completed the race! :)", 5*width/20, 8*height/20, textPaint);
			canvas.drawText("Total time : "+totalTime, 7*width/20, 10*height/20, textPaint);
			canvas.drawText("Choose your option from the screen in 5sec ",4*width/20, 13*height/20, textPaint2);
		}
    	
		if(flgDraw){
			
			canvas.drawText("Finish Line", 6*width/20,19*height/20-HORSE_SIZE-6, countdownPaint);
			
			//for barrel at the middle
//    		canvas.drawBitmap(bitmapBarrel, mXOrigin - BARREL_SIZE/2, mYOrigin - BARREL_SIZE/2, null);
//    		float a = mXOrigin - BARREL_SIZE/2;
//    		float b = mYOrigin - BARREL_SIZE/2; 
    		
//    		System.out.println("mXOrigin - HOLE_SIZE/2 : "+a+" mYOrigin - HOLE_SIZE/2: "+b);
            
//          mBall.sethXPos(mXOrigin - BARREL_SIZE/2, mYOrigin - BARREL_SIZE/2);
			
            mHorseObject.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);

            mHorseObject.collisionBoundary();
            
            mHorseObject.collisionBarrel();
            
            // Logic for going round the barrel
            mHorseObject.barrelRound();
            
            mHorseObject.pointToLineDistance();
//            
//            // line to show angles
//            canvas.drawLine(3*width/4, height/2, width/2, height/2, bluePaint);
//            canvas.drawLine(3*width/4, height/2, mHorseObject.mPosX + HORSE_SIZE/2, mHorseObject.mPosY + HORSE_SIZE/2, bluePaint);
//
//            canvas.drawLine((width/4), (height/2),width/2, height/2, yelloPaint);
//            canvas.drawLine( (width/4), (height/2) , mHorseObject.mPosX + HORSE_SIZE/2, mHorseObject.mPosY + HORSE_SIZE/2, yelloPaint);
//            
//            canvas.drawLine( (width/2), (height/4),width/2, height/2, textPaint);
//            canvas.drawLine( (width/2), (height/4) , mHorseObject.mPosX + HORSE_SIZE/2, mHorseObject.mPosY + HORSE_SIZE/2, textPaint);
            
//            System.out.println((mBall.calculateAngle( 3*width/2, height/2, 3*width/2+BARREL_SIZE/2, height/2))* (180/Math.PI) );
            
            // newest
            canvas.drawBitmap( bitmapHorse, mHorseObject.mPosX , mHorseObject.mPosY , null );
            
//            System.out.println("DRAWING WITH X: " + mBall.mPosX + " and Y : " +mBall.mPosY);
            
//            mBall.touchedHole( (mXOrigin - HORSE_SIZE/2) + mBall.mPosX, (mYOrigin - HORSE_SIZE/2) - mBall.mPosY );
         
//            canvas.drawBitmap( bitmapHorse, 
//            		(width/2- HORSE_SIZE/2) + mBall.mPosX , (19*height/20- HORSE_SIZE) - mBall.mPosY , null );
            
//            if(flg2){
//            canvas.drawBitmap( bitmapHorse, 
//            					( mXOrigin - HORSE_SIZE / 2 ) + mBall.mPosX , 
//                                ( mYOrigin - HORSE_SIZE / 2 ) - mBall.mPosY , null );
            
//            float c=( mXOrigin - HORSE_SIZE/2  ) + mBall.mPosX ;
//    		float d=( mYOrigin - HORSE_SIZE/2  ) - mBall.mPosY;
//    		
//    		System.out.println("DRAWING WITH X: " + c + " and Y : " +d);
//          }
//          else
//            {
//            	flg2=true;
//            	canvas.drawBitmap( mBitmap, width/2 , 0 , null );
//            	System.out.println("Initial "+width/2+" "+0);
//            }
    	}
		else
		{
			canvas.drawText("Start Line", 8*width/20,19*height/20-HORSE_SIZE-8, countdownPaint);
		}
        
		invalidate();
    }
	
	/*
	* Author : Usha
	* Date : 30/Nov/2014
	* Purpose : Algo for resetting all the values of the variables to default each time a game a restarted
	*/
	public void resetEverything()
	{
		startTime=0;
		endTime=0;
	    penaltyTime=0;
	    totalTime = 0.0f;
	    time="0.00";
	    roundComplete=false;
	    finishComplete=false;
	    hitBarrel=false;
	    flgDraw=false;
//	    flgDrawHorse=false;
	    stopTimer=false;
	    start=0;
	    mHorseObject.resetEverything();
//	    NewGame.totalScore="dnf";
	}
	
	private void startGame()
	{
		/*
	     * Handlers for defining the time delay, after a delay of 1 sec each, new event happens
	     * What to do, is handled in the onDraw method.
	     */
        final Handler handler = new Handler();
    	handler.postDelayed(new Runnable() {
    	    @Override
    	    public void run() {
    	        // Do something after 1s
    	    	start=1;
    	    }
    	}, 1000);
    	handler.postDelayed(new Runnable() {
    	    @Override
    	    public void run() {
    	        // Do something after 2s
    	    	start=2;
    	    }
    	}, 2000);
    	handler.postDelayed(new Runnable() {
    	    @Override
    	    public void run() {
    	        // Do something after 3s
    	    	start=4;
    	    }
    	}, 3000);
    	handler.postDelayed(new Runnable() {
    	    @Override
    	    public void run() {
    	        // Do something after 4s
    	    	start=5;
    	    }
    	}, 4000);
    	/*
    	 * End of Handlers
    	 */
	}
	
	private void restartGame()
	{
		/*
	     * Handlers for defining the time delay, after a delay of 1 sec each, new event happens
	     * What to do, is handled in the onDraw method.
	     */
        final Handler handler = new Handler();
        	start = 0;
    		handler.postDelayed(new Runnable() {
        	    @Override
        	    public void run() {
        	        // Do something after 4s
        	    	System.out.println("Restarting");
        	    	resetEverything();
        	    	startGame();
        	    }
        	}, 4000);
    	/*
    	 * End of Handlers
    	 */
	}

	private void setHighScore()
	{
		saveToFile();
	}
	
	private void dialogShow()
	{
		final Handler handler = new Handler();
//    	if(hitBarrel)
//    	{
        	start = 0;
    		handler.postDelayed(new Runnable() {
        	    @Override
        	    public void run() {
        	        // Do something after 4s
        	    	System.out.println("Restarting");
        	    	resetEverything();
        	    	startGame();
        	  
		Typeface typeFace=Typeface.createFromAsset(getContext().getAssets(),"fonts/Segoe-UI-Symbol.ttf");
  		// custom dialog
  		final Dialog dialog = new Dialog(getContext());
  		dialog.setContentView(R.layout.player_option_box);
  		dialog.setTitle("Please choose an option...");
  		
  		// set the custom dialog components - text, image and button
  		TextView text = (TextView) dialog.findViewById(R.id.text);
  		text.setTypeface(typeFace);
//  		ImageView image = (ImageView) dialog.findViewById(R.id.image);

  		Button dialogBtnYes = (Button) dialog.findViewById(R.id.dialogBtnReset);
  		dialogBtnYes.setTypeface(typeFace);
  		// if button is clicked, close the custom dialog
  		dialogBtnYes.setOnClickListener(new OnClickListener() {
  			@Override
  			public void onClick(View v) {
  				Log.i("Rest pressed :", "Start the game again!");
  				dialog.dismiss();
  				// we cancel the dialog so that the window is not leaked
  				dialog.cancel();
  				// call the delete operation
//  				actionDelete();
  				resetEverything();
  				// 
  				startGame();
  			}
  		});
  		Button dialogBtnNo = (Button) dialog.findViewById(R.id.dialogBtnExit);
  		dialogBtnNo.setTypeface(typeFace);
  		// if button is clicked, close the custom dialog
  		dialogBtnNo.setOnClickListener(new OnClickListener() {
  			@Override
  			public void onClick(View v) {
  				Log.i("Exit pressed :", "exit the screen");
  				dialog.cancel();
  				dialog.dismiss();
  				Intent intent = new Intent("kill");
  				LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
  			}
  		});
  		dialog.show();
  		
        	    }
        	}, 4000);
	}
	
	/*
     * Author - Usha M
     * Used for saving a record to a file
     */
	public void saveToFile()
	{
		FileIO fileIO = new FileIO();
		
		if(true) //checkValidity(FName) )
		{

			String cleanName = cleanName(NewGame.Name);
			
			String lineToWrite = cleanName+"|"+time;
			
			Log.i("Will try to to save: ", lineToWrite);
			
			boolean flg = fileIO.storeHighScore( Environment.getExternalStorageDirectory(), lineToWrite );
			if(flg)
			{
				// Logs & Toast to let the user an developer know the record was saved successfully
				Log.i("Record saved to file successfully", "");
//				Toast.makeText(getBaseContext(), "Record saved to File !", Toast.LENGTH_SHORT).show();
//				// Will go back to the first activity
//				finish();
			}
		}
	}
	
	/*
     * Author - Dhruv
     * Used for cleaning and formatting the name 
     */
	public String cleanName(String name)
	{
		name = name.trim();
		int spaceIndex = name.indexOf(" ");
		if (spaceIndex != -1)
		{
		    name = name.substring(0, spaceIndex);
		}
		
		if(name.length()<=0)
		{
			name = name+"***";
		}
//		String	upToNCharacters = name.substring(0, Math.min(name.length(), 3)); // for making name only 3 letters long
		return name;
	}
    
}