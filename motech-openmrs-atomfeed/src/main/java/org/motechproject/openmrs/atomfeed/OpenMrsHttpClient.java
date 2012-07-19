package org.motechproject.openmrs.atomfeed;

public interface OpenMrsHttpClient {

    public String getOpenMrsAtomFeed();

    public String getOpenMrsAtomFeedSinceDate(String lastUpdateTime);

}
