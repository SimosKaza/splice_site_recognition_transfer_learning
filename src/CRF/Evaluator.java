package CRF;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Evaluator {

    private Double TP;
    private Double FP;
    private Double TN;
    private Double FN;
    public Double[][] truth_table = null;

    public Evaluator() {
        TP = 0.0;
        TN = 0.0;
        FP = 0.0;
        FN = 0.0;
    	truth_table = new Double[][] { { 0.0, 0.0 }, { 0.0, 0.0 } };

    }

    public void evaluate(String pathCrfResult) {
		Integer valueTT_Classifier = 0;
		Integer valueTT_truth = 0;

		try {
			BufferedReader br_result =  new BufferedReader(new FileReader(pathCrfResult));
			String CurrentLine;
			String[] splitLine = null;
			int pos_counter=0,neg_counter=0;
			
			while ((CurrentLine = br_result.readLine()) != null) {

				if (CurrentLine.isEmpty()) {
					
//					System.out.println(pos_counter+" *** "+neg_counter +" \t= "+ splitLine[1]+ " # "+valueTT_Classifier);
					pos_counter=0;
					neg_counter=0;
					
					if (valueTT_Classifier < 0)
						valueTT_Classifier = 1;
					else
						valueTT_Classifier = 0;

					if (Integer.parseInt(splitLine[1]) == -1)
						valueTT_truth = 1;
					else
						valueTT_truth = 0;

					truth_table[valueTT_truth][valueTT_Classifier]++;
					valueTT_Classifier = 0;

				} else {
					splitLine = CurrentLine.split("\t");
					if (Integer.parseInt(splitLine[2])>0)
						pos_counter++;	
					else
						neg_counter++;
					// System.out.println(CurrentLine+"->"+ValueLabel[1]
					// +"***"+ValueLabel[2]);
					valueTT_Classifier = valueTT_Classifier
							+ Integer.parseInt(splitLine[2]);

				}
			}
			br_result.close();
			
			TP=truth_table[0][0];
			FP=truth_table[0][1];
			FN=truth_table[1][0];
			TN=truth_table[1][1];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
        
    }

    public double getAccuracy() {
        if (FP + FN == 0) {
            return 1;//Perfect Accuracy
        } else {
            return ((TP + TN) / (TP + TN + FP + FN));
        }
    }

    public double getRecallPos() {
        if (TP + FN == 0) {
            return 1;
        } else {
            return TP / (TP + FN);
        }
    }

    public double getRecallNeg() {
        if (TN + FP == 0) {
            return 1;
        } else {
            return TN / (TN + FP);
        }
    }

    public double getPrecisionPos() {
        if (TP + FP == 0) {
            return 1;
        } else {
            return TP / (TP + FP);
        }
    }

    public double getPrecisionNeg() {
        if (TN + FN == 0) {
            return 1;
        } else {
            return TN / (TN + FN);
        }
    }
    
    public double getFMeasure() {
    	return (FMeasurePos() + FMeasureNeg())/ 2;
//        return (2 * TP) / ((2 * TP) + FP + FN);
    }
    
    public double FMeasurePos() {
        return (2 * getPrecisionPos() * getRecallPos()) / (getPrecisionPos() + getRecallPos());
    }

    public double FMeasureNeg() {
        return (2 * getPrecisionNeg() * getRecallNeg()) / (getPrecisionNeg() + getRecallNeg());
    }
}
