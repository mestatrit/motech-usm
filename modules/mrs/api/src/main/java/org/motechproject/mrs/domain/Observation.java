package org.motechproject.mrs.domain;

import java.util.Set;
import org.joda.time.DateTime;

public interface Observation<T> {

    String getPatientId();

    void setPatientId(String patientId);

    String getObservationId();

    void setObservationId(String observationId);

    DateTime getDate();

    void setDate(DateTime date);

    String getConceptName();

    void setConceptName(String conceptName);

    T getValue();

    void setValue(T value);

    Set<Observation> getDependantObservations();

    void setDependantObservations(Set<Observation> dependantObservations);
}
