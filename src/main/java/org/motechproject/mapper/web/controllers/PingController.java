package org.motechproject.mapper.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("mrsMapperPingController")
@RequestMapping("/web-api")
public class PingController {

    @RequestMapping(value = "/ping-mrs-mapper", method = RequestMethod.GET)
    @ResponseBody
    public String pingPage() {
        return "CommcareMRSMapper Ping Page";
    }
}
