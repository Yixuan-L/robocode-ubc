package assignment2;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import robocode.*;


public class YixuanRobot extends AdvancedRobot
{
	public static final double PI = Math.PI;
	private RobotEnemy target;
	private static LookUpTable table;
	private Learning learner;
	
	
	private double reinforcement ;
	private double firePower;
	private int isHitWall = 0;
	private int isHitByBullet = 0;

	
	private boolean policy =true;
	private double explorationRate = 0.99d;
	public static int NumTest =800;
	

	static int total = 0;

	
	
	//************************************************************
	//Start running the game...
    //************************************************************
	public void run()
	{
		table = new LookUpTable();
	    loadData();
	    learner = new Learning(table);
	
	    target = new RobotEnemy();
	    target.distance = 100000;

	    setColors(Color.red, Color.blue, Color.gray);
	    setAdjustGunForRobotTurn(true); 
	    setAdjustRadarForGunTurn(true); 
	    turnRadarRightRadians(2 * PI);
	    
	    while (true)
	    {
	    	robotMovement();
	    	firePower = 400 / target.distance;
	    	if (firePower > 3)
	    	{
	    		firePower = 3;
	    	}
	    	radarMovement();
	    	gunMovement();
	        if (getGunHeat() == 0) 
	        {
	        	setFire(firePower);
	        }
	        execute();
	    }
	}  
	
	//************************************************************
	//Movement of the robot, radar, gun...

	private void robotMovement()
	{  
		
		int state = getState();
		int action=learner.selectAction(state, explorationRate);
	 
	    learner.Learn(state,action,reinforcement,policy);
	   
	    reinforcement = 0.0;
	    isHitWall = 0;
	    isHitByBullet = 0;

	    switch (action)
	    {
	    case RobotAction.RobotAhead:
			setAhead(RobotAction.RobotMoveDistance1);
			break;
		case RobotAction.RobotBack:
			setBack(RobotAction.RobotMoveDistance2);
			break;
		case RobotAction.RobotAheadTurnLeft:
			setAhead(RobotAction.RobotMoveDistance1);
			setTurnLeft(RobotAction.RobotTurnDegree);
			break;
		case RobotAction.RobotAheadTurnRight:
			setAhead(RobotAction.RobotMoveDistance1);
			setTurnRight(RobotAction.RobotTurnDegree);
			break;
		case RobotAction.RobotBackTurnLeft:
			setBack(RobotAction.RobotMoveDistance2);
			setTurnLeft(RobotAction.RobotTurnDegree);
			break;
		case RobotAction.RobotBackTurnRight:
			setBack(RobotAction.RobotMoveDistance2);
			setTurnRight(RobotAction.RobotTurnDegree);
			break;
	    }
	}

	private void radarMovement()
	{
		 setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
	}
	
	private void gunMovement()
	{
		
		
	    long time;
	    long nextTime;
	    Point2D.Double p;
	    p = new Point2D.Double(target.x, target.y);
	    for (int i = 0; i < 20; i++)
	    {
	    	nextTime = (int)Math.round((getrange(getX(),getY(),p.x,p.y)/(20-(3*firePower))));
	        time = getTime() + nextTime - 10;
	        p = target.guessPosition(time);
	    }
	  
	    double gunOffset = getGunHeadingRadians() - (Math.PI/2 - Math.atan2(p.y - getY(),p.x -  getX()));
	    
	    setTurnGunLeftRadians(NormaliseBearing(gunOffset));

		 
	}
		
	//************************************************************
    //Functions used in movement function of robot, radar, gun
    //************************************************************
	//get the state of robot
	private int getState()
	{
	    int heading = RobotState.getHeading(getHeading());
	    int targetDistance = RobotState.getEnermyDistance(target.distance);
	    int targetBearing = RobotState.getEnermyBearing(target.bearing);
	
	    int state = RobotState.MapState[targetDistance][targetBearing][heading][isHitWall][isHitByBullet];
	
	    return state;
	}
	
	//bearing is within the -pi to pi range
	double NormaliseBearing(double ang) 
	{
		if (ang > PI)
			ang -= 2*PI;
	    if (ang < -PI)
	        ang += 2*PI;
	    return ang;
	}

	//returns the distance between two x,y coordinates
	public double getrange(double x1, double y1, double x2, double y2)
	{
	    double xo = x2 - x1;
	    double yo = y2 - y1;
	    double h = Math.sqrt(xo * xo + yo * yo);
	    return h;
	}
	
	//************************************************************
    //Events of the robot...
    //************************************************************
	//When the bullet hit another robot...
	
	public void onBulletHit(BulletHitEvent e)
	{
	    if (target.name == e.getName())
	    {
	        reinforcement += 5;
	    }
	}  

	//When the bullet doesn't hit another robot...
	public void onBulletMissed(BulletMissedEvent e)
	{
	    reinforcement += -1;
	}

	//When the robot is hit by bullet...
	public void onHitByBullet(HitByBulletEvent e)
	{
	    reinforcement += -5;
	    isHitByBullet = 1;
	}

	//When the robot hits another robot...
	public void onHitRobot(HitRobotEvent e)
	{
	    reinforcement += -5;
	}

	//When the robot hits the wall...
	public void onHitWall(HitWallEvent e)
	{
	    reinforcement += -1;
	    isHitWall = 1;
	}
	
	
	//************************************************************
    //onScannedRobot: What to do when you see another robot
    //************************************************************
	public void onScannedRobot(ScannedRobotEvent e)
	{
	    if ((e.getDistance() < target.distance)||(target.name == e.getName()))
	    {
	    	//Gets the absolute bearing to the point where the robot is
	        double absbearing_rad = (getHeadingRadians() + e.getBearingRadians()) % (2 * PI);
	        
	        //Sets all the information about our target
	        target.name = e.getName();
	        double h = NormaliseBearing(e.getHeadingRadians() - target.heading);
	        h = h / (getTime() - target.ctime);
	        target.changeHeading = h;
	        target.x = getX() + Math.sin(absbearing_rad) * e.getDistance(); 
	        target.y = getY() + Math.cos(absbearing_rad) * e.getDistance(); 
	        target.bearing = e.getBearingRadians();
	        target.heading = e.getHeadingRadians();
	        target.ctime = getTime(); //game time at which this scan was produced
	        target.speed = e.getVelocity();
	        target.distance = e.getDistance();
	        target.energy = e.getEnergy();
	    }
	}
	
	//************************************************************
    //When the robot wins or dies (round ends)...
	//************************************************************
	public void onRobotDeath(RobotDeathEvent e)
	{
		if (e.getName() == target.name)
			target.distance = 10000;
	}

	public void onWin(WinEvent event)
	{		
	    reinforcement += 10;
	 
	//    robotMovement();
	    saveData();
	}

	public void onDeath(DeathEvent event)
	{
		
	    reinforcement += -10;
	   
	//    robotMovement();
	    saveData();
	}

	
	//************************************************************
	//Load and save the data of the Q table...
	//************************************************************
	public void loadData()
	{
		try
	    {
			table.loadData(getDataFile("LUT.dat"));
	    }
	    catch (Exception e)
		{
	    }
	}

	public void saveData()
	{
		try
	    {
	    	table.saveData(getDataFile("LUT.dat"));
	    }
	    catch (Exception e)
	    {
	        out.println("Exception trying to write: " + e);
	    }
	}


} 

