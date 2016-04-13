package wyConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nnet.LookupLayer;
import other.Data;
import other.Funcs;
import adapter.FileTool;

public class Net4ViewUser {
	public HashMap<String, Integer> uid_i = null;

	public List<Data> trainDataList=null;
	public List<Data> testDataList=null;  
	public List<Data> validateDataList=null;  

	public LookupLayer userfeature;
	public int idxData;
	public String type;

	public String trainFile;
	public String testFile;
	public String validateFile;
	public String embeddingFile;

	public Net4ViewUser(View view) throws Exception{
		this.trainFile=view.trainFile;
		this.testFile=view.testFile;
		this.validateFile = view.validateFile;
		String[] temp = view.embeddingFile.split("\\\\");
		this.embeddingFile = temp[temp.length-1].replace(".txt", "");
		int userSize = Funcs.lineCounter(view.embeddingFile, "utf8");

		userfeature = new LookupLayer(view.embeddingLength, userSize, 1);//Net4ViewUser的主要目的在于得到用户的向量
		uid_i = new HashMap<String, Integer>();
		Funcs.loadEmbeddingFile(view.embeddingFile, "utf8", false, uid_i, userfeature.table);
		this.loadData(trainFile, testFile, validateFile);
	}

	public void loadData(
			String trainFile,
			String testFile,
			String validateFile)
	{
		FileTool.writeLog("================ start loading corpus ==============");
		trainDataList = new ArrayList<Data>();  
		Funcs.loadCorpus(trainFile, "utf8", trainDataList);
		FileTool.writeLog("training size: " + trainDataList.size());

		if(!validateFile.equals("")){
			validateDataList = new ArrayList<Data>();  
			Funcs.loadCorpus(validateFile, "utf8", validateDataList);
			FileTool.writeLog("validateDataList size: " + validateDataList.size());
		}

		testDataList = new ArrayList<Data>();  
		Funcs.loadCorpus(testFile, "utf8", testDataList);
		FileTool.writeLog("testDataList size: " + testDataList.size());

		FileTool.writeLog("================ finsh loading corpus ==============");
	}

}
