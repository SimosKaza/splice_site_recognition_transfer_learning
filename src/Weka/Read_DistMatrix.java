package Weka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Read_DistMatrix {
	
	private BufferedReader br;
	public Read_DistMatrix() {
		try {
			br = new BufferedReader(new FileReader("/home/simos/workspace/Master_thesis/Data/distMatrix.txt"), 500);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Vector<Double> readDistMatrix(String testName, String trainingNam) throws IOException {
//		System.out.println(testName+"****"+trainingNam);

		
		String[]  trainingNames = trainingNam.split("#");		
		String CurrentLine;
		Vector <String> speciesNames = new Vector <String>();
		String[] TestLineWeight = null;
 
		while ( (CurrentLine = br.readLine()) != null  )   {
			speciesNames.add(CurrentLine.split("\t")[0]);
			if (CurrentLine.split("\t")[0].equals(testName)){
				TestLineWeight=CurrentLine.split("\t");
			}
		}
		br.close();
		
		Vector<Double> Weight = new Vector<Double>();
		double Normalize=1;
		for(int i=0;i<trainingNames.length;i++){
			Weight.add(Double.parseDouble(TestLineWeight[speciesNames.indexOf(trainingNames[i])+1])/Normalize);
		}
		return Weight;
		
	}
	public Vector<Double> readHoppes(String testName, String trainingNam){
		String[]  trainingNames = trainingNam.split("#");		

		
		Vector <String> speciesNames = new Vector <String>();
		Vector <Double> countplus = new Vector <Double>();

		speciesNames.add("H_sapiens_acc");
		countplus.add(7.0);
		speciesNames.add("D_rerio_acc");
		countplus.add(7.0);
		speciesNames.add("C_elegans_acc");
		countplus.add(5.0);
		speciesNames.add("D_melanogaster_acc");
		countplus.add(3.0);
		speciesNames.add("A_thaliana_acc");
		countplus.add(2.0);
		
		
		Vector<Double> Weight = new Vector<Double>();
		for(int i=0;i<trainingNames.length;i++){
			Weight.add((100-10*Math.abs(countplus.get(speciesNames.indexOf(trainingNames[i]))-countplus.get(speciesNames.indexOf(testName))))/100);
		}
		return Weight;
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
	
	public static void main(String[] args) throws IOException{
		Read_DistMatrix ClassWeight =  new Read_DistMatrix();
		System.out.println(ClassWeight.readDistMatrix("H_sapiens_acc","D_rerio_acc#D_melanogaster_acc#").get(1));
		
	}


}

