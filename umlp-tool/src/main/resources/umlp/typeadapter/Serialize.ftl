<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocType")}

${cd4c.method("JsonElement serialize(GemList<${assocType}> src, Type typeOfT, JsonDeserializationContext context) throws JsonParseException")}

// TODO: initially send some values?
JsonObject res = new JsonObject();
res.addProperty("listId", src.getGemId());
return res;