package org.motechproject.mapper.service;


import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSAttributeDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NonStalePersonFieldUpdateStrategyTest {

    @Test
    public void shouldUpdateIfNewValueNotStale(){
        MRSPerson person = mock(MRSPerson.class);
        String attributeFieldName = "_date_modified_at";
        DateTime existingDateTime = new DateTime(2012,11,1,1,1);
        List<MRSAttribute> attributes = new ArrayList<MRSAttribute>(Arrays.asList(new MRSAttributeDto(attributeFieldName, existingDateTime.toString())));
        when(person.getAttributes()).thenReturn(attributes);

        DateTime currentUpdateDateTime = new DateTime(2012, 12, 1, 1, 1);
        NonStalePersonFieldUpdateStrategy updateStrategy = new NonStalePersonFieldUpdateStrategy(person, currentUpdateDateTime);

        assertTrue(updateStrategy.canUpdate("date", "new_date"));
    }

    @Test
    public void shouldUpdateIfNoLastModifiedDateIsPresentForAField(){
        MRSPerson person = mock(MRSPerson.class);

        DateTime currentUpdateDateTime = new DateTime(2012, 12, 1, 1, 1);
        NonStalePersonFieldUpdateStrategy updateStrategy = new NonStalePersonFieldUpdateStrategy(person, currentUpdateDateTime);

        assertTrue(updateStrategy.canUpdate("date", "new_date"));
    }

    @Test
    public void shouldNotUpdateIfNewValueStale(){
        MRSPerson person = mock(MRSPerson.class);
        String attributeFieldName = "_date_modified_at";
        DateTime existingDateTime = new DateTime(2013,11,1,1,1);
        List<MRSAttribute> attributes = new ArrayList<MRSAttribute>(Arrays.asList(new MRSAttributeDto(attributeFieldName, existingDateTime.toString())));
        when(person.getAttributes()).thenReturn(attributes);

        DateTime currentUpdateDateTime = new DateTime(2012, 12, 1, 1, 1);
        NonStalePersonFieldUpdateStrategy updateStrategy = new NonStalePersonFieldUpdateStrategy(person, currentUpdateDateTime);

        assertFalse(updateStrategy.canUpdate("date", "new_date"));
    }

    @Test
    public void shouldUpdateIfNewValueNull(){
        MRSPerson person = mock(MRSPerson.class);
        String attributeFieldName = "_date_modified_at";
        DateTime existingDateTime = new DateTime(2012,11,1,1,1);
        List<MRSAttribute> attributes = new ArrayList<MRSAttribute>(Arrays.asList(new MRSAttributeDto(attributeFieldName, existingDateTime.toString())));
        when(person.getAttributes()).thenReturn(attributes);

        DateTime currentUpdateDateTime = new DateTime(2012, 12, 1, 1, 1);
        NonStalePersonFieldUpdateStrategy updateStrategy = new NonStalePersonFieldUpdateStrategy(person, currentUpdateDateTime);

        assertFalse(updateStrategy.canUpdate("date", null));
    }

    @Test
    public void shouldUpdateAttributeForLastModifiedTimeForAFieldIfAleradyPresent(){
        MRSAttribute attribute1 = new MRSAttributeDto("field1", "field1Value");
        MRSAttribute attribute2 = new MRSAttributeDto("field2", "field2Value");
        MRSAttribute attribute3 = new MRSAttributeDto("field3", "field3Value");
        MRSAttribute attribute4 = new MRSAttributeDto("_field1_modified_at", new DateTime(1000, 1, 1, 1, 1, 1).toString());
        MRSAttribute attribute5 = new MRSAttributeDto("_field2_modified_at", new DateTime(1000, 1, 1, 1, 1, 2).toString());
        MRSAttribute attribute6 = new MRSAttributeDto("_field4_modified_at", new DateTime(1000, 1, 1, 1, 1, 3).toString());

        List<MRSAttribute> currentAttributes = new ArrayList<>(asList(attribute1, attribute2, attribute3, attribute4, attribute5, attribute6));


        MRSPerson person = mock(MRSPerson.class);
        when(person.getAttributes()).thenReturn(currentAttributes);

        DateTime currentUpdateTime = new DateTime(2000, 10, 10, 10, 10);

        NonStalePersonFieldUpdateStrategy strategy = new NonStalePersonFieldUpdateStrategy(person, currentUpdateTime);

        strategy.markUpdated("field1");

        verify(person, never()).setAttributes(any(List.class));

        assertEquals(6, currentAttributes.size());
        assertEquals(attribute1, currentAttributes.get(0));
        assertEquals(attribute2, currentAttributes.get(1));
        assertEquals(attribute3, currentAttributes.get(2));
        assertEquals(attribute5, currentAttributes.get(4));
        assertEquals(attribute6, currentAttributes.get(5));

        MRSAttribute updatedAttribute = currentAttributes.get(3);
        assertEquals("_field1_modified_at", updatedAttribute.getName());
        assertEquals(currentUpdateTime.toString(), updatedAttribute.getValue());
    }

    @Test
    public void shouldCreateUpdateAttributeForLastModifiedTimeForAFieldIfNotFound(){
        MRSAttribute attribute1 = new MRSAttributeDto("field1", "field1Value");
        MRSAttribute attribute2 = new MRSAttributeDto("field2", "field2Value");
        MRSAttribute attribute3 = new MRSAttributeDto("field3", "field3Value");
        MRSAttribute attribute5 = new MRSAttributeDto("_field2_modified_at", new DateTime(1000, 1, 1, 1, 1, 2).toString());
        MRSAttribute attribute6 = new MRSAttributeDto("_field4_modified_at", new DateTime(1000, 1, 1, 1, 1, 3).toString());

        List<MRSAttribute> currentAttributes = new ArrayList(asList(attribute1, attribute2, attribute3, attribute5, attribute6));


        MRSPerson person = mock(MRSPerson.class);
        when(person.getAttributes()).thenReturn(currentAttributes);

        DateTime currentUpdateTime = new DateTime(2000, 10, 10, 10, 10);

        NonStalePersonFieldUpdateStrategy strategy = new NonStalePersonFieldUpdateStrategy(person, currentUpdateTime);

        strategy.markUpdated("field1");

        verify(person, never()).setAttributes(any(List.class));

        assertEquals(6, currentAttributes.size());
        assertEquals(attribute1, currentAttributes.get(0));
        assertEquals(attribute2, currentAttributes.get(1));
        assertEquals(attribute3, currentAttributes.get(2));
        assertEquals(attribute5, currentAttributes.get(3));
        assertEquals(attribute6, currentAttributes.get(4));

        MRSAttribute updatedAttribute = currentAttributes.get(5);
        assertEquals("_field1_modified_at", updatedAttribute.getName());
        assertEquals(currentUpdateTime.toString(), updatedAttribute.getValue());
    }

    @Test
    public void shouldCreateUpdateAttributeForLastModifiedTimeForAFieldIfAttributesAreNull(){
        MRSPerson person = mock(MRSPerson.class);
        when(person.getAttributes()).thenReturn(null);

        DateTime currentUpdateTime = new DateTime(2000, 10, 10, 10, 10);

        NonStalePersonFieldUpdateStrategy strategy = new NonStalePersonFieldUpdateStrategy(person, currentUpdateTime);

        strategy.markUpdated("field1");

        ArgumentCaptor<List> attributesCaptor = ArgumentCaptor.forClass(List.class);
        verify(person).setAttributes(attributesCaptor.capture());

        List<MRSAttribute> actualUpdatedAttributes = attributesCaptor.getValue();
        assertEquals(1, actualUpdatedAttributes.size());

        MRSAttribute actualUpdatedAttribute = (MRSAttribute) actualUpdatedAttributes.get(0);
        assertEquals("_field1_modified_at", actualUpdatedAttribute.getName());
        assertEquals(currentUpdateTime.toString(), actualUpdatedAttribute.getValue());
    }

}
