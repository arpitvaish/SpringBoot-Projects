package com.siemens.krawal.krawalcloudmanager.db.constants;

public final class RelationshipConstants {

	private RelationshipConstants() {

	}

	// users
	public static final String HAS_GID = "hasGid";
	public static final String HAS_KEY = "hasKey";
	public static final String HAS_USERNAME = "hasUserName";

	// projects
	public static final String HAS_NAME = "hasName";
	public static final String HAS_DESCRIPTION = "hasDescription";
	public static final String HAS_LATEST_REVISION_NUMBER = "hasLatestRevisionNumber";
	public static final String HAS_REVISION = "hasRevision";
	public static final String HAS_OBJECT_COUNTER = "hasObjectCounter";
	public static final String HAS_USER = "hasUser";
	public static final String CREATED_BY = "createdBy";

	// revision
	public static final String HAS_CHECKOUT_REVISION_ID = "hasCheckoutRevisionId";
	public static final String HAS_REVISION_NUMBER = "hasRevisionNumber";
	public static final String HAS_COMMENT = "hasComment";
	public static final String COMMITTED_BY = "committedBy";
	public static final String CREATED = "created";
	public static final String DELETED = "deleted";

	public static final String ACCESSIBLE_TO = "accessibleTo";

	// aggregates
	public static final String HAS_ID = "hasId";
	public static final String HAS_TYPE = "hasType";
	public static final String HAS_ASSEMBLY_ASSOCIATION = "hasAssemblyAssociation";
	public static final String HAS_PARTS = "isPartOf";

	// cycle segments
	public static final String CONTAINS = "contains";
	public static final String HAS_CYCLE_SEGMENT_ASSOCIATION = "hasCycleSegmentAssociation";

	// mediumConnections
	public static final String VIA = "via";
	public static final String INGRES_PORT = "ingresPort";
	public static final String OUTGRES_PORT = "outgresport";

	public static final String RDFS_LABEL = "label";

	// loadpoint
	public static final String HAS_CONTROLUNIT = "hasControlUnit";
	public static final String HAS_CONSTRAINT = "hasConstraint";
	public static final String HAS_SIGNAL_CONNECTION = "hasSignalConnection";
	public static final String BELONGS_TO = "belongsTo";

	// constraint
	public static final String REFERENCING_TO = "referencingTo";

	// signalConnection
	public static final String APPLIED_TO = "appliedTo";
	public static final String APPLIED_VIA = "appliedVia";
	public static final String APPLIED_TO_PORT = "appliedToPort";
	public static final String APPLIED_VIA_PORT = "appliedViaPort";

	// data
	public static final String HAS_UUID = "hasUUID";
	public static final String HAS_PLANT_DATA = "hasPlantData";
	public static final String HAS_DESIGN_DATA = "hasDesignData";
	public static final String HAS_LOAD_DATA = "hasLoadData";

}
