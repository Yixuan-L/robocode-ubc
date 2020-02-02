package assignment2;

public class Learning {

	public double learnRate=0.01;
	public double discountRate=0.4;
	int preState;
	int preAction;
	double preValue;
	double newValue;

	LookUpTable Qtable;
	boolean first=true;
	
	public Learning(LookUpTable table)
	{
		this.Qtable=table;
	}
	
	public void Learn(int state,int action,double reward,boolean policy)
	{
		if(first)
			first=false;
		else
		{
			preValue=Qtable.getValue(preState,preAction);
			//(off policy)Q learning
			if(policy==true)
			{
				newValue=(1-learnRate)*preValue+learnRate*(reward+discountRate*Qtable.getMaxValue(state));
				
			}
			//on policy
			else
			{
				newValue=(1-learnRate)*preValue+learnRate*(reward+discountRate*Qtable.getValue(state, action));
				Qtable.visit[preState][preAction]++;
			}
			
			Qtable.setValue(preState,preAction,newValue);
		}
		preState=state;
		preAction=action;
	
	}
	public int selectAction(int state,double explore)
	{
		int action;
		double random=Math.random();
		if(random<explore)
		{
			action=(int)(Math.random()*RobotAction.Num);
		}
		else
			action=Qtable.getBestAction(state);
		return action;
	}

}
