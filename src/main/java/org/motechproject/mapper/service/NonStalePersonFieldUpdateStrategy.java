package org.motechproject.mapper.service;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSAttributeDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NonStalePersonFieldUpdateStrategy implements PersonFieldUpdateStrategy {

    protected static final String MODIFIED_AT_SUFFIX = "_modified_at";

    private Map<String, DateTime> lastModifiedAtMap = new HashMap<>();
    private MRSPerson person;
    protected DateTime currentUpdateTime;

    public NonStalePersonFieldUpdateStrategy(MRSPerson person, DateTime currentUpdateTime) {
        this.person = person;
        this.currentUpdateTime = currentUpdateTime;
        fetchLastModifiedMap(person);
    }

    private void fetchLastModifiedMap(MRSPerson person) {
        List<MRSAttribute> attributes = person.getAttributes();
        if(attributes == null) {
            return;
        }
        for (MRSAttribute attribute : attributes) {
            String name = attribute.getName();

            if(name.endsWith(MODIFIED_AT_SUFFIX)) {
                lastModifiedAtMap.put(name, DateTime.parse(attribute.getValue()));
            }
        }
    }

    @Override
    public boolean canUpdate(String fieldName, Object fieldValue) {
        if(fieldValue == null) {
            return false;
        }

        DateTime lastModifiedAt = lastModifiedAtMap.get(constructLastModifiedAtKey(fieldName));
        return lastModifiedAt == null || !lastModifiedAt.isAfter(currentUpdateTime);
    }

    @Override
    public void markUpdated(String fieldName) {
        List<MRSAttribute> attributes = person.getAttributes();
        if(attributes == null) {
            attributes = new ArrayList<>();
            attributes.add(new MRSAttributeDto(constructLastModifiedAtKey(fieldName), currentUpdateTime.toString()));
            person.setAttributes(attributes);
            return;
        }
        for (MRSAttribute attribute : attributes) {
            if(constructLastModifiedAtKey(fieldName).equals(attribute.getName())) {
                attribute.setValue(currentUpdateTime.toString());
                return;
            }

        }
        attributes.add(new MRSAttributeDto(constructLastModifiedAtKey(fieldName), currentUpdateTime.toString()));
    }


    protected String constructLastModifiedAtKey(String fieldName) {
        return String.format("_%s%s", fieldName, MODIFIED_AT_SUFFIX);
    }
}
