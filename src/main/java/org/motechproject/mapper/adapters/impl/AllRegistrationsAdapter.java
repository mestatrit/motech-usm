package org.motechproject.mapper.adapters.impl;

import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.util.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static org.motechproject.mapper.constants.FormMappingConstants.*;

@Component
public class AllRegistrationsAdapter extends ActivityFormAdapter {

    private MRSUtil mrsUtil;
    private IdentityResolver idResolver;
    private MRSPatientAdapter mrsPatientAdapter;
    private ValidationManager validator;

    @Autowired
    public AllRegistrationsAdapter(MRSUtil mrsUtil, IdentityResolver idResolver, MRSPatientAdapter mrsPatientAdapter, ValidationManager validator, AllElementSearchStrategies allElementSearchStrategies) {
        super(allElementSearchStrategies);
        this.mrsUtil = mrsUtil;
        this.idResolver = idResolver;
        this.mrsPatientAdapter = mrsPatientAdapter;
        this.validator = validator;
    }

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {
        MRSRegistrationActivity registrationActivity = (MRSRegistrationActivity) activity;
        for (CommcareFormBeneficiarySegment beneficiarySegment : getAllBeneficiarySegments(form, activity)) {

            String gender = getStringValueFor(GENDER_FIELD, registrationActivity, beneficiarySegment);
            DateTime dateOfBirth = getDateValueFor(DOB_FIELD, registrationActivity, beneficiarySegment);
            String firstName = getStringValueFor(FIRST_NAME_FIELD, registrationActivity, beneficiarySegment);
            String lastName = getStringValueFor(LAST_NAME_FIELD, registrationActivity, beneficiarySegment);
            String middleName = getStringValueFor(MIDDLE_NAME_FIELD, registrationActivity, beneficiarySegment);
            String preferredName = getStringValueFor(PREFERRED_NAME_FIELD, registrationActivity, beneficiarySegment);
            String address = getStringValueFor(ADDRESS_FIELD, registrationActivity, beneficiarySegment);
            Integer age = getIntegerValueFor(AGE_FIELD, registrationActivity, beneficiarySegment);
            Boolean birthDateIsEstimated = getBooleanValueFor(BIRTH_DATE_ESTIMATED_FIELD, registrationActivity, beneficiarySegment);
            Boolean isDead = getBooleanValueFor(IS_DEAD_FIELD, registrationActivity, beneficiarySegment);
            DateTime deathDate = getDateValueFor(DEATH_DATE_FIELD, registrationActivity, beneficiarySegment);
            String facilityName = getStringValueFor(FACILITY_NAME_FIELD, registrationActivity, beneficiarySegment);

            MRSFacility facility = getMRSFacility(registrationActivity.getFacilityScheme(), facilityName, beneficiarySegment);

            List<MRSAttribute> attributes = getMRSAttributes(registrationActivity, beneficiarySegment);

            Map<String, String> patientIdScheme = registrationActivity.getPatientIdScheme();
            String motechId = idResolver.retrieveId(patientIdScheme, beneficiarySegment);
            if (motechId == null) {
                logger.error("MotechId could not be obtained");
                return;
            }
            MRSPatient patient = mrsPatientAdapter.getPatientByMotechId(motechId);

            addOrUpdatePatient(gender, dateOfBirth, firstName, lastName, middleName, preferredName, address, age, birthDateIsEstimated, isDead, deathDate, facility, attributes, motechId, patient);
        }

    }

    private void addOrUpdatePatient(String gender, DateTime dateOfBirth, String firstName, String lastName, String middleName, String preferredName, String address, Integer age, Boolean birthDateIsEstimated, Boolean dead, DateTime deathDate, MRSFacility facility, List<MRSAttribute> attributes, String motechId, MRSPatient patient) {
        MRSPerson person;
        if (patient == null) {
            String id = UUID.randomUUID().toString();
            if(dead == null) {
                dead = false;
            }
            if(birthDateIsEstimated == null) {
                birthDateIsEstimated = false;
            }
            person = new MRSPersonDto(id, firstName, middleName, lastName, preferredName, address, dateOfBirth, birthDateIsEstimated, age, gender, dead, attributes, deathDate);
            patient = new MRSPatientDto(id, facility, person, motechId);
            try {
                List<ValidationError> validationErrors = validator.validatePatient(patient);
                if (validationErrors.size() == 0) {
                    mrsPatientAdapter.savePatient(patient);
                    logger.info(String.format("Registered new patient by MotechId(%s) and PatientId(%s)", motechId, id));
                } else {
                    logger.error("Could not save patient due to validation errors");
                }
            } catch (MRSException e) {
                logger.info("Could not save patient: " + e.getMessage());
            }
        } else {
            person = patient.getPerson();
            patient.setFacility(facility);
            updatePatient(patient, person, firstName, lastName, dateOfBirth, gender, middleName, preferredName,
                    address, birthDateIsEstimated, age, dead, deathDate, attributes);
        }
    }

    private List<MRSAttribute> getMRSAttributes(MRSRegistrationActivity registrationActivity, CommcareFormBeneficiarySegment beneficiarySegment) {
        List<MRSAttribute> attributes = new ArrayList<>();
        Map<String, String> mappedAttributes = registrationActivity.getAttributes();
        if (mappedAttributes != null) {
            for (Entry<String, String> entry : mappedAttributes.entrySet()) {

                FormNode attributeElement = beneficiarySegment.search(entry.getValue());

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

    private MRSFacility getMRSFacility(Map<String, String> facilityIdScheme, String facilityName, CommcareFormBeneficiarySegment beneficiarySegment) {
        if (facilityName == null) {
            facilityName = idResolver.retrieveId(facilityIdScheme, beneficiarySegment);
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
        List<ValidationError> validationErrors = validator.validatePatient(patient);
        if (validationErrors.size() == 0) {
            mrsPatientAdapter.updatePatient(patient);
            logger.info("Patient already exists, updated patient: " + patient.getMotechId());
        } else {
            logger.error("Could not update patient due to validation errors");
        }
    }

    private String getStringValueFor(String fieldName, MRSRegistrationActivity registrationActivity, CommcareFormBeneficiarySegment beneficiarySegment) {
        return getValueFor(fieldName, registrationActivity, beneficiarySegment, String.class);
    }

    private Integer getIntegerValueFor(String fieldName, MRSRegistrationActivity registrationActivity, CommcareFormBeneficiarySegment beneficiarySegment) {
        return getValueFor(fieldName, registrationActivity, beneficiarySegment, Integer.class);
    }

    private Boolean getBooleanValueFor(String fieldName, MRSRegistrationActivity registrationActivity, CommcareFormBeneficiarySegment beneficiarySegment) {
        return getValueFor(fieldName, registrationActivity, beneficiarySegment, Boolean.class);
    }

    private DateTime getDateValueFor(String fieldName, MRSRegistrationActivity registrationActivity, CommcareFormBeneficiarySegment beneficiarySegment) {
        return getValueFor(fieldName, registrationActivity, beneficiarySegment, DateTime.class);
    }

    private <T> T getValueFor(String fieldName, MRSRegistrationActivity registrationActivity, CommcareFormBeneficiarySegment beneficiarySegment, Class<T> convertTo) {
        Map<String, String> registrationMappings = registrationActivity.getRegistrationMappings();
        if (registrationMappings == null) return null;
        String fieldValue = registrationMappings.get(fieldName);
        if (fieldValue != null) {
            return ExpressionUtil.resolve(fieldValue, beneficiarySegment, convertTo);
        }
        return getDefaultValue(fieldName, convertTo, registrationActivity);
    }

    private <T> T getDefaultValue(String fieldName, Class<T> convertTo, MRSRegistrationActivity registrationActivity) {
        Map<String, String> staticMappings = registrationActivity.getStaticMappings();

        if(staticMappings != null) {
            return (T) staticMappings.get(fieldName);
        }
        return null;
    }
}
