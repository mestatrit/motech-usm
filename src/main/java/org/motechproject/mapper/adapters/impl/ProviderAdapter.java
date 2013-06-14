package org.motechproject.mapper.adapters.impl;


import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.service.MRSMappingService;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.MRSMappingVersionMatchStrategy;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.model.MRSProviderDto;
import org.motechproject.mrs.services.MRSProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProviderAdapter {

    public static final String PROVIDER_XML_NS = "http://bihar.commcarehq.org/motech/provider";

    private PersonAdapter personAdapter;
    private MRSProviderAdapter providerAdapter;
    private MRSMappingService mappingService;
    private AllElementSearchStrategies allElementSearchStrategies;
    private MRSMappingVersionMatchStrategy mappingVersionMatchStrategy;

    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Autowired
    public ProviderAdapter(PersonAdapter personAdapter, MRSProviderAdapter providerAdapter, MRSMappingService mappingService,
                           AllElementSearchStrategies allElementSearchStrategies, MRSMappingVersionMatchStrategy mappingVersionMatchStrategy) {
        this.personAdapter = personAdapter;
        this.providerAdapter = providerAdapter;
        this.mappingService = mappingService;
        this.allElementSearchStrategies = allElementSearchStrategies;
        this.mappingVersionMatchStrategy = mappingVersionMatchStrategy;
    }

    public void adaptForm(CommcareForm commcareForm) {
        logger.info("Processing provider information");
        CommcareFormSegment formSegment = new CommcareFormSegment(commcareForm, commcareForm.getForm(), null, allElementSearchStrategies);

        String providerId = getProviderId(formSegment);
        if(providerId == null) {
            logger.error("Provider id could not be obtained");
            return;
        }

        MRSProvider provider = providerAdapter.getProviderByProviderId(providerId);
        if(provider != null) {
            logger.info(String.format("Deleting existing provider with id: %s", providerId));
            providerAdapter.removeProvider(providerId);
        }

        logger.info(String.format("Creating a provider with id: %s", providerId));
        createAndSaveProvider(formSegment);
    }

    private void createAndSaveProvider(CommcareFormSegment formSegment) {
        MRSRegistrationActivity activity = getProviderRegistrationActivity();

        String providerId = getProviderId(formSegment);

        MRSPerson person = createPerson(formSegment, activity);
        MRSProvider provider = new MRSProviderDto();
        provider.setPerson(person);
        provider.setProviderId(providerId);
        providerAdapter.saveProvider(provider);
        logger.info(String.format("Successfully created a provider. Provider Id: %s, Person Id: %s", providerId, person.getPersonId()));
    }

    private MRSRegistrationActivity getProviderRegistrationActivity() {
        MRSMapping mapping = mappingVersionMatchStrategy.findBestMatch(mappingService.findAllMappingsForXmlns(PROVIDER_XML_NS), null);
        if(mapping == null) {
            RuntimeException e = new RuntimeException("Could not find provider mapping");
            logger.error("Could not find provider mapping", e);
            throw e;
        }
        for(MRSActivity activity: mapping.getActivities()) {
            if(FormMappingConstants.REGISTRATION_ACTIVITY.equals(activity.getType())) {
                return (MRSRegistrationActivity) activity;
            }
        }
        RuntimeException e = new RuntimeException("Could not find registration activity for provider");
        logger.error("Could not find registration activity for provider", e);
        throw e;
    }

    private MRSPerson createPerson(CommcareFormSegment formSegment, MRSRegistrationActivity activity) {
        String personId = UUID.randomUUID().toString();
        MRSPerson person = personAdapter.createPerson(activity, formSegment);
        person.setPersonId(personId);
        return person;
    }

    private String getProviderId(CommcareFormSegment formSegment) {
        FormNode idElement = formSegment.search("/form/id");
        return idElement == null ? null : idElement.getValue();
    }
}
