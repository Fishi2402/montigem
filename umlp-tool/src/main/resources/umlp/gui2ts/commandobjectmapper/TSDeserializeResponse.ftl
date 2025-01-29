<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdResultDeSers")}
${cd4c.method("protected Optional<CommandResult<any>> _deserializeResponse(String responseStr)")}

return this.resultDeSer.deserialize(responseStr)
  <#list superCdResultDeSers as tmp>
    ?? this.${tmp}.deserialize(command)
  </#list>
;
