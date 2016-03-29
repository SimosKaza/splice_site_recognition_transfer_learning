package MainExecute;

import java.io.FileWriter;
import java.io.IOException;

public class FileResults {
	// Delimiter used in CSV file and header
	public static String TAB_DELIMITER = "\t";
	public String NEW_LINE_SEPARATOR = "\n";
	private final String FILE_HEADER = "ID" + TAB_DELIMITER
			+ "Chosen Entries" + TAB_DELIMITER 
			+ "Posibility label(-1)" + TAB_DELIMITER 
			+ "Selection Rate of Seqeuence" + TAB_DELIMITER
			+ "Method" + TAB_DELIMITER 
			+ "Part of Sequence" + TAB_DELIMITER
			+ "Training " + TAB_DELIMITER 
			+ "Testing " + TAB_DELIMITER
			+ "Algorithm" + TAB_DELIMITER  
			+ "Truth Table" + TAB_DELIMITER + "J48_"+"Accuracy" + TAB_DELIMITER + "J48_" + "F-Measure" + TAB_DELIMITER+ "auPRC" + TAB_DELIMITER
			+ "Truth Table" + TAB_DELIMITER + "SVM_"+"Accuracy" + TAB_DELIMITER + "SVM_" + "F-Measure" + TAB_DELIMITER+ "auPRC"
			+ NEW_LINE_SEPARATOR;
	static FileWriter fileWriter = null;

	public FileResults(String pathFinalResults) {
		try {
			fileWriter = new FileWriter(pathFinalResults);
			fileWriter.append(FILE_HEADER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void appendFileWriter(String str) {
		try {
			fileWriter.append(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
	
	public void closeStream(){
		try {
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
