package Plot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Plot {
	static String path="";
	static int j84=11;
	static int svm=15;
	static int ROC=j84+1;
	static int position = j84;  

	public Plot(String path) {
		Plot.path=path;
	}
	
	public Plot(String path,int p) {
		Plot.path=path;
		position=p;
	}

	public void plot() throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] ValueLabel;
		String CurrentLine;
		String[][] array = new String[5][100];    
		Vector <String> SpeciesGroups = new Vector <String>();
		SpeciesGroups.add("H_sapiens_acc#");SpeciesGroups.add("D_rerio_acc#");SpeciesGroups.add("D_melanogaster_acc#");SpeciesGroups.add("C_elegans_acc#");SpeciesGroups.add("A_thaliana_acc#");
//		SpeciesGroups.add("H_sapiens_acc#D_rerio_acc#");SpeciesGroups.add("H_sapiens_acc#D_melanogaster_acc#");	SpeciesGroups.add("H_sapiens_acc#C_elegans_acc#");SpeciesGroups.add("H_sapiens_acc#A_thaliana_acc#");SpeciesGroups.add("D_melanogaster_acc#D_rerio_acc#");SpeciesGroups.add("D_melanogaster_acc#C_elegans_acc#");SpeciesGroups.add("D_melanogaster_acc#A_thaliana_acc#");SpeciesGroups.add("C_elegans_acc#D_rerio_acc#");SpeciesGroups.add("C_elegans_acc#A_thaliana_acc#");SpeciesGroups.add("A_thaliana_acc#D_rerio_acc#");
//		SpeciesGroups.add("H_sapiens_acc#D_melanogaster_acc#D_rerio_acc#");	SpeciesGroups.add("H_sapiens_acc#D_melanogaster_acc#C_elegans_acc#");SpeciesGroups.add("H_sapiens_acc#D_melanogaster_acc#A_thaliana_acc#");SpeciesGroups.add("H_sapiens_acc#C_elegans_acc#D_rerio_acc#");SpeciesGroups.add("H_sapiens_acc#C_elegans_acc#A_thaliana_acc#");SpeciesGroups.add("H_sapiens_acc#A_thaliana_acc#D_rerio_acc#");SpeciesGroups.add("D_melanogaster_acc#C_elegans_acc#D_rerio_acc#");SpeciesGroups.add("D_melanogaster_acc#C_elegans_acc#A_thaliana_acc#");SpeciesGroups.add("D_melanogaster_acc#A_thaliana_acc#D_rerio_acc#");SpeciesGroups.add("C_elegans_acc#A_thaliana_acc#D_rerio_acc#");
		
		Vector <String> Species = new Vector <String>();
		
		Species.add("H_sapiens_acc");Species.add("D_rerio_acc");Species.add("D_melanogaster_acc");Species.add("C_elegans_acc");Species.add("A_thaliana_acc");
		
	
		while ( ((CurrentLine = br.readLine()) != null)) {
			if (CurrentLine.contains("Random") & CurrentLine.contains("all") ){
				ValueLabel=CurrentLine.split("\t");
				if (! CurrentLine.contains("#"))
					ValueLabel[6]=ValueLabel[6]+"#";	
				if (Species.indexOf(ValueLabel[7])==-1)
					Species.add(ValueLabel[7]);
				if (SpeciesGroups.indexOf(ValueLabel[6])==-1)
					SpeciesGroups.add(ValueLabel[6]);			
				array[Species.indexOf(ValueLabel[7])][SpeciesGroups.indexOf(ValueLabel[6])]=ValueLabel[position];
			}
		}
		String categories="  ";
		for(int i=0;i<SpeciesGroups.size();i++){
			categories+=SpeciesGroups.elementAt(i)+", ";
		}
		System.out.println(categories.replaceAll("D_rerio_acc#", "Rer").replaceAll("H_sapiens_acc#", "Sap").replaceAll("C_elegans_acc#", "Ele").replaceAll("D_melanogaster_acc#", "Mel").replaceAll("A_thaliana_acc#", "Thl"));
		
		String sapiens="",elegan="",rerio="",mela="",thaliana="";
		for(int i=0;i<SpeciesGroups.size();i++){
			sapiens+= String.format(" %.2f",getDouble(array[Species.indexOf("H_sapiens_acc")][i]))+", ";
			elegan+= String.format(" %.2f",getDouble(array[Species.indexOf("C_elegans_acc")][i]))+", ";
			rerio+= String.format(" %.2f",getDouble(array[Species.indexOf("D_rerio_acc")][i]))+", ";
			mela+= String.format(" %.2f",getDouble(array[Species.indexOf("D_melanogaster_acc")][i]))+", ";
			thaliana+= String.format(" %.2f",getDouble(array[Species.indexOf("A_thaliana_acc")][i]))+", ";
		}
		
		double MOsapiens=0.0,MOelegan=0.0,MOrerio=0.0,MOmela=0.0,MOthaliana=0.0;
		for(int i=0;i<SpeciesGroups.size();i++){
			MOsapiens+=getDouble(array[Species.indexOf("H_sapiens_acc")][i]);
			MOelegan+=getDouble(array[Species.indexOf("C_elegans_acc")][i]);
			MOrerio+=getDouble(array[Species.indexOf("D_rerio_acc")][i]);
			MOmela+=getDouble(array[Species.indexOf("D_melanogaster_acc")][i]);
			MOthaliana+=getDouble(array[Species.indexOf("A_thaliana_acc")][i]);
		}
		
		int amount = SpeciesGroups.size()-sapiens.split("0.00").length +1 ;
		System.out.println(sapiens+"  ->"+String.format("%.2f",MOsapiens/amount));
		System.out.println(rerio+"  ->"+String.format("%.2f",MOrerio/amount));
		System.out.println(mela+"  ->"+String.format("%.2f",MOmela/amount));
		System.out.println(elegan+"  ->"+String.format("%.2f",MOelegan/amount));
		System.out.println(thaliana+"  ->"+String.format("%.2f",MOthaliana/amount));	
		System.out.println();		

	}
	
	public double getDouble(String str){
		
		if (str==null)
			return 0.0;
		else
			return Double.parseDouble(str);
	}

	public static void main(String[] args) throws IOException{
		
		String pathResult = "/home/simos/workspace/Master_thesis/Data/common/resultFolder/";
		String path;
		
		int k=8;
		for(int i=position;i<11+(k*4);i=i+4){
//			Plot pl = new Plot("/home/simos/workspace/Master_thesis/Data/common/Ngram_multiTask_FinalResults9_Instances:6500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.7.csv");
			Plot pl = new Plot("/home/simos/workspace/Master_thesis/Data/common/Ngram_FinalResults_Instances:40000_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.7.csv");
//			Plot pl = new Plot("/home/simos/workspace/Master_thesis/Data/common/crf_16000_FinalResults.csv");
			position=i;
			pl.plot();
		}
		System.exit(-1);
		
		
		path=("FinalResults7_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive.csv");
		plotformat(pathResult,path,"");
		
		System.out.println("\n**************************************************************************\n");
		System.out.println("\n\n------------------------------Rich ACTG---------------------------------");
		path=("FinalResults4_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine.csv");	
		plotformat(pathResult,path,"count in the whole sequence");
	
		path=("FinalResults2_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine.csv");	
		plotformat(pathResult,path,"count the rich region");

//		path=("FinalResults8_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_distrGuanineAdenine_distrCytosineThymine.csv");	
//		plotformat(pathResult,path,"count the rich region");

		System.out.println("\n**************************************************************************\n");
		System.out.println("\n\n-----------------------BranchSiteScore-----------------------------------");
	
		path="FinalResults3_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_BranchSiteScore.csv";	
		plotformat(pathResult,path,"Score=\"psa1.getScore()\"");
	
		
//		path="FinalResults8_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_BranchSiteScore.csv";	
//		plotformat(pathResult,path,"Score=\"Math.log(psa1.getScore()/psa.getIndexInTargetAt(1))\"");
//
//		path="FinalResults7_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_BranchSiteScore.csv";	
//		plotformat(pathResult,path,"Score=\"Math.log((psa1.getSimilarity()/psa.getIndexInTargetAt(1)))+psa1.getScore()\"");
//		
		System.out.println("\n**************************************************************************\n");
		System.out.println("\n\n-----------------------Motif_Acceptor-----------------------------------");
		
		path="FinalResults8_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_Motif_Acceptor.csv";
		plotformat(pathResult,path,"");

//		path="FinalResults9_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_Motif_Acceptor.csv";
//		plotformat(pathResult,path,"\"Math.log(psa.getScore()/psa.getComputationTime()\"");

		
		System.out.println("\n**************************************************************************\n");
		System.out.println("\n\n----------------BranchSiteScore + Motif_Acceptor-------------------------");
		
		path=("FinalResults8_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_BranchSiteScore_Motif_Acceptor.csv");	
		plotformat(pathResult,path,"");
		
//		path="FinalResults9_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_BranchSiteScore_Motif_Acceptor.csv";
//		plotformat(pathResult,path,"Score=\"Math.log(psa1.getScore()/psa.getIndexInTargetAt(1))\"");
//		
//		path="FinalResults7_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_BranchSiteScore_Motif_Acceptor.csv";
//		plotformat(pathResult,path,"Score=\"Math.log((psa1.getSimilarity()/psa.getIndexInTargetAt(1)))+psa1.getScore()\"");


		System.out.println("\n**************************************************************************\n");
		System.out.println("\n\n-------------------------- all ---------------------------------------");
	
		path="FinalResults3_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"");
		
		path="FinalResults2_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"different data for self-training");
	
		path="ngram/FinalResults3_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative0_positive0_distrAdenine_distrGuanine_distrThymine_distrCytosine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"NGram Min:3 Max:6 Dist:3");
	
		path="ngram/FinalResults7_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative0_positive0_distrAdenine_distrGuanine_distrThymine_distrCytosine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"NGram Min:3 Max:5 Dist:5");
	
		
		System.out.println("\n**************************************************************************\n");
		System.out.println("\n\n-------------------------- all multiple source ---------------------------------------");
		System.out.println("------------------------- merge Graphs ---------------------------------------");
		path="FinalResults661_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"no weight");
		
		path="FinalResults662weight_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"with weight");
		
		path="FinalResults672NormalizeWeight_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative0_positive0_distrAdenine_distrGuanine_distrThymine_distrCytosine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"with normalize weight");
		
		System.out.println("\n\n-------------------------  feature per class ---------------------------------------");
		path="FinalResults771_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative0_positive0_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine_BranchSiteScore_Motif_Acceptor.csv";	
		plotformat(pathResult,path,"no weight");

		path="FinalResults772NormalizeWeight_Instances:2500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative0_positive0_negative_positive_distrGuanine_distrCytosine_distrThymine_distrAdenine_BranchSiteScore_Motif_Acceptor.csv";
		plotformat(pathResult,path,"with normalize weight");
		
		path="FinalResults7_Instances:6500_PercentOflabel0:0.7_PercentOfTrainingForGraphs:0.6_Attribute:_negative0_positive0_distrAdenine_distrGuanine_distrThymine_distrCytosine_BranchSiteScore_Motif_Acceptor.csv";
		plotformat(pathResult,path,"with normalize weight--- 6500 dataset");
	
	}
	
		
	static void plotformat(String pathResult,String path,String extraInfo) throws IOException{
		System.out.println(path);
		if (!extraInfo.isEmpty())
			System.out.println(extraInfo);
		Plot pl = new Plot(pathResult+path);
		pl.plot();
	}
}
