package org.motechproject.mapper.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MappingsReader {

    private MotechJsonReader reader = new MotechJsonReader();
    private Map<Type, Object> providedAdapters = new HashMap<>();

    public MappingsReader() {
        providedAdapters.put(MRSActivity.class, new MRSActivityAdapter());
    }

    public List<MRSMapping> readJson(String json) {
        Type type = new TypeToken<List<MRSMapping>>() {
        }.getType();
        return (List<MRSMapping>) reader.readFromString(json, type, providedAdapters);
    }

    private static class MRSActivityAdapter implements JsonDeserializer<MRSActivity> {
        public MRSActivity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

            if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                JsonElement typeElement = object.get(FormMappingConstants.MRS_ACTIVITY_TYPE);
                if (typeElement.isJsonPrimitive()) {
                    JsonPrimitive primitive = typeElement.getAsJsonPrimitive();
                    String type = primitive.getAsString();
                    if (FormMappingConstants.ENCOUNTER_ACTIVITY.equals(type)) {
                        return generateEncounterActivity(json);
                    } else if (FormMappingConstants.REGISTRATION_ACTIVITY.equals(type)) {
                        return generateRegistrationActivity(json);
                    }
                }
            }
            return null;
        }

        private MRSActivity generateRegistrationActivity(JsonElement json) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            MRSRegistrationActivity activity = gson.fromJson(json, MRSRegistrationActivity.class);

            return activity;
        }

        private MRSActivity generateEncounterActivity(JsonElement json) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            MRSEncounterActivity activity = gson.fromJson(json, MRSEncounterActivity.class);

            return activity;
        }
    }
}
