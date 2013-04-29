package org.motechproject.mapper.repository;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllMRSMappingsTest extends SpringIntegrationTest {

    @Autowired
    private AllMRSMappings allMRSMappings;
    @Autowired
    @Qualifier("mapperDbConnector")
    private CouchDbConnector connector;

    @Before
    @After
    public void setup() {
        allMRSMappings.removeAll();
    }

    @Test
    public void testShouldSaveDifferentMSRActivityType() {
        MRSMapping mrsMapping = new MRSMapping();
        mrsMapping.setXmlns("xmlns");
        List<MRSActivity> activities = new ArrayList<>();
        MRSRegistrationActivity registrationActivity = new MRSRegistrationActivity();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        registrationActivity.setAttributes(attributes);
        MRSEncounterActivity encounterActivity = new MRSEncounterActivity();
        encounterActivity.setEncounterType("encounterType");
        activities.add(registrationActivity);
        activities.add(encounterActivity);
        mrsMapping.setActivities(activities);

        allMRSMappings.addOrUpdate(mrsMapping);

        List<MRSMapping> mrsMappingsFromDb = allMRSMappings.getAll();
        assertEquals(1, mrsMappingsFromDb.size());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

}
