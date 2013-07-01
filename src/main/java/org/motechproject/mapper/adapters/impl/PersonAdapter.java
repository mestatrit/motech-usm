package org.motechproject.mapper.adapters.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.service.PersonFieldUpdateStrategy;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSPersonDto;
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

    public MRSPerson createPerson(MRSRegistrationActivity registrationActivity, CommcareFormSegment commcareFormSegment, PersonFieldUpdateStrategy updateStrategy) {
        MRSPerson person = new MRSPersonDto();
        setDefaultProperties(person);
        updatePerson(person, registrationActivity, commcareFormSegment, updateStrategy);
        return person;
    }

    private void setDefaultProperties(MRSPerson person) {
        person.setDead(false);
        person.setBirthDateEstimated(false);
    }

    public void updatePerson(MRSPerson person, MRSRegistrationActivity registrationActivity, CommcareFormSegment commcareFormSegment, PersonFieldUpdateStrategy updateStrategy) {
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

        updatePersonFields(person, firstName, lastName, dateOfBirth, gender, middleName, preferredName, address, birthDateIsEstimated, age, isDead, deathDate, attributes, updateStrategy);
    }

    private void updatePersonFields(MRSPerson person, String firstName, String lastName, DateTime dateOfBirth, String gender, String middleName, String preferredName, String address, Boolean birthDateIsEstimated,
                                    Integer age, Boolean isDead, DateTime deathDate, List<MRSAttribute> attributes, PersonFieldUpdateStrategy updateStrategy) {
        if (updateStrategy.canUpdateField("firstName", firstName)) {
            person.setFirstName(firstName);
        }
        if (updateStrategy.canUpdateField("lastName", lastName)) {
            person.setLastName(lastName);
        }
        if (updateStrategy.canUpdateField("middleName", middleName)) {
            person.setMiddleName(middleName);
        }
        if (updateStrategy.canUpdateField("preferredName", preferredName)) {
            person.setPreferredName(preferredName);
        }
        if (updateStrategy.canUpdateField("gender", gender)) {
            person.setGender(gender);
        }
        if (updateStrategy.canUpdateField("address", address)) {
            person.setAddress(address);
        }
        if (updateStrategy.canUpdateField("dateOfBirth", dateOfBirth)) {
            person.setDateOfBirth(dateOfBirth);
        }
        if (updateStrategy.canUpdateField("birthDateIsEstimated", birthDateIsEstimated)) {
            person.setBirthDateEstimated(birthDateIsEstimated);
        }
        if (updateStrategy.canUpdateField("age", age)) {
            person.setAge(age);
        }
        if (updateStrategy.canUpdateField("isDead", isDead)) {
            person.setDead(isDead);
        }
        if (updateStrategy.canUpdateField("deathDate", deathDate)) {
            person.setDeathDate(deathDate);
        }
        if(attributes != null) {
            updateAttributes(person, attributes, updateStrategy);
        }
    }


    private List<MRSAttribute> updateAttributes(MRSPerson existingPerson, List<MRSAttribute> newAttributes, final PersonFieldUpdateStrategy updateStrategy) {
        final List<MRSAttribute> existingAttributes = existingPerson.getAttributes();
        CollectionUtils.filter(newAttributes, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                MRSAttribute newAttribute = (MRSAttribute) object;
                if(!updateStrategy.canUpdateField(newAttribute.getName(), newAttribute.getValue())) {
                    return false;
                }
                for (MRSAttribute existingAttribute : existingAttributes) {
                    if (StringUtils.equals(existingAttribute.getName(), newAttribute.getName())) {
                        existingAttribute.setValue(newAttribute.getValue());
                        return false;
                    }
                }
                return true;
            }
        });
        existingAttributes.addAll(newAttributes);
        return existingAttributes;
    }
}
