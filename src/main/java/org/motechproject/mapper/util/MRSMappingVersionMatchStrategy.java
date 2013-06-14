package org.motechproject.mapper.util;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.motechproject.mapper.domain.MRSMapping;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class MRSMappingVersionMatchStrategy {

    public MRSMapping findBestMatch(List<MRSMapping> mappings, String versionToMatch) {
        MRSMapping defaultMapping = null;
        sort(mappings);
        for (MRSMapping mapping : mappings) {
            if(mapping.hasWildcardVersion()) {
                defaultMapping = mapping;
                continue;
            }
            if(mapping.matchesVersion(versionToMatch)) {
                return mapping;
            }
        }
        return defaultMapping;
    }

    private void sort(List<MRSMapping> mrsMappings) {
        Collections.sort(mrsMappings, Collections.reverseOrder(new Comparator<MRSMapping>() {
            private int getVersionLength(MRSMapping mrsMapping) {
                return mrsMapping == null || mrsMapping.getVersion() == null ? 0 : mrsMapping.getVersion().length();
            }

            @Override
            public int compare(MRSMapping o1, MRSMapping o2) {
                return new CompareToBuilder()
                        .append(getVersionLength(o1), getVersionLength(o2))
                        .toComparison();
            }
        }));
    }
}
