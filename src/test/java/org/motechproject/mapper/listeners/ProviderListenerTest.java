package org.motechproject.mapper.listeners;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.Provider;
import org.motechproject.event.MotechEvent;
import org.motechproject.mapper.adapters.impl.ProviderAdapter;
import org.motechproject.mapper.util.JsonUtils;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProviderListenerTest {

    @Mock
    private ProviderAdapter providerAdapter;

    private ProviderListener providerListener;

    @Before
    public void setup() {
        initMocks(this);
        providerListener = new ProviderListener(providerAdapter);
    }

    @Test
    public void shouldConvertProviderToFormValueElement() {
        String providerJson = "{\"default_phone_number\": \"8294168471\", \"email\": \"\", \"first_name\": \"Dr.Pramod\", \"groups\": [\"89fda0284e008d2e0c980fb13fb63886\", \"89fda0284e008d2e0c980fb13fb66a7b\", \"89fda0284e008d2e0c980fb13fb72931\", \"89fda0284e008d2e0c980fb13fb76c43\", \"89fda0284e008d2e0c980fb13fb7dcf2\", \"89fda0284e008d2e0c980fb13fb8f9f3\", \"89fda0284e008d2e0c980fb13fbc20ab\", \"89fda0284e008d2e0c980fb13fbda82a\", \"89fda0284e008d2e0c980fb13fc18199\"], \"id\": \"b0645df855266f29849eb2515b5ed57c\", \"last_name\": \"Kumar Gautam\", \"phone_numbers\": [\"8294168471\"], \"resource_uri\": \"\", \"user_data\": {\"asset-id\": \"MP818\", \"awc-code\": \"\", \"block\": \"Sonbarsa\", \"district\": \"\", \"imei-no\": \"351971057712199\", \"location-code\": \"\", \"panchayat\": \"\", \"role\": \"MOIC\", \"subcentre\": \"\", \"user_type\": \"\", \"village\": \"\"}, \"username\": \"8294168471@care-bihar.commcarehq.org\"}";

        Provider provider = JsonUtils.fromJson(providerJson, Provider.class);
        MotechEvent event = new MotechEvent();
        event.getParameters().put(EventConstants.PROVIDER_DETAILS, Arrays.asList(provider));
        providerListener.handleProviderEvent(event);

        ArgumentCaptor<CommcareForm> captor = ArgumentCaptor.forClass(CommcareForm.class);
        verify(providerAdapter).adaptForm(captor.capture());
        CommcareForm actual = captor.getValue();

        assertEquals("b0645df855266f29849eb2515b5ed57c", actual.getForm().getChildElement("id").getValue());
    }
}
