/**
 * 
 */
package ca.concordia.kbs.datalog.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ca.concordia.kbs.datalog.constants.Constants;
import ca.concordia.kbs.datalog.utils.Utils;

/**
 * @author AmanRana
 *
 */
public class Expression implements DatalogElement {

	private String predicate;
	private List<String> terms;
	private boolean negation;

	/**
	 * @param predicate
	 * @param terms
	 */
	public Expression(String predicate, List<String> terms) {
		super();
		this.predicate = predicate;
		this.terms = terms;
	}

	public Expression(String predicate, String... terms) {
		this(predicate, Arrays.asList(terms));
	}

	public Expression(String predicate, boolean query, String... terms) {
		this(predicate, Arrays.asList(terms));
	}

	/**
	 * @return the predicate
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * @param predicate
	 *            the predicate to set
	 */
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	/**
	 * @return the terms
	 */
	public List<String> getTerms() {
		return terms;
	}

	/**
	 * @param terms
	 *            the terms to set
	 */
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	/**
	 * @return the negation
	 */
	public boolean isNegation() {
		return negation;
	}

	/**
	 * @param negation
	 *            the negation to set
	 */
	public void setNegation(boolean negation) {
		this.negation = negation;
	}

	public int getNoOfArguments() {
		return terms.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.negation) {
			sb.append("not ");
		}
		if (this.isBuiltIn()) {
			sb.append('(');
			sb.append(this.getTerms().get(0));
			sb.append(Constants.SINGLE_SPACE_STRING);
			sb.append(this.predicate);
			sb.append(Constants.SINGLE_SPACE_STRING);
			sb.append(this.getTerms().get(1));
			sb.append(')');
		} else {
			sb.append(this.predicate).append('(');
			for (int i = 0; i < this.terms.size(); i++) {
				sb.append(terms.get(i));
				if (i < terms.size() - 1) {
					sb.append(',');
				}
			}
			sb.append(')');
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Expression)) {
			return false;
		}
		Expression that = ((Expression) other);
		if (!this.predicate.equals(that.predicate)) {
			return false;
		}
		if (getNoOfArguments() != that.getNoOfArguments() || negation != that.negation) {
			return false;
		}
		for (int i = 0; i < terms.size(); i++) {
			if (!terms.get(i).equals(that.terms.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = predicate.hashCode();
		for (String term : terms) {
			hash += term.hashCode();
		}
		return hash;
	}

	public boolean isGroundFact() {
		for (String term : terms) {
			if (Utils.isVariable(term)) {
				return false;
			}
		}
		return true;
	}

	public boolean isBuiltIn() {
		return !Character.isLetterOrDigit(predicate.charAt(0)) && (predicate.charAt(0) != '\"');
	}

	public boolean performUnification(Expression exp, Map<String, String> bindings) {
		if (!this.predicate.equals(exp.predicate) || this.getNoOfArguments() != exp.getNoOfArguments()) {
			return false;
		}
		String term1;
		String term2;
		for (int i = 0; i < this.getNoOfArguments(); i++) {
			term1 = this.terms.get(i);
			term2 = exp.terms.get(i);
			if (Utils.isVariable(term1)) {
				if (!term1.equals(term2)) {
					if (!bindings.containsKey(term1)) {
						bindings.put(term1, term2);
					} else if (!bindings.get(term1).equals(term2)) {
						return false;
					}
				}
			} else if (Utils.isVariable(term2)) {
				if (!bindings.containsKey(term2)) {
					bindings.put(term2, term1);
				} else if (!bindings.get(term2).equals(term1)) {
					return false;
				}
			} else if (!term1.equals(term2)) {
				return false;
			}
		}
		return true;
	}

	public Expression substitute(Map<String, String> bindings) {
		Expression exp = new Expression(this.predicate, new ArrayList<>());
		exp.setNegation(negation);
		for (String term : this.terms) {
			String value;
			if (Utils.isVariable(term)) {
				value = bindings.get(term);
				if (value == null) {
					value = term;
				}
			} else {
				value = term;
			}
			exp.terms.add(value);
		}
		return exp;
	}

	public boolean evalBuiltIn(Map<String, String> bindings) {
		String term1 = terms.get(0);
		if (Utils.isVariable(term1) && bindings.containsKey(term1))
			term1 = bindings.get(term1);
		String term2 = terms.get(1);
		if (Utils.isVariable(term2) && bindings.containsKey(term2))
			term2 = bindings.get(term2);
		if (predicate.equals("=")) {
			if (Utils.isVariable(term1)) {
				if (Utils.isVariable(term2)) {
					throw new RuntimeException("Unbound Operands (" + term1 + ", " + term2 + ") in " + this);
				}
				bindings.put(term1, term2);
				return true;
			} else if (Utils.isVariable(term2)) {
				bindings.put(term2, term1);
				return true;
			} else {
				if (Utils.tryParseDouble(term1) && Utils.tryParseDouble(term2)) {
					double d1 = Double.parseDouble(term1);
					double d2 = Double.parseDouble(term2);
					return d1 == d2;
				} else {
					return term1.equals(term2);
				}
			}
		} else {
			try {
				if (Utils.isVariable(term1) || Utils.isVariable(term2)) {
					throw new RuntimeException("Unbound variable in evaluation of " + this);
				}

				if (predicate.equals("<>") || predicate.equals("!=")) {
					if (Utils.tryParseDouble(term1) && Utils.tryParseDouble(term2)) {
						double d1 = Double.parseDouble(term1);
						double d2 = Double.parseDouble(term2);
						return d1 != d2;
					} else {
						return !term1.equals(term2);
					}
				} else {
					double d1 = 0.0, d2 = 0.0;
					if (Utils.tryParseDouble(term1)) {
						d1 = Double.parseDouble(term1);
					}
					if (Utils.tryParseDouble(term2)) {
						d2 = Double.parseDouble(term2);
					}
					switch (predicate) {
					case "<":
						return d1 < d2;
					case "<=":
						return d1 <= d2;
					case ">":
						return d1 > d2;
					case ">=":
						return d1 >= d2;
					}
				}
			} catch (NumberFormatException e) {
				throw new RuntimeException("parsing double value failed : ", e);
			}
		}
		throw new RuntimeException("Built-In predicate not implemented: " + predicate);
	}

}
