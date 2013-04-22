package org.motechproject.mapper.adapters.impl;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.adapters.FormAdapter;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.MRSMapping;
import org.motechproject.mapper.repository.MappingsReader;
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
    private MappingsReader mappingsReader;
    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    @Autowired
    public AllFormsAdapter(AllEncountersAdapter encounterAdapter, AllRegistrationsAdapter registrationAdapter, MappingsReader mappingsReader) {
        this.encounterAdapter = encounterAdapter;
        this.registrationAdapter = registrationAdapter;
        this.mappingsReader = mappingsReader;
    }

    @Override
    public void adaptForm(CommcareForm form) {
        List<MRSMapping> mappings = mappingsReader.getAllMappings();

        String formName = form.getForm().getAttributes().get(FORM_NAME_ATTRIBUTE);

        String xmlns = form.getForm().getAttributes().get(FORM_XMLNS_ATTRIBUTE);

        logger.info("Received form: " + formName);

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
            logger.info("Adapting registration form: " + formName);
            return registrationAdapter;
        } else if (ENCOUNTER_ACTIVITY.equals(activityType)) {
            logger.info("Adapting encounter form: " + formName);
            return encounterAdapter;
        }
        return null;
    }
}
