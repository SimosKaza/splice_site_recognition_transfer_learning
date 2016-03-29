package MainExecute;

public class initializeValues {
	private static final String TAB_DELIMITER = "\t";
	private static final String NEW_LINE_SEPARATOR = "\n";
	Integer LoopCounter;
	Integer chooseInstances;
	Double PercentOflabel0;
	Double ratingSelectionSeq;		
	String[] partOfsequence={"all", "left", "right"};		// all, left, right
	String[] MethodValidation={"Random", "kFold" };	// fold, random // Random sampling || 10 fold cross validation
	int numberOfFolds;
	Double dPercentOfTrainingForGraphs;
	String pathChanceFile;
	
	public initializeValues(Integer chooseInstances,String pathChanceFile) {
		LoopCounter = 1;
		this.chooseInstances = chooseInstances;
		PercentOflabel0 = 0.7;
		ratingSelectionSeq = 0.5;		
		numberOfFolds =  10;
		dPercentOfTrainingForGraphs = 0.7;
		this.pathChanceFile=pathChanceFile;
	}
	

	public void setdPercentOfTrainingForGraphs(Double dPercentOfTrainingForGraphs) {
		this.dPercentOfTrainingForGraphs = dPercentOfTrainingForGraphs;
	}

	public synchronized void IncreaseLoopCounter() {
		this.LoopCounter++;
	}
	
	public synchronized Integer getLoopCounter() {
		return this.LoopCounter;
	}

}
	
