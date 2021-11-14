package cn.amazon.aws.rp.spapi.aa;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LWAClientScopesSerializerDeserializer implements JsonDeserializer<LWAClientScopes>,
        JsonSerializer<LWAClientScopes> {

    @Override
    public JsonElement serialize(LWAClientScopes src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(String.join(" ", src.getScopes()));
    }

    @Override
    public LWAClientScopes deserialize(JsonElement jsonElement, Type type,
                                       JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        Set<String> scopeSet = new HashSet<>(Arrays.asList(jsonObj.get("scope").getAsString().split(" ")));
        return new LWAClientScopes(scopeSet);

    }
}
