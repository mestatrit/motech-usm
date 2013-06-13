package org.motechproject.mapper.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbDocument;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllMRSMappingsTest extends SpringIntegrationTest {

    @Autowired
    private AllMRSMappings allMRSMappings;
    @Autowired
    @Qualifier("mapperDbConnector")
    private CouchDbConnector connector;

    private List<String> idsToDelete = new ArrayList<>();

    @Override
    @After
    public void tearDown() {
        super.tearDown();
        for(String id : idsToDelete) {
            MRSMapping mrsMapping = connector.find(MRSMapping.class, id);
            if(mrsMapping != null) {
                connector.delete(mrsMapping);
            }
        }
    }

    @Test
    public void shouldFindMRSMappingByXmlnsAndVersion() {
        createMRSMapping("xmlns1", "*");
        createMRSMapping("xmlns1", "version1");
        createMRSMapping("xmlns2", "*");
        createMRSMapping("xmlns2", "version2");

        validateEquals("xmlns1", "*", allMRSMappings.findByXmlnsAndVersion("xmlns1", "*"));
        validateEquals("xmlns2", "version2", allMRSMappings.findByXmlnsAndVersion("xmlns2", "version2"));
    }

    @Test
    public void shouldFindMRSMappingsByXmlns() {
        createMRSMapping("xmlns1", "*");
        createMRSMapping("xmlns1", "version1");
        createMRSMapping("xmlns2", "*");
        createMRSMapping("xmlns2", "version2");

        List<MRSMapping> actualMappings = allMRSMappings.findByXmlns("xmlns1");
        validateEquals("xmlns1", "*", actualMappings.get(0));
        validateEquals("xmlns1", "version1", actualMappings.get(1));
    }

    @Test
    public void shouldDeleteMRSMappingsById() {
        MRSMapping mrsMapping = createMRSMapping("xmlns1", "version1");
        String id = mrsMapping.getId();

        assertNotNull(connector.get(MRSMapping.class, id));

        allMRSMappings.deleteMapping(id);

        assertNull(connector.find(MRSMapping.class, id));
    }

    @Test
    public void shouldIgnoreDeletingIfMappingForIdDoesNotExist() {
        String id = "doesnotexist";

        assertNull(connector.find(MRSMapping.class, id));

        allMRSMappings.deleteMapping(id);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @Override
    protected void markForDeletion(Object obj) {
        if(!(obj instanceof CouchDbDocument)) {
            return;
        }
        idsToDelete.add(((CouchDbDocument) obj).getId());
    }

    private MRSMapping createMRSMapping(String xmlNs, String version) {
        MRSMapping mrsMapping = new MRSMapping();
        mrsMapping.setXmlns(xmlNs);
        mrsMapping.setVersion(version);
        connector.create(mrsMapping);
        markForDeletion(mrsMapping);
        return mrsMapping;
    }

    private void validateEquals(String xmlNs, String version, MRSMapping mrsMapping) {
        assertEquals(xmlNs, mrsMapping.getXmlns());
        assertEquals(version, mrsMapping.getVersion());
    }

}
