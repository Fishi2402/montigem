<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdResultDeSers")}
${cd4c.method("protected Optional<CommandResult<?>> _deserializeResponse(String responseStr)")}

return resultDeSer.deserialize(responseStr)
<#list superCdResultDeSers as tmp>
  .or(() -> ${tmp}.deserialize(responseStr))
</#list>
;
