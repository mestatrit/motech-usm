package org.motechproject.openmrs.atomfeed;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.motechproject.MotechException;
import org.springframework.stereotype.Component;

@Component
public class OpenMrsHttpClientImpl implements OpenMrsHttpClient {
    private static final String ATOM_FEED_MODULE_PATH = "/moduleServlet/atomfeed/atomfeed";
    private static final Logger LOGGER = Logger.getLogger(OpenMrsHttpClientImpl.class);

    private final HttpClient httpClient;

    public OpenMrsHttpClientImpl(String openmrsUrl) throws URIException {
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        httpClient.getHostConfiguration().setHost(new URI(openmrsUrl, false));
    }

    @Override
    public String getOpenMrsAtomFeed() {
        GetMethod get = new GetMethod(ATOM_FEED_MODULE_PATH);
        return executeGetMethod(get);
    }

    private String executeGetMethod(GetMethod get) {
        try {
            httpClient.executeMethod(get);
            if (get.getStatusCode() == HttpStatus.SC_OK) {
                return get.getResponseBodyAsString();
            } else {
                LOGGER.warn("OpenMRS Atom Feed module returned non 200 status: " + get.getStatusCode());
                return "";
            }
        } catch (IOException e) {
            LOGGER.error("Motech OpenMRS Atom Feed module could not communicate with the OpenMRS");
            throw new MotechException(e.getMessage());
        }
    }

    @Override
    public String getOpenMrsAtomFeedSinceDate(String lastUpdateTime) {
        GetMethod get = new GetMethod(ATOM_FEED_MODULE_PATH);
        NameValuePair[] params = new NameValuePair[1];
        params[0] = new NameValuePair("asOfDate", lastUpdateTime);
        get.setQueryString(params);

        return executeGetMethod(get);
    }
}
