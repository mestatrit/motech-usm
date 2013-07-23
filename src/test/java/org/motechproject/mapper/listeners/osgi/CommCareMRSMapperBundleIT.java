package org.motechproject.mapper.listeners.osgi;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.mapper.adapters.FormAdapter;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;


public class CommCareMRSMapperBundleIT extends BaseOsgiIT {

    private boolean testFormIntegration = false;

    public void testBundle() throws Exception {
        testOsgiPlatformStarts();
    }

    public void testShouldFetchFormAndAdapt() throws Exception {
        if(!testFormIntegration) {
            return;
        }
        ServiceReference reference = bundleContext.getServiceReference(CommcareFormService.class.getName());
        CommcareFormService commcareFormService = (CommcareFormService) bundleContext.getService(reference);
        CommcareForm commcareForm = commcareFormService.retrieveForm("0e6a3c25-90c5-45a3-89ea-9e253f7308cc");

        ServiceReference serviceReference = bundleContext.getServiceReference(FormAdapter.class.getName());
        FormAdapter formAdapter = (FormAdapter) bundleContext.getService(serviceReference);

        formAdapter.adaptForm(commcareForm);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"classpath:META-INF/spring/testCommcareMapperBundleContext.xml"};
    }


}

