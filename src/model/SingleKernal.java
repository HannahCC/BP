package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nnet.LinearLayer;
import nnet.LinearTanhLayer;
import nnet.MultiConnectLayer;
import nnet.SoftmaxLayer;
import other.Data;
import other.Metric;
import wyConnect.Net4ViewUser;
import wyConnect.View;
import adapter.FileTool;
import adapter.ToolKit;

public class SingleKernal {
	List<Net4ViewUser> net4UserList;

	//LookupLayer fusionLookup;
	MultiConnectLayer connectItem;

	int hiddenlayernumber;
	List<LinearTanhLayer> hiddenlayerlist = new ArrayList<LinearTanhLayer>();

	LinearLayer linearForSoftmax;
	SoftmaxLayer softmax;

	public SingleKernal(
			List<View> views,
			List<Integer> hiddenLayerLengthList, 
			int classNum,
			double randomizeBase) throws Exception{

		//item lookup layers
		net4UserList=new ArrayList<Net4ViewUser>();
		for(View view:views){
			//Net4ViewUser net4User=new Net4ViewUser(view.embeddingFileWord,view.embeddingLengthWord,windowSizeWordLookupList,view.averageOutputLength,view.userItemFile,view.trainFile,view.testFile);
			Net4ViewUser net4User=new Net4ViewUser(view);
			net4UserList.add(net4User);
		}  

		//connect layer
		int[] multConInputlengths=new int[net4UserList.size()];
		for(int i=0;i<net4UserList.size();++i){
			multConInputlengths[i]=net4UserList.get(i).userfeature.output.length;
		}
		connectItem = new MultiConnectLayer(multConInputlengths);	
		for(int i=0;i<net4UserList.size();++i){
			net4UserList.get(i).userfeature.link(connectItem, i);
		}
		
		//hidden layers
		hiddenlayernumber = hiddenLayerLengthList.size();
		int inputlength = connectItem.outputLength;
		int outputlength = 0;
		for(int l=0;l<hiddenlayernumber;l++){
			outputlength = hiddenLayerLengthList.get(l);
			LinearTanhLayer hiddenlayer = new LinearTanhLayer(inputlength, outputlength);
			inputlength = outputlength;
			hiddenlayerlist.add(hiddenlayer);
		}
		connectItem.link(hiddenlayerlist.get(0));
		for(int l=0;l<hiddenlayernumber-1;l++){
			hiddenlayerlist.get(l).link(hiddenlayerlist.get(l+1));
		}

		// linear for softmax
		linearForSoftmax = new LinearLayer(outputlength, classNum);
		hiddenlayerlist.get(hiddenlayernumber-1).link(linearForSoftmax);
		softmax = new SoftmaxLayer(classNum);
		linearForSoftmax.link(softmax);

		//initial parameters
		Random rnd = new Random(); 
		for(int l=0;l<hiddenlayernumber;l++){
			hiddenlayerlist.get(l).randomize(rnd, -1.0 * randomizeBase, randomizeBase);
		}
		linearForSoftmax.randomize(rnd, -1.0 * randomizeBase, randomizeBase);
	}

	@SuppressWarnings("deprecation")
	public double[] run(
			int roundNum,
			double probThreshould,
			double learningRate,
			int classNum,
			String dumpItemEmbeddingFile
			) throws Exception
	{

		double[] best_res = new double[5];

		for(Net4ViewUser n4u:net4UserList){
			Collections.shuffle(n4u.trainDataList, new Random(10000));
		}
		for(int round = 1; round <= roundNum; round++)
		{
			double lossV = 0.0;
			int lossC = 0;
			FileTool.writeLog("============== running round: " + round + " ===============");

			for(int idxData = 0; idxData < net4UserList.get(0).trainDataList.size(); idxData++)
			{
				for(Net4ViewUser net4User:net4UserList){
					Data user=net4User.trainDataList.get(idxData);
					net4User.userfeature.input[0] = net4User.uid_i.get(user.userStr);
					net4User.userfeature.forward();
				}
				//fusionLookup.input[0] = 0;
				//fusionLookup.forward();
				connectItem.forward();
				for(int l=0;l<hiddenlayernumber;l++){
					hiddenlayerlist.get(l).forward();
				}
				linearForSoftmax.forward();
				softmax.forward();

				//write down new user feature
				Data data=net4UserList.get(0).trainDataList.get(idxData);
				boolean isAppend=idxData==0?false:true;
				ToolKit.writeFeature2File("train", data, linearForSoftmax.input, isAppend);
				// set cross-entropy error 
				// we minus 1 because the saved goldRating is in range 1~5, while what we need is in range 0~4
				int goldRating = data.goldRating - 1;
				lossV += -Math.log(softmax.output[goldRating]);
				lossC += 1;
				for(int k = 0; k < softmax.outputG.length; k++)
					softmax.outputG[k] = 0.0;
				if(softmax.output[goldRating] < probThreshould)
					softmax.outputG[goldRating] =  1.0 / probThreshould;
				else
					softmax.outputG[goldRating] = 1.0 / softmax.output[goldRating];

				// backward
				softmax.backward();
				linearForSoftmax.backward();
				for(int l=hiddenlayernumber-1;l>=0;l--){
					hiddenlayerlist.get(l).backward();
				}
				connectItem.backward();
				//fusionLookup.backward();
				/*for(Net4ViewUser net4User:net4UserList){
					net4User.userfeature.backward();
				}*/

				// update
				linearForSoftmax.update(learningRate);
				for(int l=hiddenlayernumber-1;l>=0;l--){
					hiddenlayerlist.get(l).update(learningRate);
				}
				//fusionLookup.update(learningRate);

				// clearGrad
				/*for(Net4ViewUser net4User:net4UserList){
					net4User.userfeature.clearGrad();
				}*/
				//fusionLookup.clearGrad();
				connectItem.clearGrad();
				for(int l=0;l<hiddenlayernumber;l++){
					hiddenlayerlist.get(l).clearGrad();
				}
				linearForSoftmax.clearGrad();
				softmax.clearGrad();

				if(idxData % 100 == 0)
				{
					FileTool.writeLog("running idxData = " + idxData + "/" + net4UserList.get(0).trainDataList.size() + "\t "
							+ "lossV/lossC = " + lossV + "/" + lossC + "\t"
							+ " = " + lossV/lossC
							+ "\t" + new Date().toLocaleString());
				}
			}

			FileTool.writeLog("============= finish training round: " + round + " ==============");
			double[] res = validate(round);
			if(best_res[1]<res[1]){
				best_res[1] = res[1];
				best_res[0] = res[0];
				best_res[2] = round;
			}else if(best_res[1]==res[1]&&best_res[0]<=res[0]){
				best_res[1] = res[1];
				best_res[0] = res[0];
				best_res[2] = round;
			}
			double[] pre_res = predict(round);

			if(best_res[2]-round==0){//如果是最好的结果，将其特征和预测结果复制一遍
				best_res[3] = pre_res[0];
				best_res[4] = pre_res[1];
				ToolKit.keepBestResult();
			}
		}
		return best_res;
	}
	public double[] validate(int round) throws Exception
	{
		FileTool.writeLog("=========== validateing round: " + round + " ===============");

		List<Integer> goldList = new ArrayList<Integer>();
		List<Integer> predList = new ArrayList<Integer>();

		for(int idxData = 0; idxData < net4UserList.get(0).validateDataList.size(); idxData++)
		{
			for(Net4ViewUser net4User:net4UserList){
				Data user=net4User.validateDataList.get(idxData);
				net4User.userfeature.input[0] = net4User.uid_i.get(user.userStr);
				net4User.userfeature.forward();
			}
			//fusionLookup.input[0] = 0;
			//fusionLookup.forward();
			connectItem.forward();
			for(int l=0;l<hiddenlayernumber;l++){
				hiddenlayerlist.get(l).forward();
			}
			linearForSoftmax.forward();
			softmax.forward();

			//write down new user feature
			Data data=net4UserList.get(0).validateDataList.get(idxData);
			boolean isAppend=idxData==0?false:true;
			ToolKit.writeFeature2File("validate", data, linearForSoftmax.input, isAppend);
			ToolKit.writeResult2File(softmax.predClass + 1, softmax.output, isAppend);

			predList.add(softmax.predClass + 1);
			goldList.add(data.goldRating);
		}

		double[] res = Metric.calcMetric(goldList, predList);
		FileTool.writeLog("============== finish validating =================");
		return res;
	}
	public double[] predict(int round) throws Exception
	{
		FileTool.writeLog("=========== predicting round: " + round + " ===============");

		List<Integer> goldList = new ArrayList<Integer>();
		List<Integer> predList = new ArrayList<Integer>();

		for(int idxData = 0; idxData < net4UserList.get(0).testDataList.size(); idxData++)
		{
			for(Net4ViewUser net4User:net4UserList){
				Data user=net4User.testDataList.get(idxData);
				net4User.userfeature.input[0] = net4User.uid_i.get(user.userStr);
				net4User.userfeature.forward();
			}
			//fusionLookup.input[0] = 0;
			//fusionLookup.forward();
			connectItem.forward();
			for(int l=0;l<hiddenlayernumber;l++){
				hiddenlayerlist.get(l).forward();
			}
			linearForSoftmax.forward();
			softmax.forward();

			//write down new user feature
			Data data=net4UserList.get(0).testDataList.get(idxData);
			boolean isAppend=idxData==0?false:true;
			ToolKit.writeFeature2File("test", data, linearForSoftmax.input, isAppend);
			ToolKit.writeResult2File(softmax.predClass + 1, softmax.output, isAppend);

			predList.add(softmax.predClass + 1);
			goldList.add(data.goldRating);
		}

		double[] pre_res = Metric.calcMetric(goldList, predList);
		FileTool.writeLog("============== finish predicting =================");
		return pre_res;
	}

	public static void main(String[] args) throws Exception{
		/*final String resfile = args[1];
		final String fold = args[0];
		String embeddingFile1 = args[2];
		String embeddingFile2 = args[3];
		String embeddingFile3 = args[4];
		String embeddingFile4 = args[5];
		String embeddingFile5 = args[6];
		String embeddingFile6 = args[7];*/

		final String resfile = "SingleKernal_VFriTagAvgVec";
		final String fold = "0";
		String embeddingFile1 = "";//"Feature_UserInfo\\Tag_feature.txt";//
		String embeddingFile2 = "";//"Feature_Relation\\VFri_feature.txt";//
		String embeddingFile3 = "";//"Feature_Src\\Src_feature.txt";//
		String embeddingFile4 = "Feature_Relation\\VFriTagAvgVec_feature.txt";//
		String embeddingFile5 = "";//"Feature_Relation\\SrcType1Vector_feature.txt";//
		String embeddingFile6 = "";//"Feature_Src\\SrcAvgVec_feature.txt";//

		int embeddingLength1 = 6465;
		int embeddingLength2 = 65373;
		int embeddingLength3 = 3905;
		int embeddingLength4 = 101;

		FileTool.resfile = FileTool.base1 + resfile +"\\";
		FileTool.resfile += fold+"\\";
		FileTool.mkdir(FileTool.resfile);

		String trainFile =FileTool.userID + fold+"\\"+ "training_id.txt";
		String testFile = FileTool.userID + fold+"\\"+ "testing_id.txt";
		String validateFile = FileTool.userID + fold+"\\"+ "learning_id.txt";
		List<Integer> hiddenLayerLengthList = new ArrayList<Integer>();
		hiddenLayerLengthList.add(10);

		int classNum = 2;
		int roundNum = 50;
		double probThreshold = 0.001;
		double learningRate = 0.02;
		double randomizeBase = 0.001;

		List<View> views=new ArrayList<View>();
		if(!embeddingFile1.equals(""))views.add(new View(FileTool.base2+embeddingFile1,embeddingLength1,trainFile,testFile,validateFile));
		if(!embeddingFile2.equals(""))views.add(new View(FileTool.base2+embeddingFile2,embeddingLength2,trainFile,testFile,validateFile));
		if(!embeddingFile3.equals(""))views.add(new View(FileTool.base2+embeddingFile3,embeddingLength3,trainFile,testFile,validateFile));
		if(!embeddingFile4.equals(""))views.add(new View(FileTool.base2+embeddingFile4,embeddingLength4,trainFile,testFile,validateFile));
		if(!embeddingFile5.equals(""))views.add(new View(FileTool.base2+embeddingFile5,embeddingLength4,trainFile,testFile,validateFile));
		if(!embeddingFile6.equals(""))views.add(new View(FileTool.base2+embeddingFile6,embeddingLength4,trainFile,testFile,validateFile));
		
		FileTool.writeLog("Start...");

		SingleKernal main = new SingleKernal(
				views,
				hiddenLayerLengthList,
				classNum, 
				randomizeBase);
		double[] res = main.run(roundNum, 
				probThreshold, 
				learningRate, 
				classNum,
				"");
		FileTool.writeLog("End");
		FileTool.writeLog(fold+":"+res[3]*100+"\\"+res[4]*100+"\t"+res[2]+":"+res[0]*100+"\\"+res[1]*100);
		FileTool.saveLog(false);
	}

}
