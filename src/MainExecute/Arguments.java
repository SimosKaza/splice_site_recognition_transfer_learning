package MainExecute;

import weka.core.Instances;


public class Arguments {
	
	public String tempResult;
	public String TrainName;
	public String TestName;
	public String TempMethodVald;
	public String TempPartOfseq;
	public int chooseInstances;
	public double dPercentOfTrainingForGraphs;
	public Instances dataSource;
	public Instances dataTarget;
	
	public Arguments(String TrainName,String TestName, String TempMethodVald, String TempPartOfseq,int chooseInstances,double dPercentOfTrainingForGraphs) {
		this.TrainName=TrainName;
		this.TestName=TestName;
		this.TempMethodVald=TempMethodVald;
		this.TempPartOfseq=TempPartOfseq;
		this.chooseInstances=chooseInstances;
		this.dPercentOfTrainingForGraphs=dPercentOfTrainingForGraphs;
	}
	
	public String getTempResult() {
		return tempResult;
	}

	public void setTempResult(initializeValues initVal) {
		this.tempResult = initVal.getLoopCounter() + FileResults.TAB_DELIMITER + 
				initVal.chooseInstances.toString() + FileResults.TAB_DELIMITER +
				initVal.PercentOflabel0.toString() + FileResults.TAB_DELIMITER +
				initVal.ratingSelectionSeq.toString() + FileResults.TAB_DELIMITER +
				TempMethodVald + FileResults.TAB_DELIMITER+
				TempPartOfseq + FileResults.TAB_DELIMITER+
				TrainName + FileResults.TAB_DELIMITER+
				TestName + FileResults.TAB_DELIMITER;
	}
	public synchronized void appendTempResult(String tempResult) {
		this.tempResult = this.tempResult+tempResult;
	}


}
