package org.motechproject.mapper.listeners;

import org.motechproject.testing.osgi.BaseOsgiIT;


public class FormListenerIT extends BaseOsgiIT {

    public void testShouldFetchFormAndAdapt() throws Exception {
        testOsgiPlatformStarts();

    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{"classpath:META-INF/spring/testCommcareMapperBundleContext.xml"};
    }

}
