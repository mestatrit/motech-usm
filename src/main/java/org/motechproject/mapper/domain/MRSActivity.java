package org.motechproject.mapper.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.util.CommcareFormSegment;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MRSEncounterActivity.class, name = "encounterActivity"),
        @JsonSubTypes.Type(value = MRSRegistrationActivity.class, name = "registrationActivity")
})
public class MRSActivity {
    private String type;
    private Map<String, String> patientIdScheme;
    private Map<String, String> facilityScheme;
    private Map<String, String> providerScheme;
    private FormMapperProperties formMapperProperties;

    public MRSActivity() {
        formMapperProperties = new FormMapperProperties();
    }

    public FormMapperProperties getFormMapperProperties() {
        return formMapperProperties;
    }

    public void setFormMapperProperties(FormMapperProperties formMapperProperties) {
        this.formMapperProperties = formMapperProperties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getPatientIdScheme() {
        return patientIdScheme;
    }

    public void setPatientIdScheme(Map<String, String> patientIdScheme) {
        this.patientIdScheme = patientIdScheme;
    }

    public Map<String, String> getFacilityScheme() {
        return facilityScheme;
    }

    public void setFacilityScheme(Map<String, String> facilityScheme) {
        this.facilityScheme = facilityScheme;
    }

    public Map<String, String> getProviderScheme() {
        return providerScheme;
    }

    public void setProviderScheme(Map<String, String> providerScheme) {
        this.providerScheme = providerScheme;
    }

    protected DateTime getActivityDate(CommcareFormSegment beneficiarySegment, Map<String, String> mappings, String dateFieldPathKey) {
        String receivedOn = beneficiarySegment.getReceivedOn();
        DateTime activityDate = StringUtils.isNotEmpty(receivedOn) ? DateTime.parse(receivedOn) : DateTime.now();

        if (mappings == null) {
            return activityDate;
        }
        String dateFieldPath = mappings.get(dateFieldPathKey);
        if (StringUtils.isEmpty(dateFieldPath)) {
            return activityDate;
        }
        FormNode dateFieldNode = beneficiarySegment.search(dateFieldPath);
        if (dateFieldNode == null || StringUtils.isEmpty(dateFieldNode.getValue())) {
            return activityDate;
        }
        return DateTime.parse(dateFieldNode.getValue());
    }
}
