package com.PerceptronLearn;

import java.io.IOException;


public class Main {

	/**
	 * @param args
	 */
	
	private static String TRAININGFILE;
	private static String MODELFILE;
	
	public static void main(String[] args) throws IOException {
		parseArgs(args);
		System.out.println("Starter Learning.....");
		PerceptronLearn pLearn = new PerceptronLearn();
		pLearn.learn(TRAININGFILE, MODELFILE);
		System.out.println("Finished Learning.....");

	}
	
	private static void parseArgs(String[] args) {
		if(args.length < 2){
			System.out.println("Insufficient arguments. Usage -");
			System.out.println("java -jar perceptronLearn.jar TRAININGFILE MODELFILE");
			System.exit(1);
		}
		TRAININGFILE = args[0];
		MODELFILE = args[1];
			
	}

}
