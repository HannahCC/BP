package adapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;


public class FileTool {
	public static final String base1="D:\\Project_DataMinning\\DataProcessd\\Sina_GenderPre_1635\\UserTextNN\\";
	public static final String base2="D:\\Project_DataMinning\\Data\\Sina_res\\Sina_NLPIR2223_GenderPre\\";	
	public static String userID= base1 + "UserID\\";
	public static String resfile= "";
	//public static String logfile= "";

	public static void mkdir(String dirString) {
		File dir= new File(dirString);
		if(!dir.exists()){dir.mkdirs();}
	}
	public static BufferedReader getBufferedReaderFromFile(String filePath,String encoding) throws UnsupportedEncodingException, FileNotFoundException{
		return new BufferedReader(new InputStreamReader(new FileInputStream(filePath),encoding));
	}
	public static BufferedReader getBufferedReaderFromFile(String filePath) throws UnsupportedEncodingException, FileNotFoundException{
		return getBufferedReaderFromFile(filePath,"utf-8");
	}
	public static PrintWriter getPrintWriterForFile(String filePath,boolean isAppend,String encoding) throws UnsupportedEncodingException, FileNotFoundException{
		guaranteeFileDirExist(filePath);
		return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,isAppend), encoding)));
	}
	public static PrintWriter getPrintWriterForFile(String filePath,String encoding) throws UnsupportedEncodingException, FileNotFoundException{
		return getPrintWriterForFile(filePath,false,encoding);
	}
	public static PrintWriter getPrintWriterForFile(String filePath,boolean isAppend) throws UnsupportedEncodingException, FileNotFoundException{
		return getPrintWriterForFile(filePath,isAppend,"utf-8");
	}
	public static PrintWriter getPrintWriterForFile(String filePath) throws UnsupportedEncodingException, FileNotFoundException{
		return getPrintWriterForFile(filePath,false,"utf-8");
	}
	public static void guaranteeDirExist(String dirPath){
		File dir=new File(dirPath);
		if(!dir.exists()) dir.mkdirs();
	}
	public static void guaranteeFileDirExist(String file){
		File f=new File(file);
		guaranteeDirExist(f.getParent());
	}
	public static void glanceFileContent(String filePath,int start,int end) throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(filePath));
		String curLine="";
		int lineNum=0;
		while((curLine=br.readLine())!=null){
			++lineNum;
			if(lineNum>=start&&lineNum<=end) writeLog(curLine);
			if(lineNum>end) break;
		}
		br.close();
	}
	public static int getFileLineNum(String filePath) throws Exception{
		return getFileLineNum(new File(filePath));
	}
	public static int getFileLineNum(File file) throws IOException{
		BufferedReader br=new BufferedReader(new FileReader(file));
		int lineNum=0;
		while(br.readLine()!=null){
			++lineNum;
		}
		br.close();
		return lineNum;
	}
	public static String getParentPath(String filePath){
		return getParentPath(new File(filePath));
	}
	public static String getParentPath(File file){
		return file.getParent();
	}
	public static String getFileName(String filePath){
		File f=new File(filePath);
		return f.getName();
	}
	public static String getFileFomatType(String filePath){
		File f=new File(filePath);
		return f.getName().split("\\.")[1];
	}
	public static String getPureFileName(String filePath){
		File f=new File(filePath);
		return f.getName().split("\\.")[0];
	}
	public static String getPureName(String path){
		File f=new File(path);
		if(f.isDirectory()) return f.getName();
		else return getPureFileName(path);
	}
	public static String[] getDirNodes(String file){
		File f=new File(file);
		String dirPath="";
		if(f.isDirectory()){
			dirPath=f.getAbsolutePath();
		}else{
			dirPath=f.getParent();
		}
		String[] dirNodes=dirPath.split("\\\\");
		return dirNodes;
	}
	public static List<String> getDirNodesList(String file){
		List<String> nodesList=new ArrayList<String>();
		String[] nodes=getDirNodes(file);
		for(String n:nodes) nodesList.add(n);
		return nodesList;
	}
	public static String backReplaceDirNode(String file,String replaceNode,String newNode){
		String[] nodes=getDirNodes(file);
		for(int i=nodes.length-1;i>=0;--i){
			if(nodes[i].equals(replaceNode)) {
				nodes[i]=newNode;
				break;
			}
		}
		StringBuilder stb=new StringBuilder();
		for(String s:nodes){
			stb.append(s+"\\");
		}
		return stb.toString();
	}
	public static String forwardReplaceDirNode(String file,String replaceNode,String newNode){
		String[] nodes=getDirNodes(file);
		for(int i=0;i<nodes.length;++i){
			if(nodes[i].equals(replaceNode)) {
				nodes[i]=newNode;
				break;
			}
		}
		StringBuilder stb=new StringBuilder();
		for(String s:nodes){
			stb.append(s+"\\\\");
		}
		return stb.toString();
	}
	//从最后一个节点往第一个节点遍历，找到第一个插入点节点时，在该插入节点之前插入新节点
	public static String forwardInsertDirNode(String file,String insertPointNode,String newNode){
		String[] nodes=getDirNodes(file);
		int insertPoint=0;
		for(int i=nodes.length-1;i>=0;--i ){
			if(nodes[i].equals(insertPointNode)){
				insertPoint=i;
				break;
			}
		}
		StringBuilder stb=new StringBuilder();
		for(int i=0;i<insertPoint;++i){
			stb.append(nodes[i]+"\\\\");
		}
		stb.append(newNode+"\\\\");
		for(int i=insertPoint;i<nodes.length;++i){
			stb.append(nodes[i]+"\\\\");
		}
		return stb.toString();

	}
	//从第一个节点往最后一个节点遍历，找到第一个插入点节点时，在该插入节点之后插入新节点
	public static String backInsertDirNode(String file,String insertPointNode,String newNode){
		String[] nodes=getDirNodes(file);
		int insertPoint=0;
		for(int i=0;i<nodes.length;++i ){
			if(nodes[i].equals(insertPointNode)){
				insertPoint=i;
				break;
			}
		}
		StringBuilder stb=new StringBuilder();
		for(int i=0;i<=insertPoint;++i){
			stb.append(nodes[i]+"\\\\");
		}
		stb.append(newNode+"\\\\");
		for(int i=insertPoint+1;i<nodes.length;++i){
			stb.append(nodes[i]+"\\\\");
		}
		return stb.toString();

	}
	public static void copy(String srcFile,int startLineNum,int endLineNum,String destFile) throws IOException{
		copy(new File(srcFile),startLineNum,endLineNum,new File(destFile));
	}

	public static void copy(File srcFile,int startLineNum,int endLineNum,File destFile) throws IOException{
		BufferedReader reader=FileTool.getBufferedReaderFromFile(srcFile.getAbsolutePath());
		PrintWriter writer=FileTool.getPrintWriterForFile(destFile.getAbsolutePath());
		int lineNum=1;
		String line="";
		while((line=reader.readLine())!=null){
			if(lineNum>=startLineNum){
				if(lineNum>endLineNum){break;} 
				writer.write(line);
				writer.write("\r\n");
			}
			++lineNum;
		}
		writer.close();
	}

	public static void copy(File srcFile,File destFile) throws IOException{
		try {
			BufferedReader br = new BufferedReader(new FileReader(srcFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(destFile));
			String line = null;
			while((line = br.readLine())!=null){
				bw.write(line+"\r\n");
			}
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static StringBuffer log = new StringBuffer();

	public static DecimalFormat decimalFormat = new DecimalFormat("#0.000000");   

	public static void writeLog(String info) {
		System.out.println(info);
		log.append(info+"\r\n");
	}
	public static void saveLog(boolean isAppend) {
		File f = new File(FileTool.resfile+"log.txt");
		try{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,isAppend), "utf-8"));
			bw.write(log.toString());
			bw.flush();
			bw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public static ArrayList<String> readFile(String inPath, String encoding)
	{
		ArrayList<String> list = new ArrayList<String>();
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inPath) , encoding));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				list.add(line);
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return list;
	}

	public static String readFileStr(String inPath, String encoding)
	{
		String content = "";
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inPath) , encoding));
			String line = null;
			while((line = reader.readLine()) != null)
			{
				content = content + line + "\n";
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return content;
	}

	public static HashSet<String> readFileSet(String inPath, String encoding)
	{
		ArrayList<String> list = readFile(inPath, encoding);
		HashSet<String> set = new HashSet<String>();
		set.addAll(list);
		return set;
	}

	/**
	 * The input format is => word + "\t" + index(frequency)
	 * @param inPath
	 * @param encoding
	 * @return
	 */
	public static LinkedHashMap<String, Integer> readMap(String inPath, String encoding)
	{
		LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
		ArrayList<String> tmpList = readFile(inPath, encoding);
		for(String tmpLine: tmpList)
		{
			String[] set = tmpLine.split("\t");
			map.put(set[0], Integer.parseInt(set[1]));
		}
		return map;
	}
	public static void write(List<String> lists,String dest) throws UnsupportedEncodingException, FileNotFoundException{
		PrintWriter writer=FileTool.getPrintWriterForFile(dest);
		for(String s:lists){
			writer.write(s);
			writer.write("\r\n");
		}
		writer.close();
	}
	public static List<String> readFile2List(String filePath) throws IOException{
		BufferedReader reader=FileTool.getBufferedReaderFromFile(filePath);
		List<String> list=new ArrayList<String>();
		String line="";
		while((line=reader.readLine())!=null){
			list.add(line);
		}
		reader.close();
		return list;
	}
	public static void writeList2File(List<String> list,String dest ) throws UnsupportedEncodingException, FileNotFoundException{
		PrintWriter writer=FileTool.getPrintWriterForFile(dest);
		for(String s:list){
			writer.write(s);
			writer.write("\r\n");
		}
		writer.close();
	}
	/**
	 * 
	 * @param outPath
	 * @param map
	 * @param encoding
	 * @param isDescend 1 if order in ascend; 0 if order in descend
	 */
	@SuppressWarnings("unchecked")
	public static void writeMap(
			HashMap<String, Integer> map,
			String outPath, 
			String encoding, 
			int isAscend)
	{
		ArrayList<String> outlist = new ArrayList<String>();

		@SuppressWarnings("rawtypes")
		ArrayList<Entry> tmpList = new ArrayList<Entry>();
		tmpList.addAll(map.entrySet());
		if(isAscend == 1)
		{
			Collections.sort(tmpList, new EntryAscendComparator());
		}
		else if(isAscend == 0)
		{
			Collections.sort(tmpList, new EntryDescendComparator());
		}
		for(@SuppressWarnings("rawtypes") Entry entry: tmpList)
		{
			outlist.add(entry.getKey() + "\t" + entry.getValue());
		}

		writeFile(outPath, outlist, encoding);
	}

	public static void writeSet(String outPath, HashSet<String> set, String encoding)
	{
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(set);
		writeFile(outPath, list, encoding);
	}

	@SuppressWarnings("unchecked")
	public static void writeIntMap(String outPath, HashMap<Integer, Integer> map, String encoding, int isAscend)
	{
		ArrayList<String> outlist = new ArrayList<String>();

		@SuppressWarnings("rawtypes")
		ArrayList<Entry> tmpList = new ArrayList<Entry>();
		tmpList.addAll(map.entrySet());
		if(isAscend == 1)
		{
			Collections.sort(tmpList, new EntryAscendComparator());
		}
		else if(isAscend == 0)
		{
			Collections.sort(tmpList, new EntryDescendComparator());
		}
		for(@SuppressWarnings("rawtypes") Entry entry: tmpList)
		{
			outlist.add(entry.getKey() + "\t" + entry.getValue());
		}

		writeFile(outPath, outlist, encoding);
	}

	public static void writeFile(String outPath, Collection<String> list, String encoding)
	{
		try{
			//			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
			//			          new FileOutputStream(outPath), encoding)));
			PrintWriter writer =FileTool.getPrintWriterForFile(outPath, encoding);
			for(String line: list)
			{
				writer.write(line + "\n");
			}
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void writeIntFile(String outPath, Collection<Integer> list, String encoding)
	{
		try{
			//			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
			//			          new FileOutputStream(outPath), encoding)));
			PrintWriter writer = FileTool.getPrintWriterForFile(outPath, encoding);
			for(Integer line: list)
			{
				writer.write(line + "\n");
			}
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取dir文件夹下所有文件列表
	 * @param dir
	 */
	public static ArrayList<String> getFileNames(String dir){
		ArrayList<String> fileNames = new ArrayList<String>();
		File dirFile = new File(dir);
		File[] _files = dirFile.listFiles();
		for(File file: _files){
			if(file.isFile()){
				try {
					fileNames.add(file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileNames;
	}

	public static ArrayList<String> getDirNames(String dir){
		ArrayList<String> fileNames = new ArrayList<String>();
		File dirFile = new File(dir);
		File[] _files = dirFile.listFiles();
		for(File file: _files){
			if(file.isDirectory()){
				try {
					fileNames.add(file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileNames;
	}
}
@SuppressWarnings("rawtypes")
class EntryAscendComparator implements Comparator{

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		Entry<String, Integer> e0 = (Entry<String, Integer>) arg0;
		Entry<String, Integer> e1 = (Entry<String, Integer>) arg1;

		return e0.getValue().compareTo(e1.getValue());
	}
}
@SuppressWarnings("rawtypes")
class EntryDescendComparator implements Comparator{

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		Entry<String, Integer> e0 = (Entry<String, Integer>) arg0;
		Entry<String, Integer> e1 = (Entry<String, Integer>) arg1;

		return e1.getValue().compareTo(e0.getValue());
	}

}
