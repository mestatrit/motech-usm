package org.motechproject.couch.mrs.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.motechproject.couch.mrs.model.CouchAttribute;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.domain.User;

public final class CouchMRSConverterUtil {

    private CouchMRSConverterUtil() { 
    }

    public static Observation convertCouchObsToMRSObs(CouchObservationImpl obs) {
        // TODO Auto-generated method stub
        return null;
    }

    public static CouchEncounterImpl convertEncounterToEncounterImpl(Encounter mrsEncounter) {

        Provider provider = mrsEncounter.getProvider();
        Facility facility = mrsEncounter.getFacility();
        User creator = mrsEncounter.getCreator();
        Patient patient = mrsEncounter.getPatient();
        Set<? extends Observation> observations = mrsEncounter.getObservations();

        String providerId = null;
        String facilityId = null;
        String creatorId = null;
        String patientId = null;
        Set<String> observationIds = null;

        if (provider != null) {
            providerId = provider.getProviderId();
        }

        if (facility != null) {
            facilityId = facility.getFacilityId();
        }

        if (creator != null) {
            creatorId = creator.getUserId();
        }

        if (patient != null) {
            patientId = patient.getMotechId();
        }

        if (observations != null && observations.size() > 0) {
            Iterator<? extends Observation> obsIterator = observations.iterator();
            observationIds = new HashSet<String>();
            while (obsIterator.hasNext()) {
                String obsId = obsIterator.next().getObservationId();
                if (obsId != null && obsId.trim().length() > 0) {
                    observationIds.add(obsId);
                }
            }
        }

        return new CouchEncounterImpl(mrsEncounter.getEncounterId(), providerId, creatorId, facilityId, mrsEncounter.getDate(), observationIds, patientId, mrsEncounter.getEncounterType());
    }


    public static CouchPatientImpl createPatient (Patient patient) {
        List<Attribute> attributeList = new ArrayList<>();

        if (patient.getPerson() != null) {
            for (Attribute attribute : patient.getPerson().getAttributes()){
                CouchAttribute couchAttribute = new CouchAttribute();
                couchAttribute.setName(attribute.getName());
                couchAttribute.setValue(attribute.getValue());

                attributeList.add(couchAttribute);
            }
        }

        CouchPerson person = new CouchPerson();
        person.setAddress(patient.getPerson().getAddress());
        person.setFirstName(patient.getPerson().getFirstName());
        person.setLastName(patient.getPerson().getLastName());
        person.setAge(patient.getPerson().getAge());
        person.setBirthDateEstimated(patient.getPerson().getBirthDateEstimated());
        person.setDateOfBirth(patient.getPerson().getDateOfBirth());
        person.setDead(patient.getPerson().isDead());
        person.setDeathDate(patient.getPerson().getDeathDate());
        person.setGender(patient.getPerson().getGender());
        person.setMiddleName(patient.getPerson().getMiddleName());
        person.setPersonId(patient.getPerson().getPersonId());
        person.setPreferredName(patient.getPerson().getPreferredName());
        person.setAttributes(attributeList);

        Facility facility = patient.getFacility();

        String facilityId = null;
        if (facility != null) {
            facilityId = facility.getFacilityId();
        }

        return new CouchPatientImpl(patient.getPatientId(), patient.getMotechId(), person, facilityId);
    }

}
