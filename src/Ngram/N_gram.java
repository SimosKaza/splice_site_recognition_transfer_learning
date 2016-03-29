package Ngram;

import gr.demokritos.iit.conceptualIndex.structs.Distribution;
import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramSymWinGraph;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import gr.demokritos.iit.jinsect.structs.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.SubstitutionMatrixHelper;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.template.PairwiseSequenceAligner;
import org.biojava.nbio.alignment.template.Profile;
import org.biojava.nbio.alignment.template.SequencePair;
import org.biojava.nbio.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.template.Compound;

import beans.BranchSite;
import beans.RichPyrimidine;
import beans.motif_acceptor_position;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import MainExecute.Arguments;
import MainExecute.InfoSpecies;
import Weka.weka;

public class N_gram {
 
	//4 6 2 === 80+% 
	static int MIN_N = 3;
	static int MAX_N = 4;
	static int DIST  = 3;

    public void executeNgram(Arguments classArguments, Vector<InfoSpecies>  infoSpecies) throws Exception {
    	
    	double dPercentOfTrainingForGraphs = classArguments.dPercentOfTrainingForGraphs;
    	String TrainingName=classArguments.TrainName;
		String TestName=classArguments.TestName;
		
		classArguments.appendTempResult("NGram"+" Min:"+ MIN_N +" Max:"+ MAX_N +" Dist:"+ DIST + "_%TrnGrph:" + dPercentOfTrainingForGraphs + "\t");

		int indexTrainingName= getIndexVector(infoSpecies, TrainingName);
		int indexTestName=-1;
		
		//putting instances (data2TrainingAndTesting) 
		Instances[][] data2TrainingAndTesting = new Instances[2][1];
		if (TrainingName.equals(TestName+"#")){ // case domain - target are same
			data2TrainingAndTesting[0][0] = infoSpecies.get(indexTrainingName).dataSource; //training - Source
			data2TrainingAndTesting[1][0] = infoSpecies.get(indexTrainingName).dataTarget; //testing - target
		}
		else{ // case domain - target are different
			indexTestName=getIndexVector(infoSpecies, TestName);
			data2TrainingAndTesting[1][0] = infoSpecies.get(indexTestName).dataTarget;//testing - Source
		}
		
		// Init class graph map
		TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab0 = new TreeMap<String, DocumentNGramSymWinGraph>();
		TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab1 = new TreeMap<String, DocumentNGramSymWinGraph>();
		// training and test set
		HashSet<Pair<String,String>> lAllTrainingInstances = new HashSet<Pair<String, String>>();
		HashSet<Pair<String,String>> lAllTestInstances = new HashSet<Pair<String, String>>();


	
		switch (classArguments.TempMethodVald) {
		case "Random": {
			
			// total training and test instances
			if( indexTrainingName!=-1 && infoSpecies.get(indexTrainingName).mClassGraphsLab0==null){
				lAllTrainingInstances.addAll(readFromFA(data2TrainingAndTesting[0][0], classArguments.TempPartOfseq));
			}
			lAllTestInstances.addAll(readFromFA(data2TrainingAndTesting[1][0], classArguments.TempPartOfseq));

			String sCurClass = null;
			// **********************************************************************
			// Training and Test files Measure and output similarities to ARFF files
			// **********************************************************************
			if( indexTrainingName!=-1 && infoSpecies.get(indexTrainingName).mClassGraphsLab0==null){
				sCurClass=TrainingName.replace("#", "");
				CreateTrainingGram(sCurClass, lAllTrainingInstances, mClassGraphsLab0, mClassGraphsLab1, dPercentOfTrainingForGraphs);			
				classArguments.dataSource = CreateTrainingTestingNgram(TrainingName, lAllTrainingInstances, infoSpecies, mClassGraphsLab0, mClassGraphsLab1,TrainingName);
				infoSpecies.get(indexTrainingName).mClassGraphsLab0 = mClassGraphsLab0;
				infoSpecies.get(indexTrainingName).mClassGraphsLab1 = mClassGraphsLab1;
				infoSpecies.get(indexTrainingName).dataforTraining = classArguments.dataSource;

			}
			else{			

				String[] allTrainingName=TrainingName.split("#");
//				System.out.println(TrainingName+"-----"+allTrainingName.length+"-----"+allTrainingName[0]+"-----"+TestName);			
				for( String str:allTrainingName ){

					 indexTrainingName= getIndexVector(infoSpecies, str);
					 sCurClass = str;
					 //put all instances from the source domain
					 if(classArguments.dataSource==null){
						 classArguments.dataSource = new Instances(infoSpecies.get(indexTrainingName).dataforTraining);
					 }
					 else{
						 classArguments.dataSource.addAll(infoSpecies.get(indexTrainingName).dataforTraining);
					 }

					 //add all mClassGraphsLab
					 mClassGraphsLab0.put(sCurClass,  infoSpecies.get(indexTrainingName).mClassGraphsLab0.get(sCurClass));
					 mClassGraphsLab1.put(sCurClass, infoSpecies.get(indexTrainingName).mClassGraphsLab1.get(sCurClass));				
				}
			}
			classArguments.dataTarget = CreateTrainingTestingNgram(TrainingName, lAllTestInstances, infoSpecies, mClassGraphsLab0, mClassGraphsLab1,TestName);
			break;
		}
		default:
			break;
		}
		

    }
       
    public Instances CreateTrainingTestingNgram(String sCurClass,
			HashSet<Pair<String,String>> lAllTrainInstances, 
			Vector<InfoSpecies>  infoSpecies,
			TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab0,
			TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab1,	
    		String TName) throws Exception{
		
    	// Init class instance counts
		Distribution<String> dClassCountsLab0 = new Distribution<String>();
		Distribution<String> dClassCountsLab1 = new Distribution<String>();

    	String[] allTrainingName=sCurClass.split("#");
    	
    	// merge graphs 
    	if (allTrainingName.length>1){
    		MergeMeanGraphs(sCurClass,mClassGraphsLab0, mClassGraphsLab1);
    	}
    	else
    		sCurClass = allTrainingName[0];

		attribute attr = new attribute();
		Instances dataNgram = new Instances(TName,attr.atts,0);
		dataNgram.setClassIndex(dataNgram.numAttributes() - 1); 
		
		// Get similarities from each class graph
		double[] gsCur0 = new double[5];
		double[] gsCur1 = new double[5];
		

		DocumentNGramSymWinGraph dgClassCur0 = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);
		DocumentNGramSymWinGraph dgClassCur1 = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);	
//		DocumentNGramSymWinGraph tempdgClassCur0 = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);
//		DocumentNGramSymWinGraph tempdgClassCur1 = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);		
//		// k-Mean
//		int indexClasses =-100;
//		if (!sCurClass.equals(TName.replace("#","")) ){
//			int k_mer=2;
//			Vector <String> Kmers= new Vector <String>();
//			Vector <String> Basis =new Vector <String>();
//			Basis.add("A");Basis.add("T");Basis.add("G");Basis.add("C");
//			CreateKmer(k_mer, Kmers, Basis);
//
//			ArrayList<Attribute> att = new ArrayList<Attribute>();
//			att.add(new Attribute("distrGuanineAdenine"));
//			att.add(new Attribute("distrCytosineThymine"));
//			for (int i=0;i<Kmers.size();i++){
//				att.add(new Attribute("distr"+i+"_"+Kmers.get(i)));
//			}
//			att.add(new Attribute("BranchSiteScore"));
//			att.add(new Attribute("Motif_Acceptor"));
//
//
//			Instances data = new Instances(TName,att,0);
//			for (final Pair<String,String> pCurChromosome : lAllTrainInstances) {
//				double[]val = new double[data.numAttributes()];
//				int indexVals=0;
//				BranchSite branchSiteInfo = null;
//				branchSiteInfo = new BranchSite();
//				branchSiteInfo.BranchSite_position_and_score(ConvertString(pCurChromosome.getSecond()));
//				double[] DistributionResidue = null;
//				RichPyrimidine rch =new RichPyrimidine();
//				DistributionResidue = rch.motif_RichPyrimidine(branchSiteInfo.getSeq(),Kmers);
//				val[indexVals++] = DistributionResidue[0]+DistributionResidue[1];
//				val[indexVals++] = DistributionResidue[2]+DistributionResidue[3];
//				for (int i=0;i<DistributionResidue.length;i++){
//					//				System.out.println(i);
//					val[indexVals++] = DistributionResidue[i];
//				}
//				val[indexVals++] = branchSiteInfo.getScore();
//				motif_acceptor_position motifAcceptor = new motif_acceptor_position();
//				val[indexVals++] = motifAcceptor.Score(pCurChromosome.getSecond());
//				data.add(new DenseInstance( 1.0, val));
//			}
//
//
//			SimpleKMeans kmeans = new SimpleKMeans();
//			kmeans.setPreserveInstancesOrder(true);
//			kmeans.setSeed(10);
//			kmeans.setDistanceFunction(new ManhattanDistance());
//			kmeans.setNumClusters(2);
//			kmeans.buildClusterer(data);
//			int[] assignments = kmeans.getAssignments();
//			//		Instances instanc = kmeans.getClusterCentroids();
//			//		for (Instance inst: instanc){
//			//			System.out.println(inst.toString());
//			//		}
//
//			dgClassCur0=mClassGraphsLab0.get(sCurClass);
//			dgClassCur1=mClassGraphsLab1.get(sCurClass);
//			int jj=0;
//			
//			double checkTrueClass0 = 0.0;
//			double checkTrueClass1 = 0.0;
//
//			for (final Pair<String,String> pCurChromosome : lAllTrainInstances) {
//				// Create instance graph
//				DocumentNGramSymWinGraph dg = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);
//				try {
//					dg.setDataString(ConvertString(pCurChromosome.getSecond()));
//				} catch (CompoundNotFoundException e) {
//					e.printStackTrace();
//				}		
//
//				if ( assignments[jj++]==1){// && getPairwiseAligner_Score(dgClassCur0.getDataString(),ConvertString(pCurChromosome.getSecond())) >0 ){ // classs -1 (pCurChromosome.getFirst()).equals("-1") ){//
//					//					System.out.println(getPairwiseAligner_Score(dgClassCur0.getDataString(),ConvertString(pCurChromosome.getSecond())) );
//					// Get instance count for selected class
//					double dGraphCnt = dClassCountsLab0.getValue(sCurClass);
//					tempdgClassCur0.merge(dg,1.0/(dGraphCnt+ 1.0));
//					// Update counts
//					dClassCountsLab0.increaseValue(sCurClass, 1.0);	
//					checkTrueClass0+=Double.parseDouble(pCurChromosome.getFirst());
//
//				}
//				else{
//					// Get instance count for selected class
//					double dGraphCnt = dClassCountsLab1.getValue(sCurClass);
//					tempdgClassCur1.merge(dg,1.0/(dGraphCnt+ 1.0));
//					// Update counts
//					dClassCountsLab1.increaseValue(sCurClass, 1.0);
//					checkTrueClass1+=Double.parseDouble(pCurChromosome.getFirst());
//
//				}
//			}
//			NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
//			double gsCur00 = ngc.getSimilarityBetween(tempdgClassCur0,dgClassCur0).ValueSimilarity;
//			double gsCur01 = ngc.getSimilarityBetween(tempdgClassCur0,dgClassCur1).ValueSimilarity;
//
//			double gsCur10 = ngc.getSimilarityBetween(tempdgClassCur1,dgClassCur0).ValueSimilarity;
//			double gsCur11 = ngc.getSimilarityBetween(tempdgClassCur1,dgClassCur1).ValueSimilarity;
//			// indexClasses take the negative
//			if (gsCur00>gsCur10 && gsCur01<gsCur11) //class -1
//				indexClasses=1;
//			else if(gsCur10>gsCur00 && gsCur11<gsCur01)
//				indexClasses=0;
//			else {// if (gsCur00>gsCur10 && gsCur01>gsCur11){
//				indexClasses=0;
//				if (gsCur00>gsCur01)
//					indexClasses=1;
//			}
//			
//			// if my presumption is fault
//			if(indexClasses==1 && checkTrueClass0>0)
//				System.out.println("FAULT FAULT FAULT FAULT FAULT label -1" );
//			if(indexClasses==0 && checkTrueClass1>0)
//				System.out.println("FAULT FAULT FAULT FAULT FAULT label 1" );
//
//		}
		
		
		for (final Pair<String,String> pCurChromosome : lAllTrainInstances) {
			// Create instance graph
			DocumentNGramSymWinGraph dg = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);
			try {
				dg.setDataString(ConvertString(pCurChromosome.getSecond()));
			} catch (CompoundNotFoundException e) {
				e.printStackTrace();
			}
			// Measure similarity
			double max = -1.0;
			int indexMax = -1;	
						
			// measure similarities of all instances to class graphs
			for(int i=0;i<allTrainingName.length;i++){
				//merge graphs 
				if ( allTrainingName.length<2 )
					sCurClass = allTrainingName[i];
				NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
		    	gsCur0[i]= ngc.getSimilarityBetween(dg,mClassGraphsLab0.get(sCurClass)).ValueSimilarity;
				gsCur1[i]=  ngc.getSimilarityBetween(dg,mClassGraphsLab1.get(sCurClass)).ValueSimilarity;
//				if (!sCurClass.equals(TName.replace("#","")) ){
//					double a= 0.3;
//					if (indexClasses==1){
//						gsCur0[i] = (1-a)*(ngc.getSimilarityBetween(dg,tempdgClassCur0).ValueSimilarity)+a*gsCur0[i];
//						gsCur1[i] = (1-a)*(ngc.getSimilarityBetween(dg,tempdgClassCur1).ValueSimilarity)+a*gsCur1[i];
//					}
//					else{
//						gsCur0[i] =  (1-a)*(ngc.getSimilarityBetween(dg,tempdgClassCur1).ValueSimilarity)+a*gsCur0[i];
//						gsCur1[i] =  (1-a)*(ngc.getSimilarityBetween(dg,tempdgClassCur0).ValueSimilarity)+a*gsCur1[i];
//	
//					}
//				}

				if (max<gsCur0[i]){
					max=gsCur0[i];
					indexMax =i;
				}
				if (max<gsCur1[i]){
					max=gsCur1[i];
					indexMax =i;
				}
			}
			double[]vals = new double[dataNgram.numAttributes()];
			attr.getValuesAtts(vals,gsCur0[indexMax],gsCur1[indexMax],pCurChromosome.getSecond(),pCurChromosome.getFirst(),
								mClassGraphsLab0.get(sCurClass).getDataString(),
								mClassGraphsLab1.get(sCurClass).getDataString());
			dataNgram.add(new DenseInstance( 1.0, vals));
		}
		return dataNgram;
	}
    
    public void Mean_Score_Target_MultipleSeqAlignmenrt(HashSet<Pair<String,String>> TestInstances, String str){

    	DNASequence target = null;
		DNASequence query = null;
		
		SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
		SimpleGapPenalty gapP = new SimpleGapPenalty();
		gapP.setOpenPenalty((short)5);
		gapP.setExtensionPenalty((short)2); 

		double sumScore=0.0;
		List <DNASequence> l = new ArrayList<DNASequence>(2);
    	int i=0;
		for (final Pair<String,String> CurInstances : TestInstances) {
    		if (i++==2)
    			break;
    		try {
				l.add(new DNASequence(CurInstances.getSecond()));
			} catch (CompoundNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//    		try {
//    			target = new DNASequence(CurInstances.getSecond(),AmbiguityDNACompoundSet.getDNACompoundSet());
//    			query = new DNASequence(str,AmbiguityDNACompoundSet.getDNACompoundSet());
//    		} catch (CompoundNotFoundException e) {
//    			e.printStackTrace();
//    		}
//    		PairwiseSequenceAligner<DNASequence, NucleotideCompound> psa = Alignments.getPairwiseAligner(query, target, PairwiseSequenceAlignerType.GLOBAL, gapP, matrix);
//    		sumScore+=psa.getScore();
    		
    	}
		List<SequencePair<DNASequence, NucleotideCompound>> psa = Alignments.getAllPairsAlignments(l, PairwiseSequenceAlignerType.GLOBAL, gapP, matrix);
		System.out.println(psa.get(0).toString());
		System.out.println(psa.get(1).toString());
//		System.out.println(psa.getAlignedSequence(2).toString());
//		System.out.println(psa.getAlignedSequence(3).toString());
//		System.out.println(psa.getAlignedSequence(4).toString());
//    	return sumScore/TestInstances.size();
    	
    } 

    public void MergeMeanGraphs(String TrainingName,
    		TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab0,
			TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab1){

    	
		String[] allTrainingName=TrainingName.split("#");
//		System.out.println(TrainingName+"-----"+allTrainingName.length+"-----"+allTrainingName[0]+"-----"+TestName);
		
		DocumentNGramSymWinGraph dgClassCur0 = new DocumentNGramSymWinGraph();
		DocumentNGramSymWinGraph dgClassCur1 = new DocumentNGramSymWinGraph();
		DocumentNGramGraph dgCur = new DocumentNGramGraph();
		
		for( String sCurClass:allTrainingName ){
			
			 //merge all mClassGraphsLab0 and all mClassGraphsLab1
			 if (dgClassCur0.isEmpty()){
				 dgClassCur0 = mClassGraphsLab0.get(sCurClass);
				 dgClassCur1 = mClassGraphsLab1.get(sCurClass);
			 }
			 else{
				dgCur.setDataString(mClassGraphsLab0.get(sCurClass).getDataString());				 
				 for (;;){
					 try{
						 dgClassCur0.mergeGraph(dgCur, 0.5);
						 break;
					 }catch(ConcurrentModificationException e){
						 continue;
					 }
				 }
				 dgCur.setDataString(mClassGraphsLab1.get(sCurClass).getDataString());
				 for (;;){
					 try{
						 dgClassCur1.mergeGraph(dgCur, 0.5);
						 break;
					 }catch(ConcurrentModificationException e){
						 continue;
					 }
				 }
			 }					
		}
		mClassGraphsLab0.clear();mClassGraphsLab1.clear();

		mClassGraphsLab0.put(TrainingName, dgClassCur0);
		mClassGraphsLab1.put(TrainingName, dgClassCur1);
    }
    
	// DEBUG --	Get similarities from each class 
    public void Checksimilarity(DocumentNGramSymWinGraph a, DocumentNGramSymWinGraph b){
    	GraphSimilarity gsCur = null ;	
    	NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
    	gsCur = ngc.getSimilarityBetween(a,b);	
    	System.out.println("Ngram Get similarities:"+"----"+gsCur.ValueSimilarity);
    }
    
	public void CreateTrainingGram(String sCurClass,
			HashSet<Pair<String,String>> lAllTrainingInstances,
			TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab0,
			TreeMap<String,DocumentNGramSymWinGraph> mClassGraphsLab1,
			double dPercentOfTrainingForGraphs ){	
		
		
		// Init class instance counts
		Distribution<String> dClassCountsLab0 = new Distribution<String>();
		Distribution<String> dClassCountsLab1 = new Distribution<String>();

		Random rRand0 = new Random();
		Random rRand1 = new Random();

		// System.out.println(lAllTrainingInstances.size());
		for (Pair<String,String> sCurChromosome : lAllTrainingInstances) {

				if (sCurChromosome.getFirst().equals("-1") && (rRand1.nextDouble() < dPercentOfTrainingForGraphs) ){
					
					// Create instance graph
					DocumentNGramSymWinGraph dgCur = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);
					try {
						dgCur.setDataString(ConvertString(sCurChromosome.getSecond()));
					} catch (CompoundNotFoundException e1) {
						e1.printStackTrace();
					}
					// If category not yet initialized
					if (mClassGraphsLab0.get(sCurClass) == null) {
						// initialize
						mClassGraphsLab0.put(sCurClass, dgCur);
						// Update counts
						dClassCountsLab0.increaseValue(sCurClass, 1.0);
					}
					else {
						// Merge
						DocumentNGramSymWinGraph dgClassCur = mClassGraphsLab0.get(sCurClass);
						// Get instance count for selected class
						double dGraphCnt = dClassCountsLab0.getValue(sCurClass);
						dgClassCur.merge(dgCur, 1.0 / (dGraphCnt + 1.0));
						mClassGraphsLab0.put(sCurClass, dgClassCur);
						// Update counts
						dClassCountsLab0.increaseValue(sCurClass, 1.0);
					}
				}
				else if (sCurChromosome.getFirst().equals("1") && (rRand0.nextDouble() < dPercentOfTrainingForGraphs)){
					// Create instance graph
					DocumentNGramSymWinGraph dgCur = new DocumentNGramSymWinGraph(MIN_N, MAX_N, DIST);
					try {
						dgCur.setDataString(ConvertString(sCurChromosome.getSecond()));
					} catch (CompoundNotFoundException e1) {
						e1.printStackTrace();
					}

					// If category not yet initialized
					if (mClassGraphsLab1.get(sCurClass) == null) {
						// initialize
						mClassGraphsLab1.put(sCurClass, dgCur);
						// Update counts
						dClassCountsLab1.increaseValue(sCurClass, 1.0);
					}
					else {
						// Merge
						DocumentNGramSymWinGraph dgClassCur = mClassGraphsLab1.get(sCurClass);
						// Get instance count for selected class
						double dGraphCnt = dClassCountsLab1.getValue(sCurClass);
						dgClassCur.merge(dgCur, 1.0 / (dGraphCnt + 1.0));

						mClassGraphsLab1.put(sCurClass, dgClassCur);
						// Update counts
						dClassCountsLab1.increaseValue(sCurClass, 1.0);
					}

				}
				else 
					continue;
				// DEBUG LINES
//				if (++iProgressCnt % 10 == 0) {
//					System.out.print(".");
//					if (iProgressCnt % 100 == 0)
//						System.out.println(iProgressCnt);
//				}
			
		}// end for chromosome
	
	}

	private String ConvertString(String str) throws CompoundNotFoundException{
		//Protein
//		return(new DNASequence(str.substring(str.length()/2 - 42, str.length()/2  +4 )).getRNASequence().getProteinSequence().toString());
//		return (new DNASequence(str).getRNASequence().getProteinSequence().toString());
		//DNA
		return str;
//		return(str.substring(str.length()/2 - 42, str.length()/2  +4 ));

	}
	
	private ArrayList<Pair<String,String>> readFromFA(Instances entries, String TempPartOfseq) {
		ArrayList<Pair<String,String>> lResults = new ArrayList<Pair<String,String>>();

	        for (int i = 0;i<entries.numInstances();i++){
		        String[] str = new String[2]; 
		        str = entries.instance(i).toString().split(",");
	        	String Seq = str[0];
	        	String Label = str[1];
	        	//choose part of sequence
	        	int start,end;
	        	if ("all".equals(TempPartOfseq)){
	        		start=0;
	        		end=Seq.toString().length();
	        	}
	        	else if ("left".equals(TempPartOfseq)){
	        		start=0;
	        		end=Seq.toString().length()/2;
	        	}
	        	else{
	        		start=Seq.toString().length()/2+1;
	        		end=Seq.toString().length();
	        	}

	        	Pair<String,String> pResult = new Pair<String,String>(Label, (String) Seq.subSequence(start, end));
	        	lResults.add(pResult);
	        }
//	        System.out.println(lResults.size());
	        return lResults;
	    }

	public BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return inputReader;
	}
	
	// Species' position is founded in the Vector
	public int getIndexVector( Vector<InfoSpecies> infoSpecies, String name){
		
		int index = -1;
		for(int i=0;i<infoSpecies.size();i++){
			if(infoSpecies.get(i).Name.contains(name.replace("#", ""))){
				index=i;
				break;
			}
		}

		return index;
	}
	
	private double getPairwiseAligner_Score(String meanStr, String sequence) {
		DNASequence target = null;
		DNASequence query = null;

		try {
			target = new DNASequence(ConvertString(meanStr),AmbiguityDNACompoundSet.getDNACompoundSet());
			query = new DNASequence(ConvertString(sequence),AmbiguityDNACompoundSet.getDNACompoundSet());
		} catch (CompoundNotFoundException e) {
			e.printStackTrace();
		}
 
		SubstitutionMatrix<NucleotideCompound> matrix = SubstitutionMatrixHelper.getNuc4_4();
		SimpleGapPenalty gapP = new SimpleGapPenalty();
		gapP.setOpenPenalty((short)5);
		gapP.setExtensionPenalty((short)2); 

		PairwiseSequenceAligner<DNASequence, NucleotideCompound> psa0 = Alignments.getPairwiseAligner(query, target, PairwiseSequenceAlignerType.GLOBAL, gapP, matrix);
		return psa0.getScore();//Distance();
	}
	
	private void CreateKmer(int k_mer, Vector <String> Kmers, Vector <String> Basis){	
		if (k_mer==1){
			Kmers.add("A");
			Kmers.add("G");
			Kmers.add("T");
			Kmers.add("C");	
		}
		else if(k_mer==0){}
		else{
			CreateKmer(k_mer-1, Kmers, Basis);
			int Size=Kmers.size();
			for (int i=0;i<Size;i++){
				for (int j=0;j<Basis.size();j++){
					if (!Kmers.contains(Kmers.get(i)+Basis.get(j)))
						Kmers.add(Kmers.get(i)+Basis.get(j));
				}
			}
		}
	}
}
