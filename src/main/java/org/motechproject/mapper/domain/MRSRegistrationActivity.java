package org.motechproject.mapper.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.ExpressionUtil;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.model.MRSAttributeDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MRSRegistrationActivity extends MRSActivity {

    private Map<String, String> attributes = new HashMap<>();
    private Map<String, String> registrationMappings = new HashMap<>();
    private Map<String, String> staticMappings = new HashMap<>();

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getRegistrationMappings() {
        return registrationMappings;
    }

    public void setRegistrationMappings(Map<String, String> registrationMappings) {
        this.registrationMappings = registrationMappings;
    }

    public Map<String, String> getStaticMappings() {
        return staticMappings;
    }

    public void setStaticMappings(Map<String, String> staticMappings) {
        this.staticMappings = staticMappings;
    }

    public <T> T getValueFor(String fieldName, CommcareFormSegment beneficiarySegment, Class<T> returnType) {
        Map<String, String> registrationMappings = getRegistrationMappings();
        if (registrationMappings == null) return null;
        String fieldValue = registrationMappings.get(fieldName);
        if (fieldValue != null) {
            return ExpressionUtil.resolve(fieldValue, beneficiarySegment, returnType);
        }
        return getDefaultValue(fieldName);
    }

    private <T> T getDefaultValue(String fieldName) {
        Map<String, String> staticMappings = getStaticMappings();

        if(staticMappings != null) {
            return (T) staticMappings.get(fieldName);
        }
        return null;
    }

    public List<MRSAttribute> getMRSAttributes(CommcareFormSegment beneficiarySegment) {
        List<MRSAttribute> attributes = new ArrayList<>();
        Map<String, String> mappedAttributes = getAttributes();
        if (mappedAttributes != null) {
            for (Map.Entry<String, String> entry : mappedAttributes.entrySet()) {
                String attributeValue = ExpressionUtil.resolve(entry.getValue(), beneficiarySegment);
                if (StringUtils.isBlank(attributeValue)) {
                    continue;
                }
                String attributeName = entry.getKey();
                MRSAttributeDto attribute = new MRSAttributeDto(attributeName, attributeValue);
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    public DateTime getActivityDate(CommcareFormSegment beneficiarySegment) {
        return getActivityDate(beneficiarySegment, registrationMappings, FormMappingConstants.REGISTRATION_DATE_FIELD);
    }
}
