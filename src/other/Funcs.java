package other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import adapter.FileTool;

public class Funcs {
	
//	public static DecimalFormat dFormat = new DecimalFormat("0.0000000000");
	
	public static int lineCounter(String file, String encoding) throws IOException
	{
		int lineCounter = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file) , encoding));
		while(reader.readLine() != null)
		{   
			lineCounter++;
		}
		reader.close();
		return lineCounter;
	}
	
	public static void loadEmbeddingFile(String embedFile,
			String encoding,
			boolean isL2Norm,
			HashMap<String, Integer> uid_i,
			Map<Integer, Map<Integer, Double>> table) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(embedFile) , encoding));
		String line = null;
		
		int idx = 0;
		while((line = reader.readLine()) != null)
		{   
			String[] items = line.split("\t");
			String uid = items[0];
			Map<Integer, Double> vector = new HashMap<Integer, Double>();
			for(int i=1;i<items.length;i++){
				String[] d_v = items[i].split(":");
				int d = Integer.parseInt(d_v[0]);
				double v = Double.parseDouble(d_v[1]);
				vector.put(d, v);
			}
			table.put(idx, vector);
			uid_i.put(uid, idx);
			// to be debuged
			if(isL2Norm)
			{
				l2Norm(vector);
			}
			
			idx++;
			
		}
		reader.close();
	}

	public static void loadUserItemsFile(String userItemFile, String encoding, String unkStr, HashMap<String, String> uid_items) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(userItemFile) , encoding));
		String line = null;
		while((line = reader.readLine()) != null)
		{   
			String[] spilts = line.split("\t",2);
			String uid = spilts[0];
			String items = spilts[1].trim().replace("\\s+", "\t");
			items += "\t"+ unkStr;items += "\t"+ unkStr;items += "\t"+ unkStr;//保证每个User都至少有3个Item，则该User不会被放弃使用
			uid_items.put(uid, items);
		}
		reader.close();
	}
	
	
	public static HashMap<String, Double[]> loadEmbedFile(String embedFile,
			int embeddingLength,
			String encoding,
			boolean isL2Norm) throws IOException
	{
		HashMap<String, Double[]> lookupTable = new HashMap<String, Double[]>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(embedFile) , encoding));
		String line = null;
		
		while((line = reader.readLine()) != null)
		{
			String[] splits = line.split(" |\t");
			
			String word = splits[0];
			Double[] embedValues = new Double[embeddingLength];
			
			for(int j = 1; j < splits.length; j++)
			{
				Double value = Double.parseDouble(splits[j]);
				embedValues[j-1] = value;
			}
			
			if(isL2Norm)
			{
				l2Norm(embedValues);
			}
			
			lookupTable.put(word, embedValues);
		}
		reader.close();
		
		return lookupTable;
	}
	
	public static void l1Norm(Double[] values)
	{
		Double Z = 0.0;
		for(int i = 0; i < values.length; i++)
		{
			Z = Z + Math.abs(values[i]);
		}
		
		for(int i = 0; i < values.length; i++)
		{
			values[i] = values[i] * 1.0 / Z;
		}
	}
	
	public static void l2Norm(Map<Integer, Double> values)
	{
		Double Z = 0.0;
		for(Entry<Integer, Double> k_v : values.entrySet()){
			Z += k_v.getValue();
		}
		for(Entry<Integer, Double> k_v : values.entrySet()){
			k_v.setValue(k_v.getValue()*1.0/Z);
		}
	}
	public static void l2Norm(Double[] values)
	{
		Double Z = 0.0;
		for(int i = 0; i < values.length; i++)
		{
			Z = Z + values[i] * values[i];
		}
		
		for(int i = 0; i < values.length; i++)
		{
			values[i] = values[i] * 1.0 / Z;
		}
	}
	public static void l2Norm(double[] values)
	{
		Double Z = 0.0;
		for(int i = 0; i < values.length; i++)
		{
			Z = Z + values[i] * values[i];
		}
		
		for(int i = 0; i < values.length; i++)
		{
			values[i] = values[i] * 1.0 / Z;
		}
	}
	
	public static void dumpEmbedFile(String embedFile, 
			String encoding,
			HashMap<String, Integer> vocabMap,
			double[][] table,
			int embeddingLength)
	{
		TreeMap<Integer, String> inverseVocabMap = new TreeMap<Integer, String>();
		for(String word: vocabMap.keySet())
		{
			inverseVocabMap.put(vocabMap.get(word), word);
		}
		
		try{
			FileTool.guaranteeFileDirExist(embedFile);
			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(embedFile), encoding)));
			for(int idx: inverseVocabMap.keySet())
			{
				writer.write(inverseVocabMap.get(idx));
				for(int j = 0; j < embeddingLength; j++)
				{
					writer.write(" " + String.format("%.7f", table[idx][j]));
				}
				writer.write("\n");
			}
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, String> parseArgs(String[] args)
	{
		HashMap<String, String> argMap = new HashMap<String, String>();
		
		for(int i = 0; i < args.length; i++)
		{
			String key = args[i];
			if(key.startsWith("-"))
			{
				if(i + 1 < args.length)
				{
					argMap.put(args[i], args[i + 1]);
					i++;
				}
			}
		}
		return argMap;
	}
	
	public static void dumpCorpus(List<Data> datasets, String outpath, String encoding)
			throws IOException
	{
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(outpath), encoding)));
		for(Data data: datasets)
		{
			writer.write(data.userStr + "\t\t" 
					+	data.productStr + "\t\t"
					+ 	data.goldRating + "\t\t"
					+	data.reviewText + "\n");
		}
		writer.close();
	}
	
	public static void loadSegCorpus(String inPath,
			String encoding,
			HashMap<String, List<Data> > trainingDatas,
			String userORproduct,
			int minFreq)
	{
		loadSegCorpus(inPath, encoding, trainingDatas, userORproduct);
		HashSet<String> keySet = new HashSet<String>();
		
		keySet.addAll(trainingDatas.keySet());
		for(String key: keySet)
		{
			if(trainingDatas.get(key).size() < minFreq)
			{
				trainingDatas.remove(key);
			}
		}
	}
	
	public static void loadSegCorpus(String inPath,
			String encoding,
			HashMap<String, List<Data> > trainingDatas,
			String userORproduct)
	{
		List<Data> datasets = new ArrayList<Data>();
		loadSegCorpus(inPath, encoding, datasets);
		
		for(Data data: datasets)
		{
			String key = "";
			if(userORproduct.equals("user"))
			{
				key = data.userStr;
			}
			else
			{
				key = data.productStr;
			}
			
			if(!trainingDatas.containsKey(key))
			{
				trainingDatas.put(key, new ArrayList<Data>());
			}
			
			trainingDatas.get(key).add(data);
		}
		datasets.clear();
	}
	
	public static void loadSegCorpus(String inPath,
			String encoding,
			List<Data> trainingDatas)
	{
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inPath) , encoding));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				String[] splits = line.split("\t\t");
				
				if(splits.length < 4)
				{
//					IO.writeLog(line);
					continue;
				}
				
				String userStr = splits[0];
				String productStr = splits[1];
				int goldRating = Integer.parseInt(splits[2].trim());
				String segText = splits[3];
				
				String[] words = segText.trim().split(" ");
				
				trainingDatas.add(new Data(userStr, productStr, words, goldRating));
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public static void loadCorpus(
			String inPath, 
			String encoding, 
			List<Data> trainingDatas)
	{
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inPath) , encoding));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				String[] splits = line.split("\t");
				String uid = splits[0];
				int label = Integer.parseInt(splits[1]);
				trainingDatas.add(new Data(uid, "", "", label));
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void loadCorpusCL(
			String allSamplesFile,
			String idLablesFile, 
			String encoding, 
			List<Data> trainingDatas)
	{
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(idLablesFile) , encoding));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				String[] splits = line.split("\t\t");
				
				if(splits.length < 4)
				{
//					IO.writeLog(line);
					continue;
				}
				
				String userStr = splits[0];
				String productStr = splits[1];
				int goldRating = Integer.parseInt(splits[2].trim());
				String reviewText = splits[3];
				
				trainingDatas.add(new Data(userStr, productStr, reviewText, goldRating));
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void minLengthSentence(List<String> trainFiles,
			String encoding)
	{
		int minLength = 999999;
		for(String fileP: trainFiles)
		{
			FileTool.writeLog("running " + fileP);
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileP) , encoding));
				String line = null;
				while((line = reader.readLine()) != null)
				{
					if(line.split(" ").length < minLength)
					{
						minLength = line.split(" ").length;
					}
				}
				reader.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		
		FileTool.writeLog(minLength+"");
	}
	
	public static void loadVocabFromFile(String vocabFile, 
			HashMap<String, Integer> vocabMap,
			String encoding)
	{
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(vocabFile) , encoding));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				String[] words = line.split(" |\t");
				if(words.length < 2)
				{
					FileTool.writeLog(line);
				}
				String word = words[0];
				int idx = Integer.parseInt(words[1]);
				
				vocabMap.put(word, idx);
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void filterMapWithFreq(
			HashMap<String, Integer> origFreatureFreqMap,
			int minFreq,
			HashMap<String, Integer> vocabMap)
	{
		TreeMap<Integer, List<String>> treeMap = new TreeMap<Integer, List<String>>();
		
		for(String word: origFreatureFreqMap.keySet())
		{
			int freq = origFreatureFreqMap.get(word);
			if(freq >= minFreq)
			{
				if(!treeMap.containsKey(freq))
				{
					treeMap.put(freq, new ArrayList<String>());
				}
				treeMap.get(freq).add(word);
			}
		}
		
		int idx = 1;
		for(int freq: treeMap.descendingKeySet())
		{
			for(String word: treeMap.get(freq))
			{
				vocabMap.put(word, idx);
				idx++;
			}
		}
	}
	
	public static void dumpVocab(HashMap<String, Integer> hashMap, 
			String outputFile, 
			String encoding)
	{
		TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>();
		for(String word: hashMap.keySet())
		{
			treeMap.put(hashMap.get(word), word);
		}
		
		try{
			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(outputFile), encoding)));
			for(int idx: treeMap.keySet())
			{
				writer.write(treeMap.get(idx) + " " + idx + "\n");
			}
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static double cosineSim(String[] words1, String[] words2)
	{
		//double sim = 0.0;
		HashSet<String> localWordVocab = new HashSet<String>();
		for(String word: words1)
		{
			localWordVocab.add(word);
		}
		for(String word: words2)
		{
			localWordVocab.add(word);
		}
		
		List<String> vocab = new ArrayList<String>();
		vocab.addAll(localWordVocab);
		
		Double[] idx1 = new Double[vocab.size()];
		Double[] idx2 = new Double[vocab.size()];
		
		for(int i = 0; i < vocab.size(); i++)
		{
			idx1[i] = 0.0;
			idx2[i] = 0.0;
		}
		
		for(String word1: words1)
		{
			idx1[vocab.indexOf(word1)] = 1.0;
		}
		
		for(String word2: words2)
		{
			idx2[vocab.indexOf(word2)] = 1.0;
		}
		
		return cosineSim(idx1, idx2);
	}
	
	public static Double cosineSim(Double[] value1, Double[] value2)
	{
		Double xx = 0.0;
		Double yy = 0.0;
		Double xy = 0.0;
		
		for(int i = 0; i < value1.length; i++)
		{
			Double x = value1[i];
			Double y = value2[i];
				
			xx = xx + x * x;
			yy = yy + y * y;
			xy = xy + x * y;
		}
		
		Double sim = xy / (Math.sqrt(xx) * Math.sqrt(yy));
		return sim;
	}
	
	public static int[][] fillDocument(
			String[] sentences,
			HashMap<String, Integer> vocabMap,
			String unkWord)
	{
		int[][] wordMatrix = new int[sentences.length][];
		for(int i = 0; i < wordMatrix.length; i++)
		{
			String[] words = sentences[i].trim().split(" |\t");
			wordMatrix[i] = new int[words.length];
			
			for(int k = 0; k < words.length; k++)
			{
				String word = words[k].trim();
				if(vocabMap.containsKey(word))
				{
					wordMatrix[i][k] = vocabMap.get(word);
				}
				else
				{
					wordMatrix[i][k] = vocabMap.get(unkWord);
				}
			}
		}
		
		return wordMatrix;
	}
	
	public static int[] fillSentence(
			String[] words,
			HashMap<String, Integer> vocabMap)
	{
		int[] wordIns = new int[words.length];
		for(int i = 0; i < wordIns.length; i++)
		{
			String word = words[i];
			
			if(vocabMap.containsKey(word))
			{
				wordIns[i] = vocabMap.get(word);
			}
			else
			{
				wordIns[i] = vocabMap.get("<unk>");
			}
		}
		
		return wordIns;
	}

//	public static int[] fillWindow(
//			int beginIdx,
//			Data data,
//			int windowSize,
//			HashMap<String, Integer> vocabMap) 
//	{
//		int[] wordIns = new int[windowSize];
//		for(int i = 0; i < windowSize; i++)
//		{
//			String word = data.words[beginIdx + i];
//			
//			if(vocabMap.containsKey(word))
//			{
//				wordIns[i] = vocabMap.get(word);
//			}
//			else
//			{
//				wordIns[i] = vocabMap.get("<unk>");
//			}
//		}
//		
//		return wordIns;
//	}
	
//	public static int[] fillSentence(
//			Data data,
//			HashMap<String, Integer> vocabMap)
//	{
//		int[] wordIns = new int[data.words.length];
//		for(int i = 0; i < wordIns.length; i++)
//		{
//			String word = data.words[i];
//			
//			if(vocabMap.containsKey(word))
//			{
//				wordIns[i] = vocabMap.get(word);
//			}
//			else
//			{
//				wordIns[i] = vocabMap.get("<unk>");
//			}
//		}
//		
//		return wordIns;
//	}
}
