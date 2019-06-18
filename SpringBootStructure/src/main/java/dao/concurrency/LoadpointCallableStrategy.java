package com.siemens.krawal.krawalcloudmanager.dao.concurrency;

import java.util.concurrent.Callable;

import org.eclipse.rdf4j.query.TupleQueryResult;

import com.siemens.krawal.krawalcloudmanager.dao.impl.FinalRepositories;

public class LoadpointCallableStrategy implements Callable<TupleQueryResult> {

	private String query;
	private FinalRepositories repo;

	public LoadpointCallableStrategy(String query, FinalRepositories repo) {
		this.query = query;
		this.repo = repo;
	}

	@Override
	public TupleQueryResult call() throws Exception {
		return repo.executeQuery(query);
	}

}
