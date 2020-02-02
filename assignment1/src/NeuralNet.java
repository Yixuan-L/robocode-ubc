
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import java.util.Scanner;


public class NeuralNet implements NeuralNetInterface {

    private int numInput;
    private int numHidden;
    private int numOutput;
    private double step;
    private double momentum;
   // private double a;
  //  private double b;
    private final double lowrange = -0.5d;
    private final double highrange = 0.5d;
    
    private final double errorThreshold = 0.05d;
    private double[] totalError;

    private Layer inputLayer;
    private Layer hiddenLayer;
    private Layer outputLayer;

    private  WriteArray excel = new WriteArray(); 
    public NeuralNet(int NumInput, int NumHidden, int NumOutput, double Step,double Momentum)
    {
        numInput = NumInput;
        numHidden = NumHidden;
        numOutput=NumOutput;
        step=Step;
        momentum = Momentum;
      //  a = A;
    //    b = B;
        totalError = new double[numOutput];
      
        inputLayer = new Layer(numInput);
        hiddenLayer = new Layer(numHidden);
        outputLayer = new Layer(numOutput);
        //connect
        inputLayer.ConnectLayer(hiddenLayer, null);
        hiddenLayer.ConnectLayer(outputLayer, inputLayer);
        outputLayer.ConnectLayer(null, hiddenLayer);
        
    }

    public double sigmoid(double x) 
    {
        
        return 0.0d;
    }

    public double customSigmoid(double x) 
    {
       
        return 0.0d;
    }
  
    public void initializeWeights()
    {
        for (Layer NowLayer = inputLayer; NowLayer != outputLayer; NowLayer = NowLayer.GetNext())
            NowLayer.SetRandomWeight(lowrange,highrange);
    }

    public void zeroWeights()
    {
        for (Layer NowLayer = inputLayer; NowLayer != outputLayer; NowLayer = NowLayer.GetNext())
            NowLayer.SetZeroWeight();
    }

    public void forwardPropagate()
    {
        for (Layer NowLayer = inputLayer; NowLayer != outputLayer; NowLayer = NowLayer.GetNext())
            NowLayer.ForwardPropagation();
    }

    public void backwardPropagate(double momentum1, double step1) 
    {
        for (Layer NowLayer = outputLayer; NowLayer != inputLayer; NowLayer = NowLayer.GetPre())
            NowLayer.BackPropagation(momentum1, step1);
    }

    public double outputFor(double[] X) 
    {
        initializeWeights();
        inputLayer.InitialInput(X);//get the input value
        forwardPropagate();
        return outputLayer.GetOutput()[0];//for #1 output
        
    }

    public double train(double[] X, double Value) 
    {
        return Value - outputFor(X);
    }

    public int train(double[][] X, double[][] y)
    {
    	double[][]demand=new double[10000][2];
        initializeWeights();
        int epoch = 0;
        totalError[0]=0;
        do {
        	totalError[0]=0;
            for (int i = 0; i < X.length; i++) 
            {
                inputLayer.InitialInput(X[i]);//row
                outputLayer.InitialOutput(y[i]);//labeled value
                forwardPropagate();
               for (int j = 0; j < numOutput; j++)
                {      
                	
            	   totalError[0]+=Math.pow(y[i][0]-outputLayer.GetOutput()[0],2);
             //   	 System.out.println(y[i][0]);
            //    	 System.out.println(totalError[0]);
        //        	   System.out.println(outputLayer.GetOutput()[0]);
                }
                backwardPropagate(momentum,step);
               
            }
            totalError[0] /= 2;
            System.out.println("Total error for output number at epoch: " + epoch + " is " + totalError[0]);
           
            demand[epoch][0]= (double)epoch;
            demand[epoch][1]=totalError[0];
    
            epoch++;
        } while (totalError[0] > errorThreshold);
        excel.writeArrayToTxt(demand, "mytxt.xls");  //import result to excel for plotting figures
        return epoch;
    }


  
     
    public void save(File argFile) {
//      
    }

    public void load(String argFileName) throws IOException 
    {
//       
    }
    
}
   