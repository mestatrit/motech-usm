package org.motechproject.mapper.adapters.impl;


import org.joda.time.DateTime;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.service.PersonUpdaterFactory;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.PersonUpdater;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSPersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.mapper.constants.FormMappingConstants.ADDRESS_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.AGE_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.BIRTH_DATE_ESTIMATED_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.DEATH_DATE_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.DOB_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.FIRST_NAME_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.GENDER_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.IS_DEAD_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.LAST_NAME_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.MIDDLE_NAME_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.PREFERRED_NAME_FIELD;

@Component
public class PersonAdapter {

    private PersonUpdaterFactory personUpdaterFactory;

    @Autowired
    public PersonAdapter(PersonUpdaterFactory personUpdaterFactory) {
        this.personUpdaterFactory = personUpdaterFactory;
    }


    public MRSPerson createPerson(MRSRegistrationActivity registrationActivity, CommcareFormSegment commcareFormSegment) {
        MRSPerson person = new MRSPersonDto();
        setDefaultProperties(person);
        PersonUpdater personUpdater = personUpdaterFactory.getPersonUpdater(person, registrationActivity.getActivityDate(commcareFormSegment));
        updatePerson(personUpdater, registrationActivity, commcareFormSegment);
        return person;
    }

    private void setDefaultProperties(MRSPerson person) {
        person.setDead(false);
        person.setBirthDateEstimated(false);
    }

    public void updatePerson(MRSPerson person, MRSRegistrationActivity registrationActivity, CommcareFormSegment commcareFormSegment) {
        updatePerson(personUpdaterFactory.getPersonUpdater(person, registrationActivity.getActivityDate(commcareFormSegment)), registrationActivity, commcareFormSegment);
    }

    private void updatePerson(PersonUpdater personUpdater, MRSRegistrationActivity registrationActivity, CommcareFormSegment commcareFormSegment) {
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

        updatePersonFields(personUpdater, firstName, lastName, dateOfBirth, gender, middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, attributes);
    }

    private void updatePersonFields(PersonUpdater personUpdater, String firstName, String lastName, DateTime dateOfBirth, String gender, String middleName, String preferredName, String address, Boolean birthDateIsEstimated,
                                    Integer age, Boolean isDead, DateTime deathDate, List<MRSAttribute> attributes) {

        personUpdater.setFirstName(firstName);
        personUpdater.setLastName(lastName);
        personUpdater.setPreferredName(preferredName);
        personUpdater.setMiddleName(middleName);
        personUpdater.setGender(gender);
        personUpdater.setAddress(address);
        personUpdater.setDateOfBirth(dateOfBirth);
        personUpdater.setBirthDateEstimated(birthDateIsEstimated);
        personUpdater.setAge(age);
        personUpdater.setDead(isDead);
        personUpdater.setDeathDate(deathDate);

        if(attributes != null) {
            for (MRSAttribute attribute : attributes) {
                personUpdater.addAttribute(attribute);
            }
        }
    }
}
