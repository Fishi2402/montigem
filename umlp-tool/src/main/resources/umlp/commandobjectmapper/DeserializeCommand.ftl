<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdDeSers")}
${cd4c.method("protected Optional<Command<Object>> _deserializeCommand(String cmdStr)")}

return commandDeSer.deserialize(cmdStr)
<#list superCdDeSers as tmp>
  .or(() -> ${tmp}.deserialize(cmdStr))
</#list>
;
