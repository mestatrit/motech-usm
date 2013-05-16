package org.motechproject.mapper.adapters.impl;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.adapters.FormAdapter;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.service.MRSMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.mapper.constants.FormMappingConstants.*;

@Component
public class AllFormsAdapter implements FormAdapter {

    private AllEncountersAdapter encounterAdapter;
    private AllRegistrationsAdapter registrationAdapter;
    private MRSMappingService mrsMappingService;
    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Autowired
    public AllFormsAdapter(AllEncountersAdapter encounterAdapter, AllRegistrationsAdapter registrationAdapter, MRSMappingService mrsMappingService) {
        this.encounterAdapter = encounterAdapter;
        this.registrationAdapter = registrationAdapter;
        this.mrsMappingService = mrsMappingService;
    }

    @Override
    public void adaptForm(CommcareForm form) {
        List<MRSMapping> mappings = mrsMappingService.getAllMappings();

        String formName = form.getForm().getAttributes().get(FORM_NAME_ATTRIBUTE);

        String xmlns = form.getForm().getAttributes().get(FORM_XMLNS_ATTRIBUTE);

        logger.info(String.format("Received form of type(%s) and xmlns(%s) ", formName, xmlns));

        for (MRSMapping mapping : mappings) {
            if (mapping.getXmlns().equals(xmlns)) {
                for (MRSActivity activity : mapping.getActivities()) {
                    ActivityFormAdapter adapter = getAdapter(activity.getType(), formName);
                    if (adapter != null)
                        adapter.adaptForm(form, activity);
                }
                return;
            }
        }
    }

    private ActivityFormAdapter getAdapter(String activityType, String formName) {
        if (REGISTRATION_ACTIVITY.equals(activityType)) {
            logger.info("Adapting registration activity for: " + formName);
            return registrationAdapter;
        } else if (ENCOUNTER_ACTIVITY.equals(activityType)) {
            logger.info("Adapting encounter activity for: " + formName);
            return encounterAdapter;
        }
        return null;
    }
}
