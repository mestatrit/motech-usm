package org.motechproject.mapper.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.OpenMRSEncounterActivity;
import org.motechproject.mapper.adapters.mappings.OpenMRSMapping;
import org.motechproject.mapper.adapters.mappings.OpenMRSRegistrationActivity;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

@Component
public final class MappingsReader {

    private static Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    private static final String MAPPING_FILE_NAME = "openMrsMappings.json";

    private static final MotechJsonReader READER = new MotechJsonReader();

    private static Map<Type, Object> providedAdapters = new HashMap<Type, Object>();

    static {
        providedAdapters.put(MRSActivity.class, new MRSActivityAdapter());
    }

    public static List<OpenMRSMapping> getAllMappings() {
        InputStream is = MappingsReader.class.getClassLoader().getResourceAsStream(MAPPING_FILE_NAME);

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {
            logger.error("Error retreiving all mappings: " + e.getMessage());
        }

        return readJson(writer.toString());
    }

    public static List<OpenMRSMapping> readJson(String json) {
        Type type = new TypeToken<List<OpenMRSMapping>>() { } .getType();
        return (List<OpenMRSMapping>) READER.readFromString(json, type, providedAdapters);
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

            OpenMRSRegistrationActivity activity = gson.fromJson(json, OpenMRSRegistrationActivity.class);

            return activity;
        }

        private MRSActivity generateEncounterActivity(JsonElement json) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            OpenMRSEncounterActivity activity = gson.fromJson(json, OpenMRSEncounterActivity.class);

            return activity;
        }
    }

    private MappingsReader() {
    }
}
