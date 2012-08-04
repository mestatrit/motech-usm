package org.motechproject.scheduletrackingdemo.listeners;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.events.constants.EventDataKeys;
import org.motechproject.mobileforms.api.events.constants.EventSubjects;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletrackingdemo.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.OpenMrsConceptConverter;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.beans.PatientEncounterBean;
import org.motechproject.scheduletrackingdemo.beans.PatientEnrollmentBean;
import org.motechproject.scheduletrackingdemo.beans.PatientRegistrationBean;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class MobileFormListener {
    private static final String DEMO_SCHEDULE_NAME = "Demo Concept Schedule";
    private static final String DEMO_PATIENT_REGISTRATION_FORM_NAME = "DemoPatientRegistration";
    private static final String DEMO_PATIENT_ENROLLMENT_FORM_NAME = "DemoPatientEnrollment";
    private static final String DEMO_PATIENT_ENCOUNTER_FORM_NAME = "DemoPatientEncounter";

    Logger logger = LoggerFactory.getLogger(MobileFormListener.class);

    @Autowired
    OpenMrsClient openmrsClient;

    @Autowired
    PatientScheduler patientScheduler;
    
    private final Gson gson = new GsonBuilder().create();

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DEMO_PATIENT_REGISTRATION_FORM_NAME })
    public void handlePatientRegistrationForm(MotechEvent event) {
        PatientRegistrationBean bean = readJson(event, PatientRegistrationBean.class);
        MRSPerson person = new MRSPerson().firstName(bean.getFirstName())
                .lastName(bean.getLastName())
                .dateOfBirth(bean.getDateOfBirth()).birthDateEstimated(false)
                .gender(bean.getGender());
        MRSFacility facility = openmrsClient.findFacility(bean.getLocation());
        MRSPatient patient = new MRSPatient(bean.getMotechId(), person,
                facility);

        openmrsClient.savePatient(patient);
        patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));

        if (bean.isEnrollPatient()) {
            patientScheduler.enrollIntoSchedule(bean.getMotechId(),
                    DEMO_SCHEDULE_NAME);
        }
    }

    private String stripDashFromPhoneNumber(String phoneNum) {
        return phoneNum.replaceAll("-", "");
    }

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DEMO_PATIENT_ENROLLMENT_FORM_NAME})
    public void handlePatientEnrollment(MotechEvent event) {
        PatientEnrollmentBean bean = readJson(event, PatientEnrollmentBean.class);
        patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));
        patientScheduler.enrollIntoSchedule(bean.getMotechId(), DEMO_SCHEDULE_NAME);
    }

    private <T extends FormBean> T readJson(MotechEvent event, Class<T> classOfT) {
        String json = event.getParameters().get(EventDataKeys.FORM_BEAN).toString();
        T bean = gson.fromJson(json, classOfT);
        return bean;
    }

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DEMO_PATIENT_ENCOUNTER_FORM_NAME})
    public void handlePatientEncounter(MotechEvent event) {
        PatientEncounterBean bean = readJson(event, PatientEncounterBean.class);
        long time = bean.getObservedDate().getTime();
        // dates are sent based on UTC time zone
        DateTime dateTime = new DateTime(bean.getObservedDate(), DateTimeZone.UTC);
        DateTime localDate = dateTime.toLocalDate().toDateTimeAtStartOfDay();
        bean.setObservedDate(localDate.toDate());
        
        String conceptName = OpenMrsConceptConverter.convertToNameFromIndex(bean.getObservedConcept());
        openmrsClient.addEncounterForPatient(bean.getMotechId(), conceptName, bean.getLocationName(),
                bean.getObservedDate());
    }
}
