package org.motechproject.scheduletrackingdemo.listeners;

import org.motechproject.mobileforms.api.events.constants.EventDataKeys;
import org.motechproject.mobileforms.api.events.constants.EventSubjects;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletrackingdemo.beans.PatientEnrollmentBean;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//    @Autowired
//    OpenMrsClient openmrsClient;
//
//    @Autowired
//    PatientScheduler patientScheduler;

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DEMO_PATIENT_REGISTRATION_FORM_NAME })
    public void handlePatientRegistrationForm(MotechEvent event) {
//        PatientRegistrationBean bean = (PatientRegistrationBean) event
//                .getParameters().get(FormGroupPublisher.FORM_BEAN_GROUP);
//        MRSPerson person = new MRSPerson().firstName(bean.getFirstName())
//                .lastName(bean.getLastName())
//                .dateOfBirth(bean.getDateOfBirth()).birthDateEstimated(false)
//                .gender(bean.getGender());
//        MRSFacility facility = new MRSFacility("1");
//        MRSPatient patient = new MRSPatient(bean.getMotechId(), person,
//                facility);
//
//        openmrsClient.savePatient(patient);
//        patientScheduler.saveMotechPatient(bean.getMotechId(),
//                stripDashFromPhoneNumber(bean.getPhoneNumber()));
//
//        if (bean.isEnrollPatient()) {
//            patientScheduler.enrollIntoSchedule(bean.getMotechId(),
//                    DEMO_SCHEDULE_NAME);
//        }
    }

    private String stripDashFromPhoneNumber(String phoneNum) {
        return phoneNum.replaceAll("-", "");
    }

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DEMO_PATIENT_ENROLLMENT_FORM_NAME})
    public void handlePatientEnrollment(MotechEvent event) {
        logger.warn("In Event Listener");
        Gson gson = new GsonBuilder().create();
        String json = event.getParameters().get(EventDataKeys.FORM_BEAN).toString();
        PatientEnrollmentBean bean = gson.fromJson(json, PatientEnrollmentBean.class);
        logger.warn(bean.getMotechId());
//        PatientEnrollmentBean bean = (PatientEnrollmentBean) event
//                .getParameters().get(FormGroupPublisher.FORM_BEAN_GROUP);
//        patientScheduler.saveMotechPatient(bean.getMotechId(),
//                stripDashFromPhoneNumber(bean.getPhoneNumber()));
//        patientScheduler.enrollIntoSchedule(bean.getMotechId(),
//                DEMO_SCHEDULE_NAME);
    }

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DEMO_PATIENT_ENCOUNTER_FORM_NAME})
    public void handlePatientEncounter(MotechEvent event) {
//        PatientEncounterBean bean = (PatientEncounterBean) event
//                .getParameters().get(FormGroupPublisher.FORM_BEAN_GROUP);
//        String conceptName = OpenMrsConceptConverter
//                .convertToNameFromIndex(bean.getObservedConcept());
//        openmrsClient.addEncounterForPatient(bean.getMotechId(), conceptName,
//                bean.getObservedDate());
    }
}
