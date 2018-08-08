/**
 * 
 */
package ca.concordia.kbs.datalog.parser;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ca.concordia.kbs.datalog.Codalog;
import ca.concordia.kbs.datalog.constants.Constants;
import ca.concordia.kbs.datalog.exception.CoDalogException;
import ca.concordia.kbs.datalog.exception.ExceptionHandler;
import ca.concordia.kbs.datalog.syntax.BuiltInOperator;
import ca.concordia.kbs.datalog.syntax.Expression;
import ca.concordia.kbs.datalog.syntax.Rule;
import ca.concordia.kbs.datalog.syntax.Statement;
import ca.concordia.kbs.datalog.syntax.StatementType;
import ca.concordia.kbs.datalog.utils.Utils;

/**
 * @author AmanRana
 *
 */
public class Parser {

	private Parser() {

	}

	/**
	 * Custom stream tokenizer for datalog program
	 * 
	 * @param rdr
	 * @return tokenizer
	 */
	public static StreamTokenizer getCustomTokenizer(StreamTokenizer scanner, String line) {
		scanner = new StreamTokenizer(new StringReader(line));
		scanner.ordinaryChar('.');
		scanner.wordChars('_', '_');
		scanner.commentChar('%');
		scanner.quoteChar('"');
		scanner.quoteChar('\'');
		return scanner;
	}

	/**
	 * Parse single line from the input file
	 * @param scanner
	 * @param lineNo
	 * @throws CoDalogException
	 */
	public static void parseLine(StreamTokenizer scanner, int lineNo) throws CoDalogException {

		try {
			Statement stmt = parseStatement(scanner);
			validateStatement(stmt);
		} catch (CoDalogException e) {
			ExceptionHandler.errors.add(String.format(Constants.ERROR_LINE_NO, lineNo + 1, e.getMessage()));
			while (scanner.ttype != StreamTokenizer.TT_EOF) {
				try {
					scanner.nextToken();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * Parse Console Queries after fixed point evaluation
	 * @param scanner
	 * @param gls
	 * @return
	 * @throws CoDalogException
	 */
	public static List<Expression> parseConsoleQueries(StreamTokenizer scanner,List<Expression> gls) throws CoDalogException {
		Expression head = parseExpression(scanner);
		try {
			scanner.nextToken();
			// query
			gls.clear();
			gls.add(head);
			if (scanner.ttype != '?' && scanner.ttype != ',') {
				throw new CoDalogException("Expected '?' or ',' ");
			}

			while (scanner.ttype == ',') {
				gls.add(parseExpression(scanner));
				scanner.nextToken();
			}
		} catch (IOException e) {
			throw new CoDalogException(e);
		}
		return gls;
	}

	/**
	 * Parse statement into facts, rules or queries
	 * @param scanner
	 * @return
	 * @throws CoDalogException
	 */
	private static Statement parseStatement(final StreamTokenizer scanner) throws CoDalogException {
		List<Expression> gls = new ArrayList<>();

		Expression head = parseExpression(scanner);

		try {
			if (scanner.nextToken() == ':') {
				// rule
				if (scanner.nextToken() != '-') {
					throw new CoDalogException("Expected :- ");
				}

				List<Expression> body = new ArrayList<>();
				do {
					Expression bodyTerm = parseExpression(scanner);
					body.add(bodyTerm);
				} while (scanner.nextToken() == ',');

				// end of rule
				if (scanner.ttype != '.') {
					throw new CoDalogException("Expected . after a rule ");
				}
				Rule newRule = new Rule(head, Codalog.getInstance().getEngine().reorder(body));
				return new Statement(newRule, StatementType.RULE);
			} else {
				// its a fact or a query
				if (scanner.ttype == '.') {
					// fact
					return new Statement(head, StatementType.FACT);
				} else {
					// query
					gls.clear();
					gls.add(head);
					if (scanner.ttype != '.' && scanner.ttype != '?' && scanner.ttype != ',') {
						throw new CoDalogException("Expected '?' or ',' or '.' ");
					}
				}
				while (scanner.ttype == ',') {
					gls.add(parseExpression(scanner));
					scanner.nextToken();
				}

				if (scanner.ttype == '?') {
					return new Statement(gls, StatementType.QUERY);
				} else {
					throw new CoDalogException("Expected '?'");
				}
			}
		} catch (IOException e) {
			throw new CoDalogException(e);
		}
	}

	/**
	 * Parse Expression
	 * @param scanner
	 * @return
	 * @throws CoDalogException
	 */
	private static Expression parseExpression(final StreamTokenizer scanner) throws CoDalogException {

		try {
			scanner.nextToken();

			String leftHandSide;
			boolean negation = false;
			boolean builtIn = false;

			if ((scanner.ttype == StreamTokenizer.TT_WORD && scanner.sval.equalsIgnoreCase(Constants.NOT))
					|| (scanner.ttype == '~')) {
				negation = true;
				scanner.nextToken();
			}

			if (scanner.ttype == StreamTokenizer.TT_WORD) {
				leftHandSide = scanner.sval;
			} else if (scanner.ttype == StreamTokenizer.TT_NUMBER) {
				leftHandSide = Utils.convertNumberToString(scanner.nval);
				builtIn = true;
			} else if (scanner.ttype == '"' || scanner.ttype == '\'') {
				leftHandSide = scanner.sval;
				builtIn = true;
			} else {
				throw new CoDalogException("Expected predicate");
			}
			scanner.nextToken();

			if (scanner.ttype == StreamTokenizer.TT_WORD || scanner.ttype == '>' || scanner.ttype == '='
					|| scanner.ttype == '<' || scanner.ttype == '!') {
				scanner.pushBack();
				Expression exp = parseBuiltIn(leftHandSide, scanner);
				exp.setNegation(negation);
				return exp;
			}

			if (builtIn) {
				throw new CoDalogException("BuiltIn expected");
			} else if (scanner.ttype != '(') {
				throw new CoDalogException("Expected '(' after predicate or an operator");
			}

			List<String> terms = new ArrayList<>();
			if (scanner.nextToken() != ')') {
				scanner.pushBack();
				do {
					if (scanner.nextToken() == StreamTokenizer.TT_WORD) {
						terms.add(scanner.sval);
					} else if (scanner.ttype == '"' || scanner.ttype == '\'') {
						terms.add("\"" + scanner.sval);
					} else if (scanner.ttype == StreamTokenizer.TT_NUMBER) {
						terms.add(Utils.convertNumberToString(scanner.nval));
					} else {
						throw new CoDalogException("Expected term");
					}
				} while (scanner.nextToken() == ',');
				if (scanner.ttype != ')') {
					throw new CoDalogException("Expected )");
				}
			}
			Expression e = new Expression(leftHandSide, terms);
			e.setNegation(negation);
			return e;

		} catch (IOException e) {
			throw new CoDalogException(e);
		}

	}

	/**
	 * Parse Built-In
	 * @param leftHandSide
	 * @param scanner
	 * @return
	 * @throws CoDalogException
	 */
	private static Expression parseBuiltIn(final String leftHandSide, final StreamTokenizer scanner)
			throws CoDalogException {

		String op;
		try {
			scanner.nextToken();

			op = (char) scanner.ttype + Constants.EMPTY_STRING;
			scanner.nextToken();
			if (scanner.ttype == '>' || scanner.ttype == '=') {
				op = op + (char) scanner.ttype;
			} else {
				scanner.pushBack();
			}

			if (!isOperatorValid(op)) {
				throw new CoDalogException("Invalid Operator : " + op);
			}

			String rightHandSide = null;
			scanner.nextToken();

			if (scanner.ttype == StreamTokenizer.TT_NUMBER) {
				rightHandSide = Double.toString(scanner.nval);
			} else if (scanner.ttype == '\'' || scanner.ttype == '"') {
				rightHandSide = scanner.sval;
			} else if (scanner.ttype == StreamTokenizer.TT_WORD) {
				rightHandSide = scanner.sval;
			} else {
				throw new CoDalogException("RHS of expression expected");
			}
			return new Expression(op, leftHandSide, rightHandSide);
		} catch (IOException e) {
			throw new CoDalogException(e);
		}
	}

	/**
	 * check for valid builtIn operator
	 * @param op
	 * @return
	 */
	private static boolean isOperatorValid(String op) {
		// check for valid operators
		for (BuiltInOperator bio : BuiltInOperator.values()) {
			if (bio.getValue().equalsIgnoreCase(op)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Validate statement(fact,rule,query) and stores it in in-memory database
	 * @param stmt
	 * @throws CoDalogException
	 */
	private static void validateStatement(Statement stmt) throws CoDalogException {
		stmt.validateAndStore();
	}

}
