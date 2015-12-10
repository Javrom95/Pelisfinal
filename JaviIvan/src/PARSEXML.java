
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PARSEXML {

	Visual visual = new Visual();
	RESTAPI api = new RESTAPI();
	private Scanner sc = new Scanner(System.in);
	private String str;
	private char option;
	private String id;
	private boolean check = true;
	private boolean nodecheck = true;

	/**
	 * Gets the online data to see it obtained previously in RESTAPI.
	 * 
	 * @param doc
	 * @return
	 */
	public Visual getDataToSee(Document doc) {
		try {
			NodeList movieList = doc.getElementsByTagName("movie");
			Node p = movieList.item(0);
			if(p==null){
				nodecheck=false;
			}
			if (p.getNodeType() == Node.ELEMENT_NODE) {
				Element movie = (Element) p;

				visual.setTitle(movie.getAttribute("title"));
				visual.setType(movie.getAttribute("type"));
				visual.setDate(movie.getAttribute("year"));
				visual.setLenght(movie.getAttribute("runtime"));
				visual.setGenre(movie.getAttribute("genre"));
				visual.setSynopsis(movie.getAttribute("plot"));
				visual.setLanguage(movie.getAttribute("language"));
				visual.setDirector(movie.getAttribute("director"));
				visual.setActors(movie.getAttribute("actors"));
			}

		} catch (NullPointerException e) {
			System.out.println("Error 404. File Not Found.");
		}

		return visual;
	}

	/**
	 * Gets the online data to write it.
	 * 
	 * @param doc
	 * @param type
	 */
	public void getDataToWrite(Document doc, String type, String name) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		String loc = null;
		try {
			// 2 Documents: doc to get the data online, and docu where we will
			// write the data.
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document docu = builder.newDocument();
			Element root = docu.createElement(name.toLowerCase());
			docu.appendChild(root);

			NodeList movieList = doc.getElementsByTagName("movie");
			Node p = movieList.item(0);
			
			// Sets the attributes of the new data from the ones gathered from
			// the document doc.
			if (p.getNodeType() == Node.ELEMENT_NODE) {
				Element movie = (Element) p;
				root.setAttribute("title", movie.getAttribute("title"));
				root.setAttribute("type", movie.getAttribute("type"));
				root.setAttribute("year", movie.getAttribute("year"));
				root.setAttribute("runtime", movie.getAttribute("runtime"));
				root.setAttribute("genre", movie.getAttribute("genre"));
				root.setAttribute("plot", movie.getAttribute("plot"));
				root.setAttribute("language", movie.getAttribute("language"));
				root.setAttribute("actors", movie.getAttribute("actors"));
				root.setAttribute("director", movie.getAttribute("director"));
			}

			DOMSource source = new DOMSource(docu);

			// The program understands where yo want to do the action.
			if (type.equals("series")) {
				loc = "resources/series.xml";
			} else if (type.equals("movies")) {
				loc = "resources/movies.xml";
			}

			Result result = new StreamResult(loc);

			TransformerFactory transf = TransformerFactory.newInstance();
			Transformer transformer = transf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);

			System.out.println("Archivo escrito.");

		} catch (Exception ex) {
			System.out.println("Please, write a correct database name.");
			// If the database name is incorrect, the method will launch again.
			getDataToWrite(api.getDocu(), api.enterType(), api.enterName());
		}

	}

	/**
	 * Reads the written data from a specific database and a specific item.
	 * 
	 * @param type
	 * @param name
	 * @throws IOException
	 */
	public Visual readWrittenData(String type, String name) throws IOException {
		String id;
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Decides from where you want to read.
			if (type.equals("series")) {
				doc = builder.parse("resources/series.xml");
			} else if (type.equals("movies")) {
				doc = builder.parse("resources/movies.xml");
			}

			NodeList personList = doc.getElementsByTagName(name);
			for (int i = 0; i < personList.getLength(); i++) {
				Node p = personList.item(i);
				if(p==null){
					nodecheck=false;
				}
				if (p.getNodeType() == Node.ELEMENT_NODE) {
					Element movie = (Element) p;
					// Gets the attribute from the specified element.
					visual.setTitle(movie.getAttribute("title"));
					visual.setType(movie.getAttribute("type"));
					visual.setDate(movie.getAttribute("year"));
					visual.setLenght(movie.getAttribute("runtime"));
					visual.setGenre(movie.getAttribute("genre"));
					visual.setSynopsis(movie.getAttribute("plot"));
					visual.setLanguage(movie.getAttribute("language"));
					visual.setDirector(movie.getAttribute("director"));
					visual.setActors(movie.getAttribute("actors"));

					System.out.println("Archivo leido.");
				}

			}

		} catch (Exception ex) {
			System.out.println("Please, write a correct database name.");
			//If the database´s name isn´t correct, the method will launch again.
			readWrittenData(api.lookForType(), api.lookForName());
		}

		return visual;
	}

	
	/**
	 * This method activates when the input in the option´s menu isn´t correct.
	 * @return
	 */
	public boolean getOption() {
		try {
			//The loop with the check boolean tells if the entry is still incorrect and launchs again the method.
			while (check) {
				str = sc.next();
				if (Integer.parseInt(str) <= 0 || Integer.parseInt(str) > 3) {
					System.out.println("Please, enter a correct option number.");
				} else {
					check = false;
				}
			}
		} catch (Exception e) {
			System.out.println("Your entry wasn´t a number.");
			System.out.println("Please, enter a correct option number..");
		}
		return check;
	}

	/**
	 * Secondary menu, to specify if you want to go back to the main menu or exit the program.
	 */
	public void submenu() {
		System.out.println("-------------------------");
		System.out.println("Going back to menu? Y/N :");
		str = sc.next();
		if (str.equals("Y") || str.equals("y"))
			menu();
		else if (str.equals("N") || str.equals("n"))
			System.exit(0);
	}

	/**
	 * If the nodes are empty, it will stop from showing null information to the user.
	 * @return
	 */
	public boolean checkNode() {
		return nodecheck;
	}

	/**
	 * Here is the main menu, where the user will interact.
	 */
	public void menu() {

		System.out.println("Insert the option number to execute it:");
		System.out.println("-------------------------");
		System.out.println("1 -Get data online and read it:");
		System.out.println("2 -Get online data and write it into the local database:");
		System.out.println("3 -Look for a specific record in the local database:");
		while (check) {
			getOption();
		}

		switch (str) {

		case "1": {
			Visual visual;

			visual = getDataToSee(api.transformXML());
			if (checkNode()) {
				System.out.println(visual.toString());
				System.out.println("-------------------------");
				System.out.println("Want to save it? Y/N :");
				str = sc.next();
				if (str.equals("Y") || str.equals("y")) {
					System.out.println("Info saved.");
					System.out.println("");
					submenu();
				} else if (str.equals("N") || str.equals("n")) {
					submenu();
				}
			} else {
				submenu();
			}
			submenu();
		}

		case "2": {

			getDataToSee(api.transformXML());
			if (checkNode()) {
				System.out.println(
						"Received data: Now choose in what database you want it, and the name of the register:");
				getDataToWrite(api.getDocu(), api.enterType(), api.enterName());

				submenu();
			} else {
				submenu();
			}
		}

		case "3": {
			Visual visual = null;
			try {
				
				visual = readWrittenData(api.lookForType(), api.lookForName());
				if(checkNode()){	    
				System.out.println(visual.toString());
				}else{
					System.out.println("Sorry, couldn´t find the data.");
				}
			} catch (IOException e) {
				System.out.println("Error. The file that you were trying to read doesn´t exist.");
				submenu();
			}
			submenu();
		}

		}

	}

}
