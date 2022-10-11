<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "fieldType")}
${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
  <#-- TODO: default values -->
  return json.getMember("${fieldName}")
  <#switch fieldType>
    <#case "Integer">
    <#case "int">
    .getAsJsonNumber().getNumberAsInteger();
      <#break>
    <#case "Double">
    <#case "double">
      .getAsJsonNumber().getNumberAsDouble();
        <#break>
    <#case "Long">
    <#case "long">
      .getAsJsonNumber().getNumberAsLong();
        <#break>
    <#case "Float">
    <#case "float">
      .getAsJsonNumber().getNumberAsFloat();
        <#break>
    <#case "Boolean">
    <#case "boolean">
      .getAsJsonBoolean().getValue();
        <#break>
    <#case "String">
      .getAsJsonString().getValue();
        <#break>
    <#default>
      <#-- TODO: as error -->
      ${tc.warn("0x3EDBFC: Can not generate deserialization for type ${fieldType}")}
  </#switch>
