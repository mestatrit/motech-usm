package org.motechproject.mapper.adapters.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueAttribute;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.service.MRSMappingService;
import org.motechproject.mapper.service.MappingsReader;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.server.config.SettingsFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllFormsAdapterTest {

    @Mock
    private AllEncountersAdapter encounterAdapter;

    @Mock
    private AllRegistrationsAdapter registrationsAdapter;

    @Mock
    private MappingsReader mappingsReader;

    @Mock
    private MRSMappingService mrsMappingService;

    @Mock
    private SettingsFacade settings;

    @Mock
    private AllElementSearchStrategies allSearchStrategies;

    @Mock
    private Properties properties;

    private AllFormsAdapter formsAdapter;

    @Before
    public void setup() {
        initMocks(this);
        when(settings.getProperties(FormMappingConstants.MAPPING_CONFIGURATION_FILE_NAME)).thenReturn(properties);
        formsAdapter = new AllFormsAdapter(encounterAdapter, registrationsAdapter, mrsMappingService, settings, allSearchStrategies);
    }

    @Test
    public void shouldAdaptForRegistrationWhenActivityTypeIsRegistration() {
        String xmlns = "myxmlns";
        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, xmlns);
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);

        MRSMapping mapping = new MRSMapping();
        List<MRSActivity> activities = new ArrayList<>();
        MRSActivity activity = new MRSActivity();
        activity.setType("registration");
        activities.add(activity);
        mapping.setActivities(activities);


        String formVersionPath = "myformversionpath";
        String version = "myversion";

        when(properties.getProperty(FormMappingConstants.FORM_VERSION_PATH)).thenReturn(formVersionPath);
        when(allSearchStrategies.searchFirst(formVersionPath, formValueElement, formValueElement, null)).thenReturn(new FormValueAttribute(version));
        when(mrsMappingService.findMatchingMappingFor(xmlns, version)).thenReturn(mapping);

        formsAdapter.adaptForm(form);

        verify(registrationsAdapter).adaptForm(form, activity);
    }

    @Test
    public void shouldAdaptForEncounterWhenActivityTypeIsEncounter() {
        String xmlns = "myxmlns";
        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, xmlns);
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);

        MRSMapping mapping = new MRSMapping();
        List<MRSActivity> activities = new ArrayList<>();
        MRSActivity activity = new MRSActivity();
        activity.setType("encounter");
        activities.add(activity);
        mapping.setActivities(activities);

        String formVersionPath = "myformversionpath";
        String version = "myversion";

        when(properties.getProperty(FormMappingConstants.FORM_VERSION_PATH)).thenReturn(formVersionPath);
        when(allSearchStrategies.searchFirst(formVersionPath, formValueElement, formValueElement, null)).thenReturn(new FormValueAttribute(version));
        when(mrsMappingService.findMatchingMappingFor(xmlns, version)).thenReturn(mapping);

        formsAdapter.adaptForm(form);

        verify(encounterAdapter).adaptForm(form, activity);
    }

    @Test
    public void shouldNotAdaptFormWhenMatchingMappingsNotFound() {
        String xmlns = "myxmlns";

        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, xmlns);
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);

        String formVersionPath = "myformversionpath";
        String version = "myversion";

        when(properties.getProperty(FormMappingConstants.FORM_VERSION_PATH)).thenReturn(formVersionPath);
        when(allSearchStrategies.searchFirst(formVersionPath, formValueElement, formValueElement, null)).thenReturn(new FormValueAttribute(version));
        when(mrsMappingService.findMatchingMappingFor(xmlns, version)).thenReturn(null);

        formsAdapter.adaptForm(form);

        verifyZeroInteractions(encounterAdapter);
        verifyZeroInteractions(registrationsAdapter);
    }

    @Test
    public void shouldFindMappingForNullVersionIfVersionPropertyNotProvided() {
        String xmlns = "myxmlns";

        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, xmlns);
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);

        when(mrsMappingService.findMatchingMappingFor(xmlns, null)).thenReturn(null);

        formsAdapter.adaptForm(form);

        verifyZeroInteractions(allSearchStrategies);
        verifyZeroInteractions(encounterAdapter);
        verifyZeroInteractions(registrationsAdapter);
    }

    @Test
    public void shouldFindMappingForNullVersionIfVersionElementIsNotFound() {
        String xmlns = "myxmlns";

        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, xmlns);
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);

        String formVersionPath = "myformversionpath";

        when(properties.getProperty(FormMappingConstants.FORM_VERSION_PATH)).thenReturn(formVersionPath);
        when(mrsMappingService.findMatchingMappingFor(xmlns, null)).thenReturn(null);

        formsAdapter.adaptForm(form);

        verify(allSearchStrategies).searchFirst(formVersionPath, formValueElement, formValueElement, null);
        verifyZeroInteractions(encounterAdapter);
        verifyZeroInteractions(registrationsAdapter);
    }
}
