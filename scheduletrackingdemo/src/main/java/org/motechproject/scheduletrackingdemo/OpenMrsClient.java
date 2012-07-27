package org.motechproject.scheduletrackingdemo;

import java.util.Date;

import org.joda.time.DateTime;
import org.motechproject.mrs.model.MRSPatient;

public interface OpenMrsClient {

    boolean hasConcept(String patientId, String conceptName);

    void printValues(String externalID, String conceptName);

    DateTime lastTimeFulfilledDateTimeObs(String patientId,
            String conceptName);

    MRSPatient getPatientByMotechId(String patientId);

    void savePatient(MRSPatient patient);

    void addEncounterForPatient(String motechId, String conceptName,
            Date observedDate);
}
