package com.siemens.krawal.krawalcloudmanager.dao.impl;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.exception.NetworkException;

@Component
public class FinalRepositories {

	private static final Logger LOGGER = LoggerFactory.getLogger(FinalRepositories.class);

	@Autowired
	private Repository repo;

	public void buildModel(ModelBuilder builder) {

		try {
			repo.initialize();
			try (RepositoryConnection conn = repo.getConnection()) {
				Model model = builder.build();
				conn.add(model);
				LOGGER.info("===== Turtle format ==== ");
				Rio.write(model, System.out, RDFFormat.TURTLE);
			}
		} catch (Exception e) {
			throw new NetworkException("Network error! please try after sometime.");
		}
	}

	public TupleQueryResult executeQuery(String query) {

		try {
			repo.initialize();
			try (RepositoryConnection conn = repo.getConnection()) {

				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
				return tupleQuery.evaluate();
			}
		} catch (Exception e) {
			throw new NetworkException("Network error! please try after sometime.");
		}

	}

	public void updateQuery(String query) {
		try {
			repo.initialize();
			try (RepositoryConnection conn = repo.getConnection()) {
				Update updateQuery = conn.prepareUpdate(query);
				updateQuery.execute();
			}
		} catch (Exception e) {
			throw new NetworkException("Network error! please try after sometime.");
		}
	}

	public boolean booleanQuery(String query) {
		try {
			repo.initialize();
			try (RepositoryConnection conn = repo.getConnection()) {
				BooleanQuery qu = conn.prepareBooleanQuery(query);
				return qu.evaluate();
			}
		} catch (Exception e) {
			throw new NetworkException("Network error! please try after sometime.");
		}
	}

	public ValueFactory getValueFactory() {
		return repo.getValueFactory();
	}
}
