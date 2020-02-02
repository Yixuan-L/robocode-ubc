package assignment2;

public class RobotState {

	public static int Num;
	public static int NumDistance=10;
	public static int NumBearing=4;
	public static int NumHeading=4;
	public static int NumHitWall=2;//hit or not
	public static int NumHitByBullet=2;//hit or not
	
	public static int MapState[][][][][];
	
	static
	{
		MapState=new int[NumDistance][NumBearing][NumHeading][NumHitWall][NumHitByBullet];
		int count=0;
		for(int i=0;i<NumDistance;i++)
			for(int j=0;j<NumBearing;j++)
				for (int m=0;m<NumHeading;m++)
					for(int p=0;p<NumHitWall;p++)
						for(int q=0;q<NumHitByBullet;q++)
						
			{
				MapState[i][j][m][p][q]=count++;
				
			}
		Num=count;
	}
	
	public static int getEnermyDistance(double value)
	{
		int distance=(int)(value/100);
		if(distance>NumDistance-1)
			distance=NumDistance-1;
		return distance;
				
	}
	public static int getEnermyBearing(double bearing)
	{
		double  totalAngle=Math.PI*2;
		if(bearing<0)
			bearing=totalAngle+bearing;
		
		double angle=totalAngle/NumBearing;
		double newBearing=bearing+angle/2;
		if(newBearing>totalAngle)
			newBearing=newBearing-totalAngle;
		return (int) (newBearing/angle);
	}
	public static int getHeading(double heading)
	{
		double totalAngle=360.0d;
		double angle=totalAngle/NumHeading;
		double newHeading=heading+angle/2;
		if(newHeading>totalAngle)
			newHeading-=totalAngle;
		return(int)(newHeading/angle);
	}
}
