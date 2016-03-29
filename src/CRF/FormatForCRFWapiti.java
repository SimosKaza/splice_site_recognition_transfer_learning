package CRF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import weka.core.Instances;
import Ngram.N_gram;
import Plot.Plot;
import PreProcessing.* ;
import CRF.Evaluator;

public class FormatForCRFWapiti {

	// Delimiter used in CSV file and header
	private static final String TAB_DELIMITER = "\t";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String FILE_HEADER = "ID" + TAB_DELIMITER
			+ "Chosen Entries" + TAB_DELIMITER + "Posibility label(-1)"
			+ TAB_DELIMITER + "Selection Rate of Seqeuence" + TAB_DELIMITER
			+ "Method" + TAB_DELIMITER + "Part of Sequence" + TAB_DELIMITER
			+ "Training " + TAB_DELIMITER + "Testing " + TAB_DELIMITER
			+ "Algorithm" + TAB_DELIMITER + "Truth Table" + TAB_DELIMITER
			+ "Accuracy" + TAB_DELIMITER + "F-Measure" + TAB_DELIMITER 
			+ "Truth Table" + TAB_DELIMITER + "J48_"+"Accuracy" + TAB_DELIMITER + "J48_" + "F-Measure" + TAB_DELIMITER
			+ "Truth Table" + TAB_DELIMITER + "SVM_"+"Accuracy" + TAB_DELIMITER + "SVM_" + "F-Measure"
			+ NEW_LINE_SEPARATOR;
	
	public static void main(String[] args) throws Exception {
		
					
			Properties properties = new Properties();
			properties.load(new FileInputStream("/home/simos/workspace/Master_thesis/Data/pathData.txt"));
			
			
			// getting values from property file
			String pathFinalResults = properties.getProperty("pathFinalResults");
			String pathChanceFile = properties.getProperty("DataFolder");
			
			initializeValues initVal= new initializeValues(40000,pathChanceFile);

	 		//** class gia to pios grafei
			// ********************************************************************
			// create csv for RESULTS
			// ********************************************************************
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(pathFinalResults);
			fileWriter.append(FILE_HEADER);		

			
			// ================================================================
			// create Arff for every species // ok 
			// I will use the data from the Ngram
			// ================================================================
			new CreateData(initVal.chooseInstances, initVal.PercentOflabel0).createSpeciesArchiveWithFewerData();
			
			final File DataFolder = new File(pathChanceFile);
			// read species
			ArrayList<String> SpeciesNames = new ArrayList<String>();
		    for (final File fileEntry : DataFolder.listFiles()) {
				if (!fileEntry.isDirectory()){ 
		        	String FileName = fileEntry.getName();
					SpeciesNames.add(FileName);	
				}
			}
		    
		    
		    for (int i=0;i<SpeciesNames.size();i++) {
		    	String FileName = SpeciesNames.get(i).replace(".arff", "");
		   	
				BufferedReader datafile = readDataFile(pathChanceFile+SpeciesNames.get(i));
			    // Create Instaces
				Instances data = new Instances(datafile);
				data.setClassIndex(data.numAttributes() - 1);
				
				for(int CounterMethod=0;CounterMethod<2;CounterMethod++) {	
				
					String pathSampling = pathChanceFile + initVal.MethodValidation[CounterMethod] + "/" + FileName + "/";	

					
					if ( initVal.MethodValidation[CounterMethod].equals("Random"))
						 initVal.numberOfFolds=4;
					else
						 initVal.numberOfFolds=10;
					
					// k-fold cross validation
					data.stratify( initVal.numberOfFolds);
					Instances[][] data2TrainingAndTesting = new Instances[2][ initVal.numberOfFolds];
					for (int index = 0; index <  initVal.numberOfFolds; index++) {
						data2TrainingAndTesting[0][index] = data.trainCV( initVal.numberOfFolds, index);
						data2TrainingAndTesting[1][index] = data.testCV( initVal.numberOfFolds, index);
					}
					
					OutputStreamWriter outFewlines = new OutputStreamWriter(new FileOutputStream(new File(pathSampling+"train.txt")));
					outFewlines.write(data2TrainingAndTesting[0][0].toString());
					outFewlines.close();
					outFewlines = new OutputStreamWriter(new FileOutputStream(new File(pathSampling+"test.txt")));
					outFewlines.write(data2TrainingAndTesting[1][0].toString());
					outFewlines.close();
					
		    		for(int CounterPartSeq=0;CounterPartSeq<3;CounterPartSeq++) {
		    			
		    			String TempMethodVald=  initVal.MethodValidation[CounterMethod];
		    			String TempPartOfseq=  initVal.partOfsequence[CounterPartSeq];

		    			fileWriter.append(	 initVal.LoopCounter + TAB_DELIMITER + 
		    					initVal.chooseInstances.toString() + TAB_DELIMITER +
		    					initVal.PercentOflabel0.toString() + TAB_DELIMITER +
		    					initVal.ratingSelectionSeq.toString() + TAB_DELIMITER +
		    					TempMethodVald + TAB_DELIMITER+
		    					TempPartOfseq + TAB_DELIMITER+
		    					FileName + TAB_DELIMITER+
		    					FileName + TAB_DELIMITER );
		    			 initVal.LoopCounter++;
		    			 
		    			executeCRF(fileWriter, pathSampling, TempMethodVald, TempPartOfseq);
		    			break;
		    		}break;
		    	}
		    }
		    
		    
			//read all file name - species
			ArrayList <String> allFileName = new ArrayList<String>();
		    for (final File fileEntry : DataFolder.listFiles()) {
		        if (!fileEntry.isDirectory() ) 
		        	allFileName.add(fileEntry.getName());
		     }
		    
		    
			String FolderWithData = properties.getProperty("DataFolder");
			for (int r=2; r<3;r++) {
				int n=allFileName.size();
				if (!alldata.isEmpty())
					alldata.clear();
				printCombination(allFileName, n, r);
				System.out.println(" -------------------" + r + " -------------------");
				for( int combination=0; combination < alldata.size(); combination++ ){
					System.out.println(alldata.get(combination)[0]);
					for(int PositionTestFile=0;PositionTestFile<r;PositionTestFile++){
//						System.out.println("Combination:"+combination+"  PositionTestFile:"+PositionTestFile);
						
						//put test file
						Path from;
						Path to;
						CopyOption[] options = new CopyOption[]{ StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES}; 
						from = Paths.get(FolderWithData + alldata.get(combination)[PositionTestFile]);
						to = Paths.get(FolderWithData+"Random/temp/test.txt");
						Files.copy(from, to, options);
						// *************************************************************
						//put taining file
						OutputStreamWriter outtrainingFile = new OutputStreamWriter(new FileOutputStream(new File(FolderWithData+"Random/temp/train.txt")));
						BufferedReader br = null;
	
						outtrainingFile.write("@relation "+r+"\n" +
								"@attribute sequence string\n" +
								"@attribute play {-1,1}\n" +
								"@data\n");
						
						for (int k=0;k<r; k++ ){
							if (k==PositionTestFile)
								continue;
							// Read file	
							br = new BufferedReader(new FileReader(FolderWithData + alldata.get(combination)[k]), 400);
							String sCurrentLine;		
		
							while ( (sCurrentLine = br.readLine()) != null  )   {
									if (!sCurrentLine.contains("@")  )
										outtrainingFile.write(sCurrentLine+"\n");				
							}
							
							br.close();
							
						}
						br.close();
						outtrainingFile.close();
						
												
						// *************************************************************
						String TempMethodVald= initVal.MethodValidation[0];
						String TempPartOfseq= initVal.partOfsequence[0];
						String pathSampling = pathChanceFile + TempMethodVald + "/temp/";
						String TrainName="";
						String TestName  = alldata.get(combination)[PositionTestFile];
						for (int k=0;k<r; k++ ){
							if (k==PositionTestFile)
								continue;
							TrainName+=alldata.get(combination)[k]+"#";
						}
//						System.out.println("Training:"+TrainName+"\nTesting:"+TestName);
	
						fileWriter.append(	initVal.LoopCounter + TAB_DELIMITER + 
								initVal.chooseInstances.toString() + TAB_DELIMITER +
								initVal.PercentOflabel0.toString() + TAB_DELIMITER +
								initVal.ratingSelectionSeq.toString() + TAB_DELIMITER +
								TempMethodVald + TAB_DELIMITER+
								TempPartOfseq + TAB_DELIMITER+
								TrainName.replace(".fasta", "").replace(".arff", "") + TAB_DELIMITER+
								TestName.replace(".fasta", "").replace(".arff", "") + TAB_DELIMITER );
						
						initVal.LoopCounter++;
						executeCRF(fileWriter,pathSampling, TempMethodVald, TempPartOfseq);
		    			
					}
				}
			}
		    
			fileWriter.flush();
			fileWriter.close();
			
			
			
			int k=1;
			for(int i=11;i<11+(k*4);i=i+4){
				Plot pl = new Plot(pathFinalResults,i);
				pl.plot();
			}
			
	}

	public static void executeCRF(FileWriter fileWriter, String pathSampling,
    							String MethodValidation, String TempPartOfseq) 
    										               throws IOException {
		fileWriter.append("CRF" + "\t");
		// ********************************************************************
		// Open property file for paths
		// ********************************************************************
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("/home/simos/workspace/Master_thesis/Data/pathData.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		String pathWapiti = properties.getProperty("pathWapiti");
		String pathRunWapiti = properties.getProperty("pathRunWapiti");
		String pathCrfResult = properties.getProperty("pathCrfResult");
		
		
		switch (MethodValidation) {
		case "Random": {
			// ********************************************************************
			// Format file in a new text for the CRF of wapiti
			// ********************************************************************
			// **** for training data
			convertFasta2Wapiti(pathSampling + "train.txt", pathWapiti
					+ "train.txt", TempPartOfseq);
			// **** for testing data
			convertFasta2Wapiti(pathSampling + "test.txt", pathWapiti
					+ "test.txt", TempPartOfseq);


			// ********************************************************************
			// Run Bash shell
			// ********************************************************************
			String command = pathRunWapiti;
			Process p = Runtime.getRuntime().exec(command);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// ********************************************************************
			// Accuracy of CRF truth table
			// simplify the positive and the negative of the sequence are been counted and thus the class is decided				
			// ********************************************************************
			Evaluator eval = new Evaluator();
			eval.evaluate(pathCrfResult);
			
			fileWriter.append(eval.truth_table[0][0].toString() 
					+ "   -    "
					+ eval.truth_table[0][1].toString() 
					+ " --- "
					+ eval.truth_table[1][0].toString() 
					+ "   -    "
					+ eval.truth_table[1][1].toString() + "\t");
			Double dd = eval.getAccuracy() * 100;
//			fileWriter.append(String.format( "%.4f", eval.getPrecisionPos() ) + "\t");
//			fileWriter.append(String.format( "%.4f", eval.getRecallPos() )+ "\t");
			fileWriter.append(String.format( "%.4f",dd) + "%\t");
			fileWriter.append(String.format( "%.4f", eval.getFMeasure() ) + "\n");

			break;
		}
		case "kFold": {
			// ********************************************************************
			// case 2 - 10 fold cross validation
			final int FOLDS_NO = 10; // Number of N-fold cross validation
			double FoldAccuracy = 0.0;

			for (int iFold = 0; iFold < FOLDS_NO; iFold++) {

				// ********************************************************************
				// Format file in a new text for the CRF of wapiti
				// ********************************************************************
				// **** for training data
				convertFasta2Wapiti(pathSampling +iFold+"_train.txt", pathWapiti+ "train.txt",TempPartOfseq);
				// **** for testing data
				convertFasta2Wapiti(pathSampling +iFold+"_test.txt", pathWapiti+ "test.txt",TempPartOfseq);

				// ********************************************************************
				// Run Bash shell
				// ********************************************************************
				String command = pathRunWapiti;
				Process p = Runtime.getRuntime().exec(command);
				
				try {
					p.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// ********************************************************************
				// Accuracy of CRF truth table 
				// simplify the positive and the negative of the sequence are been counted and thus the class is decided				
				// ********************************************************************
				Evaluator eval = new Evaluator();
				eval.evaluate(pathCrfResult);
				FoldAccuracy = FoldAccuracy + eval.getAccuracy();

				fileWriter.append(eval.truth_table[0][0].toString() 
								+ "   -    "
								+ eval.truth_table[0][1].toString() 
								+ " --- "
								+ eval.truth_table[1][0].toString()
								+ "   -    "
								+ eval.truth_table[1][1].toString() + "\t");
				Double dd = eval.getAccuracy() * 100;
				fileWriter.append(String.format( "%.4f", eval.getPrecisionPos() ) + "\t");
				fileWriter.append(String.format( "%.4f", eval.getRecallPos() )+ "\t");
				fileWriter.append(String.format( "%.4f", eval.getFMeasure() ) + "\t");
				fileWriter.append(String.format( "%.4f",dd) + "%\n\t\t\t\t\t\t\t\t\t");

				
				
				
			}// end fold
			Double dd = (FoldAccuracy / (double) FOLDS_NO) * 100;
			fileWriter.append("\t\t\t\t total=" +String.format( "%.4f",dd) + "%\n");
			break;
		}
		default:
			break;
		}

	}
    
	public static void convertFasta2Wapiti(String pathReader, String pathWriter, String TempPartOfseq) {
		// Read file - write file
		BufferedReader br = ReadWrite.readDataFile(pathReader);
		OutputStreamWriter outputWriter = ReadWrite.writeDataFile(pathWriter);

		String CurrentLine;
		String[] splitLine = null;
		int start,end;
		try {
			
			//eat first lines 
			CurrentLine = br.readLine();
			while (!CurrentLine.contains("@data")) {
				CurrentLine = br.readLine();
			}
			
			while (((CurrentLine = br.readLine()) != null)) {
					CurrentLine.replaceAll(" ", "");
					splitLine = CurrentLine.split(",");

					if ("all".equals(TempPartOfseq)){
						start=0;
						end=splitLine[0].length();
					}
					else if ("left".equals(TempPartOfseq)){
						start=0;
						end=splitLine[0].length()/2;
					}
					else{
						start=splitLine[0].length()/2+1;
						end=splitLine[0].length();
					}
					
					String str=splitLine[0];
//					str = str.substring(str.length()/2 - 150, str.length()/2  +4 );
					
					char[] ch=splitLine[0].toCharArray();
//					System.out.println("*** -> "+ch.length);
					for (int i = 0; i < str.length(); i++) {
						outputWriter.write((ch[i] + "\t"	+ splitLine[1] + "\n").replace("\t\t", "\t"));
					}
					outputWriter.write("\n");
				
			}
			br.close();
			outputWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// arr[] ---> Input Array
	// data[] ---> Temporary array for storing
	// start & end ---> Staring and Ending indexes in arr[]
	// index ---> Current index in data[]
	// r ---> Size of a combination to be printed
	static Vector<String[]> alldata = new Vector<>();

	static void printCombination(ArrayList<String> arr, int n, int r) {
		String[] data = new String[r];
		combinationUtil(arr, data, 0, n - 1, 0, r);
	}

	static void combinationUtil(ArrayList<String> arr, String data[],
				int start, int end, int index, int r) {

			if (index == r) {
				String[] data1 = new String[r];
				for (int j = 0; j < r; j++) {
					//System.out.printf("%s ", data[j]);
					data1[j]=data[j];
				}
				//System.out.printf("\n");
				alldata.add(data1);
				return;
			}

			// end-i+1 >= r-index --- makes sure that including one element
			for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
				data[index] = arr.get(i);
				combinationUtil(arr, data, i + 1, end, index + 1, r);
			}

		}
		
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
}
