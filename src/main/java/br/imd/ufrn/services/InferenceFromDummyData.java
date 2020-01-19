package br.imd.ufrn.services;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;

public class InferenceFromDummyData {
	
	public static final String SCHEMA = "static/LGeoSIMOntology.owl";
	
	// Query used to test the transitivity rule
	public static final String FINALQUERY = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "prefix owl: <http://www.w3.org/2002/07/owl#>"
			+ "prefix lgsim: <http://www.semanticweb.org/bartirarocha/ontologies/2019/4/LGeoSIMOntology#>"
			+ "SELECT ?bairro WHERE {?bairro rdf:type lgsim:NeighborHood ." + " ?bairro lgsim:isPartOf ?cidade ."
			+ " FILTER(?cidade = (lgsim:Natal)) }";
	
	public static final String TRANSITIVITYRULE = "[rule1:(?a http://www.semanticweb.org/bartirarocha/ontologies/2019/4/LGeoSIMOntology#isPartOf ?b) "
			+ "(?b http://www.semanticweb.org/bartirarocha/ontologies/2019/4/LGeoSIMOntology#isPartOf ?c)"
			+ "->(?a http://www.semanticweb.org/bartirarocha/ontologies/2019/4/LGeoSIMOntology#isPartOf ?c)]";
	
	// main: Infer data from Bartira's owl file using the transitivity rule 
	public static void main(String[] args) {
		Model data = FileManager.get().loadModel(SCHEMA);
						
		Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(TRANSITIVITYRULE));
		InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
		
		Query query = QueryFactory.create(FINALQUERY);
		QueryExecution qe = QueryExecutionFactory.create(query, infmodel);
		ResultSet results = qe.execSelect();

		printRuleResult(results);
	}
	
	// printResults: Print the results of a sparql query
	public static void printRuleResult(ResultSet results) {
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			System.out.println(soln+"\n");
		}
	}
}
