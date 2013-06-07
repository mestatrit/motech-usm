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
            allMRSMappings.addOrUpdate(mrsMapping);
        }
    }

    public List<MRSMapping> getAllMappings() {
        return allMRSMappings.getAll();
    }

    public void deleteMapping(String xmlns) {
        String fieldName = "xmlns";
        allMRSMappings.removeAll(fieldName, xmlns);
    }

    public void deleteAllMappings() {
        allMRSMappings.removeAll();
    }

    public MRSMapping findByXmlns(String xmlns) {
        return allMRSMappings.findByXmlns(xmlns);
    }
}
