package PreProcessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class GreateTrainingTestingData {
	
	public static void diverseDataRandomSampling(String pathChosenData,
			String pathSamplingTrain, String pathSamplingTest,
			Double randSampling) {
		// Read file
		BufferedReader br_chosenData = ReadWrite.readDataFile(pathChosenData);
		// write file
		OutputStreamWriter outTraining = ReadWrite.writeDataFile(pathSamplingTrain);
		OutputStreamWriter outTesting = ReadWrite.writeDataFile(pathSamplingTest);

		String CurrentLine;
		Random rRand = new Random();

		try {
			while (((CurrentLine = br_chosenData.readLine()) != null)) {
				if (CurrentLine.contains(">")) {
					if (rRand.nextDouble() < randSampling) {
						outTraining.write(CurrentLine + "\n");
						CurrentLine = br_chosenData.readLine();
						outTraining.write(CurrentLine + "\n");
					} else {
						outTesting.write(CurrentLine + "\n");
						CurrentLine = br_chosenData.readLine();
						outTesting.write(CurrentLine + "\n");
					}
				}
			}
			// close streams
			br_chosenData.close();
			outTraining.close();
			outTesting.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void diverseDataKFoldCrossValidation(String pathChosenData,
			String pathSamplingTrain, String pathSamplingTest,
			Integer FOLDS_NO, Integer iFold) {

		// Read file
		BufferedReader br_chosenData = ReadWrite.readDataFile(pathChosenData);
		// write file
		OutputStreamWriter outTraining = ReadWrite.writeDataFile(pathSamplingTrain);
		OutputStreamWriter outTesting = ReadWrite.writeDataFile(pathSamplingTest);
		
		int countEntries = 0;
		String CurrentLine;
		Integer temp_iFold= iFold + 1;

		try {
			while (((CurrentLine = br_chosenData.readLine()) != null)) {

				countEntries++;

				if (CurrentLine.contains(">")) {
					//if (countEntries >= iFirstTestPos & countEntries < iLastTestPos) {
					if (countEntries == temp_iFold ) {
						outTesting.write(CurrentLine + "\n");
						CurrentLine = br_chosenData.readLine();
						outTesting.write(CurrentLine + "\n");
						temp_iFold=temp_iFold + FOLDS_NO;
					} else {
						outTraining.write(CurrentLine + "\n");
						CurrentLine = br_chosenData.readLine();
						outTraining.write(CurrentLine + "\n");
					}
				}
			}
			// close streams
			br_chosenData.close();
			outTraining.close();
			outTesting.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
