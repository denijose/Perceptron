package com.PerceptronLearn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class PerceptronModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public LinkedHashMap<String,HashMap<String,ArrayList<Double>>> featureWeights;
	
	public PerceptronModel(){
		ArrayList<Double> arrayList = new ArrayList<Double>();
		HashMap<String,ArrayList<Double>> value = new HashMap<String,ArrayList<Double>>();
		LinkedHashMap<String,HashMap<String,ArrayList<Double>>> featureWeights = new LinkedHashMap<String,HashMap<String,ArrayList<Double>>>();
	}
	
	
}
