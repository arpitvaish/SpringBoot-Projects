package com.siemens.krawal.krawalcloudmanager.constants;

public final class APIConstants {

	private APIConstants() {

	}

	public static final String SPARQL_DEV_ENDPOINT = "blazegraph.dev.endpoint";
	public static final String SPARQL_TEST_ENDPOINT = "blazegraph.test.endpoint";
	public static final String SPARQL_PROD_ENDPOINT = "blazegraph.prod.endpoint";

	// values are fetched from
	// https://siemens-pg-001.eu.auth0.com/.well-known/jwks.json
	public static final String RSA = "RSA";
	public static final String ISSUER = "https://siemens-pg-001.eu.auth0.com/";
	public static final String KID = "M0MyMjM3ODI4OTFBNTJCMUZGNDE5NTVBRjlDOUNENzVGMjRBMUJFQQ";

	public static final String MODULUS = "wmL65-B8RsMjooonx5CaU709ykFrPj9jX42EzUtnWWxRoBC95HB0OYoQQS7YZd6ZAsYWBt6TLOaHCjWHi-4McbJwVcVtDZuWpF-yqlN_MZYFykvch4t8cVCa9ERurIAVVItPh3NuM1uOukRVtcJ8lCXo1qHIvO_kd2luOG02hsj9k5pKMocWKHOvOUovEr8a_TcdObwkAMDLUI4SImkPeeLkqhgUIQU8yB2cPYtc_CRgrpYjyHGaEpCCBwybo_mfhGGJaDRmaDiO-tDp7qtv2rnqGcg_Zvr5u3nH6nB6EKfosETKpfZjtCkSkHDVClHaXaHBIk4NPnfqjLf5ElvDBQ";
	public static final String EXPONENT = "AQAB";
	
	public static final String LOG_PATH = "/var/logs";
}
