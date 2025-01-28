<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdResultDeSers")}
${cd4c.method("public Optional<String> _serializeResponse(CommandResult<?> result)")}

return resultDeSer.optSerialize(result)
  <#list superCdResultDeSers as tmp>
    .or(() -> ${tmp}.optSerialize(command))
  </#list>
;
