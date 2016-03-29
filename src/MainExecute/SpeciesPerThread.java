package MainExecute;

import java.io.IOException;
import java.util.Vector;

import Ngram.N_gram;
import Weka.weka;

public class SpeciesPerThread extends Thread {
	
	private initializeValues initVal;
	private FileResults fileResults;
	private String TrainingName;
	private String TestName;
	Vector<InfoSpecies>  infoSpecies;
	
	public SpeciesPerThread(initializeValues initVal,FileResults fileResults,Vector<InfoSpecies>  infoSpecies,String TrainingName,String TestName) {
		this.initVal=initVal;
	    this.fileResults=fileResults;
		this.TrainingName=TrainingName;
		this.TestName=TestName;
		this.infoSpecies= infoSpecies;
	}

	@SuppressWarnings("unused")
	public void run( ) {
		

		for(int CounterMethod=0;CounterMethod<2;CounterMethod++ ) {			

			for(int CounterPartSeq=0;CounterPartSeq<3;CounterPartSeq++) {

				String TempMethodVald= initVal.MethodValidation[CounterMethod];
				String TempPartOfseq= initVal.partOfsequence[CounterPartSeq];

				Arguments classArguments = new Arguments(TrainingName,TestName,TempMethodVald,TempPartOfseq,
						initVal.chooseInstances,initVal.dPercentOfTrainingForGraphs);
				
				classArguments.setTempResult(initVal);
				initVal.IncreaseLoopCounter();

//				String pathSampling = pathChanceFile + initVal.MethodValidation[CounterMethod] + "/" + FileName + "/";	
//    			FormatForCRFWapiti.executeCRF(fileWriter,pathSampling, TempMethodVald, TempPartOfseq);
				
				try {
					N_gram ngram = new N_gram();
					ngram.executeNgram(classArguments, this.infoSpecies);
					weka wk = new weka();
					wk.executeWeka(classArguments);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				FileResults.appendFileWriter(classArguments.getTempResult()+"\n");		
				break;
			}break;
		}
	}
}
