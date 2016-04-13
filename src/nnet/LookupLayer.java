package nnet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class LookupLayer implements NNInterface
{
	// y = Wx + b
	public Map<Integer, Map<Integer, Double>> table;
	
	public int embeddingLength;
	
	public int vocabSize;
	
	public int inputLength;
	
	public int[] input;
	
	public double[] output;
	
	public double[] outputG;
	
	public int linkId;
	
	public LookupLayer()
	{
	}
	
	public LookupLayer(int embeddingLength,
			int vocabSize,
			int inputLength)
	{
		this.embeddingLength = embeddingLength;
		this.vocabSize = vocabSize;
		this.inputLength = inputLength;
		this.table = new HashMap<Integer, Map<Integer,Double>>(vocabSize);  //new double[vocabSize][embeddingLength];
		this.input   = new int[inputLength];
		this.output  = new double[embeddingLength * inputLength];
		this.outputG = new double[embeddingLength * inputLength];
        this.linkId = 0;
	}
	
	@Override
	public void forward() 
	{
		// TODO Auto-generated method stub
		for (int i = 0; i < input.length; ++i)
        {
            int inputId = input[i];

            int offset = embeddingLength * i;
            
            for (int j = 0; j < embeddingLength; ++j)
            {
                output[offset + j] = 0;
            }
            
            if (inputId >= 0)
            {
            	Map<Integer, Double> vectors = table.get(inputId);
            	for(Entry<Integer, Double> d_v : vectors.entrySet()){
            		int j = d_v.getKey();
            		double v = d_v.getValue();
            		output[offset + j] = v;
            	}
            }
        }
	}
	
	@Override
	public void link(NNInterface nextLayer, int id) throws Exception {
		// TODO Auto-generated method stub

		double[] nextI = (double[]) nextLayer.getInput(id);
		double[] nextIG = (double[])nextLayer.getInputG(id); 
		
		if(nextI.length != output.length || nextIG.length != outputG.length)
		{
			throw new Exception("The Lengths of linked layers do not match.");
		}
		output = nextI;
		outputG = nextIG;
	}

	@Override
	public void link(NNInterface nextLayer) throws Exception {
		// TODO Auto-generated method stub
		link(nextLayer, linkId);
	}

	@Override
	public Object getInput(int id) {
		// TODO Auto-generated method stub
		return input;
	}

	@Override
	public Object getOutput(int id) {
		// TODO Auto-generated method stub
		return output;
	}

	@Override
	public Object getInputG(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getOutputG(int id) {
		// TODO Auto-generated method stub
		return outputG;
	}

	@Override
	public Object cloneWithTiedParams() {
		// TODO Auto-generated method stub
		LookupLayer lookup = new LookupLayer();
		
		lookup.embeddingLength = embeddingLength;
		lookup.vocabSize = vocabSize;
		lookup.table = table;
		lookup.inputLength = inputLength;
		lookup.linkId = linkId;
		lookup.input = new int[inputLength];
		lookup.output = new double[output.length];
		lookup.outputG = new double[outputG.length];
		
		return lookup;
	}

	/*public void regularizationLookup(double lambda) {
		for (int i = 0; i < table.length; ++i)
        {
        	for(int j = 0; j < embeddingLength; j++)
        	{
        		table[i][j] -= lambda * table[i][j];
        	}
        }
	}*/

	@Override
	public void backward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(double learningRate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAdaGrad(double learningRate, int batchsize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearGrad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void randomize(Random r, double min, double max) {
		// TODO Auto-generated method stub
		
	}
}
