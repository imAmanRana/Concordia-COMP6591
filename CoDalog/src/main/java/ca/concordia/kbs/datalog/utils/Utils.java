package ca.concordia.kbs.datalog.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.concordia.kbs.datalog.constants.Constants;
import ca.concordia.kbs.datalog.syntax.Expression;

public class Utils {

	private Utils() {

	}

	private static final Pattern numberPattern = Pattern.compile("[+-]?\\d+(\\.\\d*)?([Ee][+-]?\\d+)?");

	/**
	 * Reads the file from specified path.
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> readFile(final String filePath) {
		List<String> file = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			file = lines.collect(Collectors.toList());
		} catch (IOException e) {
			System.out.println("Exception in reading file @" + filePath);
		}
		return file;
	}

	/**
	 * Converts an integer to a String
	 * 
	 * @param nval
	 * @return
	 */
	public static String convertNumberToString(final double nval) {
		int i = (int) nval;
		return String.valueOf(i);
	}

	/**
	 * Writes to File specified by path <code>file</code>
	 * 
	 * @param lines
	 * @param file
	 * @throws IOException
	 */
	public static void writeToFile(List<? extends Object> lines, String file) throws IOException {
		writeToFile(lines, file, false);
	}

	public static void writeToFile(List<? extends Object> lines, String file, boolean append) throws IOException {

		Runnable r = () -> {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
				for (Object line : lines) {
					bw.write(line.toString());
					bw.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};

		try {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(r);
			executor.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Checks if the file exists and if <code>emptyFileIfExists</code> is true,
	 * empties the file
	 * 
	 * @param fileType
	 * @param filePath
	 * @param emptyFileIfExists
	 * @return
	 */
	public static String checkAndEmptyFile(final String fileType, final String filePath, boolean emptyFileIfExists) {
		File file = new File(filePath);
		StringBuilder sb = new StringBuilder();
		if (!file.exists()) {
			sb.append(fileType).append(" doesn't exists: ").append(file.toString()).append("\n");
		} else if (emptyFileIfExists) {
			try (FileWriter f = new FileWriter(file)) {
				f.write(Constants.EMPTY_STRING);
			} catch (IOException e) {
				sb.append("Error writing to File @ " + filePath).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Checks if the term in datalog is variable or not
	 * 
	 * @param term
	 * @return
	 */
	public static boolean isVariable(String term) {
		return Character.isUpperCase(term.codePointAt(0));
	}

	/**
	 * 
	 * @param term
	 * @return
	 */
	public static boolean tryParseDouble(String term) {
		return numberPattern.matcher(term).matches();
	}

	/**
	 * 
	 * @param existingFacts
	 * @param newFacts
	 */
	public static void addAllFacts(Map<String, Set<Expression>> existingFacts, Set<Expression> newFacts) {

		if (newFacts == null)
			return;
		Iterator<Expression> itr = newFacts.iterator();
		Expression exp;
		String predicate;
		while (itr.hasNext()) {
			exp = itr.next();
			predicate = exp.getPredicate();
			if (existingFacts.containsKey(predicate)) {
				existingFacts.get(predicate).add(exp);
			} else {
				Set<Expression> factSet = new HashSet<>();
				factSet.add(exp);
				existingFacts.put(predicate, factSet);
			}
		}
	}

	/**
	 * 
	 * @param existingFacts
	 * @param newFacts
	 */
	public static void addAllFacts(Map<String, Set<Expression>> existingFacts, Map<String, Set<Expression>> newFacts) {

		if (newFacts == null)
			return;
		Iterator<String> itr = newFacts.keySet().iterator();
		String predicate;
		while (itr.hasNext()) {
			predicate = itr.next();
			if (existingFacts.containsKey(predicate)) {
				existingFacts.get(predicate).addAll(newFacts.get(predicate));
			} else {
				existingFacts.put(predicate, newFacts.get(predicate));
			}
		}
	}

	public static void printDataStructure(Map<String, Set<Expression>> map) {
		if (map == null)
			return;
		Iterator<String> itr = map.keySet().iterator();
		String predicate;
		while (itr.hasNext()) {
			predicate = itr.next();
			System.out.println(predicate);
			Iterator<Expression> setItr = map.get(predicate).iterator();
			while (setItr.hasNext()) {
				System.out.println(setItr.next());
			}
			System.out.println();
		}
	}

}
