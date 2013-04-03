package org.motechproject.couch.mrs.model;

import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.domain.User;

public class CouchEncounter implements Encounter {

    private String encounterId;
    private Provider provider;
    private User creator;
    private Facility facility;
    private DateTime date;
    private Set<? extends Observation> observations;
    private Patient patient;
    private String encounterType;

    public CouchEncounter(Provider provider, User creator, Facility facility, DateTime date, Set<? extends Observation> observations, Patient patient, String encounterType) {
        this.encounterId = UUID.randomUUID().toString();
        this.provider = provider;
        this.creator = creator;
        this.facility = facility;
        this.date = date;
        this.observations = observations;
        this.patient = patient;
        this.encounterType = encounterType;
    }

    public CouchEncounter(String encounterId, Provider provider, User creator, Facility facility, DateTime date, Set<? extends Observation> observations, Patient patient, String encounterType) {
        this(provider, creator, facility, date, observations, patient, encounterType);
        this.encounterId = encounterId;
    }

    @Override
    public String getEncounterId() {
        return encounterId;
    }

    @Override
    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    @Override
    public Provider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public User getCreator() {
        return creator;
    }

    @Override
    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public Facility getFacility() {
        return facility;
    }

    @Override
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Override
    public DateTime getDate() {
        return date;
    }

    @Override
    public void setDate(DateTime date) {
        this.date = date;
    }

    @Override
    public Set<? extends Observation> getObservations() {
        return observations;
    }

    @Override
    public void setObservations(Set<? extends Observation> observations) {
        this.observations = observations;
    }

    @Override
    public Patient getPatient() {
        return patient;
    }

    @Override
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String getEncounterType() {
        return encounterType;
    }

    @Override
    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

}
