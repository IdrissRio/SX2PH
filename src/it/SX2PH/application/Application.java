package it.SX2PH.application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.SX2PH.HybridAutomata.HYaut;
import it.SX2PH.HybridAutomata.HYautMulti;
import it.SX2PH.exception.SX2PHNoBindException;
import it.SX2PH.toolPrinter.PhaverPrettyPrinter;
import it.SX2PH.toolPrinter.PrettyPrinter;
import it.SX2PH.xmlreader.Parser;
/** SX2PH entry point.
*  Application class. This class contain the main function that call the run method.
* 
* @author  Riouak Idriss
* @version 0.1.1
*/
public class Application {
	public static String version="0.1.1";
	
	//XML input path
	@Option(name="-iXML",usage="The path of the .XML input file. The path is relative to the location of the JAR",metaVar="VAL")
    private String xmlInputPath = "../bball.xml";//"../../Desktop/SpacEx2PHAVer/ZonaTest/examples-2017/ACC/ACCS05/ACCS05-UBD05.xml";
	
	//If the path of the .cfg file is different from the .xml ones.
	@Option(name="-iCFG",usage="The path of the .CFG input file. The path is relative to the location of the JAR",metaVar="VAL")
	private String cfgInputPath = "";
	
	//The output file location. If not specified it will print on std output.
	@Option(name="-o",usage="The path of the output file. If not specified the output stream is the stdout. The path is relative to the location of the JAR",metaVar="VAL")
	private String outputFile = "../test.pha";
	
	//Verbose mode.
	@Option(name="-v", usage="Verbose mode.")
	public static Boolean verbose = true;
	
	public static Boolean noBindFound = false;

	
	public static void main(String[] args) {
		try {
			System.out.println(System.getProperty("user.dir"));
		new Application().run(args);
		}catch(IOException t) {
			t.printStackTrace();
		}

	}
	
	/**
	 * This function start with the class Parser initialization. 
	 * After that, it will init the PrettyPrinter OutputStream in function of -o option.
	 * 
	 * @param args	The option from the command line to be parsed.
	 * 
	 */
	public void run(String[] args) throws IOException {
		parseCommandLine(args);
		if(cfgInputPath.equals(""))
			new Parser(xmlInputPath);
		else 
			new Parser(xmlInputPath,cfgInputPath);
		if(!outputFile.equals("")) {
			File file = new File(outputFile);
			if(!file.exists())
				file.createNewFile();
				PrettyPrinter.out=new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));
		}
		try {
		printMulti("bind", false);
		}catch(SX2PHNoBindException t) {
			t.printErrorMessage();
			PrettyPrinter.printLine("//INFORMATION MESSAGE: NO COMPONENT INSTANCE FOUND.\n"
					+ "// Trying with component declaration.\n"
					+ "// I will set the 'initially' as it is in the .cfg file");
			PrettyPrinter.printNewLine();
			PrettyPrinter.printNewLine();
			try {
				noBindFound=true;
				printMulti("component", true);
			} catch (SX2PHNoBindException e) {
			}
			
			
		}
	}
	
	/**
	 * Main SX2PH converter method. Assumes arguments have been correctly parsed.
	 * 
	 */
	private void printMulti(String element, Boolean recovery) throws SX2PHNoBindException {
		PrettyPrinter pp = new PhaverPrettyPrinter();
		ArrayList<HYaut> automata = new ArrayList<HYaut>();
		//Each bind corresponds to an instance of an automaton. 
		//And so for every bind we will create a new HYautMulti. 
		NodeList nList = Parser.getDocument().getElementsByTagName(element);
		if(nList.getLength()==0 && !recovery) throw new SX2PHNoBindException("No bind component found. I'm trying with component declaration!");
		for (int i=0; i<nList.getLength();++i) {
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE) {
				if(element.equals("bind"))
				automata.add( new HYautMulti(nNode.getAttributes().getNamedItem("component").getNodeValue(),
													 nNode.getAttributes().getNamedItem("as").getNodeValue()));	
				else
					automata.add( new HYautMulti(nNode.getAttributes().getNamedItem("id").getNodeValue(),
							 nNode.getAttributes().getNamedItem("id").getNodeValue()));
					
			}
		}
		if(verbose)
			PrettyPrinter.printSystemInformation(automata);
		for(HYaut hy: automata) {
			//Print with the PhaverPrettyPrinter.
			pp.printAutomaton(hy);
		}
		System.err.println("Translation from SpaceEx to PHAVer complete.");
	}
	
	
	
	private void parseCommandLine(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch( CmdLineException e ) {

            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
            
        }
	}

}