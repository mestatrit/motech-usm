package org.motechproject.scheduletracking.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'PATIENT'")
public class Patient extends MotechBaseDataObject {

    @JsonProperty("type")
    private final String type = "PATIENT";

    @JsonProperty
    private String externalid;
    @JsonProperty
    private String phoneNum;

    public Patient() {

    }

    public Patient(String externalid) {
        this.externalid = externalid;
    }

    public Patient(String externalid, String phoneNum) {
        this.externalid = externalid;
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getExternalid() {
        return externalid;
    }

    public void setExternalId(String externalId) {
        this.externalid = externalId;
    }

    @Override
    public boolean equals(Object o) {
        Patient patient = (Patient) o;
        if (patient.getExternalid().equals(externalid))
            return true;
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
