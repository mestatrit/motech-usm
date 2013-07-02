package org.motechproject.mapper.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.PersonUpdater;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.server.config.SettingsFacade;

import java.util.Properties;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PersonUpdaterFactoryTest {

    @Mock
    private SettingsFacade settings;

    @Mock
    private Properties properties;

    private PersonUpdaterFactory updaterFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        updaterFactory = new PersonUpdaterFactory(settings);
        when(settings.getProperties(FormMappingConstants.MAPPING_CONFIGURATION_FILE_NAME)).thenReturn(properties);
    }

    @Test
    public void shouldReturnNonStaleUpdateStrategyIfPropertySetToTrue() {
        when(properties.getProperty(FormMappingConstants.AVOID_STALE_REGISTRATION_UPDATES)).thenReturn("TruE");

        MRSPerson person = new MRSPersonDto();
        DateTime currentUpdatedTime = DateTime.now();

        PersonUpdater personUpdater = updaterFactory.getPersonUpdater(person, currentUpdatedTime);

        assertReflectionEquals(new PersonUpdater(person, new NonStalePersonFieldUpdateStrategy(person, currentUpdatedTime)), personUpdater);
    }

    @Test
    public void shouldReturnNonNullUpdateStrategyIfPropertyNotSetToTrue() {
        when(properties.getProperty(FormMappingConstants.AVOID_STALE_REGISTRATION_UPDATES)).thenReturn("somethingelse");

        MRSPerson person = new MRSPersonDto();
        DateTime currentUpdatedTime = DateTime.now();

        PersonUpdater personUpdater = updaterFactory.getPersonUpdater(person, currentUpdatedTime);

        assertReflectionEquals(new PersonUpdater(person, new NonNullPersonFieldUpdateStrategy()), personUpdater);
    }

    @Test
    public void shouldReturnNonNullUpdateStrategyIfPropertyNotSet() {
        when(properties.getProperty(FormMappingConstants.AVOID_STALE_REGISTRATION_UPDATES)).thenReturn(null);

        MRSPerson person = new MRSPersonDto();
        DateTime currentUpdatedTime = DateTime.now();

        PersonUpdater personUpdater = updaterFactory.getPersonUpdater(person, currentUpdatedTime);

        assertReflectionEquals(new PersonUpdater(person, new NonNullPersonFieldUpdateStrategy()), personUpdater);
    }
}
