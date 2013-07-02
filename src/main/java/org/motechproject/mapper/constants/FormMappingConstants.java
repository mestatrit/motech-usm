package org.motechproject.mapper.constants;

public final class FormMappingConstants {

    //Id schemes
    public static final String ID_SCHEME_TYPE = "type";
    public static final String ID_SCHEME_FIELD = "fieldName";
    public static final String ID_SCHEME_ATTRIBUTE = "attributeName";
    public static final String ID_FROM_FORM_SCHEME = "fromForm";
    public static final String ID_FROM_USER_DATA_SCHEME = "commcareUserData";
    public static final String ID_FROM_USER_ID_SCHEME = "commcareUserId";
    public static final String ID_FROM_USERNAME_SCHEME = "commcareUsername";
    public static final String ID_FROM_COMMCARE_CASE_SCHEME = "commcareCase";

    public static final String FORM_NAME_ATTRIBUTE = "name";
    public static final String FORM_XMLNS_ATTRIBUTE = "xmlns";
    public static final String MRS_ACTIVITY_TYPE = "type";
    public static final String ENCOUNTER_ACTIVITY = "encounter";
    public static final String REGISTRATION_ACTIVITY = "registration";
    public static final String FORM_TIME_END = "timeEnd";
    public static final String FORM_USERNAME = "username";
    public static final String LIST_TYPE = "list";
    public static final String LIST_DELIMITER = " ";
    public static final String CASE_ELEMENT = "case";
    public static final String CASE_ID_ATTRIBUTE = "case_id";
    public static final String CONCEPT_ID_ATTRIBUTE = "concept_id";
    public static final String USER_ID = "userID";

    //MRS Registration constants
    public static final String DOB_FIELD = "dateOfBirth";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String MIDDLE_NAME_FIELD = "middleName";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String PREFERRED_NAME_FIELD = "preferredName";
    public static final String GENDER_FIELD = "gender";
    public static final String ADDRESS_FIELD = "address";
    public static final String AGE_FIELD = "age";
    public static final String BIRTH_DATE_ESTIMATED_FIELD = "dobEstimated";
    public static final String IS_DEAD_FIELD = "dead";
    public static final String DEATH_DATE_FIELD = "deathDate";
    public static final String FACILITY_NAME_FIELD = "facility";
    public static final String DEFAULT_FACILITY = "Unknown Location";
    public static final String ENCOUNTER_DATE_FIELD = "encounterDate";
    public static final String REGISTRATION_DATE_FIELD = "registrationDate";

    //Destination decides which values are required during the mapping process, configuration is in mappingConfiguration.properties
    public static final String DESTINATION_COUCHDB = "couchdb";
    public static final String DESTINATION_OPENMRS = "openmrs";

    public static final String MAPPING_CONFIGURATION_FILE_NAME = "mappingConfiguration.properties";
    public static final String DESTINATION = "destination";
    public static final String FORM_VERSION_PATH = "formVersionPath";
    public static final String AVOID_STALE_REGISTRATION_UPDATES = "avoidStaleRegistrationUpdates";

    public static final String REPORT_MISSING_ID = "reportMissingId";

    private FormMappingConstants() {
    }
}
