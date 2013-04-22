package org.motechproject.mapper.repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.MRSEncounterActivity;
import org.motechproject.mapper.adapters.mappings.MRSMapping;
import org.motechproject.mapper.adapters.mappings.MRSRegistrationActivity;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MappingsReader {

    private List<String> mappingFiles;
    private MotechJsonReader reader = new MotechJsonReader();
    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");
    private Map<Type, Object> providedAdapters = new HashMap<>();
    private List<MRSMapping> mrsMappingList;

    public MappingsReader() {
    }

    @Autowired
    public MappingsReader(List<String> mappingFiles) {
        this.mappingFiles = mappingFiles;
        providedAdapters.put(MRSActivity.class, new MRSActivityAdapter());
        mrsMappingList = new ArrayList<>();
        constructMRSMapping();
    }

    private void constructMRSMapping() {
        for (String file : mappingFiles) {
            InputStream is = MappingsReader.class.getClassLoader().getResourceAsStream(file);
            StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(is, writer, "UTF-8");
            } catch (IOException e) {
                logger.error("Error retreiving all mappings: " + e.getMessage());
            }
            mrsMappingList.addAll(readJson(writer.toString()));
        }
    }

    private List<MRSMapping> readJson(String json) {
        Type type = new TypeToken<List<MRSMapping>>() {
        }.getType();
        return (List<MRSMapping>) reader.readFromString(json, type, providedAdapters);
    }

    public List<MRSMapping> getAllMappings() {
        return mrsMappingList;
    }

    private static class MRSActivityAdapter implements JsonDeserializer<MRSActivity> {
        public MRSActivity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

            if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                JsonElement typeElement = object.get(FormMappingConstants.OPEN_MRS_ACTIVITY_TYPE);
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
