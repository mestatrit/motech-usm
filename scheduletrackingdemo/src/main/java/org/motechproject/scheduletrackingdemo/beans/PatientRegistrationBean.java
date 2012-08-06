package org.motechproject.scheduletrackingdemo.beans;

import java.util.Date;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.scheduletrackingdemo.validator.MinimumLength;

public class PatientRegistrationBean extends FormBean {

    private static final long serialVersionUID = 1L;

    @MinimumLength(size = 3)
    private String motechId;

    private String firstName;
    private String lastName;
    private String gender;
    private String location;
    private Date dateOfBirth;
    private String phoneNumber;
    private boolean enrollPatient;
    private String groupId;

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isEnrollPatient() {
        return enrollPatient;
    }

    public void setEnrollPatient(boolean enrollPatient) {
        this.enrollPatient = enrollPatient;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String groupId() {
        return groupId;
    }
}
