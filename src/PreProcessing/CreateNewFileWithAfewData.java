package PreProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;


public class CreateNewFileWithAfewData {

	private number_data VNumbData = null;
	public double percentOfchoosen=0.5;
	
	public CreateNewFileWithAfewData(number_data VNumbData) {
		this.VNumbData=VNumbData;
	}	

	public void NewFastaWithFewerData(String pathInitialFile, String pathChanceFile) {

    	try {
		// Read file
		BufferedReader br = new BufferedReader(new FileReader(pathInitialFile), 500);
		// write file
		OutputStreamWriter outFewlines = new OutputStreamWriter(new FileOutputStream(new File(pathChanceFile)));
		

		String[] ValueLabel;
		String sCurrentLine;
		Random rRand = new Random();
		
		
		while ( ((sCurrentLine = br.readLine()) != null) & !VNumbData.isfull() ) {
			
			if ( sCurrentLine.contains(">") & rRand.nextDouble()< this.percentOfchoosen){
				ValueLabel = sCurrentLine.split("label=");							
				if (Integer.parseInt(ValueLabel[1])==-1 & !VNumbData.isfull_lebel0()  ) {	
					VNumbData.add_Number_of_lebel0();
					outFewlines.write(sCurrentLine+"\n");
					sCurrentLine = br.readLine();
					outFewlines.write(sCurrentLine+"\n");

				}
				else if(Integer.parseInt(ValueLabel[1])==1 & !VNumbData.isfull_lebel1()  ){
					VNumbData.add_Number_of_lebel1();
					outFewlines.write(sCurrentLine+"\n");
					sCurrentLine = br.readLine();
					outFewlines.write(sCurrentLine+"\n");
				}
				else 
					continue;
			}					
		}
		br.close();
		outFewlines.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void NewFileArffWithFewerData(String FileName,String pathInitialFile, String pathChanceFile) {

    	try {
		// Read file
		BufferedReader br = new BufferedReader(new FileReader(pathInitialFile), 500);
		// write file
		OutputStreamWriter outFewlines = new OutputStreamWriter(new FileOutputStream(new File(pathChanceFile)));
		

		String[] ValueLabel;
		String sCurrentLine;
		Random rRand = new Random();
		
		outFewlines.append("@relation "+FileName+"\n" +
						"@attribute sequence string\n" +
						"@attribute play {-1,1}\n" +
						"@data");
		
		while ( ((sCurrentLine = br.readLine()) != null) & !VNumbData.isfull() ) {		
			if ( sCurrentLine.contains(">") & rRand.nextDouble()< this.percentOfchoosen){
				ValueLabel = sCurrentLine.split("label=");							
				if (Integer.parseInt(ValueLabel[1])==-1 & !VNumbData.isfull_lebel0()  ) {	
					VNumbData.add_Number_of_lebel0();
					sCurrentLine = br.readLine();
					outFewlines.write("\n"+sCurrentLine+",\t"+ValueLabel[1]);
				}
				else if(Integer.parseInt(ValueLabel[1])==1 & !VNumbData.isfull_lebel1()  ){
					VNumbData.add_Number_of_lebel1();
					sCurrentLine = br.readLine();
					outFewlines.write("\n"+sCurrentLine+",\t"+ValueLabel[1]);
				}
				else 
					continue;
			}					
		}
		br.close();
		outFewlines.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
