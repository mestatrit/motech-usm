package org.motechproject.couch.mrs.repository;

import java.util.List;

import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.mrs.domain.Observation;

public interface AllCouchObservations {

    List<CouchObservationImpl> findByMotechIdAndConceptName(String patientMotechId, String conceptName);

    List<CouchObservationImpl> findByObservationId(String observationId);

    void addOrUpdateObservation(Observation obs);

    void removeObservation(Observation obs);
}
