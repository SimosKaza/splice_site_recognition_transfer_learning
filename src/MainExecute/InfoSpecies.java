package MainExecute;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramSymWinGraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import weka.core.Instances;

public class InfoSpecies extends Thread{

	public String Name;
	String path;
	Instances data;
	public Instances dataSource;
	public Instances dataTarget;
	public TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab0 = null;
	public TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab1 = null;
	public Instances dataforTraining;

	public InfoSpecies(String Name, String path) {
		this.Name=Name;
		this.path=path;	
	}

	public void run() {
		BufferedReader datafile = readDataFile(path);		
		try {
			data = new Instances(datafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		data.setClassIndex(data.numAttributes() - 1);

		int numberOfFolds;
		// cross validation
		numberOfFolds=2;
		this.data.stratify(numberOfFolds);
		this.dataSource = data.trainCV(numberOfFolds, 0);
		this.dataTarget = data.testCV(numberOfFolds, 0);
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
