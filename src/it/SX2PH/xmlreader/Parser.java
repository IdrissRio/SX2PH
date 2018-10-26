package it.SX2PH.xmlreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class Parser {
	private String inputPath;
	private String cfgPath;
	private File xmlFile;
	private static Configuration config;
	private static Document doc;
	
	public Parser(String xmlInputPath){
		Parser.config= new Configuration();
		this.inputPath=xmlInputPath;
		this.cfgPath=xmlInputPath.replace(".xml", ".cfg");
		createTree();
	}
	
	public Parser(String xmlInputPath, String cfgInputPath){
		Parser.config= new Configuration();
		this.inputPath=xmlInputPath;
		this.cfgPath=cfgInputPath;
		createTree();
	}
	
	
	

	public static Configuration getConfig() {
		return config;
	}
	
	public static Document getDocument() {
		return doc;
	}
	
	private void createTree() {
		try {
		xmlFile = new File(inputPath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();	
		parseCFG();
		}catch(FileNotFoundException t) {
			System.out.println("FILE .XML or .CGF not found");
			t.printStackTrace();
		}catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	
	
	
	private void parseCFG()
	{
		if (cfgPath != null)
		{
			String line;
			try
			{
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(new FileReader(cfgPath));
			while ((line = bufferedReader.readLine()) != null)
				{
					int commentPos = line.indexOf("#");
					if (commentPos != -1)
						line = line.substring(0, commentPos);

					int eqPos = line.indexOf("=");
					if (eqPos > 0)
					{
						String property = line.substring(0, eqPos).trim().toLowerCase();

						String value = line.substring(eqPos + 1);

						int quoteIndex = value.indexOf("\"");

						// there was an open quote... but no end quote
						if (quoteIndex != -1 && value.indexOf("\"", quoteIndex + 1) == -1)
						{
							// keep reading lines until the end quote
							while ((line = bufferedReader.readLine()) != null)
							{
								commentPos = line.indexOf("#"); // trim comments
								if (commentPos > 0)
									line = line.substring(0, commentPos - 1);

								value += " " + line;

								quoteIndex = line.indexOf("\"");

								if (quoteIndex != -1)
									break;
							}

							if (quoteIndex == -1)
								throw new RuntimeException(
										"Quoted multi-line property in .cfg file did not have "
												+ "end quote: " + property);
						}

						value = value.trim().replace("\"", "");

						if (property.equals("system"))
							config.setSystemID(value);
						else if (property.equals("initially"))
						{
							config.setInitalState(removeLoc(value));
						}
						else if (property.equals("forbidden") && value.trim().length() > 0)
						{
							config.setForbiddenState(removeLoc(value));
						}
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	private String removeLoc(String t) {
		try {
		Integer firstOccOfLoc = t.indexOf("loc(");
		t=t.replace("loc(","");
		Integer rightPar = t.indexOf(")",firstOccOfLoc);
		StringBuilder temp = new StringBuilder(t);
		temp.deleteCharAt(rightPar);
		return removeLoc(temp.toString());}
		catch(Throwable e) {
			return t;
		}
	}
	
	
	
	
	
	
}
