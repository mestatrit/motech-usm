package org.motechproject.CampaignDemo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


//Spring controller for displaying the initial demo form page

@Controller
public class FormController extends MultiActionController {

    @RequestMapping(value = "/form/cron", method = RequestMethod.GET)
    public ModelAndView cronCampaign(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("cronFormPage");
    }

    @RequestMapping(value = "/form/offset", method = RequestMethod.GET)
    public ModelAndView offsetCampaign(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("formPage");
    }
    
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public ModelAndView main(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("main");
    }

}
