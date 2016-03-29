package Ngram;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Evaluator {

    public double TP;
    public double FP;
    public double TN;
    public double FN;
    public double[][] truth_table;

    public Evaluator() {
        this.TP = 0.0;
        this.TN = 0.0;
        this.FP = 0.0;
        this.FN = 0.0;
        this.truth_table = new double[][] { { 0.0, 0.0 }, { 0.0, 0.0 } };

    }

    public void evaluate(String pathCrfResult) {

        int valueTT_Classifier = 0;
		int valueTT_truth = 0;

		try {
			BufferedReader br_result =  new BufferedReader(new FileReader(pathCrfResult));
			String CurrentLine;
			String[] splitLine = null;
			
			while ((CurrentLine = br_result.readLine()) != null &  !CurrentLine.contains("@data")  ) {
			}
			while ((CurrentLine = br_result.readLine()) != null  ) {
				
				splitLine = CurrentLine.split(", ");
				
//				System.out.println("===="+splitLine[splitLine.length-1]);
				
				if (Double.parseDouble(splitLine[0]) > Double.parseDouble(splitLine[1]))
					valueTT_Classifier = 1;
				else
					valueTT_Classifier = 0;

				if (Integer.parseInt(splitLine[splitLine.length-1]) == -1)
					valueTT_truth = 1;
				else
					valueTT_truth = 0;

				this.truth_table[valueTT_truth][valueTT_Classifier]++;
			}
			
			br_result.close();
			
			TP=truth_table[0][0];
			FP=truth_table[0][1];
			FN=truth_table[1][0];
			TN=truth_table[1][1];
		} catch (IOException e) {
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
        return (2 * TP) / ((2 * TP) + FP + FN);
    }
    
    public double FMeasurePos() {
        return (2 * getPrecisionPos() * getRecallPos()) / (getPrecisionPos() + getRecallPos());
    }

    public double FMeasureNeg() {
        return (2 * getPrecisionNeg() * getRecallNeg()) / (getPrecisionNeg() + getRecallNeg());
    }
    
    public String PrintTruthTable(){
    	return String.format( "%.4f", TP) 
			+ "   -    "
			+ String.format( "%.4f", FP) 
			+ " --- "
			+ String.format( "%.4f", FN)
			+ "   -    "
			+ String.format( "%.4f", TN) + "\t";
    }
 }
    
