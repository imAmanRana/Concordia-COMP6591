/**
 * 
 */
package ca.concordia.kbs.datalog.syntax;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ca.concordia.kbs.datalog.exception.CoDalogException;
import ca.concordia.kbs.datalog.utils.Utils;

/**
 * @author AmanRana
 *
 */
public class Rule implements DatalogElement {

	private Expression head;
	private List<Expression> body;

	/**
	 * @param head
	 * @param body
	 */
	public Rule(Expression head, List<Expression> body) {
		super();
		this.head = head;
		this.body = body;
	}

	/**
	 * @return the head
	 */
	public Expression getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(Expression head) {
		this.head = head;
	}

	/**
	 * @return the body
	 */
	public List<Expression> getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(List<Expression> body) {
		this.body = body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.head);
		sb.append(":-");
		for (int i = 0; i < this.body.size(); i++) {
			sb.append(body.get(i));
			if (i < body.size() - 1) {
				sb.append(',');
			}
		}
		return sb.toString();
	}

	public void validate() throws CoDalogException {

		Set<String> ruleBodyVariables = new LinkedHashSet<>();
		for (Expression exp : getBody()) {
			if (exp.isBuiltIn()) {
				// check the size of terms, it should be 2
				if (exp.getTerms().size() != 2) {
					throw new CoDalogException("more than 2 operands for buildIn operator : " + exp.getPredicate());
				}

				if (exp.getPredicate().equals(BuiltInOperator.EQUAL.getValue())) {
					if (Utils.isVariable(exp.getTerms().get(0)) && Utils.isVariable(exp.getTerms().get(1))
							&& !ruleBodyVariables.contains(exp.getTerms().get(0))
							&& !ruleBodyVariables.contains(exp.getTerms().get(1))) {
						throw new CoDalogException("Unbound variables[" + exp.getTerms().get(0) + ","
								+ exp.getTerms().get(1) + "] for '='");
					}
				} else {
					if (Utils.isVariable(exp.getTerms().get(0)) && !ruleBodyVariables.contains(exp.getTerms().get(0))) {
						throw new CoDalogException(exp.getTerms().get(0) + " variable is unbound in " + exp);
					}
					if (Utils.isVariable(exp.getTerms().get(1)) && !ruleBodyVariables.contains(exp.getTerms().get(1))) {
						throw new CoDalogException(exp.getTerms().get(1) + " variable is unbound in " + exp);
					}
				}
			}
			if (exp.isNegation()) {
				for (String term : exp.getTerms()) {
					if (Utils.isVariable(term) && !ruleBodyVariables.contains(term)) {

						StringBuilder sb = new StringBuilder();
						sb.append("Variable ").append(term).append(" of rule").append(toString())
								.append(" should appear in 1 +ve expression at least once");
						throw new CoDalogException(sb.toString());
					}
				}
			} else {
				for (String term : exp.getTerms()) {
					if (Utils.isVariable(term)) {
						ruleBodyVariables.add(term);
					}
				}
			}
		}

		// variable in head should appear in the rule body
		for (String term : getHead().getTerms()) {
			if (!Utils.isVariable(term)) {
				throw new CoDalogException(toString() + ", head contains constant term: " + term);
			}

			if (!ruleBodyVariables.contains(term)) {
				throw new CoDalogException(toString() + ", variable[" + term + "] in head is not in the body");
			}
		}

	}

}
