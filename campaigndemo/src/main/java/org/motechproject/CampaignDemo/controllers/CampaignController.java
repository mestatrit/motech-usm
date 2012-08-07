package org.motechproject.CampaignDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * A Spring controller for starting and stopping campaigns based on an external
 * ID. The PatientDAO is used only to display the list of registered users
 */
@Controller
public class CampaignController extends MultiActionController {
    @Autowired
    private PatientDAO patientDAO;

    @Autowired
    private MessageCampaignService service;

    public CampaignController(MessageCampaignService service,
            PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
        this.service = service;
    }

    public CampaignController() {

    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ModelAndView start(HttpServletRequest request,
            HttpServletResponse response) {

        String externalId = request.getParameter("externalId");
        String campaignName = request.getParameter("campaignName");
        String startoffset = request.getParameter("offset");

        int offsetValue = 0;

        try {
            offsetValue = Integer.parseInt(startoffset);
        } catch (NumberFormatException e) {
            offsetValue = 0;
        }

        /**
         * The campaign name in the campaign request references the
         * simple-message-campaign.json file found in the campaign demo's
         * resource folder. The required name of this file is determined by the
         * messageCampaign.properties file found in the motech-messagecampaign
         * platform module's resource folder.
         */
        CampaignRequest campaignRequest = new CampaignRequest();
        campaignRequest.setCampaignName(campaignName);
        campaignRequest.setExternalId(externalId);
        campaignRequest.setStartOffset(offsetValue);
        LocalDate date = new LocalDate();
        campaignRequest.setReferenceDate(date.now());

        /**
         * The startFor method schedules a periodic task that is executed every
         * two minutes until the campaign is stopped, as defined in the campaign
         * request above. The MessageCampaignService uses the
         * motech-platform-scheduler module to schedule this task in Quartz.
         * Each time the job is executed, an event is fired and relayed by the
         * Motech platform. The event that corresponds to a triggered campaign
         * message has a subject of
         * EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, where EventKeys is a
         * class in the motech-message-campaign module. To take action based on
         * this fired event, an implementer should write a listener for the
         * event (see TestListener for a demonstration of a listener on a fired
         * campaign event).
         */
        service.startFor(campaignRequest);

        List<Patient> patientList = patientDAO.findAllPatients();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = null;

        if (campaignName.equals("Cron based SMS Program")
                || campaignName.equals("Cron based IVR Program")) {
            mv = new ModelAndView("cronFormPage", modelMap);
        } else {
            mv = new ModelAndView("formPage", modelMap);
        }

        return mv;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ModelAndView stop(HttpServletRequest request,
            HttpServletResponse response) {

        String externalId = request.getParameter("externalId");
        String campaignName = request.getParameter("campaignName");

        CampaignRequest campaignRequest = new CampaignRequest();
        campaignRequest.setCampaignName(campaignName);
        campaignRequest.setExternalId(externalId);

        /**
         * See comment for service.startFor(campaignRequest) in above method for
         * a more detailed description. When stopping a campaign, an event is
         * not raised, the job is simply removed from the Quartz scheduler and
         * no more events are raised. stopAll stops ALL messages associated with
         * the specific campaign and specific external id. To stop a specific
         * message, instead call service.stopFor(campaignRequest, messageKey)
         * with the provided message key as a parameter
         */
        try{
        service.stopAll(campaignRequest);}
        catch(Exception e){
        	if (campaignName.equals("Cron based SMS Program")
                    || campaignName.equals("Cron based IVR Program")) {
                return new ModelAndView("cronFormPage");
            } else {
                return new ModelAndView("formPage");
            }
        }

        List<Patient> patientList = patientDAO.findAllPatients();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = null;

        if (campaignName.equals("Cron based SMS Program")
                || campaignName.equals("Cron based IVR Program")) {
            mv = new ModelAndView("cronFormPage", modelMap);
        } else {
            mv = new ModelAndView("formPage", modelMap);
        }

        return mv;
    }

}
