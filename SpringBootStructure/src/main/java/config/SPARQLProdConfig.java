package com.siemens.krawal.krawalcloudmanager.config;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.siemens.krawal.krawalcloudmanager.constants.APIConstants;

@Configuration
@Profile("prod")
public class SPARQLProdConfig {

	@Autowired
	private Environment env;

	@Bean
	public Repository getRepository() {
		// Blaze graph
		String sparqlEndpoint = env.getProperty(APIConstants.SPARQL_PROD_ENDPOINT);
		return new SPARQLRepository(sparqlEndpoint);
	}

}
