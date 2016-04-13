package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import adapter.FileTool;

public class GetResult {

	public static int final_round = 100;
	public static void main(String args[]) throws IOException {
		/*getResult(final_round,"MutiKernalAuto_Tag");
		getResult(final_round,"MutiKernalAuto_TagAvgVec");
		getResult(final_round,"MutiKernalAuto_Src");
		getResult(final_round,"MutiKernalAuto_SrcAvgVec");
		getResult(final_round,"MutiKernalAuto_VFri");
		getResult(final_round,"MutiKernalAuto_VFriVec");
		getResult(final_round,"MutiKernalAuto_VFriAvgVec");
		getResult(final_round,"MutiKernalAuto_FriVec");
		getResult(final_round,"MutiKernalAuto_FriAvgVec");
		//getResult(final_round,"MutiKernalAuto_VFriTagAvgVec");
		getResult(final_round,"MutiKernalAuto_FriTagAvgVec");*/

		getResult(final_round,"MutiKernalAuto_Tag+Src");
		getResult(final_round,"MutiKernalAuto_Tag+VFri");
		getResult(final_round,"MutiKernalAuto_Src+VFri");
		getResult(final_round,"MutiKernalAuto_Tag+Src+VFri");
		
		getResult(final_round,"MutiKernalAuto_TagAvgVec+SrcAvgVec");
		getResult(final_round,"MutiKernalAuto_TagAvgVec+VFriVec");
		getResult(final_round,"MutiKernalAuto_SrcAvgVec+VFriVec");
		getResult(final_round,"MutiKernalAuto_TagAvgVec+SrcAvgVec+VFriVec");
		
		getResult(final_round,"MutiKernalAuto_TagAvgVec+VFriAvgVec");
		getResult(final_round,"MutiKernalAuto_SrcAvgVec+VFriAvgVec");
		getResult(final_round,"MutiKernalAuto_TagAvgVec+SrcAvgVec+VFriAvgVec");
		
		getResult(final_round,"MutiKernalAuto_TagAvgVec+FriVec");
		getResult(final_round,"MutiKernalAuto_SrcAvgVec+FriVec");
		getResult(final_round,"MutiKernalAuto_TagAvgVec+SrcAvgVec+FriVec");
		
		getResult(final_round,"MutiKernalAuto_TagAvgVec+FriAvgVec");
		getResult(final_round,"MutiKernalAuto_SrcAvgVec+FriAvgVec");
		getResult(final_round,"MutiKernalAuto_TagAvgVec+SrcAvgVec+FriAvgVec");
		
		FileTool.resfile = FileTool.base1 + "res_";
		FileTool.saveLog(true);
	}
	public static void getResult(int round,String pathname) throws IOException {
		double[] res = new double[2];
		FileTool.writeLog(pathname);
		for(int i=0;i<5;i++){
			double[] res_i = readLogFile(i+"", pathname);
			res[0] += res_i[0]; res[1] += res_i[1];
		}
		res[0] = res[0]/5;
		res[1] = res[1]/5;
		FileTool.writeLog("avg="+res[0]+"\\"+res[1]);
	}

	private static double[] readLogFile(String fold_i, String pathname) throws IOException {
		double[] res = new double[2];
		double Accuracy = 0;
		String macro_F = "";
		String line = "";
		BufferedReader br = new BufferedReader(new FileReader(new File(FileTool.base1+pathname+"\\"+fold_i+"\\log.txt")));
		while ((line = br.readLine())!=null) {

			//System.out.println(line);
			if(line.startsWith("End")){
				line = br.readLine();
				if(line.startsWith("Start")){continue;}
				String[] items = line.split(":|\\\\|\\s+");
				Accuracy = Double.parseDouble(items[1]);
				macro_F = items[2];				
			}
		}
		br.close();
		
		res[0] = Accuracy;
		res[1] = Float.parseFloat(macro_F);
		FileTool.writeLog(fold_i+":"+res[0]+"\\"+res[1]);
		return res;
	}
	/*public static double[] readLogFile(String fold_i, int final_round, String pathname) throws IOException {
		double[] res = new double[2];
		int round = 0;
		int validate_best_round = -1;
		double validate_macroF_best = 0;
		double validate_macroF = 0;
		double validate_Accuracy_best = 0;
		double validate_Accuracy = 0;
		
		double Accuracy = 0;
		String macro_F = "";
		String line = "";
		BufferedReader br = new BufferedReader(new FileReader(new File(FileTool.base1+pathname+"\\"+fold_i+"\\log.txt")));
		while ((line = br.readLine())!=null) {

			//System.out.println(line);
			if(line.startsWith("============== running round: "+(round+1)+" ====")){
				round = Integer.parseInt(line.split(":|\\s+")[4]);
				if(round == final_round+1) break;
			}
			else if(line.startsWith("=========== validateing round:")){
				while ((line = br.readLine())!=null) {
					if(line.startsWith("Accuracy")){
						validate_Accuracy = Double.parseDouble(line.split(":\\s+")[1]);
						line = br.readLine();
						validate_macroF = Double.parseDouble(line.split(":\\s+")[1]);
						if(validate_macroF_best<validate_macroF){
							validate_macroF_best = validate_macroF;
							validate_Accuracy_best = validate_Accuracy;
							validate_best_round = round;
						}else if(validate_macroF_best==validate_macroF&&validate_Accuracy_best<=validate_Accuracy){
							validate_macroF_best = validate_macroF;
							validate_Accuracy_best = validate_Accuracy;
							validate_best_round = round;
						}
						break;
					}
				}
			}
			else if(line.startsWith("=========== predicting round: "+(validate_best_round)+" ====")){
				while ((line = br.readLine())!=null) {
					if(line.startsWith("Accuracy")){
						Accuracy = Double.parseDouble(line.split(":\\s+")[1]);
					}else if(line.startsWith("macro-F")){
						macro_F = line.split(":\\s+")[1];
						break;
					}
				}
			}
		}
		br.close();
		
		res[0] += Accuracy*100;
		res[1] += Double.parseDouble(macro_F)*100;
		FileTool.writeLog(fold_i+":"+res[0]+"\\"+res[1]+"\t"+validate_best_round+":"+validate_Accuracy_best);
		return res;
	}*/
}
