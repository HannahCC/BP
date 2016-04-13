package nnet;

import java.util.Random;

public class FusionLayer implements NNInterface{

	public int inputLength;
	public int outputLength;

	public double W;
	public double WG;
	public double WAdaLR;

	public double[] input;
	public double[] output;


	public double[] inputG;
	public double[] outputG;


	public int linkId;

	private FusionLayer(){}

	public FusionLayer(int inputRow)
	{
		this.linkId = 0;

		this.inputLength = inputRow;
		this.outputLength = inputRow;

		this.input = new double[inputLength];
		this.inputG = new double[inputLength];
		this.output = new double[outputLength];
		this.outputG = new double[outputLength];
	}

	@Override
	public void randomize(Random r, double min, double max) {
		// TODO Auto-generated method stub
		W = r.nextFloat();
	}

	@Override
	public void forward() {
		for(int i = 0; i < inputLength; i++)
		{
			output[i] = W * input[i];
		}
	}

	@Override
	public void backward() {
		for(int i = 0; i < inputLength; i++)
		{
			inputG[i] += W * outputG[i];
		}

		for(int i = 0; i < inputLength; i++)
		{
			WG += input[i] * outputG[i];
		}
		WG /= inputLength;
	}

	@Override
	public void update(double learningRate) {
		W += learningRate * WG;
	}

	@Override
	public void updateAdaGrad(double learningRate, int batchsize) {

		WAdaLR += (WG/ batchsize) * (WG / batchsize);
		W += (learningRate / batchsize) * WG / Math.sqrt(WAdaLR);

	}

	@Override
	public void clearGrad() {
		// TODO Auto-generated method stub

		WG = 0;

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

		FusionLayer clone = new FusionLayer();
		clone.linkId = linkId;
		clone.inputLength = inputLength;
		clone.outputLength = outputLength;

		clone.W = W;
		clone.WAdaLR = WAdaLR;

		this.input = new double[inputLength];
		this.inputG = new double[inputLength];
		this.output = new double[outputLength];
		this.outputG = new double[outputLength];

		return clone;
	}

}
