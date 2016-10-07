-- ---------------------------------------------------------------------------
-- ASSIGNMENT_ASSIGNMENT
-- ---------------------------------------------------------------------------

CREATE TABLE ASSIGNMENT_ASSIGNMENT
(
    ASSIGNMENT_ID VARCHAR (99) NOT NULL,
	CONTEXT VARCHAR (99),
    XML LONGTEXT
);

CREATE UNIQUE INDEX ASSIGNMENT_ASSIGNMENT_INDEX ON ASSIGNMENT_ASSIGNMENT
(
	ASSIGNMENT_ID
);

CREATE INDEX ASSIGNMENT_ASSIGNMENT_CONTEXT ON ASSIGNMENT_ASSIGNMENT
(
	CONTEXT
);

-- ---------------------------------------------------------------------------
-- ASSIGNMENT_CONTENT
-- ---------------------------------------------------------------------------

CREATE TABLE ASSIGNMENT_CONTENT
(
    CONTENT_ID VARCHAR (99) NOT NULL,
	CONTEXT VARCHAR (99),
    XML LONGTEXT
);

CREATE UNIQUE INDEX ASSIGNMENT_CONTENT_INDEX ON ASSIGNMENT_CONTENT
(
	CONTENT_ID
);

CREATE INDEX ASSIGNMENT_CONTENT_CONTEXT ON ASSIGNMENT_CONTENT
(
	CONTEXT
);

-- ---------------------------------------------------------------------------
-- ASSIGNMENT_SUBMISSION
-- ---------------------------------------------------------------------------

CREATE TABLE ASSIGNMENT_SUBMISSION
(
    SUBMISSION_ID VARCHAR (99) NOT NULL,
	CONTEXT VARCHAR (99) NOT NULL,
	SUBMITTER_ID VARCHAR(99) NOT NULL,
	SUBMIT_TIME VARCHAR(99),
	SUBMITTED VARCHAR(6),
	GRADED VARCHAR(6),
    XML LONGTEXT
);

CREATE UNIQUE INDEX ASSIGNMENT_SUBMISSION_INDEX ON ASSIGNMENT_SUBMISSION
(
	SUBMISSION_ID
);

CREATE INDEX ASSIGNMENT_SUBMISSION_CONTEXT ON ASSIGNMENT_SUBMISSION
(
	CONTEXT
);

CREATE UNIQUE INDEX ASSIGNMENT_SUBMISSION_SUBMITTER_INDEX ON ASSIGNMENT_SUBMISSION
(
	CONTEXT,SUBMITTER_ID
);
