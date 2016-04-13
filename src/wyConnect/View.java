package wyConnect;


public class View {
	public String embeddingFile;
	public int embeddingLength;
	public String trainFile;
	public String testFile;
	public String validateFile;
	public View(String embeddingFile,
	           int embeddingLength,
	           String trainFile,
	           String testFile,
	           String validateFile){
		this.embeddingFile=embeddingFile;
		this.embeddingLength=embeddingLength;
		this.trainFile=trainFile;
		this.testFile=testFile;
		this.validateFile=validateFile;
	}
}
