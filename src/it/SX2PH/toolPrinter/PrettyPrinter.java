package it.SX2PH.toolPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import it.SX2PH.HybridAutomata.HYaut;
import it.SX2PH.application.Application;
import it.SX2PH.xmlreader.Parser;

public abstract  class PrettyPrinter {
	static public OutputStream out = System.out;
	static String indentation ="";
	static String tab="    ";
	
	
	 void increaseIndentation() {
		indentation+=tab;
	}
	
	void decreaseIndentation() {
		if(indentation.length()>0)
			indentation=indentation.substring(0, indentation.length()-tab.length());
	}
	
	static  String printFromArrayWithSemicolon(ArrayList<String> array){
		String line="";
		for(int i=0;i<array.size();++i)
			line+= array.get(i)+" ,";
		if(line=="")return line;
		line=line.substring(0, line.length()-2 );
		line+=";";
		return line;
		
	}
	
	public static void printLine(String t) {	
		((PrintStream) out).print(
				indentation + t.replace("\n", "\n"+indentation) + "\n"
				);
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 public static void printNewLine() {
		((PrintStream) out).print(
				"\n"
				);
	}
	
	void printSection(String s) {
		printLine("//*****************  " +s +"  *****************");
	}
	
	public abstract void printAutomaton(HYaut hy);
	
	public abstract void printAutomatonInformation(HYaut hy);
	
	public static void  printSystemInformation(ArrayList<HYaut> automata){
		Integer nTransition=0;
		Integer nLocation=0;
		Integer nOfInstance=automata.size();
		for(HYaut hy: automata) {
			nTransition+=hy.getTransitions().size();
			nLocation+=hy.getLocations().size();
		}
		printLine("// Created with "+HYaut.toolPrefix+" v. "+Application.version);
		printLine("// Instance of: " + Parser.getConfig().getSystemID());
		printLine("// Number of transitions: "+nTransition);
		printLine("// Number of locations:" +nLocation);
		printLine("// Number of instance: "+ nOfInstance);
		printNewLine();
		printNewLine();
	}

}
