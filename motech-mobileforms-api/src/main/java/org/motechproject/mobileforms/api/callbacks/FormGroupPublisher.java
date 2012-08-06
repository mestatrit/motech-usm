package org.motechproject.mobileforms.api.callbacks;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.events.constants.EventDataKeys;
import org.motechproject.mobileforms.api.events.constants.EventSubjects;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Component
public class FormGroupPublisher {

    private final Logger log = LoggerFactory.getLogger(FormGroupPublisher.class);

    private EventRelay eventRelay;

    @Autowired
    public FormGroupPublisher(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void publish(FormBeanGroup formBeanGroup) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DateTime.class, new DateTimeSerializer());
        Gson gson = builder.create();
        try {
            for (FormBean bean : formBeanGroup.getFormBeans()) {
                Map<String, Object> params = new HashMap<String, Object>();
                String json = gson.toJson(bean);
                params.put(EventDataKeys.FORM_BEAN, json);
                MotechEvent motechEvent = new MotechEvent(EventSubjects.BASE_SUBJECT + bean.getFormname(), params);
                eventRelay.sendEventMessage(motechEvent);
            }
        } catch (Exception e) {
            formBeanGroup.markAllFormAsFailed("Server exception, contact your administrator");
            log.error("Encountered exception while validating form group, " + formBeanGroup.toString(), e);
        }
    }
    
    public static class DateTimeSerializer implements JsonSerializer<DateTime> {

        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toDate().getTime() + "");
        }
        
    }
}
