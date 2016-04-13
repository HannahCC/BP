package nnet;

import java.util.Random;

public class LinearFusionLayer implements NNInterface{

	public double[][] W;
	
	public int inputLength;
	public int outputLength;
	
	public double[] input;
	public double[] output;
	
	public double[] inputG;
	public double[] outputG;
	
	public double[][] WG;
    public double[][] WAdaLR;
    
    public int linkId;
    
    private LinearFusionLayer()
    {
    }

    public LinearFusionLayer(int xInputLength,
    		int xOutputLength)
    {
    	this(xInputLength, xOutputLength, 0);
    }
    
    public LinearFusionLayer(int inputLength,
    		int outputLength,
    		int linkId)
    {
    	this.inputLength = inputLength;
    	this.outputLength = outputLength;
    	this.linkId = linkId;
    	
    	this.W = new double[outputLength][inputLength];
    	this.WG = new double[outputLength][inputLength];
    	this.WAdaLR = new double[outputLength][inputLength];
    	
    	this.input = new double[inputLength];
    	this.inputG = new double[inputLength];
    	this.output = new double[outputLength];
    	this.outputG = new double[outputLength];
    }
    
	@Override
	public void randomize(Random r, double min, double max) {
		// TODO Auto-generated method stub
		for(int i = 0; i < W.length; i++)
		{
			for(int j = 0; j < W[i].length; j++)
			{
				W[i][j] = r.nextFloat() * (max - min) + min;
			}
		}
	}

	@Override
	public void forward() {
		// TODO Auto-generated method stub
		MathOp.Axpy(W, input, output);
	}

	@Override
	public void backward() {
		// TODO Auto-generated method stub
		MathOp.xdotA(outputG, W, inputG);
		MathOp.A_add_xTmulty(outputG, input, WG);
	}

	@Override
	public void update(double learningRate) {
		
		for(int i = 0; i < W.length; i++)
		{
			for(int j = 0; j < W[i].length; j++)
			{
				W[i][j] += learningRate * WG[i][j];
			}
		}
	}

	@Override
	public void updateAdaGrad(double learningRate, int batchsize) {
		
		for(int i = 0; i < W.length; i++)
		{
			for(int j = 0; j < W[i].length; j++)
			{
				WAdaLR[i][j] += (WG[i][j] / batchsize) * (WG[i][j] / batchsize);
				W[i][j] += (learningRate / batchsize) * WG[i][j] / Math.sqrt(WAdaLR[i][j]);
			}
		}
	}
	
	@Override
	public void clearGrad() {
		// TODO Auto-generated method stub
		for(int i = 0; i < W.length; i++)
		{
			for(int j = 0; j < W[i].length; j++)
			{
				WG[i][j] = 0;
			}
		}
		
		for(int i = 0; i < outputG.length; i++)
		{
			outputG[i] = 0;
		}
		
		for(int i = 0; i < inputG.length; i++)
		{
			inputG[i] = 0;
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
		return inputG;
	}

	@Override
	public Object getOutputG(int id) {
		// TODO Auto-generated method stub
		return outputG;
	}

	@Override
	public Object cloneWithTiedParams() {
		// TODO Auto-generated method stub
		
		LinearFusionLayer clone = new LinearFusionLayer();
		clone.linkId = linkId;
        clone.inputLength = inputLength;
        clone.outputLength = outputLength;
        
		clone.W = W;
        clone.WG = new double[WG.length][inputLength];
        clone.WAdaLR = WAdaLR;
        
        clone.input = new double[input.length];
        clone.inputG = new double[input.length];
        clone.output = new double[output.length];
        clone.outputG = new double[output.length];
        
		return clone;
	}

}
