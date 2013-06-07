package org.motechproject.mapper.adapters.impl;


import org.joda.time.DateTime;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSPersonDto;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.mapper.constants.FormMappingConstants.*;

@Component
public class PersonAdapter {

    public MRSPerson createPerson(MRSRegistrationActivity registrationActivity, CommcareFormSegment commcareFormSegment) {
        MRSPerson person = new MRSPersonDto();
        setDefaultProperties(person);
        updatePerson(person, registrationActivity, commcareFormSegment);
        return person;
    }

    private void setDefaultProperties(MRSPerson person) {
        person.setDead(false);
        person.setBirthDateEstimated(false);
    }

    public void updatePerson(MRSPerson person, MRSRegistrationActivity registrationActivity, CommcareFormSegment commcareFormSegment) {
        String gender = registrationActivity.getValueFor(GENDER_FIELD, commcareFormSegment, String.class);
        DateTime dateOfBirth = registrationActivity.getValueFor(DOB_FIELD, commcareFormSegment, DateTime.class);
        String firstName = registrationActivity.getValueFor(FIRST_NAME_FIELD, commcareFormSegment, String.class);
        String lastName = registrationActivity.getValueFor(LAST_NAME_FIELD, commcareFormSegment, String.class);
        String middleName = registrationActivity.getValueFor(MIDDLE_NAME_FIELD, commcareFormSegment, String.class);
        String preferredName = registrationActivity.getValueFor(PREFERRED_NAME_FIELD, commcareFormSegment, String.class);
        String address = registrationActivity.getValueFor(ADDRESS_FIELD, commcareFormSegment, String.class);
        Integer age = registrationActivity.getValueFor(AGE_FIELD, commcareFormSegment, Integer.class);
        Boolean birthDateIsEstimated = registrationActivity.getValueFor(BIRTH_DATE_ESTIMATED_FIELD, commcareFormSegment, Boolean.class);
        Boolean isDead = registrationActivity.getValueFor(IS_DEAD_FIELD, commcareFormSegment, Boolean.class);
        DateTime deathDate = registrationActivity.getValueFor(DEATH_DATE_FIELD, commcareFormSegment, DateTime.class);
        List<MRSAttribute> attributes = registrationActivity.getMRSAttributes(commcareFormSegment);

        updatePersonFields(person, firstName, lastName, dateOfBirth, gender, middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, attributes);
    }
    private void updatePersonFields(MRSPerson person, String firstName, String lastName, DateTime dateOfBirth, String gender, String middleName, String preferredName, String address, Boolean birthDateIsEstimated,
                                    Integer age, Boolean isDead, DateTime deathDate, List<MRSAttribute> attributes) {
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


}
