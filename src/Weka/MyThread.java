package Weka;

import weka.clusterers.ClusterEvaluation;
import weka.core.Instances;

public class MyThread extends Thread {
	ClusterEvaluation eval;
    public MyThread(ClusterEvaluation eval) {
    	this.eval=eval;
    }

	public void run(){
	    while(eval.clusterResultsToString().contains("No clusterer built yet!")){
	    	try {
	    		System.out.println(eval.clusterResultsToString());
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    }
  }