<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "fieldType", "isList", "isOptional")}
${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}

<#if isList>${fieldType} result = new ArrayList<>();
  if(!json.hasMember("${fieldName}")) {
    return result;
  }
  for(de.monticore.symboltable.serialization.json.JsonElement e : json.getArrayMember("${fieldName}")) {
  // TODO
  // result.add(e.getAsJsonString().getValue());
  }
  return result;
<#elseif isOptional>
  if(json.hasMember("type")){
    // TODO
    // return java.util.Optional.of(json.getStringMember("type"));
    return java.util.Optional.empty();
  }
  else{
    return java.util.Optional.empty();
  }
<#else>
<#-- TODO: default values -->

    <#switch fieldType>
        <#case "java.lang.Integer">
        <#case "int">
          return json.getMember("${fieldName}").getAsJsonNumber().getNumberAsInteger();
            <#break>
        <#case "java.lang.Double">
        <#case "double">
          return json.getMember("${fieldName}").getAsJsonNumber().getNumberAsDouble();
            <#break>
        <#case "java.lang.Long">
        <#case "long">
          return json.getMember("${fieldName}").getAsJsonNumber().getNumberAsLong();
            <#break>
        <#case "java.lang.Float">
        <#case "float">
          return json.getMember("${fieldName}").getAsJsonNumber().getNumberAsFloat();
            <#break>
        <#case "java.lang.Boolean">
        <#case "boolean">
          return json.getMember("${fieldName}").getAsJsonBoolean().getValue();
            <#break>
        <#case "java.lang.String">
          return json.getMember("${fieldName}").getAsJsonString().getValue();
            <#break>
        <#default>
        <#-- TODO: as error -->
          return null;
            ${tc.warn("0x3EDBFC: Can not generate deserialization for type ${fieldType}")}
    </#switch>
</#if>