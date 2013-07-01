package org.motechproject.mapper.adapters.impl;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.builder.FormBuilder;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.service.MRSMappingService;
import org.motechproject.mapper.service.NonNullPersonFieldUpdateStrategy;
import org.motechproject.mapper.service.PersonFieldUpdateStrategy;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.MRSMappingVersionMatchStrategy;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.model.MRSProviderDto;
import org.motechproject.mrs.services.MRSProviderAdapter;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProviderAdapterTest {

    private ProviderAdapter providerAdapter;

    @Mock
    private MRSProviderAdapter mrsProviderAdapter;

    @Mock
    private MRSMappingService mrsMappingService;

    @Mock
    private AllElementSearchStrategies allElementSearchStrategies;

    @Mock
    private MRSMappingVersionMatchStrategy mappingVersionMatchStrategy;

    @Mock
    private PersonAdapter personAdapter;

    @Before
    public void setup() {
        initMocks(this);
        providerAdapter = new ProviderAdapter(personAdapter, mrsProviderAdapter, mrsMappingService, allElementSearchStrategies, mappingVersionMatchStrategy);
    }

    @Test
    public void shouldCreateAndSaveProvider() {
        String providerId = "myproviderid";

        FormNode idNode = mock(FormNode.class);
        when(idNode.getValue()).thenReturn(providerId);
        CommcareForm commcareForm = new FormBuilder("form").getForm();
        when(allElementSearchStrategies.searchFirst("/form/id", commcareForm.getForm(), commcareForm.getForm(), null)).thenReturn(idNode);

        final MRSRegistrationActivity registrationActivity = mock(MRSRegistrationActivity.class);
        when(registrationActivity.getType()).thenReturn(FormMappingConstants.REGISTRATION_ACTIVITY);
        MRSMapping mrsMapping = mock(MRSMapping.class);
        when(mrsMapping.getActivities()).thenReturn(new ArrayList<MRSActivity>(){{
            add(registrationActivity);
        }});

        List<MRSMapping> mrsMappings = new ArrayList<>();
        when(mrsMappingService.findAllMappingsForXmlns(ProviderAdapter.PROVIDER_XML_NS)).thenReturn(mrsMappings);
        when(mappingVersionMatchStrategy.findBestMatch(mrsMappings, null)).thenReturn(mrsMapping);

        MRSPerson person = new MRSPersonDto();
        ArgumentCaptor<CommcareFormSegment> formSegmentCaptor = ArgumentCaptor.forClass(CommcareFormSegment.class);
        ArgumentCaptor<PersonFieldUpdateStrategy> updateStrategyCaptor = ArgumentCaptor.forClass(PersonFieldUpdateStrategy.class);
        when(personAdapter.createPerson(eq(registrationActivity), formSegmentCaptor.capture(), updateStrategyCaptor.capture())).thenReturn(person);

        providerAdapter.adaptForm(commcareForm);

        verify(mappingVersionMatchStrategy).findBestMatch(mrsMappings, null);

        CommcareFormSegment actualFormSegment = formSegmentCaptor.getValue();
        assertEquals(idNode, actualFormSegment.search("/form/id"));

        ArgumentCaptor<MRSProvider> captor = ArgumentCaptor.forClass(MRSProvider.class);
        verify(mrsProviderAdapter).saveProvider(captor.capture());
        MRSProvider actualProvider = captor.getValue();
        assertEquals(providerId, actualProvider.getProviderId());
        assertEquals(person, actualProvider.getPerson());
        assertTrue(updateStrategyCaptor.getValue() instanceof NonNullPersonFieldUpdateStrategy);
        verify(mrsProviderAdapter, never()).removeProvider(any(String.class));
    }

    @Test
    public void shouldDeleteExistingProviderIfExistsAndSaveNewProvider() {
        String providerId = "myproviderid";

        FormNode idNode = mock(FormNode.class);
        when(idNode.getValue()).thenReturn(providerId);
        CommcareForm commcareForm = new FormBuilder("form").getForm();
        when(allElementSearchStrategies.searchFirst("/form/id", commcareForm.getForm(), commcareForm.getForm(), null)).thenReturn(idNode);

        final MRSRegistrationActivity registrationActivity = mock(MRSRegistrationActivity.class);
        when(registrationActivity.getType()).thenReturn(FormMappingConstants.REGISTRATION_ACTIVITY);
        MRSMapping mrsMapping = mock(MRSMapping.class);
        when(mrsMapping.getActivities()).thenReturn(new ArrayList<MRSActivity>(){{
            add(registrationActivity);
        }});

        List<MRSMapping> mrsMappings = new ArrayList<>();
        when(mrsMappingService.findAllMappingsForXmlns(ProviderAdapter.PROVIDER_XML_NS)).thenReturn(mrsMappings);
        when(mappingVersionMatchStrategy.findBestMatch(mrsMappings, null)).thenReturn(mrsMapping);

        MRSPerson person = new MRSPersonDto();
        ArgumentCaptor<CommcareFormSegment> formSegmentCaptor = ArgumentCaptor.forClass(CommcareFormSegment.class);
        ArgumentCaptor<PersonFieldUpdateStrategy> updateStrategyCaptor = ArgumentCaptor.forClass(PersonFieldUpdateStrategy.class);
        when(personAdapter.createPerson(eq(registrationActivity), formSegmentCaptor.capture(), updateStrategyCaptor.capture())).thenReturn(person);
        when(mrsProviderAdapter.getProviderByProviderId(providerId)).thenReturn(new MRSProviderDto());

        providerAdapter.adaptForm(commcareForm);

        verify(mappingVersionMatchStrategy).findBestMatch(mrsMappings, null);

        CommcareFormSegment actualFormSegment = formSegmentCaptor.getValue();
        assertEquals(idNode, actualFormSegment.search("/form/id"));

        ArgumentCaptor<MRSProvider> captor = ArgumentCaptor.forClass(MRSProvider.class);
        verify(mrsProviderAdapter).saveProvider(captor.capture());
        MRSProvider actualProvider = captor.getValue();
        assertEquals(providerId, actualProvider.getProviderId());
        assertEquals(person, actualProvider.getPerson());

        assertTrue(updateStrategyCaptor.getValue() instanceof NonNullPersonFieldUpdateStrategy);

        verify(mrsProviderAdapter).removeProvider(providerId);
    }

    @Test
    public void shouldIgnoreIfProviderIdIsNotFound() {
        CommcareForm commcareForm = new FormBuilder("form").getForm();
        providerAdapter.adaptForm(commcareForm);

        verifyZeroInteractions(mrsProviderAdapter);
        verifyZeroInteractions(personAdapter);
    }

}
