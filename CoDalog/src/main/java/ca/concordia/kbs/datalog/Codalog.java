/**
 * 
 */
package ca.concordia.kbs.datalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.concordia.kbs.datalog.constants.Constants;
import ca.concordia.kbs.datalog.database.BasicExtensionalDB;
import ca.concordia.kbs.datalog.database.ExtensionalDB;
import ca.concordia.kbs.datalog.database.IntensionalDB;
import ca.concordia.kbs.datalog.database.QueryDB;
import ca.concordia.kbs.datalog.engine.Engine;
import ca.concordia.kbs.datalog.engine.EngineType;
import ca.concordia.kbs.datalog.engine.NaiveEngine;
import ca.concordia.kbs.datalog.engine.SemiNaiveEngine;
import ca.concordia.kbs.datalog.exception.CoDalogException;
import ca.concordia.kbs.datalog.syntax.Expression;
import ca.concordia.kbs.datalog.syntax.Rule;

/**
 * @author AmanRana
 *
 */
public class Codalog {

	private ExtensionalDB edb;
	private IntensionalDB idb;
	private QueryDB qdb;
	private Engine engine;
	private EngineType engineType;
	private static Codalog instance;

	// singleton object
	public static Codalog getInstance() {
		if (instance == null) {
			instance = new Codalog();
		}

		return instance;
	}

	private Codalog() {
		edb = new BasicExtensionalDB();
		idb = new IntensionalDB();
		qdb = new QueryDB();

		String engineName = Main.getInstance().getProp().getProperty(Constants.EVALUATION_ENGINE);
		for (EngineType et : EngineType.values()) {
			if (et.getEngineType().equals(engineName)) {
				engineType = et;
				break;
			}
		}
		boolean printIntermediateResults = Boolean
				.valueOf(Main.getInstance().getProp().getProperty(Constants.PRINT_INTERMEDIATE_RESULT_TO_FILE));
		if (engineType == EngineType.NAIVE) {
			engine = new NaiveEngine(printIntermediateResults);
		} else {
			engine = new SemiNaiveEngine(printIntermediateResults);
		}
	}

	public Codalog fact(Expression exp) throws CoDalogException {
		if (!exp.isGroundFact()) {
			throw new CoDalogException("Not a grounded fact: " + exp);
		}
		if (exp.isNegation()) {
			throw new CoDalogException("Negation not allowed in facts: " + exp);
		}
		
		if(edb.getEdbMap().containsKey(exp.getPredicate())) {
			Set<Expression> facts = edb.getEdbMap().get(exp.getPredicate());
			Expression fact = facts.iterator().next();
			if(fact.getNoOfArguments()!=exp.getNoOfArguments()) {
				throw new CoDalogException("Arity mismatch for : " + exp);
			}
		}
		
		edb.addFact(exp);
		return this;
	}

	public void rule(Rule rule) throws CoDalogException {
		rule.setBody(engine.reorder(rule.getBody()));
		rule.validate();
		idb.getIdb().add(rule);
	}

	public void saveQuery(List<Expression> goals) {
		qdb.getQuery().add(goals);
	}

	public Map<List<Expression>, Collection<Map<String, String>>> evaluateQueries(List<List<Expression>> queries)
			throws CoDalogException {
		return engine.query(this, queries);
	}

	public void generateNewFacts() throws CoDalogException {
		engine.generateNewFacts(this);
	}

	/**
	 * @return the edb
	 */
	public ExtensionalDB getEdb() {
		return edb;
	}

	/**
	 * @return the idb
	 */
	public IntensionalDB getIdb() {
		return idb;
	}

	/**
	 * @return the qdb
	 */
	public QueryDB getQdb() {
		return qdb;
	}

	/**
	 * @return the engine
	 */
	public Engine getEngine() {
		return engine;
	}

	/**
	 * @return the engineType
	 */
	public EngineType getEngineType() {
		return engineType;
	}

	/**
	 * @param engineType the engineType to set
	 */
	public void setEngineType(EngineType engineType) {
		this.engineType = engineType;
	}

}
