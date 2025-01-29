<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdResultDeSers")}
${cd4c.method("protected Optional<CommandResult<any>> _deserializeResponse(String responseStr)")}

return this.resultDeSer.deserialize(responseStr)
  <#list superCdDeSers as tmp>
    ?? this.${tmp}.serialize(command)
  </#list>
;
