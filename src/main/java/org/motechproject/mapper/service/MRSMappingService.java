package org.motechproject.mapper.service;

import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.repository.AllMRSMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MRSMappingService {

    private AllMRSMappings allMRSMappings;

    @Autowired
    public MRSMappingService(AllMRSMappings allMRSMappings) {
        this.allMRSMappings = allMRSMappings;
    }

    public void addOrUpdate(List<MRSMapping> mrsMappings) {
        for (MRSMapping mrsMapping : mrsMappings) {
           addOrUpdate(mrsMapping);
        }
    }

    private void addOrUpdate(MRSMapping mrsMapping) {
        MRSMapping existingMapping = allMRSMappings.findByXmlnsAndVersion(mrsMapping.getXmlns(), mrsMapping.getVersion());
        if(existingMapping != null) {
            allMRSMappings.remove(existingMapping);
        }
        allMRSMappings.add(mrsMapping);
    }

    public List<MRSMapping> getAllMappings() {
        return allMRSMappings.getAll();
    }

    public boolean deleteMapping(String id) {
        return allMRSMappings.deleteMapping(id);
    }

    public void deleteAllMappings() {
        allMRSMappings.removeAll();
    }

    public MRSMapping findMatchingMappingFor(String xmlns, String version) {
        List<MRSMapping> mappings = allMRSMappings.findByXmlns(xmlns);
        MRSMapping defaultMapping = null;
        for (MRSMapping mapping : mappings) {
            if(mapping.hasWildcardVersion()) {
                defaultMapping = mapping;
                continue;
            }
            if(mapping.matchesVersion(version)) {
                return mapping;
            }
        }
        return defaultMapping;
    }
}
