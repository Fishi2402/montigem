<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdResultDeSers")}
${cd4c.method("public String _serializeResponse(CommandResult<any> result)")}

return this.resultDeSer.serialize(result)
  <#list superCdResultDeSers as tmp>
    ?? this.${tmp}.serialize(command)
  </#list>
;
