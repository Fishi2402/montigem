<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdDeSers")}
${cd4c.method("protected Optional<String> _serializeCommand(Command<?> command)")}

return commandDeSer.serialize(command)
  <#list superCdDeSers as tmp>
    .or(() -> ${tmp}.serialize(command))
  </#list>
;
