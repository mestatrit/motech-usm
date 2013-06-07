package org.motechproject.mapper.listeners;


import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.parser.FormAdapter;
import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.Provider;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mapper.adapters.impl.ProviderAdapter;
import org.motechproject.mapper.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProviderListener {

    private ProviderAdapter providerAdapter;

    @Autowired
    public ProviderListener(ProviderAdapter providerAdapter) {
        this.providerAdapter = providerAdapter;
    }

    @MotechListener(subjects = EventConstants.COMMCARE_PROVIDER_SYNC_EVENT)
    public void handleProviderEvent(MotechEvent event) {
        List<Provider> providers = (List<Provider>) event.getParameters().get(EventConstants.PROVIDER_DETAILS);
        for(Provider provider: providers) {
            CommcareForm commcareForm = constructFormValueElement(provider);
            providerAdapter.adaptForm(commcareForm);
        }
    }

    private CommcareForm constructFormValueElement(Provider provider) {
        String json = JsonUtils.toJson(provider);
        return FormAdapter.readJson(wrapJson(json));
    }

    private String wrapJson(String json) {
        return String.format("{\"form\": %s}", json);
    }
}
