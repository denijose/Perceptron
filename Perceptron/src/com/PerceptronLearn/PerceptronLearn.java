package com.PerceptronLearn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.google.gson.Gson;

public class PerceptronLearn {
	
	private static PerceptronModel MODEL;
    private static Integer NO_OF_ITERATIONS = 60;
    private static int ITERATION;
    private static Set<String> WORDSTOIGNORE = new HashSet<String>(Arrays.asList("subject", "Subject" , ",", ".", ":", "re", "fw", "-", "/" , "[", "]"));
	
	
	public void learn(String trainingFileName, String modelFileName) throws IOException {		
    	MODEL = new PerceptronModel(); 
    	ITERATION = 1;
    	createInitialModel(trainingFileName);
//    	  Gson gson = new Gson();
//		  String json = gson.toJson(MODEL);
//		  System.out.println(json);
		for (int i=1; i<= NO_OF_ITERATIONS; i++){
			process(trainingFileName);
//			 gson = new Gson();
//			  json = gson.toJson(MODEL);
//			  System.out.println(json);
			ITERATION++;
		}	
		takeAverage();
		createJSON(modelFileName);
        //System.out.println(MODEL.featureWeights.toString());
		
	}
	
	public void createInitialModel(String trainingFileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(trainingFileName)); 
		String curLine = null;
		String[] words = null;
		ArrayList<String> allWords = new ArrayList<String>();
		//collect categories
	      while( (curLine = br.readLine()) != null ){
	    	  words = curLine.split(" ");
	    	  if(MODEL.featureWeights == null){
	    		  HashMap<String,ArrayList<Double>> value = new HashMap<String,ArrayList<Double>>();
	    		  MODEL.featureWeights = new LinkedHashMap<String,HashMap<String,ArrayList<Double>>>();
	    		  MODEL.featureWeights.put(words[0], value);
	    	  }
	    	  if(!MODEL.featureWeights.containsKey(words[0])){
	    		  HashMap<String,ArrayList<Double>> value = new HashMap<String,ArrayList<Double>>();
	    		  MODEL.featureWeights.put(words[0], value);
	    	  }
	    	  //collect all the words
	    	  for(int i=1; i<words.length; i++){	  
	    		  if(!WORDSTOIGNORE.contains(words[i]))
	    		      allWords.add(words[i]);
	    	  }	  
	      }
	      br.close();
	      
	     
    	  for(int i=0; i<allWords.size(); i++){
    		  for(String category: MODEL.featureWeights.keySet()){
    			  HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
    			  if(!weightVector.containsKey(allWords.get(i))){
	    			  ArrayList<Double> value = new ArrayList<Double>();
	    			  value.add((double)0);
	    			  weightVector.put(allWords.get(i),value);
	    			  MODEL.featureWeights.put(category, weightVector);
    			  }
    		  }
    	  }
	}
	
	public void process(String trainingFileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(trainingFileName)); 
		String curLine = null;
		String[] words = null;
		String correctCategory =  null;
		String maxClass = null;
		double max = 0;
		
		//initialize the weight vectors with the previous iteration's values
		for(String category : MODEL.featureWeights.keySet()){
			HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
			for(String word : weightVector.keySet()){
				ArrayList<Double> wordWeights = weightVector.get(word);
				double prevValue = wordWeights.get(ITERATION-1);
				wordWeights.add(ITERATION, prevValue);
				weightVector.put(word, wordWeights);
			}
			MODEL.featureWeights.put(category, weightVector);
		}
		
		//for each line
		 while( (curLine = br.readLine()) != null ){
			 words = curLine.split(" ");
			 correctCategory = words[0];
			 Double wordWeightSum = null;
			 
             LinkedHashMap<String,Double> classWeightMap = new LinkedHashMap<String,Double>();
             //initialize the summed up weights for each class to zero
             for(String category: MODEL.featureWeights.keySet()){
            	 classWeightMap.put(category, (double)0);
             }
  
             for(int i=1; i<words.length; i++){
            	 if(WORDSTOIGNORE.contains(words[i]))
            		 continue;
            	 for(String category: MODEL.featureWeights.keySet()){
            		 HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
		    		 ArrayList<Double> wordVector = weightVector.get(words[i]);
		    		 wordWeightSum = classWeightMap.get(category);
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
            		 if(max < classWeightMap.get(category)){
            			 max = classWeightMap.get(category);
            			 maxClass = category;
            		 }
            	 }
             }
			
            if(!maxClass.equalsIgnoreCase(correctCategory)){
				for(int i=1; i<words.length; i++){
					if(WORDSTOIGNORE.contains(words[i]))
	            		 continue;
	            	 for(String category: MODEL.featureWeights.keySet()){
	            		 HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
			    		 ArrayList<Double> wordVector = weightVector.get(words[i]);
			    		 if(category.equalsIgnoreCase(correctCategory)){
			    			 double prevValue = wordVector.get(ITERATION);
			    			 double newValue = prevValue + 1;
			    			 wordVector.set(ITERATION, newValue);
			    		 }
			    		 else{
			    			 double prevValue = wordVector.get(ITERATION);
			    			 double newValue = prevValue - 1;
			    			 wordVector.set(ITERATION, newValue);
			    		 }
			    		 weightVector.put(words[i], wordVector);
			    		 MODEL.featureWeights.put(category, weightVector);
			    	}
				}

            }
//		    System.out.println(curLine);
//		    Gson gson = new Gson();
//			  String json = gson.toJson(MODEL);
			 // System.out.println(json);
		
		 }

		 br.close();

	}

	private void createJSON(String modelFileName) throws IOException {
		  Gson gson = new Gson();
		  String json = gson.toJson(MODEL);
		  FileWriter writer = new FileWriter(modelFileName);
		  writer.write(json);
		  writer.close();
		
	}
	
	private void takeAverage() {
		for(String category : MODEL.featureWeights.keySet()){
			HashMap<String,ArrayList<Double>> weightVector = MODEL.featureWeights.get(category);
			for(String word: weightVector.keySet()){
				double average = 0;
				ArrayList<Double> wordWeights = weightVector.get(word);
				for(double value: wordWeights )
					average+=value;
				average = (average/wordWeights.size());
				wordWeights.add(average);
				weightVector.put(word, wordWeights);
			}
			MODEL.featureWeights.put(category, weightVector);
		}
		
	}
}