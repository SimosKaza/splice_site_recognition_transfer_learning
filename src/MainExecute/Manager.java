package MainExecute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import Plot.Plot;
import PreProcessing.*;
import CRF.*;
import Ngram.*;
import Weka.*;

public class Manager {
	
	static Vector<String[]> alldata = new Vector<>();

	public static void main(String[] args) throws Exception {
		
		Properties properties = new Properties();
		properties.load(new FileInputStream("/home/simos/workspace/Master_thesis/Data/pathData.txt"));
		
		// getting values from property file
		String pathFinalResults = properties.getProperty("pathFinalResults");
		String pathChanceFile = properties.getProperty("DataFolder");
		
		// ********************************************************************
		// initialization
		// ********************************************************************
		initializeValues initVal= new initializeValues(2500,pathChanceFile);
		
		// ********************************************************************
		// create csv for RESULTS
		// ********************************************************************
		Random rRand = new Random();
		int number =rRand.nextInt(9)+1;
		attribute attr = new attribute(); 
		attr.Setvalues();
		pathFinalResults =pathFinalResults.replace(".csv", "") + number
							+"_Instances:"+initVal.chooseInstances
							+"_PercentOflabel0:"+initVal.PercentOflabel0
							+"_PercentOfTrainingForGraphs:"+initVal.dPercentOfTrainingForGraphs
//							+"_Attribute:_"+ attr.getAttributeName().substring(0, 100)
							+".csv";
		FileResults fileResults = new FileResults(pathFinalResults);

		// ====================================================================
		// create Arff for every species // ok 
		// if there aren't enough entries,I should create a new one
		// ====================================================================
		// ********************************************************************
		new CreateData(2*initVal.chooseInstances, initVal.PercentOflabel0).createSpeciesArchiveWithFewerData();

		final File DataFolder = new File(pathChanceFile);
		// read species
		ArrayList<String> SpeciesNames = new ArrayList<String>();
	    for (final File fileEntry : DataFolder.listFiles()) {
			if (!fileEntry.isDirectory()){ 
	        	String FileName = fileEntry.getName();
				SpeciesNames.add(FileName);	
			}
		}
	    
	    // Read Instancies and put it into the vector
	    Vector<InfoSpecies>  infoSpecies =  new Vector<InfoSpecies> ();
	    for (int i=0;i<SpeciesNames.size();i++) {
				InfoSpecies tempInfoSp = new InfoSpecies(SpeciesNames.get(i),pathChanceFile+SpeciesNames.get(i));
				infoSpecies.add(tempInfoSp);
				tempInfoSp.start();
	    }
	    for(InfoSpecies st:infoSpecies){
            try{
                 st.join();
            } catch (Exception ex){}
        }
	    
	    
        List<SpeciesPerThread> list = new ArrayList<SpeciesPerThread>();    
	    int numberOfThread=6;
	    int indexNmThread=0;
	    // Execute between them
		for (int r=1; r<3;r++) { // target domain's combinations 
			int n=SpeciesNames.size();
			if (!alldata.isEmpty())
				alldata.clear();
			printCombination(SpeciesNames, n, r);
			System.out.println(" ------------------- " + r + " -------------------");
			for( int combination=0; combination < alldata.size(); combination++ ){ // we have all the combination
				System.out.println(alldata.get(combination)[0]);
				// everyone must be passed from the target domain position
				for(int PositionTestFile=0;PositionTestFile<r;PositionTestFile++){ 
					indexNmThread++;
					
					String TrainName="";
					String TestName  = alldata.get(combination)[PositionTestFile];
					for (int k=0;k<r; k++ ){
						if (k==PositionTestFile & r!=1)
							continue;
						TrainName+=alldata.get(combination)[k]+"#";
					}

					SpeciesPerThread spThread = new SpeciesPerThread(initVal,fileResults, infoSpecies,
											TrainName.replace(".arff", ""),TestName.replace(".arff", ""));
					spThread.start();
					list.add(spThread);
				} 
				if (indexNmThread>=numberOfThread || combination == alldata.size()-1 ){
					for(SpeciesPerThread st:list){
						try{
							st.join();
//							System.out.println(st.getId());		 
						} catch (Exception ex){}
					}
//					list.clear();
					indexNmThread=0;
				}
			}
		}
		
		fileResults.closeStream();
		
		int k=8;
		for(int i=11;i<11+(k*4);i=i+4){
			Plot pl = new Plot(pathFinalResults,i);
			pl.plot();
		}


	}

	// arr[] ---> Input Array
	// data[] ---> Temporary array for storing
	// start & end ---> Staring and Ending indexes in arr[]
	// index ---> Current index in data[]
	// r ---> Size of a combination to be printed
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
	
	public BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}

}
