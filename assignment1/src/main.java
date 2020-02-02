
public class main {

    public static void main(String[] args)
    {
    	int epoch = 0;
        NeuralNet xor = new NeuralNet(2, 4, 1, 0.2, 0.0);
       
        for (int i = 0; i < 100; i++)
        {
       epoch += xor.train(new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}, new double[][]{{-1}, {1}, {1}, {-1}});
   //     epoch += xor.train(new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}}, new double[][]{{0}, {1}, {1}, {0}});
        }
        System.out.println(epoch / 100);
    }
}


