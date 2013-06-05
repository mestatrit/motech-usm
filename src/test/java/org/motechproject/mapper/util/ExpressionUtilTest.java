package org.motechproject.mapper.util;


import org.junit.Assert;
import org.junit.Test;
import org.motechproject.commcare.domain.FormNode;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpressionUtilTest {

    @Test
    public void shouldReturnFormNodeValue() {
        String expression = "myexpression";
        String expectedValue = "myvalue";

        CommcareFormBeneficiarySegment beneficiarySegment = mock(CommcareFormBeneficiarySegment.class);
        FormNode formNode = mock(FormNode.class);

        when(beneficiarySegment.search(expression)).thenReturn(formNode);
        when(formNode.getValue()).thenReturn(expectedValue);

        String actualValue = ExpressionUtil.resolve(expression, beneficiarySegment);
        Assert.assertEquals(actualValue, expectedValue);
    }

    @Test
    public void shouldHandleNullFormNode() {
        CommcareFormBeneficiarySegment beneficiarySegment = mock(CommcareFormBeneficiarySegment.class);
        String actualValue = ExpressionUtil.resolve("myexpression", beneficiarySegment);
        assertNull(actualValue);
    }

    @Test
    public void shouldConvertAndReturn() {
        CommcareFormBeneficiarySegment beneficiarySegment = mock(CommcareFormBeneficiarySegment.class);
        FormNode formNode = mock(FormNode.class);

        String actualValue = ExpressionUtil.resolve("gender", beneficiarySegment);
        assertNull(actualValue);

        actualValue = ExpressionUtil.resolve("gender::{default: 'female'}", beneficiarySegment);
        assertEquals("female", actualValue);

        when(beneficiarySegment.search("gender")).thenReturn(formNode);
        when(formNode.getValue()).thenReturn("male");
        actualValue = ExpressionUtil.resolve("gender::{default: 'female'}", beneficiarySegment);
        assertEquals("male", actualValue);
    }
}
