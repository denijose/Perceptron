package com.PerceptronLearn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PerceptronLearn {
	
	private static PerceptronModel MODEL;
    private static Integer NO_OF_ITERATIONS = 10;
	
	public void learn(String trainingFileName, String modelFileName) throws IOException {		
    	MODEL = new PerceptronModel(); 
    	createInitialModel(trainingFileName);
		for (int i=1; i< NO_OF_ITERATIONS-1; i++){
			process(trainingFileName);
		}

		
	}
	
	public void createInitialModel(String trainingFileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(trainingFileName)); 
		String curLine = null;
		String[] words = null;
	      while( (curLine = br.readLine()) != null ){
	    	  words = curLine.split(" ");
	    	  if(!MODEL.featureWeights.containsKey(words[0])){
	    		  HashMap<String,ArrayList<Double>> value = new HashMap<String,ArrayList<Double>>();
	    		  MODEL.featureWeights.put(words[0], value);
	    	  }
	    	  for(int i=1; i<words.length; i++){
	    		  for(String category: MODEL.featureWeights.keySet()){
	    			  HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
	    			  if(!weightVector.containsKey(words[i])){
		    			  ArrayList<Double> value = new ArrayList<Double>();
		    			  value.add((double)0);
		    			  weightVector.put(words[i],value);
	    			  }
	    		  }
	    	  }
	    	  
	      }
	      br.close();
	}
	
	public void process(String trainingFileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(trainingFileName)); 
		String curLine = null;
		String[] words = null;
		String correctCategory =  null;
		String maxClass = null;
		double max = 0;
		
		 while( (curLine = br.readLine()) != null ){
			 words = curLine.split(" ");
			 correctCategory = words[0];
			 
             LinkedHashMap<String,Double> classWeightMap = new LinkedHashMap<String,Double>();
             //initialize the summed up weights for each class to zero
             for(String category: MODEL.featureWeights.keySet()){
            	 classWeightMap.put(category, (double)0);
             }
  
             for(int i=1; i<words.length; i++){
            	 for(String category: MODEL.featureWeights.keySet()){
            		 HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
		    		 ArrayList<Double> wordVector = weightVector.get(words[i]);
		    		 Double wordWeightSum = classWeightMap.get(category);
		    		 wordWeightSum += wordVector.get(wordVector.size()-1);
		    		 classWeightMap.put(category, wordWeightSum);
		    	}
		    	
		    	
		    }
             
             maxClass = null;
			 max = Double.MIN_VALUE;
             for(String category: MODEL.featureWeights.keySet()){
            	 if(max == Double.MIN_VALUE){
            		 max = classWeightMap.get(category);
            		 maxClass = category;
            	 }	 
            	 else{
            		 if(max > classWeightMap.get(category)){
            			 max = classWeightMap.get(category);
            			 maxClass = category;
            		 }
            	 }
             }
			
            if(!maxClass.equalsIgnoreCase(correctCategory)){
				for(int i=1; i<words.length; i++){
	            	 for(String category: MODEL.featureWeights.keySet()){
	            		 HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
			    		 ArrayList<Double> wordVector = weightVector.get(words[i]);
			    		 if(category.equalsIgnoreCase(maxClass)){
			    			 double prevValue = wordVector.get(wordVector.size()-1);
			    			 double newValue = prevValue + 1;
			    			 wordVector.add(newValue);
			    		 }
			    		 else{
			    			 double prevValue = wordVector.get(wordVector.size()-1);
			    			 double newValue = prevValue - 1;
			    			 wordVector.add(newValue);
			    		 }
			    		 weightVector.put(words[i], wordVector);
			    		 MODEL.featureWeights.put(category, weightVector);
			    	}
				}

            }
		
		
		 }

		 br.close();

	}

	
}