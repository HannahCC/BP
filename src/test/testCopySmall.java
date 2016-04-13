package test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import adapter.FileTool;

public class testCopySmall {
	public static void excCopySmall() throws IOException{
		String dir="F:\\ExpData\\DataFromOther\\dytang-cnn\\yelp-13\\";
		File d=new File(dir);
		File[] fs=d.listFiles();
		for(File f:fs){
			String[] dirNodes=FileTool.getDirNodes(f.getAbsolutePath());
			String dest=FileTool.forwardInsertDirNode(f.getAbsolutePath(),dirNodes[dirNodes.length-1], "Test")+f.getName();
			int end=FileTool.getFileLineNum(f)/100;
			FileTool.writeLog(end+"");
			FileTool.copy(f.getAbsolutePath(), 1,end ,dest);
		}

	}
	
	public static void excRandomSelectSmall() throws IOException{
		String dir="F:\\ExpData\\DataFromOther\\dytang-cnn\\yelp-13\\";
		File d=new File(dir);
		File[] fs=d.listFiles();
		for(File f:fs){
			String[] dirNodes=FileTool.getDirNodes(f.getAbsolutePath());
			String dest=FileTool.forwardInsertDirNode(f.getAbsolutePath(),dirNodes[dirNodes.length-1], "Test")+f.getName();
			List<String> contents=FileTool.readFile2List(f.getAbsolutePath());
			Collections.shuffle(contents);
			int end=contents.size()/100;
			FileTool.writeLog(end+"");
			FileTool.writeList2File(contents.subList(0, end), dest);
			
			
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		excRandomSelectSmall();
	}

}
