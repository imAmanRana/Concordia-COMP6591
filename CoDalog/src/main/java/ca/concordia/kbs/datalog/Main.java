package ca.concordia.kbs.datalog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import ca.concordia.kbs.datalog.constants.Constants;
import ca.concordia.kbs.datalog.engine.EngineType;
import ca.concordia.kbs.datalog.exception.CoDalogException;
import ca.concordia.kbs.datalog.exception.ExceptionHandler;
import ca.concordia.kbs.datalog.parser.Parser;
import ca.concordia.kbs.datalog.syntax.Expression;
import ca.concordia.kbs.datalog.syntax.Rule;
import ca.concordia.kbs.datalog.utils.Utils;

/**
 * Starting point for the <b>CoDalog</b> Project
 * @author AmanRana
 */
public class Main {

	private static Main instance = new Main();

	/** properties file for CoDalog */
	public final Properties prop = new Properties();

	public final List<String> program = new ArrayList<>();

	// singleton object
	public static Main getInstance() {
		return instance;
	}

	/**
	 * @param args
	 * @throws CoDalogException
	 */
	public static void main(String[] args) throws CoDalogException {
		Main obj = getInstance();
		obj.startProcessing();
	}

	private void startProcessing() throws CoDalogException {

		initializeSystemProperties();
		readInputFile();
		readInTokens();
		storeCodeListing();
		if (ExceptionHandler.errors.isEmpty()) {
			evaluateProgram();
			askUserForQueries();
			System.out.println("\n\nThank You for using CoDalog System.");
			System.out.println("The fixed point is stored in P.res file@ "+prop.getProperty(Constants.OUTPUT_RESULT_FILE));
		} else {
			try {
				Utils.writeToFile(ExceptionHandler.errors, prop.getProperty(Constants.OUTPUT_ERRORS_FILE));
			} catch (IOException e) {
				System.out.println("I/O Exception " + e.getMessage());
			}
			System.out.println(
					"Program has errors, please see the P.err file @" + prop.getProperty(Constants.OUTPUT_ERRORS_FILE));
		}

	}

	private void askUserForQueries() {
		Scanner sc = new Scanner(System.in);
		List<List<Expression>> listOfGoals = new ArrayList<>();
		List<Expression> goals = null;
		System.out.print("All facts have been derived, Do you wanna query something(y/n) : ");
		char c;
		c = sc.next().charAt(0);
		while ('Y' == c || 'y' == c) {
			goals = new ArrayList<>();
			listOfGoals = new ArrayList<>();
			sc.nextLine();
			String query;
			if ('Y' == c || 'y' == c) {
				System.out.println("Input Query[eg - query(a,X)?] : ");
				query = sc.nextLine();
				StreamTokenizer tokenizer = null;
				tokenizer = Parser.getCustomTokenizer(tokenizer, query);
				try {
					tokenizer.nextToken();

					while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
						tokenizer.pushBack();
						Parser.parseConsoleQueries(tokenizer, goals);
						tokenizer.nextToken();
					}

					listOfGoals.add(goals);
					System.out.println("-----------------------------");
					outputQueryResults(Codalog.getInstance().evaluateQueries(listOfGoals), false);
					System.out.println("-----------------------------");
				} catch (Exception e) {
					System.out.println(String.format(e.getMessage()));
				}
			}
			
			System.out.print("\nDo you wanna query more(y/n) : ");
			c = sc.next().charAt(0);
		}
		sc.close();
	}

	private void storeCodeListing() {
		List<Rule> rules = Codalog.getInstance().getIdb().getIdb();
		try {
			Utils.writeToFile(rules, prop.getProperty(Constants.CODELISTING_FILE));
		} catch (IOException e) {
			System.out.println("I/O Exception " + e.getMessage());
		}

	}

	private void readInputFile() throws CoDalogException {
		try (BufferedReader reader = new BufferedReader(new FileReader(prop.getProperty("INPUT_FILE_PATH")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				program.add(line);
			}
		} catch (FileNotFoundException e) {
			throw new CoDalogException("System Error, P.cdl file not found.");
		} catch (IOException e) {
			throw new CoDalogException("Syster Erro, IO Exception.");
		}
	}

	private void readInTokens() throws CoDalogException {
		StreamTokenizer scanner = null;
		try {
			for (int i = 0; i < program.size(); i++) {
				scanner = Parser.getCustomTokenizer(scanner, program.get(i));
				scanner.nextToken();
				while (scanner.ttype != StreamTokenizer.TT_EOF) {
					scanner.pushBack();
					Parser.parseLine(scanner, i);
					scanner.nextToken();
				}
			}

		} catch (IOException e) {
			System.err.println("Error in reading tokens");
		}

	}

	private void evaluateProgram() throws CoDalogException {
		Codalog.getInstance().generateNewFacts();
		storeFixedPoint();
		Codalog cdg = Codalog.getInstance();
		Map<List<Expression>, Collection<Map<String, String>>> results = cdg.evaluateQueries(cdg.getQdb().getQuery());
		outputQueryResults(results, true);
	}

	private void storeFixedPoint() {

		List<String> fixedPoint = Codalog.getInstance().getEdb().getAllFacts().stream()
				.map(fact -> fact.toString() + ".").collect(Collectors.toList());
		try {
			Utils.writeToFile(fixedPoint, prop.getProperty(Constants.OUTPUT_RESULT_FILE));
		} catch (IOException e) {
			System.out.println("I/O Exception " + e.getMessage());
		}

	}

	private void outputQueryResults(Map<List<Expression>, Collection<Map<String, String>>> results,
			boolean storeInFile) {
		List<String> output = new ArrayList<>();
		try {
			Iterator<List<Expression>> itr = results.keySet().iterator();
			List<Expression> query;
			Collection<Map<String, String>> answers;
			while (itr.hasNext()) {
				query = itr.next();
				answers = results.get(query);
				System.out.println(query);
				output.add(query.toString());
				if (!answers.isEmpty()) {
					if (answers.iterator().next().isEmpty()) {
						System.out.println("  true");
						output.add(" true.");
					} else {
						for (Map<String, String> answer : answers) {
							System.out.println("  " + (answer));
							output.add("  " + (answer));
						}
					}
				} else {
					System.out.println("  false");
					output.add(" false.");
				}
			}

			if (storeInFile)
				Utils.writeToFile(output, prop.getProperty(Constants.OUTPUT_QUERY_RESULT_TO_FILE));

		} catch (FileNotFoundException e) {
			System.out.println("Output file P.output not found");
		} catch (IOException e) {
			System.out.println("IO Erro occured while writing to output file");
		}

	}

	/**
	 * read the application.properties file
	 */
	private boolean initializeSystemProperties() {

		ClassLoader classLoader = getClass().getClassLoader();
		try (InputStream input = new FileInputStream(classLoader.getResource("application.properties").getFile())) {
			// load a properties file
			prop.load(input);
		} catch (FileNotFoundException e) {
			System.err.println("application.properties file not found");
		} catch (IOException e) {
			System.err.println("I/O Exception");
		}
		return checkForErrors();
	}

	private boolean checkForErrors() {
		boolean error = false;
		StringBuilder sb = new StringBuilder();
		sb.append(
				Utils.checkAndEmptyFile(Constants.INPUT_FILE_PATH, prop.getProperty(Constants.INPUT_FILE_PATH), false));
		sb.append(Utils.checkAndEmptyFile(Constants.CODELISTING_FILE, prop.getProperty(Constants.CODELISTING_FILE),
				true));
		sb.append(Utils.checkAndEmptyFile(Constants.OUTPUT_ERRORS_FILE, prop.getProperty(Constants.OUTPUT_ERRORS_FILE),
				true));
		sb.append(Utils.checkAndEmptyFile(Constants.OUTPUT_QUERY_RESULT_TO_FILE,
				prop.getProperty(Constants.OUTPUT_QUERY_RESULT_TO_FILE), true));
		sb.append(Utils.checkAndEmptyFile(Constants.OUTPUT_INTERMEDIATE_RESULT_FILE,
				prop.getProperty(Constants.OUTPUT_INTERMEDIATE_RESULT_FILE), true));
		sb.append(Utils.checkAndEmptyFile(Constants.OUTPUT_RESULT_FILE,
				prop.getProperty(Constants.OUTPUT_RESULT_FILE), true));

		if (sb.length() > 0)
			error = true;

		String engineName = prop.getProperty(Constants.EVALUATION_ENGINE);
		boolean match = false;
		for (EngineType et : EngineType.values()) {
			if (et.getEngineType().equals(engineName)) {
				match = true;
				break;
			}
		}
		if (!match) {
			error = true;
			sb.append(Constants.EVALUATION_ENGINE + "[" + engineName
					+ "] doesn't matches any one of the required values: ");
			for (EngineType et : EngineType.values()) {
				sb.append(et.getEngineType() + " ");
			}
			sb.append("\n");
		}
		if (error) {
			System.out.println(sb.toString());
		}
		return error;
	}

	public Properties getProp() {
		return prop;
	}
}
