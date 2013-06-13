package org.motechproject.mapper.util;

import org.motechproject.mapper.domain.MRSMapping;

import java.util.ArrayList;
import java.util.List;

public class AllMRSMappings extends ArrayList<MRSMapping> {

    public AllMRSMappings(List<MRSMapping> mappings) {
        super(mappings);
    }

    public MRSMapping findMappingForVersion(CommcareFormSegment formSegment) {
        return null;
    }
}
