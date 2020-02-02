import java.util.Random;

public class Layer {

	private boolean BINARY=false;
	private Layer next;
	private Layer pre;
	private int num;
	private double[][] preWeight;
	private double[][] nowWeight;
//	private double a;
  //private double b;
	private double[] delta;
	private double[] value;
	private double[] givenOutput;//only for output layer
	private double[][]weightChange;
	public Layer(int num)
	{
		this.num=num;
	//	this.a=a;
	//	this.b=b;
		this.value=new double[num+1];//bias
		this.delta=new double[num];
	}
	public void InitialInput(double [] input)
	{
		if(input.length!=num)
			throw new IllegalArgumentException();
		else
		{
			for(int i=0;i<num;i++)
				value[i]=input[i];
			value[num]=1;//bias=1
			next.value[next.num]=1;
		}
		
	}
	
	public void InitialOutput(double [] output)
	{
		givenOutput=new double[num];
		if(output.length!=num)
			throw new IllegalArgumentException();
		else
		{
			for(int i=0;i<num;i++)
				givenOutput[i]=output[i];
		}
		
	}
	public void ConnectLayer( Layer next,Layer pre)
	{
		this.next=next;
		this.pre=pre;
		if(pre==null)//input layer
		{
			preWeight=new double[num+1][next.num];
			nowWeight=new double[num+1][next.num];
			weightChange=new double[num+1][next.num];
		}
		else if(next==null)//output layer
		{
			preWeight=null;
			nowWeight=null;
		}
		else  //hidden layer
		{
			preWeight=new double[num+1][next.num];
			nowWeight=new double[num+1][next.num];
			weightChange=new double[num+1][next.num];
		}
		
	}
	
	public double[] GetOutput()//only use for output layer
	{
		return value;
		
	}
	public double [][] GetWeight()
	{
		return nowWeight;
	}
	public Layer GetPre()
	{
		return pre;
	}
	public Layer GetNext()
	{
		return next;
	}
	public double sigmoid(double x)
	{
	        return 2 / (1 + Math.exp(-x)) - 1;
	    
	}
	
	public double customSigmoid(double x) 
	{
	        if (BINARY)
	            return 1/ (1 + Math.exp(-x)) + 0;
	        else
	            return sigmoid(x);
	    
	}
	public void SetRandomWeight(double lowrange,double highrange)
	{
		
	   if(pre==null)
		{
			for(int j=0;j<next.num;j++)
			   for(int i=0;i<num+1;i++)
			{
				Random random=new Random();
				nowWeight[i][j]=random.nextDouble()*(highrange-lowrange)+lowrange;
				preWeight[i][j]=nowWeight[i][j];
				weightChange[i][j]=0.0d;
		
			}
	
		
		}
		else
		{
			for(int j=0;j<next.num;j++)
				for(int i=0;i<num+1;i++)
				{
					Random random=new Random();
					nowWeight[i][j]=random.nextDouble()*(highrange-lowrange)+lowrange;
					preWeight[i][j]=nowWeight[i][j];
					weightChange[i][j]=0.0d;
			
				}

		}
	
	}
	
	public void SetZeroWeight()
	{
		for(int j=0;j<next.num;j++)
			for(int i=0;i<num+1;i++)
			{
				nowWeight[i][j]=0.0d;
				preWeight[i][j]=nowWeight[i][j];
				weightChange[i][j]=0.0d;
			}
	
	}

	
	
	public void ForwardPropagation()
	{
	    if(pre==null)//input layer
	    {
	    	for (int j=0;j<next.num;j++)
	    	{
	    		next.value[j]=0.0d;
	    		for(int i=0;i<num+1;i++)
	    		{
	    			next.value[j]+=nowWeight[i][j]*value[i];
	    		}
	    		next.value[j]=customSigmoid(next.value[j]);
	    		
	        }
	    }
	    else //hidden layer
	    {
	    	for(int j=0;j<next.num;j++)
	    	{
	    		next.value[j]=0.0d;
	   // 		for(int i=0;i<num;i++)
	    		for(int i=0;i<num+1;i++)
	    		{
	    			next.value[j]+=nowWeight[i][j]*value[i];
	    		}
	    		next.value[j]=customSigmoid(next.value[j]);
	    	}
	    	
	    }
	}
	
	public void BackPropagation(double momentum,double step)
	{
		if(next==null)//output layer
		{
			for (int i=0;i<num;i++)
			{
				delta[i]=0.0d;
				double gradient;
				double residual=givenOutput[i]-value[i];
				if(BINARY==true)
					gradient=value[i]*(1-value[i]);
				else
					gradient=(value[i]+1)*(1-value[i])*0.5;
				delta[i]=gradient*residual;
			}
		}
		else if (pre==null)//input layer
		{
			return;
		}
		else //hidden layer
		{
			for(int i=0;i<num;i++)
			{
				delta[i]=0.0d;
				double gradient;
				if(BINARY==true)
					gradient=value[i]*(1-value[i]);
				else
					gradient=(value[i]+1)*(1-value[i])*0.5;
				double sumpart=0.0d;
				for(int j=0;j<next.num;j++)
				{
					sumpart+=nowWeight[i][j]*next.delta[j];
					delta[i]=gradient*sumpart;
				}
				
			}
		}
		//weight updating
		if(next==null)//output 
		{
			
			for(int j=0;j<num;j++)
				for(int i=0;i<(pre.num)+1;i++)
			{
				pre.weightChange[i][j]=momentum*pre.weightChange[i][j]+step*delta[j]*pre.value[i];
				
				pre.nowWeight[i][j]+=pre.weightChange[i][j];
				
			}
		}
		else//hidden 
		{
			
			for(int j=0;j<num;j++)
				for(int i=0;i<(pre.num )+1;i++)
				
			{
				pre.weightChange[i][j]=momentum*pre.weightChange[i][j]+step*delta[j]*pre.value[i];
					
				pre.nowWeight[i][j]+=pre.weightChange[i][j];
			}
	    }
			
		
	}
}
