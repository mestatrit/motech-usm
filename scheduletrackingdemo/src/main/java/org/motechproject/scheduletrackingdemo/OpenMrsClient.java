package org.motechproject.scheduletrackingdemo;

import java.util.Date;

import org.joda.time.DateTime;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;

public interface OpenMrsClient {

    boolean hasConcept(String patientId, String conceptName);

    DateTime lastTimeFulfilledDateTimeObs(String patientId,
            String conceptName);

    MRSPatient getPatientByMotechId(String patientId);

    void savePatient(MRSPatient patient);

    void addEncounterForPatient(String motechId, String conceptName, String facilityName, Date observedDate);

    MRSFacility findFacility(String location);
}
