package org.motechproject.mapper.adapters.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.MRSMapping;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.repository.MappingsReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllFormsAdapterTest {


    @Mock
    private AllEncountersAdapter encounterAdapter;
    @Mock
    private AllRegistrationsAdapter registrationsAdapter;
    @Mock
    private MappingsReader mappingsReader;

    private AllFormsAdapter formsAdapter;

    @Before
    public void setUp() {
        initMocks(this);
        formsAdapter = new AllFormsAdapter(encounterAdapter, registrationsAdapter, mappingsReader);
    }

    @Test
    public void shouldAdaptForRegistrationWhenActivityTypeIsRegistration() {
        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, "xmlns");
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);
        List<MRSMapping> mappings = new ArrayList<>();
        MRSMapping mapping = new MRSMapping();
        mapping.setXmlns("xmlns");
        List<MRSActivity> activities = new ArrayList<>();
        MRSActivity activity = new MRSActivity();
        activity.setType("registration");
        activities.add(activity);
        mapping.setActivities(activities);
        mappings.add(mapping);
        when(mappingsReader.getAllMappings()).thenReturn(mappings);

        formsAdapter.adaptForm(form);

        verify(registrationsAdapter).adaptForm(form, activity);
    }

    @Test
    public void shouldAdaptForEncounterWhenActivityTypeIsEncounter() {
        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, "xmlns");
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);
        List<MRSMapping> mappings = new ArrayList<>();
        MRSMapping mapping = new MRSMapping();
        mapping.setXmlns("xmlns");
        List<MRSActivity> activities = new ArrayList<>();
        MRSActivity activity = new MRSActivity();
        activity.setType("encounter");
        activities.add(activity);
        mapping.setActivities(activities);
        mappings.add(mapping);
        when(mappingsReader.getAllMappings()).thenReturn(mappings);

        formsAdapter.adaptForm(form);

        verify(encounterAdapter).adaptForm(form, activity);
    }

    @Test
    public void shouldNotAdaptFormWhenXmlnsDoesNotMatch() {
        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, "xmlns");
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);
        List<MRSMapping> mappings = new ArrayList<>();
        MRSMapping mapping = new MRSMapping();
        mapping.setXmlns("different_xmlns");
        List<MRSActivity> activities = new ArrayList<>();
        MRSActivity activity1 = new MRSActivity();
        activity1.setType("encounter");
        MRSActivity activity2 = new MRSActivity();
        activity2.setType("registration");
        activities.add(activity1);
        activities.add(activity2);
        mapping.setActivities(activities);
        mappings.add(mapping);
        when(mappingsReader.getAllMappings()).thenReturn(mappings);

        formsAdapter.adaptForm(form);

        verify(encounterAdapter, never()).adaptForm(form, activity1);
        verify(registrationsAdapter, never()).adaptForm(form, activity1);
    }

    @Test
    public void shouldAdaptFormOnlyOnceIfThereAreMultipleMappingsWithSameNamespace() {
        CommcareForm form = new CommcareForm();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(FormMappingConstants.FORM_XMLNS_ATTRIBUTE, "same xmlns");
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setAttributes(attributes);
        form.setForm(formValueElement);
        List<MRSActivity> activities = new ArrayList<>();
        MRSActivity activity = new MRSActivity();
        activity.setType("encounter");
        activities.add(activity);
        List<MRSMapping> mappings = new ArrayList<>();
        MRSMapping mapping1 = new MRSMapping();
        mapping1.setXmlns("same xmlns");
        mapping1.setActivities(activities);
        mappings.add(mapping1);
        MRSMapping mapping2 = new MRSMapping();
        mapping2.setXmlns("same xmlns");
        mapping2.setActivities(activities);
        mappings.add(mapping2);
        when(mappingsReader.getAllMappings()).thenReturn(mappings);

        formsAdapter.adaptForm(form);

        verify(encounterAdapter, times(1)).adaptForm(form, activity);
    }
}
