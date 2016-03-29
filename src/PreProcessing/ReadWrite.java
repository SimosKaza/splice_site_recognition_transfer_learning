package PreProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

public class ReadWrite {
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return inputReader;
	}
	
	public static OutputStreamWriter writeDataFile(String filename) {

		OutputStreamWriter outputWriter = null;
		try {
			outputWriter = new OutputStreamWriter(new FileOutputStream(
					new File(filename)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return outputWriter;
	}
}
