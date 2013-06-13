package org.motechproject.mapper.domain;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.util.CommcareFormSegment;

public class MappingVersion {

    public static MappingVersion wildCardVersion = new MappingVersion(null, null);
    private String field;
    private String equals;

    public MappingVersion(String field, String equals) {
        this.field = field;
        this.equals = equals;
    }

    public boolean matches(CommcareFormSegment formSegment) {
        if(isWildcard()) {
            return true;
        }
        FormNode node = formSegment.search(field);
        if(node == null) {
            return false;
        }
        String nodeValue = node.getValue();
        return nodeValue != null && nodeValue.equals(equals);

    }

    public boolean isWildcard() {
        return equals == null || "*".equals(equals);
    }
}
