package org.motechproject.mapper.adapters.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.OpenMRSRegistrationActivity;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
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

@Component
public class AllRegistrationsAdapter implements ActivityFormAdapter {

    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    @Autowired
    private MRSUtil mrsUtil;

    @Autowired
    private IdentityResolver idResolver;

    @Autowired
    private MRSPatientAdapter mrsPatientAdapter;

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {

        OpenMRSRegistrationActivity registrationActivity = (OpenMRSRegistrationActivity) activity;

        FormValueElement topFormElement = form.getForm();

        Map<String, String> mappedAttributes = registrationActivity.getAttributes();
        Map<String, String> registrationMappings = registrationActivity.getRegistrationMappings();

        Map<String, String> patientIdScheme = registrationActivity.getPatientIdScheme();
        Map<String, String> facilityIdScheme = registrationActivity.getFacilityScheme();

        String motechId = null;

        if (patientIdScheme != null) {
            motechId = idResolver.retrieveId(patientIdScheme, form);
        }

        MRSPatient patient = mrsPatientAdapter.getPatientByMotechId(motechId);

        if (patient == null) {
            logger.info("Registering new patient by MotechId " + motechId);
        } else {
            logger.info("Patient already exists, updating patient " + motechId);
        }

        String dobField = registrationMappings.get(FormMappingConstants.DOB_FIELD);
        String firstNameField = registrationMappings.get(FormMappingConstants.FIRST_NAME_FIELD);
        String middleNameField = registrationMappings.get(FormMappingConstants.MIDDLE_NAME_FIELD);
        String lastNameField = registrationMappings.get(FormMappingConstants.LAST_NAME_FIELD);
        String preferredNameField = registrationMappings.get(FormMappingConstants.PREFERRED_NAME_FIELD);
        String genderField = registrationMappings.get(FormMappingConstants.GENDER_FIELD);
        String addressField = registrationMappings.get(FormMappingConstants.ADDRESS_FIELD);
        String ageField = registrationMappings.get(FormMappingConstants.AGE_FIELD);
        String birthDateIsEstimatedField = registrationMappings.get(FormMappingConstants.BIRTH_DATE_ESTIMATED_FIELD);
        String isDeadField = registrationMappings.get(FormMappingConstants.IS_DEAD_FIELD);
        String deathDateField = registrationMappings.get(FormMappingConstants.DEATH_DATE_FIELD);
        String facilityNameField = registrationMappings.get(FormMappingConstants.FACILITY_NAME_FIELD);

        String gender = populateStringValue(genderField, topFormElement);

        if (gender == null) {
            gender = registrationActivity.getStaticMappings().get("gender");
        }

        DateTime dateOfBirth = populateDateValue(dobField, topFormElement);

        if (dateOfBirth == null) {
            try {
                dateOfBirth = DateTime.parse(registrationActivity.getStaticMappings().get("dob"));
            } catch (IllegalArgumentException | NullPointerException e) {
                logger.info("Unable to parse date: " + e.getMessage());
            }
        }

        String firstName = populateStringValue(firstNameField, topFormElement);

        if (firstName == null) {
            firstName = registrationActivity.getStaticMappings().get("firstName");
        }

        String lastName = populateStringValue(lastNameField, topFormElement);

        if (lastName == null) {
            lastName = registrationActivity.getStaticMappings().get("lastName");
        }

        String middleName = populateStringValue(middleNameField, topFormElement);

        if (middleName == null) {
            middleName = registrationActivity.getStaticMappings().get("middleName");
        }

        String preferredName = populateStringValue(preferredNameField, topFormElement);

        if (preferredName == null) {
            preferredName = registrationActivity.getStaticMappings().get("preferredName");
        }

        String address = populateStringValue(addressField, topFormElement);

        if (address == null) {
            address = registrationActivity.getStaticMappings().get("address");
        }

        Integer age = populateIntegerValue(ageField, topFormElement);

        if (age == null) {
            try {
                age = Integer.parseInt(registrationActivity.getStaticMappings().get("age"));
            } catch (NumberFormatException e) {
                logger.error("Age was not a valid number");
            }
        }

        Boolean birthDateIsEstimated = populateBooleanValue(birthDateIsEstimatedField, topFormElement);

        if (birthDateIsEstimated == null) {
            try {
                birthDateIsEstimated = Boolean.parseBoolean(registrationActivity.getStaticMappings().get(
                        "birthdateIsEstimated"));
            } catch (Exception e) {
                logger.error("Error in birthdate: " + e.getMessage());
            }
        }

        Boolean isDead = populateBooleanValue(isDeadField, topFormElement);

        if (isDead == null) {
            try {
                isDead = Boolean.parseBoolean(registrationActivity.getStaticMappings().get("dead"));
            } catch (Exception e) {
                logger.error("Error in is dead value: " + e.getMessage());
            }
        }

        DateTime deathDate = populateDateValue(deathDateField, topFormElement);

        if (deathDate == null) {
            try {
                deathDate = DateTime.parse(registrationActivity.getStaticMappings().get("deathDate"));
            } catch (Exception e) {
                logger.error("Error in death date: " + e.getMessage());
            }
        }

        MRSFacility facility = null;

        String facilityName = populateStringValue(facilityNameField, topFormElement);

        if (facilityName == null) {
            facilityName = registrationActivity.getStaticMappings().get("facility");
        }

        if (facilityName == null) {
            facilityName = idResolver.retrieveId(facilityIdScheme, form);
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + FormMappingConstants.DEFAULT_FACILITY);
            facilityName = FormMappingConstants.DEFAULT_FACILITY;
        } 

        facility = mrsUtil.findFacility(facilityName);

        logger.info("Facility name: " + facilityName);

        MRSPerson person = null;

        List<MRSAttribute> attributes = new ArrayList<MRSAttribute>();


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

        if (patient == null && facility != null && firstName != null && lastName != null && dateOfBirth != null
                && motechId != null) {
            person = new MRSPersonDto();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setGender(gender);
            person.setDateOfBirth(dateOfBirth);

            setPerson(middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, person, attributes);

            patient = new MRSPatientDto();
            patient.setMotechId(motechId);
            patient.setPerson(person);

            try {
                patient = mrsPatientAdapter.savePatient(patient);
                logger.info("New patient saved: " + motechId);
            } catch (MRSException e) {
                logger.info("Could not save patient: " + e.getMessage());
            }
        } else if (patient != null) {
            person = patient.getPerson();
            updatePatient(patient, person, firstName, lastName, dateOfBirth, gender, middleName, preferredName,
                    address, birthDateIsEstimated, age, isDead, deathDate, attributes);
        } else {
            logger.info("Unable to save patient due to missing information");
            if (facility == null) {
                logger.info("Reason: No facility provided");
            }
            if (firstName == null) {
                logger.info("Reason: No first name provided");
            }
            if (lastName == null) {
                logger.info("Reason: No last name provided");
            }
            if (dateOfBirth == null) {
                logger.info("Reason: No date of birth provided");
            }
            if (motechId == null) {
                logger.info("Reason: No MOTECH id provided");
            }
        }
    }

    private void setPerson(String middleName, String preferredName, String address, Boolean birthDateIsEstimated,
            Integer age, Boolean isDead, DateTime deathDate, MRSPerson person, List<MRSAttribute> attributes) {
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

        setPerson(middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, person, attributes);

        logger.info("About to update patient");

        mrsPatientAdapter.updatePatient(patient);
    }

    private Integer populateIntegerValue(String fieldName, FormValueElement topFormElement) {
        Integer value = null;
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                try {
                    value = Integer.valueOf(element.getValue());
                } catch (NumberFormatException e) {
                    logger.error("Error parsing age value from registration form: " + e.getMessage());
                    return null;
                }
            }
        }
        return value;
    }

    private Boolean populateBooleanValue(String fieldName, FormValueElement topFormElement) {
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                return new Boolean(element.getValue());
            }
        }
        return null;
    }

    private DateTime populateDateValue(String fieldName, FormValueElement topFormElement) {
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                DateTime date = null;
                try {
                    date = DateTime.parse(element.getValue());
                    return date;
                } catch (IllegalArgumentException e) {
                    logger.info("Unable to parse date value: " + e.getMessage());
                }
                return null;
            }
        }
        return null;
    }

    private String populateStringValue(String fieldName, FormValueElement topFormElement) {
        if (fieldName != null) {
            FormValueElement element = topFormElement.getElementByName(fieldName);
            if (element != null) {
                return element.getValue();
            }
        }
        return null;
    }
}
