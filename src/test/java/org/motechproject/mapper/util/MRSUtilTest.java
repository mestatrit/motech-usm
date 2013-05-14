package org.motechproject.mapper.util;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.validation.ValidationError;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.model.MRSEncounterDto;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSProviderAdapter;
import org.motechproject.server.config.SettingsFacade;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MRSUtilTest {
    @Mock
    private MRSEncounterAdapter mrsEncounterAdapter;
    @Mock
    private MRSFacilityAdapter facilityAdapter;
    @Mock
    private MRSProviderAdapter mrsProviderAdapter;
    @Mock
    private MRSPatientAdapter mrsPatientAdapter;
    @Mock
    private ValidationManager validator;
    @Mock
    private SettingsFacade settings;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldValidateTheEncounter() {
        MRSUtil mrsUtil = new MRSUtil(mrsEncounterAdapter, facilityAdapter, mrsProviderAdapter, mrsPatientAdapter, validator, settings);
        MRSPatient patient = new MRSPatientDto();
        Set<MRSObservationDto> mrsObservationDtos = new HashSet<>();
        String providerID = "provider ID";
        String facilityName = "facility Name";
        String encounterType = "encounter type";
        List<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(new ValidationError("error type","error message"));
        when(validator.validateEncounter(any(MRSEncounterDto.class))).thenReturn(validationErrors);
        Properties properties = new Properties();
        properties.put(FormMappingConstants.DESTINATION, FormMappingConstants.DESTINATION_COUCHDB);
        when(settings.getProperties(FormMappingConstants.MAPPING_CONFIGURATION_FILE_NAME)).thenReturn(properties);

        mrsUtil.addEncounter(patient, mrsObservationDtos, providerID, DateTime.now(), facilityName, encounterType);

        verify(mrsEncounterAdapter, never()).createEncounter(any(MRSEncounterDto.class));
    }

}
