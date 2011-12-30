package org.motechproject.server.alerts.service;

import org.ektorp.DocumentNotFoundException;
import org.motechproject.server.alerts.dao.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class AlertServiceImpl implements AlertService {
    private AllAlerts allAlerts;

    final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    public AlertServiceImpl(AllAlerts allAlerts) {
        this.allAlerts = allAlerts;
    }

    @Override
    public void createAlert(Alert alert) {
        allAlerts.add(alert);
    }

    @Override
    public List<Alert> getBy(String externalId, AlertType type, AlertStatus status, Integer priority, int limit) {
        return allAlerts.listAlerts(externalId, type, status, priority, limit);
    }

    @Override
    public void changeStatus(String id, AlertStatus status) {
        Alert alert = get(id);
        alert.setStatus(status);
        allAlerts.update(alert);
    }

    @Override
    public void setData(String id, String key, String value) {
        Alert alert = get(id);
        final Map<String, String> data = alert.getData();
        data.put(key, value);
        allAlerts.update(alert);
    }

    /**
     * @param id
     * @return  Alert object if found, otherwise returns null.
     */
    public Alert get(String id) {
        try {
            return allAlerts.get(id);
        } catch (DocumentNotFoundException e) {
            logger.error(String.format("No Alert found for the given id: %s.", id), e);
        }
        return null;
    }

}
