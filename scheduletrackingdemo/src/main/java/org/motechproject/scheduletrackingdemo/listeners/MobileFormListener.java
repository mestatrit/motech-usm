package org.motechproject.scheduletrackingdemo.listeners;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.events.constants.EventDataKeys;
import org.motechproject.mobileforms.api.events.constants.EventSubjects;
import org.motechproject.model.Time;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.scheduletrackingdemo.DemoConstants;
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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@Component
public class MobileFormListener {
    private static final Logger logger = LoggerFactory.getLogger(MobileFormListener.class);

    private final OpenMrsClient openmrsClient;
    private final PatientScheduler patientScheduler;
    private final ScheduleTrackingService scheduleTrackingService;
    private final Gson gson;

    @Autowired
    public MobileFormListener(OpenMrsClient openmrsClient, PatientScheduler patientScheduler,
            ScheduleTrackingService scheduleTrackingService) {
        this.openmrsClient = openmrsClient;
        this.patientScheduler = patientScheduler;
        this.scheduleTrackingService = scheduleTrackingService;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
        gson = builder.create();
    }

    static class DateTimeDeserializer implements JsonDeserializer<DateTime> {

        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            long time = Long.parseLong(json.getAsString());

            return new DateTime(time);
        }

    }

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DemoConstants.DEMO_PATIENT_REGISTRATION_FORM_NAME })
    public void handlePatientRegistrationForm(MotechEvent event) {
        PatientRegistrationBean bean = readJson(event, PatientRegistrationBean.class);
        MRSPerson person = new MRSPerson().firstName(bean.getFirstName()).lastName(bean.getLastName())
                .dateOfBirth(bean.getDateOfBirth()).birthDateEstimated(false).gender(bean.getGender());
        MRSFacility facility = openmrsClient.findFacility(bean.getLocation());
        MRSPatient patient = new MRSPatient(bean.getMotechId(), person, facility);

        openmrsClient.savePatient(patient);
        patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));

        if (bean.isEnrollPatient()) {
            patientScheduler.enrollIntoSchedule(bean.getMotechId(), DemoConstants.DEMO_SCHEDULE_NAME);
        }
    }

    private String stripDashFromPhoneNumber(String phoneNum) {
        return phoneNum.replaceAll("-", "");
    }

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DemoConstants.DEMO_PATIENT_ENROLLMENT_FORM_NAME })
    public void handlePatientEnrollment(MotechEvent event) {
        PatientEnrollmentBean bean = readJson(event, PatientEnrollmentBean.class);
        patientScheduler.saveMotechPatient(bean.getMotechId(), stripDashFromPhoneNumber(bean.getPhoneNumber()));
        patientScheduler.enrollIntoSchedule(bean.getMotechId(), DemoConstants.DEMO_SCHEDULE_NAME);
    }

    private <T extends FormBean> T readJson(MotechEvent event, Class<T> classOfT) {
        String json = event.getParameters().get(EventDataKeys.FORM_BEAN).toString();
        T bean = gson.fromJson(json, classOfT);
        return bean;
    }

    @MotechListener(subjects = { EventSubjects.BASE_SUBJECT + DemoConstants.DEMO_PATIENT_ENCOUNTER_FORM_NAME })
    public void handlePatientEncounter(MotechEvent event) {
        PatientEncounterBean bean = readJson(event, PatientEncounterBean.class);

        String conceptName = OpenMrsConceptConverter.convertToNameFromIndex(bean.getObservedConcept());
        try {
            openmrsClient.addEncounterForPatient(bean.getMotechId(), conceptName, bean.getLocationName(), bean
                    .getObservedDate().toDate());
            LocalTime time = new LocalTime();
            scheduleTrackingService.fulfillCurrentMilestone(bean.getMotechId(), DemoConstants.DEMO_SCHEDULE_NAME,
                    LocalDate.now(), new Time(time.getHourOfDay(), time.getMinuteOfHour()));
        } catch (MRSException e) {
            logger.error("Could not add an encounter to the OpenMRS");
        }
    }
}
