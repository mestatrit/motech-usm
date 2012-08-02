package org.motechproject.scheduletrackingdemo.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.motechproject.scheduletrackingdemo.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.DAO.MRSPatientDAO;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EnrollController {

    @Autowired
    MRSPatientDAO patientDAO;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private OpenMrsClient openMrsClient;
    
    private static final String MAPPING_FILE_NAME = "simple-schedule.json";

    @Autowired
    private PatientScheduler patientSchedule;

    @RequestMapping("/enroll/start")
    public ModelAndView start(HttpServletRequest request,
            HttpServletResponse response) {

        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");

        patientSchedule.enrollIntoSchedule(externalID, scheduleName);

        List<Patient> patientList = patientDAO.findAllPatients();

        ModelAndView mv = new ModelAndView("scheduleTrackingPage");
        
        mv.addObject("patientsList", patientList);

        return mv;

    }

    @RequestMapping("/enroll/stop")
    public ModelAndView stop(HttpServletRequest request,
            HttpServletResponse response) {

        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");
        List<String> scheduleNames = new ArrayList<String>();
        scheduleNames.add(scheduleName);
        try {
            scheduleTrackingService.unenroll(externalID, scheduleNames);
        } catch (InvalidEnrollmentException e) {
//            logger.warn("Could not unenroll externalId=" + externalID
//                    + ", scheduleName=" + scheduleName);
        }

        List<Patient> patientList = patientDAO.findAllPatients();

        ModelAndView mv = new ModelAndView("scheduleTrackingPage");
        
        mv.addObject("patientsList", patientList);

        return mv;
    }

    /**
     * For testing purposes only
     * 
     * @param request
     * @param response
     * @return
     */
    /*
     * public ModelAndView fulfill(HttpServletRequest request,
     * HttpServletResponse response) {
     * 
     * String externalID = request.getParameter("externalID"); String
     * scheduleName = request.getParameter("scheduleName");
     * 
     * EnrollmentRequest enrollmentRequest = new EnrollmentRequest(externalID,
     * scheduleName, new Time(LocalTime.now()), DateUtil.today(), new
     * Time(LocalTime.now()), DateUtil.today(), new Time(LocalTime.now()),
     * schedule.getNextMilestoneName(lastConceptFulfilled), null);
     * 
     * scheduleTrackingService.fulfillCurrentMilestone(externalID, scheduleName,
     * DateUtil.today());
     * 
     * List<Patient> patientList = patientDAO.findAllPatients();
     * 
     * Map<String, Object> modelMap = new TreeMap<String, Object>();
     * modelMap.put("patients", patientList); //List of patients is for display
     * purposes only
     * 
     * ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);
     * 
     * return mv;
     * 
     * }
     * 
     * /** For testing purposes
     * 
     * @param request
     * 
     * @param response
     * 
     * @return
     */
    /*
     * public ModelAndView obs(HttpServletRequest request, HttpServletResponse
     * response) { String externalID = request.getParameter("externalID");
     * String conceptName = request.getParameter("conceptName");
     * 
     * openMrsClient.printValues(externalID, conceptName);
     * 
     * openMrsClient.lastTimeFulfilledDateTimeObs(externalID, conceptName);
     * 
     * List<Patient> patientList = patientDAO.findAllPatients();
     * 
     * Map<String, Object> modelMap = new TreeMap<String, Object>();
     * modelMap.put("patients", patientList); //List of patients is for display
     * purposes only
     * 
     * ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);
     * 
     * return mv; }
     */

    @RequestMapping("/enroll/scheduletracking")
    public ModelAndView scheduleTracking(HttpServletRequest request,
            HttpServletResponse response) {

        List<Patient> patientList = patientDAO.findAllPatients();

        ModelAndView mv = new ModelAndView("scheduleTrackingPage");
        
        mv.addObject("patientsList", patientList);

        return mv;
    }
    
    @RequestMapping("/addschedules")
    public ModelAndView addschedules(HttpServletRequest request,
            HttpServletResponse response) {
        
        InputStream is = getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME);

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        scheduleTrackingService.add(writer.toString());

        return null;
    }
}
