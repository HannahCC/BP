package adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import other.Data;

public  class ToolKit {
	public static void prepareDataForWordEmbeding(String dir,String dest) throws IOException{
		File d=new File(dir);
		File[] fs=d.listFiles();
		List<String> reviewList=new LinkedList<String>();

		for(File f:fs){
			BufferedReader reader=FileTool.getBufferedReaderFromFile(f.getAbsolutePath());
			String line="";
			while((line=reader.readLine())!=null){
				String[] elms=line.split("\t\t");
				String filterStr=elms[3].replace("<sssss>", "");
				reviewList.add(filterStr);
			}
			reader.close();
		}

		FileTool.write(reviewList, dest);
	}
	public static void printDiem(String filePath) throws IOException{
		BufferedReader reader=FileTool.getBufferedReaderFromFile(filePath);
		String line=reader.readLine();
		String[] elms=line.split("\\s{1,}");
		FileTool.writeLog("diem is "+(elms.length-1));
	}

	public static void writeFeature2File(String type,Data data, double[] feature,boolean isAppend) throws UnsupportedEncodingException, FileNotFoundException{
		PrintWriter writer1=FileTool.getPrintWriterForFile(FileTool.resfile+"cur_"+type+"ing_id.txt",isAppend);
		PrintWriter writer2=FileTool.getPrintWriterForFile(FileTool.resfile+"cur_"+type+"ing_data.txt",isAppend);
		String seperater="\t";
		writer1.write(data.userStr+"\t"+data.goldRating);
		writer2.write(data.goldRating+"");
		for(int i=0;i<feature.length;++i){
			writer2.write(seperater+(i+1)+":"+feature[i]);
		}
		writer1.write("\r\n");
		writer1.close();
		writer2.write("\r\n");
		writer2.close();
	}

	public static void writeResult2File(int label, double[] result,boolean isAppend) throws UnsupportedEncodingException, FileNotFoundException{
		PrintWriter writer=FileTool.getPrintWriterForFile(FileTool.resfile+"cur_result_cnn.txt",isAppend);
		String seperater=" ";
		writer.write(label+"");
		for(int i=0;i<result.length;++i){
			writer.write(seperater+result[i]);
		}
		writer.write("\r\n");
		writer.close();
	}
	public static void keepBestResult() throws IOException {

		File srcFile = new File(FileTool.resfile+"cur_training_id.txt");
		File destFile = new File(FileTool.resfile+"training_id.txt");
		FileTool.copy(srcFile, destFile);
		srcFile = new File(FileTool.resfile+"cur_training_data.txt");
		destFile = new File(FileTool.resfile+"training_data.txt");
		FileTool.copy(srcFile, destFile);
		srcFile = new File(FileTool.resfile+"cur_testing_id.txt");
		destFile = new File(FileTool.resfile+"testing_id.txt");
		FileTool.copy(srcFile, destFile);
		srcFile = new File(FileTool.resfile+"cur_testing_data.txt");
		destFile = new File(FileTool.resfile+"testing_data.txt");
		FileTool.copy(srcFile, destFile);
		srcFile = new File(FileTool.resfile+"cur_result_cnn.txt");
		destFile = new File(FileTool.resfile+"result_cnn.txt");
		FileTool.copy(srcFile, destFile);
	}
}
