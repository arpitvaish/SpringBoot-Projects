package com.siemens.krawal.krawalcloudmanager.db.constants;

public interface DBConstants {

	public static final String ROOT_NAMESPACE = "http://www.siemens.com/krawalcloud/projects/";
	public static final String USER_NAMESPACE = "http://www.siemens.com/krawalcloud/users/";

	public static final String RDFS_LABEL = "label";
	// sparql
	public static final String ROOT = "root";
	public static final String ROOT_SUBJECT = "root:";
	public static final String STRING_NS = " ^^xsd:string";

	public static final String REV = "rev";
	public static final String REV_SUBJECT = "rev:";

	public static final String GENERIC_NS = "aggregate";
	public static final String GENERIC_NS_SUB = "aggregate:";

	public static final String GENERIC_NS_SECONDARY = "secondary";
	public static final String GENERIC_NS_SUB_SECONDARY = "secondary:";

	public static final String LOADPOINT_NS = "loadpoint";
	public static final String LOADPOINT_NS_SUB = "loadpoint:";

	public static final String USER_ID = "user:";
	public static final String USER = "user";

	public static final String RDFS_SUBJECT = "rdfs:";

	// Authorization
	public static final String AUTH_TOKEN_PREFIX = "Bearer";
	public static final String AUTH_PROVIDER = "Siemens";

	// Labels
	public static final String AGGREGATE_LABEL = "Aggregate";
	public static final String AGGREGATE_ASSEMBLY_LABEL = "AggregateAssemblyAssociation";
	public static final String CYCLE_SEGMENT_LABEL = "CycleSegment";
	public static final String CYCLE_SEGMENT_ASSOCIATION_LABEL = "CycleSegmentAssociation";
	public static final String MEDIUM_CONNECTION_LABEL = "MediumConnection";
	public static final String LOADPOINT_LABEL = "Loadpoint";
	public static final String CONTROLUNIT_LABEL = "ControlUnit";
	public static final String CONSTRAINT_LABEL = "Constraint";
	public static final String SIGNALCONNECTION_LABEL = "SignalConnection";
	public static final String PLANTDATA_LABEL = "PlantData";
	public static final String LOADDATA_LABEL = "LoadData";
	public static final String DESIGNDATA_LABEL = "DesignData";

	// query variables
	public static final String AGGREGATES_NAMESPACE_VARIABLE = "/aggregates";
	public static final String AGGREGATES_ASSOCIATION_NAMESPACE_VARIABLE = "/aggregatesAssociation";
	public static final String REVISIONS_NAMESPACE_VARIABLE = "/revisions";
	public static final String CONSTRAINTS_NAMESPACE_VARIABLE = "/constraints";
	public static final String LOADPOINTS_NAMESPACE_VARIABLE = "/loadpoints";
	public static final String CONTROLUNITS_NAMESPACE_VARIABLE = "/controlUnits";
	public static final String CYCLESEGMENTS_NAMESPACE_VARIABLE = "/cycleSegments";
	public static final String CYCLESEGMENTS_ASSOCIATION_NAMESPACE_VARIABLE = "/cycleSegmentAssociation";
	public static final String MEDIUMCONNECTIONS_NAMESPACE_VARIABLE = "/mediumConnections";
	public static final String SIGNALCONNECTIONS_NAMESPACE_VARIABLE = "/signalConnections";
	public static final String NAMESPACE_SLASH = "/";
	public static final String GENERIC_DATA_NAMESPACE_VARIABLE = "/data";
	public static final String PLANT_DATA_NAMESPACE_VARIABLE = "/data/plantData";
	public static final String DESIGN_DATA_NAMESPACE_VARIABLE = "/data/designData";
	public static final String LOAD_DATA_NAMESPACE_VARIABLE = "/data/loadData";

	public static final String QUERY_VARIABLE_OBJECTS = "objects";
	public static final String QUERY_VARIABLE_S = "s";
	public static final String QUERY_VARIABLE_D = "d";
	public static final String QUERY_VARIABLE_G = "g";
	public static final String QUERY_VARIABLE_E = "e";
	public static final String QUERYVARIABLE_LP = "lp";
	public static final String QUERY_VARIABLE_PROJECTIRI = "projectIRI";
	public static final String QUERY_VARIABLE_LATEST_REVISION_IRI = "latestRevisionNumber";
	public static final String QUERYVARIABLE_AGGREGATE = "aggregate";
	public static final String QUERYVARIABLE_ID = "id";
	public static final String QUERYVARIABLE_GID = "gid";
	public static final String QUERYVARIABLE_USER = "user";
	public static final String QUERYVARIABLE_PROJECTS = "projects";
	public static final String QUERYVARIABLE_USERS = "users";
	public static final String QUERYVARIABLE_REVISIONS = "revisionNo";
	public static final String QUERYVARIABLE_CHECKOUT_REVISIONS = "checkoutRevisionId";
	public static final String QUERYVARIABLE_DESCRIPTION = "description";
	public static final String QUERYVARIABLE_OBJECTCOUNTER = "objectCounter";
	public static final String QUERYVARIABLE_LRNUMBER = "lrNumber";
	public static final String QUERYVARIABLE_COMMENT = "comment";
	public static final String QUERYVARIABLE_COMMITTED_BY = "committedBy";
	public static final String QUERYVARIABLE_CREATED_BY = "createdBy";

	// messages
	public static final String SUCCESS = "success";

	public static final String PLANT_SCOPE = "Plant";
	public static final String DESIGN_SCOPE = "Design";

	public static final String UNDERSCORE_STRING = "_";
}
