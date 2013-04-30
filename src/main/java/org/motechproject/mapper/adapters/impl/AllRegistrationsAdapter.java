package org.motechproject.mapper.adapters.impl;

import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
import org.motechproject.mapper.validation.ValidationError;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSAttributeDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.motechproject.mapper.constants.FormMappingConstants.*;

@Component
public class AllRegistrationsAdapter implements ActivityFormAdapter {

    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");
    @Autowired
    private MRSUtil mrsUtil;
    @Autowired
    private IdentityResolver idResolver;
    @Autowired
    private MRSPatientAdapter mrsPatientAdapter;
    @Autowired
    private ValidationManager validator;

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {
        MRSRegistrationActivity registrationActivity = (MRSRegistrationActivity) activity;

        FormValueElement topFormElement = form.getForm();

        String gender = getValueFor(GENDER_FIELD, topFormElement, registrationActivity);
        DateTime dateOfBirth = getDateValueFor(DOB_FIELD, topFormElement, registrationActivity);
        String firstName = getValueFor(FIRST_NAME_FIELD, topFormElement, registrationActivity);
        String lastName = getValueFor(LAST_NAME_FIELD, topFormElement, registrationActivity);
        String middleName = getValueFor(MIDDLE_NAME_FIELD, topFormElement, registrationActivity);
        String preferredName = getValueFor(PREFERRED_NAME_FIELD, topFormElement, registrationActivity);
        String address = getValueFor(ADDRESS_FIELD, topFormElement, registrationActivity);
        Integer age = getIntegerValueFor(AGE_FIELD, topFormElement, registrationActivity);
        Boolean birthDateIsEstimated = getBooleanValueFor(BIRTH_DATE_ESTIMATED_FIELD, topFormElement, registrationActivity);
        Boolean isDead = getBooleanValueFor(IS_DEAD_FIELD, topFormElement, registrationActivity);
        DateTime deathDate = getDateValueFor(DEATH_DATE_FIELD, topFormElement, registrationActivity);
        String facilityName = getValueFor(FACILITY_NAME_FIELD, topFormElement, registrationActivity);

        MRSFacility facility = getMRSFacility(form, registrationActivity.getFacilityScheme(), facilityName);

        List<MRSAttribute> attributes = getMRSAttributes(registrationActivity, topFormElement);

        Map<String, String> patientIdScheme = registrationActivity.getPatientIdScheme();
        String motechId = idResolver.retrieveId(patientIdScheme, form);
        MRSPatient patient = mrsPatientAdapter.getPatientByMotechId(motechId);

        addOrUpdatePatient(gender, dateOfBirth, firstName, lastName, middleName, preferredName, address, age, birthDateIsEstimated, isDead, deathDate, facility, attributes, motechId, patient);
    }

    private void addOrUpdatePatient(String gender, DateTime dateOfBirth, String firstName, String lastName, String middleName, String preferredName, String address, Integer age, Boolean birthDateIsEstimated, Boolean dead, DateTime deathDate, MRSFacility facility, List<MRSAttribute> attributes, String motechId, MRSPatient patient) {
        MRSPerson person;
        if (patient == null) {
            logger.info("Registering new patient by MotechId " + motechId);
            person = new MRSPersonDto("", firstName, middleName, lastName, preferredName, address, dateOfBirth, birthDateIsEstimated, age, gender, dead, attributes, deathDate);
            patient = new MRSPatientDto("", facility, person, motechId);
            try {
                List<ValidationError> validationErrors = validator.validatePatient(patient);
                if (validationErrors.size() == 0) {
                    mrsPatientAdapter.savePatient(patient);
                } else {
                    logger.info("Could not save patient due to validation errors");
                }
                logger.info("New patient saved: " + motechId);
            } catch (MRSException e) {
                logger.info("Could not save patient: " + e.getMessage());
            }
        } else {
            logger.info("Patient already exists, updating patient " + motechId);
            person = patient.getPerson();
            patient.setFacility(facility);
            updatePatient(patient, person, firstName, lastName, dateOfBirth, gender, middleName, preferredName,
                    address, birthDateIsEstimated, age, dead, deathDate, attributes);
        }
    }

    private List<MRSAttribute> getMRSAttributes(MRSRegistrationActivity registrationActivity, FormValueElement topFormElement) {
        List<MRSAttribute> attributes = new ArrayList<>();
        Map<String, String> mappedAttributes = registrationActivity.getAttributes();
        if (mappedAttributes != null) {
            for (Entry<String, String> entry : mappedAttributes.entrySet()) {
                FormValueElement attributeElement = topFormElement.getElementByName(entry.getValue());
                String attributeValue = null;
                if (attributeElement != null) {
                    attributeValue = attributeElement.getValue();
                }
                if (attributeValue != null && attributeValue.trim().length() > 0) {
                    String attributeName = entry.getKey();
                    MRSAttributeDto attribute = new MRSAttributeDto(attributeName, attributeValue);
                    attributes.add(attribute);
                }
            }
        }
        return attributes;
    }

    private MRSFacility getMRSFacility(CommcareForm form, Map<String, String> facilityIdScheme, String facilityName) {
        if (facilityName == null) {
            facilityName = idResolver.retrieveId(facilityIdScheme, form);
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + DEFAULT_FACILITY);
            facilityName = DEFAULT_FACILITY;
        }

        MRSFacility facility = mrsUtil.findFacility(facilityName);

        logger.info("Facility name: " + facilityName);
        return facility;
    }

    private void setPerson(String firstName, String lastName, DateTime dateOfBirth, String gender, String middleName, String preferredName, String address, Boolean birthDateIsEstimated,
                           Integer age, Boolean isDead, DateTime deathDate, MRSPerson person, List<MRSAttribute> attributes) {
        if (firstName != null) {
            person.setFirstName(firstName);
        }
        if (lastName != null) {
            person.setLastName(lastName);
        }
        if (dateOfBirth != null) {
            person.setDateOfBirth(dateOfBirth);
        }
        if (gender != null) {
            person.setGender(gender);
        }
        if (middleName != null) {
            person.setMiddleName(middleName);
        }
        if (preferredName != null) {
            person.setPreferredName(preferredName);
        }
        if (address != null) {
            person.setAddress(address);
        }
        if (birthDateIsEstimated != null) {
            person.setBirthDateEstimated(birthDateIsEstimated);
        }
        if (age != null) {
            person.setAge(age);
        }
        if (isDead != null) {
            person.setDead(isDead);
        }
        if (deathDate != null) {
            person.setDeathDate(deathDate);
        }
        if (attributes != null) {
            person.setAttributes(attributes);
        }
    }

    private void updatePatient(MRSPatient patient, MRSPerson person, String firstName, String lastName,
                               DateTime dateOfBirth, String gender, String middleName, String preferredName, String address,
                               Boolean birthDateIsEstimated, Integer age, Boolean isDead, DateTime deathDate, List<MRSAttribute> attributes) {

        setPerson(firstName, lastName, dateOfBirth, gender, middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, person, attributes);

        logger.info("About to update patient");

        List<ValidationError> validationErrors = validator.validatePatient(patient);
        if (validationErrors.size() == 0) {
            mrsPatientAdapter.updatePatient(patient);
        } else {
            logger.info("Could not update patient due to validation errors");
        }
    }

    private Integer getIntegerValueFor(String fieldName, FormValueElement topFormElement, MRSRegistrationActivity registrationActivity) {
        String value = getValueFor(fieldName, topFormElement, registrationActivity);
        if (value == null)
            return null;

        Integer integerValue = null;
        try {
            integerValue = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            logger.info(String.format("Error parsing %s value from registration form: %s", fieldName, e.getMessage()));
        }
        return integerValue;
    }

    private Boolean getBooleanValueFor(String fieldName, FormValueElement topFormElement, MRSRegistrationActivity registrationActivity) {
        String value = getValueFor(fieldName, topFormElement, registrationActivity);
        return Boolean.valueOf(value);
    }

    private DateTime getDateValueFor(String fieldName, FormValueElement topFormElement, MRSRegistrationActivity registrationActivity) {
        String value = getValueFor(fieldName, topFormElement, registrationActivity);
        if (value == null)
            return null;

        DateTime dateValue = null;
        try {
            dateValue = DateTime.parse(value);
        } catch (IllegalArgumentException e) {
            logger.info(String.format("Unable to parse %s value: %s", fieldName, e.getMessage()));
        }
        return dateValue;
    }

    private String getValueFor(String fieldName, FormValueElement topFormElement, MRSRegistrationActivity registrationActivity) {
        Map<String, String> registrationMappings = registrationActivity.getRegistrationMappings();
        registrationMappings.get(fieldName);
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                return element.getValue();
            }
        }
        return registrationActivity.getStaticMappings().get(fieldName);
    }
}