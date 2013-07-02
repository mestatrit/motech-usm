package org.motechproject.mapper.service;

import org.joda.time.DateTime;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.PersonUpdater;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PersonUpdaterFactory {

    private SettingsFacade settings;

    @Autowired
    public PersonUpdaterFactory(@Qualifier("commcareMapperSettings") SettingsFacade settings) {
        this.settings = settings;
    }

    public PersonUpdater getPersonUpdater(MRSPerson person, DateTime currentUpdateTime) {
        return new PersonUpdater(person, getUpdateStrategy(person, currentUpdateTime));
    }

    private PersonFieldUpdateStrategy getUpdateStrategy(MRSPerson person, DateTime currentUpdateTime) {
        return shouldAvoidStaleRegistrationUpdates() ? new NonStalePersonFieldUpdateStrategy(person, currentUpdateTime) : new NonNullPersonFieldUpdateStrategy();
    }

    private boolean shouldAvoidStaleRegistrationUpdates() {
        String propertyValue = settings.getProperties(FormMappingConstants.MAPPING_CONFIGURATION_FILE_NAME).getProperty(FormMappingConstants.AVOID_STALE_REGISTRATION_UPDATES);
        return Boolean.parseBoolean(propertyValue);
    }
}
