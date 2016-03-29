package PreProcessing;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class CreateData {

	private number_data VNumbData = null;
	
	public CreateData(int entries,double percentOflabel0) {
		this.VNumbData = new number_data( entries, percentOflabel0);
	}
	
	public void createFolderSpeciesWithTrainTest() throws IOException {
		final File DataFolder = new File("/home/simos/Desktop/MasterThesis/Project/data/");
		final File pathChanceFile = new File("/home/simos/workspace/Master_thesis/Data/common/data/");

	    for (final File fileEntry : DataFolder.listFiles()) {
	        if (!fileEntry.isDirectory() & fileEntry.getName().contains("acc")) {

	        	String pathInitialFile = DataFolder +"/"+ fileEntry.getName();
	        	String FileName =fileEntry.getName().replace("_all_examples.fasta", "");
	            String pathChooseData = pathChanceFile +"/"+  FileName ;
	            
				CreateNewFileWithAfewData newfastafile = new CreateNewFileWithAfewData(VNumbData);
				newfastafile.NewFastaWithFewerData(pathInitialFile, pathChooseData);
				
				//Random
				String path = pathChanceFile+"/Random/"+FileName+"/";
				new File(path).mkdirs();	
				GreateTrainingTestingData.diverseDataRandomSampling(pathChooseData,
						path + "train.txt", path + "test.txt", 0.7);
				
				//k-Fold
				path = pathChanceFile+"/kFold/"+FileName+"/";
				new File(path).mkdirs();			
				final int FOLDS_NO = 10; // Number of N-fold cross validation
				for (int iFold = 0; iFold < FOLDS_NO; iFold++) {
					GreateTrainingTestingData.diverseDataKFoldCrossValidation(pathChooseData, path + iFold +"_train.txt",
							path + iFold +"_test.txt", FOLDS_NO, iFold);	
				}
			} 	
	    }
	    
	    
	    // test training-test different species
	    String FileName = "A_thaliana_acc#C_elegans_acc";
	    String path = pathChanceFile+FileName;
		new File(path).mkdirs();
		
		Path from;
		Path to ;
		//overwrite existing file, if exists
	    CopyOption[] options = new CopyOption[]{ StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES}; 

		//Random
		path = pathChanceFile+"/Random/"+FileName+"/";
		new File(path).mkdirs();	
	    from = Paths.get("/Master_thesis/Data/common/data/Random/A_thaliana_acc/train.txt");
	    to = Paths.get(path);
	 	Files.copy(from, to, options);
	    from = Paths.get("/Master_thesis/Data/common/data/Random/C_elegans_acc/test.txt");
	    to = Paths.get(path);
	    //overwrite existing file, if exists
	    Files.copy(from, to, options);
		//k-Fold
	    
		path = pathChanceFile+"/kFold/"+FileName+"/";
		new File(path).mkdirs();
		
		
	} 
	
	public void createSpeciesArchiveWithFewerData() throws IOException {
		final File DataFolder = new File("/home/simos/Desktop/MasterThesis/Project/data/");
		final File pathChanceFile = new File("/home/simos/workspace/Master_thesis/Data/common/data/");
		

	    for (final File fileEntry : DataFolder.listFiles()) {
	    	
	        if (!fileEntry.isDirectory() & fileEntry.getName().contains("acc")) {
	        	
	        	this.VNumbData.Initialize();
	        	String pathInitialFile = DataFolder +"/"+ fileEntry.getName();
	        	String FileName =fileEntry.getName().replace("_all_examples.fasta", "");
	            String pathChooseData = pathChanceFile +"/"+  FileName +".arff" ;
	            
				CreateNewFileWithAfewData newfastafile = new CreateNewFileWithAfewData(this.VNumbData);
				newfastafile.NewFileArffWithFewerData(FileName,pathInitialFile,pathChooseData);
				
	        }
	    }
	}

}
