package org.motechproject.mapper.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MRSMappingTest {

    @Test
    public void shouldCompareWithNull() {
        MRSMapping mrsMapping = new MRSMapping();
        assertEquals(1, mrsMapping.compareTo(null));

        mrsMapping.setVersion("version");
        mrsMapping.setXmlns("ns");
        assertEquals(1, mrsMapping.compareTo(null));

        mrsMapping.setVersion(null);
        mrsMapping.setXmlns(null);
        assertEquals(1, mrsMapping.compareTo(null));
    }

    @Test
    public void shouldCompareWithOtherMappingBasedOnXmlsNs() {
        MRSMapping mrsMapping1 = new MRSMapping();
        mrsMapping1.setXmlns("ns1");

        MRSMapping mrsMapping2 = new MRSMapping();
        mrsMapping2.setXmlns("ns2");

        assertEquals(-1, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping1.setXmlns(null);
        assertEquals(-1, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping2.setXmlns(null);
        assertEquals(0, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping1.setXmlns("ns1");
        assertEquals(1, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping2.setXmlns("ns0");
        assertEquals(1, mrsMapping1.compareTo(mrsMapping2));
    }

    @Test
    public void shouldCompareWithOtherMappingBasedOnVersion() {
        MRSMapping mrsMapping1 = new MRSMapping();
        mrsMapping1.setVersion("version1");

        MRSMapping mrsMapping2 = new MRSMapping();
        mrsMapping2.setVersion("version2");

        assertEquals(-1, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping1.setVersion(null);
        assertEquals(-1, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping2.setVersion(null);
        assertEquals(0, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping1.setVersion("version1");
        assertEquals(1, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping2.setVersion("version0");
        assertEquals(1, mrsMapping1.compareTo(mrsMapping2));
    }


    @Test
    public void shouldCompareWithOtherMappingBasedOnMappingAndThenVersion() {
        MRSMapping mrsMapping1 = new MRSMapping();
        mrsMapping1.setXmlns("ns1");
        mrsMapping1.setVersion("version2");

        MRSMapping mrsMapping2 = new MRSMapping();
        mrsMapping2.setXmlns("ns2");
        mrsMapping2.setVersion("version1");

        assertEquals(-1, mrsMapping1.compareTo(mrsMapping2));

        mrsMapping1.setXmlns("ns2");
        assertEquals(1, mrsMapping1.compareTo(mrsMapping2));
    }

    @Test
    public void shouldCheckForWildcardVersion() {
        MRSMapping mrsMapping = new MRSMapping();
        assertTrue(mrsMapping.hasWildcardVersion());

        mrsMapping.setVersion(null);
        assertTrue(mrsMapping.hasWildcardVersion());

        mrsMapping.setVersion("");
        assertFalse(mrsMapping.hasWildcardVersion());

        mrsMapping.setVersion("someversion");
        assertFalse(mrsMapping.hasWildcardVersion());

        mrsMapping.setVersion("*");
        assertTrue(mrsMapping.hasWildcardVersion());
    }

    @Test
    public void shouldVerifyIfVersionMatches() {
        MRSMapping mrsMapping = new MRSMapping();
        mrsMapping.setVersion("3.14");

        assertTrue(mrsMapping.matchesVersion("3.14"));
        assertTrue(mrsMapping.matchesVersion("for-3.14.5"));

        assertFalse(mrsMapping.matchesVersion("3.1"));
        assertFalse(mrsMapping.matchesVersion(null));
        assertFalse(mrsMapping.matchesVersion(""));
        assertFalse(mrsMapping.matchesVersion("*"));
    }

    @Test
    public void shouldMatchAllVersionIfHasWildcardVersion() {
        MRSMapping mrsMapping = new MRSMapping();

        assertTrue(mrsMapping.matchesVersion("someversion"));
        assertTrue(mrsMapping.matchesVersion("someotherversion"));
        assertTrue(mrsMapping.matchesVersion(null));
        assertTrue(mrsMapping.matchesVersion(""));
        assertTrue(mrsMapping.matchesVersion("*"));

        mrsMapping = new MRSMapping();
        mrsMapping.setVersion(null);

        assertTrue(mrsMapping.matchesVersion("someversion"));
        assertTrue(mrsMapping.matchesVersion("someotherversion"));
        assertTrue(mrsMapping.matchesVersion(null));
        assertTrue(mrsMapping.matchesVersion(""));
        assertTrue(mrsMapping.matchesVersion("*"));
    }

    @Test
    public void shouldDefaultVersionToWildcard() {
        assertEquals(MRSMapping.WILDCARD_VERSION, new MRSMapping().getVersion());
    }

}
