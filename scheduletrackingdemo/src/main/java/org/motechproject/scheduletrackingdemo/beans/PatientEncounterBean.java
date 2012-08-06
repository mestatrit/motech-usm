package org.motechproject.scheduletrackingdemo.beans;

import org.joda.time.DateTime;
import org.motechproject.mobileforms.api.domain.FormBean;

public class PatientEncounterBean extends FormBean {

    private static final long serialVersionUID = 1L;
    private String motechId;
    private DateTime observedDate;
    private Integer observedConcept;
    private String groupId;
    private String locationName;

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public DateTime getObservedDate() {
        return observedDate;
    }

    public void setObservedDate(DateTime observedDate) {
        this.observedDate = observedDate;
    }

    public Integer getObservedConcept() {
        return observedConcept;
    }

    public void setObservedConcept(Integer observedConcept) {
        this.observedConcept = observedConcept;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String groupId() {
        return groupId;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }
}
