package org.motechproject.mapper.adapters.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.adapters.FormAdapter;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.service.MRSMappingService;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.mapper.constants.FormMappingConstants.ENCOUNTER_ACTIVITY;
import static org.motechproject.mapper.constants.FormMappingConstants.FORM_NAME_ATTRIBUTE;
import static org.motechproject.mapper.constants.FormMappingConstants.FORM_XMLNS_ATTRIBUTE;
import static org.motechproject.mapper.constants.FormMappingConstants.REGISTRATION_ACTIVITY;

@Component
public class AllFormsAdapter implements FormAdapter {

    private AllEncountersAdapter encounterAdapter;
    private AllRegistrationsAdapter registrationAdapter;
    private MRSMappingService mrsMappingService;
    private SettingsFacade settings;
    private AllElementSearchStrategies allElementSearchStrategies;
    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Autowired
    public AllFormsAdapter(AllEncountersAdapter encounterAdapter, AllRegistrationsAdapter registrationAdapter,
                           MRSMappingService mrsMappingService, @Qualifier("commcareMapperSettings") SettingsFacade settings,
                           AllElementSearchStrategies allElementSearchStrategies) {
        this.encounterAdapter = encounterAdapter;
        this.registrationAdapter = registrationAdapter;
        this.mrsMappingService = mrsMappingService;
        this.settings = settings;
        this.allElementSearchStrategies = allElementSearchStrategies;
    }

    @Override
    public void adaptForm(CommcareForm form) {
        String formName = form.getForm().getAttributes().get(FORM_NAME_ATTRIBUTE);
        String xmlns = form.getForm().getAttributes().get(FORM_XMLNS_ATTRIBUTE);
        String version = findFormVersion(form);

        logger.info(String.format("Received form of type: %s; xmlns: %s; version: %s", formName, xmlns, version == null ? "N/A" : version));

        MRSMapping mapping = mrsMappingService.findMatchingMappingFor(xmlns, version);
        if(mapping == null) {
            logger.warn(String.format("Could not find mappings for form of type: %s; xmlns: %s; version: %s", formName, xmlns, version == null ? "N/A" : version));
            return;
        }

        for (MRSActivity activity : mapping.getActivities()) {
            ActivityFormAdapter adapter = getAdapter(activity.getType(), formName);
            if (adapter != null)
                adapter.adaptForm(form, activity);
        }
    }

    private String findFormVersion(CommcareForm form) {
        String formVersionField = settings.getProperties(FormMappingConstants.MAPPING_CONFIGURATION_FILE_NAME).getProperty(FormMappingConstants.FORM_VERSION_PATH);
        if(StringUtils.isBlank(formVersionField)) {
            return null;
        }

        CommcareFormSegment formSegment = new CommcareFormSegment(form, form.getForm(), null, allElementSearchStrategies);

        FormNode versionNode = formSegment.search(formVersionField);
        return versionNode == null ? null : versionNode.getValue();
    }

    private MRSMapping findAppropriateMappingForVersion(List<MRSMapping> mappings, CommcareForm form) {
        MRSMapping wildCardMapping = null;

        for (MRSMapping mapping : mappings) {
            if(mapping.hasWildcardVersion()) {
                wildCardMapping = mapping;
                continue;
            }

        }
        return mappings.get(0);
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
