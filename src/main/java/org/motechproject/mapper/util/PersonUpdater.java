package org.motechproject.mapper.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mapper.service.PersonFieldUpdateStrategy;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSAttributeDto;

import java.util.ArrayList;
import java.util.List;

public class PersonUpdater implements MRSPerson {

    private MRSPerson person;
    private PersonFieldUpdateStrategy updateStrategy;

    public PersonUpdater(MRSPerson person, PersonFieldUpdateStrategy updateStrategy) {
        this.person = person;
        this.updateStrategy = updateStrategy;
    }

    @Override
    public String getPersonId() {
        return person.getPersonId();
    }

    @Override
    public void setPersonId(String id) {
        String fieldName = "id";
        if(updateStrategy.canUpdate(fieldName, id)) {
            person.setPersonId(id);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public String getFirstName() {
        return person.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        String fieldName = "firstName";
        if(updateStrategy.canUpdate(fieldName, firstName)) {
            person.setFirstName(firstName);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public String getMiddleName() {
        return person.getMiddleName();
    }

    @Override
    public void setMiddleName(String middleName) {
        String fieldName = "middleName";
        if(updateStrategy.canUpdate(fieldName, middleName)) {
            person.setMiddleName(middleName);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public String getLastName() {
        return person.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        String fieldName = "lastName";
        if(updateStrategy.canUpdate(fieldName, lastName)) {
            person.setLastName(lastName);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public String getPreferredName() {
        return person.getPreferredName();
    }

    @Override
    public void setPreferredName(String preferredName) {
        String fieldName = "preferredName";
        if(updateStrategy.canUpdate(fieldName, preferredName)) {
            person.setPreferredName(preferredName);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public String getAddress() {
        return person.getAddress();
    }

    @Override
    public void setAddress(String address) {
        String fieldName = "address";
        if(updateStrategy.canUpdate(fieldName, address)) {
            person.setAddress(address);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public DateTime getDateOfBirth() {
        return person.getDateOfBirth();
    }

    @Override
    public void setDateOfBirth(DateTime dateOfBirth) {
        String fieldName = "dateOfBirth";
        if(updateStrategy.canUpdate(fieldName, dateOfBirth)) {
            person.setDateOfBirth(dateOfBirth);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public Boolean getBirthDateEstimated() {
        return person.getBirthDateEstimated();
    }

    @Override
    public void setBirthDateEstimated(Boolean birthDateEstimated) {
        String fieldName = "birthDateEstimated";
        if(updateStrategy.canUpdate(fieldName, birthDateEstimated)) {
            person.setBirthDateEstimated(birthDateEstimated);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public Integer getAge() {
        return person.getAge();
    }

    @Override
    public void setAge(Integer age) {
        String fieldName = "age";
        if(updateStrategy.canUpdate(fieldName, age)) {
            person.setAge(age);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public String getGender() {
        return person.getGender();
    }

    @Override
    public void setGender(String gender) {
        String fieldName = "gender";
        if(updateStrategy.canUpdate(fieldName, gender)) {
            person.setGender(gender);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public Boolean isDead() {
        return person.isDead();
    }

    @Override
    public void setDead(Boolean dead) {
        String fieldName = "dead";
        if(updateStrategy.canUpdate(fieldName, dead)) {
            person.setDead(dead);
            updateStrategy.markUpdated(fieldName);
        }
    }

    @Override
    public List<MRSAttribute> getAttributes() {
        return person.getAttributes();
    }

    @Override
    public void setAttributes(List<MRSAttribute> attributes) {
        throw new UnsupportedOperationException("setAttributes is not implemented on Updater. Use addAttribute instead");
    }

    @Override
    public DateTime getDeathDate() {
        return person.getDeathDate();
    }

    @Override
    public void setDeathDate(DateTime deathDate) {
        String fieldName = "deathDate";
        if(updateStrategy.canUpdate(fieldName, deathDate)) {
            person.setDeathDate(deathDate);
            updateStrategy.markUpdated(fieldName);
        }
    }

    public void addAttribute(MRSAttribute attribute) {
        String attributeName = attribute.getName();
        String attributeValue = attribute.getValue();

        if(!updateStrategy.canUpdate(attributeName, attributeValue)) {
            return;
        }

        updateStrategy.markUpdated(attributeName);

        List<MRSAttribute> existingAttributes = person.getAttributes();
        if(existingAttributes == null) {
            existingAttributes = new ArrayList<>();
            person.setAttributes(existingAttributes);
        }

        for(MRSAttribute existingAttribute: existingAttributes) {
            if(StringUtils.equals(existingAttribute.getName(), attributeName)) {
                existingAttribute.setValue(attributeValue);
                return;
            }
        }

        existingAttributes.add(new MRSAttributeDto(attributeName, attributeValue));
    }
}
