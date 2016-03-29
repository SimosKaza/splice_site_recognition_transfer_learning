package Weka;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import cc.mallet.util.Maths;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;
import MainExecute.Arguments;
import Ngram.attribute;

// ******************************************************************************
// http://www.programcreek.com/2013/01/a-simple-machine-learning-example-in-java/
// ******************************************************************************

public class weka {
	
	attribute attr = new attribute();	

	public void executeWeka(Arguments classArguments ) throws Exception {
		
		// Use a set of classifiers
		Classifier[] models = { 
				new J48(),    // decision tree
				new SMO(),    // RBFKernel
				new LibSVM(), // linear
				new LibSVM(), // Polunomial
				new IBk(),    // 3nn  - manhattan Distance
				new IBk()     // 21nn - manhattan Distance
		};
		// set options in each classifier
		SetParameters(models);
		
		switch (classArguments.TempMethodVald) {
		case "Random": {
			
			// ****************************************************************
			// Data will be change, but we should keep the original data unchanged  
			// ****************************************************************
			Instances dataTraining = new Instances("dataTraining",attr.atts,0);
			dataTraining.setClassIndex(dataTraining.numAttributes() - 1); 
			dataTraining.addAll(classArguments.dataSource);

			Instances dataTesting = classArguments.dataTarget;

			// ****************************************************************
			// Weight
			// ****************************************************************
			setWeight2eachInst(dataTraining,dataTesting,classArguments);

			// ****************************************************************
			//Running for each model  
			// ****************************************************************
			Evaluation validation = null;
			for (int j = 0; j < models.length; j++){
				// For each training-testing split pair, train and test the classifier
				validation = classify(models[j],dataTraining, dataTesting);			
				// Print current classifier's name and accuracy in a complicated, but nice-looking way.
				PrintResultIntoFile(classArguments,validation);
			}

			WriteArff2file(classArguments, dataTraining, dataTesting);

			// ****************************************************************
			// First case 
			// Algorithm use for training the most Similar Instances for each label from both domains 
			// get clusters-Kmean and Validate with KNN, Results-LibSVM(Poly)
			// ****************************************************************
			Algorithm1_Similarite(validation, dataTraining, dataTesting, classArguments, models);
			
			dataTraining.clear();
			dataTraining.addAll(classArguments.dataSource);
			validation = classify(models[5],dataTraining, dataTesting);			
			// ****************************************************************
			// second case 
			// transfer Features space in order to be both domains more similar
			// ****************************************************************
			Algorithm2_transferFeatures(validation, dataTraining, dataTesting, classArguments, models);
			

			break;
		}
		default:
			break;
		}
	}
	
	// ************************************************************************
	//  Algorithm2 
	// ************************************************************************
	private void Algorithm2_transferFeatures(Evaluation validation,
			Instances dataTraining, Instances dataTesting,
			Arguments classArguments, Classifier[] models) throws Exception {
		
		int i;

		Instances PosDataTraining = new Instances("PosDataTraining",attr.atts,0);
		PosDataTraining.setClassIndex(PosDataTraining.numAttributes() - 1); 
		Instances NegDataTraining = new Instances("NegDataTraining",attr.atts,0);
		NegDataTraining.setClassIndex(NegDataTraining.numAttributes() - 1); 
		for (i=0;i<dataTraining.numInstances();i++){
			if(dataTraining.get(i).classValue()==0)
				PosDataTraining.add(dataTraining.get(i));
			else
				NegDataTraining.add(dataTraining.get(i));

		}

//		choose a classifier  
//		validation = classify(models[6],dataTraining, dataTesting);			
		
		Instances PosDataTest = new Instances("PosDataTest",attr.atts,0);
		PosDataTest.setClassIndex(PosDataTest.numAttributes() - 1); 
		Instances NegDataTest = new Instances("NegDataTest",attr.atts,0);
		NegDataTest.setClassIndex(NegDataTest.numAttributes() - 1); 
		for (i=0;i<dataTesting.numInstances();i++){
			if(validation.predictions().get(i).predicted()==0)
				PosDataTest.add(dataTesting.get(i));
			else
				NegDataTest.add(dataTesting.get(i));

		}
		
		getTheMostSimilarData(PosDataTest,PosDataTest);
		getTheMostSimilarData(NegDataTest,NegDataTest);
		
		getTheMostSimilarData(PosDataTraining,PosDataTest);
		getTheMostSimilarData(NegDataTraining,NegDataTest);
		
		Instances inst =  new Instances("Data",attr.atts,0);
		inst.setClassIndex(dataTesting.numAttributes() - 1); 
		
		double a= 0.8;
		int lastAttribute = 3;
		for(int k=0;k<10;k++){
			

			for (i=0;i<dataTesting.numInstances();i++){
				double predicted = validation.predictions().get(i).predicted();
				
				
				dataTesting.get(i).setValue(0, transportation(a,dataTesting.get(i).value(0),NegDataTest,NegDataTraining,0)); 
				dataTesting.get(i).setValue(1, transportation(a,dataTesting.get(i).value(1),PosDataTest,PosDataTraining,1));
				
				if (predicted==1){ // label -1
					for (int j=2;j<dataTesting.numAttributes()-lastAttribute;j++){
						dataTesting.get(i).setValue(j, transportation(a,dataTesting.get(i).value(j),NegDataTest,NegDataTraining,j)) ; 
					}
				}
				else{ // predicted==0 // label  1
					for (int j=2;j<dataTesting.numAttributes()-lastAttribute;j++){
						dataTesting.get(i).setValue(j,transportation(a,dataTesting.get(i).value(j),PosDataTest,PosDataTraining,j)) ; 
					}
				}
			}
			// used == 2
			validation = classify(models[2],dataTraining, dataTesting);
			
			PosDataTest.clear();NegDataTest.clear();
			
			for ( i=0;i<dataTesting.numInstances();i++){
				inst.clear();
				if(validation.predictions().get(i).predicted()==0){
					inst.add(dataTesting.get(i));
					inst.get(0).setValue(dataTesting.numAttributes()-1,0);
					PosDataTest.add(inst.get(0));
				}
				else{
					inst.add(dataTesting.get(i));
					inst.get(0).setValue(dataTesting.numAttributes()-1,1);
					NegDataTest.add(inst.get(0));
				}
			}
			
			PosDataTraining.clear();NegDataTraining.clear();
			dataTraining.clear();
			dataTraining.addAll(classArguments.dataSource);


			for (i=0;i<dataTraining.numInstances();i++){
				if(dataTraining.get(i).value(dataTraining.numAttributes()-1)==0)
					PosDataTraining.add(dataTraining.get(i));
				else
					NegDataTraining.add(dataTraining.get(i));

			}
			
			getTheMostSimilarData(PosDataTest,PosDataTest);
			getTheMostSimilarData(NegDataTest,NegDataTest);
			
			getTheMostSimilarData(PosDataTraining,PosDataTest);
			getTheMostSimilarData(NegDataTraining,NegDataTest);
			
			
			System.out.println(PosDataTraining.numInstances()+" _---_ "+NegDataTraining.numInstances()+"---"+
					PosDataTest.numInstances()+" _---_ "+NegDataTest.numInstances()+"__"+dataTraining.numInstances());

			
			dataTraining.clear();
			dataTraining.addAll(PosDataTraining);
			dataTraining.addAll(NegDataTraining);
//			dataTraining.addAll(PosDataTest);
//			dataTraining.addAll(NegDataTest);
			
		}
        //Print current classifier's name and accuracy in a complicated, but nice-looking way.
		PrintResultIntoFile(classArguments,validation);
		WriteArff2file(classArguments, dataTraining, dataTesting);

				
	}
	
	// equation transformation
	private double transportation(double a, double value, Instances data1,
			Instances data2,int index) {
		double division;

		//kl-divergence
//		***************************************************************************************
//		Maths.klDivergence(double[] p1, double[] p2); // Returns the KL divergence
////		***************************************************************************************
//		double[] atr_data1=data1.attributeToDoubleArray(index);
//		double[] atr_data2=data2.attributeToDoubleArray(index);
//		
//		if (data1.numInstances()<data2.numInstances()){
//			double[] temp = new double[atr_data2.length];
//			for(int i=0;i<atr_data2.length;i++)
//				temp[i]=atr_data1[i/atr_data1.length];
//			division = Maths.klDivergence(temp,atr_data2);
//		}
//		else{
//			double[] temp = new double[atr_data1.length];
//			for(int i=0;i<atr_data1.length;i++)
//				temp[i]=atr_data2[i/atr_data2.length];
//			division = Maths.klDivergence(atr_data1,temp);
//
//		}
//		return (a * value) + (1-a) * value * (division) ;	

//		// means
		division = ( data2.meanOrMode(index) - data2.variance(index)/2)
											  /
				   ( data1.meanOrMode(index) - data1.variance(index)/2);
		
		return (a * value) + (1-a) * value * division ;	
	
	}
	
	// ************************************************************************
	//  Algorithm1 
	// ************************************************************************
	private void Algorithm1_Similarite(Evaluation validation,
			Instances dataTraining, Instances dataTesting,
			Arguments classArguments, Classifier[] models) throws Exception {

		int i;
		// Read data 
		Instances Test =ReadData("/home/simos/workspace/Master_thesis/Data/ngram/Random_kFold/"+classArguments.TrainName+"_"+classArguments.TestName+"_test.arff");

		// Kmeans set options, Build, result
		SimpleKMeans kmeans = new SimpleKMeans();
		setOptionsKmeans(kmeans);
		kmeans.buildClusterer(Test);
		int[] assignments = kmeans.getAssignments();

//		choose a classifier  
//		validation = classify(models[1],dataTraining, dataTesting);			

		// return 0 or 1. the result of the Classifier to found Which of the Classes  is the Negative one
		int NegClass=FoundNegativeClass(assignments, validation);


		Instances PosDataTest = new Instances("PosDataTest",attr.atts,0);
		PosDataTest.setClassIndex(PosDataTest.numAttributes() - 1); 
		Instances NegDataTest = new Instances("NegDataTest",attr.atts,0);
		NegDataTest.setClassIndex(NegDataTest.numAttributes() - 1); 
		i=-1;
		Instances inst =  new Instances("Data",attr.atts,0);
		inst.setClassIndex(dataTesting.numAttributes() - 1); 
		for(int clusterNum : assignments) {	
			i++;
			if (clusterNum==0 && NegClass==0)
				clusterNum=1;
			else if(clusterNum==1 && NegClass==0){
				clusterNum=0;
			}
			inst.clear();
			if ( clusterNum ==0){ // class 1
				inst.add(dataTesting.get(i));
				inst.get(0).setValue(dataTesting.numAttributes()-1,0);
				PosDataTest.add(inst.get(0));
			} 
			else if(clusterNum ==1){ // class -1
				inst.add(dataTesting.get(i));
				inst.get(0).setValue(dataTesting.numAttributes()-1,1);
				NegDataTest.add(inst.get(0));
			}
		}



		getTheMostSimilarData(PosDataTest,PosDataTest);
		getTheMostSimilarData(NegDataTest,NegDataTest);

		Instances PosDataTraining = new Instances("PosDataTraining",attr.atts,0);
		PosDataTraining.setClassIndex(PosDataTraining.numAttributes() - 1); 
		Instances NegDataTraining = new Instances("NegDataTraining",attr.atts,0);
		NegDataTraining.setClassIndex(NegDataTraining.numAttributes() - 1); 


		for (i=0;i<dataTraining.numInstances();i++){
			if(dataTraining.get(i).classValue()==0.0)
				PosDataTraining.add(dataTraining.get(i));
			else
				NegDataTraining.add(dataTraining.get(i));
		}



		getTheMostSimilarData(PosDataTraining,PosDataTest);
		getTheMostSimilarData(NegDataTraining,NegDataTest);


		// put weight
		// first weight allready done for each instance there is a similarity 
		// second weight the hops from species' distance  
		// third weight is the labels' percentage
		int amount = PosDataTraining.numInstances()+NegDataTraining.numInstances()+
				PosDataTest.numInstances();//+NegDataTest.numInstances();

		dataTraining.clear();

		dataTraining.addAll(PosDataTraining);
		setweights(PosDataTraining,amount);

		dataTraining.addAll(PosDataTest);
		setweights(PosDataTest,amount);

		dataTraining.addAll(NegDataTraining);
		setweights(NegDataTraining,amount);

		dataTraining.addAll(NegDataTest);
		setweights(NegDataTest,amount);

		System.out.println(PosDataTraining.numInstances()+" +---+ "+NegDataTraining.numInstances()+"---"+
				PosDataTest.numInstances()+" +---+ "+NegDataTest.numInstances()+"==="+dataTraining.numInstances());


		validation = classify(models[1],dataTraining, dataTesting);


		PrintResultIntoFile(classArguments,validation);
//		WriteArff2file(classArguments, dataTraining, dataTesting);
		
	}

	// Found Negative Class
	private int FoundNegativeClass(int[] assignments,Evaluation validation){
		
		int counterClass0=0;
		int counterClass1=0;
		int ii=-1;
		for(int clusterNum : assignments) {
			ii++;
			if (clusterNum==0){
				if(validation.predictions().get(ii).predicted()==0){
					counterClass0+=1;
				}else{
					counterClass0-=1;
				}
			}
			else{
				if(validation.predictions().get(ii).predicted()==0){
					counterClass1+=1;
				}else{
					counterClass1-=1;
				}
			}
		}
		int NegClass=0; // the position has been taken from the smaller number
		if (counterClass1 < counterClass0)
			NegClass=1;
		return NegClass;
	}
	private int FoundNegativeClass2(Instances clusterCentroids,
			Instance instance) {
		
		double[] similarity= new double[clusterCentroids.numInstances()];
		double sumTrain, sumTest,sumTrainTest;

		System.out.println(instance.toString());
		for(int i = 0;i<clusterCentroids.numInstances();i++){
			similarity[i]=0.0;
			sumTrain=0.0;
			sumTest=0.0;
			sumTrainTest=0.0;

			for (int j=0; j<clusterCentroids.numAttributes()-4;j++){
				sumTrain+=Math.pow(clusterCentroids.get(i).value(j),2);
			}
			sumTrain=Math.sqrt(sumTrain);			

				sumTest=0.0;
				sumTrainTest=0.0;
				for (int j=0; j<clusterCentroids.numAttributes()-4;j++){
					sumTest+=Math.pow(instance.value(j),2);
				}
				sumTest=Math.sqrt(sumTest);
				for (int j=0; j<clusterCentroids.numAttributes()-4;j++){
					sumTrainTest+=(instance.value(j) * clusterCentroids.get(i).value(j));
				}

				similarity[i] += sumTrainTest/(sumTest * sumTrain );


		}
		if (similarity[0]>similarity[1])
			return 0;
		else
			return 1;
	}

	// ************************************************************************
	// Weight  
	// ************************************************************************
	private void setWeight2eachInst(Instances dataTraining,Instances dataTesting,
			Arguments classArguments) throws IOException {		
		
		int i;
		
//		// Phylogenetic Distance Matrix
		Read_DistMatrix classWeight =  new Read_DistMatrix();
		Vector<Double> Weight = classWeight.readDistMatrix(classArguments.TestName,classArguments.TrainName);
		
		//weight on each instance from DistMatrix
		for(i = 0;i<dataTraining.size();i++){
			if (dataTraining.get(i).value(dataTraining.numAttributes()-1)==-1)
//				dataTraining.get(i).setWeight(0.5 );
//				dataTraining.get(i).setWeight( Weight.get(i/classArguments.chooseInstances) );
				dataTraining.get(i).setWeight(0.5* Weight.get(i/classArguments.chooseInstances) + 0.5 * 0.7 ); //0.7
			else
//				dataTraining.get(i).setWeight( 0.99 );
				dataTraining.get(i).setWeight(0.5* Weight.get(i/classArguments.chooseInstances) + 0.5 * 0.99); //0.99

		}		
		
//		// weight with hoppes
//		Vector<Double> hopWeight = classWeight.readHoppes(classArguments.TestName,classArguments.TrainName);
//		for( i = 0;i<dataTraining.size();i++){
//			dataTraining.get(i).setWeight(hopWeight.get(i/classArguments.chooseInstances));
//		}


	}
	
	private void setweights(Instances data, int amount) {
		for (int i=0;i<data.numInstances();i++){
			data.get(i).setWeight(0.75 * (data.get(i).weight())+ 0.25 * 
					(1-(data.numInstances()/amount)) );
//			data.get(i).setWeight((1-(data.numInstances()/amount)));
		}		
	}
	
	private void setSimilarityASWeight(Instances data,
			double[] similarity, int i) {
		
//		// ****************************************************************
//		// put weight -----------------------------------------------------
		data.get(i).setWeight(0.75 * (data.get(i).weight())+ 0.25 * similarity[i]); 
//		// ****************************************************************
		
	}
	
	// ************************************************************************
	// Parameters  
	// ************************************************************************
	private void SetParameters(Classifier[] models) throws Exception {
		//RBF Kernel
		((SMO) models[1]).setKernel(new RBFKernel());
		String opts;
		//linear Kernel
		opts = "-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -Z -W 1.0";
		((LibSVM) models[2]).setOptions(opts.split(" "));
		
		//poly Kernel
		opts = "-S 0 -K 1 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -Z -W 1.0";
		((LibSVM) models[3]).setOptions(opts.split(" "));
		
		//3-NN
		((IBk) models[4]).setKNN(3);
		LinearNNSearch lnn2 = new LinearNNSearch();
		lnn2.setDistanceFunction(new ManhattanDistance());
		((IBk) models[4]).setNearestNeighbourSearchAlgorithm(lnn2);
		
		//21-NN
		((IBk) models[5]).setKNN(21);
		LinearNNSearch lnn = new LinearNNSearch();
		lnn.setDistanceFunction(new ManhattanDistance());
		((IBk) models[5]).setNearestNeighbourSearchAlgorithm(lnn);		
	
	}

	private void setOptionsKmeans(SimpleKMeans kmeans) {
        kmeans.setPreserveInstancesOrder(true);
		kmeans.setSeed(10);
		try {
			kmeans.setDistanceFunction(new ManhattanDistance());
		    kmeans.setNumClusters(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ************************************************************************
	// Classifier  
	// ************************************************************************
	private Evaluation classify(Classifier model,
			Instances trainingSet, Instances testingSet) throws Exception {
 
		model.buildClassifier(trainingSet);
		Evaluation evaluation = new Evaluation(trainingSet);
		evaluation.evaluateModel(model, testingSet);
 
		return evaluation;
	}
	
	private double calculateAccuracy(FastVector predictions) {
		double correct = 0;
		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}
		return 100 * correct / predictions.size();
	}
	 
	public Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
		Instances[][] split = new Instances[2][numberOfFolds];
		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}
		return split;
	}

	// ************************************************************************
	// Data Similarites
	// ************************************************************************
	private void getTheMostSimilarData(Instances DataTraining,
			Instances DataTest) {
		
		Vector<Integer> delelements = new Vector<Integer>();
		double min=100,max=-100,mean=0.0;
		int lastAttribute = 1;
		
		double[] similarity= new double[DataTraining.numInstances()];
		double sumTrain, sumTest,sumTrainTest;
		for(int i = 0;i<DataTraining.numInstances();i++){
			similarity[i]=0.0;
			sumTrain=0.0;
			sumTest=0.0;
			sumTrainTest=0.0;

			for (int j=0; j<DataTraining.numAttributes()-lastAttribute;j++){
				sumTrain+=Math.pow(DataTraining.get(i).value(j),2);
			}
			sumTrain=Math.sqrt(sumTrain);			

			for(int ii=0;ii<DataTest.numInstances();ii++){
				sumTest=0.0;
				sumTrainTest=0.0;
				for (int j=0; j<DataTraining.numAttributes()-lastAttribute;j++){
					sumTest+=Math.pow(DataTest.get(ii).value(j),2);
				}
				sumTest=Math.sqrt(sumTest);
				for (int j=0; j<DataTraining.numAttributes()-lastAttribute;j++){
					sumTrainTest+=(DataTest.get(ii).value(j) * DataTraining.get(i).value(j));
				}

				similarity[i] += sumTrainTest/(sumTest * sumTrain );
			}
			similarity[i]/=DataTest.numInstances();
			mean+=similarity[i];
//			if (min>similarity[i])
//				min=similarity[i];
//			if(max<similarity[i])
//				max=similarity[i];
		}
		mean=mean/similarity.length;
//		System.out.println(max +"___"+min+"___"+mean);
	
		double var=0.0;
		for (int k=0;k<similarity.length;k++){
			
			// ****************************************************************
			// put weight -----------------------------------------------------
			setSimilarityASWeight(DataTraining,similarity,k);
			// ****************************************************************

			var+=Math.pow(similarity[k]-mean, 2);
		}
		var/=similarity.length;
		double s=Math.sqrt(var);
		for (int k=0;k<similarity.length;k++){
			if (similarity[k]<mean-0.01)//var/4) // var against 0.01
				delelements.add(k);
		}
		deleteNoSimilar(DataTraining,delelements);
	}
	
	private void getTheMostSimilarDataTest(Instances DataTest) {
		Vector<Integer> delelements = new Vector<Integer>();
	
		
		for (int i=0;i<DataTest.numInstances();i++){
			for (int j=0;j<DataTest.numAttributes()-3;j++){
				double d= Math.abs(DataTest.get(i).value(j) - DataTest.meanOrMode(j)) / (DataTest.variance(j));
//				System.out.println("  ---  "+d);
				if ( d > 50  ){
					delelements.add(i);
					break;
				}	
			}
		}
		deleteNoSimilar(DataTest,delelements);		
	}
	
	private void deleteNoSimilar(Instances posDataTest,
			Vector<Integer> delelements) {
	
//		double siz=posDataTest.numInstances();
		int counter=0;
		for(int index : delelements){
			posDataTest.delete(index-counter);
			counter++;
		}
//		System.out.println(delelements.size()+"---"+ siz +"---"+ posDataTest.numInstances());
//		System.exit(-1);		
	}
	
	// ************************************************************************
	// Print - write  
	// ************************************************************************
	private void PrintResultIntoFile(Arguments classArguments,
			Evaluation validation) {
		classArguments.appendTempResult(validation.numTruePositives(0) 
				+ "   -    "
				+ validation.numFalseNegatives(0)
				+ " --- "
				+ validation.numFalsePositives(0) 
				+ "   -    "
				+ validation.numTrueNegatives(0) + "\t"
				+ String.format("%.2f%% \t %.4f \t", validation.pctCorrect(), (validation.fMeasure(0)+validation.fMeasure(1))/2 )
				+ String.format("%.4f\t", (validation.areaUnderROC(0)+validation.areaUnderROC(1)) / 2 ));		
	}

	private void WriteArff2file(Arguments classArguments, Instances dataTraining, Instances dataTesting) throws IOException {
		OutputStreamWriter outFewlines = new OutputStreamWriter(new FileOutputStream(new File("/home/simos/workspace/Master_thesis/Data/ngram/Random_kFold/"+classArguments.TrainName+"_"+classArguments.TestName+"_train.arff")));
		outFewlines.write(dataTraining.toString());
		outFewlines.close();
		outFewlines = new OutputStreamWriter(new FileOutputStream(new File("/home/simos/workspace/Master_thesis/Data/ngram/Random_kFold/"+classArguments.TrainName+"_"+classArguments.TestName+"_test.arff")));
		outFewlines.write(dataTesting.toString());
		outFewlines.close();
	}

	private Instances ReadData(String string) throws IOException {
		BufferedReader breader = new BufferedReader(new FileReader(string));
        Instances Test = new Instances(breader);
		Test.deleteAttributeAt(Test.numAttributes() - 1); // the label deleted because of it is not attribute for the k-means
        breader.close();		
        return Test;
	}

	private BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}

}