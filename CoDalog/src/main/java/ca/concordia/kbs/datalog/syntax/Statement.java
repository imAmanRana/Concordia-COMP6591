/**
 * 
 */
package ca.concordia.kbs.datalog.syntax;

import java.util.List;

import ca.concordia.kbs.datalog.Codalog;
import ca.concordia.kbs.datalog.constants.Constants;
import ca.concordia.kbs.datalog.exception.CoDalogException;

/**
 * @author AmanRana
 *
 */
public class Statement {
	
	private final Expression fact;
	private final Rule rule;
	private final List<Expression> goals;
	private final StatementType stmtType;
	
	/**
	 * @param fact
	 * @param stmtType
	 */
	public Statement(Expression fact, StatementType stmtType) {
		super();
		this.fact = fact;
		this.stmtType = stmtType;
		this.rule = null;
		this.goals = null;
	}

	/**
	 * @param rule
	 * @param stmtType
	 */
	public Statement(Rule rule, StatementType stmtType) {
		super();
		this.rule = rule;
		this.stmtType = stmtType;
		this.fact = null;
		this.goals = null;
	}

	/**
	 * @param goals
	 * @param stmtType
	 */
	public Statement(List<Expression> goals, StatementType stmtType) {
		super();
		this.goals = goals;
		this.stmtType = stmtType;
		this.fact = null;
		this.rule = null;
	}

	/**
	 * @return the fact
	 */
	public Expression getFact() {
		return fact;
	}

	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * @return the stmtType
	 */
	public StatementType getStmtType() {
		return stmtType;
	}
	
	
	public void validateAndStore() throws CoDalogException {
		Codalog codalog = Codalog.getInstance();
		switch(this.stmtType) {
			case FACT:
				codalog.fact(this.getFact());
				break;
			case RULE:
				codalog.rule(this.getRule());
				break;
			case QUERY:
				codalog.saveQuery(goals);
				break;
			
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		switch(this.stmtType) {
		case FACT:
			sb.append(fact.toString());
			break;
		case RULE:
			sb.append(rule.toString());
			break;
		case QUERY:
			for(int k=0;k<goals.size();k++) {
				sb.append(goals.get(k));
				if(k<goals.size()-1) {
					sb.append(Constants.COMMA);
				}
			}
			sb.append(Constants.QUESTION_MARK);
			break;
		
	}
		
		return sb.toString();
	}
	

}
