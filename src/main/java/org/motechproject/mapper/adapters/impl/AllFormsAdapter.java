package org.motechproject.mapper.adapters.impl;

import java.util.List;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.adapters.FormAdapter;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.OpenMRSMapping;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.repository.MappingsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllFormsAdapter implements FormAdapter {

    @Autowired
    private AllEncountersAdapter encounterAdapter;

    @Autowired
    private AllRegistrationsAdapter registrationAdapter;

    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    @Override
    public void adaptForm(CommcareForm form) {
        List<OpenMRSMapping> mappings = MappingsReader.getAllMappings();
        
        String formName = form.getForm().getAttributes().get(FormMappingConstants.FORM_NAME_ATTRIBUTE);
        
        String xmlns = form.getForm().getAttributes().get(FormMappingConstants.FORM_XMLNS_ATTRIBUTE);
        
        logger.info("Received form: " + formName);

        for (OpenMRSMapping mapping : mappings) {
            if (mapping.getXmlns().equals(xmlns)) {
                for (MRSActivity activity : mapping.getActivities()) {
                    if ("registration".equals(activity.getType())) {
                        logger.info("Adapting registration form: " + formName);
                        registrationAdapter.adaptForm(form, activity);
                    } else if ("encounter".equals(activity.getType())) {
                        logger.info("Adapting encounter form: " + formName);
                        encounterAdapter.adaptForm(form, activity);
                    }
                }
                return;
            }
        }

    }
}
