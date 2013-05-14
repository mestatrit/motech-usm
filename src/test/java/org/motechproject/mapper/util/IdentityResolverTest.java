package org.motechproject.mapper.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.mapper.builder.FormBuilder;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mapper.constants.FormMappingConstants.*;

public class IdentityResolverTest {

    @Mock
    private CommcareCaseService caseService;
    @Mock
    private CommcareUserService userService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetIdFromFormWithIdSchemeAttribute() {
        String fieldName = "case";
        String attributeName = "case_id";
        String expectedId = "attribute_value";
        IdentityResolver identityResolver = new IdentityResolver(caseService, userService);
        FormValueElement element = new FormValueElement();
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(attributeName, expectedId);
        element.setAttributes(attributes);
        CommcareForm form = new FormBuilder("form").with(fieldName, element).getForm();
        HashMap<String, String> idScheme = new HashMap<>();
        idScheme.put(ID_SCHEME_TYPE, ID_FROM_FORM_SCHEME);
        idScheme.put(ID_SCHEME_FIELD, fieldName);
        idScheme.put(ID_SCHEME_ATTRIBUTE, attributeName);

        String id = identityResolver.retrieveId(idScheme, form, form.getForm());

        assertEquals(expectedId, id);
    }

    @Test
    public void shouldGetIdFromFormWithoutIdSchemeAttribute() {
        String fieldName = "case";
        String expectedId = "value";
        IdentityResolver identityResolver = new IdentityResolver(caseService, userService);
        FormValueElement element = new FormValueElement();
        element.setValue(expectedId);
        CommcareForm form = new FormBuilder("form").with(fieldName, element).getForm();
        HashMap<String, String> idScheme = new HashMap<>();
        idScheme.put(ID_SCHEME_TYPE, ID_FROM_FORM_SCHEME);
        idScheme.put(ID_SCHEME_FIELD, fieldName);

        String id = identityResolver.retrieveId(idScheme, form, form.getForm());

        assertEquals(expectedId, id);
    }
}
