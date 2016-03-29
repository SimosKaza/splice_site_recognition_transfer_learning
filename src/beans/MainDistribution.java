package beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFrame;

import weka.core.Instances;

public class MainDistribution {

	public static void main(String[] args) throws IOException, InterruptedException { //

		String pathChanceFile = "/home/simos/workspace/Master_thesis/Data/common/data/";
		final File DataFolder = new File(pathChanceFile);

		// read species
		ArrayList<String> SpeciesNames = new ArrayList<String>();
		for (final File fileEntry : DataFolder.listFiles()) {
			if (!fileEntry.isDirectory()){ 
				String FileName = fileEntry.getName();
				SpeciesNames.add(FileName);	
				System.out.println(FileName);
			}
		}
		Vector<Double> VectorPOS = new Vector<Double>();
		Vector<Double> VectorNEG = new Vector<Double>();


		for (int i=1;i<SpeciesNames.size();i++) {   	
			BufferedReader datafile = readDataFile(pathChanceFile+SpeciesNames.get(i));
			// Create Instaces
			Instances data = new Instances(datafile);
			data.setClassIndex(data.numAttributes() - 1);
			
			double value=0.0;
			String str="";
			
			for (int j=0;j<data.numInstances();j++){
				str=data.get(j).stringValue(0);
//				BranchSite branchSiteInfo = new BranchSite();
//				branchSiteInfo.BranchSite_position_and_score(str);
//				value=branchSiteInfo.getScore();
				motif_acceptor_position acceptor =new motif_acceptor_position();
				acceptor.Score(str);
				value=acceptor.getScore();
				if (data.get(j).stringValue(1).equals("1") )
					VectorPOS.add(value);
				else 
					VectorNEG.add(value);
			}


			double max=VectorPOS.get(0);
			double min=VectorPOS.get(0);
			Vector<Double> VectorBins = new Vector<Double>();

			double[] POS = new double[VectorPOS.size()];
			for(int j=0;j<VectorPOS.size();j++){
				POS[j] = VectorPOS.get(j);
				if (max<POS[j])
					max=POS[j];
				if(min>POS[j])
					min=POS[j];
				if(!VectorBins.contains(POS[j])){
					VectorBins.add(POS[j]);
				}
			}

			double[] NEG = new double[VectorPOS.size()];
			for(int j=0;j<VectorNEG.size() & j<VectorPOS.size();j++){
				NEG[j] = VectorNEG.get(j);
				if (max<NEG[j])
					max=NEG[j];
				if(min>NEG[j])
					min=NEG[j];
				if(!VectorBins.contains(NEG[j])){
					VectorBins.add(NEG[j]);
				}
			}
			System.out.println(POS.length);
			System.out.println(NEG.length);

			System.out.println(VectorBins.size());
			Histogram frame = new Histogram(POS,NEG,min,max,VectorBins.size());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setBounds(10, 10, 600, 600);
			frame.setTitle("Diststribution Negative Positive");
			frame.setVisible(true);

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
