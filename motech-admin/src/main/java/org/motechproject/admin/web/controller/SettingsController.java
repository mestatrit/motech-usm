package org.motechproject.admin.web.controller;

import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SettingsController {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsController.class);

    private static final String PLATFORM_SETTINGS_SAVED = "{settings.saved}";

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private StatusMessageService statusMessageService;

    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.GET)
    @ResponseBody public List<BundleSettings> getBundleSettings(@PathVariable long bundleId) throws IOException {
        return settingsService.getBundleSettings(bundleId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/{bundleId}", method = RequestMethod.POST)
    public void saveBundleSettings(@PathVariable long bundleId, @RequestBody BundleSettings bundleSettings) throws IOException {
        settingsService.saveBundleSettings(bundleSettings, bundleId);
        statusMessageService.ok("{settings.saved.bundle}");
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void savePlatformSettings(HttpServletRequest request) throws IOException {
        List<SettingsOption> options = constructSettingsOptions(request);
        settingsService.savePlatformSettings(options);
        statusMessageService.ok(PLATFORM_SETTINGS_SAVED);
    }

    @RequestMapping(value = "/settings/platform", method = RequestMethod.GET)
    @ResponseBody public List<SettingsOption> getPlatformSettings() {
        return settingsService.getSettings();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/upload", method = RequestMethod.POST)
    public void uploadSettingsFile(@RequestParam(required = true) MultipartFile settingsFile) {
        settingsService.saveSettingsFile(settingsFile);
        statusMessageService.ok(PLATFORM_SETTINGS_SAVED);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/platform/location", method = RequestMethod.POST)
    public void uploadSettingsLocation(@RequestParam(required = true) String location) {
        settingsService.addSettingsPath(location);
        statusMessageService.ok("{settings.saved.location}");
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/settings/bundles/list", method = RequestMethod.GET)
    @ResponseBody public List<String> getBundlesWithSettings() {
        return settingsService.retrieveRegisteredBundleNames();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public void handleException(Exception e) {
        LOG.error(e.getMessage(), e);
        statusMessageService.error("Error: " + e.getMessage());
    }

    private static List<SettingsOption> constructSettingsOptions(HttpServletRequest request) {
        List<SettingsOption> settingsOptions = new ArrayList<>();
        Map<Object, Object> paramMap = request.getParameterMap();

        for (Map.Entry<Object, Object> param : paramMap.entrySet()) {
            SettingsOption option = new SettingsOption();

            String[] value = (String[]) param.getValue();
            option.setValue(value[0]);
            option.setKey((String) param.getKey());

            settingsOptions.add(option);
        }
        return settingsOptions;
    }
}
